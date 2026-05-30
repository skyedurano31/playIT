package com.playit.app.domain.usecase

import com.playit.app.domain.repository.LetterGroupMemberRepository
import com.playit.app.domain.repository.LessonProgressRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupUnlockManager @Inject constructor(
    private val lessonProgressRepository: LessonProgressRepository,
    private val letterGroupMemberRepository: LetterGroupMemberRepository
) {
    suspend fun isGroupComplete(profileId: Int, groupId: Int): Boolean {
        val members = letterGroupMemberRepository.getMembersByGroup(groupId)
        if (members.isEmpty()) return false
        return members.all { member ->
            lessonProgressRepository.isCompleted(profileId, member.phonemeId)
        }
    }
}