package com.playit.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.playit.app.data.local.entity.SayItAttempt

@Dao
public interface SayItAttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: SayItAttempt)

    @Query("SELECT * FROM say_it_attempt WHERE profileId = :profileId AND phonemeId = :phonemeId")
    suspend fun getAttempts(profileId: Int, phonemeId: Int): List<SayItAttempt>
}
