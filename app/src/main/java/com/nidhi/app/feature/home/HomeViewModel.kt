package com.nidhi.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.model.*
import com.nidhi.app.domain.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val healthScore: FamilyHealthScore? = null,
    val upcomingExpiryDocs: List<Document> = emptyList(),
    val unreadAlerts: List<Alert> = emptyList(),
    val unreadAlertCount: Int = 0,
    val eligibleBenefitsCount: Int = 0,
    val isDemoMode: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val documentRepository: DocumentRepository,
    private val alertRepository: AlertRepository,
    private val benefitRepository: BenefitRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { observeData() }

    private fun observeData() {
        viewModelScope.launch {
            val userId = userPreferences.currentUserId.first() ?: "demo_user"
            val isDemo = userPreferences.isDemoMode.first()

            combine(
                documentRepository.getUpcomingExpiryDocuments(userId),
                alertRepository.getUnreadAlerts(userId),
                alertRepository.getUnreadCount(userId),
                benefitRepository.getEligibleCount()
            ) { docs, alerts, alertCount, benefitCount ->
                HomeUiState(
                    isLoading = false,
                    userName = "User",
                    healthScore = calcScore(docs, alerts, benefitCount),
                    upcomingExpiryDocs = docs,
                    unreadAlerts = alerts.take(5),
                    unreadAlertCount = alertCount,
                    eligibleBenefitsCount = benefitCount,
                    isDemoMode = isDemo
                )
            }.catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
             .collect { _uiState.value = it }
        }

        viewModelScope.launch {
            val uid = userPreferences.currentUserId.first() ?: return@launch
            userRepository.getUser(uid).filterNotNull()
                .collect { user -> _uiState.update { it.copy(userName = user.name) } }
        }
    }

    private fun calcScore(docs: List<Document>, alerts: List<Alert>, eligible: Int): FamilyHealthScore {
        val docScore     = if (docs.isEmpty()) 100 else maxOf(40, 100 - docs.size * 15)
        val alertScore   = if (alerts.isEmpty()) 100 else maxOf(50, 100 - alerts.size * 10)
        val benefitScore = minOf(100, 50 + eligible * 10)
        val overall      = (docScore * 0.4 + alertScore * 0.3 + benefitScore * 0.3).toInt()

        return FamilyHealthScore(
            score = overall,
            documentsScore = docScore,
            benefitsScore = benefitScore,
            alertsScore = alertScore,
            insights = buildList {
                if (docs.isNotEmpty()) add("${docs.size} document(s) expiring soon")
                if (alerts.isNotEmpty()) add("${alerts.size} unread alert(s)")
                if (eligible > 0) add("$eligible benefit scheme(s) you may qualify for")
            }
        )
    }

    fun scheduleAlerts() {
        viewModelScope.launch {
            val uid = userPreferences.currentUserId.first() ?: return@launch
            alertRepository.scheduleExpiryAlerts(uid)
        }
    }
}
