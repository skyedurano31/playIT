package com.playit.app.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioCapture @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate, channelConfig, audioFormat
    )

    // VAD settings
    private val silenceThreshold = 800        // amplitude below this = silence
    private val silenceDurationMs = 1000L     // stop after 1 second of silence
    private val minSpeechDurationMs = 300L    // must speak for at least 300ms

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun startCapture(
        onAudioCaptured: (ShortArray) -> Unit,
        onSpeechDetected: () -> Unit = {},
        onSilenceDetected: () -> Unit = {}
    ) {
        if (!hasPermission()) return

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )
            audioRecord?.startRecording()
            isRecording = true

            Thread {
                val buffer = ShortArray(bufferSize)
                var silenceStartTime = 0L
                var speechStartTime = 0L
                var hasSpeechBeenDetected = false
                var isSilenceDetected = false

                while (isRecording) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        val amplitude = buffer.take(read).maxOf { it.toInt().coerceAtLeast(0) }
                        val currentTime = System.currentTimeMillis()

                        if (amplitude > silenceThreshold) {
                            // speech detected
                            if (!hasSpeechBeenDetected) {
                                speechStartTime = currentTime
                                hasSpeechBeenDetected = true
                                onSpeechDetected()
                            }
                            silenceStartTime = 0L
                            isSilenceDetected = false
                            onAudioCaptured(buffer.copyOf(read))
                        } else {
                            // silence detected
                            onAudioCaptured(buffer.copyOf(read))
                            if (hasSpeechBeenDetected && !isSilenceDetected) {
                                if (silenceStartTime == 0L) {
                                    silenceStartTime = currentTime
                                }
                                val speechDuration = currentTime - speechStartTime
                                val silenceDuration = currentTime - silenceStartTime
                                if (silenceDuration >= silenceDurationMs
                                    && speechDuration >= minSpeechDurationMs) {
                                    isSilenceDetected = true
                                    onSilenceDetected()
                                    stopCapture()
                                }
                            }
                        }
                    }
                }
            }.start()
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun stopCapture() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}