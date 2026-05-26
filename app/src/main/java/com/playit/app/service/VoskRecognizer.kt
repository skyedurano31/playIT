package com.playit.app.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.StorageService
import javax.inject.Inject
import javax.inject.Singleton

data class RecognitionResult(
    val text: String,
    val confidence: Float
)

@Singleton
class VoskRecognizer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var model: Model? = null
    private var recognizer: Recognizer? = null
    private var isReady = false

    fun initialize(onReady: () -> Unit, onError: (Exception) -> Unit) {
        android.util.Log.d("VoskRecognizer", "Starting initialization...")
        StorageService.unpack(
            context,
            "vosk-model",
            "model",
            { model ->
                android.util.Log.d("VoskRecognizer", "Model loaded successfully")
                this.model = model
                this.recognizer = Recognizer(model, 16000.0f)
                isReady = true
                onReady()
            },
            { exception ->
                android.util.Log.e("VoskRecognizer", "Error loading model: ${exception.message}")
                onError(exception)
            }
        )
    }

    fun acceptWaveForm(buffer: ShortArray): RecognitionResult? {
        if (!isReady || recognizer == null) return null
        return try {
            recognizer!!.acceptWaveForm(buffer, buffer.size)
            val result = recognizer!!.partialResult
            parseResult(result)
        } catch (e: Exception) {
            null
        }
    }

    fun getFinalResult(): RecognitionResult? {
        if (!isReady || recognizer == null) return null
        return try {
            val result = recognizer!!.finalResult
            parseResult(result)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseResult(json: String): RecognitionResult {
        // parse partial result — format: {"partial": "text"}
        // parse final result — format: {"text": "text"}
        val text = json
            .replace("{", "")
            .replace("}", "")
            .replace("\"", "")
            .replace("partial :", "")
            .replace("text :", "")
            .replace("partial:", "")
            .replace("text:", "")
            .trim()
        return RecognitionResult(text = text, confidence = 0.8f)
    }

    fun reset() {
        recognizer?.reset()
    }

    fun release() {
        recognizer?.close()
        model?.close()
        isReady = false
    }

    fun isReady() = isReady
}