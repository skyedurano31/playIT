package com.playit.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.playit.app.presentation.common.SplashScreen
import com.playit.app.presentation.profile.NamePromptScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNoProfile = {
                    navController.navigate(Screen.NamePrompt.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onProfileExists = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.NamePrompt.route) {
            NamePromptScreen(
                onProfileCreated = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.NamePrompt.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Map.route) {
            // MapScreen goes here — placeholder for now
        }
    }
}