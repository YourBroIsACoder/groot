package com.example.groot.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call

import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person

import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Properties for items that will appear in the bottom navigation bar
    open val title: String? = null
    open val icon: ImageVector? = null

    // For bottom bar items, we override the properties.
    object Home : Screen("home") {
        override val title: String = "Home"
        override val icon: ImageVector = Icons.Default.Home
    }

    object Identifier : Screen("identifier") {
        override val title: String = "Identifier"
        override val icon: ImageVector = Icons.Default.Search
    }

    object Chat : Screen("chat") {
        override val title: String = "Chatbot"
        override val icon: ImageVector = Icons.Default.Call
    }

    // The new Cart screen, which is also a bottom bar item
    object Cart : Screen("cart") {
        override val title: String = "Cart"
        override val icon: ImageVector = Icons.Default.ShoppingCart
    }
    // ... inside sealed class Screen
    object Assistant : Screen("assistant") {
        override val title: String = "Assistant"
        // Let's use a more fitting icon
        override val icon: ImageVector = Icons.Default.Person // Or Spa, Grass, Eco...
    }

    // For the detail screen, we don't need a title or icon for the bottom bar.
    // This is a destination we navigate to *from* other screens.
    object Detail : Screen("detail/{plantId}") {
        // Helper function to build the route with the actual plant ID
        fun createRoute(plantId: String) = "detail/$plantId"
    }
}