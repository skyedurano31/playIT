package com.playit.app.di

import android.content.Context
import androidx.room.Room
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
import com.playit.app.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "playit_database"
        )
            .addCallback(AppDatabase.FOREIGN_KEY_CALLBACK)
            .build()
    }

    @Provides fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()
    @Provides fun providePhonemeDao(db: AppDatabase): PhonemeDao = db.phonemeDao()
    @Provides fun provideLetterGroupDao(db: AppDatabase): LetterGroupDao = db.letterGroupDao()
    @Provides fun provideLetterGroupMemberDao(db: AppDatabase): LetterGroupMemberDao = db.letterGroupMemberDao()
    @Provides fun provideLessonProgressDao(db: AppDatabase): LessonProgressDao = db.lessonProgressDao()
    @Provides fun provideSayItAttemptDao(db: AppDatabase): SayItAttemptDao = db.sayItAttemptDao()
    @Provides fun provideFindItAttemptDao(db: AppDatabase): FindItAttemptDao = db.findItAttemptDao()
    @Provides fun provideAchievementDao(db: AppDatabase): AchievementDao = db.achievementDao()
    @Provides fun provideReportLogDao(db: AppDatabase): ReportLogDao = db.reportLogDao()
    @Provides fun provideBlendItWordDao(db: AppDatabase): BlendItWordDao = db.blendItWordDao()
    @Provides fun provideBlendItProgressDao(db: AppDatabase): BlendItProgressDao = db.blendItProgressDao()
    @Provides fun provideBlendItAttemptDao(db: AppDatabase): BlendItAttemptDao = db.blendItAttemptDao()
}