package com.nidhi.app.feature.benefits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.model.Benefit
import com.nidhi.app.domain.model.BenefitStatus
import com.nidhi.app.domain.repository.BenefitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BenefitsUiState(
    val isLoading: Boolean = true,
    val benefits: List<Benefit> = emptyList(),
    val selectedFilter: BenefitFilter = BenefitFilter.ALL,
    val error: String? = null
)

enum class BenefitFilter(val label: String) {
    ALL("All"), ELIGIBLE("Eligible"), MISSING_DOCS("Missing Docs"), INELIGIBLE("Ineligible")
}

class BenefitsViewModel(
    private val benefitRepository: BenefitRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(BenefitsUiState())
    val uiState: StateFlow<BenefitsUiState> = _uiState.asStateFlow()

    val filteredBenefits: StateFlow<List<Benefit>> = combine(
        _uiState.map { it.benefits },
        _uiState.map { it.selectedFilter }
    ) { benefits, filter ->
        when (filter) {
            BenefitFilter.ALL          -> benefits
            BenefitFilter.ELIGIBLE     -> benefits.filter { it.status == BenefitStatus.ELIGIBLE }
            BenefitFilter.MISSING_DOCS -> benefits.filter { it.status == BenefitStatus.MISSING_DOCS }
            BenefitFilter.INELIGIBLE   -> benefits.filter { it.status == BenefitStatus.INELIGIBLE }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init { load() }

    private fun load() {
        viewModelScope.launch {
            benefitRepository.loadBenefitsFromAssets()
            benefitRepository.getAllBenefits()
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { _uiState.update { s -> s.copy(isLoading = false, benefits = it) } }
        }
    }

    fun setFilter(f: BenefitFilter) = _uiState.update { it.copy(selectedFilter = f) }

    fun checkEligibility() {
        viewModelScope.launch {
            val uid = userPreferences.currentUserId.first() ?: return@launch
            benefitRepository.evaluateEligibility(uid)
        }
    }
}
