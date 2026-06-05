package com.nidhi.app.feature.notifications

import android.app.NotificationManager
import android.content.Context
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.nidhi.app.domain.model.Alert
import com.nidhi.app.domain.model.AlertType
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
                    IconButton(onClick = onBack) {
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
        if (uiState.alerts.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.NotificationsNone, null,
                        Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(16.dp))
                    Text("No alerts yet", color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.alerts, key = { it.id }) { alert ->
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

@Composable
private fun AlertListItem(
    alert: Alert,
    onRead: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }
    val iconTint = when (alert.type) {
        AlertType.EXPIRY -> MaterialTheme.colorScheme.error
        AlertType.DEADLINE -> MaterialTheme.colorScheme.tertiary
        AlertType.BENEFIT -> MaterialTheme.colorScheme.primary
        AlertType.GENERAL -> MaterialTheme.colorScheme.secondary
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
                    AlertType.EXPIRY -> Icons.Default.Warning
                    AlertType.DEADLINE -> Icons.Default.Schedule
                    AlertType.BENEFIT -> Icons.Default.LocalOffer
                    AlertType.GENERAL -> Icons.Default.Notifications
                },
                null,
                tint = iconTint
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(alert.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (!alert.isRead) FontWeight.SemiBold else FontWeight.Normal)
                    if (!alert.isRead) {
                        Box(
                            modifier = Modifier.size(8.dp)
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
                Text(alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2)
                Text(dateFormat.format(Date(alert.triggerTime)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, "Delete",
                    Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
