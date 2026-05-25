package com.playit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "report_log")
data class ReportLog(
    @PrimaryKey(autoGenerate = true) val reportId: Int = 0,
    val profileId: Int,
    val filePath: String,
    val generatedAt: Long
)
