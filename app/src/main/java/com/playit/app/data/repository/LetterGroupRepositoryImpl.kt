package com.playit.app.data.repository

import com.playit.app.data.local.dao.LetterGroupDao
import com.playit.app.domain.repository.LetterGroupRepository
import javax.inject.Inject

class LetterGroupRepositoryImpl @Inject constructor(
    private val letterGroupDao: LetterGroupDao
) : LetterGroupRepository