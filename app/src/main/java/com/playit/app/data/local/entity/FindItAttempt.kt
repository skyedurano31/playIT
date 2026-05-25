package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "find_it_attempt",
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
        ),
        ForeignKey(
            entity = Phoneme::class,
            parentColumns = ["phonemeId"],
            childColumns = ["selectedPhonemeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["phonemeId"]),
        Index(value = ["selectedPhonemeId"])
    ]
)
data class FindItAttempt(
    @PrimaryKey(autoGenerate = true) val attemptId: Int = 0,
    val profileId: Int,
    val phonemeId: Int,
    val selectedPhonemeId: Int,
    val isCorrect: Int,
    val attemptedAt: Long
)