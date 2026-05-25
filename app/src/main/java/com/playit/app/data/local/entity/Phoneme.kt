package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phoneme")
data class Phoneme(
    @PrimaryKey(autoGenerate = true) val phonemeId: Int = 0,
    val letter: String,
    val audioPath: String,
    val imagePath: String,
    val exampleWord: String
)