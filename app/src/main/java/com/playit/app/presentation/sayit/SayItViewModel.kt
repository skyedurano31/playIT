package com.playit.app.presentation.sayit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.local.entity.Phoneme
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.SayItAttemptRepository
import com.playit.app.domain.usecase.HeartManager
import com.playit.app.domain.usecase.SessionManager
import com.playit.app.domain.usecase.SpeechValidator
import com.playit.app.service.AudioPlayer
import com.playit.app.service.VoskRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.vosk.android.RecognitionListener
import javax.inject.Inject

sealed class FeedbackState {
    object Idle : FeedbackState()
    object Listening : FeedbackState()
    object Correct : FeedbackState()
    object Incorrect : FeedbackState()
}

@HiltViewModel
class SayItViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val sayItAttemptRepository: SayItAttemptRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val voskRecognizer: VoskRecognizer,
    private val speechValidator: SpeechValidator,
    private val heartManager: HeartManager,
    private val audioPlayer: AudioPlayer
) : ViewModel(), RecognitionListener {

    private val _phoneme = MutableStateFlow<Phoneme?>(null)
    val phoneme: StateFlow<Phoneme?> = _phoneme

    private val _hearts = MutableStateFlow(5)
    val hearts: StateFlow<Int> = _hearts

    private val _feedback = MutableStateFlow<FeedbackState>(FeedbackState.Idle)
    val feedback: StateFlow<FeedbackState> = _feedback

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _isVoskReady = MutableStateFlow(false)
    val isVoskReady: StateFlow<Boolean> = _isVoskReady

    private val _isSessionPassed = MutableStateFlow(false)
    val isSessionPassed: StateFlow<Boolean> = _isSessionPassed

    private val _attempts = MutableStateFlow<List<Boolean>>(emptyList())
    val attempts: StateFlow<List<Boolean>> = _attempts

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText

    private var resultHandled = false

    fun loadPhoneme(phonemeId: Int) {
        viewModelScope.launch {
            _phoneme.value = phonemeRepository.getPhonemeById(phonemeId)
            initializeVosk()
        }
    }

    private fun initializeVosk() {
        viewModelScope.launch(Dispatchers.IO) {
            val success = voskRecognizer.initialize()
            withContext(Dispatchers.Main) {
                _isVoskReady.value = success
            }
        }
    }

    fun startRecording() {
        if (!_isVoskReady.value) return
        resultHandled = false
        _feedback.value = FeedbackState.Listening
        _isListening.value = true
        _partialText.value = ""
        viewModelScope.launch(Dispatchers.Main) {
            try {
                voskRecognizer.startListening(this@SayItViewModel)
            } catch (e: Exception) {
                android.util.Log.e("SayItViewModel", "startRecording failed: ${e.message}")
                _isListening.value = false
                _feedback.value = FeedbackState.Idle
            }
        }
    }

    fun stopRecording() {
        voskRecognizer.stopListening()
        _isListening.value = false
    }

    override fun onPartialResult(hypothesis: String) {
        val text = voskRecognizer.parseResult(hypothesis, "partial")
        if (text.isNotEmpty()) {
            _partialText.value = text
        }
    }

    override fun onResult(hypothesis: String) {
        if (resultHandled) return
        val text = voskRecognizer.parseResult(hypothesis, "text")
        if (text.isNotEmpty()) {
            resultHandled = true
            _partialText.value = ""
            processResult(text)
        }
    }

    override fun onFinalResult(hypothesis: String) {
        if (resultHandled) return
        val text = voskRecognizer.parseResult(hypothesis, "text")
        resultHandled = true
        _partialText.value = ""
        if (text.isNotEmpty()) processResult(text)
        else {
            viewModelScope.launch(Dispatchers.Main) {
                _isListening.value = false
                _feedback.value = FeedbackState.Idle
            }
        }
    }

    override fun onError(exception: Exception) {
        voskRecognizer.stopListening()
        viewModelScope.launch(Dispatchers.Main) {
            _isListening.value = false
            _feedback.value = FeedbackState.Idle
        }
        android.util.Log.e("SayItViewModel", "Vosk error: ${exception.message}")
    }

    override fun onTimeout() {
        voskRecognizer.stopListening()
        viewModelScope.launch(Dispatchers.Main) {
            _isListening.value = false
            _feedback.value = FeedbackState.Idle
        }
    }

    private fun processResult(recognizedText: String) {
        val letter = _phoneme.value?.letter ?: return
        val isCorrect = speechValidator.validate(recognizedText, letter)

        viewModelScope.launch(Dispatchers.Main) {
            // stop mic before anything else
            voskRecognizer.stopListening()
            _isListening.value = false

            withContext(Dispatchers.IO) {
                saveAttempt(isCorrect)
            }

            if (isCorrect) {
                _feedback.value = FeedbackState.Correct
                heartManager.onCorrect()
                _hearts.value = heartManager.getHearts()
                _isSessionPassed.value = true
            } else {
                _feedback.value = FeedbackState.Incorrect
                heartManager.deductHeart()
                _hearts.value = heartManager.getHearts()

                // safe to play now — mic is already stopped
                _phoneme.value?.audioPath?.let { path ->
                    audioPlayer.play(path)
                }

                if (heartManager.isDepletedAndReset()) {
                    _hearts.value = 3
                }
            }

            _attempts.value = _attempts.value + isCorrect
        }
    }

    private suspend fun saveAttempt(isCorrect: Boolean) {
        val phonemeId = _phoneme.value?.phonemeId ?: return
        sayItAttemptRepository.saveAttempt(
            profileId = SessionManager.activeProfileId,
            phonemeId = phonemeId,
            isCorrect = isCorrect,
            attemptedAt = System.currentTimeMillis()
        )
    }

    fun saveSayItProgress(onSaved: () -> Unit) {
        viewModelScope.launch {
            val phonemeId = _phoneme.value?.phonemeId ?: return@launch
            val existing = lessonProgressRepository.getProgress(
                SessionManager.activeProfileId,
                phonemeId
            )
            if (existing != null) {
                withContext(Dispatchers.IO) {
                    lessonProgressRepository.update(
                        existing.copy(heartsLost = heartManager.getTotalHeartsLost())
                    )
                }
            }
            withContext(Dispatchers.Main) {
                onSaved()
            }
        }
    }

    fun resetFeedback() {
        _feedback.value = FeedbackState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        voskRecognizer.stopListening()
        voskRecognizer.release()
        audioPlayer.release()
    }
}