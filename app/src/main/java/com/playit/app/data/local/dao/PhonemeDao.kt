package com.playit.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.playit.app.data.local.entity.Phoneme;

import java.util.List;

@Dao
public interface PhonemeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(phonemes:List<Phoneme>)
}