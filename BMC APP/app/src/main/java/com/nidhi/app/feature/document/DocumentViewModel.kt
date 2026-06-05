package com.nidhi.app.feature.document

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.model.Document
import com.nidhi.app.domain.model.DocumentSummary
import com.nidhi.app.domain.model.Result
import com.nidhi.app.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class DocumentsUiState(
    val isLoading: Boolean = true,
    val documents: List<Document> = emptyList(),
    val error: String? = null
)

data class CaptureUiState(
    val isProcessing: Boolean = false,
    val ocrText: String? = null,
    val summary: String? = null,
    val error: String? = null,
    val isSaved: Boolean = false
)

class DocumentViewModel(
    private val documentRepository: DocumentRepository,
    private val userPreferences: UserPreferences,
    private val context: Context
) : ViewModel() {

    private val _documentsUiState = MutableStateFlow(DocumentsUiState())
    val documentsUiState: StateFlow<DocumentsUiState> = _documentsUiState.asStateFlow()

    private val _captureUiState = MutableStateFlow(CaptureUiState())
    val captureUiState: StateFlow<CaptureUiState> = _captureUiState.asStateFlow()

    private val _selectedDocument = MutableStateFlow<Document?>(null)
    val selectedDocument: StateFlow<Document?> = _selectedDocument.asStateFlow()

    init { loadDocuments() }

    private fun loadDocuments() {
        viewModelScope.launch {
            val uid = userPreferences.currentUserId.first() ?: "demo_user"
            documentRepository.getDocuments(uid)
                .catch { e -> _documentsUiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { docs -> _documentsUiState.update { it.copy(isLoading = false, documents = docs) } }
        }
    }

    fun loadDocument(id: String) {
        viewModelScope.launch { _selectedDocument.value = documentRepository.getDocumentById(id) }
    }

    fun processCapture(imagePath: String, docType: String, docTitle: String) {
        viewModelScope.launch {
            _captureUiState.update { it.copy(isProcessing = true, error = null) }

            // Determine effective OCR result
            val ocrResult = if (imagePath.isNotBlank()) {
                documentRepository.performOcr(imagePath)
            } else {
                // Simulated scan — no real image
                Result.Success("Simulated scan: $docType document captured on ${java.util.Date()}.")
            }

            when (ocrResult) {
                is Result.Success -> {
                    _captureUiState.update { it.copy(ocrText = ocrResult.data) }
                    val summary = when (val s = documentRepository.generateSummary(ocrResult.data, docType)) {
                        is Result.Success -> s.data
                        else -> DocumentSummary(docTitle, listOf("Document saved"), null, emptyMap())
                    }
                    val uid = userPreferences.currentUserId.first() ?: "demo_user"
                    val effectivePath = imagePath.ifBlank { "simulated_${System.currentTimeMillis()}" }
                    val doc = Document(
                        id = UUID.randomUUID().toString(),
                        ownerId = uid,
                        type = docType,
                        title = docTitle.ifBlank { docType },
                        filePath = effectivePath,
                        thumbnailPath = effectivePath.takeIf { imagePath.isNotBlank() },
                        ocrText = ocrResult.data,
                        summary = summary,
                        expiryDate = parseDate(summary.expiryDateStr),
                        linkedMemberId = null,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    documentRepository.saveDocument(doc)
                    _captureUiState.update {
                        it.copy(isProcessing = false, summary = summary.headline, isSaved = true)
                    }
                }
                is Result.Error ->
                    _captureUiState.update {
                        it.copy(isProcessing = false, error = ocrResult.message ?: "Processing failed")
                    }
                is Result.Loading -> Unit
            }
        }
    }

    fun deleteDocument(doc: Document) { viewModelScope.launch { documentRepository.deleteDocument(doc) } }
    fun clearCaptureState() { _captureUiState.value = CaptureUiState() }

    private fun parseDate(s: String?): Long? {
        if (s.isNullOrBlank()) return null
        listOf("dd/MM/yyyy", "MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy").forEach { fmt ->
            try { return java.text.SimpleDateFormat(fmt, java.util.Locale.getDefault()).parse(s)?.time }
            catch (_: Exception) {}
        }
        return null
    }
}
