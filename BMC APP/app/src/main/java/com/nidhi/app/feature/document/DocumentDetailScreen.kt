package com.nidhi.app.feature.document

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import coil.compose.AsyncImage
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.model.Document
import com.nidhi.app.feature.auth.BiometricHelper
import com.nidhi.app.feature.auth.SessionBiometricCache
import com.nidhi.app.feature.auth.triggerBiometric
import com.nidhi.app.ui.components.ExpiryBadge
import com.nidhi.app.ui.components.GlassCard
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Document detail screen with biometric gate for sensitive document types (Req 2.3–2.7).
 *
 * Flow:
 *  1. Check if sensitive doc gate is enabled + this doc's type is in the sensitive list.
 *  2. Check session cache — if already authenticated this session, show immediately.
 *  3. Otherwise, show a loading placeholder and trigger BiometricPrompt.
 *  4. On success → grant session cache + show content.
 *  5. On failure/cancel → navigate back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailScreen(
    documentId: String,
    onBack: () -> Unit,
    viewModel: DocumentViewModel = koinViewModel()
) {
    val document by viewModel.selectedDocument.collectAsState()
    val context = LocalContext.current

    // Biometric dependencies from Koin
    val userPreferences: UserPreferences = koinInject()
    val biometricHelper: BiometricHelper = koinInject()
    val sessionCache: SessionBiometricCache = koinInject()

    // Collect sensitive-doc preferences
    val sensitiveEnabled by userPreferences.isSensitiveDocBiometricEnabled.collectAsState(initial = false)
    val sensitiveTypes   by userPreferences.sensitiveDocTypes.collectAsState(initial = emptySet())

    // Auth state for this screen instance
    var biometricPassed by remember { mutableStateOf(false) }
    var biometricChecked by remember { mutableStateOf(false) }

    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId)
    }

    // Run biometric gate once the document is loaded and we know its type
    LaunchedEffect(document, sensitiveEnabled, sensitiveTypes) {
        val doc = document ?: return@LaunchedEffect
        if (biometricChecked) return@LaunchedEffect   // already decided

        val isSensitive = sensitiveEnabled && doc.type in sensitiveTypes
        if (!isSensitive) {
            // Not sensitive — allow through
            biometricPassed = true
            biometricChecked = true
            return@LaunchedEffect
        }
        if (sessionCache.isAuthenticated(doc.type)) {
            // Already authenticated this session — allow through
            biometricPassed = true
            biometricChecked = true
            return@LaunchedEffect
        }
        // Trigger biometric prompt
        biometricChecked = true
        triggerBiometric(
            context = context,
            onSuccess = {
                sessionCache.grant(doc.type)
                biometricPassed = true
            },
            onError = {
                onBack()   // navigate back on failure/cancel (Req 2.5)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(document?.title ?: "Document") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.semantics { contentDescription = "Navigate back" }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    document?.let { doc ->
                        IconButton(
                            onClick = { shareDocument(context, doc) },
                            modifier = Modifier.semantics { contentDescription = "Share document" }
                        ) {
                            Icon(Icons.Default.Share, "Share")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            // Still loading document or biometric not decided yet
            document == null || (!biometricPassed && !biometricChecked) -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Biometric decided but not passed (we already called onBack above, this is transient)
            !biometricPassed -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Content granted
            else -> {
                val doc = document!!
                DocumentContent(
                    doc = doc,
                    modifier = Modifier.padding(padding),
                    context = context
                )
            }
        }
    }
}

@Composable
private fun DocumentContent(
    doc: Document,
    modifier: Modifier = Modifier,
    context: Context
) {
    // Build accessibility description for the whole content (Req 20.4)
    val expiryDesc = doc.expiryDate?.let {
        SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(it))
    } ?: "no expiry date"
    val contentDesc = "${doc.title.ifEmpty { "Untitled document" }}, ${doc.type}, $expiryDesc"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .semantics { contentDescription = contentDesc },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Document image
        if (doc.filePath.isNotBlank() && File(doc.filePath).exists()) {
            AsyncImage(
                model = File(doc.filePath),
                contentDescription = "Document image for ${doc.title}",
                modifier = Modifier.fillMaxWidth().height(240.dp)
            )
        } else if (doc.filePath.isBlank()) {
            // Cloud-only metadata document indicator (Req 5.3)
            Surface(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.CloudOff,
                        contentDescription = "Image not available locally",
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        "Image not available locally. Re-scan to restore.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        // Meta info card
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaRow("Type", doc.type)
                MetaRow("Title", doc.title)
                doc.expiryDate?.let { expiry ->
                    MetaRow(
                        label = "Expiry",
                        value = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expiry))
                    )
                    Spacer(Modifier.height(4.dp))
                    // ExpiryBadge with colour + text label (Req 13.5, 20.7)
                    ExpiryBadge(expiryDate = expiry)
                }
                MetaRow(
                    "Added",
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(doc.createdAt))
                )
            }
        }

        // AI Summary card (Req 13.5)
        doc.summary?.let { summary ->
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            Icons.Default.AutoAwesome, null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "AI Summary",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(summary.headline, style = MaterialTheme.typography.bodyMedium)

                    // Key points as bulleted list (Req 13.5)
                    if (summary.keyPoints.isNotEmpty()) {
                        HorizontalDivider()
                        summary.keyPoints.forEach { point ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("•", color = MaterialTheme.colorScheme.primary)
                                Text(point, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    // Extracted IDs
                    if (summary.extractedIds.isNotEmpty()) {
                        HorizontalDivider()
                        summary.extractedIds.forEach { (key, value) ->
                            MetaRow(key, value)
                        }
                    }
                }
            }
        }

        // OCR text (expandable)
        if (!doc.ocrText.isNullOrBlank()) {
            var expanded by remember { mutableStateOf(false) }
            GlassCard {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Extracted Text",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(onClick = { expanded = !expanded }) {
                            Text(if (expanded) "Collapse" else "Expand")
                        }
                    }
                    if (expanded) {
                        Text(
                            doc.ocrText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

private fun shareDocument(context: Context, document: Document) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, document.title)
        putExtra(Intent.EXTRA_TEXT, buildString {
            appendLine("📄 ${document.title}")
            appendLine("Type: ${document.type}")
            document.summary?.headline?.let { appendLine("Summary: $it") }
            document.expiryDate?.let {
                appendLine("Expires: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))}")
            }
            appendLine("\nShared from NIDHI – Your Family's Financial Guardian")
        })
    }
    val file = File(document.filePath)
    if (file.exists()) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.type = "image/*"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share via…"))
}
