package com.nidhi.app.feature.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.nidhi.app.domain.model.Conversation
import com.nidhi.app.ui.components.EmptyState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatListScreen(
    onConversationClick: (String) -> Unit,
    onNewChat: () -> Unit,
    viewModel: AiViewModel = koinViewModel()
) {
    val uiState by viewModel.chatListState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("NIDHI AI Assistant") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewChat,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("New Chat") }
            )
        }
    ) { padding ->
        if (uiState.conversations.isEmpty()) {
            // Empty state (Req 16.3)
            EmptyState(
                icon = Icons.Default.Chat,
                message = "Start a conversation with NIDHI AI",
                ctaLabel = "New Conversation",
                onCtaClick = onNewChat,
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.conversations, key = { it.id }) { conv ->
                    ConversationCard(
                        conversation = conv,
                        onClick = { onConversationClick(conv.id) },
                        onDelete = { viewModel.deleteConversation(conv) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun ConversationCard(
    conversation: Conversation,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM", Locale.getDefault()) }
    val lastMessage = conversation.messages.lastOrNull()

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Chat, null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(conversation.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1)
                if (lastMessage != null) {
                    Text(
                        lastMessage.content.take(60),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(dateFormat.format(Date(conversation.updatedAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.DeleteOutline, "Delete",
                        modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
