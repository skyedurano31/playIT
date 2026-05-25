package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.playit.app.data.local.entity.LetterGroupMember

@Dao
interface LetterGroupMemberDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(members: List<LetterGroupMember>)
}