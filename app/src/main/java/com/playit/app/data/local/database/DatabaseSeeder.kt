package com.playit.app.data.local.database

import com.playit.app.data.local.entity.BlendItWord
import com.playit.app.data.local.entity.LetterGroup
import com.playit.app.data.local.entity.LetterGroupMember
import com.playit.app.data.local.entity.Phoneme

object DatabaseSeeder {

    fun getPhonemes() = listOf(
        Phoneme(phonemeId = 1,  letter = "M",  audioPath = "audio/phonemes/phoneme_m.mp3",  imagePath = "images/phonemes/m_image.png",  exampleWord = "Monkey"),
        Phoneme(phonemeId = 2,  letter = "S",  audioPath = "audio/phonemes/phoneme_s.mp3",  imagePath = "images/phonemes/s_image.png",  exampleWord = "Snake"),
        Phoneme(phonemeId = 3,  letter = "A",  audioPath = "audio/phonemes/phoneme_a.mp3",  imagePath = "images/phonemes/a_image.png",  exampleWord = "Apple"),
        Phoneme(phonemeId = 4,  letter = "I",  audioPath = "audio/phonemes/phoneme_i.mp3",  imagePath = "images/phonemes/i_image.png",  exampleWord = "Igloo"),
        Phoneme(phonemeId = 5,  letter = "O",  audioPath = "audio/phonemes/phoneme_o.mp3",  imagePath = "images/phonemes/o_image.png",  exampleWord = "Octopus"),
        Phoneme(phonemeId = 6, letter = "B",  audioPath = "audio/phonemes/phoneme_b.mp3",  imagePath = "images/phonemes/b_image.png",  exampleWord = "Ball"),
        Phoneme(phonemeId = 7, letter = "E",  audioPath = "audio/phonemes/phoneme_e.mp3",  imagePath = "images/phonemes/e_image.png",  exampleWord = "Egg"),
        Phoneme(phonemeId = 8,  letter = "U",  audioPath = "audio/phonemes/phoneme_u.mp3",  imagePath = "images/phonemes/u_image.png",  exampleWord = "Umbrella"),
        Phoneme(phonemeId = 9,  letter = "T",  audioPath = "audio/phonemes/phoneme_t.mp3",  imagePath = "images/phonemes/t_image.png",  exampleWord = "Tiger"),
        Phoneme(phonemeId = 10, letter = "K",  audioPath = "audio/phonemes/phoneme_k.mp3",  imagePath = "images/phonemes/k_image.png",  exampleWord = "Kite"),
        Phoneme(phonemeId = 11,  letter = "L",  audioPath = "audio/phonemes/phoneme_l.mp3",  imagePath = "images/phonemes/l_image.png",  exampleWord = "Lion"),
        Phoneme(phonemeId = 12, letter = "Y",  audioPath = "audio/phonemes/phoneme_y.mp3",  imagePath = "images/phonemes/y_image.png",  exampleWord = "Yarn"),
        Phoneme(phonemeId = 13,  letter = "N",  audioPath = "audio/phonemes/phoneme_n.mp3",  imagePath = "images/phonemes/n_image.png",  exampleWord = "Nest"),
        Phoneme(phonemeId = 14, letter = "G",  audioPath = "audio/phonemes/phoneme_g.mp3",  imagePath = "images/phonemes/g_image.png",  exampleWord = "Goat"),
        Phoneme(phonemeId = 15, letter = "P",  audioPath = "audio/phonemes/phoneme_p.mp3",  imagePath = "images/phonemes/p_image.png",  exampleWord = "Pig"),
        Phoneme(phonemeId = 16, letter = "R",  audioPath = "audio/phonemes/phoneme_r.mp3",  imagePath = "images/phonemes/r_image.png",  exampleWord = "Rabbit"),
        Phoneme(phonemeId = 17, letter = "D",  audioPath = "audio/phonemes/phoneme_d.mp3",  imagePath = "images/phonemes/d_image.png",  exampleWord = "Dog"),
        Phoneme(phonemeId = 18, letter = "H",  audioPath = "audio/phonemes/phoneme_h.mp3",  imagePath = "images/phonemes/h_image.png",  exampleWord = "Hat"),
        Phoneme(phonemeId = 19, letter = "W",  audioPath = "audio/phonemes/phoneme_w.mp3",  imagePath = "images/phonemes/w_image.png",  exampleWord = "Water"),
        Phoneme(phonemeId = 20, letter = "C",  audioPath = "audio/phonemes/phoneme_c.mp3",  imagePath = "images/phonemes/c_image.png",  exampleWord = "Cat"),
        Phoneme(phonemeId = 21, letter = "F",  audioPath = "audio/phonemes/phoneme_f.mp3",  imagePath = "images/phonemes/f_image.png",  exampleWord = "Fish"),
        Phoneme(phonemeId = 22, letter = "J",  audioPath = "audio/phonemes/phoneme_j.mp3",  imagePath = "images/phonemes/j_image.png",  exampleWord = "Jar"),
        Phoneme(phonemeId = 23, letter = "Q",  audioPath = "audio/phonemes/phoneme_q.mp3",  imagePath = "images/phonemes/q_image.png",  exampleWord = "Queen"),
        Phoneme(phonemeId = 24, letter = "V",  audioPath = "audio/phonemes/phoneme_v.mp3",  imagePath = "images/phonemes/v_image.png",  exampleWord = "Van"),
        Phoneme(phonemeId = 25, letter = "X",  audioPath = "audio/phonemes/phoneme_x.mp3",  imagePath = "images/phonemes/x_image.png",  exampleWord = "X-ray"),
        Phoneme(phonemeId = 26, letter = "Z",  audioPath = "audio/phonemes/phoneme_z.mp3",  imagePath = "images/phonemes/z_image.png",  exampleWord = "Zebra"),
        Phoneme(phonemeId = 27, letter = "NG", audioPath = "audio/phonemes/phoneme_ng.mp3", imagePath = "images/phonemes/ng_image.png", exampleWord = "Ring"),
        Phoneme(phonemeId = 28, letter = "NY", audioPath = "audio/phonemes/phoneme_ny.mp3", imagePath = "images/phonemes/ny_image.png", exampleWord = "Canyon")
    )

    fun getLetterGroups() = listOf(
        LetterGroup(groupId = 1, groupNumber = 1),
        LetterGroup(groupId = 2, groupNumber = 2),
        LetterGroup(groupId = 3, groupNumber = 3),
        LetterGroup(groupId = 4, groupNumber = 4)
    )

    fun getLetterGroupMembers() = listOf(
        LetterGroupMember(groupId = 1, phonemeId = 1, position = 1),  // M
        LetterGroupMember(groupId = 1, phonemeId = 2, position = 2),  // S
        LetterGroupMember(groupId = 1, phonemeId = 3, position = 3),  // A
        LetterGroupMember(groupId = 1, phonemeId = 4, position = 4),  // I
        LetterGroupMember(groupId = 1, phonemeId = 5, position = 5),  // O
        LetterGroupMember(groupId = 1, phonemeId = 6, position = 6),  // B
        LetterGroupMember(groupId = 1, phonemeId = 7, position = 7),  // E

        // ==================== GROUP 2 (7 letters) ====================
        LetterGroupMember(groupId = 2, phonemeId = 8, position = 1),  // U
        LetterGroupMember(groupId = 2, phonemeId = 9, position = 2),  // T
        LetterGroupMember(groupId = 2, phonemeId = 10, position = 3), // K
        LetterGroupMember(groupId = 2, phonemeId = 11, position = 4), // L
        LetterGroupMember(groupId = 2, phonemeId = 12, position = 5), // Y
        LetterGroupMember(groupId = 2, phonemeId = 13, position = 6), // N
        LetterGroupMember(groupId = 2, phonemeId = 14, position = 7), // G

        // ==================== GROUP 3 (7 letters) ====================
        LetterGroupMember(groupId = 3, phonemeId = 15, position = 1), // P
        LetterGroupMember(groupId = 3, phonemeId = 16, position = 2), // R
        LetterGroupMember(groupId = 3, phonemeId = 17, position = 3), // D
        LetterGroupMember(groupId = 3, phonemeId = 18, position = 4), // H
        LetterGroupMember(groupId = 3, phonemeId = 19, position = 5), // W
        LetterGroupMember(groupId = 3, phonemeId = 20, position = 6), // C
        LetterGroupMember(groupId = 3, phonemeId = 21, position = 7), // F

        // ==================== GROUP 4 (7 letters - NG and NY at the end) ====================
        LetterGroupMember(groupId = 4, phonemeId = 22, position = 1), // J
        LetterGroupMember(groupId = 4, phonemeId = 23, position = 2), // Q
        LetterGroupMember(groupId = 4, phonemeId = 24, position = 3), // V
        LetterGroupMember(groupId = 4, phonemeId = 25, position = 4), // X
        LetterGroupMember(groupId = 4, phonemeId = 26, position = 5), // Z
        LetterGroupMember(groupId = 4, phonemeId = 27, position = 6), // NG
        LetterGroupMember(groupId = 4, phonemeId = 28, position = 7)  // NY
    )
    fun getBlendItWords() = listOf(
        // Group 1 — M S A I O B E
        BlendItWord(groupId = 1, word = "SAM", wordPattern = "CVC", audioPath = "audio/words/sam.mp3", imagePath = "images/words/sam.png"),
        BlendItWord(groupId = 1, word = "SIM", wordPattern = "CVC", audioPath = "audio/words/sim.mp3", imagePath = "images/words/sim.png"),
        BlendItWord(groupId = 1, word = "AIM", wordPattern = "VVC", audioPath = "audio/words/aim.mp3", imagePath = "images/words/aim.png"),
        BlendItWord(groupId = 1, word = "MAS", wordPattern = "CVC", audioPath = "audio/words/mas.mp3", imagePath = "images/words/mas.png"),
        BlendItWord(groupId = 1, word = "SAI", wordPattern = "CVC", audioPath = "audio/words/sai.mp3", imagePath = "images/words/sai.png"),

        // Group 2 — N T O L + cumulative
        BlendItWord(groupId = 2, word = "TAN", wordPattern = "CVC", audioPath = "audio/words/tan.mp3", imagePath = "images/words/tan.png"),
        BlendItWord(groupId = 2, word = "NOT", wordPattern = "CVC", audioPath = "audio/words/not.mp3", imagePath = "images/words/not.png"),
        BlendItWord(groupId = 2, word = "LOT", wordPattern = "CVC", audioPath = "audio/words/lot.mp3", imagePath = "images/words/lot.png"),
        BlendItWord(groupId = 2, word = "MIST", wordPattern = "CCVC", audioPath = "audio/words/mist.mp3", imagePath = "images/words/mist.png"),
        BlendItWord(groupId = 2, word = "MINT", wordPattern = "CCVC", audioPath = "audio/words/mint.mp3", imagePath = "images/words/mint.png"),

        // Group 3 — U B K D + cumulative
        BlendItWord(groupId = 3, word = "BUD", wordPattern = "CVC", audioPath = "audio/words/bud.mp3", imagePath = "images/words/bud.png"),
        BlendItWord(groupId = 3, word = "BULK", wordPattern = "CVCC", audioPath = "audio/words/bulk.mp3", imagePath = "images/words/bulk.png"),
        BlendItWord(groupId = 3, word = "DUST", wordPattern = "CVCC", audioPath = "audio/words/dust.mp3", imagePath = "images/words/dust.png"),
        BlendItWord(groupId = 3, word = "BOND", wordPattern = "CVCC", audioPath = "audio/words/bond.mp3", imagePath = "images/words/bond.png"),
        BlendItWord(groupId = 3, word = "TUSK", wordPattern = "CVCC", audioPath = "audio/words/tusk.mp3", imagePath = "images/words/tusk.png"),

        // Group 4 — G P R E + cumulative
        BlendItWord(groupId = 4, word = "GRIP", wordPattern = "CCVC", audioPath = "audio/words/grip.mp3", imagePath = "images/words/grip.png"),
        BlendItWord(groupId = 4, word = "DRUM", wordPattern = "CCVC", audioPath = "audio/words/drum.mp3", imagePath = "images/words/drum.png"),
        BlendItWord(groupId = 4, word = "PEST", wordPattern = "CVCC", audioPath = "audio/words/pest.mp3", imagePath = "images/words/pest.png"),
        BlendItWord(groupId = 4, word = "BURN", wordPattern = "CVCC", audioPath = "audio/words/burn.mp3", imagePath = "images/words/burn.png"),
        BlendItWord(groupId = 4, word = "GLEN", wordPattern = "CCVC", audioPath = "audio/words/glen.mp3", imagePath = "images/words/glen.png"),
    )
}