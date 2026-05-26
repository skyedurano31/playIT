package com.playit.app.domain.repository

import com.playit.app.data.local.entity.LessonProgress

interface LessonProgressRepository {
    suspend fun insert(lessonProgress: LessonProgress)
    suspend fun update(lessonProgress: LessonProgress)
    suspend fun getProgressByProfile(profileId: Int): List<LessonProgress>
    suspend fun getProgress(profileId: Int, phonemeId: Int): LessonProgress?
    suspend fun isCompleted(profileId: Int, phonemeId: Int): Boolean
}