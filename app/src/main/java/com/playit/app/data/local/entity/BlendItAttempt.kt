package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "blend_it_attempt",
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
        ),
        ForeignKey(
            entity = BlendItWord::class,
            parentColumns = ["wordId"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profileId"]),
        Index(value = ["groupId"]),
        Index(value = ["wordId"])
    ]
)
data class BlendItAttempt(
    @PrimaryKey(autoGenerate = true) val attemptId: Int = 0,
    val profileId: Int,
    val groupId: Int,
    val wordId: Int,
    val isCorrect: Int,
    val attemptedAt: Long
)