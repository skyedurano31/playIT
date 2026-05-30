package com.playit.app.domain.usecase

import com.playit.app.data.local.entity.BlendItWord
import com.playit.app.domain.model.TileItem
import com.playit.app.domain.repository.BlendItWordRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlendItWordSelector @Inject constructor(
    private val blendItWordRepository: BlendItWordRepository
) {
    companion object {
        const val WORDS_PER_SESSION = 5
    }

    suspend fun selectWords(
        currentGroupId: Int,
        masteredGroupIds: List<Int>
    ): List<BlendItWord> {
        val allGroupIds = (masteredGroupIds + currentGroupId).distinct()
        val allWords = blendItWordRepository.getWordsByGroups(allGroupIds)

        // ensure at least 1 word from current group
        val currentGroupWords = allWords.filter { it.groupId == currentGroupId }
        val otherWords = allWords.filter { it.groupId != currentGroupId }.shuffled()

        val selected = mutableListOf<BlendItWord>()

        // add 1-2 from current group first
        selected.addAll(currentGroupWords.shuffled().take(2))

        // fill rest from all words
        val remaining = otherWords
            .filter { it !in selected }
            .take(WORDS_PER_SESSION - selected.size)
        selected.addAll(remaining)

        return selected.shuffled().take(WORDS_PER_SESSION)
    }

    fun generateTiles(word: String, allLetters: List<String>): List<TileItem> {
        val correctTiles = word.map { letter ->
            TileItem(letter = letter.toString(), isDistractor = false)
        }

        // add 1-2 distractor tiles
        val distractors = allLetters
            .filter { it !in word.map { c -> c.toString() } }
            .shuffled()
            .take(2)
            .mapIndexed { index, letter ->
                TileItem(letter = letter, isDistractor = true, id = index + 100)
            }

        return (correctTiles + distractors).shuffled()
            .mapIndexed { index, tile -> tile.copy(id = index) }
    }
}