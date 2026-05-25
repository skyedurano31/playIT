package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blend_it_progress")
data class BlendItProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileId: Int,
    val groupId: Int,
    val starsEarned: Int = 0,
    val heartsLost: Int = 0,
    val isCompleted: Int = 0,
    val completedAt: Long = 0L
)