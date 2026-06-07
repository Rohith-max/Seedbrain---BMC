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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import org.koin.compose.viewmodel.koinViewModel
import coil.compose.AsyncImage
import com.nidhi.app.domain.model.Document
import com.nidhi.app.ui.components.GlassCard
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailScreen(
    documentId: String,
    onBack: () -> Unit,
    viewModel: DocumentViewModel = koinViewModel()
) {
    val document by viewModel.selectedDocument.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(document?.title ?: "Document") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    document?.let { doc ->
                        IconButton(onClick = { shareDocument(context, doc) }) {
                            Icon(Icons.Default.Share, "Share")
                        }
                    }
                }
            )
        }
    ) { padding ->
        document?.let { doc ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image
                if (doc.filePath.isNotBlank()) {
                    AsyncImage(
                        model = File(doc.filePath),
                        contentDescription = "Document image",
                        modifier = Modifier.fillMaxWidth().height(240.dp)
                    )
                }

                // Meta info
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MetaRow("Type", doc.type)
                        MetaRow("Title", doc.title)
                        doc.expiryDate?.let {
                            val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            MetaRow("Expiry", fmt.format(Date(it)),
                                valueColor = MaterialTheme.colorScheme.error)
                        }
                        MetaRow("Added",
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                .format(Date(doc.createdAt)))
                    }
                }

                // AI Summary
                doc.summary?.let { summary ->
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, null,
                                    tint = MaterialTheme.colorScheme.primary)
                                Text("AI Summary",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold)
                            }
                            Text(summary.headline, style = MaterialTheme.typography.bodyMedium)
                            if (summary.keyPoints.isNotEmpty()) {
                                Divider()
                                summary.keyPoints.forEach { point ->
                                    Text("• $point", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            if (summary.extractedIds.isNotEmpty()) {
                                Divider()
                                summary.extractedIds.forEach { (key, value) ->
                                    MetaRow(key, value)
                                }
                            }
                        }
                    }
                }

                // OCR Text
                if (!doc.ocrText.isNullOrBlank()) {
                    var expanded by remember { mutableStateOf(false) }
                    GlassCard {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Extracted Text",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold)
                                TextButton(onClick = { expanded = !expanded }) {
                                    Text(if (expanded) "Collapse" else "Expand")
                                }
                            }
                            if (expanded) {
                                Text(doc.ocrText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
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
        Text(label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = valueColor)
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
                appendLine("Expires: ${java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(it))}")
            }
            appendLine("\nShared from NIDHI – Your Family's Financial Guardian")
        })
    }

    // Also attach image if available
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
