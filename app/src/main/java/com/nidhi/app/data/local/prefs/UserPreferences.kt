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

class UserPreferences(
    private val context: Context
) {

    private object Keys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val LANGUAGE = stringPreferencesKey("language")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val DEMO_MODE = booleanPreferencesKey("demo_mode")
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        val LAST_SYNC = longPreferencesKey("last_sync")
    }

    private val dataStore = context.dataStore

    val isOnboardingComplete: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.ONBOARDING_COMPLETE] ?: false }

    val isDarkTheme: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.DARK_THEME] ?: false }

    val language: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.LANGUAGE] ?: "en" }

    val isBiometricEnabled: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.BIOMETRIC_ENABLED] ?: false }

    val pinHash: Flow<String?> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.PIN_HASH] }

    val isDemoMode: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.DEMO_MODE] ?: false }

    val currentUserId: Flow<String?> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.CURRENT_USER_ID] }

    val lastSyncTime: Flow<Long> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.LAST_SYNC] ?: 0L }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setDarkTheme(dark: Boolean) {
        dataStore.edit { it[Keys.DARK_THEME] = dark }
    }

    suspend fun setLanguage(lang: String) {
        dataStore.edit { it[Keys.LANGUAGE] = lang }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun setPinHash(hash: String?) {
        dataStore.edit {
            if (hash == null) it.remove(Keys.PIN_HASH)
            else it[Keys.PIN_HASH] = hash
        }
    }

    suspend fun setDemoMode(demo: Boolean) {
        dataStore.edit { it[Keys.DEMO_MODE] = demo }
    }

    suspend fun setCurrentUserId(uid: String?) {
        dataStore.edit {
            if (uid == null) it.remove(Keys.CURRENT_USER_ID)
            else it[Keys.CURRENT_USER_ID] = uid
        }
    }

    suspend fun setFcmToken(token: String) {
        dataStore.edit { it[Keys.FCM_TOKEN] = token }
    }

    suspend fun setLastSyncTime(time: Long) {
        dataStore.edit { it[Keys.LAST_SYNC] = time }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
