package com.playit.app.data.repository

import com.playit.app.data.local.dao.BlendItProgressDao
import com.playit.app.domain.repository.BlendItProgressRepository
import javax.inject.Inject

class BlendItProgressRepositoryImpl @Inject constructor(
    private val blendItProgressDao: BlendItProgressDao
) : BlendItProgressRepository