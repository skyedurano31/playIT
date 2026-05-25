package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "achievement",
    foreignKeys = [
        ForeignKey(
            entity = Profile::class,
            parentColumns = ["profileId"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["profileId"])]
)
data class Achievement(
    @PrimaryKey(autoGenerate = true) val achievementId: Int = 0,
    val profileId: Int,
    val title: String,
    val isUnlocked: Int = 0,
    val unlockedAt: Long = 0L
)