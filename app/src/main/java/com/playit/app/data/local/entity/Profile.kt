package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey(autoGenerate = true) val profileId: Int = 0,
    val name: String,
    val avatarResId: Int,
    val totalStars: Int = 0,
    val currentStreak: Int = 0,
    val lastPlayedAt: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)