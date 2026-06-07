package com.nidhi.app.data.remote

import com.nidhi.app.BuildConfig
import com.nidhi.app.config.ApiKeys
import kotlinx.serialization.Serializable

/**
 * Clerk Authentication Service.
 *
 * Clerk provides secure, modern authentication for web and mobile apps.
 * In a production app, you would use Clerk's SDK or deep-link to their hosted UI.
 *
 * For now, this serves as documentation for how Clerk would be integrated.
 *
 * Integration steps:
 * 1. Use Clerk's React Native SDK or custom OAuth flow
 * 2. Exchange Clerk JWT for Supabase JWT (optional but recommended)
 * 3. Store user session in app's local prefs
 *
 * Clerk flow for Android:
 * - Publishable key: pk_test_cmFyZS16ZWJyYS0zNC5jbGVyay5hY2NvdW50cy5kZXYk
 * - Secret key: sk_test_4cYUPNBQiDjIuypP57pQndvAYWc8DKfixjD9F0Mkx9 (backend only)
 * - Redirect URI: YOUR_APP_SCHEME://clerk-callback or similar
 */
object ClerkAuthService {

    const val PUBLISHABLE_KEY = ApiKeys.CLERK_PUBLISHABLE_KEY
    const val SECRET_KEY = ApiKeys.CLERK_SECRET_KEY  // Never embed in production app

    /**
     * Deep-link to Clerk-hosted sign-in UI.
     * Android app should define an intent filter for clerk-callback://
     */
    fun getSignInUrl(): String =
        "https://rare-zebra-34.clerk.accounts.dev/sign-in"

    /**
     * Deep-link to Clerk-hosted sign-up UI.
     */
    fun getSignUpUrl(): String =
        "https://rare-zebra-34.clerk.accounts.dev/sign-up"

    /**
     * Build a redirect URI for OAuth callback.
     * Should match your app's AndroidManifest.xml intent filter.
     */
    fun getRedirectUri(scheme: String = "com.nidhi.app"): String =
        "$scheme://clerk-callback"

    /**
     * Verify that Clerk is properly configured (for development).
     */
    fun isConfigured(): Boolean =
        PUBLISHABLE_KEY.isNotBlank() &&
                SECRET_KEY.isNotBlank()
}

/**
 * Typical Clerk user object after successful authentication.
 * This would come from Clerk's backend after token verification.
 */
@Serializable
data class ClerkUser(
    val id: String,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val profileImageUrl: String?,
    val primaryPhoneNumberId: String?,
    val phoneNumbers: List<ClerkPhoneNumber> = emptyList()
)

@Serializable
data class ClerkPhoneNumber(
    val id: String,
    val phoneNumber: String,
    val verificationStatus: String
)

/**
 * Integration notes:
 * - For web: Use @clerk/nextjs or @clerk/react
 * - For mobile: Use custom OAuth or Clerk's mobile SDK when available
 * - For backend: Verify tokens using Clerk's jwks_uri
 * - After auth, create/update user in Supabase with matching UID
 */
