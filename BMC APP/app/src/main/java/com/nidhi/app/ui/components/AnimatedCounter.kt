package com.nidhi.app.ui.components

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle

/**
 * Animates an integer counter from its previous value to [targetValue].
 * Respects the system "Reduce Motion" accessibility setting — jumps instantly when enabled.
 * (Req 17.3, 17.6)
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    durationMs: Int = 800,
    style: TextStyle = MaterialTheme.typography.displaySmall
) {
    val context = LocalContext.current
    val effectiveDuration = if (isReduceMotionEnabled(context)) 0 else durationMs
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = effectiveDuration, easing = FastOutSlowInEasing),
        label = "counter"
    )
    Text(
        text = "$animatedValue",
        modifier = modifier,
        style = style
    )
}

/** Returns true if the system animator duration scale is set to 0 (Reduce Motion). */
fun isReduceMotionEnabled(context: Context): Boolean =
    Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    ) == 0f
