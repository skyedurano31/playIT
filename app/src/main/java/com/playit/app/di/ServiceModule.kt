package com.playit.app.di

import android.content.Context
import com.playit.app.service.AudioCapture
import com.playit.app.service.AudioPlayer
import com.playit.app.service.VoskRecognizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideAudioPlayer(
        @ApplicationContext context: Context
    ): AudioPlayer = AudioPlayer(context)

    @Provides
    @Singleton
    fun provideAudioCapture(
        @ApplicationContext context: Context
    ): AudioCapture = AudioCapture(context)

    @Provides
    @Singleton
    fun provideVoskRecognizer(
        @ApplicationContext context: Context
    ): VoskRecognizer = VoskRecognizer(context)
}