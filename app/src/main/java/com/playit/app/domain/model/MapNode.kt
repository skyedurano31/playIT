package com.playit.app.domain.model

sealed class MapNode {
    data class LetterNode(
        val phonemeId: Int,
        val letter: String,
        val isUnlocked: Boolean,
        val starsEarned: Int
    ) : MapNode()

    data class BlendItNode(
        val groupId: Int,
        val groupNumber: Int,
        val isUnlocked: Boolean,
        val starsEarned: Int
    ) : MapNode()
}