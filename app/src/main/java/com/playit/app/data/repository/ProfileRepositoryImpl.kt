package com.playit.app.data.repository

import com.playit.app.data.local.dao.ProfileDao
import com.playit.app.data.local.entity.Profile
import com.playit.app.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileRepository {

    override suspend fun getAllProfiles(): List<Profile> {
        return profileDao.getAllProfiles()
    }

    override suspend fun insertProfile(profile: Profile): Long {
        return profileDao.insert(profile)
    }

    override suspend fun deleteProfile(profileId: Int) {
        profileDao.delete(profileId)
    }

    override suspend fun updateProfile(profile: Profile) {
        profileDao.update(profile)
    }

    override suspend fun getProfileById(profileId: Int): Profile? {
        return profileDao.getProfileById(profileId)
    }
}