package com.playit.app.domain.repository

import com.playit.app.data.local.entity.FindItAttempt

interface FindItAttemptRepository {
    suspend fun saveAttempt(
        profileId: Int,
        phonemeId: Int,
        selectedPhonemeId: Int,
        isCorrect: Boolean,
        attemptedAt: Long
    )
    suspend fun getAttempts(profileId: Int, phonemeId: Int): List<FindItAttempt>
}