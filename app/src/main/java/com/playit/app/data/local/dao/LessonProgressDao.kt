package com.playit.app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.playit.app.data.local.entity.LessonProgress

@Dao
public interface LessonProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lessonProgress: LessonProgress)

    @Update
    suspend fun update(lessonProgress: LessonProgress)

    @Query("SELECT * FROM lesson_progress WHERE profileId = :profileId")
    suspend fun getProgressByProfile(profileId: Int): List<LessonProgress>

    @Query("SELECT * FROM lesson_progress WHERE profileId = :profileId AND phonemeId = :phonemeId")
    suspend fun getProgress(profileId: Int, phonemeId: Int): LessonProgress?

    @Query("SELECT isCompleted FROM lesson_progress WHERE profileId = :profileId AND phonemeId = :phonemeId")
    suspend fun isCompleted(profileId: Int, phonemeId: Int): Int?
}
