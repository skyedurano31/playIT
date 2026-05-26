package com.playit.app.domain.repository

import com.playit.app.data.local.entity.Profile

interface ProfileRepository {
    suspend fun getAllProfiles(): List<Profile>
    suspend fun insertProfile(profile: Profile): Long
    suspend fun deleteProfile(profileId: Int)
    suspend fun updateProfile(profile: Profile)
    suspend fun getProfileById(profileId: Int): Profile?
}