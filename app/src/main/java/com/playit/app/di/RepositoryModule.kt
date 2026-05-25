package com.playit.app.di

import com.playit.app.data.repository.*
import com.playit.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindPhonemeRepository(
        impl: PhonemeRepositoryImpl
    ): PhonemeRepository

    @Binds
    @Singleton
    abstract fun bindLetterGroupRepository(
        impl: LetterGroupRepositoryImpl
    ): LetterGroupRepository

    @Binds
    @Singleton
    abstract fun bindLetterGroupMemberRepository(
        impl: LetterGroupMemberRepositoryImpl
    ): LetterGroupMemberRepository

    @Binds
    @Singleton
    abstract fun bindLessonProgressRepository(
        impl: LessonProgressRepositoryImpl
    ): LessonProgressRepository

    @Binds
    @Singleton
    abstract fun bindSayItAttemptRepository(
        impl: SayItAttemptRepositoryImpl
    ): SayItAttemptRepository

    @Binds
    @Singleton
    abstract fun bindFindItAttemptRepository(
        impl: FindItAttemptRepositoryImpl
    ): FindItAttemptRepository

    @Binds
    @Singleton
    abstract fun bindAchievementRepository(
        impl: AchievementRepositoryImpl
    ): AchievementRepository

    @Binds
    @Singleton
    abstract fun bindReportLogRepository(
        impl: ReportLogRepositoryImpl
    ): ReportLogRepository

    @Binds
    @Singleton
    abstract fun bindBlendItWordRepository(
        impl: BlendItWordRepositoryImpl
    ): BlendItWordRepository

    @Binds
    @Singleton
    abstract fun bindBlendItProgressRepository(
        impl: BlendItProgressRepositoryImpl
    ): BlendItProgressRepository

    @Binds
    @Singleton
    abstract fun bindBlendItAttemptRepository(
        impl: BlendItAttemptRepositoryImpl
    ): BlendItAttemptRepository
}