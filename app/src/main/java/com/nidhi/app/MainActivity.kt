package com.nidhi.app

import android.Manifest
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.WorkManager
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.ui.navigation.NidhiNavHost
import com.nidhi.app.ui.theme.NidhiTheme
import com.nidhi.app.worker.NotificationWorker
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val userPreferences: UserPreferences by inject()

    // Android 13+ runtime notification permission request
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fireWelcomeNotification()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request POST_NOTIFICATIONS on Android 13+
        requestNotificationPermissionIfNeeded()

        setContent {
            val isDarkTheme by userPreferences.isDarkTheme.collectAsState(initial = false)
            val appThemeKey by userPreferences.appTheme.collectAsState(initial = "teal")
            val isOnboardingComplete by userPreferences.isOnboardingComplete.collectAsState(initial = null)

            splashScreen.setKeepOnScreenCondition { isOnboardingComplete == null }

            NidhiTheme(
                darkTheme = isDarkTheme,
                appTheme  = com.nidhi.app.ui.theme.AppTheme.fromKey(appThemeKey)
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val onboarding = isOnboardingComplete) {
                        null -> Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                        else -> NidhiNavHost(
                            isOnboardingComplete = onboarding,
                            deepLinkIntent = intent
                        )
                    }
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Already granted – fire welcome notification on first install
                    fireWelcomeNotification()
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Pre-Android 13 – notifications allowed by default
            fireWelcomeNotification()
        }
    }

    /**
     * Fires a one-time welcome notification so the user can verify
     * notifications are working immediately after install.
     */
    private fun fireWelcomeNotification() {
        val nm = getSystemService(NotificationManager::class.java) ?: return
        if (!nm.areNotificationsEnabled()) return

        // Only fire once – check if channel is already set up
        val prefs = getSharedPreferences("nidhi_meta", MODE_PRIVATE)
        if (prefs.getBoolean("welcome_notif_sent", false)) return
        prefs.edit().putBoolean("welcome_notif_sent", true).apply()

        val notification = NotificationCompat.Builder(this, NidhiApplication.CHANNEL_GENERAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Welcome to NIDHI 🙏")
            .setContentText("Your family's financial guardian is ready. Scan documents, explore benefits, and stay alert!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("NIDHI helps you:\n• Track Aadhaar, PAN, Passport expiry\n• Find eligible government schemes\n• Get AI-powered family financial advice\n\nTap to get started!"))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        nm.notify(1001, notification)

        // Also schedule a document-expiry reminder for 3 days from now
        val reminderRequest = NotificationWorker.buildRequest(
            title = "📄 Check Your Documents",
            message = "Have you scanned your Aadhaar, PAN, and Passport? Keep them updated in NIDHI for expiry alerts.",
            channel = NidhiApplication.CHANNEL_ALERTS,
            delayMillis = 3 * 24 * 60 * 60 * 1000L // 3 days
        )
        WorkManager.getInstance(this).enqueue(reminderRequest)
    }
}
