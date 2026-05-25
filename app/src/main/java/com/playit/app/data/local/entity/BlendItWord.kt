package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blend_it_word")
data class BlendItWord(
    @PrimaryKey(autoGenerate = true) val wordId: Int = 0,
    val groupId: Int,
    val word: String,
    val wordPattern: String,
    val audioPath: String,
    val imagePath: String
)