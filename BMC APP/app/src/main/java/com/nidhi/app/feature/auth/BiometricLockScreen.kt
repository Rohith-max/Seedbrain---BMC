package com.nidhi.app.feature.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Biometric / device-credential lock screen shown before the main app.
 * Uses AndroidX BiometricPrompt — supports fingerprint, face, and PIN/pattern fallback.
 */
@Composable
fun BiometricLockScreen(
    onUnlocked: () -> Unit,
    onSkip: () -> Unit // called if device has no biometric enrolled
) {
    val context = LocalContext.current
    var authState by remember { mutableStateOf(AuthState.IDLE) }
    var errorMessage by remember { mutableStateOf("") }

    // Pulse animation for the fingerprint icon
    val scale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Check if the device has any lock mechanism at all.
    // Only skip if there is genuinely no PIN/pattern/biometric set (BIOMETRIC_ERROR_NONE_ENROLLED
    // with DEVICE_CREDENTIAL also absent). Any other result — including transient HW errors —
    // should still show the lock screen and let the user try or use PIN/Password.
    LaunchedEffect(Unit) {
        val biometricManager = BiometricManager.from(context)
        val canAuth = biometricManager.canAuthenticate(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
        if (canAuth == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            // Neither biometric nor device credential is enrolled — device has no lock at all.
            onSkip()
            return@LaunchedEffect
        }
        // For all other results (SUCCESS, HW errors, etc.) attempt the prompt.
        // The prompt itself will surface a useful error to the user if hardware is unavailable.
        triggerBiometric(context, onSuccess = onUnlocked, onError = { msg ->
            authState = AuthState.ERROR
            errorMessage = msg
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Security,
                        null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                "NIDHI",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                "Verify your identity to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.size(8.dp))

            // Fingerprint icon with pulse animation
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(if (authState == AuthState.IDLE) scale else 1f)
                    .clip(CircleShape)
                    .background(
                        when (authState) {
                            AuthState.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
                            AuthState.ERROR -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (authState) {
                        AuthState.SUCCESS -> Icons.Default.CheckCircle
                        AuthState.ERROR -> Icons.Default.ErrorOutline
                        else -> Icons.Default.Fingerprint
                    },
                    null,
                    modifier = Modifier.size(52.dp),
                    tint = when (authState) {
                        AuthState.SUCCESS -> MaterialTheme.colorScheme.primary
                        AuthState.ERROR -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                    }
                )
            }

            AnimatedVisibility(visible = authState == AuthState.ERROR) {
                Text(
                    errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            AnimatedVisibility(visible = authState == AuthState.ERROR) {
                Button(
                    onClick = {
                        authState = AuthState.IDLE
                        triggerBiometric(context, onSuccess = onUnlocked, onError = { msg ->
                            authState = AuthState.ERROR
                            errorMessage = msg
                        })
                    }
                ) {
                    Icon(Icons.Default.Fingerprint, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Try Again")
                }
            }

            TextButton(onClick = {
                triggerBiometric(
                    context,
                    credentialOnly = true,
                    onSuccess = onUnlocked,
                    onError = { msg ->
                        authState = AuthState.ERROR
                        errorMessage = msg
                    }
                )
            }) {
                Text("Use PIN / Password")
            }
        }
    }
}

private enum class AuthState { IDLE, SUCCESS, ERROR }

fun triggerBiometric(
    context: Context,
    credentialOnly: Boolean = false,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val activity = context as? FragmentActivity ?: run {
        // If context is not a FragmentActivity, skip biometric gracefully
        onSuccess()
        return
    }

    val authenticators = if (credentialOnly) DEVICE_CREDENTIAL
    else (BIOMETRIC_WEAK or DEVICE_CREDENTIAL)

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock NIDHI")
        .setSubtitle("Use your biometric credential")
        .setAllowedAuthenticators(authenticators)
        .build()

    val executor = ContextCompat.getMainExecutor(context)
    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> onError("Authentication cancelled. Tap to retry.")
                    // Hardware not present / not enrolled: show error, do NOT silently unlock.
                    // The user must disable biometric lock in Settings if they no longer want it.
                    BiometricPrompt.ERROR_NO_BIOMETRICS ->
                        onError("No biometric enrolled. Use PIN / Password or disable biometric lock in Settings.")
                    BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL ->
                        onError("No screen lock set on this device. Please set a PIN or pattern in device Settings.")
                    BiometricPrompt.ERROR_HW_NOT_PRESENT,
                    BiometricPrompt.ERROR_HW_UNAVAILABLE ->
                        onError("Biometric hardware unavailable. Use PIN / Password.")
                    else -> onError(errString.toString())
                }
            }
            override fun onAuthenticationFailed() {
                onError("Biometric not recognised. Please try again.")
            }
        }
    )
    biometricPrompt.authenticate(promptInfo)
}
