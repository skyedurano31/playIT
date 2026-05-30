package com.playit.app.presentation.blendit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.local.entity.BlendItWord
import com.playit.app.domain.model.SlotItem
import com.playit.app.domain.model.TileItem
import com.playit.app.domain.repository.BlendItAttemptRepository
import com.playit.app.domain.repository.BlendItProgressRepository
import com.playit.app.domain.repository.BlendItWordRepository
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.usecase.BlendItWordSelector
import com.playit.app.domain.usecase.HeartManager
import com.playit.app.domain.usecase.SessionManager
import com.playit.app.domain.usecase.StarCalculator
import com.playit.app.service.AudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class BlendItFeedback {
    object Idle : BlendItFeedback()
    object Correct : BlendItFeedback()
    object Incorrect : BlendItFeedback()
    object SessionComplete : BlendItFeedback()
}

@HiltViewModel
class BlendItViewModel @Inject constructor(
    private val blendItWordRepository: BlendItWordRepository,
    private val blendItAttemptRepository: BlendItAttemptRepository,
    private val blendItProgressRepository: BlendItProgressRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val blendItWordSelector: BlendItWordSelector,
    private val heartManager: HeartManager,
    private val starCalculator: StarCalculator,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    private val _currentWord = MutableStateFlow<BlendItWord?>(null)
    val currentWord: StateFlow<BlendItWord?> = _currentWord

    private val _tileBank = MutableStateFlow<List<TileItem>>(emptyList())
    val tileBank: StateFlow<List<TileItem>> = _tileBank

    private val _slots = MutableStateFlow<List<SlotItem>>(emptyList())
    val slots: StateFlow<List<SlotItem>> = _slots

    private val _hearts = MutableStateFlow(5)
    val hearts: StateFlow<Int> = _hearts

    private val _wordProgress = MutableStateFlow(0)
    val wordProgress: StateFlow<Int> = _wordProgress

    private val _feedback = MutableStateFlow<BlendItFeedback>(BlendItFeedback.Idle)
    val feedback: StateFlow<BlendItFeedback> = _feedback

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _wrongAttempts = MutableStateFlow(0)
    val wrongAttempts: StateFlow<Int> = _wrongAttempts

    private var sessionWords = listOf<BlendItWord>()
    private var currentWordIndex = 0
    private var currentGroupId = 0
    private var correctWords = 0
    private var totalAttempts = 0

    fun loadSession(groupId: Int) {
        currentGroupId = groupId
        heartManager.reset()
        _hearts.value = 5
        correctWords = 0
        totalAttempts = 0
        currentWordIndex = 0

        viewModelScope.launch {
            _isLoading.value = true
            withContext(Dispatchers.IO) {
                // get mastered groups (all groups before current)
                val masteredGroupIds = (1 until groupId).toList()
                sessionWords = blendItWordSelector.selectWords(groupId, masteredGroupIds)
            }
            _isLoading.value = false
            loadCurrentWord()
        }
    }

    private fun loadCurrentWord() {
        if (currentWordIndex >= sessionWords.size) {
            // session complete
            _feedback.value = BlendItFeedback.SessionComplete
            saveSessionProgress()
            return
        }

        val word = sessionWords[currentWordIndex]
        _currentWord.value = word
        _wrongAttempts.value = 0

        // generate slots
        val slots = word.word.mapIndexed { index, _ ->
            SlotItem(position = index)
        }
        _slots.value = slots

        // generate tiles
        val allLetters = ('A'..'Z').map { it.toString() }
        val tiles = blendItWordSelector.generateTiles(word.word, allLetters)
        _tileBank.value = tiles

        // play word audio
        audioPlayer.play(word.audioPath)
    }

    fun onTileTapped(tile: TileItem) {
        if (tile.isPlaced) return

        val currentSlots = _slots.value
        val firstEmpty = currentSlots.indexOfFirst { !it.isFilled && !it.isLocked }
        if (firstEmpty == -1) return

        // place tile in first empty slot
        val updatedSlots = currentSlots.toMutableList()
        updatedSlots[firstEmpty] = updatedSlots[firstEmpty].copy(
            letter = tile.letter,
            isFilled = true
        )
        _slots.value = updatedSlots

        // mark tile as placed
        val updatedTiles = _tileBank.value.toMutableList()
        val tileIndex = updatedTiles.indexOfFirst { it.id == tile.id }
        if (tileIndex != -1) {
            updatedTiles[tileIndex] = updatedTiles[tileIndex].copy(isPlaced = true)
        }
        _tileBank.value = updatedTiles
    }

    fun onSlotTapped(slot: SlotItem) {
        if (slot.isLocked || !slot.isFilled) return

        // remove letter from slot
        val updatedSlots = _slots.value.toMutableList()
        updatedSlots[slot.position] = updatedSlots[slot.position].copy(
            letter = "",
            isFilled = false
        )
        _slots.value = updatedSlots

        // unplace tile
        val updatedTiles = _tileBank.value.toMutableList()
        val tileIndex = updatedTiles.indexOfFirst {
            it.letter == slot.letter && it.isPlaced
        }
        if (tileIndex != -1) {
            updatedTiles[tileIndex] = updatedTiles[tileIndex].copy(isPlaced = false)
        }
        _tileBank.value = updatedTiles
    }

    fun onSubmit() {
        val currentWord = _currentWord.value ?: return
        val slots = _slots.value

        // check if all slots are filled
        if (slots.any { !it.isFilled && !it.isLocked }) return

        val builtWord = slots.joinToString("") { it.letter }
        val isCorrect = builtWord == currentWord.word
        totalAttempts++

        viewModelScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                blendItAttemptRepository.saveAttempt(
                    profileId = SessionManager.activeProfileId,
                    groupId = currentGroupId,
                    wordId = currentWord.wordId,
                    isCorrect = isCorrect,
                    attemptedAt = System.currentTimeMillis()
                )
            }

            if (isCorrect) {
                correctWords++
                _feedback.value = BlendItFeedback.Correct
                heartManager.onCorrect()
                _hearts.value = heartManager.getHearts()

                kotlinx.coroutines.delay(1200)
                _feedback.value = BlendItFeedback.Idle
                currentWordIndex++
                _wordProgress.value = currentWordIndex
                loadCurrentWord()

            } else {
                _wrongAttempts.value += 1
                _feedback.value = BlendItFeedback.Incorrect
                heartManager.deductHeart()
                _hearts.value = heartManager.getHearts()

                if (heartManager.isDepletedAndReset()) {
                    _hearts.value = 3
                }

                // apply hint after 2 wrong attempts
                if (_wrongAttempts.value >= 2) {
                    applyHint()
                }

                kotlinx.coroutines.delay(1000)
                _feedback.value = BlendItFeedback.Idle
                clearSlots()
            }
        }
    }

    fun applyHint() {
        val currentWord = _currentWord.value ?: return
        val slots = _slots.value.toMutableList()

        // lock first unfilled correct slot
        val firstUnfilled = slots.indexOfFirst { !it.isFilled && !it.isLocked }
        if (firstUnfilled == -1) return

        val correctLetter = currentWord.word[firstUnfilled].toString()
        slots[firstUnfilled] = slots[firstUnfilled].copy(
            letter = correctLetter,
            isFilled = true,
            isLocked = true
        )
        _slots.value = slots

        // mark that tile as placed
        val updatedTiles = _tileBank.value.toMutableList()
        val tileIndex = updatedTiles.indexOfFirst {
            it.letter == correctLetter && !it.isPlaced
        }
        if (tileIndex != -1) {
            updatedTiles[tileIndex] = updatedTiles[tileIndex].copy(isPlaced = true)
        }
        _tileBank.value = updatedTiles
    }

    private fun clearSlots() {
        val currentWord = _currentWord.value ?: return
        // keep locked slots, clear rest
        val clearedSlots = _slots.value.map { slot ->
            if (slot.isLocked) slot
            else slot.copy(letter = "", isFilled = false)
        }
        _slots.value = clearedSlots

        // unplace all non-locked tiles
        val updatedTiles = _tileBank.value.map { tile ->
            val isLockedInSlot = _slots.value.any {
                it.isLocked && it.letter == tile.letter
            }
            if (isLockedInSlot) tile
            else tile.copy(isPlaced = false)
        }
        _tileBank.value = updatedTiles
    }

    fun replayAudio() {
        _currentWord.value?.audioPath?.let { path ->
            audioPlayer.play(path)
        }
    }

    fun getStarsEarned(): Int {
        return starCalculator.calculate(
            correctTaps = correctWords,
            totalTaps = totalAttempts,
            heartsLost = heartManager.getTotalHeartsLost()
        )
    }

    private fun saveSessionProgress() {
        viewModelScope.launch(Dispatchers.IO) {
            blendItProgressRepository.saveProgress(
                profileId = SessionManager.activeProfileId,
                groupId = currentGroupId,
                starsEarned = getStarsEarned(),
                heartsLost = heartManager.getTotalHeartsLost(),
                isCompleted = true
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}