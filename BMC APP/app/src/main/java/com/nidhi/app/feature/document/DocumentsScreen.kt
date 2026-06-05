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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import coil.compose.AsyncImage
import com.nidhi.app.domain.model.Document
import com.nidhi.app.ui.components.CardSkeleton
import com.nidhi.app.ui.components.ErrorBanner
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
                icon = { Icon(Icons.Default.AddAPhoto, null) },
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
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            ErrorBanner(message = uiState.error ?: "", visible = uiState.error != null)

            if (uiState.isLoading) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(4) { CardSkeleton(Modifier.fillMaxWidth().height(80.dp)) }
                }
            } else {
                val filtered = uiState.documents.filter {
                    searchQuery.isBlank() ||
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.type.contains(searchQuery, ignoreCase = true)
                }

                if (filtered.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.FolderOpen,
                                null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                if (searchQuery.isBlank()) "No documents yet.\nTap Scan to add your first document."
                                else "No results for \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
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

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().animateContentSize()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail or type icon
            if (document.thumbnailPath != null) {
                AsyncImage(
                    model = document.thumbnailPath,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            } else {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(document.type.take(2).uppercase(), fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(document.title, style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold)
                Text(document.type, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                if (expiryLabel != null) {
                    Text(
                        "Expires: $expiryLabel",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, "Delete",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        }
    }
}
