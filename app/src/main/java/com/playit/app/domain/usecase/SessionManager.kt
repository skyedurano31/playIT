package com.playit.app.domain.usecase

object SessionManager {
    var activeProfileId: Int = -1
        private set

    fun setActiveProfile(profileId: Int) {
        activeProfileId = profileId
    }

    fun isProfileSelected(): Boolean = activeProfileId != -1

    fun clearSession() {
        activeProfileId = -1
    }
}