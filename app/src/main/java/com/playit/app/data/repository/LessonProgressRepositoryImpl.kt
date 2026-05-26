package com.playit.app.data.repository

import com.playit.app.data.local.dao.LessonProgressDao
import com.playit.app.data.local.entity.LessonProgress
import com.playit.app.domain.repository.LessonProgressRepository
import javax.inject.Inject

class LessonProgressRepositoryImpl @Inject constructor(
    private val lessonProgressDao: LessonProgressDao
) : LessonProgressRepository {
    override suspend fun insert(lessonProgress: LessonProgress) {
        lessonProgressDao.insert(lessonProgress)
    }

    override suspend fun update(lessonProgress: LessonProgress) {
        lessonProgressDao.update(lessonProgress)
    }

    override suspend fun getProgressByProfile(profileId: Int): List<LessonProgress> {
        return lessonProgressDao.getProgressByProfile(profileId)
    }

    override suspend fun getProgress(profileId: Int, phonemeId: Int): LessonProgress? {
        return lessonProgressDao.getProgress(profileId, phonemeId)
    }

    override suspend fun isCompleted(profileId: Int, phonemeId: Int): Boolean {
        return (lessonProgressDao.isCompleted(profileId, phonemeId) ?: 0) == 1
    }
    
    override suspend fun saveHearItComplete(profileId: Int, phonemeId: Int) {
        val existing = lessonProgressDao.getProgress(profileId, phonemeId)
        if (existing == null) {
            lessonProgressDao.insert(
                LessonProgress(
                    profileId = profileId,
                    phonemeId = phonemeId,
                    isCompleted = 0
                )
            )
        }
    }
}