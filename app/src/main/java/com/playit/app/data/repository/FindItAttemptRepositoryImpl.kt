package com.playit.app.data.repository

import com.playit.app.data.local.dao.FindItAttemptDao
import com.playit.app.data.local.entity.FindItAttempt
import com.playit.app.domain.repository.FindItAttemptRepository
import javax.inject.Inject

class FindItAttemptRepositoryImpl @Inject constructor(
    private val findItAttemptDao: FindItAttemptDao
) : FindItAttemptRepository {

    override suspend fun saveAttempt(
        profileId: Int,
        phonemeId: Int,
        selectedPhonemeId: Int,
        isCorrect: Boolean,
        attemptedAt: Long
    ) {
        findItAttemptDao.insert(
            FindItAttempt(
                profileId = profileId,
                phonemeId = phonemeId,
                selectedPhonemeId = selectedPhonemeId,
                isCorrect = if (isCorrect) 1 else 0,
                attemptedAt = attemptedAt
            )
        )
    }

    override suspend fun getAttempts(
        profileId: Int,
        phonemeId: Int
    ): List<FindItAttempt> {
        return findItAttemptDao.getAttempts(profileId, phonemeId)
    }
}