package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "find_it_attempt")
data class FindItAttempt(
    @PrimaryKey(autoGenerate = true) val attemptId: Int = 0,
    val profileId: Int,
    val phonemeId: Int,
    val selectedPhonemeId: Int,
    val isCorrect: Int,
    val attemptedAt: Long
)