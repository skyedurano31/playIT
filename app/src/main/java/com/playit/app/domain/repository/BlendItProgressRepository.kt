package com.playit.app.domain.repository

import com.playit.app.data.local.entity.BlendItProgress

interface BlendItProgressRepository {
    suspend fun saveProgress(
        profileId: Int,
        groupId: Int,
        starsEarned: Int,
        heartsLost: Int,
        isCompleted: Boolean
    )
    suspend fun getProgress(profileId: Int, groupId: Int): BlendItProgress?
    suspend fun getAllProgress(profileId: Int): List<BlendItProgress>
    suspend fun isGroupCompleted(profileId: Int, groupId: Int): Boolean
}