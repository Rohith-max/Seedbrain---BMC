package com.nidhi.app.data.repository

import com.nidhi.app.BuildConfig
import com.nidhi.app.data.local.dao.ConversationDao
import com.nidhi.app.data.local.entity.ConversationEntity
import com.nidhi.app.data.remote.LlmApiService
import com.nidhi.app.data.remote.SimulatedLlmEngine
import com.nidhi.app.data.remote.dto.ChatCompletionRequest
import com.nidhi.app.data.remote.dto.LlmMessage
import com.nidhi.app.domain.model.ChatMessage
import com.nidhi.app.domain.model.ChatRole
import com.nidhi.app.domain.model.Conversation
import com.nidhi.app.domain.model.Result
import com.nidhi.app.domain.repository.AiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.UUID

/**
 * Uses kotlinx.serialization (which matches @Serializable annotations on ChatMessage / ChatRole)
 * instead of Moshi — eliminates the Moshi/kotlinx mismatch crash.
 */
class AiRepositoryImpl(
    private val conversationDao: ConversationDao,
    private val llmApiService: LlmApiService,
    private val simulatedLlm: SimulatedLlmEngine
) : AiRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    override fun getConversations(userId: String): Flow<List<Conversation>> =
        conversationDao.getConversations(userId).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getConversationById(id: String): Conversation? =
        conversationDao.getConversationById(id)?.toDomain()

    override suspend fun sendMessage(
        conversationId: String,
        message: String
    ): Flow<Result<ChatMessage>> = flow {
        emit(Result.Loading)
        try {
            val replyContent = withContext(Dispatchers.IO) {
                if (BuildConfig.USE_SIMULATED_LLM) {
                    simulatedLlm.chat(message)
                } else {
                    try {
                        val conversation = conversationDao.getConversationById(conversationId)
                        val history = conversation?.let {
                            try { json.decodeFromString<List<ChatMessage>>(it.messagesJson) }
                            catch (_: Exception) { emptyList() }
                        } ?: emptyList()

                        val llmMessages = mutableListOf(
                            LlmMessage("system", NIDHI_SYSTEM_PROMPT)
                        )
                        history.takeLast(10).forEach { msg ->
                            llmMessages.add(
                                LlmMessage(
                                    role = when (msg.role) {
                                        ChatRole.USER      -> "user"
                                        ChatRole.ASSISTANT -> "assistant"
                                        ChatRole.SYSTEM    -> "system"
                                    },
                                    content = msg.content
                                )
                            )
                        }
                        llmMessages.add(LlmMessage("user", message))

                        val request = ChatCompletionRequest(
                            model    = BuildConfig.LLM_MODEL,
                            messages = llmMessages,
                            maxTokens = 800,
                            temperature = 0.7
                        )
                        val response = llmApiService.createChatCompletion(request)
                        response.choices.firstOrNull()?.message?.content
                            ?: simulatedLlm.chat(message)
                    } catch (_: Exception) {
                        simulatedLlm.chat(message)
                    }
                }
            }

            emit(
                Result.Success(
                    ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = ChatRole.ASSISTANT,
                        content = replyContent,
                        timestamp = System.currentTimeMillis()
                    )
                )
            )
        } catch (e: Exception) {
            val fallback = try { simulatedLlm.chat(message) }
            catch (_: Exception) { "I'm here to help! Ask me about government schemes, documents, or financial planning." }
            emit(
                Result.Success(
                    ChatMessage(
                        id = UUID.randomUUID().toString(),
                        role = ChatRole.ASSISTANT,
                        content = fallback,
                        timestamp = System.currentTimeMillis()
                    )
                )
            )
        }
    }

    override suspend fun saveConversation(conversation: Conversation) {
        conversationDao.upsertConversation(conversation.toEntity())
    }

    override suspend fun deleteConversation(conversation: Conversation) {
        conversationDao.deleteConversation(conversation.toEntity())
    }

    override suspend fun clearAllConversations(userId: String) {
        conversationDao.clearAll(userId)
    }

    // ── Mappers (using kotlinx.serialization throughout) ──────────────────────

    private fun ConversationEntity.toDomain(): Conversation {
        val messages = try {
            json.decodeFromString<List<ChatMessage>>(messagesJson)
        } catch (_: Exception) { emptyList() }
        return Conversation(id, userId, title, messages, createdAt, updatedAt)
    }

    private fun Conversation.toEntity(): ConversationEntity {
        val messagesJson = try {
            json.encodeToString(messages)
        } catch (_: Exception) { "[]" }
        return ConversationEntity(id, userId, title, messagesJson, createdAt, updatedAt)
    }

    companion object {
        private const val NIDHI_SYSTEM_PROMPT = """
            You are NIDHI, a knowledgeable and caring AI assistant for Indian families.
            You help with government benefits and schemes, document management, financial planning,
            health and family welfare, and tax planning. Be concise, accurate, and warm.
            Always respond in the language the user writes in.
        """
    }
}
