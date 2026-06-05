package com.nidhi.app.feature.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nidhi.app.MainActivity
import com.nidhi.app.NidhiApplication
import com.nidhi.app.R

class NidhiFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "NIDHI"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        val deepLink = remoteMessage.data["deep_link"]
        val channel = remoteMessage.data["channel"] ?: NidhiApplication.CHANNEL_GENERAL

        showNotification(title, body, deepLink, channel)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Save token to DataStore / Firestore for server-side push
    }

    private fun showNotification(
        title: String,
        body: String,
        deepLink: String?,
        channel: String
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            deepLink?.let { putExtra("deep_link", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channel)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
