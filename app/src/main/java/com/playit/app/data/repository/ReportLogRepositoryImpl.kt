package com.playit.app.data.repository

import com.playit.app.data.local.dao.ReportLogDao
import com.playit.app.domain.repository.ReportLogRepository
import javax.inject.Inject

class ReportLogRepositoryImpl @Inject constructor(
    private val reportLogDao: ReportLogDao
) : ReportLogRepository