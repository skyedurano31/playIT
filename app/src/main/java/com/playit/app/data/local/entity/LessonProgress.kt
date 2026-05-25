package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_progress")
data class LessonProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileId: Int,
    val phonemeId: Int,
    val starsEarned: Int = 0,
    val heartsLost: Int = 0,
    val isCompleted: Int = 0,
    val completedAt: Long = 0L
)