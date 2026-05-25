package com.playit.app.data.repository

import com.playit.app.data.local.dao.BlendItAttemptDao
import com.playit.app.domain.repository.BlendItAttemptRepository
import javax.inject.Inject

class BlendItAttemptRepositoryImpl @Inject constructor(
    private val blendItAttemptDao: BlendItAttemptDao
) : BlendItAttemptRepository