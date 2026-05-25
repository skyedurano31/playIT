package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blend_it_attempt")
data class BlendItAttempt(
    @PrimaryKey(autoGenerate = true) val attemptId: Int = 0,
    val profileId: Int,
    val groupId: Int,
    val wordId: Int,
    val isCorrect: Int,
    val attemptedAt: Long
)