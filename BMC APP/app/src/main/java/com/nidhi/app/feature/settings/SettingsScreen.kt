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

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Settings") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile section
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
                    }
                }
            }

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

            SettingsSection(title = "Appearance") {
                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    checked = uiState.isDarkTheme,
                    onCheckedChange = { viewModel.setDarkTheme(it) }
                )
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                )
            }

            SettingsSection(title = "Security") {
                SettingsSwitchItem(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric Lock",
                    subtitle = "Use fingerprint or face to unlock",
                    checked = uiState.biometricEnabled,
                    onCheckedChange = { viewModel.setBiometricEnabled(it) }
                )
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Change PIN",
                    onClick = { /* Navigate to PIN screen */ }
                )
            }

            SettingsSection(title = "Data") {
                SettingsSwitchItem(
                    icon = Icons.Default.PlayArrow,
                    title = "Demo Mode",
                    subtitle = if (uiState.isDemoMode) "Using sample data" else "Use real data",
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

            SettingsSection(title = "Account") {
                SettingsItem(
                    icon = Icons.Default.Logout,
                    title = "Sign Out",
                    onClick = { showSignOutDialog = true },
                    isDestructive = true
                )
            }

            // Version
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("NIDHI v1.0.0 – Your Family's Financial Guardian",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

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
            Icon(icon, null,
                tint = if (isDestructive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary)
        },
        trailingContent = {
            Icon(Icons.Default.ChevronRight, null,
                tint = MaterialTheme.colorScheme.outline)
        },
        modifier = Modifier.fillMaxWidth()
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
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        },
        modifier = Modifier.fillMaxWidth()
    )
}
