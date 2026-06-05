package com.nidhi.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Light Color Scheme ────────────────────────────────────────────────────────
private val LightColors = lightColorScheme(
    primary = Teal600,
    onPrimary = NeutralWhite,
    primaryContainer = TealContainer,
    onPrimaryContainer = Teal900,
    secondary = Gold600,
    onSecondary = NeutralBlack,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = Gold400,
    tertiary = WarningOrange,
    onTertiary = NeutralWhite,
    background = NeutralWhite,
    onBackground = NeutralBlack,
    surface = NeutralWhite,
    onSurface = NeutralBlack,
    surfaceVariant = NeutralGray100,
    onSurfaceVariant = NeutralGray800,
    outline = NeutralGray400,
    error = ErrorRed,
    onError = NeutralWhite,
    errorContainer = ErrorRedContainer,
    onErrorContainer = ErrorRed,
    scrim = Scrim
)

// ── Dark Color Scheme ─────────────────────────────────────────────────────────
private val DarkColors = darkColorScheme(
    primary = Teal200,
    onPrimary = Teal900,
    primaryContainer = Teal700,
    onPrimaryContainer = TealContainer,
    secondary = Gold200,
    onSecondary = NeutralBlack,
    secondaryContainer = Gold500,
    onSecondaryContainer = GoldContainer,
    tertiary = WarningOrange,
    onTertiary = NeutralWhite,
    background = DarkBackground,
    onBackground = NeutralGray100,
    surface = DarkSurface,
    onSurface = NeutralGray100,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = NeutralGray400,
    outline = DarkOutline,
    error = ErrorRed,
    onError = NeutralWhite,
    errorContainer = ErrorRed,
    onErrorContainer = ErrorRedContainer,
    scrim = Scrim
)

@Composable
fun NidhiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NidhiTypography,
        content = content
    )
}
