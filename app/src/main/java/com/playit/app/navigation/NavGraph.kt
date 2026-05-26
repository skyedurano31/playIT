package com.playit.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.playit.app.presentation.common.SplashScreen
import com.playit.app.presentation.hearit.HearItScreen
import com.playit.app.presentation.map.MapScreen
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
            MapScreen(
                onLetterSelected = { phonemeId ->
                    navController.navigate(Screen.HearIt.createRoute(phonemeId))
                }
            )
        }

        composable(
            route = Screen.HearIt.route,
            arguments = listOf(navArgument("phonemeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val phonemeId = backStackEntry.arguments?.getInt("phonemeId") ?: 1
            HearItScreen(
                phonemeId = phonemeId,
                onNext = { id ->
                    navController.navigate(Screen.SayIt.createRoute(id))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

// SayIt placeholder so it does not crash
        composable(
            route = Screen.SayIt.route,
            arguments = listOf(navArgument("phonemeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val phonemeId = backStackEntry.arguments?.getInt("phonemeId") ?: 1
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Say It — phonemeId: $phonemeId")
            }
        }

    }
}