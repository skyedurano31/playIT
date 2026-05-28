package com.playit.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.SayItAttemptRepository
import com.playit.app.domain.usecase.LetterPerformance
import com.playit.app.domain.usecase.LetterStatus
import com.playit.app.domain.usecase.LetterStatusCalculator
import com.playit.app.domain.usecase.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ParentDashboardViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val sayItAttemptRepository: SayItAttemptRepository,
    private val letterStatusCalculator: LetterStatusCalculator
) : ViewModel() {

    private val _letterPerformances = MutableStateFlow<List<LetterPerformance>>(emptyList())
    val letterPerformances: StateFlow<List<LetterPerformance>> = _letterPerformances

    private val _completedCount = MutableStateFlow(0)
    val completedCount: StateFlow<Int> = _completedCount

    private val _totalStars = MutableStateFlow(0)
    val totalStars: StateFlow<Int> = _totalStars

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            val profileId = SessionManager.activeProfileId

            withContext(Dispatchers.IO) {
                val phonemes = phonemeRepository.getAllPhonemesOrdered()
                val progressList = lessonProgressRepository.getProgressByProfile(profileId)
                val progressMap = progressList.associateBy { it.phonemeId }
                val completedCount = lessonProgressRepository.getAllCompletedCount(profileId)
                val totalStars = progressList.sumOf { it.starsEarned }

                val performances = phonemes.map { phoneme ->
                    val progress = progressMap[phoneme.phonemeId]
                    val correctCount = sayItAttemptRepository.getCorrectCount(profileId, phoneme.phonemeId)
                    val totalCount = sayItAttemptRepository.getTotalCount(profileId, phoneme.phonemeId)

                    letterStatusCalculator.calculate(
                        phonemeId = phoneme.phonemeId,
                        letter = phoneme.letter,
                        correctCount = correctCount,
                        totalCount = totalCount,
                        starsEarned = progress?.starsEarned ?: 0,
                        isCompleted = (progress?.isCompleted ?: 0) == 1
                    )
                }

                withContext(Dispatchers.Main) {
                    _letterPerformances.value = performances
                    _completedCount.value = completedCount
                    _totalStars.value = totalStars
                    _isLoading.value = false
                }
            }
        }
    }
}