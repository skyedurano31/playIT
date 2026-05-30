package com.playit.app.di

import com.playit.app.domain.repository.BlendItWordRepository
import com.playit.app.domain.usecase.HeartManager
import com.playit.app.domain.usecase.SpeechValidator
import com.playit.app.domain.usecase.UnlockManager
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.LetterGroupMemberRepository
import com.playit.app.domain.usecase.BlendItWordSelector
import com.playit.app.domain.usecase.GridGenerator
import com.playit.app.domain.usecase.GroupUnlockManager
import com.playit.app.domain.usecase.LetterStatusCalculator
import com.playit.app.domain.usecase.StarCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideSpeechValidator(): SpeechValidator = SpeechValidator()

    @Provides
    fun provideHeartManager(): HeartManager = HeartManager()

    @Provides
    @Singleton
    fun provideUnlockManager(
        lessonProgressRepository: LessonProgressRepository
    ): UnlockManager = UnlockManager(lessonProgressRepository)

    @Provides
    @Singleton
    fun provideStarCalculator(): StarCalculator = StarCalculator()

    @Provides
    @Singleton
    fun provideGridGenerator(): GridGenerator = GridGenerator()

    @Provides
    @Singleton
    fun provideLetterStatusCalculator(): LetterStatusCalculator = LetterStatusCalculator()

    @Provides
    @Singleton
    fun provideGroupUnlockManager(
        lessonProgressRepository: LessonProgressRepository,
        letterGroupMemberRepository: LetterGroupMemberRepository
    ): GroupUnlockManager = GroupUnlockManager(lessonProgressRepository, letterGroupMemberRepository)

    @Provides
    @Singleton
    fun provideBlendItWordSelector(
        blendItWordRepository: BlendItWordRepository
    ): BlendItWordSelector = BlendItWordSelector(blendItWordRepository)
}