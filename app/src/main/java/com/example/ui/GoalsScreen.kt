package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CoupleGoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    viewModel: TwofoldViewModel,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.allGoals.collectAsState()
    val statusFilter by viewModel.goalStatusFilter.collectAsState()
    val categoryFilter by viewModel.goalCategoryFilter.collectAsState()
    val profile by viewModel.coupleProfile.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var goalToDelete by remember { mutableStateOf<CoupleGoal?>(null) }

    val categories = listOf("All", "Travel & Adventure", "Nest & Home", "Wellness & Growth", "Date Nights")
    val statuses = listOf("All", "In Progress", "Dream Bucket", "Completed")

    // Filtered goals
    val filteredGoals = goals.filter { goal ->
        val matchesStatus = when (statusFilter) {
            "All" -> true
            else -> goal.status == statusFilter
        }
        val matchesCategory = when (categoryFilter) {
            "All" -> true
            else -> goal.category == categoryFilter
        }
        matchesStatus && matchesCategory
    }

    val totalCompleted = goals.count { it.status == "Completed" }
    val totalGoals = goals.size
    val overallProgress = if (totalGoals > 0) ((totalCompleted.toFloat() / totalGoals) * 100).toInt() else 0

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Goal") },
                text = { Text("New Goal", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_goal_fab")
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
            // Hero Banner Overview
            item {
                CoupleHeroBanner(
                    title = "Shared Goals & Dreams",
                    subtitle = "$totalCompleted of $totalGoals milestones accomplished ($overallProgress% completed)",
                    daysTogether = 1153, // Dynamically computed in Profile screen or fallback
                    partnerA = profile?.partnerAName ?: "Alex",
                    partnerB = profile?.partnerBName ?: "Sam"
                )
            }

            // Status Filter Tabs
            item {
                Text(
                    text = "Goal Status",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statuses.forEach { status ->
                        val isSelected = statusFilter == status
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setGoalStatusFilter(status) },
                            label = { Text(status) },
                            modifier = Modifier.testTag("status_chip_${status.lowercase().replace(" ", "_")}")
                        )
                    }
                }
            }

            // Category Filter Scroll
            item {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            category = category,
                            isSelected = categoryFilter == category,
                            onClick = { viewModel.setGoalCategoryFilter(category) }
                        )
                    }
                }
            }

            // Goal List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Milestones (${filteredGoals.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Empty State
            if (filteredGoals.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .testTag("empty_goals_card")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No shared goals found",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Tap the + button below to create your next couple adventure or bucket list dream!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredGoals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onProgressChange = { newProgress -> viewModel.updateGoalProgress(goal, newProgress) },
                        onFavoriteToggle = { viewModel.toggleGoalFavorite(goal) },
                        onDeleteClick = { goalToDelete = goal }
                    )
                }
            }
        }
    }

    // Add Goal Dialog
    if (showAddDialog) {
        AddGoalDialog(
            partnerA = profile?.partnerAName ?: "Alex",
            partnerB = profile?.partnerBName ?: "Sam",
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, category, targetDate, partner, status ->
                viewModel.addGoal(title, desc, category, targetDate, partner, status)
                showAddDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    goalToDelete?.let { goal ->
        AlertDialog(
            onDismissRequest = { goalToDelete = null },
            title = { Text("Delete Goal?") },
            text = { Text("Are you sure you want to remove '${goal.title}' from your shared goals?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGoal(goal)
                        goalToDelete = null
                    },
                    modifier = Modifier.testTag("confirm_delete_goal_button")
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { goalToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun GoalCard(
    goal: CoupleGoal,
    onProgressChange: (Int) -> Unit,
    onFavoriteToggle: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("goal_card_${goal.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Category Badge, Favorite, Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = goal.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.testTag("favorite_button_${goal.id}")
                    ) {
                        Icon(
                            imageVector = if (goal.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (goal.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.testTag("delete_button_${goal.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Title & Description
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            if (goal.description.isNotBlank()) {
                Text(
                    text = goal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Details Badges (Target Date & Assigned Partner)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = goal.targetDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "With ${goal.assignedPartner}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Progress Slider & Percentage
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${goal.progressPercent}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                LinearProgressIndicator(
                    progress = { goal.progressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )

                // Quick Increment Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onProgressChange(goal.progressPercent + 10) },
                        modifier = Modifier.testTag("increment_progress_button_${goal.id}")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("+10% Progress", style = MaterialTheme.typography.labelSmall)
                    }

                    if (goal.progressPercent < 100) {
                        TextButton(
                            onClick = { onProgressChange(100) },
                            modifier = Modifier.testTag("complete_goal_button_${goal.id}")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Mark Accomplished", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    partnerA: String,
    partnerB: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, category: String, targetDate: String, partner: String, status: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Travel & Adventure") }
    var targetDate by remember { mutableStateOf("") }
    var partner by remember { mutableStateOf("Both") }
    var status by remember { mutableStateOf("In Progress") }

    val categories = listOf("Travel & Adventure", "Nest & Home", "Wellness & Growth", "Date Nights")
    val partners = listOf("Both", partnerA, partnerB)
    val statuses = listOf("In Progress", "Dream Bucket")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Shared Goal",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title") },
                    placeholder = { Text("e.g., Road trip along Pacific Coast") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_goal_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Details") },
                    placeholder = { Text("Why this matters to us...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_goal_desc_input")
                )

                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target Date / Timeline") },
                    placeholder = { Text("e.g. Summer 2027") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_goal_date_input")
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

                Text("Partner Assignment", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    partners.forEach { p ->
                        FilterChip(
                            selected = partner == p,
                            onClick = { partner = p },
                            label = { Text(p) }
                        )
                    }
                }

                Text("Type", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    statuses.forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description, category, targetDate, partner, status)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("confirm_add_goal_button")
            ) {
                Text("Save Goal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
