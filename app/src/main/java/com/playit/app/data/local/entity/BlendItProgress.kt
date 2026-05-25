package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "blend_it_progress",
    foreignKeys = [
        ForeignKey(
            entity = Profile::class,
            parentColumns = ["profileId"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LetterGroup::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["groupId"])
    ]
)
data class BlendItProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileId: Int,
    val groupId: Int,
    val starsEarned: Int = 0,
    val heartsLost: Int = 0,
    val isCompleted: Int = 0,
    val completedAt: Long = 0L
)