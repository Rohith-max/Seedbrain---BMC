package com.nidhi.app.config

/**
 * Centralized API key configuration for Supabase, Clerk, and GROQ.
 * 
 * IMPORTANT: In production, these should be:
 * 1. Read from BuildConfig fields (set via build.gradle.kts from secrets.properties)
 * 2. Stored in Android Keystore for sensitive keys
 * 3. Never hardcoded in source files
 *
 * For development/demo, see the template at: secrets.properties.template
 * Copy to secrets.properties and fill in your actual keys (never commit secrets.properties)
 */
object ApiKeys {
    
    // Placeholder values — replace with your actual keys in secrets.properties
    const val SUPABASE_URL = "https://jzywkasbmaftxilgpmy.supabase.co"
    // Keys should come from BuildConfig in production
    const val SUPABASE_ANON_KEY = "[FETCH FROM BuildConfig]"
    const val SUPABASE_SERVICE_ROLE_KEY = "[USE ON BACKEND ONLY]"

    // Clerk Authentication
    const val CLERK_PUBLISHABLE_KEY = "[FETCH FROM BuildConfig]"
    const val CLERK_SECRET_KEY = "[BACKEND ONLY - NEVER EMBED]"

    // GROQ API Configuration
    const val GROQ_API_KEY = "[FETCH FROM BuildConfig]"
    const val GROQ_MODEL = "mixtral-8x7b-32768" // Fast and powerful

    // WhatsApp Integration (optional - use WhatsApp Business API or message intent)
    const val WHATSAPP_ENABLED = true

    // Feature Flags
    const val ENABLE_CLOUD_SYNC = true
    const val ENABLE_NOTIFICATIONS = true
    const val ENABLE_WHATSAPP_SHARE = true

    /**
     * Load keys from BuildConfig at runtime (set from secrets.properties)
     * This is more secure than hardcoding.
     */
    fun loadFromBuildConfig() {
        // In real implementation:
        // val supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        // val groqKey = BuildConfig.GROQ_API_KEY
        // val clerkKey = BuildConfig.CLERK_PUBLISHABLE_KEY
    }
}

