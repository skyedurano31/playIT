package com.playit.app.data.repository

import com.playit.app.data.local.dao.LessonProgressDao
import com.playit.app.domain.repository.LessonProgressRepository
import javax.inject.Inject

class LessonProgressRepositoryImpl @Inject constructor(
    private val lessonProgressDao: LessonProgressDao
) : LessonProgressRepository