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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val profile by viewModel.currentUserProfile.collectAsState()
    val courses by viewModel.allCourses.collectAsState()
    val enrollments by viewModel.allEnrollments.collectAsState()

    // Daily missions mock state inside main dashboard
    var task1Claimed by remember { mutableStateOf(false) }
    var task2Claimed by remember { mutableStateOf(false) }
    var task3Claimed by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Determine EAT Time Greeting
    val hour = Calendar.getInstance(TimeZone.getTimeZone("GMT+3")).get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Yene Konjo Sugih (Good Morning)"
        in 12..16 -> "Melkam Ken (Good Afternoon)"
        else -> "Melkam Mishit (Good Evening)"
    }

    val todayDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // --- Warm Welcome Header Block ---
        GlassCard(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            borderColor = HbBorderBright
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "$greeting 👋",
                        color = HbGoldBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = profile?.fullName ?: "Innovator",
                        color = HbText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = todayDate,
                        color = HbTextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Streak pill display
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(HbRed.copy(alpha = 0.15f))
                        .border(1.dp, HbRed, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = HbRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${profile?.streak ?: 1} DAY STREAK",
                            color = HbRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // --- Stats & level progress dual grids ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Level Circular dial
            GlassCard(
                modifier = Modifier.weight(1.2f),
                borderColor = HbBorder
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GROWTH WHEEL",
                        color = HbTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LevelCircularProgress(
                        xp = profile?.xp ?: 0,
                        level = profile?.level ?: 1,
                        size = 110.dp
                    )
                }
            }

            // Quick XP milestones card
            GlassCard(
                modifier = Modifier.weight(1f),
                borderColor = HbBorder
            ) {
                Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "ACQUISITION LOG",
                        color = HbTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Column {
                        Text(
                            text = "TOTAL XP",
                            color = HbText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${profile?.xp ?: 0} XP",
                            color = HbGoldBright,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Column {
                        Text(
                            text = "ENROLLED IN",
                            color = HbText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${enrollments.size} Courses",
                            color = HbGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // --- Active Continuous Learning Widget ---
        val lastEnrollment = enrollments.lastOrNull()
        val enrolledCourse = courses.find { it.id == lastEnrollment?.courseId }

        if (lastEnrollment != null && enrolledCourse != null) {
            Text(
                text = "CONTINUE BUILDING",
                color = HbGoldBright,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                onClick = {
                    viewModel.selectCourse(enrolledCourse.id)
                    viewModel.navigateTab("COURSES")
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(HbGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = HbGoldBright,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = enrolledCourse.title,
                            color = HbText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Course Completion: ${(lastEnrollment.progress * 100).toInt()}%",
                                color = HbTextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        GoldProgressBar(progress = lastEnrollment.progress.toFloat())
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = HbTextMuted
                    )
                }
            }
        } else {
            // First Launch / No Courses CTA
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                onClick = { viewModel.navigateTab("COURSES") },
                borderColor = HbGold
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = HbGold,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ENROLL IN YOUR FIRST COURSE",
                        color = HbText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Start with HTML & CSS Mastery or programming fundamentals free!",
                        color = HbTextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // --- Smart AI Suggestions ---
        Text(
            text = "MENTOR ADVISORY",
            color = HbGoldBright,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        GlassCard(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            onClick = { viewModel.navigateTab("AI_MENTOR") }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(HbGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = HbGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Suggestion for you:",
                        color = HbText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (lastEnrollment == null) {
                            "You haven't enrolled in python loops yet! Let's build your hardware logic under Arduino Robotics. Chat with Teacher now!"
                        } else {
                            "Write 50+ lines of code inside our Sandbox Playground. Click Explain Code to check your closure configurations!"
                        },
                        color = HbTextMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // --- Daily Objectives Widgets ---
        Text(
            text = "DAILY MISSIONS",
            color = HbGoldBright,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
            // Task A
            DailyTaskRow(
                title = "Consult with AI Mentor today",
                xpReward = 40,
                progress = 1f,
                claimed = task1Claimed,
                onClaim = {
                    viewModel.showToast("+40 XP Claimed!")
                    task1Claimed = true
                }
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Task B
            val finishedLesson = lastEnrollment != null && lastEnrollment.progress > 0.0
            DailyTaskRow(
                title = "Complete 1 premium lesson",
                xpReward = 75,
                progress = if (finishedLesson) 1f else 0f,
                claimed = task2Claimed,
                onClaim = {
                    viewModel.showToast("+75 XP Claimed!")
                    task2Claimed = true
                }
            )

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Task C
            val codesWritten = viewModel.editorCode.value.length > 50
            DailyTaskRow(
                title = "Simulate code in Playground",
                xpReward = 50,
                progress = if (codesWritten) 1f else 0f,
                claimed = task3Claimed,
                onClaim = {
                    viewModel.showToast("+50 XP Claimed!")
                    task3Claimed = true
                }
            )
        }
    }
}

@Composable
fun DailyTaskRow(
    title: String,
    xpReward: Int,
    progress: Float,
    claimed: Boolean,
    onClaim: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (progress >= 1f) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (progress >= 1f) HbGreen else HbTextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = HbText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(HbGoldDim.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+$xpReward XP",
                        color = HbGoldBright,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (progress >= 1f) "Completed" else "In Progress",
                    color = if (progress >= 1f) HbGreen else HbTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        if (claimed) {
            Text(
                text = "CLAIMED ✓",
                color = HbGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (progress >= 1f) HbGold else Color.White.copy(alpha = 0.05f))
                    .clickable(enabled = progress >= 1f, onClick = onClaim)
                    .padding(vertical = 6.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "CLAIM",
                    color = if (progress >= 1f) HbBlack else HbTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
