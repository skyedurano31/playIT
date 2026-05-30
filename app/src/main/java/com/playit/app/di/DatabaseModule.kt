package com.playit.app.di

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.playit.app.data.local.dao.*
import com.playit.app.data.local.database.AppDatabase
import com.playit.app.data.local.database.DatabaseSeeder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton
import androidx.room.Room

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        lateinit var db: AppDatabase
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "playit_database"
        )
            .addCallback(AppDatabase.FOREIGN_KEY_CALLBACK)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(database: SupportSQLiteDatabase) {
                    super.onCreate(database)
                    CoroutineScope(Dispatchers.IO).launch {
                        db.phonemeDao().insertAll(DatabaseSeeder.getPhonemes())
                        db.letterGroupDao().insertAll(DatabaseSeeder.getLetterGroups())
                        db.letterGroupMemberDao().insertAll(DatabaseSeeder.getLetterGroupMembers())
                        db.blendItWordDao().insertAll(DatabaseSeeder.getBlendItWords())
                    }
                }
            })
            .build()
        return db
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