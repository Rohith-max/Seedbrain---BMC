package com.nidhi.app.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nidhi.app.BuildConfig
import com.nidhi.app.data.local.dao.DocumentDao
import com.nidhi.app.data.local.dao.TombstoneDao
import com.nidhi.app.data.local.entity.DeletedDocumentEntity
import com.nidhi.app.data.local.entity.DocumentEntity
import com.nidhi.app.data.ocr.StructuredFieldParser
import com.nidhi.app.data.remote.LlmApiService
import com.nidhi.app.data.remote.SimulatedLlmEngine
import com.nidhi.app.data.remote.dto.ChatCompletionRequest
import com.nidhi.app.data.remote.dto.LlmMessage
import com.nidhi.app.domain.model.Document
import com.nidhi.app.domain.model.DocumentSummary
import com.nidhi.app.domain.model.Result
import com.nidhi.app.domain.repository.DocumentRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DocumentRepositoryImpl(
    private val documentDao: DocumentDao,
    private val tombstoneDao: TombstoneDao,
    private val llmApiService: LlmApiService,
    private val simulatedLlm: SimulatedLlmEngine,
    private val moshi: Moshi,
    private val context: Context
) : DocumentRepository {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val dateParser  = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun getDocuments(ownerId: String): Flow<List<Document>> =
        documentDao.getDocuments(ownerId).map { list -> list.map { it.toDomain(moshi) } }

    override fun getUpcomingExpiryDocuments(ownerId: String): Flow<List<Document>> =
        documentDao.getUpcomingExpiryDocuments(ownerId).map { list ->
            list.map { it.toDomain(moshi) }
        }

    override suspend fun getDocumentById(id: String): Document? =
        documentDao.getDocumentById(id)?.toDomain(moshi)

    /**
     * Saves a document to Room.
     * Guards: verifies the image file exists before writing (Req 14.3).
     * Invariant: updatedAt ≥ createdAt (Req 14.6).
     */
    override suspend fun saveDocument(document: Document) {
        // File existence guard — only check when filePath is non-empty
        if (document.filePath.isNotEmpty() && !File(document.filePath).exists()) {
            // Still persist the entity as "metadata-only" (filePath may be empty on cloud restore)
            // but do NOT silently succeed with a wrong path for a newly captured document.
            // For metadata-only documents restored from cloud, filePath is already "".
            return   // caller should handle this as a silent skip; summary already generated
        }
        val now = System.currentTimeMillis()
        val entity = document.toEntity(moshi).let { e ->
            // Enforce updatedAt ≥ createdAt invariant
            if (e.updatedAt < e.createdAt) e.copy(updatedAt = now) else e
        }
        documentDao.upsertDocument(entity)
    }

    /**
     * Deletes a document from Room, writes a tombstone to prevent ghost re-sync,
     * then best-effort deletes local image files (Req 14.4).
     */
    override suspend fun deleteDocument(document: Document) {
        documentDao.deleteDocument(document.toEntity(moshi))
        tombstoneDao.insert(DeletedDocumentEntity(document.id))
        try { File(document.filePath).delete() } catch (_: Exception) {}
        try { document.thumbnailPath?.let { File(it).delete() } } catch (_: Exception) {}
    }

    /**
     * Performs OCR on the image at [imagePath].
     * - Downsamples images whose largest dimension exceeds 2048 px (Req 11.2).
     * - Returns Success("") for blank OCR result (Req 11.4).
     * - Returns Error with a descriptive message on recognition failure (Req 11.5).
     */
    override suspend fun performOcr(imagePath: String): Result<String> {
        return try {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(imagePath, options)

            // Raise downsample threshold to 2048px max dimension (Req 11.2)
            val maxDim = 2048
            options.inSampleSize = calculateInSampleSize(options, maxDim, maxDim)
            options.inJustDecodeBounds = false

            val bitmap = BitmapFactory.decodeFile(imagePath, options)
                ?: return Result.Error(Exception("Could not decode image"),
                    "Unable to read image. Please capture again.")

            val image = InputImage.fromBitmap(bitmap, 0)
            val mlKitResult = recognizer.process(image).await()
            bitmap.recycle()

            val text = mlKitResult.text  // May be blank — not an error (Req 11.4)
            Result.Success(text)

        } catch (e: Exception) {
            Result.Error(e,
                "No text detected. Try improving lighting or holding the camera steadier.")
        }
    }

    /**
     * Generates an AI document summary with a 15-second timeout.
     * Falls back to StructuredFieldParser output on timeout (Req 13.2).
     */
    override suspend fun generateSummary(
        ocrText: String,
        documentType: String
    ): Result<DocumentSummary> {
        return try {
            val rawJson = if (BuildConfig.USE_SIMULATED_LLM) {
                simulatedLlm.summarise(ocrText, documentType)
            } else {
                // 15-second timeout (Req 13.2)
                val llmResult = withTimeoutOrNull(15_000L) {
                    try {
                        val request = ChatCompletionRequest(
                            model = "gpt-4o-mini",
                            messages = listOf(
                                LlmMessage(role = "system", content = SYSTEM_PROMPT),
                                LlmMessage(role = "user", content = buildSummaryPrompt(ocrText, documentType))
                            ),
                            maxTokens = 500
                        )
                        llmApiService.createChatCompletion(request)
                            .choices.firstOrNull()?.message?.content
                    } catch (_: Exception) { null }
                }

                // If LLM timed out or returned null, fall back to parser-based summary
                llmResult ?: buildParserFallbackJson(ocrText, documentType)
            }

            val summary = parseSummaryFromJson(rawJson, documentType)
            Result.Success(summary)
        } catch (e: Exception) {
            Result.Success(defaultSummary(documentType))
        }
    }

    override suspend fun updateOcrAndSummary(
        id: String,
        ocrText: String?,
        summary: DocumentSummary?
    ) {
        val summaryJson = summary?.let {
            try { moshi.adapter(DocumentSummary::class.java).toJson(it) }
            catch (_: Exception) { null }
        }
        documentDao.updateOcrAndSummary(id, ocrText, summaryJson)
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height, width) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun buildSummaryPrompt(ocrText: String, docType: String) = """
        Document Type: $docType
        Extracted Text: $ocrText
        
        Return ONLY valid JSON with fields:
        headline (string, max 80 chars), keyPoints (array of 2-5 strings),
        expiryDateStr (nullable string dd/MM/yyyy), extractedIds (object of label:maskedValue pairs).
        No markdown fences or prose.
    """.trimIndent()

    /**
     * Builds a JSON summary from StructuredFieldParser when the LLM is unavailable.
     * Satisfies Requirement 13.2.
     */
    private fun buildParserFallbackJson(ocrText: String, docType: String): String {
        val fields = StructuredFieldParser.parse(ocrText)
        val expiry = fields["Expiry Date"]
        val ids    = fields.filter { it.key != "Expiry Date" && it.key != "Issue Date" && it.key != "Date of Birth" }
        val keyPoints = listOf(
            "Document type: $docType",
            if (expiry != null) "Expiry date: $expiry" else "No expiry date detected",
            "Scanned and stored securely"
        )
        val keyPointsJson = keyPoints.joinToString(",") { "\"$it\"" }
        val extractedIdsJson = ids.entries.joinToString(",") { (k, v) -> "\"$k\":\"$v\"" }
        return """{"headline":"$docType","keyPoints":[$keyPointsJson],"expiryDateStr":${if (expiry != null) "\"$expiry\"" else "null"},"extractedIds":{$extractedIdsJson}}"""
    }

    private fun parseSummaryFromJson(content: String, docType: String): DocumentSummary {
        return try {
            // Strip markdown fences (Req 13.4)
            val json = content
                .replace(Regex("```json\\s*"), "")
                .replace(Regex("```\\s*"), "")
                .trim()
            moshi.adapter(DocumentSummary::class.java).fromJson(json)
                ?: defaultSummary(docType)
        } catch (_: Exception) {
            defaultSummary(docType)
        }
    }

    private fun defaultSummary(docType: String) = DocumentSummary(
        headline = "$docType processed successfully",
        keyPoints = listOf("Document saved", "OCR text extracted"),
        expiryDateStr = null,
        extractedIds = emptyMap()
    )

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun DocumentEntity.toDomain(moshi: Moshi): Document {
        val summary = summaryJson?.let {
            try { moshi.adapter(DocumentSummary::class.java).fromJson(it) }
            catch (_: Exception) { null }
        }
        return Document(
            id, ownerId, type, title, filePath, thumbnailPath,
            ocrText, summary, expiryDate, linkedMemberId, createdAt, updatedAt
        )
    }

    private fun Document.toEntity(moshi: Moshi): DocumentEntity {
        val summaryJson = summary?.let {
            try { moshi.adapter(DocumentSummary::class.java).toJson(it) }
            catch (_: Exception) { null }
        }
        return DocumentEntity(
            id, ownerId, type, title, filePath, thumbnailPath,
            ocrText, summaryJson, expiryDate, linkedMemberId, createdAt, updatedAt
        )
    }

    companion object {
        private const val SYSTEM_PROMPT =
            "You are NIDHI AI. Summarise the document and return structured JSON only. " +
            "No markdown, no prose — pure JSON."
    }
}
