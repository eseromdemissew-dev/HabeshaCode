package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RocketLaunch
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EthiopianFlagStrip
import com.example.ui.components.GlassCard
import com.example.ui.components.HabeshaButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun OnboardingScreen(viewModel: MainViewModel) {
    val step by viewModel.onboardingStep.collectAsState()
    val selectedInterests by viewModel.selectedInterests.collectAsState()
    val userProfile by viewModel.currentUserProfile.collectAsState()

    // Mock sequence messages for AI analysis state loading
    val loadingMessages = listOf(
        "Deconstructing your quiz results...",
        "Evaluating engineering profile...",
        "Curating optimal custom roadmaps...",
        "Matching perfect tech starter courses...",
        "Generating initial credential seeds...",
        "Ecosystem ready! ✨"
    )
    var currentLoadingTextIndex by remember { mutableStateOf(0) }

    LaunchedEffect(step) {
        if (step == 4) {
            currentLoadingTextIndex = 0
            for (i in loadingMessages.indices) {
                currentLoadingTextIndex = i
                kotlinx.coroutines.delay(1200)
            }
            viewModel.advanceOnboarding()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HbBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // STEP DOTS BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..4) {
                    val isActive = i <= step
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isActive) HbGold else Color.White.copy(alpha = 0.15f))
                    )
                    if (i < 4) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(1.dp)
                                .background(if (i < step) HbGold else Color.White.copy(alpha = 0.1f))
                        )
                    }
                }
            }

            // MIDDLE WIZARD
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                when (step) {
                    1 -> OnboardingStepWelcome(userProfile?.fullName ?: "Innovator") {
                        viewModel.advanceOnboarding()
                    }
                    2 -> OnboardingStepInterests(selectedInterests, onToggle = {
                        viewModel.selectInterest(it)
                    }) {
                        if (selectedInterests.isNotEmpty()) {
                            viewModel.advanceOnboarding()
                        } else {
                            viewModel.showToast("Select at least one coding interest area!")
                        }
                    }
                    3 -> OnboardingStepQuiz {
                        viewModel.advanceOnboarding()
                    }
                    4 -> OnboardingStepAnalyzing(loadingMessages[currentLoadingTextIndex])
                }
            }

            // Bottom brand strip
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                EthiopianFlagStrip()
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Step $step of 4",
                        color = HbTextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Skip personalize",
                        color = HbGoldDim,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.skipOnboarding() }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingStepWelcome(name: String, onNext: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().widthIn(max = 400.dp)
    ) {
        Icon(
            imageVector = Icons.Default.RocketLaunch,
            contentDescription = null,
            tint = HbGoldBright,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Welcome to HabeshaCode,\n$name! 🎉",
            color = HbText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Let's align your software engineering journey in 2 minutes. We will seed customized roadmaps, quizzes, and local AI prompts tailored to your level.",
            color = HbTextMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(30.dp))
        HabeshaButton(
            text = "Begin Personalization →",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingStepInterests(
    selected: Set<String>,
    onToggle: (String) -> Unit,
    onNext: () -> Unit
) {
    val interestsList = listOf(
        "🌐 Web Dev" to "web",
        "🤖 Machine Learning" to "ai",
        "🦾 Robotics & IoT" to "robotics",
        "📱 Mobile Config" to "mobile",
        "🔒 Cybersecurity" to "cyber",
        "⚙️ DevOps Streams" to "devops",
        "🎨 UI/UX Styling" to "uiux",
        "📊 Data Analysis" to "data",
        "🎮 Game Systems" to "game"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "What do you want to build? 💻",
            color = HbText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Choose your core computer science focus areas (select at least 1)",
            color = HbTextMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            items(interestsList.size) { index ->
                val pair = interestsList[index]
                val isSelected = selected.contains(pair.second)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) HbGold.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f))
                        .border(1.5.dp, if (isSelected) HbGold else HbBorder, RoundedCornerShape(12.dp))
                        .clickable { onToggle(pair.second) }
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            text = pair.first,
                            color = if (isSelected) HbGoldBright else HbText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Course guides ready",
                            color = HbTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HabeshaButton(
            text = "Sync Interests →",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().widthIn(max = 400.dp)
        )
    }
}

@Composable
fun OnboardingStepQuiz(onComplete: () -> Unit) {
    val questions = listOf(
        "Have you written any software code before?" to listOf("No, completely new", "A little (HTML or simple variables)", "Built small scripts", "Professional builder"),
        "What is a standard variable variable?" to listOf("A mathematical constant", "A memory box storing datasets", "A type of sensor input", "An offline cache folder"),
        "What represents an API?" to listOf("An internet router link", "A contract connecting separate software", "An Android layout class", "A SQL server trigger")
    )
    var currentQIndex by remember { mutableStateOf(0) }
    var selectedAnsIndex by remember { mutableStateOf(-1) }

    val currentQ = questions[currentQIndex]

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().widthIn(max = 420.dp)
    ) {
        Text(
            text = "Ecosystem Skill Quiz 🎯",
            color = HbGoldBright,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Question ${currentQIndex + 1} of 3",
            color = HbText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Question Box card
        GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
            Text(
                text = currentQ.first,
                color = HbText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }

        // Alternative answers mapping
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            currentQ.second.forEachIndexed { idx, option ->
                val isSelected = selectedAnsIndex == idx
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) HbGold.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.03f))
                        .border(1.dp, if (isSelected) HbGold else HbBorder, RoundedCornerShape(10.dp))
                        .clickable { selectedAnsIndex = idx }
                        .padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, if (isSelected) HbGold else HbTextMuted, RoundedCornerShape(8.dp))
                                .background(if (isSelected) HbGold else Color.Transparent)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option,
                            color = if (isSelected) HbGoldBright else HbText,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        HabeshaButton(
            text = if (currentQIndex < 2) "Next Question" else "Complete Audit ✓",
            onClick = {
                if (selectedAnsIndex == -1) {
                    onComplete() // fallback
                } else {
                    if (currentQIndex < 2) {
                        currentQIndex++
                        selectedAnsIndex = -1
                    } else {
                        onComplete()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedAnsIndex != -1
        )
    }
}

@Composable
fun OnboardingStepAnalyzing(loadingText: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "analyse")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().widthIn(max = 300.dp)
    ) {
        // Glowing animated brain loader
        Box(
            modifier = Modifier
                .size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = HbGold.copy(alpha = 0.02f)
                )
                drawArc(
                    brush = Brush.sweepGradient(listOf(HbGoldDim, HbGold, HbGoldBright, HbGoldDim)),
                    startAngle = angle,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = HbGoldBright,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "AI CURATOR ACTIVE",
            color = HbGoldBright,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = loadingText,
            color = HbText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
