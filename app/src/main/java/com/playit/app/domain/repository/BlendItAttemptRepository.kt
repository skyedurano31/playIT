package com.playit.app.domain.repository

import com.playit.app.data.local.entity.BlendItAttempt

interface BlendItAttemptRepository {
    suspend fun saveAttempt(
        profileId: Int,
        groupId: Int,
        wordId: Int,
        isCorrect: Boolean,
        attemptedAt: Long
    )
    suspend fun getAttempts(profileId: Int, groupId: Int): List<BlendItAttempt>
}