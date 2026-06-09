package com.nidhi.app.data.sync

import android.content.Context
import androidx.work.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.nidhi.app.data.local.dao.*
import com.nidhi.app.data.local.entity.AlertEntity
import com.nidhi.app.data.local.entity.ConversationEntity
import com.nidhi.app.data.local.entity.DocumentEntity
import com.nidhi.app.data.local.entity.FamilyMemberEntity
import com.nidhi.app.data.local.prefs.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

/**
 * CoroutineWorker that performs bi-directional Room ↔ Firestore sync.
 *
 * Sync phases (in order):
 *  1. syncUserProfile    — merge-writes local user to Firestore users/{uid}
 *  2. syncFamilyMembers  — last-write-wins merge via updatedAt
 *  3. syncDocuments      — metadata only (no image files); skips tombstoned IDs
 *  4. syncConversations  — full conversation records
 *  5. syncAlerts         — full alert records
 *
 * Non-retryable Firestore codes (PERMISSION_DENIED, UNAUTHENTICATED, NOT_FOUND,
 * INVALID_ARGUMENT) are logged and skipped — the worker returns success so
 * WorkManager does not retry for permanent errors (Req 4.7, 6.6).
 *
 * Batch writes are capped at 500 operations each (Req 6.5).
 */
class FirestoreSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val firestore: FirebaseFirestore by inject()
    private val userDao: UserDao by inject()
    private val documentDao: DocumentDao by inject()
    private val conversationDao: ConversationDao by inject()
    private val alertDao: AlertDao by inject()
    private val tombstoneDao: TombstoneDao by inject()
    private val userPreferences: UserPreferences by inject()

    override suspend fun doWork(): Result {
        val uid = userPreferences.currentUserId.first()
            ?: return Result.failure()

        runCatching { syncUserProfile(uid) }.onFailure { logErr("user profile", it) }
        runCatching { syncFamilyMembers(uid) }.onFailure { logErr("family members", it) }
        runCatching { syncDocuments(uid) }.onFailure { logErr("documents", it) }
        runCatching { syncConversations(uid) }.onFailure { logErr("conversations", it) }
        runCatching { syncAlerts(uid) }.onFailure { logErr("alerts", it) }

        userPreferences.setLastSyncTime(System.currentTimeMillis())
        return Result.success()
    }

    // ── 1. User profile ────────────────────────────────────────────────────────

    private suspend fun syncUserProfile(uid: String) {
        val user = userDao.getUserById(uid) ?: return
        val data = mapOf(
            "uid"       to user.uid,
            "email"     to (user.email ?: ""),
            "phone"     to (user.phone ?: ""),
            "name"      to user.name,
            "photoUri"  to (user.photoUri ?: ""),
            "createdAt" to user.createdAt
        )
        safeFs { firestore.collection("users").document(uid).set(data, SetOptions.merge()).await() }
    }

    // ── 2. Family members ─────────────────────────────────────────────────────

    private suspend fun syncFamilyMembers(uid: String) {
        // Pull remote → upsert locally if newer (Req 4.5 last-write-wins)
        val snap = safeFs {
            firestore.collection("users").document(uid).collection("family_members").get().await()
        } ?: return

        snap.documents.forEach { doc ->
            val remoteTs = doc.getLong("updatedAt") ?: 0L
            val local = runCatching { userDao.getFamilyMemberById(doc.id) }.getOrNull()
            if (local == null || remoteTs > local.updatedAt) {
                runCatching {
                    userDao.upsertFamilyMember(
                        FamilyMemberEntity(
                            id        = doc.id,
                            userId    = uid,
                            name      = doc.getString("name") ?: "",
                            relation  = doc.getString("relation") ?: "",
                            dob       = doc.getLong("dob"),
                            photoUri  = doc.getString("photoUri"),
                            contactId = doc.getString("contactId"),
                            updatedAt = remoteTs
                        )
                    )
                }.onFailure { logErr("fm upsert ${doc.id}", it) }
            }
        }

        // Push local → Firestore in batches of 500
        val locals = runCatching { userDao.getFamilyMembersDirect(uid) }.getOrElse { emptyList() }
        locals.chunked(500).forEach { chunk ->
            safeFs {
                val batch = firestore.batch()
                chunk.forEach { m ->
                    val ref = firestore.collection("users").document(uid)
                        .collection("family_members").document(m.id)
                    batch.set(ref, mapOf(
                        "name"      to m.name,
                        "relation"  to m.relation,
                        "dob"       to m.dob,
                        "photoUri"  to m.photoUri,
                        "contactId" to m.contactId,
                        "updatedAt" to m.updatedAt
                    ))
                }
                batch.commit().await()
            }
        }
    }

    // ── 3. Documents metadata ─────────────────────────────────────────────────

    private suspend fun syncDocuments(uid: String) {
        val snap = safeFs {
            firestore.collection("users").document(uid).collection("documents").get().await()
        } ?: return

        val tombstoned = runCatching { tombstoneDao.getAllDeletedIds() }.getOrElse { emptyList() }.toSet()

        snap.documents.forEach { doc ->
            if (doc.id in tombstoned) return@forEach   // Req 5.2 — skip tombstoned
            val remoteTs = doc.getLong("updatedAt") ?: 0L
            val local = runCatching { documentDao.getDocumentById(doc.id) }.getOrNull()
            if (local == null || remoteTs > local.updatedAt) {
                runCatching {
                    documentDao.upsertDocument(
                        DocumentEntity(
                            id             = doc.id,
                            ownerId        = uid,
                            type           = doc.getString("type") ?: "",
                            title          = doc.getString("title") ?: "",
                            filePath       = "",   // image never stored in Firestore (Req 5.1)
                            thumbnailPath  = doc.getString("thumbnailPath"),
                            ocrText        = doc.getString("ocrText"),
                            summaryJson    = doc.getString("summaryJson"),
                            expiryDate     = doc.getLong("expiryDate"),
                            linkedMemberId = doc.getString("linkedMemberId"),
                            createdAt      = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                            updatedAt      = remoteTs
                        )
                    )
                }.onFailure { logErr("doc upsert ${doc.id}", it) }
            }
        }

        // Push local metadata → Firestore
        val locals = runCatching { documentDao.getDocumentsByOwner(uid) }.getOrElse { emptyList() }
        locals.chunked(500).forEach { chunk ->
            safeFs {
                val batch = firestore.batch()
                chunk.forEach { d ->
                    val ref = firestore.collection("users").document(uid)
                        .collection("documents").document(d.id)
                    batch.set(ref, mapOf(
                        "type"           to d.type,
                        "title"          to d.title,
                        "thumbnailPath"  to d.thumbnailPath,
                        "ocrText"        to d.ocrText,
                        "summaryJson"    to d.summaryJson,
                        "expiryDate"     to d.expiryDate,
                        "linkedMemberId" to d.linkedMemberId,
                        "createdAt"      to d.createdAt,
                        "updatedAt"      to d.updatedAt
                    ))
                }
                batch.commit().await()
            }
        }
    }

    // ── 4. Conversations ───────────────────────────────────────────────────────

    private suspend fun syncConversations(uid: String) {
        val snap = safeFs {
            firestore.collection("users").document(uid).collection("conversations").get().await()
        } ?: return

        snap.documents.forEach { doc ->
            val remoteTs = doc.getLong("updatedAt") ?: 0L
            val local = runCatching { conversationDao.getConversationById(doc.id) }.getOrNull()
            if (local == null || remoteTs > local.updatedAt) {
                runCatching {
                    conversationDao.upsertConversation(
                        ConversationEntity(
                            id           = doc.id,
                            userId       = uid,
                            title        = doc.getString("title") ?: "",
                            messagesJson = doc.getString("messagesJson") ?: "[]",
                            createdAt    = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                            updatedAt    = remoteTs
                        )
                    )
                }.onFailure { logErr("conv upsert ${doc.id}", it) }
            }
        }

        val locals = runCatching { conversationDao.getConversationsDirect(uid) }.getOrElse { emptyList() }
        locals.chunked(500).forEach { chunk ->
            safeFs {
                val batch = firestore.batch()
                chunk.forEach { c ->
                    val ref = firestore.collection("users").document(uid)
                        .collection("conversations").document(c.id)
                    batch.set(ref, mapOf(
                        "title"        to c.title,
                        "messagesJson" to c.messagesJson,
                        "createdAt"    to c.createdAt,
                        "updatedAt"    to c.updatedAt
                    ))
                }
                batch.commit().await()
            }
        }
    }

    // ── 5. Alerts ─────────────────────────────────────────────────────────────

    private suspend fun syncAlerts(uid: String) {
        val snap = safeFs {
            firestore.collection("users").document(uid).collection("alerts").get().await()
        } ?: return

        snap.documents.forEach { doc ->
            runCatching {
                alertDao.upsertAlert(
                    AlertEntity(
                        id          = doc.id,
                        userId      = uid,
                        title       = doc.getString("title") ?: "",
                        message     = doc.getString("message") ?: "",
                        type        = doc.getString("type") ?: "GENERAL",
                        triggerTime = doc.getLong("triggerTime") ?: System.currentTimeMillis(),
                        isRead      = doc.getBoolean("isRead") ?: false,
                        deepLink    = doc.getString("deepLink"),
                        createdAt   = doc.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                )
            }.onFailure { logErr("alert upsert ${doc.id}", it) }
        }

        val locals = runCatching { alertDao.getAlertsDirect(uid) }.getOrElse { emptyList() }
        locals.chunked(500).forEach { chunk ->
            safeFs {
                val batch = firestore.batch()
                chunk.forEach { a ->
                    val ref = firestore.collection("users").document(uid)
                        .collection("alerts").document(a.id)
                    batch.set(ref, mapOf(
                        "title"       to a.title,
                        "message"     to a.message,
                        "type"        to a.type,
                        "triggerTime" to a.triggerTime,
                        "isRead"      to a.isRead,
                        "deepLink"    to a.deepLink,
                        "createdAt"   to a.createdAt
                    ))
                }
                batch.commit().await()
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Runs a Firestore block, catching non-retryable errors and returning null for them. */
    private suspend fun <T> safeFs(block: suspend () -> T): T? = try {
        block()
    } catch (e: FirebaseFirestoreException) {
        if (isNonRetryable(e)) { logErr("firestore", e); null } else throw e
    }

    private fun isNonRetryable(e: FirebaseFirestoreException) = e.code in setOf(
        FirebaseFirestoreException.Code.PERMISSION_DENIED,
        FirebaseFirestoreException.Code.UNAUTHENTICATED,
        FirebaseFirestoreException.Code.NOT_FOUND,
        FirebaseFirestoreException.Code.INVALID_ARGUMENT
    )

    private fun logErr(phase: String, t: Throwable) =
        android.util.Log.e(TAG, "Sync '$phase' failed: ${t.message}")

    companion object {
        private const val TAG = "FirestoreSyncWorker"
        const val WORK_NAME   = "firestore_sync"

        /** One-time network-constrained sync with exponential back-off. */
        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.KEEP,
                OneTimeWorkRequestBuilder<FirestoreSyncWorker>()
                    .setConstraints(constraints)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                    .build()
            )
        }

        /** Periodic sync (15 min minimum interval). */
        fun enqueueRecurring(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "${WORK_NAME}_periodic",
                ExistingPeriodicWorkPolicy.KEEP,
                androidx.work.PeriodicWorkRequestBuilder<FirestoreSyncWorker>(
                    15, TimeUnit.MINUTES
                ).setConstraints(constraints).build()
            )
        }
    }
}
