package com.nidhi.app.data.remote

import com.nidhi.app.BuildConfig
import com.nidhi.app.data.remote.dto.ChatCompletionRequest
import com.nidhi.app.data.remote.dto.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LlmApiService {

    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body request: ChatCompletionRequest,
        @Header("Authorization") auth: String = "Bearer ${BuildConfig.LLM_API_KEY}"
    ): ChatCompletionResponse
}
