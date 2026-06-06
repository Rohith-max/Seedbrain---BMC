package com.nidhi.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidhi.app.data.local.dao.DocumentDao
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.repository.AuthRepository
import com.nidhi.app.domain.repository.UserRepository
import com.nidhi.app.feature.auth.BiometricHelper
import com.nidhi.app.feature.auth.SessionBiometricCache
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val isDarkTheme: Boolean = false,
    val themeMode: String = "System Default",      // "System Default" | "Light" | "Dark"
    val biometricEnabled: Boolean = false,
    val biometricSubtitle: String = "",            // authenticator label shown when enabled
    val sensitiveDocBiometricEnabled: Boolean = false,
    val sensitiveDocTypes: Set<String> = emptySet(),
    val availableDocTypes: List<String> = emptyList(), // all types in Room
    val isDemoMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val lastSyncTime: Long = 0L,
    val isBiometricAvailable: Boolean = false,     // whether device supports biometric at all
    val biometricError: String? = null             // shown as snackbar / banner
)

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences,
    private val documentDao: DocumentDao,
    private val biometricHelper: BiometricHelper,
    private val sessionBiometricCache: SessionBiometricCache
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isBiometricAvailable = biometricHelper.isAvailable()) }
        }
        viewModelScope.launch {
            combine(
                userPreferences.isDarkTheme,
                userPreferences.themeMode,
                userPreferences.isBiometricEnabled,
                userPreferences.isSensitiveDocBiometricEnabled,
                userPreferences.sensitiveDocTypes,
                userPreferences.isDemoMode,
                userPreferences.lastSyncTime
            ) { values ->
                val dark    = values[0] as Boolean
                val mode    = values[1] as String
                val bio     = values[2] as Boolean
                val sensDoc = values[3] as Boolean
                @Suppress("UNCHECKED_CAST")
                val sensTypes = values[4] as Set<String>
                val demo    = values[5] as Boolean
                val sync    = values[6] as Long
                Triple(Triple(dark, mode, bio), Triple(sensDoc, sensTypes, demo), sync)
            }.collect { (a, b, sync) ->
                val (dark, mode, bio) = a
                val (sensDoc, sensTypes, demo) = b
                _uiState.update {
                    it.copy(
                        isDarkTheme = dark,
                        themeMode = mode,
                        biometricEnabled = bio,
                        biometricSubtitle = if (bio) biometricHelper.enrolledAuthenticatorLabel() else "",
                        sensitiveDocBiometricEnabled = sensDoc,
                        sensitiveDocTypes = sensTypes,
                        isDemoMode = demo,
                        lastSyncTime = sync
                    )
                }
            }
        }
        viewModelScope.launch {
            val uid = userPreferences.currentUserId.first() ?: return@launch
            userRepository.getUser(uid).filterNotNull().collect { user ->
                _uiState.update { it.copy(userName = user.name, userEmail = user.email ?: user.phone ?: "") }
            }
        }
        viewModelScope.launch {
            val uid = userPreferences.currentUserId.first() ?: return@launch
            val types = documentDao.getDistinctTypes(uid)
            _uiState.update { it.copy(availableDocTypes = types) }
        }
    }

    // ── Theme ──────────────────────────────────────────────────────────────────

    fun setDarkTheme(v: Boolean) = viewModelScope.launch { userPreferences.setDarkTheme(v) }

    fun setThemeMode(mode: String) = viewModelScope.launch { userPreferences.setThemeMode(mode) }

    // ── Biometric app lock ─────────────────────────────────────────────────────

    /**
     * Called when the user toggles "App Lock – Biometric".
     * Pre-checks hardware availability before enabling (Req 1.2).
     * Actual BiometricPrompt confirmation is handled in the UI layer.
     */
    fun onBiometricToggleRequest(enable: Boolean) {
        if (enable && !biometricHelper.isAvailable()) {
            _uiState.update {
                it.copy(biometricError = "No biometric credential is enrolled on this device.")
            }
            return
        }
        // UI should present BiometricPrompt; on success call confirmBiometricToggle()
    }

    fun confirmBiometricEnabled(enabled: Boolean) = viewModelScope.launch {
        userPreferences.setBiometricEnabled(enabled)
        if (!enabled) sessionBiometricCache.revokeAll()
        _uiState.update { it.copy(biometricError = null) }
    }

    fun clearBiometricError() = _uiState.update { it.copy(biometricError = null) }

    // ── Sensitive document biometric ───────────────────────────────────────────

    fun setSensitiveDocBiometricEnabled(v: Boolean) = viewModelScope.launch {
        userPreferences.setSensitiveDocBiometricEnabled(v)
        if (!v) sessionBiometricCache.revokeAll()
    }

    fun setSensitiveDocTypes(types: Set<String>) = viewModelScope.launch {
        userPreferences.setSensitiveDocTypes(types)
        // Invalidate session cache for any type removed/added (Req 2.6)
        sessionBiometricCache.revokeAll()
    }

    // ── Other settings ─────────────────────────────────────────────────────────

    fun setDemoMode(v: Boolean) = viewModelScope.launch { userPreferences.setDemoMode(v) }
    fun setNotificationsEnabled(v: Boolean) = _uiState.update { it.copy(notificationsEnabled = v) }
    fun backupToCloud() = Unit // TODO: Firestore backup triggered here

    fun signOut() = viewModelScope.launch {
        authRepository.signOut()
        userPreferences.clearAll()
    }
}
