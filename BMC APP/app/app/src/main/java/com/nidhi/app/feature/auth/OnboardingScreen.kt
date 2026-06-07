package com.nidhi.app.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nidhi.app.ui.theme.Gold600
import com.nidhi.app.ui.theme.Teal600
import kotlinx.coroutines.delay

data class OnboardingPage(
    val emoji: String,
    val title: String,
    val description: String
)

private val pages = listOf(
    OnboardingPage("📂", "All Documents, One Place", "Store Aadhaar, PAN, passports, and every family document securely – accessible offline, always."),
    OnboardingPage("🤖", "AI-Powered Assistance", "Ask NIDHI anything about government benefits, document renewals, or financial planning in your language."),
    OnboardingPage("🏛️", "Discover Benefits", "NIDHI scans your family profile and finds eligible government schemes worth lakhs that you may be missing."),
    OnboardingPage("🔔", "Never Miss a Deadline", "Smart alerts for expiring documents, EMI dates, tax deadlines, and benefit application windows."),
    OnboardingPage("🛡️", "Bank-Grade Security", "End-to-end encryption, biometric lock, and PIN protection keep your family's data private.")
)

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val pagerState = rememberPagerState { pages.size }
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Teal600, Teal600.copy(alpha = 0.85f))
                )
            )
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 4 }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(64.dp))

                // Logo / brand
                Text(
                    text = "NIDHI",
                    style = MaterialTheme.typography.displaySmall,
                    color = Gold600
                )
                Text(
                    text = "Your Family's Financial Guardian",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                )

                Spacer(Modifier.height(48.dp))

                // Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    OnboardingPageContent(pages[page])
                }

                // Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { idx ->
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(if (idx == pagerState.currentPage) 10.dp else 6.dp)
                                .background(
                                    if (idx == pagerState.currentPage)
                                        Gold600
                                    else
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
                                )
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold600,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.lastIndex)
                            "Get Started" else "Continue",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                TextButton(
                    onClick = onGetStarted,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("Skip", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = page.emoji,
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}
