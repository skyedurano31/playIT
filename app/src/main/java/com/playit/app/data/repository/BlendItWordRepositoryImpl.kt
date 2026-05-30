package com.playit.app.data.repository

import com.playit.app.data.local.dao.BlendItWordDao
import com.playit.app.data.local.entity.BlendItWord
import com.playit.app.domain.repository.BlendItWordRepository
import javax.inject.Inject

class BlendItWordRepositoryImpl @Inject constructor(
    private val blendItWordDao: BlendItWordDao
) : BlendItWordRepository {

    override suspend fun insertAll(words: List<BlendItWord>) {
        blendItWordDao.insertAll(words)
    }

    override suspend fun getWordsByGroups(groupIds: List<Int>): List<BlendItWord> {
        return blendItWordDao.getWordsByGroups(groupIds)
    }

    override suspend fun getWordsByGroup(groupId: Int): List<BlendItWord> {
        return blendItWordDao.getWordsByGroup(groupId)
    }
}