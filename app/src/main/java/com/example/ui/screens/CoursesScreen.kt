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
import com.example.data.Course
import com.example.data.Lesson
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CoursesScreen(viewModel: MainViewModel) {
    val selectedCourseId by viewModel.selectedCourseId.collectAsState()
    val courses by viewModel.allCourses.collectAsState()
    val enrollments by viewModel.allEnrollments.collectAsState()

    AnimatedContent(
        targetState = selectedCourseId,
        transitionSpec = {
            slideInHorizontally { width -> width } + fadeIn() togetherWith
                slideOutHorizontally { width -> -width } + fadeOut()
        },
        label = "courseScreenState"
    ) { activeId ->
        if (activeId == null) {
            CoursesCatalog(
                courses = courses,
                enrollments = enrollments,
                onSelectCourse = { viewModel.selectCourse(it) },
                onEnroll = { viewModel.enrollInCourse(it) }
            )
        } else {
            val currentCourse = courses.find { it.id == activeId }
            if (currentCourse != null) {
                LessonPlayer(
                    viewModel = viewModel,
                    course = currentCourse,
                    onBack = { viewModel.selectCourse(null) }
                )
            } else {
                viewModel.selectCourse(null)
            }
        }
    }
}

@Composable
fun CoursesCatalog(
    courses: List<Course>,
    enrollments: List<com.example.data.Enrollment>,
    onSelectCourse: (String) -> Unit,
    onEnroll: (String) -> Unit
) {
    var selectedFilterCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "html", "javascript", "python", "robotics")

    val filteredList = if (selectedFilterCategory == "All") {
        courses
    } else {
        courses.filter { it.category == selectedFilterCategory }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "COURSE CATALOG",
            color = HbGoldBright,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Master production coding and physical computing completely free",
            color = HbTextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Horizontal Category Quick Scroll Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedFilterCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) HbGold else Color.White.copy(alpha = 0.03f))
                        .border(1.dp, if (isSelected) HbGold else HbBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedFilterCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = cat.uppercase(),
                        color = if (isSelected) HbBlack else HbTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Lazy catalog view
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            filteredList.forEach { cur ->
                val matchingEnrollment = enrollments.find { it.courseId == cur.id }
                val isEnrolled = matchingEnrollment != null

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (isEnrolled) HbGold else HbBorder
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = cur.level.uppercase(),
                                    color = when (cur.level) {
                                        "beginner" -> HbGreen
                                        "intermediate" -> HbYellow
                                        else -> HbRed
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = HbYellow,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "4.9 (42 ratings)",
                                    color = HbTextMuted,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = cur.title,
                            color = HbText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = cur.shortDescription,
                            color = HbTextMuted,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = HbGoldBright,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${cur.totalLessons} Lessons",
                                    color = HbTextMuted,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(HbGoldDim.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+${cur.xpReward} XP REWARD",
                                    color = HbGoldBright,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        if (isEnrolled) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Progress: ${(matchingEnrollment!!.progress * 100).toInt()}%",
                                    color = HbTextMuted,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            GoldProgressBar(progress = matchingEnrollment.progress.toFloat())
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (!isEnrolled) {
                                HabeshaButton(
                                    text = "Enroll For Free",
                                    onClick = { onEnroll(cur.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                HabeshaButton(
                                    text = "Continue Building →",
                                    onClick = { onSelectCourse(cur.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonPlayer(
    viewModel: MainViewModel,
    course: Course,
    onBack: () -> Unit
) {
    val lessons by viewModel.activeLessons.collectAsState()
    val completions by viewModel.activeCompletions.collectAsState()
    val selectedId by viewModel.selectedLessonId.collectAsState()
    
    val profile by viewModel.currentUserProfile.collectAsState()

    var showDiplomaCertificateDialog by remember { mutableStateOf(false) }

    val activeLesson = lessons.find { it.id == selectedId } 
        ?: lessons.getOrNull(0)

    LaunchedEffect(lessons) {
        if (lessons.isNotEmpty() && selectedId == null) {
            viewModel.selectLesson(lessons[0].id)
        }
    }

    if (showDiplomaCertificateDialog) {
        AlertDialog(
            onDismissRequest = { showDiplomaCertificateDialog = false },
            containerColor = HbNavy,
            title = {
                Text(
                    text = "🏆 COURSE COMPLETED!",
                    color = HbGoldBright,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(HbGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = HbGoldBright,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "This premium credential certifies that",
                        color = HbTextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = profile?.fullName?.uppercase() ?: "DEVELOPER",
                        color = HbText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "has successfully completed the complete track requirements for \n\n\"${course.title.uppercase()}\"\n\nissued on the blockchain of tech education.",
                        color = HbTextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "CERTIFICATE ID: HC-2026-${course.category.take(3).uppercase()}-882X",
                        color = HbGold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                HabeshaButton(
                    text = "View Diplomas Hub",
                    onClick = {
                        showDiplomaCertificateDialog = false
                        viewModel.navigateTab("LEADERBOARD")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Player Action Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = HbText)
            }
            Text(
                text = course.title,
                color = HbText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { viewModel.navigateTab("PLAYGROUND") }) {
                Icon(Icons.Default.Terminal, contentDescription = "Open Sandbox", tint = HbGoldBright)
            }
        }
        
        EthiopianFlagStrip()

        if (activeLesson != null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Lesson video card simulation
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .padding(bottom = 16.dp),
                    borderColor = HbBorder
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(
                                brush = Brush.linearGradient(listOf(HbNavy, HbNavyLight)),
                                size = size
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = HbGoldBright,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "L${activeLesson.lessonOrder}: ${activeLesson.title}",
                                color = HbText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Curriculum Video: ${activeLesson.durationMinutes} minutes",
                                color = HbTextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Lesson Content Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = HbBorder
                ) {
                    Text(
                        text = activeLesson.title.uppercase(),
                        color = HbGoldBright,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = activeLesson.content,
                        color = HbText,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Lesson completed checkbox
                val isDone = completions.any { it.lessonId == activeLesson.id }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDone) HbGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                        .border(1.5.dp, if (isDone) HbGreen else HbBorder, RoundedCornerShape(10.dp))
                        .clickable {
                            viewModel.completeActiveLesson(course.id, activeLesson.id)
                            val totalLessonsCount = lessons.size
                            val isFinalLesson = !isDone && (completions.size + 1 >= totalLessonsCount)
                            if (isFinalLesson) {
                                showDiplomaCertificateDialog = true
                            }
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isDone) HbGreen else HbTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isDone) "MARKED COMPLETE ✓" else "MARK AS COMPLETE",
                            color = if (isDone) HbGreen else HbText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Other lessons list selector
                Text(
                    text = "NEXT LESSONS IN MODULE",
                    color = HbTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                lessons.forEach { les ->
                    val completed = completions.any { it.lessonId == les.id }
                    val isSelected = les.id == selectedId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color.White.copy(alpha = 0.04f) else Color.Transparent)
                            .clickable { viewModel.selectLesson(les.id) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (completed) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (completed) HbGreen else if (isSelected) HbGoldBright else HbTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "L${les.lessonOrder}: ${les.title}",
                                color = if (isSelected) HbGoldBright else HbText,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                        Text(
                            text = "${les.durationMinutes}m",
                            color = HbTextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Divider(color = Color.White.copy(alpha = 0.03f))
                }
            }
        }
    }
}
