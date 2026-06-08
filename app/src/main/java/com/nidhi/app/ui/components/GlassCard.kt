package com.nidhi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Glassmorphism-style card with semi-transparent background and subtle border.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    fillWidth: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = !MaterialTheme.colorScheme.background.isLight()
    val glassColor = if (isDark)
        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
    else
        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
    val borderColor = if (isDark)
        Color.White.copy(alpha = 0.08f)
    else
        Color.White.copy(alpha = 0.6f)

    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
            .clip(shape)
            .background(glassColor)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .padding(16.dp),
        content = content
    )
}

// Extension helper
private fun Color.isLight(): Boolean {
    val luminance = (0.299f * red + 0.587f * green + 0.114f * blue)
    return luminance > 0.5f
}
