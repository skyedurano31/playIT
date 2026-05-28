package com.playit.app.domain.usecase

import javax.inject.Inject

enum class LetterStatus { GREEN, YELLOW, RED, NOT_STARTED }

data class LetterPerformance(
    val phonemeId: Int,
    val letter: String,
    val accuracy: Float,
    val status: LetterStatus,
    val starsEarned: Int,
    val isCompleted: Boolean
)

class LetterStatusCalculator @Inject constructor() {

    fun calculate(
        phonemeId: Int,
        letter: String,
        correctCount: Int,
        totalCount: Int,
        starsEarned: Int,
        isCompleted: Boolean
    ): LetterPerformance {
        if (totalCount == 0) {
            return LetterPerformance(
                phonemeId = phonemeId,
                letter = letter,
                accuracy = 0f,
                status = LetterStatus.NOT_STARTED,
                starsEarned = starsEarned,
                isCompleted = isCompleted
            )
        }

        val accuracy = correctCount.toFloat() / totalCount.toFloat()
        val status = when {
            accuracy >= 0.8f -> LetterStatus.GREEN
            accuracy >= 0.5f -> LetterStatus.YELLOW
            else -> LetterStatus.RED
        }

        return LetterPerformance(
            phonemeId = phonemeId,
            letter = letter,
            accuracy = accuracy,
            status = status,
            starsEarned = starsEarned,
            isCompleted = isCompleted
        )
    }
}