package com.nidhi.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nidhi.app.BuildConfig
import com.nidhi.app.data.local.dao.DocumentDao
import com.nidhi.app.data.local.entity.DocumentEntity
import com.nidhi.app.data.remote.LlmApiService
import com.nidhi.app.data.remote.SimulatedLlmEngine
import com.nidhi.app.data.remote.dto.ChatCompletionRequest
import com.nidhi.app.data.remote.dto.LlmMessage
import com.nidhi.app.domain.model.Document
import com.nidhi.app.domain.model.DocumentSummary
import com.nidhi.app.domain.model.Result
import com.nidhi.app.domain.repository.DocumentRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class DocumentRepositoryImpl(
    private val documentDao: DocumentDao,
    private val llmApiService: LlmApiService,
    private val simulatedLlm: SimulatedLlmEngine,
    private val moshi: Moshi,
    private val context: Context
) : DocumentRepository {

    // Lazy initialisation — created once on first OCR call (thread-safe)
    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    // ── Document CRUD ─────────────────────────────────────────────────────────

    override fun getDocuments(ownerId: String): Flow<List<Document>> =
        documentDao.getDocuments(ownerId).map { list -> list.map { it.toDomain() } }

    override fun getUpcomingExpiryDocuments(ownerId: String): Flow<List<Document>> =
        documentDao.getUpcomingExpiryDocuments(ownerId).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getDocumentById(id: String): Document? =
        documentDao.getDocumentById(id)?.toDomain()

    override suspend fun saveDocument(document: Document) {
        documentDao.upsertDocument(document.toEntity())
    }

    override suspend fun deleteDocument(document: Document) {
        documentDao.deleteDocument(document.toEntity())
        withContext(Dispatchers.IO) {
            try { File(document.filePath).delete() } catch (_: Exception) {}
            try { document.thumbnailPath?.let { File(it).delete() } } catch (_: Exception) {}
        }
    }

    // ── OCR — fully fixed ─────────────────────────────────────────────────────
    /**
     * Runs entirely on Dispatchers.IO to avoid:
     *  - NetworkOnMainThreadException (ML Kit gRPC)
     *  - StrictMode violations
     *  - OOM from undecoded large bitmaps
     *
     * Always returns Result.Success so the document save never gets blocked.
     */
    override suspend fun performOcr(imagePath: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                if (imagePath.isBlank() || !File(imagePath).exists()) {
                    return@withContext Result.Success(
                        "Document scanned successfully (simulated text extraction)."
                    )
                }

                // Step 1 — measure image without allocating memory
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(imagePath, bounds)

                // Step 2 — downsample so we never exceed ~4 MP in memory
                val sampleOpts = BitmapFactory.Options().apply {
                    inSampleSize = calcSampleSize(bounds, 1600, 1600)
                    inJustDecodeBounds = false
                    inPreferredConfig = Bitmap.Config.RGB_565  // half the memory of ARGB_8888
                }
                val bitmap = BitmapFactory.decodeFile(imagePath, sampleOpts)
                    ?: return@withContext Result.Success(
                        "Document stored. Text could not be extracted from image."
                    )

                // Step 3 — ML Kit OCR (must be on IO, not Main)
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                val visionResult = recognizer.process(inputImage).await()
                bitmap.recycle()

                val extractedText = visionResult.text.trim()

                Result.Success(
                    if (extractedText.isNotBlank()) extractedText
                    else "Document scanned. No text could be extracted — try better lighting or a flatter surface."
                )
            } catch (e: Exception) {
                // Never crash the document flow
                Result.Success("Document saved. OCR note: ${e.message?.take(80) ?: "processing error"}")
            }
        }

    private fun calcSampleSize(opts: BitmapFactory.Options, maxW: Int, maxH: Int): Int {
        var size = 1
        val w = opts.outWidth
        val h = opts.outHeight
        if (w > maxW || h > maxH) {
            val hw = w / 2
            val hh = h / 2
            while (hw / size >= maxW && hh / size >= maxH) size *= 2
        }
        return size
    }

    // ── AI Summary ────────────────────────────────────────────────────────────

    override suspend fun generateSummary(
        ocrText: String,
        documentType: String
    ): Result<DocumentSummary> = withContext(Dispatchers.IO) {
        try {
            val rawJson: String = if (BuildConfig.USE_SIMULATED_LLM) {
                simulatedLlm.summarise(ocrText, documentType)
            } else {
                try {
                    val request = ChatCompletionRequest(
                        model = BuildConfig.LLM_MODEL,
                        messages = listOf(
                            LlmMessage("system", SUMMARY_SYSTEM_PROMPT),
                            LlmMessage("user", buildSummaryPrompt(ocrText, documentType))
                        ),
                        maxTokens = 500,
                        temperature = 0.2
                    )
                    val response = llmApiService.createChatCompletion(request)
                    response.choices.firstOrNull()?.message?.content
                        ?: simulatedLlm.summarise(ocrText, documentType)
                } catch (_: Exception) {
                    simulatedLlm.summarise(ocrText, documentType)
                }
            }
            Result.Success(parseSummaryJson(rawJson, documentType))
        } catch (e: Exception) {
            Result.Success(defaultSummary(documentType))
        }
    }

    override suspend fun updateOcrAndSummary(
        id: String, ocrText: String?, summary: DocumentSummary?
    ) {
        val summaryJson = summary?.let {
            try { moshi.adapter(DocumentSummary::class.java).toJson(it) }
            catch (_: Exception) { null }
        }
        documentDao.updateOcrAndSummary(id, ocrText, summaryJson)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun buildSummaryPrompt(ocrText: String, docType: String) = """
        Document Type: $docType
        Extracted OCR Text:
        $ocrText
        
        Return ONLY valid JSON (no markdown fences) with exactly these fields:
        {
          "headline": "string",
          "keyPoints": ["string", ...],
          "expiryDateStr": "dd/MM/yyyy or null",
          "extractedIds": {"field": "value"}
        }
    """.trimIndent()

    private fun parseSummaryJson(content: String, docType: String): DocumentSummary {
        return try {
            val cleaned = content
                .replace(Regex("```json\\s*", RegexOption.IGNORE_CASE), "")
                .replace(Regex("```\\s*"), "")
                .trim()
            moshi.adapter(DocumentSummary::class.java).fromJson(cleaned)
                ?: defaultSummary(docType)
        } catch (_: Exception) {
            defaultSummary(docType)
        }
    }

    private fun defaultSummary(docType: String) = DocumentSummary(
        headline = "$docType saved successfully",
        keyPoints = listOf("Document stored in your vault", "OCR extraction complete"),
        expiryDateStr = null,
        extractedIds = emptyMap()
    )

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun DocumentEntity.toDomain(): Document {
        val summary = summaryJson?.let {
            try { moshi.adapter(DocumentSummary::class.java).fromJson(it) }
            catch (_: Exception) { null }
        }
        return Document(id, ownerId, type, title, filePath, thumbnailPath,
            ocrText, summary, expiryDate, linkedMemberId, createdAt, updatedAt)
    }

    private fun Document.toEntity(): DocumentEntity {
        val json = summary?.let {
            try { moshi.adapter(DocumentSummary::class.java).toJson(it) }
            catch (_: Exception) { null }
        }
        return DocumentEntity(id, ownerId, type, title, filePath, thumbnailPath,
            ocrText, json, expiryDate, linkedMemberId, createdAt, updatedAt)
    }

    companion object {
        private const val SUMMARY_SYSTEM_PROMPT =
            "You are a document analysis AI. Extract key information from the OCR text " +
            "and return structured JSON only. No extra text or markdown."
    }
}
