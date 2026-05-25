package com.playit.app.data.repository

import com.playit.app.data.local.dao.BlendItWordDao
import com.playit.app.domain.repository.BlendItWordRepository
import javax.inject.Inject

class BlendItWordRepositoryImpl @Inject constructor(
    private val blendItWordDao: BlendItWordDao
) : BlendItWordRepository