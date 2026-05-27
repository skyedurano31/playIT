package com.playit.app.domain.model

data class GridItem(
    val phonemeId: Int,
    val letter: String,
    val imagePath: String,
    val word: String,
    val isTarget: Boolean
)