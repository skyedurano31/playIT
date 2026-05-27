package com.playit.app.presentation.findit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.domain.model.GridItem
import com.playit.app.domain.repository.FindItAttemptRepository
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.usecase.GridGenerator
import com.playit.app.domain.usecase.HeartManager
import com.playit.app.domain.usecase.SessionManager
import com.playit.app.domain.usecase.StarCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FindItViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val findItAttemptRepository: FindItAttemptRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val gridGenerator: GridGenerator,
    private val heartManager: HeartManager,
    private val starCalculator: StarCalculator
) : ViewModel() {

    private val _grid = MutableStateFlow<List<GridItem>>(emptyList())
    val grid: StateFlow<List<GridItem>> = _grid

    private val _hearts = MutableStateFlow(5)
    val hearts: StateFlow<Int> = _hearts

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _totalTaps = MutableStateFlow(0)
    val totalTaps: StateFlow<Int> = _totalTaps

    private val _isComplete = MutableStateFlow(false)
    val isComplete: StateFlow<Boolean> = _isComplete

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _tappedItems = MutableStateFlow<Set<Int>>(emptySet())
    val tappedItems: StateFlow<Set<Int>> = _tappedItems

    private val _lastTapCorrect = MutableStateFlow<Boolean?>(null)
    val lastTapCorrect: StateFlow<Boolean?> = _lastTapCorrect

    private var currentPhonemeId = 0

    fun loadGrid(phonemeId: Int) {
        currentPhonemeId = phonemeId
        viewModelScope.launch {
            _isLoading.value = true
            heartManager.reset()
            _hearts.value = heartManager.getHearts()

            withContext(Dispatchers.IO) {
                val targetPhoneme = phonemeRepository.getPhonemeById(phonemeId)
                val allPhonemes = phonemeRepository.getAllPhonemesOrdered()
                val mastered = allPhonemes.filter {
                    it.phonemeId < phonemeId
                }

                if (targetPhoneme != null) {
                    val grid = gridGenerator.generateGrid(targetPhoneme, mastered)
                    withContext(Dispatchers.Main) {
                        _grid.value = grid
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun onCardTapped(item: GridItem, index: Int) {
        // ignore already tapped cards
        if (_tappedItems.value.contains(index)) return
        // ignore if complete
        if (_isComplete.value) return

        viewModelScope.launch(Dispatchers.Main) {
            _totalTaps.value += 1
            _tappedItems.value = _tappedItems.value + index
            _lastTapCorrect.value = item.isTarget

            withContext(Dispatchers.IO) {
                findItAttemptRepository.saveAttempt(
                    profileId = SessionManager.activeProfileId,
                    phonemeId = currentPhonemeId,
                    selectedPhonemeId = item.phonemeId,
                    isCorrect = item.isTarget,
                    attemptedAt = System.currentTimeMillis()
                )
            }

            if (item.isTarget) {
                _score.value += 1
                heartManager.onCorrect()
                _hearts.value = heartManager.getHearts()

                if (_score.value >= 3) {
                    _isComplete.value = true
                    saveProgress()
                }
            } else {
                heartManager.deductHeart()
                _hearts.value = heartManager.getHearts()

                if (heartManager.isDepletedAndReset()) {
                    _hearts.value = 3
                    resetGrid()
                }
            }

            // reset tap feedback after delay
            kotlinx.coroutines.delay(600)
            _lastTapCorrect.value = null
        }
    }

    private fun resetGrid() {
        _score.value = 0
        _tappedItems.value = emptySet()
        _totalTaps.value = 0
        viewModelScope.launch(Dispatchers.IO) {
            val targetPhoneme = phonemeRepository.getPhonemeById(currentPhonemeId)
            val allPhonemes = phonemeRepository.getAllPhonemesOrdered()
            val mastered = allPhonemes.filter { it.phonemeId < currentPhonemeId }
            if (targetPhoneme != null) {
                val grid = gridGenerator.generateGrid(targetPhoneme, mastered)
                withContext(Dispatchers.Main) {
                    _grid.value = grid
                }
            }
        }
    }

    private fun saveProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            val stars = starCalculator.calculate(
                correctTaps = _score.value,
                totalTaps = _totalTaps.value,
                heartsLost = heartManager.getTotalHeartsLost()
            )
            lessonProgressRepository.saveFindItProgress(
                profileId = SessionManager.activeProfileId,
                phonemeId = currentPhonemeId,
                starsEarned = stars,
                heartsLost = heartManager.getTotalHeartsLost()
            )
        }
    }

    fun getStarsEarned(): Int {
        return starCalculator.calculate(
            correctTaps = _score.value,
            totalTaps = _totalTaps.value,
            heartsLost = heartManager.getTotalHeartsLost()
        )
    }
}