@file:Suppress("OPT_IN_IS_NOT_ENABLED")
@file:OptIn(androidx.camera.core.ExperimentalGetImage::class)
package com.nidhi.app.feature.document

import android.Manifest
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.vision.text.Text as MlText
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import java.util.concurrent.Executors

val documentTypes = listOf(
    "Aadhaar Card", "PAN Card", "Passport", "Voter ID", "Driving Licence",
    "Ration Card", "Birth Certificate", "Income Certificate", "Land Record",
    "Insurance Policy", "Bank Passbook", "Other"
)

/** Luma (brightness) threshold below which capture is blocked (Req 11.7). */
private const val LUMA_THRESHOLD = 40.0

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DocumentCaptureScreen(
    onCaptured: () -> Unit,
    onBack: () -> Unit,
    viewModel: DocumentViewModel = koinViewModel()
) {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState        by viewModel.captureUiState.collectAsState()

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var selectedDocType  by remember { mutableStateOf(documentTypes[0]) }
    var docTitle         by remember { mutableStateOf("") }
    var showTypeDropdown by remember { mutableStateOf(false) }
    var imageCapture     by remember { mutableStateOf<ImageCapture?>(null) }

    // Live text-overlay state (Req 11.6)
    var detectedBlocks   by remember { mutableStateOf<List<MlText.TextBlock>>(emptyList()) }
    // Preview dimensions needed to scale ML Kit bounding boxes
    var previewWidth     by remember { mutableStateOf(1f) }
    var previewHeight    by remember { mutableStateOf(1f) }
    // Source image dimensions reported by ImageAnalysis
    var imageWidth       by remember { mutableStateOf(1) }
    var imageHeight      by remember { mutableStateOf(1) }

    // Luma error (Req 11.7)
    var lumaError        by remember { mutableStateOf<String?>(null) }

    // Shared single-thread executor for ImageAnalysis
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose { analysisExecutor.shutdown() }
    }

    // ML Kit text recogniser (reused across frames)
    val textRecognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    DisposableEffect(Unit) {
        onDispose { textRecognizer.close() }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            viewModel.clearCaptureState()
            onCaptured()
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Document") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Document type selector
            ExposedDropdownMenuBox(
                expanded = showTypeDropdown,
                onExpandedChange = { showTypeDropdown = it }
            ) {
                OutlinedTextField(
                    value = selectedDocType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Document Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(showTypeDropdown) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = showTypeDropdown,
                    onDismissRequest = { showTypeDropdown = false }
                ) {
                    documentTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedDocType = type
                                if (docTitle.isBlank()) docTitle = type
                                showTypeDropdown = false
                            }
                        )
                    }
                }
            }

            // Document title
            OutlinedTextField(
                value = docTitle,
                onValueChange = { docTitle = it },
                label = { Text("Document Name") },
                placeholder = { Text("e.g. Dad's Aadhaar") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (cameraPermission.status.isGranted) {
                // Camera preview with live overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                ) {
                    // CameraX preview + ImageAnalysis
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                            cameraProviderFuture.addListener({
                                try {
                                    val provider = cameraProviderFuture.get()

                                    val preview = Preview.Builder().build().also {
                                        it.surfaceProvider = previewView.surfaceProvider
                                    }

                                    val capture = ImageCapture.Builder()
                                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                        .build()

                                    // ImageAnalysis use-case for live overlay + luma (Req 11.6, 11.7)
                                    val analysis = ImageAnalysis.Builder()
                                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                        .build()

                                    analysis.setAnalyzer(analysisExecutor) { proxy ->
                                        val mediaImage = proxy.image
                                        if (mediaImage != null) {
                                            // ── Luma check (Req 11.7) ─────────────────────
                                            val lumaValue = computeLuma(proxy)
                                            if (lumaValue < LUMA_THRESHOLD) {
                                                lumaError = "Image too dark. Please improve lighting."
                                            } else {
                                                lumaError = null
                                            }

                                            // ── Live text detection (Req 11.6) ────────────
                                            imageWidth  = proxy.width
                                            imageHeight = proxy.height
                                            val inputImage = InputImage.fromMediaImage(
                                                mediaImage, proxy.imageInfo.rotationDegrees
                                            )
                                            textRecognizer.process(inputImage)
                                                .addOnSuccessListener { result ->
                                                    // Update overlay; retain last positions on failure
                                                    if (result.textBlocks.isNotEmpty()) {
                                                        detectedBlocks = result.textBlocks
                                                    }
                                                }
                                                .addOnCompleteListener { proxy.close() }
                                        } else {
                                            proxy.close()
                                        }
                                    }

                                    provider.unbindAll()
                                    provider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        capture,
                                        analysis
                                    )
                                    imageCapture = capture
                                } catch (_: Exception) {
                                    // Camera binding failed — Simulate Scan still available
                                }
                            }, ContextCompat.getMainExecutor(ctx))

                            previewView
                        },
                        update = { view ->
                            previewWidth  = view.width.toFloat().coerceAtLeast(1f)
                            previewHeight = view.height.toFloat().coerceAtLeast(1f)
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Live text-block overlay (Req 11.6)
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val scaleX = size.width  / imageWidth.toFloat()
                        val scaleY = size.height / imageHeight.toFloat()
                        detectedBlocks.forEach { block ->
                            block.boundingBox?.let { rect ->
                                drawRect(
                                    color = Color(0xFF00E5FF),
                                    topLeft = Offset(rect.left * scaleX, rect.top * scaleY),
                                    size = Size(rect.width() * scaleX, rect.height() * scaleY),
                                    style = Stroke(width = 2f)
                                )
                            }
                        }
                    }

                    // Framing corners
                    DocumentFrameOverlay()

                    // Top hint
                    Surface(
                        modifier = Modifier.align(Alignment.TopCenter).padding(12.dp),
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            if (detectedBlocks.isNotEmpty()) "Text detected — tap to capture"
                            else "Place document within the frame",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }

                    // Luma warning badge (Req 11.7)
                    lumaError?.let { msg ->
                        Surface(
                            modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    msg,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }

                // Capture button
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isProcessing) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp))
                            Text(
                                if (uiState.ocrText != null) "Generating summary…" else "Extracting text…",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                // Dim shutter when too dark (Req 11.7)
                                .background(
                                    if (lumaError != null) MaterialTheme.colorScheme.outline
                                    else MaterialTheme.colorScheme.primary
                                )
                                .border(4.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = {
                                    // Block capture when too dark (Req 11.7)
                                    if (lumaError != null) return@IconButton

                                    val dir  = context.cacheDir.resolve("camera").also { it.mkdirs() }
                                    val file = File(dir, "doc_${System.currentTimeMillis()}.jpg")
                                    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                                    imageCapture?.takePicture(
                                        outputOptions,
                                        ContextCompat.getMainExecutor(context),
                                        object : ImageCapture.OnImageSavedCallback {
                                            override fun onImageSaved(out: ImageCapture.OutputFileResults) {
                                                viewModel.processCapture(
                                                    file.absolutePath, selectedDocType,
                                                    docTitle.ifBlank { selectedDocType }
                                                )
                                            }
                                            // Camera hardware error — allow retry (Req 11.8)
                                            override fun onError(exc: ImageCaptureException) {
                                                viewModel.setCaptureError(
                                                    "Camera error. Please try again."
                                                )
                                            }
                                        }
                                    ) ?: run {
                                        // Camera not ready — simulate
                                        viewModel.processCapture(
                                            "", selectedDocType, docTitle.ifBlank { selectedDocType }
                                        )
                                    }
                                },
                                modifier = Modifier.size(72.dp)
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt, "Capture",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Permission denied
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline)
                        Text(
                            "Camera permission needed to scan documents",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                            Text("Grant Permission")
                        }
                        OutlinedButton(onClick = {
                            viewModel.processCapture("", selectedDocType, docTitle.ifBlank { selectedDocType })
                        }) {
                            Icon(Icons.Default.Sensors, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Simulate Scan")
                        }
                    }
                }
            }

            // Error banner (shows luma + camera errors, Req 11.7, 11.8)
            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, null,
                            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                        Text(error, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
        }
    }
}

/**
 * Computes the average luma (brightness) of the ImageProxy's Y-plane.
 * Returns a value in [0, 255]. Used for Req 11.7 luma gate.
 */
private fun computeLuma(proxy: ImageProxy): Double {
    val buffer = proxy.planes[0].buffer
    val data = ByteArray(buffer.remaining())
    buffer.get(data)
    return data.map { it.toInt() and 0xFF }.average()
}

@Composable
private fun DocumentFrameOverlay() {
    Box(modifier = Modifier.fillMaxSize()) {
        val cornerColor = Color.White.copy(alpha = 0.9f)
        val cornerSize  = 28.dp
        val strokeW     = 3.dp
        // Top-left
        Box(Modifier.align(Alignment.TopStart).padding(20.dp)) {
            Box(Modifier.width(cornerSize).height(strokeW).background(cornerColor))
            Box(Modifier.width(strokeW).height(cornerSize).background(cornerColor))
        }
        // Top-right
        Box(Modifier.align(Alignment.TopEnd).padding(20.dp)) {
            Box(Modifier.width(cornerSize).height(strokeW).background(cornerColor).align(Alignment.TopEnd))
            Box(Modifier.width(strokeW).height(cornerSize).background(cornerColor).align(Alignment.TopEnd))
        }
        // Bottom-left
        Box(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Box(Modifier.width(cornerSize).height(strokeW).background(cornerColor).align(Alignment.BottomStart))
            Box(Modifier.width(strokeW).height(cornerSize).background(cornerColor).align(Alignment.BottomStart))
        }
        // Bottom-right
        Box(Modifier.align(Alignment.BottomEnd).padding(20.dp)) {
            Box(Modifier.width(cornerSize).height(strokeW).background(cornerColor).align(Alignment.BottomEnd))
            Box(Modifier.width(strokeW).height(cornerSize).background(cornerColor).align(Alignment.BottomEnd))
        }
    }
}
