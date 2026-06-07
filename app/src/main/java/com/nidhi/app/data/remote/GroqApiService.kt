package com.nidhi.app.data.remote

import com.nidhi.app.BuildConfig
import com.nidhi.app.config.ApiKeys
import com.nidhi.app.data.remote.dto.ChatCompletionRequest
import com.nidhi.app.data.remote.dto.ChatCompletionResponse
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

/**
 * Groq API Service — Fast open-source LLM inference (OpenAI-compatible).
 * Using Mixtral-8x7b-32768 for efficient and intelligent responses.
 *
 * Groq is significantly faster than typical LLMs (~5x speedup) and is ideal for real-time chat.
 */
interface GroqApiService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}

/**
 * Factory to create Groq API service with proper authentication and configuration.
 */
object GroqServiceFactory {
    fun create(): GroqApiService {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG)
                    HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            })
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer ${BuildConfig.LLM_API_KEY}")
                    .build()
                chain.proceed(newRequest)
            }
            .build()

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.LLM_BASE_URL)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        return retrofit.create(GroqApiService::class.java)
    }
}
