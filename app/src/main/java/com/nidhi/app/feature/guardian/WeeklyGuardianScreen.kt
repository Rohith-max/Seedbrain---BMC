package com.nidhi.app.feature.guardian

import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.nidhi.app.NidhiApplication
import com.nidhi.app.feature.notifications.WhatsAppNotifier

data class GuardianInsight(
    val icon: ImageVector,
    val color: Color,
    val title: String,
    val detail: String,
    val action: String,
    val urgency: String   // "high" | "medium" | "low"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyGuardianScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    val insights = listOf(
        GuardianInsight(Icons.Default.Warning, Color(0xFFBA1A1A),
            "Passport expires in 8 months",
            "Renew before international travel. Takes 2-4 weeks normally or 3 days via Tatkaal.",
            "Book appointment at passportindia.gov.in", "high"),
        GuardianInsight(Icons.Default.HealthAndSafety, Color(0xFFBF5700),
            "LIC Policy — Nominee not updated",
            "Your LIC Endowment policy 345678901 has no nominee set. Critical for family protection.",
            "Call LIC branch or update at licindia.in", "high"),
        GuardianInsight(Icons.Default.LocalOffer, Color(0xFF2D6A4F),
            "You qualify for PMAY subsidy",
            "Your income is below ₹12L/year threshold. Eligible for ₹2.67 lakh CLSS subsidy.",
            "Apply at pmaymis.gov.in", "medium"),
        GuardianInsight(Icons.Default.School, Color(0xFF1565C0),
            "Daughter eligible for scholarship",
            "National Scholarship Portal has a ₹50,000 merit scholarship for 8th standard girls.",
            "Apply at scholarships.gov.in", "medium"),
        GuardianInsight(Icons.Default.AccountBalance, Color(0xFF5C3317),
            "FD maturity in 6 months",
            "Your SBI Fixed Deposit of ₹2,50,000 matures in December 2026. Plan reinvestment.",
            "Decide on reinvestment before maturity", "low"),
        GuardianInsight(Icons.Default.Savings, Color(0xFF2D6A4F),
            "Potential savings: ₹80,000/year",
            "Unused ELSS quota ₹50,000 + senior citizen railway concession + APY benefit gap",
            "Check all savings opportunities", "medium"),
    )

    val highCount   = insights.count { it.urgency == "high" }
    val medCount    = insights.count { it.urgency == "medium" }
    val totalSaving = "₹80,000"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("NIDHI Guardian")
                        Text("Weekly family status report",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Summary card
            item {
                Box(
                    Modifier.fillMaxWidth().background(
                        Brush.linearGradient(listOf(Color(0xFF006D77), Color(0xFF004E57))),
                        RoundedCornerShape(16.dp)
                    ).padding(20.dp)
                ) {
                    Column {
                        Text("Family Status Report",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Week of ${java.text.SimpleDateFormat("dd MMM yyyy",
                            java.util.Locale.getDefault()).format(java.util.Date())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(0.7f))
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly) {
                            StatChip("$highCount urgent", Color(0xFFFFDAD6))
                            StatChip("$medCount deadlines", Color(0xFFFFDBC9))
                            StatChip(totalSaving + " savings", Color(0xFFB7E4C7))
                        }
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { sendGuardianNotification(context, highCount, medCount, totalSaving) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Notifications, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Send Alert", style = MaterialTheme.typography.labelMedium)
                    }
                    Button(
                        onClick = { shareOnWhatsApp(context, highCount, medCount, totalSaving) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("WhatsApp", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            items(insights.sortedWith(
                compareBy { when (it.urgency) { "high" -> 0; "medium" -> 1; else -> 2 } }
            )) { insight ->
                InsightCard(insight)
            }
        }
    }
}

@Composable
private fun StatChip(text: String, bg: Color) {
    Surface(color = bg.copy(0.2f), shape = RoundedCornerShape(8.dp)) {
        Text(text, Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun InsightCard(insight: GuardianInsight) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(Modifier.size(36.dp), RoundedCornerShape(8.dp),
                    color = insight.color.copy(0.12f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(insight.icon, null, Modifier.size(20.dp), tint = insight.color)
                    }
                }
                Column(Modifier.weight(1f)) {
                    Text(insight.title, style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold)
                }
                Surface(
                    color = when (insight.urgency) {
                        "high"   -> MaterialTheme.colorScheme.errorContainer
                        "medium" -> MaterialTheme.colorScheme.tertiaryContainer
                        else     -> MaterialTheme.colorScheme.secondaryContainer
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(insight.urgency, Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when (insight.urgency) {
                            "high" -> MaterialTheme.colorScheme.error
                            else   -> MaterialTheme.colorScheme.onSecondaryContainer
                        })
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(insight.detail, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(0.75f))
            Spacer(Modifier.height(6.dp))
            Surface(color = MaterialTheme.colorScheme.primaryContainer.copy(0.5f),
                shape = RoundedCornerShape(6.dp)) {
                Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowForward, null,
                        Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                    Text(insight.action, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

private fun sendGuardianNotification(ctx: Context, high: Int, med: Int, saving: String) {
    val nm = ctx.getSystemService(NotificationManager::class.java) ?: return
    val n = NotificationCompat.Builder(ctx, NidhiApplication.CHANNEL_ALERTS)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("NIDHI Guardian — Weekly Report")
        .setContentText("$high urgent, $med deadlines, $saving in savings opportunities")
        .setStyle(NotificationCompat.BigTextStyle().bigText(
            "Your weekly family status:\n" +
            "• $high urgent actions needed\n" +
            "• $med approaching deadlines\n" +
            "• $saving in identified savings\n\n" +
            "Open NIDHI to view full report."
        ))
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()
    nm.notify(8888, n)
}

private fun shareOnWhatsApp(ctx: Context, high: Int, med: Int, saving: String) {
    val msg = """
*NIDHI Guardian — Weekly Report*

Family Status Summary:

*$high URGENT actions needed*
*$med deadlines approaching*
*$saving in savings opportunities*

Details:
- Passport expiry in 8 months
- LIC nominee not updated
- PMAY eligibility detected
- Scholarship opportunity for daughter

_Generated by NIDHI — Your Family's Financial Guardian_
    """.trimIndent()
    WhatsAppNotifier.send(ctx, msg)
}
