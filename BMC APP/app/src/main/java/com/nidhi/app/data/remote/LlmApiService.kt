package com.nidhi.app.data.remote

import com.nidhi.app.BuildConfig
import com.nidhi.app.data.remote.dto.ChatCompletionRequest
import com.nidhi.app.data.remote.dto.ChatCompletionResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Streaming

interface LlmApiService {

    /** Non-streaming chat completions endpoint. */
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body request: ChatCompletionRequest,
        @Header("Authorization") auth: String = "Bearer ${BuildConfig.LLM_API_KEY}"
    ): ChatCompletionResponse

    /**
     * Streaming chat completions endpoint.
     * Returns a raw [ResponseBody] that is parsed as an SSE stream by [SseParser].
     * The caller must close the response body when done.
     */
    @Streaming
    @POST("chat/completions")
    suspend fun createStreamingChatCompletion(
        @Body request: ChatCompletionRequest,
        @Header("Authorization") auth: String = "Bearer ${BuildConfig.LLM_API_KEY}"
    ): ResponseBody
}
