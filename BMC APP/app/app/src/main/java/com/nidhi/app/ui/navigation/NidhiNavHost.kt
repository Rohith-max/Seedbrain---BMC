package com.nidhi.app.ui.navigation

import android.content.Intent
import android.util.Base64
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.feature.ai.AiChatListScreen
import com.nidhi.app.feature.ai.ChatDetailScreen
import com.nidhi.app.feature.auth.BiometricLockScreen
import com.nidhi.app.feature.auth.LoginScreen
import com.nidhi.app.feature.auth.OnboardingScreen
import com.nidhi.app.feature.auth.OtpVerifyScreen
import com.nidhi.app.feature.benefits.BenefitDetailScreen
import com.nidhi.app.feature.settings.FamilyMembersScreen
import com.nidhi.app.feature.benefits.BenefitsScreen
import com.nidhi.app.feature.document.DocumentCaptureScreen
import com.nidhi.app.feature.document.DocumentDetailScreen
import com.nidhi.app.feature.document.DocumentsScreen
import com.nidhi.app.feature.emergency.EmergencyScreen
import com.nidhi.app.feature.home.HomeScreen
import com.nidhi.app.feature.notifications.AlertsScreen
import com.nidhi.app.feature.settings.SettingsScreen
import com.nidhi.app.feature.webview.WebViewScreen
import org.koin.compose.koinInject

@Composable
fun NidhiNavHost(
    isOnboardingComplete: Boolean,
    deepLinkIntent: Intent?
) {
    val navController = rememberNavController()
    val userPreferences: UserPreferences = koinInject()

    // Observe biometric preference reactively
    val isBiometricEnabled by userPreferences.isBiometricEnabled.collectAsState(initial = false)
    var biometricUnlocked by remember { mutableStateOf(false) }

    // Show biometric lock if enabled and not yet unlocked this session
    if (isBiometricEnabled && !biometricUnlocked) {
        BiometricLockScreen(
            onUnlocked = { biometricUnlocked = true },
            onSkip = { biometricUnlocked = true }
        )
        return
    }

    val startDest = if (isOnboardingComplete) NavRoutes.HOME else NavRoutes.ONBOARDING

    // Bottom nav visibility
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = BottomNavItem.items.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(300)
                )
            },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(200)) },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(300)
                )
            }
        ) {
            // ── Auth ──────────────────────────────────────────────────────────
            composable(NavRoutes.ONBOARDING) {
                OnboardingScreen(
                    onGetStarted = { navController.navigate(NavRoutes.LOGIN) }
                )
            }
            composable(NavRoutes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                        }
                    },
                    onNavigateToOtp = { verificationId, phone ->
                        navController.navigate(NavRoutes.otpVerify(verificationId, phone))
                    }
                )
            }
            composable(NavRoutes.OTP_VERIFY) { backStack ->
                val verificationId = backStack.arguments?.getString("verificationId") ?: ""
                val phone = backStack.arguments?.getString("phone") ?: ""
                OtpVerifyScreen(
                    verificationId = verificationId,
                    phone = phone,
                    onVerified = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                        }
                    },
                    onBack = { navController.navigateUp() }
                )
            }

            // ── Main ──────────────────────────────────────────────────────────
            composable(NavRoutes.HOME) {
                HomeScreen(
                    onNavigateToDocuments = { navController.navigate(NavRoutes.DOCUMENTS) },
                    onNavigateToBenefits = { navController.navigate(NavRoutes.BENEFITS) },
                    onNavigateToAlerts = { navController.navigate(NavRoutes.ALERTS) },
                    onNavigateToEmergency = { navController.navigate(NavRoutes.EMERGENCY) }
                )
            }
            composable(NavRoutes.DOCUMENTS) {
                DocumentsScreen(
                    onDocumentClick = { id ->
                        navController.navigate(NavRoutes.documentDetail(id))
                    },
                    onAddDocument = { navController.navigate(NavRoutes.DOCUMENT_CAPTURE) }
                )
            }
            composable(NavRoutes.BENEFITS) {
                BenefitsScreen(
                    onBenefitClick = { id ->
                        navController.navigate(NavRoutes.benefitDetail(id))
                    }
                )
            }
            composable(NavRoutes.AI_CHAT) {
                AiChatListScreen(
                    onConversationClick = { id ->
                        navController.navigate(NavRoutes.chatDetail(id))
                    },
                    onNewChat = {
                        navController.navigate(NavRoutes.chatDetail("new"))
                    }
                )
            }
            composable(NavRoutes.SETTINGS) {
                SettingsScreen(
                    onNavigateToFamilyMembers = { navController.navigate(NavRoutes.FAMILY_MEMBERS) },
                    onSignOut = {
                        biometricUnlocked = false
                        navController.navigate(NavRoutes.ONBOARDING) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToEmergency = { navController.navigate(NavRoutes.EMERGENCY) }
                )
            }

            // ── Sub-screens ───────────────────────────────────────────────────
            composable(NavRoutes.DOCUMENT_DETAIL) { backStack ->
                val documentId = backStack.arguments?.getString("documentId") ?: ""
                DocumentDetailScreen(
                    documentId = documentId,
                    onBack = { navController.navigateUp() }
                )
            }
            composable(NavRoutes.DOCUMENT_CAPTURE) {
                DocumentCaptureScreen(
                    onCaptured = { navController.navigateUp() },
                    onBack = { navController.navigateUp() }
                )
            }
            composable(NavRoutes.CHAT_DETAIL) { backStack ->
                val conversationId = backStack.arguments?.getString("conversationId") ?: "new"
                ChatDetailScreen(
                    conversationId = conversationId,
                    onBack = { navController.navigateUp() }
                )
            }
            composable(NavRoutes.EMERGENCY) {
                EmergencyScreen(onBack = { navController.navigateUp() })
            }
            composable(NavRoutes.ALERTS) {
                AlertsScreen(onBack = { navController.navigateUp() })
            }
            composable(NavRoutes.BENEFIT_DETAIL) { backStack ->
                val benefitId = backStack.arguments?.getString("benefitId") ?: ""
                BenefitDetailScreen(
                    benefitId = benefitId,
                    onBack = { navController.navigateUp() },
                    onOpenUrl = { url, title ->
                        navController.navigate(NavRoutes.webView(url, title))
                    }
                )
            }
            composable(NavRoutes.FAMILY_MEMBERS) {
                FamilyMembersScreen(onBack = { navController.navigateUp() })
            }

            // ── In-app WebView browser ────────────────────────────────────────
            composable(NavRoutes.WEB_VIEW) { backStack ->
                val encodedUrl = backStack.arguments?.getString("encodedUrl") ?: ""
                val title = backStack.arguments?.getString("title") ?: "Official Website"
                val url = try {
                    String(Base64.decode(encodedUrl, Base64.URL_SAFE or Base64.NO_WRAP))
                } catch (_: Exception) { "https://india.gov.in" }

                WebViewScreen(
                    url = url,
                    title = android.net.Uri.decode(title),
                    onBack = { navController.navigateUp() }
                )
            }
        }
    }
}
