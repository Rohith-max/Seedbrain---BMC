package com.nidhi.app.data.repository

import com.nidhi.app.BuildConfig
import com.nidhi.app.data.local.dao.ConversationDao
import com.nidhi.app.data.local.dao.DocumentDao
import com.nidhi.app.data.local.entity.ConversationEntity
import com.nidhi.app.data.remote.LlmApiService
import com.nidhi.app.data.remote.SimulatedLlmEngine
import com.nidhi.app.data.remote.SseParser
import com.nidhi.app.data.remote.dto.ChatCompletionRequest
import com.nidhi.app.data.remote.dto.LlmMessage
import com.nidhi.app.domain.model.ChatMessage
import com.nidhi.app.domain.model.ChatRole
import com.nidhi.app.domain.model.Conversation
import com.nidhi.app.domain.model.Result
import com.nidhi.app.domain.repository.AiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.util.UUID

/**
 * Uses kotlinx.serialization (which matches @Serializable annotations on ChatMessage / ChatRole)
 * instead of Moshi — eliminates the Moshi/kotlinx mismatch crash.
 *
 * Enhancements (kiro spec):
 *  - Streaming SSE responses via SseParser (Req 8)
 *  - Retry with exponential back-off for 429/5xx (Req 7.4)
 *  - Document-context injection into system prompt (Req 9)
 *  - SimulatedLlmEngine receives document context and prepends count prefix (Req 9.4)
 */
class AiRepositoryImpl(
    private val conversationDao: ConversationDao,
    private val documentDao: DocumentDao,
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

        val msgId = UUID.randomUUID().toString()

        // ── Build document context (Req 9.1–9.3) ─────────────────────────────
        val docContext = try { buildDocumentContext(conversationId) } catch (_: Exception) { "" }

        // ── Simulated LLM path ────────────────────────────────────────────────
        if (useSimulated()) {
            val reply = simulatedLlm.chat(message, docContext)
            emit(Result.Success(
                ChatMessage(id = msgId, role = ChatRole.ASSISTANT, content = reply,
                    timestamp = System.currentTimeMillis())
            ))
            return@flow
        }

        // ── Real API — streaming with retry ──────────────────────────────────
        val conversation = conversationDao.getConversationById(conversationId)
        val history = conversation?.let {
            try { json.decodeFromString<List<ChatMessage>>(it.messagesJson) }
            catch (_: Exception) { emptyList() }
        } ?: emptyList()

        val llmMessages = buildLlmMessages(history, message, docContext)

        var accumulated = ""
        var attempt = 0
        val maxRetries = 2

        while (attempt <= maxRetries) {
            try {
                val request = ChatCompletionRequest(
                    model = BuildConfig.LLM_MODEL,
                    messages = llmMessages,
                    maxTokens = 800,
                    stream = true
                )
                val body = llmApiService.createStreamingChatCompletion(request)
                var streamDone = false

                SseParser.parse(body).collect { delta ->
                    if (delta == null) {
                        // [DONE] — emit final non-streaming message
                        emit(Result.Success(
                            ChatMessage(msgId, ChatRole.ASSISTANT, accumulated,
                                System.currentTimeMillis(), isStreaming = false)
                        ))
                        streamDone = true
                    } else {
                        accumulated += delta
                        emit(Result.Success(
                            ChatMessage(msgId, ChatRole.ASSISTANT, accumulated,
                                System.currentTimeMillis(), isStreaming = true)
                        ))
                    }
                }

                if (streamDone) return@flow   // success — exit retry loop

                // Stream ended without [DONE] — treat as transient error
                throw IOException("Stream ended without [DONE]")

            } catch (e: HttpException) {
                when (e.code()) {
                    429, in 500..599 -> {
                        attempt++
                        if (attempt <= maxRetries) {
                            delay(1000L * attempt)  // 1 s, then 2 s
                        } else {
                            // All retries exhausted — fall back to simulated (Req 7.4)
                            val fallback = simulatedLlm.chat(message, docContext)
                            emit(Result.Success(
                                ChatMessage(msgId, ChatRole.ASSISTANT, fallback,
                                    System.currentTimeMillis())
                            ))
                            return@flow
                        }
                    }
                    401, 403 -> {
                        emit(Result.Error(e,
                            "API key is invalid or unauthorised (HTTP ${e.code()})"))
                        return@flow
                    }
                    else -> {
                        emit(Result.Error(e, "Request failed: HTTP ${e.code()}"))
                        return@flow
                    }
                }
            } catch (e: IOException) {
                attempt++
                if (attempt > maxRetries) {
                    emit(Result.Error(e, "Connection interrupted. Please retry."))
                    return@flow
                }
                delay(1000L * attempt)
            } catch (e: Exception) {
                // Generic fallback
                val fallback = simulatedLlm.chat(message, docContext)
                emit(Result.Success(
                    ChatMessage(msgId, ChatRole.ASSISTANT, fallback, System.currentTimeMillis())
                ))
                return@flow
            }
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

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun useSimulated(): Boolean =
        BuildConfig.USE_SIMULATED_LLM || BuildConfig.LLM_API_KEY == "SIMULATED"

    /**
     * Builds a condensed document context string injected into the system prompt.
     * Sorted by nearest expiry first, null-expiry last. Truncated at 2000 chars (Req 9.3).
     */
    private suspend fun buildDocumentContext(conversationId: String): String {
        // We don't have the userId directly here, so we pull from conversation owner
        val conversation = conversationDao.getConversationById(conversationId) ?: return ""
        val docs = documentDao.getDocumentsByOwner(conversation.userId)

        val sorted = docs.sortedWith(
            compareBy(nullsLast()) { it.expiryDate }
        )

        val sb = StringBuilder()
        for (doc in sorted) {
            val entry = buildString {
                append("[${doc.type}] ${doc.title}")
                doc.expiryDate?.let { append(" | Expiry: ${java.util.Date(it)}") }
                doc.ocrText?.take(300)?.let { append(" | $it") }
                append("\n")
            }
            if (sb.length + entry.length > 2000) break
            sb.append(entry)
        }
        return sb.toString()
    }

    /**
     * Assembles the messages list for the LLM call:
     * system prompt (with optional doc context) + last 10 history messages + current message.
     */
    private fun buildLlmMessages(
        history: List<ChatMessage>,
        userMessage: String,
        docContext: String
    ): List<LlmMessage> {
        val systemPrompt = if (docContext.isNotBlank())
            NIDHI_SYSTEM_PROMPT + "\n\n## User's Documents\n$docContext"
        else
            NIDHI_SYSTEM_PROMPT

        val messages = mutableListOf(LlmMessage("system", systemPrompt))
        history.takeLast(10).forEach { msg ->
            messages.add(LlmMessage(
                role = when (msg.role) {
                    ChatRole.USER      -> "user"
                    ChatRole.ASSISTANT -> "assistant"
                    ChatRole.SYSTEM    -> "system"
                },
                content = msg.content
            ))
        }
        messages.add(LlmMessage("user", userMessage))
        return messages
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

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
            When documents are listed below, use them to give personalised, accurate answers.
        """
    }
}
