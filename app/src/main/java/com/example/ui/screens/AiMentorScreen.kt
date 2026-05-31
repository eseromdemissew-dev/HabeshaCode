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
import com.example.ui.components.EthiopianFlagStrip
import com.example.ui.components.GlassCard
import com.example.ui.components.HabeshaButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiMentorScreen(viewModel: MainViewModel) {
    val messages by viewModel.aiMessages.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val activeMode by viewModel.aiMode.collectAsState()

    var typedMessageText by remember { mutableStateOf("") }
    val threadScrollState = rememberScrollState()

    // Scroll chat bottom on new message incoming
    LaunchedEffect(messages.size, isAiLoading) {
        if (messages.isNotEmpty()) {
            threadScrollState.animateScrollTo(threadScrollState.maxValue)
        }
    }

    val modes = listOf(
        "mentor" to "Mentor",
        "teacher" to "Teacher",
        "debugger" to "Debugger",
        "builder" to "Builder",
        "coach" to "Recruiter"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // --- Header Mode Selection Tab Row ---
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "AI TRAINING ECOSYSTEM 🤖",
                    color = HbText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                
                Text(
                    text = "Clear Sessions",
                    color = HbRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .clickable { viewModel.clearAiChat() }
                        .padding(4.dp)
                )
            }

            // Tabs row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                modes.forEach { item ->
                    val isSelected = activeMode == item.first
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) HbGold else Color.White.copy(alpha = 0.03f))
                            .border(1.dp, if (isSelected) HbGold else HbBorder, RoundedCornerShape(6.dp))
                            .clickable { viewModel.changeAiMode(item.first) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = item.second.uppercase(),
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

        // --- Message Threads Scroll Area ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(threadScrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (messages.isEmpty()) {
                // Greeting State
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Brush.linearGradient(listOf(HbGreen, HbYellow, HbRed)))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(26.dp))
                                .background(HbBlack),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = HbGoldBright,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "I AM YOUR ENHANCED HABESHA AI",
                        color = HbGoldBright,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Select active modes above to consult coding mentors, request live code debugging compilation, mock interview loops, or design builders.",
                        color = HbTextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Mode Specific Quick Chips
                    val starterPrompts = when (activeMode) {
                        "teacher" -> listOf("Teach me JavaScript Closures simply", "Explain semantic HTML structures", "Prepared coding exercises for loops")
                        "debugger" -> listOf("Find bugs in my python loops code", "Why does async network block main thread?", "Differentiate ES6 let and var errors")
                        "builder" -> listOf("Plan architecture layout for Android app", "Generate boilerplate code for HTML layouts", "Structure databases with Room schemas")
                        "coach" -> listOf("Ask me a technical interview question", "Critique my computer science profile", "Give me a mock hardware engineering quiz")
                        else -> listOf("Mentorship opportunities in Addis Ababa", "What tech stacking builds senior career paths?", "Strategies for publishing mobile projects")
                    }

                    Text(
                        text = "QUICK STRATEGIC SUGGESTIONS",
                        color = HbTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    starterPrompts.forEach { chipText ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.03f))
                                .border(1.dp, HbBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.sendAiMentorMessage(chipText)
                                }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chipText,
                                color = HbGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Render conversation thread
                messages.forEach { msg ->
                    if (msg.isUser) {
                        // User message (Aligned Right)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp))
                                    .background(HbGoldDim.copy(alpha = 0.15f))
                                    .border(1.dp, HbGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    color = HbText,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    } else {
                        // AI Response (Aligned Left)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            // Monogram
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(HbGold)
                                    .padding(1.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(HbBlack),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "</>",
                                        color = HbGoldBright,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp))
                                    .background(HbGlass)
                                    .border(1.dp, HbBorder, RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                        text = msg.text,
                                        color = HbText,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp
                                    )
                            }
                        }
                    }
                }
            }

            // AI is thinking typing indicators
            if (isAiLoading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "...",
                            color = HbGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp))
                            .background(HbGlass)
                            .border(1.dp, HbBorder, RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Consulting model clusters... (Generating tokens)",
                            color = HbTextMuted,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // --- Bottom Message Text Dispatch Area ---
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            borderColor = HbBorderBright
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = typedMessageText,
                    onValueChange = { typedMessageText = it },
                    placeholder = { Text("Ask your programming mentor anything...", color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = HbText,
                        unfocusedTextColor = HbText
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        if (typedMessageText.trim().isNotEmpty()) {
                            viewModel.sendAiMentorMessage(typedMessageText)
                            typedMessageText = ""
                        }
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(HbGold)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = HbBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
