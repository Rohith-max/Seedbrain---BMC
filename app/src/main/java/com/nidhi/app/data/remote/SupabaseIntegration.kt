package com.nidhi.app.data.remote

import android.content.Context
import com.nidhi.app.config.ApiKeys
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Supabase client initialization.
 *
 * Supabase provides:
 * - PostgreSQL database (Postgrest)
 * - Real-time sync (Realtime)
 * - User authentication (Auth)
 * - File storage (Storage)
 *
 * This is a single source of truth for backend connectivity.
 */
object SupabaseIntegration {

    private lateinit var supabaseClient: io.github.jan.supabase.SupabaseClient

    /**
     * Initialize Supabase client with the provided API keys.
     * Should be called once in Application.onCreate() or a Koin module.
     */
    fun initialize() {
        supabaseClient = createSupabaseClient(
            supabaseUrl = ApiKeys.SUPABASE_URL,
            supabaseKey = ApiKeys.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }

    /**
     * Get the initialized Supabase client.
     */
    fun getClient() = supabaseClient

    /**
     * Check if Supabase is properly configured.
     */
    fun isConfigured(): Boolean =
        ::supabaseClient.isInitialized &&
                ApiKeys.SUPABASE_URL.isNotBlank() &&
                ApiKeys.SUPABASE_ANON_KEY.isNotBlank()
}

/**
 * Supabase tables schema (for documentation — these are created in Supabase console).
 *
 * TABLE: users
 *   - uid (UUID, PK)
 *   - email (text)
 *   - phone (text, nullable)
 *   - name (text)
 *   - avatar_url (text, nullable)
 *   - created_at (timestamp)
 *   - updated_at (timestamp)
 *
 * TABLE: documents
 *   - id (UUID, PK)
 *   - owner_id (UUID, FK → users.uid)
 *   - type (text) — Aadhaar, PAN, Passport, etc.
 *   - title (text)
 *   - file_path (text) — GCS/S3 path
 *   - thumbnail_path (text, nullable)
 *   - ocr_text (text, nullable)
 *   - summary_json (text, nullable)
 *   - expiry_date (timestamp, nullable)
 *   - created_at (timestamp)
 *   - updated_at (timestamp)
 *
 * TABLE: conversations (AI chat history)
 *   - id (UUID, PK)
 *   - user_id (UUID, FK → users.uid)
 *   - title (text)
 *   - messages_json (text)
 *   - created_at (timestamp)
 *   - updated_at (timestamp)
 */
