package com.playit.app.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.domain.model.MapNode
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.usecase.SessionManager
import com.playit.app.domain.usecase.UnlockManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val phonemeRepository: PhonemeRepository,
    private val lessonProgressRepository: LessonProgressRepository,
    private val unlockManager: UnlockManager
) : ViewModel() {

    private val _mapNodes = MutableStateFlow<List<MapNode>>(emptyList())
    val mapNodes: StateFlow<List<MapNode>> = _mapNodes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadMapNodes()
    }

    fun loadMapNodes() {
        viewModelScope.launch {
            _isLoading.value = true

            // ✅ FIXED: Handle null profileId
            val profileId = SessionManager.activeProfileId
            if (profileId == null) {
                _isLoading.value = false
                _mapNodes.value = emptyList()
                return@launch
            }

            val phonemes = phonemeRepository.getAllPhonemesOrdered()
            val progressList = lessonProgressRepository.getProgressByProfile(profileId)
            val progressMap = progressList.associateBy { it.phonemeId }

            // ✅ FIXED: Added .reversed() - m (first letter) at BOTTOM
            val nodes = phonemes.map { phoneme ->
                val progress = progressMap[phoneme.phonemeId]
                MapNode.LetterNode(
                    phonemeId = phoneme.phonemeId,
                    letter = phoneme.letter,
                    isUnlocked = unlockManager.isUnlocked(profileId, phoneme.phonemeId),
                    starsEarned = progress?.starsEarned ?: 0
                )
            }.reversed()  // ✅ BOTTOM TO TOP

            _mapNodes.value = nodes
            _isLoading.value = false
        }
    }
}