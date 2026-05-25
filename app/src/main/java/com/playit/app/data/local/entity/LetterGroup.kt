package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "letter_group")
data class LetterGroup(
    @PrimaryKey(autoGenerate = true) val groupId: Int = 0,
    val groupNumber: Int
)