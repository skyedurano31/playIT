package com.playit.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.playit.app.data.local.entity.Profile
import com.playit.app.domain.repository.ProfileRepository
import com.playit.app.domain.usecase.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles: StateFlow<List<Profile>> = _profiles

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadProfiles()
    }

    fun loadProfiles() {
        viewModelScope.launch {
            _isLoading.value = true
            _profiles.value = profileRepository.getAllProfiles()
            _isLoading.value = false
        }
    }

    fun createProfile(name: String, avatarResId: Int, onCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val profile = Profile(
                name = name,
                avatarResId = avatarResId,
                createdAt = System.currentTimeMillis(),
                lastPlayedAt = System.currentTimeMillis()
            )
            val id = profileRepository.insertProfile(profile)
            SessionManager.setActiveProfile(id.toInt())
            onCreated(id.toInt())
        }
    }

    fun selectProfile(profileId: Int) {
        SessionManager.setActiveProfile(profileId)
    }

    fun deleteProfile(profileId: Int) {
        viewModelScope.launch {
            profileRepository.deleteProfile(profileId)
            loadProfiles()
        }
    }
}