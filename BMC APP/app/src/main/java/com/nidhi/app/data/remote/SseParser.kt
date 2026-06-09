package com.nidhi.app.data.remote

import com.nidhi.app.data.remote.dto.StreamingChunk
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody

/**
 * Parses an OkHttp [ResponseBody] server-sent-events (SSE) stream into a [Flow] of
 * content delta strings.
 *
 * Emits:
 *  - Non-null [String] for each delta token received
 *  - `null` when the `[DONE]` sentinel is encountered (signals stream completion)
 *
 * Any content tokens that appear after `[DONE]` are silently ignored.
 */
object SseParser {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val chunkAdapter = moshi.adapter(StreamingChunk::class.java)

    /**
     * Parses the SSE response body into a flow of delta strings.
     * Caller is responsible for closing the [ResponseBody] after collection.
     */
    fun parse(body: ResponseBody): Flow<String?> = flow {
        var done = false
        body.source().use { source ->
            while (!source.exhausted() && !done) {
                val line = source.readUtf8Line() ?: break
                if (!line.startsWith("data: ")) continue

                val data = line.removePrefix("data: ").trim()
                when {
                    data == "[DONE]" -> {
                        emit(null)   // signal stream completion
                        done = true  // stop reading after [DONE] — satisfies Property 8
                    }
                    data.isNotEmpty() -> {
                        try {
                            val chunk = chunkAdapter.fromJson(data)
                            val content = chunk?.choices?.firstOrNull()?.delta?.content
                            if (content != null) emit(content)
                        } catch (_: Exception) {
                            // Malformed chunk — skip silently and continue
                        }
                    }
                }
            }
        }
    }
}
