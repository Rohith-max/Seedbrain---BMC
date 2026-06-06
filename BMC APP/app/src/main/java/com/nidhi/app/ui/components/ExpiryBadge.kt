package com.nidhi.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Colour-coded expiry badge (Req 13.5, 20.7).
 * Uses BOTH a colour AND a visible text label so status is not conveyed by colour alone.
 *
 *  ≤ 30 days  → error colour   + "Expires soon"
 *  31–90 days → tertiary colour + "Expiring"
 *  > 90 days  → primary colour  + "Valid"
 */
@Composable
fun ExpiryBadge(expiryDate: Long) {
    val localExpiry = Instant.ofEpochMilli(expiryDate)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), localExpiry)

    val (containerColor, label) = when {
        daysUntil <= 30  -> MaterialTheme.colorScheme.errorContainer to "Expires soon"
        daysUntil <= 90  -> MaterialTheme.colorScheme.tertiaryContainer to "Expiring"
        else             -> MaterialTheme.colorScheme.primaryContainer to "Valid"
    }
    val contentColor = when {
        daysUntil <= 30  -> MaterialTheme.colorScheme.onErrorContainer
        daysUntil <= 90  -> MaterialTheme.colorScheme.onTertiaryContainer
        else             -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    AssistChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        leadingIcon = {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor,
            leadingIconContentColor = contentColor
        )
    )
}
