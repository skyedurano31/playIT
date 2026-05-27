package com.playit.app.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class VoskRecognizer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var isReady = false

    suspend fun initialize(): Boolean = suspendCancellableCoroutine { continuation ->
        StorageService.unpack(
            context,
            "vosk-model",
            "model",
            { loadedModel ->
                model = loadedModel
                isReady = true
                continuation.resume(true)
            },
            { exception ->
                android.util.Log.e("VoskRecognizer", "Model load failed: ${exception.message}")
                continuation.resume(false)
            }
        )
    }

    fun startListening(listener: RecognitionListener) {
        val currentModel = model ?: return
        try {
            val recognizer = Recognizer(currentModel, 16000.0f)
            speechService?.shutdown()
            speechService = SpeechService(recognizer, 16000.0f)
            speechService!!.startListening(listener)
        } catch (e: Exception) {
            android.util.Log.e("VoskRecognizer", "Start listening failed: ${e.message}")
        }
    }

    fun stopListening() {
        speechService?.stop()
    }

    fun isReady() = isReady

    fun parseResult(json: String, key: String): String {
        return try {
            JSONObject(json).getString(key).trim()
        } catch (e: Exception) {
            ""
        }
    }

    fun release() {
        speechService?.shutdown()
        speechService = null
        model?.close()
        model = null
        isReady = false
    }
}