package com.playit.app.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.domain.model.MapNode
import com.playit.app.domain.repository.PhonemeRepository
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
            val profileId = SessionManager.activeProfileId
            val phonemes = phonemeRepository.getAllPhonemesOrdered()

            val nodes = phonemes.map { phoneme ->
                MapNode.LetterNode(
                    phonemeId = phoneme.phonemeId,
                    letter = phoneme.letter,
                    isUnlocked = unlockManager.isUnlocked(profileId, phoneme.phonemeId),
                    starsEarned = 0
                )
            }

            _mapNodes.value = nodes
            _isLoading.value = false
        }
    }
}