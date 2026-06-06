package com.nidhi.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * "Documents context active" chip shown in the AI chat screen when document context
 * has been injected into the system prompt (Req 9.5).
 * Hidden automatically when docContextActive is false.
 */
@Composable
fun DocumentsContextChip(
    docContextActive: Boolean,
    modifier: Modifier = Modifier
) {
    if (!docContextActive) return
    AssistChip(
        onClick = {},
        label = { Text("Documents context active", style = MaterialTheme.typography.labelSmall) },
        leadingIcon = {
            Icon(
                Icons.Default.Description,
                contentDescription = null
            )
        },
        modifier = modifier,
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}
