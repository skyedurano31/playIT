package com.playit.app.domain.repository

import com.playit.app.data.local.entity.LetterGroupMember

interface LetterGroupMemberRepository {
    suspend fun getMembersByGroup(groupId: Int): List<LetterGroupMember>
    suspend fun getAllMembers(): List<LetterGroupMember>
}