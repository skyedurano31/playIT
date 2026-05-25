package com.playit.app.data.repository

import com.playit.app.data.local.dao.ProfileDao
import com.playit.app.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileRepository