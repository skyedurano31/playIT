package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "letter_group_member",
    foreignKeys = [
        ForeignKey(
            entity = LetterGroup::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
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
        Index(value = ["groupId"]),
        Index(value = ["phonemeId"])
    ]
)
data class LetterGroupMember(
    @PrimaryKey(autoGenerate = true) val memberId: Int = 0,
    val groupId: Int,
    val phonemeId: Int,
    val position: Int
)