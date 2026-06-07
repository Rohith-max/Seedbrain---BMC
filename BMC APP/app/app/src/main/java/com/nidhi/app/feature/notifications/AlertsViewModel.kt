package com.nidhi.app.feature.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.model.Alert
import com.nidhi.app.domain.repository.AlertRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AlertsUiState(
    val alerts: List<Alert> = emptyList(),
    val unreadCount: Int = 0
)

class AlertsViewModel(
    private val alertRepository: AlertRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertsUiState())
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val uid = userPreferences.currentUserId.first() ?: "demo_user"
            combine(alertRepository.getAlerts(uid), alertRepository.getUnreadCount(uid)) { alerts, count ->
                AlertsUiState(alerts, count)
            }.collect { _uiState.value = it }
        }
    }

    fun markAsRead(id: String) = viewModelScope.launch { alertRepository.markAsRead(id) }
    fun markAllAsRead() = viewModelScope.launch {
        alertRepository.markAllAsRead(userPreferences.currentUserId.first() ?: return@launch)
    }
    fun deleteAlert(alert: Alert) = viewModelScope.launch { alertRepository.deleteAlert(alert) }
}
