package com.playit.app.domain.model

data class SlotItem(
    val position: Int,
    val letter: String = "",
    val isLocked: Boolean = false,
    val isFilled: Boolean = false
)