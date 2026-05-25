package com.playit.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.playit.app.data.local.dao.AchievementDao
import com.playit.app.data.local.dao.BlendItAttemptDao
import com.playit.app.data.local.dao.BlendItProgressDao
import com.playit.app.data.local.dao.BlendItWordDao
import com.playit.app.data.local.dao.FindItAttemptDao
import com.playit.app.data.local.dao.LessonProgressDao
import com.playit.app.data.local.dao.LetterGroupDao
import com.playit.app.data.local.dao.LetterGroupMemberDao
import com.playit.app.data.local.dao.PhonemeDao
import com.playit.app.data.local.dao.ProfileDao
import com.playit.app.data.local.dao.ReportLogDao
import com.playit.app.data.local.dao.SayItAttemptDao
import com.playit.app.data.local.entity.Achievement
import com.playit.app.data.local.entity.BlendItAttempt
import com.playit.app.data.local.entity.BlendItProgress
import com.playit.app.data.local.entity.BlendItWord
import com.playit.app.data.local.entity.FindItAttempt
import com.playit.app.data.local.entity.LessonProgress
import com.playit.app.data.local.entity.LetterGroup
import com.playit.app.data.local.entity.LetterGroupMember
import com.playit.app.data.local.entity.Phoneme
import com.playit.app.data.local.entity.Profile
import com.playit.app.data.local.entity.ReportLog
import com.playit.app.data.local.entity.SayItAttempt

@Database(
    entities = [
        Profile::class,
        Phoneme::class,
        LetterGroup::class,
        LetterGroupMember::class,
        LessonProgress::class,
        SayItAttempt::class,
        FindItAttempt::class,
        Achievement::class,
        ReportLog::class,
        BlendItWord::class,
        BlendItProgress::class,
        BlendItAttempt::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun phonemeDao(): PhonemeDao
    abstract fun letterGroupDao(): LetterGroupDao
    abstract fun letterGroupMemberDao(): LetterGroupMemberDao
    abstract fun lessonProgressDao(): LessonProgressDao
    abstract fun sayItAttemptDao(): SayItAttemptDao
    abstract fun findItAttemptDao(): FindItAttemptDao
    abstract fun achievementDao(): AchievementDao
    abstract fun reportLogDao(): ReportLogDao
    abstract fun blendItWordDao(): BlendItWordDao
    abstract fun blendItProgressDao(): BlendItProgressDao
    abstract fun blendItAttemptDao(): BlendItAttemptDao

    companion object {
        val FOREIGN_KEY_CALLBACK = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("PRAGMA foreign_keys = ON")
            }
        }
    }
}