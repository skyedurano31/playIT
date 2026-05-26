package com.playit.app.domain.repository

import com.playit.app.data.local.entity.Phoneme

interface PhonemeRepository {
    suspend fun getAllPhonemesOrdered(): List<Phoneme>
    suspend fun getPhonemeById(phonemeId: Int): Phoneme?
}