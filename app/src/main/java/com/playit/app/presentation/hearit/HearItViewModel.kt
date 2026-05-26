package com.playit.app.presentation.hearit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.usecase.SessionManager
import com.playit.app.service.AudioPlayer
import com.playit.app.data.local.entity.Phoneme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HearItViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _phoneme = MutableStateFlow<Phoneme?>(null)
    val phoneme: StateFlow<Phoneme?> = _phoneme

    private val _replayCount = MutableStateFlow(0)
    val replayCount: StateFlow<Int> = _replayCount

    private val _isNextEnabled = MutableStateFlow(false)
    val isNextEnabled: StateFlow<Boolean> = _isNextEnabled

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPhoneme(phonemeId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _phoneme.value = phonemeRepository.getPhonemeById(phonemeId)
            _isLoading.value = false
        }
    }

    fun playAudio() {
        val path = _phoneme.value?.audioPath ?: return
        audioPlayer.play(path) {
            _isNextEnabled.value = true
        }
        _replayCount.value += 1
        _isNextEnabled.value = true
    }

    fun saveProgress(phonemeId: Int, onSaved: () -> Unit) {
        viewModelScope.launch {
            lessonProgressRepository.saveHearItComplete(
                profileId = SessionManager.activeProfileId,
                phonemeId = phonemeId
            )
            onSaved()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}