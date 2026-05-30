package com.playit.app.data.repository

import com.playit.app.data.local.dao.BlendItAttemptDao
import com.playit.app.data.local.entity.BlendItAttempt
import com.playit.app.domain.repository.BlendItAttemptRepository
import javax.inject.Inject

class BlendItAttemptRepositoryImpl @Inject constructor(
    private val blendItAttemptDao: BlendItAttemptDao
) : BlendItAttemptRepository {

    override suspend fun saveAttempt(
        profileId: Int,
        groupId: Int,
        wordId: Int,
        isCorrect: Boolean,
        attemptedAt: Long
    ) {
        blendItAttemptDao.insert(
            BlendItAttempt(
                profileId = profileId,
                groupId = groupId,
                wordId = wordId,
                isCorrect = if (isCorrect) 1 else 0,
                attemptedAt = attemptedAt
            )
        )
    }

    override suspend fun getAttempts(profileId: Int, groupId: Int): List<BlendItAttempt> {
        return blendItAttemptDao.getAttempts(profileId, groupId)
    }
}