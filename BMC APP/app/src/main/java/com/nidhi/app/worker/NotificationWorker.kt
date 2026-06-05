package com.nidhi.app.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.nidhi.app.MainActivity
import com.nidhi.app.NidhiApplication
import java.util.concurrent.TimeUnit

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val title    = inputData.getString(KEY_TITLE)    ?: "NIDHI Reminder"
        val message  = inputData.getString(KEY_MESSAGE)  ?: ""
        val deepLink = inputData.getString(KEY_DEEP_LINK)
        val channel  = inputData.getString(KEY_CHANNEL)  ?: NidhiApplication.CHANNEL_ALERTS
        showNotification(title, message, deepLink, channel)
        return Result.success()
    }

    private fun showNotification(
        title: String, message: String, deepLink: String?, channel: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            deepLink?.let { putExtra("deep_link", it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            context, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val KEY_TITLE    = "title"
        const val KEY_MESSAGE  = "message"
        const val KEY_DEEP_LINK = "deep_link"
        const val KEY_CHANNEL  = "channel"

        fun buildRequest(
            title: String,
            message: String,
            deepLink: String? = null,
            channel: String = NidhiApplication.CHANNEL_ALERTS,
            delayMillis: Long = 0L
        ): OneTimeWorkRequest {
            val data = Data.Builder()
                .putString(KEY_TITLE, title)
                .putString(KEY_MESSAGE, message)
                .apply { deepLink?.let { putString(KEY_DEEP_LINK, it) } }
                .putString(KEY_CHANNEL, channel)
                .build()
            return OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(data)
                .apply { if (delayMillis > 0) setInitialDelay(delayMillis, TimeUnit.MILLISECONDS) }
                .build()
        }
    }
}
