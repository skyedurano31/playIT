package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.playit.app.data.local.entity.BlendItProgress

@Dao
interface BlendItProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: BlendItProgress)

    @Update
    suspend fun update(progress: BlendItProgress)

    @Query("SELECT * FROM blend_it_progress WHERE profileId = :profileId AND groupId = :groupId")
    suspend fun getProgress(profileId: Int, groupId: Int): BlendItProgress?

    @Query("SELECT * FROM blend_it_progress WHERE profileId = :profileId")
    suspend fun getAllProgress(profileId: Int): List<BlendItProgress>

    @Query("SELECT isCompleted FROM blend_it_progress WHERE profileId = :profileId AND groupId = :groupId")
    suspend fun isGroupCompleted(profileId: Int, groupId: Int): Int?
}