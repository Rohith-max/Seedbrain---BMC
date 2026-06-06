package com.nidhi.app.feature.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt

/**
 * Wraps AndroidX BiometricManager capability checks and prompt-info building.
 * Registered as a Koin single so it can be injected into ViewModels.
 */
class BiometricHelper(private val context: Context) {

    /**
     * Returns the strongest authenticator combination available on this device, or null if none.
     * Order of preference: BIOMETRIC_STRONG + DEVICE_CREDENTIAL → DEVICE_CREDENTIAL only → null
     */
    fun availableAuthenticator(): Int? {
        val mgr = BiometricManager.from(context)
        val strong = BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        return when {
            mgr.canAuthenticate(strong) == BiometricManager.BIOMETRIC_SUCCESS -> strong
            mgr.canAuthenticate(DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS -> DEVICE_CREDENTIAL
            else -> null
        }
    }

    /**
     * Returns true if any biometric (strong or weak) + device credential is supported.
     * Use this to decide whether to show the biometric toggle at all.
     */
    fun isAvailable(): Boolean = availableAuthenticator() != null

    /**
     * Returns a human-readable description of the enrolled authenticator(s) for the
     * Settings subtitle, e.g. "Fingerprint", "Face", "PIN / Pattern".
     */
    fun enrolledAuthenticatorLabel(): String {
        val mgr = BiometricManager.from(context)
        return when {
            mgr.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS ->
                "Fingerprint / Face"
            mgr.canAuthenticate(BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS ->
                "Biometric"
            mgr.canAuthenticate(DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS ->
                "PIN / Pattern / Password"
            else -> "None enrolled"
        }
    }

    /**
     * Builds a [BiometricPrompt.PromptInfo] allowing both biometric and device credential,
     * so the OS always shows a fallback option.
     */
    fun buildPromptInfo(
        title: String = "Unlock NIDHI",
        subtitle: String = "Authenticate to continue"
    ): BiometricPrompt.PromptInfo {
        val authenticators = availableAuthenticator() ?: DEVICE_CREDENTIAL
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(authenticators)
            .build()
    }

    /**
     * Builds a credential-only (PIN / pattern / password) prompt info.
     */
    fun buildCredentialOnlyPromptInfo(title: String = "Use Device Credential"): BiometricPrompt.PromptInfo =
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle("Use your PIN, pattern, or password")
            .setAllowedAuthenticators(DEVICE_CREDENTIAL)
            .build()
}
