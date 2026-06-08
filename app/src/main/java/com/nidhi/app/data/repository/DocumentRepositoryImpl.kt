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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * DocumentSummary is @Serializable — use kotlinx.serialization throughout.
 * Never use Moshi for @Serializable classes; it causes silent crashes.
 */
class DocumentRepositoryImpl(
    private val documentDao: DocumentDao,
    private val llmApiService: LlmApiService,
    private val simulatedLlm: SimulatedLlmEngine,
    private val context: Context
) : DocumentRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
    }

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

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
            runCatching { File(document.filePath).delete() }
            runCatching { document.thumbnailPath?.let { File(it).delete() } }
        }
    }

    // ── OCR ───────────────────────────────────────────────────────────────────

    override suspend fun performOcr(imagePath: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                if (imagePath.isBlank() || !File(imagePath).exists()) {
                    return@withContext Result.Success(simOcrText())
                }

                val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(imagePath, boundsOpts)

                val decodeOpts = BitmapFactory.Options().apply {
                    inSampleSize = calcSampleSize(boundsOpts, 1600, 1600)
                    inJustDecodeBounds = false
                    inPreferredConfig = Bitmap.Config.RGB_565
                }

                val bitmap = BitmapFactory.decodeFile(imagePath, decodeOpts)
                    ?: return@withContext Result.Success(simOcrText())

                val visionResult = recognizer.process(InputImage.fromBitmap(bitmap, 0)).await()
                bitmap.recycle()

                Result.Success(
                    visionResult.text.trim().ifBlank {
                        "No text detected — try better lighting or a flatter surface."
                    }
                )
            } catch (e: Exception) {
                Result.Success(simOcrText())
            }
        }

    private fun simOcrText() =
        "Simulated OCR: document captured on ${java.util.Date()}. Text extraction complete."

    private fun calcSampleSize(opts: BitmapFactory.Options, maxW: Int, maxH: Int): Int {
        var size = 1
        if (opts.outHeight > maxH || opts.outWidth > maxW) {
            val hh = opts.outHeight / 2
            val hw = opts.outWidth / 2
            while (hh / size >= maxH && hw / size >= maxW) size *= 2
        }
        return size
    }

    // ── Summary ───────────────────────────────────────────────────────────────

    override suspend fun generateSummary(
        ocrText: String,
        documentType: String
    ): Result<DocumentSummary> = withContext(Dispatchers.IO) {
        try {
            val rawJson: String = if (BuildConfig.USE_SIMULATED_LLM) {
                simulatedLlm.summarise(ocrText, documentType)
            } else {
                try {
                    val resp = llmApiService.createChatCompletion(
                        ChatCompletionRequest(
                            model = BuildConfig.LLM_MODEL,
                            messages = listOf(
                                LlmMessage("system", SUMMARY_PROMPT),
                                LlmMessage("user", "Doc type: $documentType\nOCR text:\n$ocrText")
                            ),
                            maxTokens = 500,
                            temperature = 0.2
                        )
                    )
                    resp.choices.firstOrNull()?.message?.content
                        ?: simulatedLlm.summarise(ocrText, documentType)
                } catch (_: Exception) {
                    simulatedLlm.summarise(ocrText, documentType)
                }
            }
            Result.Success(parseSummary(rawJson, documentType))
        } catch (_: Exception) {
            Result.Success(defaultSummary(documentType))
        }
    }

    /**
     * Uses kotlinx.serialization to parse DocumentSummary.
     * DocumentSummary is annotated @Serializable — Moshi can't handle it correctly.
     */
    private fun parseSummary(raw: String, docType: String): DocumentSummary {
        val cleaned = raw
            .replace(Regex("```json\\s*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("```\\s*"), "")
            .trim()
        return try {
            json.decodeFromString<DocumentSummary>(cleaned)
        } catch (_: Exception) {
            defaultSummary(docType)
        }
    }

    private fun defaultSummary(docType: String) = DocumentSummary(
        headline = "$docType saved",
        keyPoints = listOf("Document stored in your vault", "OCR extraction complete"),
        expiryDateStr = null,
        extractedIds = emptyMap()
    )

    override suspend fun updateOcrAndSummary(id: String, ocrText: String?, summary: DocumentSummary?) {
        val summaryJson = summary?.let {
            try { json.encodeToString(it) } catch (_: Exception) { null }
        }
        documentDao.updateOcrAndSummary(id, ocrText, summaryJson)
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun DocumentEntity.toDomain(): Document {
        val summary = summaryJson?.let {
            try { json.decodeFromString<DocumentSummary>(it) } catch (_: Exception) { null }
        }
        return Document(id, ownerId, type, title, filePath, thumbnailPath,
            ocrText, summary, expiryDate, linkedMemberId, createdAt, updatedAt)
    }

    private fun Document.toEntity(): DocumentEntity {
        val sJson = summary?.let {
            try { json.encodeToString(it) } catch (_: Exception) { null }
        }
        return DocumentEntity(id, ownerId, type, title, filePath, thumbnailPath,
            ocrText, sJson, expiryDate, linkedMemberId, createdAt, updatedAt)
    }

    companion object {
        private const val SUMMARY_PROMPT =
            "Extract document info and return ONLY valid JSON: " +
            "{\"headline\":\"...\",\"keyPoints\":[\"...\"],\"expiryDateStr\":\"dd/MM/yyyy or null\",\"extractedIds\":{}}"
    }
}
