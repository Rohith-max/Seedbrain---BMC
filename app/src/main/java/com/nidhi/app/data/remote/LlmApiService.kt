package com.nidhi.app.data.remote

import com.nidhi.app.BuildConfig
import com.nidhi.app.data.remote.dto.ChatCompletionRequest
import com.nidhi.app.data.remote.dto.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * OpenAI-compatible chat completions endpoint.
 * Works with:
 *  - Groq  (https://api.groq.com/openai/v1/) — model: llama3-8b-8192
 *  - OpenAI (https://api.openai.com/v1/)      — model: gpt-4o-mini
 *
 * Base URL and API key are injected via BuildConfig from secrets.properties.
 */
interface LlmApiService {

    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body request: ChatCompletionRequest,
        @Header("Authorization") auth: String = "Bearer ${BuildConfig.LLM_API_KEY}"
    ): ChatCompletionResponse
}
