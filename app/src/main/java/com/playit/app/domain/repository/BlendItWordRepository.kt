package com.playit.app.domain.repository

import com.playit.app.data.local.entity.BlendItWord

interface BlendItWordRepository {
    suspend fun insertAll(words: List<BlendItWord>)
    suspend fun getWordsByGroups(groupIds: List<Int>): List<BlendItWord>
    suspend fun getWordsByGroup(groupId: Int): List<BlendItWord>
}