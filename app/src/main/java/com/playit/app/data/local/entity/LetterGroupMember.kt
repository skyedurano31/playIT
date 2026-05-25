package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "letter_group_member")
data class LetterGroupMember(
    @PrimaryKey(autoGenerate = true) val memberId: Int = 0,
    val groupId: Int,
    val phonemeId: Int,
    val position: Int
)