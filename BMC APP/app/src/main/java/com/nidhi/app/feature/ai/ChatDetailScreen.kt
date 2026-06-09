package com.nidhi.app.feature.ai

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.nidhi.app.domain.model.ChatMessage
import com.nidhi.app.domain.model.ChatRole
import com.nidhi.app.ui.components.DocumentsContextChip
import com.nidhi.app.ui.theme.Teal600
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ChatDetailScreen(
    conversationId: String,
    onBack: () -> Unit,
    seedPrompt: String = "",   // Req 10.1 — auto-send when arriving from Benefits → Ask AI
    viewModel: AiViewModel = koinViewModel()
) {
    val uiState by viewModel.chatDetailState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    val micPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    // Quick prompts for Indian family context
    val quickPrompts = listOf(
        "What benefits am I eligible for?",
        "When does my Aadhaar expire?",
        "How to apply for PMJAY?",
        "Explain PM-KISAN scheme"
    )

    LaunchedEffect(conversationId) {
        viewModel.loadConversation(conversationId)
        // Auto-send the seed prompt if provided (Req 10.1)
        if (seedPrompt.isNotBlank()) {
            viewModel.sendMessage(seedPrompt)
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(uiState.messages.lastIndex)
            }
        }
    }

    // Initialize TTS
    DisposableEffect(context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("en", "IN")
            }
        }
        onDispose { tts?.shutdown() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(uiState.conversationTitle.ifBlank { "NIDHI AI" },
                            maxLines = 1)
                        if (uiState.isTyping) {
                            Text("typing…",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                // Input bar
                Surface(
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .navigationBarsPadding()
                            .imePadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Documents context chip (Req 9.5)
                        DocumentsContextChip(
                            docContextActive = uiState.messages.isNotEmpty(),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .navigationBarsPadding()
                            .imePadding(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Voice input
                        IconButton(onClick = {
                            if (micPermission.status.isGranted) {
                                isListening = !isListening
                                if (isListening) {
                                    startListening(context) { transcript ->
                                        inputText = transcript
                                        isListening = false
                                    }
                                }
                            } else {
                                micPermission.launchPermissionRequest()
                            }
                        }) {
                            Icon(
                                if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                "Voice",
                                tint = if (isListening) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.primary
                            )
                        }

                        // Disable text field while streaming (Req 8.5)
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { if (!uiState.isTyping) inputText = it },
                            placeholder = { Text("Ask NIDHI anything…") },
                            modifier = Modifier.weight(1f),
                            maxLines = 4,
                            shape = RoundedCornerShape(24.dp),
                            enabled = !uiState.isTyping
                        )

                        // Send — disabled while streaming (Req 8.5)
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank() && !uiState.isTyping) {
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                }
                            },
                            enabled = inputText.isNotBlank() && !uiState.isTyping,
                            modifier = Modifier.semantics {
                                contentDescription = "Send message"
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = if (inputText.isNotBlank() && !uiState.isTyping)
                                    MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Empty state with quick prompts
            if (uiState.messages.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, null,
                            Modifier.size(48.dp), tint = Teal600)
                        Text("Hi! I'm NIDHI AI",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold)
                        Text(
                            "Ask me about government schemes, your documents, or financial planning.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Try asking:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        // Use a regular Column instead of LazyRow to avoid nested lazy crash
                        quickPrompts.forEach { prompt ->
                            SuggestionChip(
                                onClick = { viewModel.sendMessage(prompt) },
                                label = { Text(prompt) },
                                modifier = Modifier.fillMaxWidth(),
                                icon = { Icon(Icons.Default.AutoAwesome, null, Modifier.size(16.dp)) }
                            )
                        }
                    }
                }
            }

            items(uiState.messages, key = { it.id }) { message ->
                MessageBubble(
                    message = message,
                    onSpeak = { tts?.speak(message.content, TextToSpeech.QUEUE_FLUSH, null, null) }
                )
            }

            // Typing indicator
            if (uiState.isTyping) {
                item {
                    TypingIndicator()
                }
            }

            // Error
            uiState.error?.let { err ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, null,
                                tint = MaterialTheme.colorScheme.error)
                            Text(err, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    onSpeak: () -> Unit
) {
    val isUser = message.role == ChatRole.USER
    // Accessibility: sender role + content (Req 20.6)
    val senderLabel = if (isUser) "You" else "NIDHI AI"
    val a11yLabel   = "$senderLabel: ${message.content}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = a11yLabel },
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("N", color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                color = if (isUser)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(
                    topStart = if (isUser) 16.dp else 4.dp,
                    topEnd = if (isUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                )
            ) {
                Text(
                    message.content,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!isUser) {
                IconButton(onClick = onSpeak, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.VolumeUp, "Read aloud",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "alpha"
    )

    Row(
        modifier = Modifier.padding(start = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { idx ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = alpha * (1f - idx * 0.2f)
                        )
                    )
            )
        }
    }
}

private fun startListening(context: Context, onResult: (String) -> Unit) {
    val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    recognizer.setRecognitionListener(object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            onResult(matches?.firstOrNull() ?: "")
            recognizer.destroy()
        }
        override fun onError(error: Int) { onResult(""); recognizer.destroy() }
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onPartialResults(partialResults: Bundle?) {
            val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            partial?.firstOrNull()?.let { onResult(it) }
        }
        override fun onEvent(eventType: Int, params: Bundle?) {}
    })

    recognizer.startListening(intent)
}
