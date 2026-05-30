package com.playit.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.BlendItAttempt

@Dao
interface BlendItAttemptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: BlendItAttempt)

    @Query("SELECT * FROM blend_it_attempt WHERE profileId = :profileId AND groupId = :groupId")
    suspend fun getAttempts(profileId: Int, groupId: Int): List<BlendItAttempt>
}