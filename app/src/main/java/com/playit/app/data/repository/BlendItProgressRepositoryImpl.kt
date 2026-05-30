package com.playit.app.data.repository

import com.playit.app.data.local.dao.BlendItProgressDao
import com.playit.app.data.local.entity.BlendItProgress
import com.playit.app.domain.repository.BlendItProgressRepository
import javax.inject.Inject

class BlendItProgressRepositoryImpl @Inject constructor(
    private val blendItProgressDao: BlendItProgressDao
) : BlendItProgressRepository {

    override suspend fun saveProgress(
        profileId: Int,
        groupId: Int,
        starsEarned: Int,
        heartsLost: Int,
        isCompleted: Boolean
    ) {
        val existing = blendItProgressDao.getProgress(profileId, groupId)
        if (existing != null) {
            blendItProgressDao.update(
                existing.copy(
                    starsEarned = starsEarned,
                    heartsLost = heartsLost,
                    isCompleted = if (isCompleted) 1 else 0,
                    completedAt = System.currentTimeMillis()
                )
            )
        } else {
            blendItProgressDao.insert(
                BlendItProgress(
                    profileId = profileId,
                    groupId = groupId,
                    starsEarned = starsEarned,
                    heartsLost = heartsLost,
                    isCompleted = if (isCompleted) 1 else 0,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
    }

    override suspend fun getProgress(profileId: Int, groupId: Int): BlendItProgress? {
        return blendItProgressDao.getProgress(profileId, groupId)
    }

    override suspend fun getAllProgress(profileId: Int): List<BlendItProgress> {
        return blendItProgressDao.getAllProgress(profileId)
    }

    override suspend fun isGroupCompleted(profileId: Int, groupId: Int): Boolean {
        return (blendItProgressDao.isGroupCompleted(profileId, groupId) ?: 0) == 1
    }
}