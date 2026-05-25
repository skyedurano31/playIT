package com.playit.app.data.repository

import com.playit.app.data.local.dao.AchievementDao
import com.playit.app.domain.repository.AchievementRepository
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: AchievementDao
) : AchievementRepository