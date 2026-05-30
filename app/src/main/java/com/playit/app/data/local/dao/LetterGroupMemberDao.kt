package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.LetterGroupMember

@Dao
interface LetterGroupMemberDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(members: List<LetterGroupMember>)

    @Query("SELECT * FROM letter_group_member WHERE groupId = :groupId ORDER BY position ASC")
    suspend fun getMembersByGroup(groupId: Int): List<LetterGroupMember>

    @Query("SELECT * FROM letter_group_member ORDER BY groupId ASC, position ASC")
    suspend fun getAllMembers(): List<LetterGroupMember>
}