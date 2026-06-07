package com.nidhi.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.model.FamilyMember
import com.nidhi.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class FamilyMembersUiState(
    val members: List<FamilyMember> = emptyList(),
    val isLoading: Boolean = true
)

class FamilyMembersViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(FamilyMembersUiState())
    val uiState: StateFlow<FamilyMembersUiState> = _uiState.asStateFlow()

    private var userId = "demo_user"

    init {
        viewModelScope.launch {
            userId = userPreferences.currentUserId.first() ?: "demo_user"
            userRepository.getFamilyMembers(userId)
                .collect { members -> _uiState.update { it.copy(members = members, isLoading = false) } }
        }
    }

    fun addMember(name: String, relation: String, dob: Long?) {
        viewModelScope.launch {
            userRepository.saveFamilyMember(
                FamilyMember(UUID.randomUUID().toString(), userId, name, relation, dob, null, null)
            )
        }
    }

    fun deleteMember(member: FamilyMember) = viewModelScope.launch { userRepository.deleteFamilyMember(member) }
}
