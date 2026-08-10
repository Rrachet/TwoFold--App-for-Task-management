package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.DailyPromptAnswer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SparksScreen(
    viewModel: TwofoldViewModel,
    modifier: Modifier = Modifier
) {
    val prompts by viewModel.allPrompts.collectAsState()
    val dateIdeas by viewModel.dateNightIdeas.collectAsState()
    val profile by viewModel.coupleProfile.collectAsState()

    var showNewPromptDialog by remember { mutableStateOf(false) }
    var selectedPriceTier by remember { mutableStateOf("All") }

    val partnerA = profile?.partnerAName ?: "Alex"
    val partnerB = profile?.partnerBName ?: "Sam"

    val filteredDateIdeas = if (selectedPriceTier == "All") dateIdeas else dateIdeas.filter { it.priceTier == selectedPriceTier }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewPromptDialog = true },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "New Connection Prompt") },
                text = { Text("Ask Question", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_spark_prompt_fab")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 80.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sparks_hero_card")
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Daily Connection Sparks",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "Answer daily questions together, reveal each other's heartfelt thoughts, and spark unforgettable date nights.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Connection Questions Section
            item {
                Text(
                    text = "Intimacy Check-Ins & Prompts",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            if (prompts.isEmpty()) {
                item {
                    Text(
                        text = "No questions available today. Tap 'Ask Question' to send a spark!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(prompts, key = { it.id }) { prompt ->
                    PromptCard(
                        prompt = prompt,
                        partnerA = partnerA,
                        partnerB = partnerB,
                        onSaveAnswers = { a, b, mood -> viewModel.savePromptAnswers(prompt, a, b, mood) }
                    )
                }
            }

            // Date Night Generator Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Date Night Planner & Ideas",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("All", "$", "$$", "$$$").forEach { tier ->
                            FilterChip(
                                selected = selectedPriceTier == tier,
                                onClick = { selectedPriceTier = tier },
                                label = { Text(tier) }
                            )
                        }
                    }
                }
            }

            items(filteredDateIdeas) { idea ->
                DateNightCard(
                    idea = idea,
                    onAddGoal = {
                        viewModel.addGoal(
                            title = idea.title,
                            description = idea.description,
                            category = "Date Nights",
                            targetDate = "Upcoming Date",
                            assignedPartner = "Both",
                            status = "In Progress"
                        )
                    }
                )
            }
        }
    }

    if (showNewPromptDialog) {
        AddSparkPromptDialog(
            onDismiss = { showNewPromptDialog = false },
            onConfirm = { question, category ->
                viewModel.addCustomPrompt(question, category)
                showNewPromptDialog = false
            }
        )
    }
}

@Composable
fun PromptCard(
    prompt: DailyPromptAnswer,
    partnerA: String,
    partnerB: String,
    onSaveAnswers: (answerA: String, answerB: String, mood: Int) -> Unit
) {
    var answerAInput by remember(prompt.partnerAAnswer) { mutableStateOf(prompt.partnerAAnswer) }
    var answerBInput by remember(prompt.partnerBAnswer) { mutableStateOf(prompt.partnerBAnswer) }
    var moodRatingInput by remember(prompt.moodRating) { mutableIntStateOf(prompt.moodRating) }
    var isExpanded by remember { mutableStateOf(prompt.isRevealed) }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("prompt_card_${prompt.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = prompt.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = prompt.promptDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = prompt.question,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Mood Intimacy Rating Hearts
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Intimacy Vibe: ",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                (1..5).forEach { heart ->
                    IconButton(
                        onClick = {
                            moodRatingInput = heart
                            onSaveAnswers(answerAInput, answerBInput, heart)
                        },
                        modifier = Modifier.size(28.dp).testTag("heart_rating_${prompt.id}_$heart")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Rating $heart",
                            tint = if (heart <= moodRatingInput) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Reveal answers toggle button
            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.testTag("toggle_answers_${prompt.id}")
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isExpanded) "Hide Couple Answers" else "Tap to Reveal Answers",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Partner A Input / Answer
                    OutlinedTextField(
                        value = answerAInput,
                        onValueChange = {
                            answerAInput = it
                            onSaveAnswers(answerAInput, answerBInput, moodRatingInput)
                        },
                        label = { Text("$partnerA's Answer") },
                        placeholder = { Text("Write your response...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("answer_input_a_${prompt.id}")
                    )

                    // Partner B Input / Answer
                    OutlinedTextField(
                        value = answerBInput,
                        onValueChange = {
                            answerBInput = it
                            onSaveAnswers(answerAInput, answerBInput, moodRatingInput)
                        },
                        label = { Text("$partnerB's Answer") },
                        placeholder = { Text("Write your response...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("answer_input_b_${prompt.id}")
                    )
                }
            }
        }
    }
}

@Composable
fun DateNightCard(
    idea: DateNightIdea,
    onAddGoal: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("date_idea_card_${idea.title.lowercase().replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = idea.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = idea.priceTier,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = idea.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(idea.vibe, style = MaterialTheme.typography.labelSmall) }
                    )
                    Text(
                        text = "⏱ ${idea.duration}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Button(
                    onClick = onAddGoal,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("add_date_idea_goal_${idea.title.lowercase().take(10)}")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Goal", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun AddSparkPromptDialog(
    onDismiss: () -> Unit,
    onConfirm: (question: String, category: String) -> Unit
) {
    var question by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Deep Intimacy") }
    val categories = listOf("Deep Intimacy", "Fun & Play", "Shared Future", "Gratitude")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Connection Question", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question Prompt") },
                    placeholder = { Text("e.g. What is one goal we should focus on next month?") },
                    modifier = Modifier.fillMaxWidth().testTag("custom_spark_question_input")
                )

                Text("Category", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (question.isNotBlank()) onConfirm(question, category) },
                enabled = question.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("confirm_add_spark_prompt")
            ) {
                Text("Save Question")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
