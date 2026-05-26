package com.playit.app.data.repository

import com.playit.app.data.local.dao.PhonemeDao
import com.playit.app.data.local.entity.Phoneme
import com.playit.app.domain.repository.PhonemeRepository
import javax.inject.Inject

class PhonemeRepositoryImpl @Inject constructor(
    private val phonemeDao: PhonemeDao
) : PhonemeRepository {
    override suspend fun getAllPhonemesOrdered(): List<Phoneme> {
        return phonemeDao.getAllPhonemesOrdered()
    }

    override suspend fun getPhonemeById(phonemeId: Int): Phoneme? {
        return phonemeDao.getPhonemeById(phonemeId)
    }
}