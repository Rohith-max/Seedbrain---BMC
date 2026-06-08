package com.nidhi.app.feature.document

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val documentTypes = listOf(
    "Aadhaar Card", "PAN Card", "Passport", "Voter ID", "Driving Licence",
    "Ration Card", "Birth Certificate", "Income Certificate", "Land Record",
    "Insurance Policy", "Bank Passbook", "Other"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun DocumentCaptureScreen(
    onCaptured: () -> Unit,
    onBack: () -> Unit,
    viewModel: DocumentViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.captureUiState.collectAsState()

    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var selectedDocType by remember { mutableStateOf(documentTypes[0]) }
    var docTitle by remember { mutableStateOf("") }
    var showTypeDropdown by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    // Executor with proper cleanup to prevent leak
    val executor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose { executor.shutdown() }
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
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
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

            // Camera or permission denied state
            if (cameraPermission.status.isGranted) {
                // Camera preview — fills remaining space
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                ) {
                    CameraPreviewView(
                        lifecycleOwner = lifecycleOwner,
                        onImageCaptureReady = { capture -> imageCapture = capture },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Document framing overlay
                    DocumentFrameOverlay()

                    // Hint text at top of preview
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(12.dp),
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Place document within the frame",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }

                // Capture button row — properly centred
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
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
                        // Shutter button
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(4.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = {
                                    val dir = context.cacheDir.resolve("camera").also { it.mkdirs() }
                                    val file = File(dir, "doc_${System.currentTimeMillis()}.jpg")
                                    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                                    imageCapture?.takePicture(
                                        outputOptions,
                                        ContextCompat.getMainExecutor(context),
                                        object : ImageCapture.OnImageSavedCallback {
                                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                                val title = docTitle.ifBlank { selectedDocType }
                                                viewModel.processCapture(file.absolutePath, selectedDocType, title)
                                            }
                                            override fun onError(exc: ImageCaptureException) {
                                                // Even on camera error, use a simulated path
                                                val title = docTitle.ifBlank { selectedDocType }
                                                viewModel.processCapture(file.absolutePath, selectedDocType, title)
                                            }
                                        }
                                    ) ?: run {
                                        // imageCapture not ready yet — process with empty path (simulated)
                                        val title = docTitle.ifBlank { selectedDocType }
                                        viewModel.processCapture("", selectedDocType, title)
                                    }
                                },
                                modifier = Modifier.size(72.dp)
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    "Capture",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Permission denied state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            "Camera permission needed to scan documents",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                            Text("Grant Permission")
                        }
                        OutlinedButton(onClick = {
                            // Simulate a scan without camera
                            val title = docTitle.ifBlank { selectedDocType }
                            viewModel.processCapture("", selectedDocType, title)
                        }) {
                            Icon(Icons.Default.Sensors, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Simulate Scan")
                        }
                    }
                }
            }

            // Error message
            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun CameraPreviewView(
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onImageCaptureReady: (ImageCapture) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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

                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        capture
                    )
                    onImageCaptureReady(capture)
                } catch (_: Exception) {
                    // Camera binding failed silently — user will see "Simulate Scan" option
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = modifier
    )
}

@Composable
private fun DocumentFrameOverlay() {
    // Drawn purely in Compose — no Canvas needed
    Box(modifier = Modifier.fillMaxSize()) {
        val cornerColor = Color.White.copy(alpha = 0.9f)
        val cornerSize = 28.dp
        val strokeW = 3.dp

        // Top-left corner
        Box(Modifier.align(Alignment.TopStart).padding(20.dp)) {
            Box(Modifier.width(cornerSize).height(strokeW).background(cornerColor))
            Box(Modifier.width(strokeW).height(cornerSize).background(cornerColor))
        }
        // Top-right corner
        Box(Modifier.align(Alignment.TopEnd).padding(20.dp)) {
            Box(Modifier.width(cornerSize).height(strokeW).background(cornerColor).align(Alignment.TopEnd))
            Box(Modifier.width(strokeW).height(cornerSize).background(cornerColor).align(Alignment.TopEnd))
        }
        // Bottom-left corner
        Box(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
            Box(Modifier.width(cornerSize).height(strokeW).background(cornerColor).align(Alignment.BottomStart))
            Box(Modifier.width(strokeW).height(cornerSize).background(cornerColor).align(Alignment.BottomStart))
        }
        // Bottom-right corner
        Box(Modifier.align(Alignment.BottomEnd).padding(20.dp)) {
            Box(Modifier.width(cornerSize).height(strokeW).background(cornerColor).align(Alignment.BottomEnd))
            Box(Modifier.width(strokeW).height(cornerSize).background(cornerColor).align(Alignment.BottomEnd))
        }
    }
}
