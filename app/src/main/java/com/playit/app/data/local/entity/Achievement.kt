package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement")
data class Achievement(
    @PrimaryKey(autoGenerate = true) val achievementId: Int = 0,
    val profileId: Int,
    val title: String,
    val isUnlocked: Int = 0,
    val unlockedAt: Long = 0L
)