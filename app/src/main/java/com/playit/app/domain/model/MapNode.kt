package com.playit.app.domain.model

sealed class MapNode {
    data class LetterNode(
        val phonemeId: Int,
        val letter: String,
        val isUnlocked: Boolean,
        val starsEarned: Int
    ) : MapNode()
}