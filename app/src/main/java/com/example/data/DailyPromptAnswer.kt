package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_prompts")
data class DailyPromptAnswer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val promptDate: String, // e.g. "2026-08-10"
    val question: String,
    val category: String, // "Deep Intimacy", "Fun & Play", "Shared Future", "Gratitude"
    val partnerAAnswer: String = "",
    val partnerBAnswer: String = "",
    val moodRating: Int = 5, // 1 to 5 intimacy rating
    val isRevealed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
