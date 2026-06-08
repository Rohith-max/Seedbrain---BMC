package com.nidhi.app.ui.navigation

object NavRoutes {
    // Auth
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val OTP_VERIFY = "otp_verify/{verificationId}/{phone}"
    const val PIN_LOCK = "pin_lock"
    const val BIOMETRIC_LOCK = "biometric_lock"

    // Main (bottom nav)
    const val HOME = "home"
    const val DOCUMENTS = "documents"
    const val BENEFITS = "benefits"
    const val AI_CHAT = "ai_chat"
    const val SETTINGS = "settings"

    // Sub-screens
    const val DOCUMENT_DETAIL = "document/{documentId}"
    const val DOCUMENT_CAPTURE = "document_capture"
    const val CHAT_DETAIL = "chat/{conversationId}"
    const val FAMILY_MEMBERS = "family_members"
    const val ADD_FAMILY_MEMBER = "add_family_member"
    const val EMERGENCY = "emergency"
    const val ALERTS = "alerts"
    const val BENEFIT_DETAIL = "benefit/{benefitId}"
    const val CALENDAR = "calendar"
    const val DEMO_MODE = "demo"
    const val WEB_VIEW = "webview/{encodedUrl}/{title}"

    // Helpers
    fun documentDetail(documentId: String) = "document/$documentId"
    fun chatDetail(conversationId: String) = "chat/$conversationId"
    fun otpVerify(verificationId: String, phone: String) = "otp_verify/$verificationId/$phone"
    fun benefitDetail(benefitId: String) = "benefit/$benefitId"
    fun webView(url: String, title: String = "Official Website"): String {
        val encoded = android.util.Base64.encodeToString(url.toByteArray(), android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP)
        val encodedTitle = android.net.Uri.encode(title)
        return "webview/$encoded/$encodedTitle"
    }
}
