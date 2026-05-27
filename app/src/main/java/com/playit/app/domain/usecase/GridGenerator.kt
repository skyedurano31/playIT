package com.playit.app.domain.usecase

import com.playit.app.data.local.entity.Phoneme
import com.playit.app.domain.model.GridItem
import javax.inject.Inject

class GridGenerator @Inject constructor() {

    fun generateGrid(
        targetPhoneme: Phoneme,
        masteredPhonemes: List<Phoneme>
    ): List<GridItem> {
        // 3 target cards
        val targets = List(3) {
            GridItem(
                phonemeId = targetPhoneme.phonemeId,
                letter = targetPhoneme.letter,
                imagePath = targetPhoneme.imagePath,
                word = targetPhoneme.exampleWord,
                isTarget = true
            )
        }

        // 2 distractor cards from mastered letters
        val distractors = masteredPhonemes
            .filter { it.phonemeId != targetPhoneme.phonemeId }
            .shuffled()
            .take(2)
            .map { phoneme ->
                GridItem(
                    phonemeId = phoneme.phonemeId,
                    letter = phoneme.letter,
                    imagePath = phoneme.imagePath,
                    word = phoneme.exampleWord,
                    isTarget = false
                )
            }

        // combine and shuffle
        return (targets + distractors).shuffled()
    }
}