package com.nidhi.app.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToFamilyMembers: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showSensitiveDocTypePicker by remember { mutableStateOf(false) }

    // ── Sign-out dialog ────────────────────────────────────────────────────────
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out?") },
            text = { Text("Your local data will remain. You'll need to sign in again.") },
            confirmButton = {
                TextButton(onClick = {
                    showSignOutDialog = false
                    viewModel.signOut()
                    onSignOut()
                }) { Text("Sign Out") }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) { Text("Cancel") }
            }
        )
    }

    // ── Biometric error snackbar ───────────────────────────────────────────────
    uiState.biometricError?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.clearBiometricError() },
            title = { Text("Biometric Unavailable") },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearBiometricError() }) { Text("OK") }
            }
        )
    }

    // ── Sensitive doc type picker ─────────────────────────────────────────────
    if (showSensitiveDocTypePicker && uiState.availableDocTypes.isNotEmpty()) {
        val selected = remember(uiState.sensitiveDocTypes) {
            mutableStateOf(uiState.sensitiveDocTypes.toMutableSet())
        }
        AlertDialog(
            onDismissRequest = { showSensitiveDocTypePicker = false },
            title = { Text("Select Sensitive Document Types") },
            text = {
                Column {
                    uiState.availableDocTypes.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = type in selected.value,
                                onCheckedChange = { checked ->
                                    val set = selected.value.toMutableSet()
                                    if (checked) set += type else set -= type
                                    selected.value = set
                                }
                            )
                            Text(type, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setSensitiveDocTypes(selected.value)
                    showSensitiveDocTypePicker = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showSensitiveDocTypePicker = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Profile card ──────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                uiState.userName.take(1).uppercase().ifBlank { "U" },
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Column {
                        Text(uiState.userName.ifBlank { "Guest" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold)
                        Text(uiState.userEmail.ifBlank { "Demo Mode" },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        if (uiState.lastSyncTime > 0L) {
                            Text(
                                "Last synced: ${formatRelativeTime(uiState.lastSyncTime)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                            )
                        } else {
                            Text("Never synced",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            // ── Family ────────────────────────────────────────────────────────
            SettingsSection(title = "Family") {
                SettingsItem(
                    icon = Icons.Default.Group,
                    title = "Family Members",
                    subtitle = "Manage your family profile",
                    onClick = onNavigateToFamilyMembers
                )
                SettingsItem(
                    icon = Icons.Default.Emergency,
                    title = "Emergency",
                    subtitle = "SOS and emergency contacts",
                    onClick = onNavigateToEmergency
                )
            }

            // ── Appearance ────────────────────────────────────────────────────
            SettingsSection(title = "Appearance") {
                // Three-way theme selector (Req 19.2)
                SettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = uiState.themeMode,
                    onClick = {
                        val next = when (uiState.themeMode) {
                            "System Default" -> "Light"
                            "Light" -> "Dark"
                            else -> "System Default"
                        }
                        viewModel.setThemeMode(next)
                    }
                )
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                )
            }

            // ── Security ──────────────────────────────────────────────────────
            SettingsSection(title = "Security") {
                // App Lock — Biometric (Req 1.1, 1.7)
                SettingsSwitchItem(
                    icon = Icons.Default.Fingerprint,
                    title = "App Lock – Biometric",
                    subtitle = if (uiState.biometricEnabled) uiState.biometricSubtitle else null,
                    checked = uiState.biometricEnabled,
                    onCheckedChange = { viewModel.confirmBiometricEnabled(it) }
                )
                // Sensitive document gate (Req 2.1, 2.2)
                SettingsSwitchItem(
                    icon = Icons.Default.Lock,
                    title = "Require Biometric to View Sensitive Documents",
                    subtitle = if (uiState.sensitiveDocBiometricEnabled)
                        "${uiState.sensitiveDocTypes.size} type(s) protected" else null,
                    checked = uiState.sensitiveDocBiometricEnabled,
                    onCheckedChange = { viewModel.setSensitiveDocBiometricEnabled(it) }
                )
                if (uiState.sensitiveDocBiometricEnabled && uiState.availableDocTypes.isNotEmpty()) {
                    SettingsItem(
                        icon = Icons.Default.Shield,
                        title = "Protected Document Types",
                        subtitle = uiState.sensitiveDocTypes
                            .takeIf { it.isNotEmpty() }
                            ?.joinToString(", ")
                            ?: "None selected",
                        onClick = { showSensitiveDocTypePicker = true }
                    )
                }
            }

            // ── Data ──────────────────────────────────────────────────────────
            SettingsSection(title = "Data") {
                SettingsSwitchItem(
                    icon = Icons.Default.PlayArrow,
                    title = "Demo Mode",
                    subtitle = if (uiState.isDemoMode) "Using sample data" else "Using real data",
                    checked = uiState.isDemoMode,
                    onCheckedChange = { viewModel.setDemoMode(it) }
                )
                SettingsItem(
                    icon = Icons.Default.Backup,
                    title = "Backup to Cloud",
                    subtitle = "Sync your data to Firebase",
                    onClick = { viewModel.backupToCloud() }
                )
            }

            // ── Account ───────────────────────────────────────────────────────
            SettingsSection(title = "Account") {
                SettingsItem(
                    icon = Icons.Default.Logout,
                    title = "Sign Out",
                    onClick = { showSignOutDialog = true },
                    isDestructive = true
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("NIDHI v1.1.0 – Your Family's Financial Guardian",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

// ── Sub-components ─────────────────────────────────────────────────────────────

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column { content() }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    ListItem(
        headlineContent = {
            Text(title,
                color = if (isDestructive) MaterialTheme.colorScheme.error else LocalContentColor.current)
        },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = {
            Icon(icon, contentDescription = title,
                tint = if (isDestructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary)
        },
        trailingContent = {
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = MaterialTheme.colorScheme.outline)
        },
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = 48.dp)
    )
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}

// ── Helpers ────────────────────────────────────────────────────────────────────

private fun formatRelativeTime(epochMs: Long): String {
    val diffMs = System.currentTimeMillis() - epochMs
    return when {
        diffMs < 60_000L -> "just now"
        diffMs < 3_600_000L -> "${TimeUnit.MILLISECONDS.toMinutes(diffMs)} min ago"
        diffMs < 86_400_000L -> "${TimeUnit.MILLISECONDS.toHours(diffMs)} hours ago"
        else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(epochMs))
    }
}
