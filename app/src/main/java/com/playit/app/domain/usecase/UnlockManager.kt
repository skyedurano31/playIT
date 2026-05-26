package com.playit.app.domain.usecase

import com.playit.app.domain.repository.LessonProgressRepository
import javax.inject.Inject

class UnlockManager @Inject constructor(
    private val lessonProgressRepository: LessonProgressRepository
) {
    suspend fun isUnlocked(profileId: Int, phonemeId: Int): Boolean {
        // first letter M is always unlocked
        if (phonemeId == 1) return true
        // all other letters unlock only when previous letter is completed
        return lessonProgressRepository.isCompleted(profileId, phonemeId - 1)
    }
}