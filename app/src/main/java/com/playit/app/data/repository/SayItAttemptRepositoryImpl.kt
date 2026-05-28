package com.playit.app.data.repository

import com.playit.app.data.local.dao.SayItAttemptDao
import com.playit.app.data.local.entity.SayItAttempt
import com.playit.app.domain.repository.SayItAttemptRepository
import javax.inject.Inject

class SayItAttemptRepositoryImpl @Inject constructor(
    private val sayItAttemptDao: SayItAttemptDao
) : SayItAttemptRepository {

    override suspend fun saveAttempt(
        profileId: Int,
        phonemeId: Int,
        isCorrect: Boolean,
        attemptedAt: Long
    ) {
        sayItAttemptDao.insert(
            SayItAttempt(
                profileId = profileId,
                phonemeId = phonemeId,
                isCorrect = if (isCorrect) 1 else 0,
                attemptedAt = attemptedAt
            )
        )
    }

    override suspend fun getAttempts(
        profileId: Int,
        phonemeId: Int
    ): List<SayItAttempt> {
        return sayItAttemptDao.getAttempts(profileId, phonemeId)
    }

    override suspend fun getCorrectCount(profileId: Int, phonemeId: Int): Int {
        return sayItAttemptDao.getCorrectCount(profileId, phonemeId)
    }

    override suspend fun getTotalCount(profileId: Int, phonemeId: Int): Int {
        return sayItAttemptDao.getTotalCount(profileId, phonemeId)
    }
}