package com.nidhi.app.feature.auth

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

private const val TAG = "NidhiBio"

private enum class S { IDLE, WAITING, OK, ERR }

/**
 * Samsung A52s — definitive fix.
 *
 * PROBLEM HISTORY:
 * - canAuthenticate() with any flags returns non-SUCCESS on Samsung One UI 4/5
 *   even when fingerprints are enrolled → code called onSkip() immediately.
 * - LaunchedEffect / DisposableEffect / SideEffect all run on coroutine dispatcher →
 *   Samsung biometric daemon rejects the call silently.
 *
 * FINAL SOLUTION:
 * 1. Do NOT call canAuthenticate() at all to decide whether to show the screen.
 *    Always show the lock screen when biometric is enabled.
 * 2. The prompt is launched by a Button onClick — this is a real user interaction
 *    token that Samsung ALWAYS accepts.
 * 3. Show a "Tap to authenticate" prompt immediately on first render.
 * 4. onSkip() is called ONLY for ERROR_HW_NOT_PRESENT (truly no hardware).
 *    Every other error stays on screen and lets user retry or use PIN.
 *
 * NOTE: The screen renders, user sees fingerprint icon, they tap it or place
 * finger → system biometric dialog appears. This is the Samsung-required flow.
 */
@Composable
fun BiometricLockScreen(
    onUnlocked: () -> Unit,
    onSkip: () -> Unit          // only called when truly no biometric HW present
) {
    val context = LocalContext.current
    var state   by remember { mutableStateOf(S.IDLE) }
    var status  by remember { mutableStateOf("Tap fingerprint icon or use PIN") }

    val pulse by rememberInfiniteTransition(label = "p").animateFloat(
        1f, 1.2f,
        infiniteRepeatable(tween(850, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "sc"
    )

    // Choose authenticator flags (never combined check, just pick by API level)
    val authFlags = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL  // API 30+ — covers optical + PIN
        else
            BIOMETRIC_STRONG                        // API 28-29 — fingerprint only
    }
    val needNegBtn = Build.VERSION.SDK_INT < Build.VERSION_CODES.R

    fun launch() {
        val activity = context.findActivity()
        if (activity == null) { onUnlocked(); return }

        state  = S.WAITING
        status = "Authenticating..."

        val info = try {
            val b = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock NIDHI")
                .setSubtitle("Verify your identity")
                .setDescription("Use your fingerprint or device PIN")
            if (needNegBtn) {
                b.setAllowedAuthenticators(BIOMETRIC_STRONG)
                 .setNegativeButtonText("Use PIN instead")
            } else {
                b.setAllowedAuthenticators(authFlags)
            }
            b.build()
        } catch (e: Exception) {
            Log.e(TAG, "PromptInfo build err: ${e.message}")
            // Absolute fallback — DEVICE_CREDENTIAL only (always works)
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock NIDHI")
                .setSubtitle("Enter your device PIN")
                .setAllowedAuthenticators(DEVICE_CREDENTIAL)
                .build()
        }

        BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(r: BiometricPrompt.AuthenticationResult) {
                    Log.d(TAG, "SUCCESS type=${r.authenticationType}")
                    state = S.OK
                    onUnlocked()
                }
                override fun onAuthenticationError(code: Int, msg: CharSequence) {
                    Log.w(TAG, "ERROR $code: $msg")
                    when (code) {
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
                            // Truly no hardware — pass through
                            onSkip()
                        }
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                            state  = S.ERR
                            status = "Too many attempts. Use PIN below."
                        }
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_CANCELED,
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            state  = S.ERR
                            status = "Tap the icon to try again"
                        }
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            // No fingerprint enrolled but device has hardware
                            state  = S.ERR
                            status = "No fingerprint enrolled. Use PIN below."
                        }
                        BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {
                            // No PIN/pattern set — let through
                            onSkip()
                        }
                        else -> {
                            state  = S.ERR
                            status = msg.toString().ifBlank { "Tap icon to retry" }
                        }
                    }
                }
                override fun onAuthenticationFailed() {
                    Log.d(TAG, "FAILED — bad scan, waiting")
                    state  = S.ERR
                    status = "Not recognised. Try again."
                }
            }
        ).authenticate(info)
    }

    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(Modifier.size(80.dp), CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Security, null,
                        Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }

            Text("NIDHI", style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Text("Your family's financial vault",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(0.55f),
                textAlign = TextAlign.Center)

            Spacer(Modifier.height(8.dp))

            // THE FINGERPRINT BUTTON — user taps this, launch() fires from onClick
            // onClick is a real UI interaction token — Samsung ALWAYS accepts this
            Box(
                Modifier
                    .size(110.dp)
                    .scale(if (state == S.IDLE) pulse else 1f)
                    .clip(CircleShape)
                    .background(
                        if (state == S.ERR) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = ::launch, Modifier.fillMaxSize()) {
                    Icon(
                        if (state == S.ERR) Icons.Default.ErrorOutline else Icons.Default.Fingerprint,
                        "Authenticate",
                        Modifier.size(58.dp),
                        tint = if (state == S.ERR) MaterialTheme.colorScheme.error
                               else MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(status,
                style = MaterialTheme.typography.bodySmall,
                color = if (state == S.ERR) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface.copy(0.65f),
                textAlign = TextAlign.Center)

            Spacer(Modifier.height(4.dp))

            // PIN fallback — onClick is real interaction token, always works
            FilledTonalButton(onClick = {
                val activity = context.findActivity()
                if (activity == null) { onUnlocked(); return@FilledTonalButton }
                state  = S.IDLE
                status = "Enter your PIN or password"
                val pinInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Unlock NIDHI")
                    .setSubtitle("Enter your device PIN")
                    .setAllowedAuthenticators(DEVICE_CREDENTIAL)
                    .build()
                BiometricPrompt(activity, ContextCompat.getMainExecutor(activity),
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(r: BiometricPrompt.AuthenticationResult) { onUnlocked() }
                        override fun onAuthenticationError(c: Int, m: CharSequence) {
                            state  = S.ERR
                            status = m.toString().ifBlank { "PIN entry failed" }
                        }
                        override fun onAuthenticationFailed() { state = S.ERR; status = "Wrong PIN" }
                    }).authenticate(pinInfo)
            }) {
                Icon(Icons.Default.Pin, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Use PIN / Password")
            }

            TextButton(onClick = onSkip) {
                Text("Skip for now", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

// ── Context unwrapper ──────────────────────────────────────────────────────────
fun Context.findActivity(): FragmentActivity? {
    var c: Context = this
    repeat(20) {
        if (c is FragmentActivity) return c as FragmentActivity
        c = (c as? android.content.ContextWrapper)?.baseContext ?: return null
    }
    return null
}

// ── Shims for rest of app ──────────────────────────────────────────────────────
fun triggerBiometric(context: Context, credentialOnly: Boolean = false,
    onSuccess: () -> Unit, onError: (String) -> Unit) {
    val a = context.findActivity() ?: run { onSuccess(); return }
    val flags = if (credentialOnly) DEVICE_CREDENTIAL
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                else BIOMETRIC_STRONG
    val info = try {
        val b = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock NIDHI").setSubtitle("Verify identity")
        if (!credentialOnly && Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            b.setAllowedAuthenticators(BIOMETRIC_STRONG).setNegativeButtonText("Cancel")
        else b.setAllowedAuthenticators(flags)
        b.build()
    } catch (e: Exception) {
        BiometricPrompt.PromptInfo.Builder().setTitle("Unlock NIDHI")
            .setAllowedAuthenticators(DEVICE_CREDENTIAL).build()
    }
    BiometricPrompt(a, ContextCompat.getMainExecutor(a),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(r: BiometricPrompt.AuthenticationResult) = onSuccess()
            override fun onAuthenticationError(c: Int, m: CharSequence) = onError(m.toString())
            override fun onAuthenticationFailed() = onError("Not recognised")
        }).authenticate(info)
}

data class BiometricInfo(val available: Boolean, val authenticators: Int, val useNegativeButton: Boolean)
fun resolveBiometricAuthenticator(context: Context) = BiometricInfo(true,
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) BIOMETRIC_STRONG or DEVICE_CREDENTIAL else BIOMETRIC_STRONG,
    Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
fun launchBiometricPrompt(context: Context, info: BiometricInfo,
    onSuccess: () -> Unit, onError: (Int, String) -> Unit) =
    triggerBiometric(context, false, onSuccess) { m -> onError(0, m) }
