package com.example.data

import kotlinx.coroutines.flow.Flow

class CoupleRepository(private val dao: CoupleDao) {
    val allGoals: Flow<List<CoupleGoal>> = dao.getAllGoals()
    val allPrompts: Flow<List<DailyPromptAnswer>> = dao.getAllPrompts()
    val allMemories: Flow<List<CoupleMemory>> = dao.getAllMemories()
    val coupleProfile: Flow<CoupleProfile?> = dao.getProfile()

    suspend fun insertGoal(goal: CoupleGoal) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: CoupleGoal) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: CoupleGoal) = dao.deleteGoal(goal)

    suspend fun getPromptForDate(date: String) = dao.getPromptForDate(date)
    suspend fun insertPrompt(prompt: DailyPromptAnswer) = dao.insertPrompt(prompt)
    suspend fun updatePrompt(prompt: DailyPromptAnswer) = dao.updatePrompt(prompt)

    suspend fun insertMemory(memory: CoupleMemory) = dao.insertMemory(memory)
    suspend fun deleteMemory(memory: CoupleMemory) = dao.deleteMemory(memory)

    suspend fun saveProfile(profile: CoupleProfile) = dao.insertOrUpdateProfile(profile)

    suspend fun seedInitialDataIfEmpty() {
        // Seed default profile if missing
        dao.insertOrUpdateProfile(
            CoupleProfile(
                id = 1,
                partnerAName = "Alex",
                partnerBName = "Sam",
                anniversaryDate = "2023-06-14",
                loveLanguageA = "Quality Time",
                loveLanguageB = "Words of Affirmation",
                coupleMotto = "Building our dream life, hand in hand."
            )
        )

        // Seed default goals if empty
        val sampleGoals = listOf(
            CoupleGoal(
                title = "Weekend Cabin Getaway in the Pines",
                description = "Rent a secluded cozy cabin, unplug from phones for 48 hours, and sit by the fireplace.",
                category = "Travel & Adventure",
                targetDate = "Oct 2026",
                progressPercent = 65,
                assignedPartner = "Both",
                status = "In Progress",
                isFavorite = true
            ),
            CoupleGoal(
                title = "Start Our Joint House Down-Payment Fund",
                description = "Save $500 monthly together towards our dream home nest.",
                category = "Nest & Home",
                targetDate = "Dec 2027",
                progressPercent = 35,
                assignedPartner = "Both",
                status = "In Progress",
                isFavorite = true
            ),
            CoupleGoal(
                title = "Weekly Tech-Free Candlelight Dinner",
                description = "Cook a new recipe together every Thursday night with soft jazz music.",
                category = "Date Nights",
                targetDate = "Ongoing",
                progressPercent = 90,
                assignedPartner = "Alex",
                status = "In Progress",
                isFavorite = false
            ),
            CoupleGoal(
                title = "Learn Italian Together before Rome Trip",
                description = "Complete 30 Duolingo lessons and practice conversation during breakfast.",
                category = "Wellness & Growth",
                targetDate = "May 2027",
                progressPercent = 20,
                assignedPartner = "Sam",
                status = "In Progress",
                isFavorite = false
            ),
            CoupleGoal(
                title = "Hot Air Balloon Flight over Kyoto",
                description = "Fly over cherry blossoms during spring festival.",
                category = "Travel & Adventure",
                targetDate = "Spring 2028",
                progressPercent = 0,
                assignedPartner = "Both",
                status = "Dream Bucket",
                isFavorite = true
            )
        )

        for (goal in sampleGoals) {
            dao.insertGoal(goal)
        }

        // Seed daily prompts
        val samplePrompts = listOf(
            DailyPromptAnswer(
                promptDate = "2026-08-10",
                question = "What was a small moment this week when you felt deeply loved or appreciated by me?",
                category = "Deep Intimacy",
                partnerAAnswer = "When you made my favorite coffee in the morning without me even asking!",
                partnerBAnswer = "How you held my hand during the hectic store run yesterday.",
                moodRating = 5,
                isRevealed = true
            ),
            DailyPromptAnswer(
                promptDate = "2026-08-09",
                question = "If we could pause time for 24 hours anywhere in the world together, where would we go?",
                category = "Shared Future",
                partnerAAnswer = "A quiet beach villa in Amalfi Coast listening to the waves.",
                partnerBAnswer = "A cozy wooden lodge in the Swiss Alps watching snowfall.",
                moodRating = 5,
                isRevealed = true
            ),
            DailyPromptAnswer(
                promptDate = "2026-08-11",
                question = "What is a song that instantly reminds you of the night we first met?",
                category = "Memories",
                partnerAAnswer = "",
                partnerBAnswer = "",
                moodRating = 5,
                isRevealed = false
            )
        )

        for (prompt in samplePrompts) {
            dao.insertPrompt(prompt)
        }

        // Seed sample memories
        val sampleMemories = listOf(
            CoupleMemory(
                title = "First Sunrise Hike at Stargaze Peak",
                description = "Woke up at 4 AM, packed hot cocoa thermos, and watched the golden fog rise above the valley.",
                dateString = "July 12, 2026",
                location = "Stargaze Ridge",
                emotionTag = "Adventurous"
            ),
            CoupleMemory(
                title = "Finished Our 1000-Piece Puzzle Night",
                description = "A rainy Sunday evening with cider, fireplace crackle, and placing the final piece together.",
                dateString = "May 28, 2026",
                location = "Living Room Blanket Fort",
                emotionTag = "Loved"
            )
        )

        for (mem in sampleMemories) {
            dao.insertMemory(mem)
        }
    }
}
