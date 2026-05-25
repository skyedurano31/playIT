package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "say_it_attempt",
    foreignKeys = [
        ForeignKey(
            entity = Profile::class,
            parentColumns = ["profileId"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Phoneme::class,
            parentColumns = ["phonemeId"],
            childColumns = ["phonemeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["phonemeId"])
    ]
)
data class SayItAttempt(
    @PrimaryKey(autoGenerate = true) val attemptId: Int = 0,
    val profileId: Int,
    val phonemeId: Int,
    val isCorrect: Int,
    val attemptedAt: Long
)