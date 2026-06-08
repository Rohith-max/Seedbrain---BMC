package com.nidhi.app.feature.settings

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidhi.app.NidhiApplication
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.repository.AuthRepository
import com.nidhi.app.domain.repository.UserRepository
import com.nidhi.app.widget.NidhiWidget
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userName: String = "",
    val userEmail: String = "",
    val isDarkTheme: Boolean = false,
    val appTheme: String = "teal",
    val language: String = "en",
    val fontSize: String = "medium",
    val biometricEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val isDemoMode: Boolean = false,
    val whatsappPhone: String = "",
    val widgetEnabled: Boolean = true,
    val lastSyncTime: Long = 0L
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
                userPreferences.appTheme,
                userPreferences.language,
                userPreferences.fontSize,
                userPreferences.isBiometricEnabled
            ) { dark, theme, lang, font, bio ->
                _uiState.update { it.copy(
                    isDarkTheme = dark,
                    appTheme = theme,
                    language = lang,
                    fontSize = font,
                    biometricEnabled = bio
                ) }
            }.collect()
        }
        viewModelScope.launch {
            combine(
                userPreferences.isNotificationsEnabled,
                userPreferences.isDemoMode,
                userPreferences.whatsappPhone,
                userPreferences.isWidgetEnabled,
                userPreferences.lastSyncTime
            ) { notif, demo, wa, widget, sync ->
                _uiState.update { it.copy(
                    notificationsEnabled = notif,
                    isDemoMode = demo,
                    whatsappPhone = wa,
                    widgetEnabled = widget,
                    lastSyncTime = sync
                ) }
            }.collect()
        }
        viewModelScope.launch {
            val uid = userPreferences.currentUserId.first() ?: return@launch
            userRepository.getUser(uid).filterNotNull().collect { user ->
                _uiState.update { it.copy(
                    userName = user.name,
                    userEmail = user.email ?: user.phone ?: ""
                ) }
            }
        }
    }

    fun setDarkTheme(v: Boolean)           = viewModelScope.launch { userPreferences.setDarkTheme(v) }
    fun setAppTheme(v: String)             = viewModelScope.launch { userPreferences.setAppTheme(v) }
    fun setLanguage(v: String)             = viewModelScope.launch { userPreferences.setLanguage(v) }
    fun setFontSize(v: String)             = viewModelScope.launch { userPreferences.setFontSize(v) }
    fun setBiometricEnabled(v: Boolean)    = viewModelScope.launch { userPreferences.setBiometricEnabled(v) }
    fun setNotificationsEnabled(v: Boolean)= viewModelScope.launch { userPreferences.setNotificationsEnabled(v) }
    fun setDemoMode(v: Boolean)            = viewModelScope.launch { userPreferences.setDemoMode(v) }
    fun setWhatsappPhone(v: String)        = viewModelScope.launch { userPreferences.setWhatsappPhone(v) }
    fun setWidgetEnabled(v: Boolean)       = viewModelScope.launch { userPreferences.setWidgetEnabled(v) }

    fun sendTestNotification(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java) ?: return
        val n = NotificationCompat.Builder(context, NidhiApplication.CHANNEL_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🔔 NIDHI – Test Notification")
            .setContentText("Notifications are working correctly!")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "✅ Alerts are configured.\n\nYou'll be reminded about:\n• Document expiry dates\n• Scheme deadlines\n• New benefits you're eligible for"
            ))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        nm.notify(9999, n)
    }

    fun backupToCloud() {
        viewModelScope.launch {
            userPreferences.setLastSyncTime(System.currentTimeMillis())
            _uiState.update { it.copy(lastSyncTime = System.currentTimeMillis()) }
        }
    }

    fun refreshWidget(context: Context) {
        NidhiWidget.forceUpdate(context)
    }

    fun signOut() = viewModelScope.launch {
        authRepository.signOut()
        userPreferences.clearAll()
    }
}
