package com.nidhi.app.feature.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nidhi.app.data.local.prefs.UserPreferences
import com.nidhi.app.domain.model.*
import com.nidhi.app.domain.repository.AiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatListUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = true
)

data class ChatDetailUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val error: String? = null,
    val conversationTitle: String = "NIDHI AI"
)

class AiViewModel(
    private val aiRepository: AiRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _chatListState   = MutableStateFlow(ChatListUiState())
    val chatListState: StateFlow<ChatListUiState> = _chatListState.asStateFlow()

    private val _chatDetailState = MutableStateFlow(ChatDetailUiState())
    val chatDetailState: StateFlow<ChatDetailUiState> = _chatDetailState.asStateFlow()

    private var currentConversationId = UUID.randomUUID().toString()
    private var currentUserId = "demo_user"

    init {
        viewModelScope.launch {
            currentUserId = userPreferences.currentUserId.first() ?: "demo_user"
            aiRepository.getConversations(currentUserId).collect { convs ->
                _chatListState.update { it.copy(conversations = convs, isLoading = false) }
            }
        }
    }

    fun loadConversation(id: String) {
        if (id == "new") {
            currentConversationId = UUID.randomUUID().toString()
            _chatDetailState.value = ChatDetailUiState()
            return
        }
        currentConversationId = id
        viewModelScope.launch {
            aiRepository.getConversationById(id)?.let { conv ->
                _chatDetailState.update { it.copy(messages = conv.messages, conversationTitle = conv.title) }
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(UUID.randomUUID().toString(), ChatRole.USER, text, System.currentTimeMillis())
        _chatDetailState.update { it.copy(messages = it.messages + userMsg, isTyping = true, error = null) }
        persist(userMsg)

        viewModelScope.launch {
            aiRepository.sendMessage(currentConversationId, text).collect { result ->
                when (result) {
                    is Result.Success -> {
                        // For streaming, multiple emissions share the same message ID.
                        // Replace the existing message with that ID (streaming updates),
                        // or append if it's a brand-new message.
                        _chatDetailState.update { state ->
                            val existing = state.messages.indexOfFirst { it.id == result.data.id }
                            val updatedMessages = if (existing >= 0) {
                                state.messages.toMutableList().also { it[existing] = result.data }
                            } else {
                                state.messages + result.data
                            }
                            state.copy(
                                messages = updatedMessages,
                                isTyping = result.data.isStreaming   // still typing while streaming
                            )
                        }
                        if (!result.data.isStreaming) persist(result.data)
                    }
                    is Result.Error -> _chatDetailState.update { it.copy(isTyping = false, error = result.message) }
                    is Result.Loading -> Unit
                }
            }
        }
    }

    private fun persist(msg: ChatMessage) {
        viewModelScope.launch {
            val msgs = _chatDetailState.value.messages
            val title = msgs.firstOrNull { it.role == ChatRole.USER }?.content?.take(40) ?: "New Chat"
            aiRepository.saveConversation(
                Conversation(
                    id = currentConversationId, userId = currentUserId, title = title,
                    messages = msgs, createdAt = msgs.firstOrNull()?.timestamp ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteConversation(conv: Conversation) { viewModelScope.launch { aiRepository.deleteConversation(conv) } }
    fun clearError() = _chatDetailState.update { it.copy(error = null) }
}
