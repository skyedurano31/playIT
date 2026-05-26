package com.playit.app.domain.usecase

import javax.inject.Inject

class HeartManager @Inject constructor() {

    private var currentHearts = 5
    private var consecutiveCorrect = 0
    private var wrongAttempts = 0

    fun getHearts(): Int = currentHearts

    fun deductHeart(): Int {
        consecutiveCorrect = 0
        wrongAttempts++
        if (currentHearts > 0) currentHearts--
        return currentHearts
    }

    fun onCorrect(): Boolean {
        wrongAttempts = 0
        consecutiveCorrect++
        // recover 1 heart every 3 consecutive correct
        if (consecutiveCorrect % 3 == 0 && currentHearts < 5) {
            currentHearts++
            return true // heart recovered
        }
        return false
    }

    fun isDepletedAndReset(): Boolean {
        if (currentHearts == 0) {
            currentHearts = 3
            consecutiveCorrect = 0
            wrongAttempts = 0
            return true
        }
        return false
    }

    fun shouldHint(): Boolean = wrongAttempts >= 2

    fun getTotalHeartsLost(): Int = 5 - currentHearts

    fun reset(startHearts: Int = 5) {
        currentHearts = startHearts
        consecutiveCorrect = 0
        wrongAttempts = 0
    }
}