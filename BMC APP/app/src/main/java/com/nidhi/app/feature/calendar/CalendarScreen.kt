package com.nidhi.app.feature.calendar

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.nidhi.app.domain.model.Document
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    viewModel: CalendarViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val calendarPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar Sync") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (!calendarPermissions.allPermissionsGranted) {
                CalendarPermissionCard(
                    onRequest = { calendarPermissions.launchMultiplePermissionRequest() }
                )
            } else {
                Text(
                    "Upcoming Deadlines",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                if (uiState.expiringDocuments.isEmpty()) {
                    Box(
                        Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No upcoming document deadlines",
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.expiringDocuments, key = { it.id }) { doc ->
                            CalendarEventCard(
                                document = doc,
                                onAddToCalendar = {
                                    doc.expiryDate?.let { expiry ->
                                        addEventToCalendar(
                                            context,
                                            title = "${doc.title} – Expiry Reminder",
                                            description = "Document type: ${doc.type}",
                                            startTime = expiry - 7 * 24 * 3600 * 1000L,
                                            endTime = expiry
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarPermissionCard(onRequest: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.CalendarMonth, null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp))
            Text("Calendar Permission Needed",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold)
            Text("Allow NIDHI to add document renewal reminders to your calendar.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Button(onClick = onRequest, modifier = Modifier.fillMaxWidth()) {
                Text("Grant Calendar Access")
            }
        }
    }
}

@Composable
private fun CalendarEventCard(document: Document, onAddToCalendar: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(document.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium)
                document.expiryDate?.let {
                    Text("Expires: ${dateFormat.format(Date(it))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                }
            }
            IconButton(onClick = onAddToCalendar) {
                Icon(Icons.Default.AddAlert, "Add to Calendar",
                    tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

private fun addEventToCalendar(
    context: Context,
    title: String,
    description: String,
    startTime: Long,
    endTime: Long
) {
    try {
        val values = ContentValues().apply {
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, description)
            put(CalendarContract.Events.DTSTART, startTime)
            put(CalendarContract.Events.DTEND, endTime)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            put(CalendarContract.Events.CALENDAR_ID, 1L)
        }
        context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
