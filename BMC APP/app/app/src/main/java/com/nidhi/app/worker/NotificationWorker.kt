package com.nidhi.app.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.nidhi.app.MainActivity
import com.nidhi.app.NidhiApplication
import com.nidhi.app.R
import com.nidhi.app.feature.notifications.WhatsAppNotifier
import java.util.concurrent.TimeUnit

/**
 * Fully functional notification worker.
 * Fires a rich system notification AND optionally forwards to WhatsApp.
 */
class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val title       = inputData.getString(KEY_TITLE)       ?: "NIDHI Reminder"
        val message     = inputData.getString(KEY_MESSAGE)     ?: ""
        val deepLink    = inputData.getString(KEY_DEEP_LINK)
        val channel     = inputData.getString(KEY_CHANNEL)     ?: NidhiApplication.CHANNEL_ALERTS
        val whatsappNum = inputData.getString(KEY_WHATSAPP_PHONE)
        val sendWA      = inputData.getBoolean(KEY_SEND_WHATSAPP, false)

        showSystemNotification(title, message, deepLink, channel)

        if (sendWA) {
            val waMessage = WhatsAppNotifier.buildExpiryMessage(title, 0)
            WhatsAppNotifier.send(context, waMessage, whatsappNum)
        }

        return Result.success()
    }

    private fun showSystemNotification(
        title: String,
        message: String,
        deepLink: String?,
        channel: String
    ) {
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            deepLink?.let { putExtra("deep_link", it) }
        }
        val tapPi = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // "View in app" action
        val viewPi = PendingIntent.getActivity(
            context,
            (System.currentTimeMillis() + 1).toInt(),
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channel)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setPriority(
                if (channel == NidhiApplication.CHANNEL_ALERTS)
                    NotificationCompat.PRIORITY_HIGH
                else
                    NotificationCompat.PRIORITY_DEFAULT
            )
            .setContentIntent(tapPi)
            .addAction(
                android.R.drawable.ic_menu_view,
                "View",
                viewPi
            )
            .build()

        val notifId = (title + message).hashCode()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // POST_NOTIFICATIONS permission required on API 33+; check before notifying
            val nm = context.getSystemService(NotificationManager::class.java)
            if (nm.areNotificationsEnabled()) {
                nm.notify(notifId, notification)
            }
        } else {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(notifId, notification)
        }
    }

    companion object {
        const val KEY_TITLE           = "title"
        const val KEY_MESSAGE         = "message"
        const val KEY_DEEP_LINK       = "deep_link"
        const val KEY_CHANNEL         = "channel"
        const val KEY_SEND_WHATSAPP   = "send_whatsapp"
        const val KEY_WHATSAPP_PHONE  = "whatsapp_phone"

        /**
         * Schedule a one-time notification, optionally forwarded to WhatsApp.
         */
        fun buildRequest(
            title: String,
            message: String,
            deepLink: String?     = null,
            channel: String       = NidhiApplication.CHANNEL_ALERTS,
            delayMillis: Long     = 0L,
            sendToWhatsApp: Boolean = false,
            whatsappPhone: String?  = null
        ): OneTimeWorkRequest {
            val data = Data.Builder()
                .putString(KEY_TITLE,  title)
                .putString(KEY_MESSAGE, message)
                .putString(KEY_CHANNEL, channel)
                .putBoolean(KEY_SEND_WHATSAPP, sendToWhatsApp)
                .apply {
                    deepLink?.let     { putString(KEY_DEEP_LINK, it) }
                    whatsappPhone?.let { putString(KEY_WHATSAPP_PHONE, it) }
                }
                .build()

            return OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(data)
                .apply {
                    if (delayMillis > 0) setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                }
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()
        }
    }
}
