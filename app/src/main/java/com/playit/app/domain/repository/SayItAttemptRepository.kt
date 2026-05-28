package com.playit.app.domain.repository

import com.playit.app.data.local.entity.SayItAttempt

interface SayItAttemptRepository {
    suspend fun saveAttempt(profileId: Int, phonemeId: Int, isCorrect: Boolean, attemptedAt: Long)
    suspend fun getAttempts(profileId: Int, phonemeId: Int): List<SayItAttempt>

    suspend fun getCorrectCount(profileId: Int, phonemeId: Int): Int
    suspend fun getTotalCount(profileId: Int, phonemeId: Int): Int
}