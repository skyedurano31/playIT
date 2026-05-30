package com.playit.app.domain.model

data class TileItem(
    val letter: String,
    val isDistractor: Boolean = false,
    val id: Int = 0,
    val isPlaced: Boolean = false  // Add this

)