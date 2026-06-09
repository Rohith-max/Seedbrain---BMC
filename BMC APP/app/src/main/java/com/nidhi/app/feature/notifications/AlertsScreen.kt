package com.nidhi.app.feature.notifications

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nidhi.app.domain.model.Alert
import com.nidhi.app.domain.model.AlertType
import com.nidhi.app.ui.components.EmptyState
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    onBack: () -> Unit,
    viewModel: AlertsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alerts") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.semantics { contentDescription = "Navigate back" }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (uiState.unreadCount > 0) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Text("Mark all read")
                        }
                    }
                }
            )
        }
    ) { padding ->
        // ── Empty state (Req 16.4) ──────────────────────────────────────────────
        if (uiState.alerts.isEmpty()) {
            EmptyState(
                icon = Icons.Default.NotificationsNone,
                message = "You're all caught up — no alerts right now",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.alerts, key = { it.id }) { alert ->
                    // Slide-in animation for new alert items (Req 17.4)
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = tween(250)
                        ) + fadeIn(tween(250))
                    ) {
                        AlertListItem(
                            alert = alert,
                            onRead = { viewModel.markAsRead(alert.id) },
                            onDelete = { viewModel.deleteAlert(alert) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertListItem(
    alert: Alert,
    onRead: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }
    val iconTint = when (alert.type) {
        AlertType.EXPIRY   -> MaterialTheme.colorScheme.error
        AlertType.DEADLINE -> MaterialTheme.colorScheme.tertiary
        AlertType.BENEFIT  -> MaterialTheme.colorScheme.primary
        AlertType.GENERAL  -> MaterialTheme.colorScheme.secondary
    }

    Card(
        onClick = onRead,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (!alert.isRead)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                when (alert.type) {
                    AlertType.EXPIRY   -> Icons.Default.Warning
                    AlertType.DEADLINE -> Icons.Default.Schedule
                    AlertType.BENEFIT  -> Icons.Default.LocalOffer
                    AlertType.GENERAL  -> Icons.Default.Notifications
                },
                contentDescription = null,
                tint = iconTint
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        alert.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (!alert.isRead) FontWeight.SemiBold else FontWeight.Normal
                    )
                    if (!alert.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxSize()
                            ) {}
                        }
                    }
                }
                Text(
                    alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2
                )
                Text(
                    dateFormat.format(Date(alert.triggerTime)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .semantics { contentDescription = "Delete alert ${alert.title}" }
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
