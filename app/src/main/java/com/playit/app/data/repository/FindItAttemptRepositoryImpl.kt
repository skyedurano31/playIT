package com.playit.app.data.repository

import com.playit.app.data.local.dao.FindItAttemptDao
import com.playit.app.domain.repository.FindItAttemptRepository
import javax.inject.Inject

class FindItAttemptRepositoryImpl @Inject constructor(
    private val findItAttemptDao: FindItAttemptDao
) : FindItAttemptRepository