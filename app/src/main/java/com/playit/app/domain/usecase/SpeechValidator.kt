package com.playit.app.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechValidator @Inject constructor() {

    private val acceptedWords: Map<String, List<String>> = mapOf(
        "M"  to listOf("m", "em", "muh", "mmm", "ma", "me", "mm"),
        "A"  to listOf("a", "ah", "ay", "aah", "aa"),
        "S"  to listOf("s", "es", "ss", "sss", "se"),
        "I"  to listOf("i", "ih", "ee", "iy"),
        "N"  to listOf("n", "en", "nn", "nnn"),
        "T"  to listOf("t", "te", "tuh", "tt"),
        "O"  to listOf("o", "oh", "oo", "aw"),
        "L"  to listOf("l", "el", "ll", "lll"),
        "U"  to listOf("u", "uh", "oo", "yoo"),
        "B"  to listOf("b", "be", "buh", "bb"),
        "K"  to listOf("k", "ka", "kuh", "key"),
        "D"  to listOf("d", "de", "duh", "dd"),
        "G"  to listOf("g", "ge", "guh", "gg"),
        "P"  to listOf("p", "pe", "puh", "pp"),
        "R"  to listOf("r", "ar", "rr", "rrr"),
        "E"  to listOf("e", "eh", "ee", "ay"),
        "H"  to listOf("h", "ha", "huh", "hh"),
        "W"  to listOf("w", "wa", "wuh", "dub"),
        "F"  to listOf("f", "ef", "ff", "fff"),
        "J"  to listOf("j", "je", "juh", "jay"),
        "C"  to listOf("c", "se", "see", "kuh"),
        "Q"  to listOf("q", "kw", "cue", "kyoo"),
        "V"  to listOf("v", "ve", "vuh", "vee"),
        "X"  to listOf("x", "ex", "eks", "zz"),
        "Y"  to listOf("y", "ya", "yuh", "why"),
        "Z"  to listOf("z", "ze", "zuh", "zee"),
        "NG" to listOf("ng", "ing", "eng", "ang"),
        "NY" to listOf("ny", "ni", "nya", "nyi")
    )

    fun validate(
        recognizedText: String,
        targetLetter: String,
        confidenceThreshold: Float = 0.75f
    ): Boolean {
        if (recognizedText.isBlank()) return false
        val accepted = acceptedWords[targetLetter.uppercase()] ?: return false
        val cleaned = recognizedText.lowercase().trim()
        return accepted.any { word ->
            cleaned.contains(word) || cleaned == word
        }
    }

    fun getAcceptedWords(letter: String): List<String> {
        return acceptedWords[letter.uppercase()] ?: emptyList()
    }
}