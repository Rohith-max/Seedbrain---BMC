package com.nidhi.app.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.data.remote.SupabaseRepository
import com.nidhi.app.domain.repository.AlertRepository
import com.nidhi.app.domain.repository.BenefitRepository
import com.nidhi.app.domain.repository.DocumentRepository
import com.nidhi.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val userRepository: UserRepository         by inject()
    private val benefitRepository: BenefitRepository  by inject()
    private val alertRepository: AlertRepository       by inject()
    private val documentRepository: DocumentRepository by inject()
    private val userPreferences: UserPreferences       by inject()
    private val supabaseRepository: SupabaseRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            val userId = userPreferences.currentUserId.first() ?: return Result.success()

            // 1 — Firebase sync (existing)
            userRepository.syncWithFirestore(userId)

            // 2 — Evaluate benefits eligibility
            benefitRepository.evaluateEligibility(userId)

            // 3 — Schedule local expiry alerts
            alertRepository.scheduleExpiryAlerts(userId)

            // 4 — Sync documents to Supabase (cloud backup)
            try {
                val docs = documentRepository.getDocuments(userId).first()
                docs.forEach { doc ->
                    supabaseRepository.upsertDocument(doc)
                }
                Log.d("SyncWorker", "Synced ${docs.size} docs to Supabase")
            } catch (e: Exception) {
                Log.w("SyncWorker", "Supabase doc sync skipped: ${e.message}")
            }

            // 5 — Sync alerts to Supabase (so they appear across devices)
            try {
                val alerts = alertRepository.getAlerts(userId).first()
                alerts.forEach { alert ->
                    supabaseRepository.upsertAlert(alert)
                }
            } catch (e: Exception) {
                Log.w("SyncWorker", "Supabase alert sync skipped: ${e.message}")
            }

            userPreferences.setLastSyncTime(System.currentTimeMillis())
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync failed: ${e.message}")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "nidhi_periodic_sync"

        fun buildRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            return PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()
        }
    }
}
