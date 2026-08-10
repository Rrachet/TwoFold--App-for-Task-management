package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "couple_goals")
data class CoupleGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val category: String, // "Travel & Adventure", "Nest & Home", "Wellness", "Date Nights", "Future Milestones"
    val targetDate: String = "",
    val progressPercent: Int = 0, // 0 to 100
    val assignedPartner: String = "Both", // "Both", "Alex", "Sam"
    val status: String = "In Progress", // "In Progress", "Completed", "Dream Bucket"
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
