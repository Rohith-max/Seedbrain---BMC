package com.nidhi.app.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nidhi_prefs")

class UserPreferences(private val context: Context) {

    private object Keys {
        val ONBOARDING_COMPLETE  = booleanPreferencesKey("onboarding_complete")
        val DARK_THEME           = booleanPreferencesKey("dark_theme")
        val APP_THEME            = stringPreferencesKey("app_theme")        // "teal"|"saffron"|"forest"|"midnight"|"coral"
        val LANGUAGE             = stringPreferencesKey("language")          // "en"|"hi"|"ta"|"te"|"bn"|"mr"
        val FONT_SIZE            = stringPreferencesKey("font_size")         // "small"|"medium"|"large"
        val BIOMETRIC_ENABLED    = booleanPreferencesKey("biometric_enabled")
        val NOTIFICATIONS_ENABLED= booleanPreferencesKey("notifications_enabled")
        val PIN_HASH             = stringPreferencesKey("pin_hash")
        val DEMO_MODE            = booleanPreferencesKey("demo_mode")
        val CURRENT_USER_ID      = stringPreferencesKey("current_user_id")
        val FCM_TOKEN            = stringPreferencesKey("fcm_token")
        val LAST_SYNC            = longPreferencesKey("last_sync")
        val WHATSAPP_PHONE       = stringPreferencesKey("whatsapp_phone")
        val WIDGET_ENABLED       = booleanPreferencesKey("widget_enabled")
    }

    private val store = context.dataStore

    val isOnboardingComplete: Flow<Boolean> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.ONBOARDING_COMPLETE] ?: false }

    val isDarkTheme: Flow<Boolean> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.DARK_THEME] ?: false }

    val appTheme: Flow<String> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.APP_THEME] ?: "teal" }

    val language: Flow<String> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.LANGUAGE] ?: "en" }

    val fontSize: Flow<String> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.FONT_SIZE] ?: "medium" }

    val isBiometricEnabled: Flow<Boolean> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.BIOMETRIC_ENABLED] ?: false }

    val isNotificationsEnabled: Flow<Boolean> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }

    val pinHash: Flow<String?> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PIN_HASH] }

    val isDemoMode: Flow<Boolean> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.DEMO_MODE] ?: false }

    val currentUserId: Flow<String?> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.CURRENT_USER_ID] }

    val lastSyncTime: Flow<Long> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.LAST_SYNC] ?: 0L }

    val whatsappPhone: Flow<String> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.WHATSAPP_PHONE] ?: "" }

    val isWidgetEnabled: Flow<Boolean> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.WIDGET_ENABLED] ?: true }

    // ── Writers ───────────────────────────────────────────────────────────────

    suspend fun setOnboardingComplete(v: Boolean)    = store.edit { it[Keys.ONBOARDING_COMPLETE] = v }
    suspend fun setDarkTheme(v: Boolean)             = store.edit { it[Keys.DARK_THEME] = v }
    suspend fun setAppTheme(v: String)               = store.edit { it[Keys.APP_THEME] = v }
    suspend fun setLanguage(v: String)               = store.edit { it[Keys.LANGUAGE] = v }
    suspend fun setFontSize(v: String)               = store.edit { it[Keys.FONT_SIZE] = v }
    suspend fun setBiometricEnabled(v: Boolean)      = store.edit { it[Keys.BIOMETRIC_ENABLED] = v }
    suspend fun setNotificationsEnabled(v: Boolean)  = store.edit { it[Keys.NOTIFICATIONS_ENABLED] = v }
    suspend fun setDemoMode(v: Boolean)              = store.edit { it[Keys.DEMO_MODE] = v }
    suspend fun setWhatsappPhone(v: String)          = store.edit { it[Keys.WHATSAPP_PHONE] = v }
    suspend fun setWidgetEnabled(v: Boolean)         = store.edit { it[Keys.WIDGET_ENABLED] = v }

    suspend fun setPinHash(hash: String?) = store.edit {
        if (hash == null) it.remove(Keys.PIN_HASH) else it[Keys.PIN_HASH] = hash
    }
    suspend fun setCurrentUserId(uid: String?) = store.edit {
        if (uid == null) it.remove(Keys.CURRENT_USER_ID) else it[Keys.CURRENT_USER_ID] = uid
    }
    suspend fun setFcmToken(token: String)           = store.edit { it[Keys.FCM_TOKEN] = token }
    suspend fun setLastSyncTime(time: Long)          = store.edit { it[Keys.LAST_SYNC] = time }
    suspend fun clearAll()                           = store.edit { it.clear() }
}
