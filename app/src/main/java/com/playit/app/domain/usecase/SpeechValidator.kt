package com.playit.app.domain.usecase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeechValidator @Inject constructor() {

    private val acceptedWords: Map<String, List<String>> = mapOf(
        "M"  to listOf("m", "em", "muh", "mmm", "ma", "me", "mm", "um", "am", "mom", "him", "ham"),
        "S"  to listOf("s", "es", "ss", "sss", "se", "see", "say", "so", "sun"),
        "A"  to listOf("a", "ah", "ay", "aah", "aa", "apple", "at", "an"),
        "I"  to listOf("i", "ih", "I", "iii", "ihhh", "iiih", "ee","iy"),
        "O"  to listOf("o", "oh", "oo", "aw", "or", "on", "of"),
        "B"  to listOf("b", "be", "buh", "bb", "by", "bay", "bee"),
        "E"  to listOf("e", "eh", "ee", "ay", "egg", "end", "every"),
        "U"  to listOf("u", "uh", "oo", "yoo", "up", "us", "under"),
        "T"  to listOf("t", "te", "tuh", "tt", "to", "too", "the", "tea"),
        "K"  to listOf("k", "ka", "kuh", "key", "kay", "ca", "ok"),
        "L"  to listOf("l", "el", "ll", "lll", "la", "lay", "lee"),
        "Y"  to listOf("y", "ya", "yuh", "why", "yes", "yay", "you"),
        "N"  to listOf("n", "en", "nn", "nnn", "no", "now", "knee"),
        "G"  to listOf("g", "ge", "guh", "gg", "go", "gay", "gee"),
        "P"  to listOf("p", "pe", "puh", "pp", "pay", "pee", "pie"),
        "R"  to listOf("r", "ar", "rr", "rrr", "ray", "row", "rah"),
        "D"  to listOf("d", "de", "duh", "dd", "day", "do", "dee"),
        "H"  to listOf("h", "ha", "huh", "hh", "hay", "he", "hi"),
        "W"  to listOf("w", "wa", "wuh", "dub", "way", "we", "why"),
        "C"  to listOf("c", "se", "see", "kuh", "ca", "cow", "cup"),
        "F"  to listOf("f", "ef", "ff", "fff", "fee", "few", "far"),
        "J"  to listOf("j", "je", "juh", "jay", "jaw", "joy", "joe"),
        "Q"  to listOf("q", "kw", "cue", "kyoo", "queue", "quit"),
        "V"  to listOf("v", "ve", "vuh", "vee", "van", "very", "via"),
        "X"  to listOf("x", "ex", "eks", "zz", "exit", "extra"),
        "Z"  to listOf("z", "ze", "zuh", "zee", "zoo", "zero", "zip"),
        "NG" to listOf("ng", "ing", "eng", "ang", "ring", "sing", "king"),
        "NY" to listOf("ny", "ni", "nya", "nyi", "canyon", "onion")
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