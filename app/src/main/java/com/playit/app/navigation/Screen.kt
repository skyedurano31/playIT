package com.playit.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object NamePrompt : Screen("name_prompt")
    object Map : Screen("map")
    object HearIt : Screen("hear_it/{phonemeId}") {
        fun createRoute(phonemeId: Int) = "hear_it/$phonemeId"
    }
    object SayIt : Screen("say_it/{phonemeId}") {
        fun createRoute(phonemeId: Int) = "say_it/$phonemeId"
    }
    object FindIt : Screen("find_it/{phonemeId}") {
        fun createRoute(phonemeId: Int) = "find_it/$phonemeId"
    }
    object LetterComplete : Screen("letter_complete/{phonemeId}") {
        fun createRoute(phonemeId: Int) = "letter_complete/$phonemeId"
    }
    object Dashboard : Screen("dashboard")
}