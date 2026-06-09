package com.nidhi.app.feature.auth

/**
 * In-memory session cache of document types that have been biometrically authenticated
 * during the current app session. Cleared on process death.
 *
 * Registered as a Koin `single` so it lives for the lifetime of the application process.
 */
class SessionBiometricCache {

    private val authenticated = mutableSetOf<String>()

    /** Returns true if the given document type has been authenticated this session. */
    fun isAuthenticated(docType: String): Boolean = docType in authenticated

    /** Marks the given document type as authenticated for this session. */
    fun grant(docType: String) {
        authenticated += docType
    }

    /** Removes the given document type from the session cache, forcing re-auth on next access. */
    fun revoke(docType: String) {
        authenticated -= docType
    }

    /** Clears all session authentications (e.g. when biometric setting changes). */
    fun revokeAll() {
        authenticated.clear()
    }
}
