package com.nidhi.app.feature.emergency

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EmergencyScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var sosTapped by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(5) }

    val permissions = rememberMultiplePermissionsState(
        listOf(Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION)
    )

    // Pulsing animation for SOS button
    val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "scale"
    )

    // Countdown timer when SOS tapped
    LaunchedEffect(sosTapped) {
        if (sosTapped) {
            vibrateDevice(context)
            countdown = 5
            while (countdown > 0) {
                kotlinx.coroutines.delay(1000)
                countdown--
            }
            // Trigger emergency actions
            triggerSos(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // SOS Button
            Box(
                modifier = Modifier.scale(scale),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f), CircleShape)
                )
                // Inner button
                Button(
                    onClick = {
                        if (!sosTapped) {
                            if (permissions.allPermissionsGranted) {
                                sosTapped = true
                            } else {
                                permissions.launchMultiplePermissionRequest()
                            }
                        } else {
                            sosTapped = false // Cancel
                        }
                    },
                    modifier = Modifier.size(160.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (sosTapped)
                            MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        else
                            MaterialTheme.colorScheme.error
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Emergency, null,
                            Modifier.size(48.dp), tint = Color.White)
                        Text(
                            if (sosTapped) "CANCEL\n($countdown)" else "SOS",
                            color = Color.White,
                            fontSize = if (sosTapped) 18.sp else 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (!sosTapped) {
                Text(
                    "Tap to send your location to emergency contacts and call 112",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    "Sending SOS in $countdown seconds…\nTap button to cancel",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider()

            // Quick actions
            Text("Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EmergencyActionCard(
                    icon = Icons.Default.Call,
                    label = "Call 112",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                        )
                    }
                )
                EmergencyActionCard(
                    icon = Icons.Default.Call,
                    label = "Ambulance\n108",
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))
                        )
                    }
                )
                EmergencyActionCard(
                    icon = Icons.Default.LocalPolice,
                    label = "Police\n100",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:100"))
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EmergencyActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
            Text(label, style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

private fun vibrateDevice(context: Context) {
    try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator.vibrate(
                VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200), -1)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200), -1))
            }
        }
    } catch (_: Exception) {}
}

private fun triggerSos(context: Context) {
    // In production: send location via WhatsApp, call emergency contact
    // For now, dial 112
    try {
        context.startActivity(
            Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
        )
    } catch (_: Exception) {}
}
