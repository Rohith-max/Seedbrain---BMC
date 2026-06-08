package com.nidhi.app.feature.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import com.nidhi.app.domain.model.Alert
import com.nidhi.app.domain.model.Document
import com.nidhi.app.domain.model.FamilyHealthScore
import com.nidhi.app.ui.components.CardSkeleton
import com.nidhi.app.ui.components.GlassCard
import com.nidhi.app.ui.components.SectionHeader
import com.nidhi.app.ui.theme.Gold600
import com.nidhi.app.ui.theme.SuccessGreen
import com.nidhi.app.ui.theme.Teal600
import com.nidhi.app.ui.theme.WarningOrange
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDocuments: () -> Unit,
    onNavigateToBenefits: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.scheduleAlerts() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("NIDHI", style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary)
                        Text("Good ${getGreeting()}, ${uiState.userName.ifBlank { "there" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                },
                actions = {
                    // Alerts bell
                    BadgedBox(
                        badge = {
                            if (uiState.unreadAlertCount > 0) {
                                Badge { Text(uiState.unreadAlertCount.coerceAtMost(99).toString()) }
                            }
                        }
                    ) {
                        IconButton(onClick = onNavigateToAlerts) {
                            Icon(Icons.Default.Notifications, "Alerts")
                        }
                    }
                    // SOS
                    IconButton(onClick = onNavigateToEmergency) {
                        Icon(
                            Icons.Default.Emergency,
                            "Emergency",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(4) { CardSkeleton(Modifier.fillMaxWidth()) }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Demo banner
                if (uiState.isDemoMode) {
                    item {
                        Surface(
                            color = Gold600.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Info, null, tint = Gold600)
                                Text(
                                    "Demo Mode – Sample data",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Health Score card
                uiState.healthScore?.let { score ->
                    item {
                        HealthScoreCard(score = score)
                    }
                }

                // Quick actions
                item {
                    SectionHeader(title = "Quick Actions")
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            QuickActionChip(
                                icon = Icons.Default.Description,
                                label = "Documents",
                                onClick = onNavigateToDocuments
                            )
                        }
                        item {
                            QuickActionChip(
                                icon = Icons.Default.LocalOffer,
                                label = "Benefits",
                                badge = uiState.eligibleBenefitsCount.takeIf { it > 0 }?.toString(),
                                onClick = onNavigateToBenefits
                            )
                        }
                        item {
                            QuickActionChip(
                                icon = Icons.Default.Notifications,
                                label = "Alerts",
                                badge = uiState.unreadAlertCount.takeIf { it > 0 }?.toString(),
                                onClick = onNavigateToAlerts
                            )
                        }
                        item {
                            QuickActionChip(
                                icon = Icons.Default.Emergency,
                                label = "SOS",
                                onClick = onNavigateToEmergency,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Upcoming expiry
                if (uiState.upcomingExpiryDocs.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Expiring Soon",
                            action = {
                                TextButton(onClick = onNavigateToDocuments) { Text("See all") }
                            }
                        )
                    }
                    items(uiState.upcomingExpiryDocs.take(3)) { doc ->
                        ExpiryDocumentCard(doc = doc, onClick = onNavigateToDocuments)
                    }
                }

                // Recent alerts
                if (uiState.unreadAlerts.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Recent Alerts",
                            action = {
                                TextButton(onClick = onNavigateToAlerts) { Text("See all") }
                            }
                        )
                    }
                    items(uiState.unreadAlerts) { alert ->
                        AlertCard(alert = alert)
                    }
                }

                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun HealthScoreCard(score: FamilyHealthScore) {
    val animatedScore by animateIntAsState(
        targetValue = score.score,
        animationSpec = tween(1000),
        label = "score"
    )

    GlassCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Family Health Score",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Based on documents, alerts & benefits",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { score.score / 100f },
                        modifier = Modifier.size(72.dp),
                        strokeWidth = 6.dp,
                        color = when {
                            score.score >= 75 -> Color(SuccessGreen.value)
                            score.score >= 50 -> Gold600
                            else -> WarningOrange
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        "$animatedScore",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (score.insights.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                score.insights.forEach { insight ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Text(insight, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    badge: String? = null,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    BadgedBox(
        badge = {
            if (badge != null) {
                Badge { Text(badge) }
            }
        }
    ) {
        ElevatedCard(
            onClick = onClick,
            modifier = Modifier.width(90.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(28.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }
    }
}

@Composable
private fun ExpiryDocumentCard(doc: Document, onClick: () -> Unit) {
    val daysLeft = doc.expiryDate?.let {
        TimeUnit.MILLISECONDS.toDays(it - System.currentTimeMillis())
    }
    val urgencyColor = when {
        daysLeft == null -> MaterialTheme.colorScheme.outline
        daysLeft <= 7 -> MaterialTheme.colorScheme.error
        daysLeft <= 30 -> WarningOrange
        else -> Color(SuccessGreen.value)
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Description,
                    null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(doc.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text(doc.type, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
            }
            Surface(
                color = urgencyColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (daysLeft != null) "$daysLeft days" else "No expiry",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = urgencyColor
                )
            }
        }
    }
}

@Composable
private fun AlertCard(alert: Alert) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.NotificationImportant,
                null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(alert.title, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
                Text(alert.message, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2)
            }
        }
    }
}

private fun getGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "morning"
        in 12..16 -> "afternoon"
        in 17..20 -> "evening"
        else -> "night"
    }
}
