package com.playit.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.BlendItWord

@Dao
public interface BlendItWordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<BlendItWord>)

    @Query("SELECT * FROM blend_it_word WHERE groupId IN (:groupIds)")
    suspend fun getWordsByGroups(groupIds: List<Int>): List<BlendItWord>

    @Query("SELECT * FROM blend_it_word WHERE groupId = :groupId")
    suspend fun getWordsByGroup(groupId: Int): List<BlendItWord>
}
