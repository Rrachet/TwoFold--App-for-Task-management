package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CoupleDao {
    // Goals
    @Query("SELECT * FROM couple_goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<CoupleGoal>>

    @Query("SELECT * FROM couple_goals WHERE status = :status ORDER BY createdAt DESC")
    fun getGoalsByStatus(status: String): Flow<List<CoupleGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: CoupleGoal): Long

    @Update
    suspend fun updateGoal(goal: CoupleGoal)

    @Delete
    suspend fun deleteGoal(goal: CoupleGoal)

    // Daily Prompts
    @Query("SELECT * FROM daily_prompts ORDER BY createdAt DESC")
    fun getAllPrompts(): Flow<List<DailyPromptAnswer>>

    @Query("SELECT * FROM daily_prompts WHERE promptDate = :date LIMIT 1")
    suspend fun getPromptForDate(date: String): DailyPromptAnswer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: DailyPromptAnswer): Long

    @Update
    suspend fun updatePrompt(prompt: DailyPromptAnswer)

    // Memories
    @Query("SELECT * FROM couple_memories ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<CoupleMemory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: CoupleMemory): Long

    @Delete
    suspend fun deleteMemory(memory: CoupleMemory)

    // Couple Profile
    @Query("SELECT * FROM couple_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<CoupleProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: CoupleProfile)
}
