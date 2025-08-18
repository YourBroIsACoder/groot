package com.example.groot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.groot.navigation.Screen
import com.example.groot.ui.screens.ChatBotScreen
import com.example.groot.ui.screens.HomeScreen
import com.example.groot.ui.screens.LoginScreen
import com.example.groot.ui.screens.PlantDetailScreen
import com.example.groot.ui.screens.samplePlants
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
                        false -> LoginScreen(onLoginSuccess = {})
                        null -> Box(
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

@Composable
fun MainAppScaffold() {
    val navController = rememberNavController()
    // Make sure Home and Chat have non-null icons and titles
    val navItems = listOf(Screen.Home, Screen.Chat).filter { it.icon != null && it.title != null }

    Scaffold(
        bottomBar = {
            // ... (bottom bar code is the same) ...
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Updated Home route
            composable(Screen.Home.route) {
                HomeScreen(onPlantClick = { plantId ->
                    navController.navigate(Screen.Detail.createRoute(plantId))
                })
            }

            composable(Screen.Chat.route) { ChatBotScreen() }

            // Add the new destination for the detail screen
            composable(route = Screen.Detail.route) { navBackStackEntry ->
                // Extract the plantId from the route
                val plantId = navBackStackEntry.arguments?.getString("plantId")?.toIntOrNull()

                // Find the plant from our sample data
                val plant = samplePlants.find { it.id == plantId }

                // If the plant is found, show the detail screen
                if (plant != null) {
                    PlantDetailScreen(
                        plant = plant,
                        onNavigateUp = { navController.navigateUp() } // Handle the back arrow click
                    )
                } else {
                    // Optionally, show a "Not Found" screen or navigate back
                    // For now, we'll just navigate back if the ID is invalid
                    navController.navigateUp()
                }
            }
        }
    }
}