package com.playit.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query

import com.playit.app.data.local.entity.Phoneme;

@Dao
public interface PhonemeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(phonemes: List<Phoneme>)

    @Query("SELECT * FROM phoneme ORDER BY phonemeId ASC")
    suspend fun getAllPhonemesOrdered(): List<Phoneme>

    @Query("SELECT * FROM phoneme WHERE phonemeId = :phonemeId")
    suspend fun getPhonemeById(phonemeId: Int): Phoneme?
}