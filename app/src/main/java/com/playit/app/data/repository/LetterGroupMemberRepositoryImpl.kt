package com.playit.app.data.repository

import com.playit.app.data.local.dao.LetterGroupMemberDao
import com.playit.app.domain.repository.LetterGroupMemberRepository
import javax.inject.Inject

class LetterGroupMemberRepositoryImpl @Inject constructor(
    private val letterGroupMemberDao: LetterGroupMemberDao
) : LetterGroupMemberRepository