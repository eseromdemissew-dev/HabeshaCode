package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
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
fun LoginScreen(viewModel: MainViewModel) {
    var isRegisterMode by remember { mutableStateOf(false) }
    
    // Core inputs
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("student") } // student, instructor, mentor
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HbBlack),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background geometric Canvas drawings
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            // Draw subtle glowing circles representing cyber Ethiopian themes
            drawCircle(
                brush = Brush.radialGradient(listOf(HbGold.copy(alpha = 0.12f), Color.Transparent)),
                radius = width * 0.5f,
                center = androidx.compose.ui.geometry.Offset(width * 0.1f, height * 0.1f)
            )
            drawCircle(
                brush = Brush.radialGradient(listOf(HbGreen.copy(alpha = 0.08f), Color.Transparent)),
                radius = width * 0.4f,
                center = androidx.compose.ui.geometry.Offset(width * 0.9f, height * 0.8f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Big branding logo block
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(HbGreen, HbYellow, HbRed)))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(HbBlack),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "</>",
                        color = HbGoldBright,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "HABESHACODE",
                color = HbGoldBright,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Learn . Build . Innovate 🇪🇹",
                color = HbTextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))

            // Login vs Register card structure
            AnimatedContent(
                targetState = isRegisterMode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "authForm"
            ) { registering ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp)
                ) {
                    Text(
                        text = if (registering) "CREATE ACCOUNT" else "WELCOME BACK",
                        color = HbText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    if (registering) {
                        // Full Name input
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = HbGold) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HbGold,
                                unfocusedBorderColor = HbBorder,
                                focusedTextColor = HbText,
                                unfocusedTextColor = HbText,
                                focusedLabelColor = HbGold,
                                unfocusedLabelColor = HbTextMuted
                            ),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        )
                    }

                    // Username Input
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.Terminal, contentDescription = null, tint = HbGold) },
                        placeholder = { Text("e.g. selam_dev", color = Color.White.copy(alpha = 0.3f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HbGold,
                            unfocusedBorderColor = HbBorder,
                            focusedTextColor = HbText,
                            unfocusedTextColor = HbText,
                            focusedLabelColor = HbGold,
                            unfocusedLabelColor = HbTextMuted
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    if (registering) {
                        // Role Selector Header
                        Text(
                            text = "Choose Your Role",
                            color = HbTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        // Role Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("student" to "Student", "instructor" to "Instructor", "mentor" to "Mentor").forEach { rolePair ->
                                val isSelected = selectedRole == rolePair.first
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) HbGold.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f))
                                        .border(1.dp, if (isSelected) HbGold else HbBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedRole = rolePair.first }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = rolePair.second,
                                        color = if (isSelected) HbGoldBright else HbTextMuted,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Submit Button
                    HabeshaButton(
                        text = if (registering) "Register Free →" else "Sign In ✓",
                        onClick = {
                            if (registering) {
                                if (fullName.trim().isEmpty() || username.trim().isEmpty()) {
                                    viewModel.showToast("All fields are required!")
                                } else {
                                    viewModel.handleRegister(fullName, username, selectedRole)
                                }
                            } else {
                                if (username.trim().isEmpty()) {
                                    viewModel.showToast("Please enter username to sign in")
                                } else {
                                    viewModel.handleLogin(username)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mode Toggle Navigation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (registering) "Already have an account?" else "New to HabeshaCode?",
                            color = HbTextMuted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (registering) "Sign In" else "Register Free",
                            color = HbGoldBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { isRegisterMode = !isRegisterMode }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            // Footer Branding
            Text(
                text = "🇪🇹 Built in Ethiopia with ❤️ for Africa and the World",
                color = HbTextMuted.copy(alpha = 0.6f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "100% Free Ecosystem . No Paywalls",
                color = HbGoldDim,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
