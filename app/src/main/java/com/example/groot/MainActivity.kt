package com.example.groot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
//import com.example.groot.ml.PlantIdentifierHelper
import com.example.groot.navigation.Screen
import com.example.groot.ui.screens.ChatBotScreen
import com.example.groot.ui.screens.HomeScreen
import com.example.groot.ui.screens.LoginScreen
import com.example.groot.ui.screens.PlantDetailScreen
import com.example.groot.ui.screens.PlantIdentifierScreen
import com.example.groot.ui.screens.samplePlants
import com.example.groot.ui.theme.GardeningNurseryTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            GardeningNurseryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()

                    // Simple if/else based on the in-memory state from the ViewModel
                    if (isLoggedIn) {
                        MainAppScaffold()
                    } else {
                        LoginScreen(
                            onLoginSuccess = { mainViewModel.onLoginSuccess() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppScaffold() {
    val navController = rememberNavController()
    val navItems = listOf(Screen.Home, Screen.Chat, Screen.Identifier).filter { it.icon != null && it.title != null }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = screen.title) },
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
        }
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
                    // Connect the chat click to the chat screen navigation

                )
            }

            composable(Screen.Chat.route) { ChatBotScreen() }
            composable(Screen.Identifier.route) { PlantIdentifierScreen() }


            composable(route = Screen.Detail.route) { navBackStackEntry ->
                val plantId = navBackStackEntry.arguments?.getString("plantId")?.toIntOrNull()
                val plant = samplePlants.find { it.id == plantId }

                if (plant != null) {
                    PlantDetailScreen(
                        plant = plant,
                        onNavigateUp = { navController.navigateUp() }
                    )
                } else {
                    navController.navigateUp()
                }
            }
        }
    }
}