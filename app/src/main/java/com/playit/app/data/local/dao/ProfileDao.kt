package com.playit.app.data.local.dao

import androidx.room.Dao;
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.playit.app.data.local.entity.Profile

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile ORDER BY createdAt ASC")
    suspend fun getAllProfiles(): List<Profile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: Profile): Long

    @Query("DELETE FROM profile WHERE profileId = :profileId")
    suspend fun delete(profileId: Int)

    @Update
    suspend fun update(profile: Profile)

    @Query("SELECT * FROM profile WHERE profileId = :profileId")
    suspend fun getProfileById(profileId: Int): Profile?
}