package com.nidhi.app.data.remote

import android.util.Log
import com.nidhi.app.domain.model.Alert
import com.nidhi.app.domain.model.AlertType
import com.nidhi.app.domain.model.Document
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val TAG = "SupabaseRepo"

// ── DTOs for Supabase tables ──────────────────────────────────────────────────

@Serializable
data class SupabaseDocument(
    val id: String,
    val owner_id: String,
    val type: String,
    val title: String,
    val ocr_text: String? = null,
    val expiry_date: Long? = null,
    val created_at: Long,
    val updated_at: Long
)

@Serializable
data class SupabaseAlert(
    val id: String,
    val user_id: String,
    val title: String,
    val message: String,
    val type: String,
    val trigger_time: Long,
    val is_read: Boolean = false
)

// ── Repository ────────────────────────────────────────────────────────────────

class SupabaseRepository {

    private val client = supabaseClient

    // ── Auth ──────────────────────────────────────────────────────────────────

    suspend fun signInWithEmail(email: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                client.auth.signInWith(Email) {
                    this.email    = email
                    this.password = password
                }
                val uid = client.auth.currentUserOrNull()?.id ?: "unknown"
                Result.success(uid)
            } catch (e: Exception) {
                Log.e(TAG, "signIn failed: ${e.message}")
                Result.failure(e)
            }
        }

    suspend fun signUpWithEmail(
        email: String, password: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            client.auth.signUpWith(Email) {
                this.email    = email
                this.password = password
            }
            val uid = client.auth.currentUserOrNull()?.id ?: "unknown"
            Result.success(uid)
        } catch (e: Exception) {
            Log.e(TAG, "signUp failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {
        try { client.auth.signOut() } catch (e: Exception) { Log.e(TAG, "signOut: ${e.message}") }
    }

    fun currentUserId(): String? = client.auth.currentUserOrNull()?.id

    // ── Documents ─────────────────────────────────────────────────────────────

    /** Upsert a document to Supabase (cloud backup) */
    suspend fun upsertDocument(doc: Document): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val dto = SupabaseDocument(
                    id          = doc.id,
                    owner_id    = doc.ownerId,
                    type        = doc.type,
                    title       = doc.title,
                    ocr_text    = doc.ocrText,
                    expiry_date = doc.expiryDate,
                    created_at  = doc.createdAt,
                    updated_at  = doc.updatedAt
                )
                client.from("documents").upsert(dto)
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "upsertDocument: ${e.message}")
                Result.failure(e)
            }
        }

    /** Fetch all documents for a user from Supabase */
    suspend fun fetchDocuments(userId: String): Result<List<SupabaseDocument>> =
        withContext(Dispatchers.IO) {
            try {
                val docs = client.from("documents")
                    .select(Columns.ALL) { filter { eq("owner_id", userId) } }
                    .decodeList<SupabaseDocument>()
                Result.success(docs)
            } catch (e: Exception) {
                Log.e(TAG, "fetchDocuments: ${e.message}")
                Result.failure(e)
            }
        }

    /** Delete a document from Supabase */
    suspend fun deleteDocument(docId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                client.from("documents").delete { filter { eq("id", docId) } }
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "deleteDocument: ${e.message}")
                Result.failure(e)
            }
        }

    // ── Alerts ────────────────────────────────────────────────────────────────

    /** Push an alert to Supabase so it syncs across devices */
    suspend fun upsertAlert(alert: Alert): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val dto = SupabaseAlert(
                    id           = alert.id,
                    user_id      = alert.userId,
                    title        = alert.title,
                    message      = alert.message,
                    type         = alert.type.name.lowercase(),
                    trigger_time = alert.triggerTime,
                    is_read      = alert.isRead
                )
                client.from("alerts").upsert(dto)
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "upsertAlert: ${e.message}")
                Result.failure(e)
            }
        }

    suspend fun fetchAlerts(userId: String): Result<List<SupabaseAlert>> =
        withContext(Dispatchers.IO) {
            try {
                val alerts = client.from("alerts")
                    .select(Columns.ALL) { filter { eq("user_id", userId) } }
                    .decodeList<SupabaseAlert>()
                Result.success(alerts)
            } catch (e: Exception) {
                Log.e(TAG, "fetchAlerts: ${e.message}")
                Result.failure(e)
            }
        }
}
