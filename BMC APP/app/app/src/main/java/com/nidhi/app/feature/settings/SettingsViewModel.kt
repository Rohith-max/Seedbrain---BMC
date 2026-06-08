package com.nidhi.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.repository.AuthRepository
import com.nidhi.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val isDarkTheme: Boolean = false,
    val biometricEnabled: Boolean = false,
    val isDemoMode: Boolean = false,
    val notificationsEnabled: Boolean = true
)

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userPreferences.isDarkTheme,
                userPreferences.isBiometricEnabled,
                userPreferences.isDemoMode
            ) { dark, bio, demo -> Triple(dark, bio, demo) }
            .collect { (dark, bio, demo) ->
                _uiState.update { it.copy(isDarkTheme = dark, biometricEnabled = bio, isDemoMode = demo) }
            }
        }
        viewModelScope.launch {
            val uid = userPreferences.currentUserId.first() ?: return@launch
            userRepository.getUser(uid).filterNotNull().collect { user ->
                _uiState.update { it.copy(userName = user.name, userEmail = user.email ?: user.phone ?: "") }
            }
        }
    }

    fun setDarkTheme(v: Boolean)      = viewModelScope.launch { userPreferences.setDarkTheme(v) }
    fun setBiometricEnabled(v: Boolean) = viewModelScope.launch { userPreferences.setBiometricEnabled(v) }
    fun setDemoMode(v: Boolean)       = viewModelScope.launch { userPreferences.setDemoMode(v) }
    fun setNotificationsEnabled(v: Boolean) = _uiState.update { it.copy(notificationsEnabled = v) }
    fun backupToCloud()               = Unit // TODO: Firestore backup

    fun signOut() = viewModelScope.launch {
        authRepository.signOut()
        userPreferences.clearAll()
    }
}
