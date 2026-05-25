package com.playit.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.playit.app.data.local.entity.LetterGroup

@Dao
interface LetterGroupDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(groups: List<LetterGroup>)
}
