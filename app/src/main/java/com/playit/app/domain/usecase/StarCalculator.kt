package com.playit.app.domain.usecase

import javax.inject.Inject

class StarCalculator @Inject constructor() {

    fun calculate(
        correctTaps: Int,
        totalTaps: Int,
        heartsLost: Int
    ): Int {
        val accuracy = if (totalTaps == 0) 0f
        else correctTaps.toFloat() / totalTaps.toFloat()
        return when {
            accuracy == 1f && heartsLost == 0 -> 3
            accuracy >= 0.8f || heartsLost <= 2 -> 2
            else -> 1
        }
    }
}