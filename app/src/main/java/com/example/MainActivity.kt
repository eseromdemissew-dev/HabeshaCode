package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HabeshaBackground
import com.example.ui.components.HabeshaHeader
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.HbBlack
import com.example.ui.theme.HbGold
import com.example.ui.theme.HbGoldBright
import com.example.ui.theme.HbNavy
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                
                // Observe ViewModel Shared Toast alerts
                LaunchedEffect(Unit) {
                    viewModel.toastMessage.collect { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                val currentScreen by viewModel.currentScreen.collectAsState()

                HabeshaBackground {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "screenTransition"
                    ) { screen ->
                        when (screen) {
                            "LOGIN" -> LoginScreen(viewModel)
                            "ONBOARDING" -> OnboardingScreen(viewModel)
                            "MAIN_DASHBOARD" -> DashboardShell(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardShell(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()

    Scaffold(
        containerColor = Color.Transparent, // transparent to let deep space background float
        topBar = {
            HabeshaHeader(onSignOutClick = { viewModel.handleSignOut() })
        },
        bottomBar = {
            NavigationBar(
                containerColor = HbNavy.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                // Home
                NavigationBarItem(
                    selected = currentTab == "HOME",
                    onClick = { viewModel.navigateTab("HOME") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HbBlack,
                        selectedTextColor = HbGoldBright,
                        indicatorColor = HbGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f)
                    )
                )

                // Courses
                NavigationBarItem(
                    selected = currentTab == "COURSES",
                    onClick = { viewModel.navigateTab("COURSES") },
                    icon = { Icon(Icons.Default.School, contentDescription = "Courses") },
                    label = { Text("Learn", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HbBlack,
                        selectedTextColor = HbGoldBright,
                        indicatorColor = HbGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f)
                    )
                )

                // Play
                NavigationBarItem(
                    selected = currentTab == "PLAYGROUND",
                    onClick = { viewModel.navigateTab("PLAYGROUND") },
                    icon = { Icon(Icons.Default.Terminal, contentDescription = "Playground") },
                    label = { Text("Editor", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HbBlack,
                        selectedTextColor = HbGoldBright,
                        indicatorColor = HbGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f)
                    )
                )

                // Mentors
                NavigationBarItem(
                    selected = currentTab == "AI_MENTOR",
                    onClick = { viewModel.navigateTab("AI_MENTOR") },
                    icon = { Icon(Icons.Default.SmartToy, contentDescription = "AI Mentor") },
                    label = { Text("Mentor", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HbBlack,
                        selectedTextColor = HbGoldBright,
                        indicatorColor = HbGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f)
                    )
                )

                // Leaderboard
                NavigationBarItem(
                    selected = currentTab == "LEADERBOARD",
                    onClick = { viewModel.navigateTab("LEADERBOARD") },
                    icon = { Icon(Icons.Default.Leaderboard, contentDescription = "Hub") },
                    label = { Text("Rank", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HbBlack,
                        selectedTextColor = HbGoldBright,
                        indicatorColor = HbGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f)
                    )
                )

                // Community Feed
                NavigationBarItem(
                    selected = currentTab == "FEED",
                    onClick = { viewModel.navigateTab("FEED") },
                    icon = { Icon(Icons.Default.Forum, contentDescription = "Community Feed") },
                    label = { Text("Feed", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HbBlack,
                        selectedTextColor = HbGoldBright,
                        indicatorColor = HbGold,
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f)
                    )
                )
            }
        }
    ) { padValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padValues)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "tabTransition"
            ) { tab ->
                when (tab) {
                    "HOME" -> DashboardScreen(viewModel)
                    "COURSES" -> CoursesScreen(viewModel)
                    "PLAYGROUND" -> PlaygroundScreen(viewModel)
                    "AI_MENTOR" -> AiMentorScreen(viewModel)
                    "LEADERBOARD" -> LeaderboardScreen(viewModel)
                    "FEED" -> FeedScreen(viewModel)
                }
            }
        }
    }
}
