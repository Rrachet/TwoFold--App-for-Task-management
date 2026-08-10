package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "couple_profile")
data class CoupleProfile(
    @PrimaryKey val id: Int = 1,
    val partnerAName: String = "Alex",
    val partnerBName: String = "Sam",
    val anniversaryDate: String = "2023-06-14", // YYYY-MM-DD
    val loveLanguageA: String = "Quality Time",
    val loveLanguageB: String = "Words of Affirmation",
    val coupleMotto: String = "Growing together, one dream at a time.",
    val partnerAAvatarColor: Long = 0xFF9E2A4B,
    val partnerBAvatarColor: Long = 0xFFD96B52
)
