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

    override suspend fun saveFindItProgress(
        profileId: Int,
        phonemeId: Int,
        starsEarned: Int,
        heartsLost: Int
    ) {
        val existing = lessonProgressDao.getProgress(profileId, phonemeId)
        if (existing != null) {
            lessonProgressDao.update(
                existing.copy(
                    starsEarned = starsEarned,
                    heartsLost = heartsLost,
                    isCompleted = 1,
                    completedAt = System.currentTimeMillis()
                )
            )
        } else {
            lessonProgressDao.insert(
                LessonProgress(
                    profileId = profileId,
                    phonemeId = phonemeId,
                    starsEarned = starsEarned,
                    heartsLost = heartsLost,
                    isCompleted = 1,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun getAllCompletedCount(profileId: Int): Int {
        return lessonProgressDao.getAllCompletedCount(profileId)
    }
}