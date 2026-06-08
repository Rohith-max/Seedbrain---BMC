package com.nidhi.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(NavRoutes.HOME, "Home", Icons.Default.Home)
    data object Documents : BottomNavItem(NavRoutes.DOCUMENTS, "Docs", Icons.Default.Description)
    data object Benefits : BottomNavItem(NavRoutes.BENEFITS, "Benefits", Icons.Default.LocalOffer)
    data object AiChat : BottomNavItem(NavRoutes.AI_CHAT, "NIDHI AI", Icons.Default.Chat)
    data object Settings : BottomNavItem(NavRoutes.SETTINGS, "Settings", Icons.Default.Settings)

    companion object {
        val items = listOf(Home, Documents, Benefits, AiChat, Settings)
    }
}
