package com.playit.app.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.domain.model.MapNode
import com.playit.app.domain.repository.BlendItProgressRepository
import com.playit.app.domain.repository.PhonemeRepository
import com.playit.app.domain.repository.LessonProgressRepository
import com.playit.app.domain.usecase.GroupUnlockManager
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
    private val blendItProgressRepository: BlendItProgressRepository,
    private val unlockManager: UnlockManager,
    private val groupUnlockManager: GroupUnlockManager
) : ViewModel() {

    private val _mapNodes = MutableStateFlow<List<MapNode>>(emptyList())
    val mapNodes: StateFlow<List<MapNode>> = _mapNodes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadMapNodes() }

    fun loadMapNodes() {
        viewModelScope.launch {
            _isLoading.value = true
            val profileId = SessionManager.activeProfileId
            val phonemes = phonemeRepository.getAllPhonemesOrdered()
            val progressList = lessonProgressRepository.getProgressByProfile(profileId)
            val progressMap = progressList.associateBy { it.phonemeId }

            val nodes = mutableListOf<MapNode>()

            // chunk by 7 to match your new group size
            val groups = phonemes.chunked(7)
            groups.forEachIndexed { groupIndex, group ->
                val groupId = groupIndex + 1

                group.forEach { phoneme ->
                    val progress = progressMap[phoneme.phonemeId]
                    nodes.add(
                        MapNode.LetterNode(
                            phonemeId = phoneme.phonemeId,
                            letter = phoneme.letter,
                            isUnlocked = unlockManager.isUnlocked(profileId, phoneme.phonemeId),
                            starsEarned = progress?.starsEarned ?: 0
                        )
                    )
                }

                val isGroupComplete = groupUnlockManager.isGroupComplete(profileId, groupId)
                val blendItProgress = blendItProgressRepository.getProgress(profileId, groupId)
                nodes.add(
                    MapNode.BlendItNode(
                        groupId = groupId,
                        groupNumber = groupId,
                        isUnlocked = isGroupComplete,
                        starsEarned = blendItProgress?.starsEarned ?: 0
                    )
                )
            }

            _mapNodes.value = nodes
            _isLoading.value = false
        }
    }
}