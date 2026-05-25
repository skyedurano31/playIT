package com.playit.app.data.repository

import com.playit.app.data.local.dao.SayItAttemptDao
import com.playit.app.domain.repository.SayItAttemptRepository
import javax.inject.Inject

class SayItAttemptRepositoryImpl @Inject constructor(
    private val sayItAttemptDao: SayItAttemptDao
) : SayItAttemptRepository