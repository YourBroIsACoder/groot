// File: app/src/main/java/com/example/groot/navigation/Screen.kt

package com.example.groot.navigation

import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Chat // Make sure this import is not commented out
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // These are the properties that child objects can override.
    open val title: String? = null
    open val icon: ImageVector? = null

    // For bottom bar items, we override the properties.
    object Home : Screen("home") {
        override val title: String = "Home"
        override val icon: ImageVector = Icons.Default.Home
    }

    object Chat : Screen("chat") {
        override val title: String = "Chatbot"
        override val icon: ImageVector = Icons.Default.Phone
    }

    // For the detail screen, we don't need to override title or icon,
    // as they will correctly be null by default from the parent class.
    object Detail : Screen("detail/{plantId}") {
        fun createRoute(plantId: Int) = "detail/$plantId"
    }
}