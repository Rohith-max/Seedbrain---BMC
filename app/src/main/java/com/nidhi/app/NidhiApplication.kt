package com.nidhi.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.nidhi.app.di.appModule
import com.nidhi.app.di.viewModelModule
import com.nidhi.app.worker.SyncWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class NidhiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin DI
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@NidhiApplication)
            modules(appModule, viewModelModule)
        }

        createNotificationChannels()
        schedulePeriodicSync()
    }

    private fun schedulePeriodicSync() {
        // Configure WorkManager manually (no Hilt integration needed)
        try {
            WorkManager.initialize(
                this,
                Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.ERROR)
                    .build()
            )
        } catch (_: IllegalStateException) {
            // Already initialized
        }

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            SyncWorker.buildRequest()
        )
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannels(
                listOf(
                    NotificationChannel(
                        CHANNEL_ALERTS, "Alerts & Reminders",
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Document expiry and deadline reminders"
                        enableVibration(true)
                    },
                    NotificationChannel(
                        CHANNEL_BENEFITS, "Benefits Updates",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply { description = "New eligible benefits and scheme updates" },
                    NotificationChannel(
                        CHANNEL_GENERAL, "General",
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply { description = "General NIDHI notifications" }
                )
            )
        }
    }

    companion object {
        const val CHANNEL_ALERTS   = "nidhi_alerts"
        const val CHANNEL_BENEFITS = "nidhi_benefits"
        const val CHANNEL_GENERAL  = "nidhi_general"
    }
}
