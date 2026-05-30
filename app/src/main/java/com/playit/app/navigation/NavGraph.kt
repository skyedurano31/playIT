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
import com.playit.app.presentation.blendit.BlendItCompleteScreen
import com.playit.app.presentation.blendit.BlendItScreen
import com.playit.app.presentation.common.SplashScreen
import com.playit.app.presentation.dashboard.ParentDashboardScreen
import com.playit.app.presentation.findit.FindItScreen
import com.playit.app.presentation.findit.LetterCompleteScreen
import com.playit.app.presentation.hearit.HearItScreen
import com.playit.app.presentation.map.MapScreen
import com.playit.app.presentation.profile.NamePromptScreen
import com.playit.app.presentation.sayit.SayItScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // Splash
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

        // Name prompt
        composable(Screen.NamePrompt.route) {
            NamePromptScreen(
                onProfileCreated = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.NamePrompt.route) { inclusive = true }
                    }
                }
            )
        }

        //map
        composable(Screen.Map.route) {
            MapScreen(
                onLetterSelected = { phonemeId ->
                    navController.navigate(Screen.HearIt.createRoute(phonemeId))
                },
                onBlendItSelected = { groupId ->
                    navController.navigate(Screen.BlendIt.createRoute(groupId))
                },
                onDashboardClicked = {
                    navController.navigate(Screen.Dashboard.route)
                }
            )
        }

        // Hear It
        composable(
            route = Screen.HearIt.route,
            arguments = listOf(
                navArgument("phonemeId") { type = NavType.IntType }
            )
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

        // Say It
        composable(
            route = Screen.SayIt.route,
            arguments = listOf(
                navArgument("phonemeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val phonemeId = backStackEntry.arguments?.getInt("phonemeId") ?: 1
            SayItScreen(
                phonemeId = phonemeId,
                onNext = { id ->
                    navController.navigate(Screen.FindIt.createRoute(id))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Find It
        composable(
            route = Screen.FindIt.route,
            arguments = listOf(
                navArgument("phonemeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val phonemeId = backStackEntry.arguments?.getInt("phonemeId") ?: 1
            FindItScreen(
                phonemeId = phonemeId,
                onComplete = { id, stars ->
                    navController.navigate(
                        Screen.LetterComplete.createRoute(id) + "?stars=$stars"
                    ) {
                        // clear hear it, say it, find it from back stack
                        popUpTo(Screen.Map.route) { inclusive = false }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Letter Complete
        composable(
            route = Screen.LetterComplete.route + "?stars={stars}",
            arguments = listOf(
                navArgument("phonemeId") { type = NavType.IntType },
                navArgument("stars") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val phonemeId = backStackEntry.arguments?.getInt("phonemeId") ?: 1
            val stars = backStackEntry.arguments?.getInt("stars") ?: 1
            LetterCompleteScreen(
                phonemeId = phonemeId,
                starsEarned = stars,
                onContinue = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Map.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            ParentDashboardScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.BlendIt.route,
            arguments = listOf(
                navArgument("groupId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: 1
            BlendItScreen(
                groupId = groupId,
                onComplete = { id, stars ->
                    navController.navigate(
                        Screen.BlendItComplete.createRoute(id) + "?stars=$stars"
                    ) {
                        popUpTo(Screen.Map.route) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.BlendItComplete.route + "?stars={stars}",
            arguments = listOf(
                navArgument("groupId") { type = NavType.IntType },
                navArgument("stars") { type = NavType.IntType; defaultValue = 1 }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: 1
            val stars = backStackEntry.arguments?.getInt("stars") ?: 1
            BlendItCompleteScreen(
                groupId = groupId,
                starsEarned = stars,
                onContinue = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Map.route) { inclusive = true }
                    }
                }
            )
        }
    }
}