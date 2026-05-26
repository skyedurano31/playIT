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
import com.playit.app.service.AudioCapture
import com.playit.app.service.AudioPlayer
import com.playit.app.service.VoskRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FeedbackState {
    object Idle : FeedbackState()
    object Listening : FeedbackState()
    object Correct : FeedbackState()
    object Incorrect : FeedbackState()
    object Loading : FeedbackState()
}

@HiltViewModel
class SayItViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val sayItAttemptRepository: SayItAttemptRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val audioCapture: AudioCapture,
    private val voskRecognizer: VoskRecognizer,
    private val speechValidator: SpeechValidator,
    private val heartManager: HeartManager,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _phoneme = MutableStateFlow<Phoneme?>(null)
    val phoneme: StateFlow<Phoneme?> = _phoneme

    private val _hearts = MutableStateFlow(5)
    val hearts: StateFlow<Int> = _hearts

    private val _feedback = MutableStateFlow<FeedbackState>(FeedbackState.Idle)
    val feedback: StateFlow<FeedbackState> = _feedback

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _attempts = MutableStateFlow<List<Boolean>>(emptyList())
    val attempts: StateFlow<List<Boolean>> = _attempts

    private val _isVoskReady = MutableStateFlow(false)
    val isVoskReady: StateFlow<Boolean> = _isVoskReady

    private val _isSessionPassed = MutableStateFlow(false)
    val isSessionPassed: StateFlow<Boolean> = _isSessionPassed

    fun loadPhoneme(phonemeId: Int) {
        viewModelScope.launch {
            _phoneme.value = phonemeRepository.getPhonemeById(phonemeId)
            initializeVosk()
        }
    }

    private fun initializeVosk() {
        voskRecognizer.initialize(
            onReady = { _isVoskReady.value = true },
            onError = { _isVoskReady.value = false }
        )
    }

    fun startRecording() {
        if (!_isVoskReady.value) return
        if (!audioCapture.hasPermission()) return
        _feedback.value = FeedbackState.Listening
        _isListening.value = true
        voskRecognizer.reset()

        audioCapture.startCapture { buffer ->
            val result = voskRecognizer.acceptWaveForm(buffer)
            result?.let {
                if (it.text.isNotBlank()) {
                    processResult(it.text)
                }
            }
        }
    }

    fun stopRecording() {
        audioCapture.stopCapture()
        _isListening.value = false
        val finalResult = voskRecognizer.getFinalResult()
        finalResult?.let {
            if (it.text.isNotBlank()) {
                processResult(it.text)
            } else {
                if (_feedback.value == FeedbackState.Listening) {
                    _feedback.value = FeedbackState.Idle
                }
            }
        }
    }

    private fun processResult(recognizedText: String) {
        val letter = _phoneme.value?.letter ?: return
        val isCorrect = speechValidator.validate(recognizedText, letter)

        viewModelScope.launch {
            saveAttempt(isCorrect)

            if (isCorrect) {
                _feedback.value = FeedbackState.Correct
                heartManager.onCorrect()
                _hearts.value = heartManager.getHearts()
                _isSessionPassed.value = true
            } else {
                _feedback.value = FeedbackState.Incorrect
                heartManager.deductHeart()
                _hearts.value = heartManager.getHearts()

                // play corrective audio
                _phoneme.value?.audioPath?.let { path ->
                    audioPlayer.play(path)
                }

                // check if hearts depleted
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
                lessonProgressRepository.update(
                    existing.copy(heartsLost = heartManager.getTotalHeartsLost())
                )
            }
            onSaved()
        }
    }

    fun resetFeedback() {
        _feedback.value = FeedbackState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        audioCapture.stopCapture()
        voskRecognizer.release()
        audioPlayer.release()
    }
}