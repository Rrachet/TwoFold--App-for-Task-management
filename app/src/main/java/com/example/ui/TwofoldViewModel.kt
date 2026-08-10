package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class TwofoldViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CoupleRepository

    val allGoals: StateFlow<List<CoupleGoal>>
    val allPrompts: StateFlow<List<DailyPromptAnswer>>
    val allMemories: StateFlow<List<CoupleMemory>>
    val coupleProfile: StateFlow<CoupleProfile?>

    // Selected Goal Filter
    private val _goalStatusFilter = MutableStateFlow("All")
    val goalStatusFilter: StateFlow<String> = _goalStatusFilter.asStateFlow()

    private val _goalCategoryFilter = MutableStateFlow("All")
    val goalCategoryFilter: StateFlow<String> = _goalCategoryFilter.asStateFlow()

    // Date Night Ideas
    private val _dateNightIdeas = MutableStateFlow<List<DateNightIdea>>(emptyList())
    val dateNightIdeas: StateFlow<List<DateNightIdea>> = _dateNightIdeas.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CoupleRepository(database.coupleDao())

        allGoals = repository.allGoals.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allPrompts = repository.allPrompts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allMemories = repository.allMemories.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        coupleProfile = repository.coupleProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        // Seed initial data on startup
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            generateDefaultDateNightIdeas()
        }
    }

    fun setGoalStatusFilter(status: String) {
        _goalStatusFilter.value = status
    }

    fun setGoalCategoryFilter(category: String) {
        _goalCategoryFilter.value = category
    }

    // Goal Actions
    fun addGoal(
        title: String,
        description: String,
        category: String,
        targetDate: String,
        assignedPartner: String,
        status: String
    ) {
        viewModelScope.launch {
            val goal = CoupleGoal(
                title = title.ifBlank { "New Shared Goal" },
                description = description,
                category = category,
                targetDate = targetDate.ifBlank { "Flexible" },
                progressPercent = if (status == "Completed") 100 else 0,
                assignedPartner = assignedPartner,
                status = status
            )
            repository.insertGoal(goal)
        }
    }

    fun updateGoalProgress(goal: CoupleGoal, newProgress: Int) {
        viewModelScope.launch {
            val clamped = newProgress.coerceIn(0, 100)
            val updatedStatus = if (clamped == 100) "Completed" else if (goal.status == "Completed") "In Progress" else goal.status
            val updated = goal.copy(
                progressPercent = clamped,
                status = updatedStatus,
                completedAt = if (clamped == 100) System.currentTimeMillis() else goal.completedAt
            )
            repository.updateGoal(updated)

            // Auto-create memory if goal completed
            if (clamped == 100 && goal.status != "Completed") {
                val todayStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
                repository.insertMemory(
                    CoupleMemory(
                        title = "Accomplished Goal: ${goal.title}",
                        description = "We achieved our shared goal together! ${goal.description}",
                        dateString = todayStr,
                        emotionTag = "Loved",
                        relatedGoalId = goal.id
                    )
                )
            }
        }
    }

    fun toggleGoalFavorite(goal: CoupleGoal) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(isFavorite = !goal.isFavorite))
        }
    }

    fun deleteGoal(goal: CoupleGoal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // Prompt Actions
    fun savePromptAnswers(
        prompt: DailyPromptAnswer,
        answerA: String,
        answerB: String,
        mood: Int
    ) {
        viewModelScope.launch {
            val updated = prompt.copy(
                partnerAAnswer = answerA,
                partnerBAnswer = answerB,
                moodRating = mood,
                isRevealed = answerA.isNotBlank() || answerB.isNotBlank()
            )
            repository.updatePrompt(updated)
        }
    }

    fun addCustomPrompt(question: String, category: String) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val prompt = DailyPromptAnswer(
                promptDate = todayStr,
                question = question,
                category = category
            )
            repository.insertPrompt(prompt)
        }
    }

    // Memory Actions
    fun addMemory(
        title: String,
        description: String,
        dateString: String,
        location: String,
        emotionTag: String
    ) {
        viewModelScope.launch {
            val todayStr = if (dateString.isBlank()) {
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
            } else dateString

            repository.insertMemory(
                CoupleMemory(
                    title = title.ifBlank { "Special Moment" },
                    description = description,
                    dateString = todayStr,
                    location = location,
                    emotionTag = emotionTag
                )
            )
        }
    }

    fun deleteMemory(memory: CoupleMemory) {
        viewModelScope.launch {
            repository.deleteMemory(memory)
        }
    }

    // Profile Actions
    fun updateProfile(
        partnerA: String,
        partnerB: String,
        anniversary: String,
        loveLangA: String,
        loveLangB: String,
        motto: String
    ) {
        viewModelScope.launch {
            val updated = CoupleProfile(
                id = 1,
                partnerAName = partnerA.ifBlank { "Alex" },
                partnerBName = partnerB.ifBlank { "Sam" },
                anniversaryDate = anniversary.ifBlank { "2023-06-14" },
                loveLanguageA = loveLangA,
                loveLanguageB = loveLangB,
                coupleMotto = motto
            )
            repository.saveProfile(updated)
        }
    }

    private fun generateDefaultDateNightIdeas() {
        _dateNightIdeas.value = listOf(
            DateNightIdea(
                title = "At-Home Stargazing & Wine Picnic",
                description = "Lay out blankets on the balcony or backyard, play ambient acoustic tunes, and share stargazing apps.",
                priceTier = "$",
                vibe = "Cozy & Romantic",
                duration = "2 hours"
            ),
            DateNightIdea(
                title = "Sunset Kayaking or Paddleboarding",
                description = "Rent a tandem kayak or paddleboards during golden hour and watch sunset reflections over water.",
                priceTier = "$$",
                vibe = "Adventurous",
                duration = "3 hours"
            ),
            DateNightIdea(
                title = "Blindfold Taste-Testing Dessert Night",
                description = "Pick up 4 different bakeries' pastries or ice cream flavors and guess the flavors blindfolded.",
                priceTier = "$",
                vibe = "Playful & Fun",
                duration = "1.5 hours"
            ),
            DateNightIdea(
                title = "Speakeasy Cocktail & Jazz Lounge",
                description = "Dress in vintage elegance, find a hidden speakeasy cocktail bar, and enjoy live saxophone.",
                priceTier = "$$$",
                vibe = "Sophisticated",
                duration = "3 hours"
            ),
            DateNightIdea(
                title = "Couple's Pottery & Clay Sculpting",
                description = "Book a pottery wheel workshop or buy air-dry clay at home to make mugs for each other.",
                priceTier = "$$",
                vibe = "Creative & Heartfelt",
                duration = "2.5 hours"
            )
        )
    }
}

data class DateNightIdea(
    val title: String,
    val description: String,
    val priceTier: String,
    val vibe: String,
    val duration: String
)
