package com.nidhi.app.feature.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.model.Document
import com.nidhi.app.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CalendarUiState(
    val expiringDocuments: List<Document> = emptyList(),
    val isLoading: Boolean = true
)

class CalendarViewModel(
    private val documentRepository: DocumentRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val uid = userPreferences.currentUserId.first() ?: "demo_user"
            documentRepository.getUpcomingExpiryDocuments(uid).collect { docs ->
                _uiState.update { it.copy(expiringDocuments = docs.filter { d -> d.expiryDate != null }, isLoading = false) }
            }
        }
    }
}
