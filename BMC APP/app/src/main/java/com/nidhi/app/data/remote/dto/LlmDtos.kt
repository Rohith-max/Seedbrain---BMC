package com.nidhi.app.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    val model: String,
    val messages: List<LlmMessage>,
    @Json(name = "max_tokens") val maxTokens: Int = 800,
    val temperature: Double = 0.7,
    val stream: Boolean = false   // NEW — set to true for SSE streaming
)

@JsonClass(generateAdapter = true)
data class LlmMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage?
)

@JsonClass(generateAdapter = true)
data class Choice(
    val index: Int,
    val message: LlmMessage,
    @Json(name = "finish_reason") val finishReason: String?
)

@JsonClass(generateAdapter = true)
data class Usage(
    @Json(name = "prompt_tokens") val promptTokens: Int,
    @Json(name = "completion_tokens") val completionTokens: Int,
    @Json(name = "total_tokens") val totalTokens: Int
)

// ── Streaming DTOs ────────────────────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class StreamingChunk(
    val id: String,
    val choices: List<StreamingChoice>
)

@JsonClass(generateAdapter = true)
data class StreamingChoice(
    val delta: DeltaContent,
    @Json(name = "finish_reason") val finishReason: String?
)

@JsonClass(generateAdapter = true)
data class DeltaContent(
    val role: String?,
    val content: String?
)
