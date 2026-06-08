package com.nidhi.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/** Named application theme — persisted as a string in UserPreferences */
enum class AppTheme(val key: String, val label: String, val shortLabel: String) {
    TEAL     ("teal",     "Ocean Teal",   "Teal"),
    SAFFRON  ("saffron",  "India Saffron","Saffron"),
    FOREST   ("forest",   "Forest Green", "Forest"),
    MIDNIGHT ("midnight", "Midnight Blue","Midnight"),
    CORAL    ("coral",    "Coral Sunset", "Coral");

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.key == key } ?: TEAL
    }
}

// ── Saffron / India ──────────────────────────────────────────────────────────
private val SaffronLight = lightColorScheme(
    primary           = Color(0xFFBF5700),
    onPrimary         = Color(0xFFFFFFFF),
    primaryContainer  = Color(0xFFFFDBC9),
    onPrimaryContainer= Color(0xFF3B0E00),
    secondary         = Color(0xFF138808),  // India green
    onSecondary       = Color(0xFFFFFFFF),
    secondaryContainer= Color(0xFFB1F0A8),
    onSecondaryContainer = Color(0xFF002200),
    background        = Color(0xFFFFF8F5),
    surface           = Color(0xFFFFF8F5),
    onBackground      = Color(0xFF201100),
    onSurface         = Color(0xFF201100),
    error             = ErrorRed,
    outline           = Color(0xFF9C5A3C)
)
private val SaffronDark = darkColorScheme(
    primary           = Color(0xFFFFB688),
    onPrimary         = Color(0xFF671E00),
    primaryContainer  = Color(0xFF913000),
    onPrimaryContainer= Color(0xFFFFDBC9),
    secondary         = Color(0xFF80D478),
    onSecondary       = Color(0xFF003A00),
    secondaryContainer= Color(0xFF005300),
    onSecondaryContainer = Color(0xFFB1F0A8),
    background        = Color(0xFF201100),
    surface           = Color(0xFF2E1800),
    onBackground      = Color(0xFFFFDBC9),
    onSurface         = Color(0xFFFFDBC9),
    error             = ErrorRed,
    outline           = Color(0xFFD49178)
)

// ── Forest Green ─────────────────────────────────────────────────────────────
private val ForestLight = lightColorScheme(
    primary           = Color(0xFF2D6A4F),
    onPrimary         = Color(0xFFFFFFFF),
    primaryContainer  = Color(0xFFB7E4C7),
    onPrimaryContainer= Color(0xFF082016),
    secondary         = Color(0xFF40916C),
    onSecondary       = Color(0xFFFFFFFF),
    secondaryContainer= Color(0xFFD8F3DC),
    onSecondaryContainer = Color(0xFF082016),
    background        = Color(0xFFF4FCF4),
    surface           = Color(0xFFF4FCF4),
    onBackground      = Color(0xFF0A1F14),
    onSurface         = Color(0xFF0A1F14),
    error             = ErrorRed,
    outline           = Color(0xFF74C69D)
)
private val ForestDark = darkColorScheme(
    primary           = Color(0xFF95D5B2),
    onPrimary         = Color(0xFF082016),
    primaryContainer  = Color(0xFF1B4332),
    onPrimaryContainer= Color(0xFFB7E4C7),
    secondary         = Color(0xFF52B788),
    onSecondary       = Color(0xFF082016),
    secondaryContainer= Color(0xFF1B4332),
    onSecondaryContainer = Color(0xFFD8F3DC),
    background        = Color(0xFF0A1F14),
    surface           = Color(0xFF0F2D1E),
    onBackground      = Color(0xFFB7E4C7),
    onSurface         = Color(0xFFB7E4C7),
    error             = ErrorRed,
    outline           = Color(0xFF52B788)
)

// ── Midnight Blue ────────────────────────────────────────────────────────────
private val MidnightLight = lightColorScheme(
    primary           = Color(0xFF1565C0),
    onPrimary         = Color(0xFFFFFFFF),
    primaryContainer  = Color(0xFFD3E4FF),
    onPrimaryContainer= Color(0xFF001C3F),
    secondary         = Color(0xFF7B61FF),
    onSecondary       = Color(0xFFFFFFFF),
    secondaryContainer= Color(0xFFEADDFF),
    onSecondaryContainer = Color(0xFF22005D),
    background        = Color(0xFFF5F6FF),
    surface           = Color(0xFFF5F6FF),
    onBackground      = Color(0xFF001C3F),
    onSurface         = Color(0xFF001C3F),
    error             = ErrorRed,
    outline           = Color(0xFF738BC0)
)
private val MidnightDark = darkColorScheme(
    primary           = Color(0xFFA8C8FF),
    onPrimary         = Color(0xFF003063),
    primaryContainer  = Color(0xFF004898),
    onPrimaryContainer= Color(0xFFD3E4FF),
    secondary         = Color(0xFFCFBDFF),
    onSecondary       = Color(0xFF37009E),
    secondaryContainer= Color(0xFF4F00D7),
    onSecondaryContainer = Color(0xFFEADDFF),
    background        = Color(0xFF001130),
    surface           = Color(0xFF001A48),
    onBackground      = Color(0xFFD3E4FF),
    onSurface         = Color(0xFFD3E4FF),
    error             = ErrorRed,
    outline           = Color(0xFF4F7BAD)
)

// ── Coral Sunset ─────────────────────────────────────────────────────────────
private val CoralLight = lightColorScheme(
    primary           = Color(0xFFB54A00),
    onPrimary         = Color(0xFFFFFFFF),
    primaryContainer  = Color(0xFFFFDBCB),
    onPrimaryContainer= Color(0xFF3C1400),
    secondary         = Color(0xFF8B4A6A),
    onSecondary       = Color(0xFFFFFFFF),
    secondaryContainer= Color(0xFFFFD8E9),
    onSecondaryContainer = Color(0xFF3A0025),
    background        = Color(0xFFFFFBFF),
    surface           = Color(0xFFFFFBFF),
    onBackground      = Color(0xFF201100),
    onSurface         = Color(0xFF201100),
    error             = ErrorRed,
    outline           = Color(0xFFAC7B67)
)
private val CoralDark = darkColorScheme(
    primary           = Color(0xFFFFB591),
    onPrimary         = Color(0xFF5F1B00),
    primaryContainer  = Color(0xFF872B00),
    onPrimaryContainer= Color(0xFFFFDBCB),
    secondary         = Color(0xFFFFAFD1),
    onSecondary       = Color(0xFF57103A),
    secondaryContainer= Color(0xFF712951),
    onSecondaryContainer = Color(0xFFFFD8E9),
    background        = Color(0xFF201100),
    surface           = Color(0xFF2D160A),
    onBackground      = Color(0xFFFFDBCB),
    onSurface         = Color(0xFFFFDBCB),
    error             = ErrorRed,
    outline           = Color(0xFFD4978B)
)

fun colorSchemeForTheme(theme: AppTheme, dark: Boolean) = when (theme) {
    AppTheme.TEAL     -> if (dark) DarkColors else LightColors
    AppTheme.SAFFRON  -> if (dark) SaffronDark else SaffronLight
    AppTheme.FOREST   -> if (dark) ForestDark else ForestLight
    AppTheme.MIDNIGHT -> if (dark) MidnightDark else MidnightLight
    AppTheme.CORAL    -> if (dark) CoralDark else CoralLight
}

// Re-export original teal schemes so Theme.kt still compiles
internal val LightColors = lightColorScheme(
    primary           = Teal600,
    onPrimary         = NeutralWhite,
    primaryContainer  = TealContainer,
    onPrimaryContainer= Teal900,
    secondary         = Gold600,
    onSecondary       = NeutralBlack,
    secondaryContainer= GoldContainer,
    onSecondaryContainer = Gold400,
    tertiary          = WarningOrange,
    onTertiary        = NeutralWhite,
    background        = NeutralWhite,
    onBackground      = NeutralBlack,
    surface           = NeutralWhite,
    onSurface         = NeutralBlack,
    surfaceVariant    = NeutralGray100,
    onSurfaceVariant  = NeutralGray800,
    outline           = NeutralGray400,
    error             = ErrorRed,
    onError           = NeutralWhite,
    errorContainer    = ErrorRedContainer,
    onErrorContainer  = ErrorRed,
    scrim             = Scrim
)
internal val DarkColors = darkColorScheme(
    primary           = Teal200,
    onPrimary         = Teal900,
    primaryContainer  = Teal700,
    onPrimaryContainer= TealContainer,
    secondary         = Gold200,
    onSecondary       = NeutralBlack,
    secondaryContainer= Gold500,
    onSecondaryContainer = GoldContainer,
    tertiary          = WarningOrange,
    onTertiary        = NeutralWhite,
    background        = DarkBackground,
    onBackground      = NeutralGray100,
    surface           = DarkSurface,
    onSurface         = NeutralGray100,
    surfaceVariant    = DarkSurfaceVariant,
    onSurfaceVariant  = NeutralGray400,
    outline           = DarkOutline,
    error             = ErrorRed,
    onError           = NeutralWhite,
    errorContainer    = ErrorRed,
    onErrorContainer  = ErrorRedContainer,
    scrim             = Scrim
)
