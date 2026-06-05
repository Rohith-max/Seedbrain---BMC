package com.nidhi.app.worker

import android.content.Context
import androidx.work.*
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.repository.AlertRepository
import com.nidhi.app.domain.repository.BenefitRepository
import com.nidhi.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams), KoinComponent {

    private val userRepository: UserRepository by inject()
    private val benefitRepository: BenefitRepository by inject()
    private val alertRepository: AlertRepository by inject()
    private val userPreferences: UserPreferences by inject()

    override suspend fun doWork(): Result {
        return try {
            val userId = userPreferences.currentUserId.first() ?: return Result.success()
            userRepository.syncWithFirestore(userId)
            benefitRepository.evaluateEligibility(userId)
            alertRepository.scheduleExpiryAlerts(userId)
            userPreferences.setLastSyncTime(System.currentTimeMillis())
            Result.success()
        } catch (e: Exception) {
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
