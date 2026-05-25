package com.playit.app.data.local.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.playit.app.data.local.entity.LetterGroup
import com.playit.app.data.local.entity.LetterGroupMember
import com.playit.app.data.local.entity.Phoneme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

class DatabaseSeeder(
    private val database: Provider<AppDatabase>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            seedAll()
        }
    }

    private suspend fun seedAll() {
        seedPhonemes()
        seedLetterGroups()
        seedLetterGroupMembers()
    }

    private suspend fun seedPhonemes() {
        val phonemes = listOf(
            Phoneme(phonemeId = 1,  letter = "M",  audioPath = "audio/phonemes/phoneme_m.mp3",  imagePath = "images/phonemes/m_image.png",  exampleWord = "Mouse"),
            Phoneme(phonemeId = 2,  letter = "A",  audioPath = "audio/phonemes/phoneme_a.mp3",  imagePath = "images/phonemes/a_image.png",  exampleWord = "Apple"),
            Phoneme(phonemeId = 3,  letter = "S",  audioPath = "audio/phonemes/phoneme_s.mp3",  imagePath = "images/phonemes/s_image.png",  exampleWord = "Sun"),
            Phoneme(phonemeId = 4,  letter = "I",  audioPath = "audio/phonemes/phoneme_i.mp3",  imagePath = "images/phonemes/i_image.png",  exampleWord = "Igloo"),
            Phoneme(phonemeId = 5,  letter = "N",  audioPath = "audio/phonemes/phoneme_n.mp3",  imagePath = "images/phonemes/n_image.png",  exampleWord = "Nest"),
            Phoneme(phonemeId = 6,  letter = "T",  audioPath = "audio/phonemes/phoneme_t.mp3",  imagePath = "images/phonemes/t_image.png",  exampleWord = "Tiger"),
            Phoneme(phonemeId = 7,  letter = "O",  audioPath = "audio/phonemes/phoneme_o.mp3",  imagePath = "images/phonemes/o_image.png",  exampleWord = "Orange"),
            Phoneme(phonemeId = 8,  letter = "L",  audioPath = "audio/phonemes/phoneme_l.mp3",  imagePath = "images/phonemes/l_image.png",  exampleWord = "Lion"),
            Phoneme(phonemeId = 9,  letter = "U",  audioPath = "audio/phonemes/phoneme_u.mp3",  imagePath = "images/phonemes/u_image.png",  exampleWord = "Umbrella"),
            Phoneme(phonemeId = 10, letter = "B",  audioPath = "audio/phonemes/phoneme_b.mp3",  imagePath = "images/phonemes/b_image.png",  exampleWord = "Ball"),
            Phoneme(phonemeId = 11, letter = "K",  audioPath = "audio/phonemes/phoneme_k.mp3",  imagePath = "images/phonemes/k_image.png",  exampleWord = "Kite"),
            Phoneme(phonemeId = 12, letter = "D",  audioPath = "audio/phonemes/phoneme_d.mp3",  imagePath = "images/phonemes/d_image.png",  exampleWord = "Dog"),
            Phoneme(phonemeId = 13, letter = "G",  audioPath = "audio/phonemes/phoneme_g.mp3",  imagePath = "images/phonemes/g_image.png",  exampleWord = "Goat"),
            Phoneme(phonemeId = 14, letter = "P",  audioPath = "audio/phonemes/phoneme_p.mp3",  imagePath = "images/phonemes/p_image.png",  exampleWord = "Pig"),
            Phoneme(phonemeId = 15, letter = "R",  audioPath = "audio/phonemes/phoneme_r.mp3",  imagePath = "images/phonemes/r_image.png",  exampleWord = "Rabbit"),
            Phoneme(phonemeId = 16, letter = "E",  audioPath = "audio/phonemes/phoneme_e.mp3",  imagePath = "images/phonemes/e_image.png",  exampleWord = "Egg"),
            Phoneme(phonemeId = 17, letter = "H",  audioPath = "audio/phonemes/phoneme_h.mp3",  imagePath = "images/phonemes/h_image.png",  exampleWord = "Hat"),
            Phoneme(phonemeId = 18, letter = "W",  audioPath = "audio/phonemes/phoneme_w.mp3",  imagePath = "images/phonemes/w_image.png",  exampleWord = "Water"),
            Phoneme(phonemeId = 19, letter = "F",  audioPath = "audio/phonemes/phoneme_f.mp3",  imagePath = "images/phonemes/f_image.png",  exampleWord = "Fish"),
            Phoneme(phonemeId = 20, letter = "J",  audioPath = "audio/phonemes/phoneme_j.mp3",  imagePath = "images/phonemes/j_image.png",  exampleWord = "Jar"),
            Phoneme(phonemeId = 21, letter = "C",  audioPath = "audio/phonemes/phoneme_c.mp3",  imagePath = "images/phonemes/c_image.png",  exampleWord = "Cat"),
            Phoneme(phonemeId = 22, letter = "Q",  audioPath = "audio/phonemes/phoneme_q.mp3",  imagePath = "images/phonemes/q_image.png",  exampleWord = "Queen"),
            Phoneme(phonemeId = 23, letter = "V",  audioPath = "audio/phonemes/phoneme_v.mp3",  imagePath = "images/phonemes/v_image.png",  exampleWord = "Van"),
            Phoneme(phonemeId = 24, letter = "X",  audioPath = "audio/phonemes/phoneme_x.mp3",  imagePath = "images/phonemes/x_image.png",  exampleWord = "X-ray"),
            Phoneme(phonemeId = 25, letter = "Y",  audioPath = "audio/phonemes/phoneme_y.mp3",  imagePath = "images/phonemes/y_image.png",  exampleWord = "Yarn"),
            Phoneme(phonemeId = 26, letter = "Z",  audioPath = "audio/phonemes/phoneme_z.mp3",  imagePath = "images/phonemes/z_image.png",  exampleWord = "Zebra"),
            Phoneme(phonemeId = 27, letter = "NG", audioPath = "audio/phonemes/phoneme_ng.mp3", imagePath = "images/phonemes/ng_image.png", exampleWord = "Ring"),
            Phoneme(phonemeId = 28, letter = "NY", audioPath = "audio/phonemes/phoneme_ny.mp3", imagePath = "images/phonemes/ny_image.png", exampleWord = "Canyon")
        )
        database.get().phonemeDao().insertAll(phonemes)
    }

    private suspend fun seedLetterGroups() {
        val groups = listOf(
            LetterGroup(groupId = 1, groupNumber = 1),
            LetterGroup(groupId = 2, groupNumber = 2),
            LetterGroup(groupId = 3, groupNumber = 3),
            LetterGroup(groupId = 4, groupNumber = 4),
            LetterGroup(groupId = 5, groupNumber = 5),
            LetterGroup(groupId = 6, groupNumber = 6),
            LetterGroup(groupId = 7, groupNumber = 7)
        )
        database.get().letterGroupDao().insertAll(groups)
    }

    private suspend fun seedLetterGroupMembers() {
        val members = listOf(
            // Group 1 — M A S I
            LetterGroupMember(groupId = 1, phonemeId = 1,  position = 1),
            LetterGroupMember(groupId = 1, phonemeId = 2,  position = 2),
            LetterGroupMember(groupId = 1, phonemeId = 3,  position = 3),
            LetterGroupMember(groupId = 1, phonemeId = 4,  position = 4),
            // Group 2 — N T O L
            LetterGroupMember(groupId = 2, phonemeId = 5,  position = 1),
            LetterGroupMember(groupId = 2, phonemeId = 6,  position = 2),
            LetterGroupMember(groupId = 2, phonemeId = 7,  position = 3),
            LetterGroupMember(groupId = 2, phonemeId = 8,  position = 4),
            // Group 3 — U B K D
            LetterGroupMember(groupId = 3, phonemeId = 9,  position = 1),
            LetterGroupMember(groupId = 3, phonemeId = 10, position = 2),
            LetterGroupMember(groupId = 3, phonemeId = 11, position = 3),
            LetterGroupMember(groupId = 3, phonemeId = 12, position = 4),
            // Group 4 — G P R E
            LetterGroupMember(groupId = 4, phonemeId = 13, position = 1),
            LetterGroupMember(groupId = 4, phonemeId = 14, position = 2),
            LetterGroupMember(groupId = 4, phonemeId = 15, position = 3),
            LetterGroupMember(groupId = 4, phonemeId = 16, position = 4),
            // Group 5 — H W F J
            LetterGroupMember(groupId = 5, phonemeId = 17, position = 1),
            LetterGroupMember(groupId = 5, phonemeId = 18, position = 2),
            LetterGroupMember(groupId = 5, phonemeId = 19, position = 3),
            LetterGroupMember(groupId = 5, phonemeId = 20, position = 4),
            // Group 6 — C Q V X
            LetterGroupMember(groupId = 6, phonemeId = 21, position = 1),
            LetterGroupMember(groupId = 6, phonemeId = 22, position = 2),
            LetterGroupMember(groupId = 6, phonemeId = 23, position = 3),
            LetterGroupMember(groupId = 6, phonemeId = 24, position = 4),
            // Group 7 — Y Z NG NY
            LetterGroupMember(groupId = 7, phonemeId = 25, position = 1),
            LetterGroupMember(groupId = 7, phonemeId = 26, position = 2),
            LetterGroupMember(groupId = 7, phonemeId = 27, position = 3),
            LetterGroupMember(groupId = 7, phonemeId = 28, position = 4)
        )
        database.get().letterGroupMemberDao().insertAll(members)
    }
}