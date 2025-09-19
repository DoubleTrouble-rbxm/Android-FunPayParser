package com.example.funpayparser

import android.annotation.SuppressLint
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Settings : Screen("settings")
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavHost(application)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavHost(application: Application) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            Scaffold (
                topBar = {
                    MainTopBar(
                        title = "Главная",
                        showBack = false,
                        onBackClick = {},
                        settingsClick = { navController.navigate(Screen.Settings.route) }
                    )

                }
            ) { paddingValues ->
                MainScreen(application = application, padding = paddingValues)
            }
        }

        composable(Screen.Settings.route) {
            Scaffold (
                topBar = {
                    MainTopBar(
                        title = "Настройки",
                        showBack = true,
                        onBackClick = { navController.navigate(Screen.Main.route) },
                        settingsClick = {}
                    )
                }

            ) { paddingValues ->
                SettingsScreen(paddingValues)
            }
        }
    }
}