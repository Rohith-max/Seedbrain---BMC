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
        val ONBOARDING_COMPLETE     = booleanPreferencesKey("onboarding_complete")
        val DARK_THEME              = booleanPreferencesKey("dark_theme")          // legacy — kept for migration
        val THEME_MODE              = stringPreferencesKey("theme_mode")           // "System Default" | "Light" | "Dark"
        val LANGUAGE                = stringPreferencesKey("language")
        val BIOMETRIC_ENABLED       = booleanPreferencesKey("biometric_enabled")
        val PIN_HASH                = stringPreferencesKey("pin_hash")
        val DEMO_MODE               = booleanPreferencesKey("demo_mode")
        val CURRENT_USER_ID         = stringPreferencesKey("current_user_id")
        val FCM_TOKEN               = stringPreferencesKey("fcm_token")
        val LAST_SYNC               = longPreferencesKey("last_sync")
        // ── Biometric security (new) ──────────────────────────────────────────
        val SENSITIVE_DOC_BIOMETRIC = booleanPreferencesKey("sensitive_doc_biometric")
        val SENSITIVE_DOC_TYPES     = stringPreferencesKey("sensitive_doc_types")  // comma-separated
    }

    private val dataStore = context.dataStore

    // ── Existing flows ────────────────────────────────────────────────────────

    val isOnboardingComplete: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.ONBOARDING_COMPLETE] ?: false }

    /** Legacy boolean dark-theme (used by existing code); prefer themeMode for new code. */
    val isDarkTheme: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val mode = prefs[Keys.THEME_MODE]
            when (mode) {
                "Dark" -> true
                "Light" -> false
                else -> prefs[Keys.DARK_THEME] ?: false   // fall back to legacy key if THEME_MODE not set
            }
        }

    /** Three-way theme preference: "System Default" | "Light" | "Dark". */
    val themeMode: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            prefs[Keys.THEME_MODE] ?: run {
                // Migrate from legacy boolean on first read
                when (prefs[Keys.DARK_THEME]) {
                    true -> "Dark"
                    else -> "System Default"
                }
            }
        }

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

    // ── New flows (biometric security) ────────────────────────────────────────

    val isSensitiveDocBiometricEnabled: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SENSITIVE_DOC_BIOMETRIC] ?: false }

    /** Returns comma-separated list of protected document types, or empty string. */
    val sensitiveDocTypesRaw: Flow<String> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.SENSITIVE_DOC_TYPES] ?: "" }

    val sensitiveDocTypes: Flow<Set<String>> = sensitiveDocTypesRaw.map { raw ->
        raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    // ── Setters ───────────────────────────────────────────────────────────────

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setDarkTheme(dark: Boolean) {
        dataStore.edit {
            it[Keys.DARK_THEME] = dark
            it[Keys.THEME_MODE] = if (dark) "Dark" else "Light"
        }
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[Keys.THEME_MODE] = mode }
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

    suspend fun setSensitiveDocBiometricEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.SENSITIVE_DOC_BIOMETRIC] = enabled }
    }

    suspend fun setSensitiveDocTypes(types: Set<String>) {
        dataStore.edit { it[Keys.SENSITIVE_DOC_TYPES] = types.joinToString(",") }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
