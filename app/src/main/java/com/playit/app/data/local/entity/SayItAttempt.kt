package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "say_it_attempt")
data class SayItAttempt(
    @PrimaryKey(autoGenerate = true) val attemptId: Int = 0,
    val profileId: Int,
    val phonemeId: Int,
    val isCorrect: Int,
    val attemptedAt: Long
)