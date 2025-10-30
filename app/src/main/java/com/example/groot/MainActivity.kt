package com.example.groot

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.groot.navigation.Screen
import com.example.groot.ui.screens.*
import com.example.groot.ui.theme.GardeningNurseryTheme
import com.example.groot.utils.NotificationHelper // <-- IMPORT YOUR HELPER

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- 1. CREATE NOTIFICATION CHANNEL ---
        NotificationHelper.createNotificationChannel(this)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                mainViewModel.isLoggedIn.value == null
            }
        }

        setContent {
            GardeningNurseryTheme {

                // --- 2. REQUEST NOTIFICATION PERMISSION ---
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted -> /* You can handle denial here if you want */ }
                    )
                    LaunchedEffect(Unit) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

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
    val cartViewModel: CartViewModel = viewModel()
    val cartItems by cartViewModel.cartItems.collectAsState()

    // I noticed you had both Assistant and Chat. Let's assume you want Assistant.
    // If you still have both, just add Screen.Chat back to this list.
    val navItems = listOf(Screen.Home, Screen.Assistant, Screen.Cart,Screen.Chat)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
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
                    // Assuming you want the FAB to go to the new Assistant, not the old Chat
                    onChatClick = {
                        navController.navigate(Screen.Assistant.route)
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
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = Screen.Detail.route) { navBackStackEntry ->
                val plantId = navBackStackEntry.arguments?.getString("plantId")
                if (plantId != null) {
                    PlantDetailScreen(
                        plantId = plantId,
                        cartViewModel = cartViewModel,
                        onNavigateUp = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}