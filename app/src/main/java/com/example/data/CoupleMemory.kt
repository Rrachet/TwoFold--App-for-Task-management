package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "couple_memories")
data class CoupleMemory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val dateString: String,
    val location: String = "",
    val emotionTag: String = "Loved", // "Loved", "Grateful", "Adventurous", "Peaceful"
    val relatedGoalId: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)
