package com.example.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.CoupleProfile
import com.example.ui.theme.NaturalPrimary
import com.example.ui.theme.NaturalSecondary
import com.example.ui.theme.PartnerAAvatarBg
import com.example.ui.theme.PartnerBAvatarBg
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: TwofoldViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.coupleProfile.collectAsState()
    val goals by viewModel.allGoals.collectAsState()
    val memories by viewModel.allMemories.collectAsState()
    val prompts by viewModel.allPrompts.collectAsState()

    var isEditing by remember { mutableStateOf(false) }

    var partnerAInput by remember(profile) { mutableStateOf(profile?.partnerAName ?: "Alex") }
    var partnerBInput by remember(profile) { mutableStateOf(profile?.partnerBName ?: "Sam") }
    var anniversaryInput by remember(profile) { mutableStateOf(profile?.anniversaryDate ?: "2023-06-14") }
    var loveLangAInput by remember(profile) { mutableStateOf(profile?.loveLanguageA ?: "Quality Time") }
    var loveLangBInput by remember(profile) { mutableStateOf(profile?.loveLanguageB ?: "Words of Affirmation") }
    var mottoInput by remember(profile) { mutableStateOf(profile?.coupleMotto ?: "Building our dream life, hand in hand.") }

    val loveLanguages = listOf("Quality Time", "Words of Affirmation", "Physical Touch", "Acts of Service", "Receiving Gifts")

    // Calculate days together
    val daysTogether = remember(profile?.anniversaryDate) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(profile?.anniversaryDate ?: "2023-06-14")
            if (date != null) {
                val diff = Date().time - date.time
                (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)
            } else 1153
        } catch (e: Exception) {
            1153
        }
    }

    val completedGoalsCount = goals.count { it.status == "Completed" }
    val totalGoalsCount = goals.size
    val totalMemoriesCount = memories.size
    val answeredPromptsCount = prompts.count { it.partnerAAnswer.isNotBlank() || it.partnerBAnswer.isNotBlank() }

    LazyColumn(
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 120.dp,
            start = 16.dp,
            end = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Grand Couple Card Header
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_hero_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(NaturalPrimary, NaturalSecondary, Color(0xFF8B5A52))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy((-12).dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LargeAvatarCircle(name = partnerAInput, color = PartnerAAvatarBg)
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(horizontal = 2.dp)
                            )
                            LargeAvatarCircle(name = partnerBInput, color = PartnerBAvatarBg)
                        }

                        Text(
                            text = "$partnerAInput & $partnerBInput",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "✨ $daysTogether Days of Shared Love",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        Text(
                            text = "\"$mottoInput\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White.copy(alpha = 0.95f),
                                fontWeight = FontWeight.Medium
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // Stats Bar Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Goals Completed",
                    value = "$completedGoalsCount / $totalGoalsCount",
                    icon = Icons.Default.EmojiEvents,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Memories Logged",
                    value = "$totalMemoriesCount",
                    icon = Icons.Default.PhotoLibrary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Sparks Shared",
                    value = "$answeredPromptsCount",
                    icon = Icons.Default.AutoAwesome,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Edit / Save Settings Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Couple Connection Details",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                TextButton(
                    onClick = {
                        if (isEditing) {
                            viewModel.updateProfile(
                                partnerA = partnerAInput,
                                partnerB = partnerBInput,
                                anniversary = anniversaryInput,
                                loveLangA = loveLangAInput,
                                loveLangB = loveLangBInput,
                                motto = mottoInput
                            )
                        }
                        isEditing = !isEditing
                    },
                    modifier = Modifier.testTag("edit_save_profile_button")
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isEditing) "Save Profile" else "Edit Details", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isEditing) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_card")
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = partnerAInput,
                            onValueChange = { partnerAInput = it },
                            label = { Text("Partner A Name") },
                            modifier = Modifier.fillMaxWidth().testTag("input_partner_a_name")
                        )

                        OutlinedTextField(
                            value = partnerBInput,
                            onValueChange = { partnerBInput = it },
                            label = { Text("Partner B Name") },
                            modifier = Modifier.fillMaxWidth().testTag("input_partner_b_name")
                        )

                        OutlinedTextField(
                            value = anniversaryInput,
                            onValueChange = { anniversaryInput = it },
                            label = { Text("Anniversary Date (YYYY-MM-DD)") },
                            modifier = Modifier.fillMaxWidth().testTag("input_anniversary_date")
                        )

                        OutlinedTextField(
                            value = mottoInput,
                            onValueChange = { mottoInput = it },
                            label = { Text("Couple Motto / Quote") },
                            modifier = Modifier.fillMaxWidth().testTag("input_couple_motto")
                        )
                    }
                }
            }
        }

        // Love Languages Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("love_languages_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Love Languages",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Partner A Love Language
                        LoveLanguageBox(
                            partnerName = partnerAInput,
                            selectedLanguage = loveLangAInput,
                            isEditing = isEditing,
                            onSelect = { loveLangAInput = it },
                            languages = loveLanguages,
                            modifier = Modifier.weight(1f)
                        )

                        // Partner B Love Language
                        LoveLanguageBox(
                            partnerName = partnerBInput,
                            selectedLanguage = loveLangBInput,
                            isEditing = isEditing,
                            onSelect = { loveLangBInput = it },
                            languages = loveLanguages,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LargeAvatarCircle(name: String, color: Color) {
    Surface(
        shape = CircleShape,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(3.dp, Color.White),
        modifier = Modifier.size(56.dp)
    ) {
        Box(
            modifier = Modifier.background(color).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(12.dp).fillMaxWidth()
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun LoveLanguageBox(
    partnerName: String,
    selectedLanguage: String,
    isEditing: Boolean,
    onSelect: (String) -> Unit,
    languages: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "$partnerName's Primary",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                text = selectedLanguage,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )

            if (isEditing) {
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(languages) { lang ->
                        FilterChip(
                            selected = selectedLanguage == lang,
                            onClick = { onSelect(lang) },
                            label = { Text(lang, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }
        }
    }
}
