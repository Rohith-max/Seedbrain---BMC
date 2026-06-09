package com.nidhi.app.feature.document

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nidhi.app.domain.model.Document
import com.nidhi.app.ui.components.CardSkeleton
import com.nidhi.app.ui.components.EmptyState
import com.nidhi.app.ui.components.ErrorBanner
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    onDocumentClick: (String) -> Unit,
    onAddDocument: () -> Unit,
    viewModel: DocumentViewModel = koinViewModel()
) {
    val uiState by viewModel.documentsUiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf<Document?>(null) }

    // Delete dialog
    showDeleteConfirm?.let { doc ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Delete Document?") },
            text = { Text("\"${doc.title}\" will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteDocument(doc)
                    showDeleteConfirm = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Documents") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddDocument,
                icon = {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = "Scan new document"
                    )
                },
                text = { Text("Scan") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search documents…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.semantics { contentDescription = "Clear search" }
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            ErrorBanner(message = uiState.error ?: "", visible = uiState.error != null)

            // ── Loading skeleton (Req 15.1, 15.2) ──────────────────────────────
            if (uiState.isLoading) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // At least 4 card-shaped skeleton items ~80dp height (Req 15.2)
                    items(4) {
                        CardSkeleton(
                            Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        )
                    }
                }
                return@Column
            }

            val filtered = uiState.documents.filter {
                searchQuery.isBlank() ||
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.type.contains(searchQuery, ignoreCase = true)
            }

            when {
                // ── Genuinely empty (no documents at all) (Req 16.1) ──────────
                uiState.documents.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.FolderOpen,
                        message = "No documents yet",
                        ctaLabel = "Scan your first document",
                        onCtaClick = onAddDocument
                    )
                }

                // ── Filter returned no results (Req 16.2) ──────────────────────
                filtered.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.SearchOff,
                        message = "No results for \"$searchQuery\""
                    )
                }

                // ── Normal list ────────────────────────────────────────────────
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(
                            items = filtered,
                            key = { it.id }
                        ) { doc ->
                            DocumentListItem(
                                document = doc,
                                onClick = { onDocumentClick(doc.id) },
                                onDelete = { showDeleteConfirm = doc }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentListItem(
    document: Document,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val expiryLabel = document.expiryDate?.let { dateFormat.format(Date(it)) }

    // Accessibility: merged description (Req 20.4)
    val expiryDesc = document.expiryDate
        ?.let { dateFormat.format(Date(it)) }
        ?: "no expiry date"
    val a11yDesc = "${document.title.ifEmpty { "Untitled document" }}, ${document.type}, $expiryDesc"

    // Cloud-only indicator: filePath is blank = metadata-only (Req 5.3)
    val isMetadataOnly = document.filePath.isBlank()

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .semantics(mergeDescendants = true) {
                contentDescription = a11yDesc
            }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail or type icon
            Box(modifier = Modifier.size(56.dp)) {
                if (document.thumbnailPath != null && !isMetadataOnly) {
                    AsyncImage(
                        model = document.thumbnailPath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                document.type.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                // Cloud-slash overlay for metadata-only docs (Req 5.3)
                if (isMetadataOnly) {
                    Icon(
                        Icons.Default.CloudOff,
                        contentDescription = "Image not available locally",
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    document.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    document.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                if (expiryLabel != null) {
                    Text(
                        "Expires: $expiryLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .semantics { contentDescription = "Delete document ${document.title}" }
            ) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}
