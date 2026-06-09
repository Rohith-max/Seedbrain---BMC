package com.nidhi.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.ui.navigation.NidhiNavHost
import com.nidhi.app.ui.theme.NidhiTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val userPreferences: UserPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Three-way theme (Req 19.1, 19.2)
            val themeMode by userPreferences.themeMode.collectAsState(initial = "System Default")
            val systemDark  = isSystemInDarkTheme()
            val isDarkTheme = when (themeMode) {
                "Dark"  -> true
                "Light" -> false
                else    -> systemDark          // "System Default"
            }

            val isOnboardingComplete by userPreferences.isOnboardingComplete.collectAsState(initial = null)

            // Keep splash visible until preferences are loaded
            splashScreen.setKeepOnScreenCondition { isOnboardingComplete == null }

            NidhiTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val onboarding = isOnboardingComplete) {
                        null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        else -> {
                            NidhiNavHost(
                                isOnboardingComplete = onboarding,
                                deepLinkIntent = intent
                            )
                        }
                    }
                }
            }
        }
    }
}
