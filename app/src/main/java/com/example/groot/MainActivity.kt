package com.example.groot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.groot.navigation.Screen
import com.example.groot.ui.screens.*
import com.example.groot.ui.theme.GardeningNurseryTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                mainViewModel.isLoggedIn.value == null
            }
        }

        super.onCreate(savedInstanceState)
        setContent {
            GardeningNurseryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()

                    when (isLoggedIn) {
                        true -> MainAppScaffold()
                        false -> LoginScreen()
                        null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold() {
    val navController = rememberNavController()

    // --- CART VIEWMODEL INTEGRATION ---
    // Get an instance of the CartViewModel. It will be shared across all screens
    // that are part of this NavHost, keeping the cart state intact.
    val cartViewModel: CartViewModel = viewModel()
    val cartItems by cartViewModel.cartItems.collectAsState()

    // Add the new Cart screen to the list of bottom navigation bar items
    val navItems = listOf(Screen.Home, Screen.Assistant, Screen.Chat, Screen.Cart)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            // Use a BadgedBox to show the number of items in the cart
                            if (screen.route == Screen.Cart.route) {
                                BadgedBox(badge = {
                                    if (cartItems.isNotEmpty()) {
                                        Badge { Text("${cartItems.size}") }
                                    }
                                }) {
                                    Icon(screen.icon!!, contentDescription = screen.title)
                                }
                            } else {
                                Icon(screen.icon!!, contentDescription = screen.title)
                            }
                        },
                        label = { Text(screen.title!!) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onPlantClick = { plantId ->
                        navController.navigate(Screen.Detail.createRoute(plantId))
                    },
                    onChatClick = {
                        navController.navigate(Screen.Chat.route)
                    }
                )
            }
            composable(Screen.Assistant.route) {
                AssistantScreen()
            }
            composable(Screen.Cart.route) {
                CartScreen(
                    cartViewModel = cartViewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            // Clear the back stack so the user can't go back to the cart after ordering
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Chat.route) { ChatBotScreen() }

            composable(route = Screen.Detail.route) { navBackStackEntry ->
                val plantId = navBackStackEntry.arguments?.getString("plantId")
                if (plantId != null) {
                    PlantDetailScreen(
                        plantId = plantId,
                        cartViewModel = cartViewModel, // Pass the shared ViewModel
                        onNavigateUp = { navController.navigateUp() }
                    )}}}}}
