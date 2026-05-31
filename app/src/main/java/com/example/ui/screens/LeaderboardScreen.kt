package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LeaderboardScreen(viewModel: MainViewModel) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    val completions by viewModel.allEnrollments.collectAsState()
    val profile by viewModel.currentUserProfile.collectAsState()

    var activeSubTab by remember { mutableStateOf("RANKINGS") }
    val tabs = listOf("RANKINGS", "DIPLOMAS", "ACHIEVEMENTS")

    val completedCourses = completions.filter { it.completed }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- Selector Horizontal Bar ---
        Column {
            Row(
                modifier = Modifier
                    .fillHorizontal()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GAMIFICATION HUD 🏆",
                    color = HbText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(HbGoldDim.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${profile?.xp ?: 0} TOTAL XP",
                        color = HbGoldBright,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Sub tab bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEach { tab ->
                    val isSelected = activeSubTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) HbGold else Color.White.copy(alpha = 0.03f))
                            .border(1.dp, if (isSelected) HbGold else HbBorder, RoundedCornerShape(6.dp))
                            .clickable { activeSubTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) HbBlack else HbTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            EthiopianFlagStrip()
        }

        // --- Active Sub View State ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            when (activeSubTab) {
                "RANKINGS" -> RankingsView(leaderboard, profile?.id ?: "")
                "DIPLOMAS" -> DiplomasView(viewModel, completedCourses)
                "ACHIEVEMENTS" -> AchievementsView(profile?.xp ?: 0)
            }
        }
    }
}

@Composable
fun RankingsView(leaderboard: List<com.example.data.Profile>, currentUserId: String) {
    val scrollState = rememberScrollState()
    
    // Sort and map the list
    val sortedLeaderboard = leaderboard.sortedByDescending { it.xp }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
        // --- Podium for Top 3 (Vibrant Visuals) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 2nd Place Block
            val secondPlace = sortedLeaderboard.getOrNull(1)
            PodiumBlock(
                name = secondPlace?.fullName?.substringBefore(" ") ?: "Selam",
                xp = secondPlace?.xp ?: 12500,
                rankText = "2ND",
                heightMultiplier = 0.7f,
                accentColor = Color(0xFFC0C0C0)
            )

            // 1st Place Block
            val firstPlace = sortedLeaderboard.getOrNull(0)
            PodiumBlock(
                name = firstPlace?.fullName?.substringBefore(" ") ?: "Helen",
                xp = firstPlace?.xp ?: 14000,
                rankText = "1ST",
                heightMultiplier = 1f,
                accentColor = HbGoldBright
            )

            // 3rd Place Block
            val thirdPlace = sortedLeaderboard.getOrNull(2)
            PodiumBlock(
                name = thirdPlace?.fullName?.substringBefore(" ") ?: "Yonas",
                xp = thirdPlace?.xp ?: 9800,
                rankText = "3RD",
                heightMultiplier = 0.55f,
                accentColor = Color(0xFFCD7F32)
            )
        }

        Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(bottom = 12.dp))

        // Subsequent list matching ranks 4-50
        sortedLeaderboard.forEachIndexed { index, item ->
            val rankPos = index + 1
            val isCurrentUser = item.id == currentUserId

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isCurrentUser) HbGold.copy(alpha = 0.12f) else Color.Transparent)
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#$rankPos",
                        color = if (rankPos <= 3) HbGoldBright else HbTextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.width(36.dp)
                    )
                    
                    // Monogram icon
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(if (isCurrentUser) HbGold else Color.White.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.fullName.take(1).uppercase(),
                            color = if (isCurrentUser) HbBlack else HbGoldBright,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.fullName,
                            color = HbText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Level ${item.level} . ${item.role.uppercase()}",
                            color = HbTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Text(
                    text = "${item.xp} XP",
                    color = HbGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
            Divider(color = Color.White.copy(alpha = 0.03f))
        }
    }
}

@Composable
fun PodiumBlock(
    name: String,
    xp: Int,
    rankText: String,
    heightMultiplier: Float,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.height(130.dp)
    ) {
        Icon(
            imageVector = if (rankText == "1ST") Icons.Default.MilitaryTech else Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = name,
            color = HbText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 2.dp)
        )
        Text(
            text = "${xp}xp",
            color = HbTextMuted,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(55.dp)
                .fillMaxHeight(heightMultiplier * 0.7f)
                .clip(RoundedCornerShape(6.dp, 6.dp, 0.dp, 0.dp))
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.dp, accentColor, RoundedCornerShape(6.dp, 6.dp, 0.dp, 0.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rankText,
                color = accentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun DiplomasView(viewModel: MainViewModel, completed: List<com.example.data.Enrollment>) {
    if (completed.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CardMembership,
                contentDescription = null,
                tint = HbGoldDim,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "NO DIPLOMAS YET 🏆",
                color = HbText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Complete all modules of any course (100% progress) to unlock certified blockchain credentials.",
                color = HbTextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            HabeshaButton(
                text = "Browse Tech Courses",
                onClick = { viewModel.navigateTab("COURSES") }
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            completed.forEach { item ->
                val courseTitle = when (item.courseId) {
                    "html_css_mastery" -> "HTML & CSS Mastery Course"
                    "javascript_complete" -> "JavaScript Complete Developer Course"
                    "python_ai_engineering" -> "Python for AI Engineering Track"
                    else -> "Arduino & Robotics IoT Builder Track"
                }

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = HbGold
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(HbGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = HbGoldBright,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DIPLOMA DEGREE",
                                color = HbGoldBright,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = courseTitle,
                                color = HbText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "CERTIFICATE ID: HC-2026-${item.courseId.take(3).uppercase()}-X88P",
                                color = HbTextMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementsView(totalXp: Int) {
    val achievements = listOf(
        Triple("First Step", "Welcome to HabeshaCode onboarding", 100),
        Triple("Code Warrior", "Cross the 1000 total XP boundary", 1000),
        Triple("Ecosystem Guru", "Cross the 5000 total XP boundary", 5000),
        Triple("AI Whisperer", "Consult with Mentor tutor bots", 300),
        Triple("Physical Actuator", "Conduct robotics digital setups", 400)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        achievements.forEach { badge ->
            val unlocked = totalXp >= badge.third

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (unlocked) HbGold else HbBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (unlocked) HbGold.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f))
                                .border(1.0.dp, if (unlocked) HbGold else HbBorder, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (unlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (unlocked) HbGoldBright else HbTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = badge.first,
                                color = if (unlocked) HbText else HbTextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = badge.second,
                                color = HbTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (unlocked) HbGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (unlocked) "UNLOCKED" else "${badge.third} XP",
                            color = if (unlocked) HbGreen else HbTextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// Helper expansion
private fun Modifier.fillHorizontal(): Modifier {
    return this.fillMaxWidth()
}
