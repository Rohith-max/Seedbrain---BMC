package com.nidhi.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nidhi.app.feature.auth.triggerBiometric
import com.nidhi.app.ui.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

// ── Theme colour swatches ─────────────────────────────────────────────────────
private val ThemeSwatches = mapOf(
    "teal"     to Color(0xFF006D77),
    "saffron"  to Color(0xFFBF5700),
    "forest"   to Color(0xFF2D6A4F),
    "midnight" to Color(0xFF1565C0),
    "coral"    to Color(0xFFB54A00)
)

// ── Language options ──────────────────────────────────────────────────────────
private val Languages = listOf(
    "en" to "English",
    "hi" to "हिन्दी",
    "ta" to "தமிழ்",
    "te" to "తెలుగు",
    "bn" to "বাংলা",
    "mr" to "मराठी",
    "gu" to "ગુજરાતી",
    "kn" to "ಕನ್ನಡ",
    "ml" to "മലയാളം",
    "pa" to "ਪੰਜਾਬੀ"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToFamilyMembers: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Dialog states
    var showSignOut by remember { mutableStateOf(false) }
    var showBioResult by remember { mutableStateOf("") }
    var showWhatsappDialog by remember { mutableStateOf(false) }
    var whatsappInput by remember { mutableStateOf(state.whatsappPhone) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showFontSheet by remember { mutableStateOf(false) }

    // ── Dialogs ───────────────────────────────────────────────────────────────
    if (showSignOut) {
        AlertDialog(
            onDismissRequest = { showSignOut = false },
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Sign Out?") },
            text = { Text("Your local data is safe. You'll need to sign in again to access cloud features.") },
            confirmButton = {
                TextButton(onClick = { showSignOut = false; viewModel.signOut(); onSignOut() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("Sign Out")
                }
            },
            dismissButton = { TextButton(onClick = { showSignOut = false }) { Text("Cancel") } }
        )
    }

    if (showBioResult.isNotBlank()) {
        AlertDialog(
            onDismissRequest = { showBioResult = "" },
            title = { Text("Biometric Test") },
            text = { Text(showBioResult) },
            confirmButton = { TextButton(onClick = { showBioResult = "" }) { Text("OK") } }
        )
    }

    if (showWhatsappDialog) {
        AlertDialog(
            onDismissRequest = { showWhatsappDialog = false },
            title = { Text("WhatsApp Number") },
            text = {
                OutlinedTextField(
                    value = whatsappInput,
                    onValueChange = { whatsappInput = it },
                    label = { Text("Phone (e.g. +919876543210)") },
                    leadingIcon = { Icon(Icons.Default.Phone, null) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setWhatsappPhone(whatsappInput)
                    showWhatsappDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showWhatsappDialog = false }) { Text("Cancel") } }
        )
    }

    if (showLanguageSheet) {
        AlertDialog(
            onDismissRequest = { showLanguageSheet = false },
            title = { Text("Select Language") },
            text = {
                Column {
                    Languages.forEach { (code, name) ->
                        ListItem(
                            headlineContent = { Text(name) },
                            trailingContent = {
                                if (state.language == code)
                                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                            },
                            modifier = Modifier.clickable {
                                viewModel.setLanguage(code)
                                showLanguageSheet = false
                            }
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showLanguageSheet = false }) { Text("Close") } }
        )
    }

    if (showFontSheet) {
        AlertDialog(
            onDismissRequest = { showFontSheet = false },
            title = { Text("Font Size") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        "small" to "Small – Compact",
                        "medium" to "Medium – Default",
                        "large" to "Large – Accessible"
                    ).forEach { (key, label) ->
                        ListItem(
                            headlineContent = { Text(label) },
                            trailingContent = {
                                if (state.fontSize == key)
                                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                            },
                            modifier = Modifier.clickable {
                                viewModel.setFontSize(key)
                                showFontSheet = false
                            }
                        )
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFontSheet = false }) { Text("Close") } }
        )
    }

    // ── Screen ────────────────────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Profile card ──────────────────────────────────────────────────
            ProfileCard(
                name = state.userName.ifBlank { "Guest" },
                email = state.userEmail.ifBlank { "Demo Mode" }
            )

            // ── Family ────────────────────────────────────────────────────────
            SettingsGroup("Family") {
                SettingsRow(Icons.Default.Group, "Family Members",
                    "Manage your family profile", onNavigateToFamilyMembers)
                HorizontalDivider(Modifier.padding(start = 56.dp))
                SettingsRow(Icons.Default.Emergency, "Emergency",
                    "SOS contacts and quick dial", onNavigateToEmergency)
            }

            // ── Appearance ────────────────────────────────────────────────────
            SettingsGroup("Appearance") {
                SettingsToggle(Icons.Default.DarkMode, "Dark Mode",
                    "Switch between light and dark", state.isDarkTheme, viewModel::setDarkTheme)
                HorizontalDivider(Modifier.padding(start = 56.dp))

                // Theme picker
                Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Palette, null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        Text("App Theme", style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AppTheme.entries.forEach { theme ->
                            val selected = state.appTheme == theme.key
                            val color = ThemeSwatches[theme.key] ?: Color.Gray
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setAppTheme(theme.key) }
                            ) {
                                Box(
                                    Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .then(
                                            if (selected) Modifier.border(3.dp,
                                                MaterialTheme.colorScheme.onSurface, CircleShape)
                                            else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selected)
                                        Icon(Icons.Default.Check, null,
                                            Modifier.size(20.dp), tint = Color.White)
                                }
                        Text(theme.shortLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
                            }
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(start = 56.dp))

                // Language
                SettingsRow(
                    icon = Icons.Default.Language,
                    title = "Language",
                    subtitle = Languages.firstOrNull { it.first == state.language }?.second ?: "English",
                    onClick = { showLanguageSheet = true }
                )
                HorizontalDivider(Modifier.padding(start = 56.dp))

                // Font size
                SettingsRow(
                    icon = Icons.Default.TextFields,
                    title = "Font Size",
                    subtitle = state.fontSize.replaceFirstChar { it.uppercase() },
                    onClick = { showFontSheet = true }
                )
            }

            // ── Notifications ─────────────────────────────────────────────────
            SettingsGroup("Notifications") {
                SettingsToggle(Icons.Default.Notifications, "Enable Notifications",
                    "Document expiry and benefit alerts", state.notificationsEnabled,
                    viewModel::setNotificationsEnabled)
                HorizontalDivider(Modifier.padding(start = 56.dp))
                SettingsRow(Icons.Default.NotificationAdd, "Send Test Notification",
                    "Verify notifications are working",
                    { viewModel.sendTestNotification(context) })
                HorizontalDivider(Modifier.padding(start = 56.dp))
                SettingsRow(Icons.Default.PhoneAndroid, "WhatsApp Alerts",
                    state.whatsappPhone.ifBlank { "Tap to set your number" },
                    { showWhatsappDialog = true })
            }

            // ── Security ──────────────────────────────────────────────────────
            SettingsGroup("Security") {
                SettingsToggle(Icons.Default.Fingerprint, "Biometric Lock",
                    "Fingerprint / face unlock on app open",
                    state.biometricEnabled, viewModel::setBiometricEnabled)
                if (state.biometricEnabled) {
                    HorizontalDivider(Modifier.padding(start = 56.dp))
                SettingsRow(
                    icon = Icons.Default.TouchApp,
                    title = "Test Biometric",
                    subtitle = "Verify fingerprint works now",
                    onClick = {
                        triggerBiometric(
                            context = context,
                            onSuccess = { showBioResult = "✅ Biometric verified successfully!" },
                            onError = { msg -> showBioResult = "❌ $msg" }
                        )
                    }
                )
                }
            }

            // ── Widget ────────────────────────────────────────────────────────
            SettingsGroup("Home Screen Widget") {
                SettingsToggle(Icons.Default.Widgets, "Show Widget",
                    "Quick-access NIDHI widget on home screen",
                    state.widgetEnabled, viewModel::setWidgetEnabled)
                HorizontalDivider(Modifier.padding(start = 56.dp))
                SettingsRow(
                    icon = Icons.Default.Refresh,
                    title = "Refresh Widget",
                    subtitle = "Force-update widget on home screen",
                    onClick = { viewModel.refreshWidget(context) }
                )

                // Widget preview
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF006D77), Color(0xFF004E57))
                            )
                        )
                        .clickable { viewModel.refreshWidget(context) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NIDHI", fontWeight = FontWeight.Bold,
                            color = Color.White, fontSize = 22.sp)
                        Text("Your Family's Guardian",
                            color = Color.White.copy(0.8f), fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        Surface(color = Color.White.copy(0.2f),
                            shape = RoundedCornerShape(20.dp)) {
                            Text("Tap to Open →",
                                Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                                color = Color.White.copy(0.9f), fontSize = 12.sp)
                        }
                    }
                }
                Text(
                    "Long-press your home screen → Widgets → NIDHI to add",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                )
            }

            // ── Data ─────────────────────────────────────────────────────────
            SettingsGroup("Data") {
                SettingsToggle(Icons.Default.Science, "Demo Mode",
                    if (state.isDemoMode) "Showing sample data" else "Using your real data",
                    state.isDemoMode, viewModel::setDemoMode)
                HorizontalDivider(Modifier.padding(start = 56.dp))
                val syncText = if (state.lastSyncTime > 0L) {
                    "Last sync: ${SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(state.lastSyncTime))}"
                } else "Not synced yet"
                SettingsRow(
                    icon = Icons.Default.Backup,
                    title = "Backup to Cloud",
                    subtitle = syncText,
                    onClick = { viewModel.backupToCloud() }
                )
            }

            // ── Account ───────────────────────────────────────────────────────
            SettingsGroup("Account") {
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Sign Out",
                    subtitle = null,
                    onClick = { showSignOut = true },
                    isDestructive = true
                )
            }

            // Footer
            Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                Text("NIDHI v1.0.0 · Your Family's Financial Guardian",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

// ── Reusable composables ──────────────────────────────────────────────────────

@Composable
private fun ProfileCard(name: String, email: String) {
    Surface(
        Modifier.fillMaxWidth().padding(16.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(Modifier.size(56.dp), CircleShape, color = MaterialTheme.colorScheme.primary) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Column {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(email, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f))
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        )
        Card(Modifier.fillMaxWidth()) { Column { content() } }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val color = if (isDestructive) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.primary
    ListItem(
        headlineContent = { Text(title, color = color) },
        supportingContent = subtitle?.let { { Text(it) } },
        leadingContent = { Icon(icon, null, tint = color) },
        trailingContent = { Icon(Icons.Default.ChevronRight, null,
            tint = MaterialTheme.colorScheme.outline) },
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    )
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
        modifier = Modifier.fillMaxWidth()
    )
}
