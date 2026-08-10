package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.*
import com.example.ui.theme.TwofoldTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TwofoldViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TwofoldTheme {
                TwofoldApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun TwofoldApp(viewModel: TwofoldViewModel) {
    var currentRoute by remember { mutableStateOf("goals") }
    val profile by viewModel.coupleProfile.collectAsState()

    val partnerA = profile?.partnerAName ?: "Alex"
    val partnerB = profile?.partnerBName ?: "Sam"

    Scaffold(
        topBar = {
            TwofoldTopBar(
                title = when (currentRoute) {
                    "goals" -> "Twofold Goals"
                    "sparks" -> "Daily Sparks"
                    "memories" -> "Memories Vault"
                    "profile" -> "Our Journey"
                    else -> "Twofold"
                },
                partnerA = partnerA,
                partnerB = partnerB,
                onProfileClick = { currentRoute = "profile" }
            )
        },
        bottomBar = {
            TwofoldBottomBar(
                selectedRoute = currentRoute,
                onNavigate = { route -> currentRoute = route }
            )
        },
        modifier = Modifier.fillMaxSize().testTag("twofold_main_scaffold")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                "goals" -> GoalsScreen(viewModel = viewModel)
                "sparks" -> SparksScreen(viewModel = viewModel)
                "memories" -> MemoriesScreen(viewModel = viewModel)
                "profile" -> ProfileScreen(viewModel = viewModel)
            }
        }
    }
}

