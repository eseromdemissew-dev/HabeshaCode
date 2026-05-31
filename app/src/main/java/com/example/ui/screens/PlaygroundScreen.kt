package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
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
fun PlaygroundScreen(viewModel: MainViewModel) {
    val editorCode by viewModel.editorCode.collectAsState()
    val editorLanguage by viewModel.editorLanguage.collectAsState()
    val consoleOutput by viewModel.consoleOutput.collectAsState()

    var typedCode by remember { mutableStateOf("") }
    var languageDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(editorCode) {
        typedCode = editorCode
    }

    val languages = listOf("HTML", "JavaScript", "Python")

    Column(modifier = Modifier.fillMaxSize()) {
        // --- Selector Top Toolbar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "SANDBOX COMPILER ⚡",
                    color = HbText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Write algorithms and test in sandbox",
                    color = HbTextMuted,
                    fontSize = 11.sp
                )
            }

            // Dropdown selector
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HbGold)
                        .clickable { languageDropdownExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = editorLanguage,
                        color = HbBlack,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                DropdownMenu(
                    expanded = languageDropdownExpanded,
                    onDismissRequest = { languageDropdownExpanded = false },
                    containerColor = HbNavy
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang, color = HbText, fontWeight = FontWeight.Bold) },
                            onClick = {
                                viewModel.changeLanguage(lang)
                                languageDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        EthiopianFlagStrip()

        // --- Core Editor Area (Natively visual) ---
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // High contrast text input area
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HbBlack)
                    .border(1.dp, HbBorder, RoundedCornerShape(12.dp))
            ) {
                Column {
                    // Header mimicking visual tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.02f))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(HbRed))
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(HbYellow))
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(HbGreen))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "main.${editorLanguage.lowercase().take(3)}",
                                color = HbTextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = HbGoldDim,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Native Scrollable Text Editor field
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        OutlinedTextField(
                            value = typedCode,
                            onValueChange = {
                                typedCode = it
                                viewModel.updateCode(it)
                            },
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = HbText
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = HbText,
                                unfocusedTextColor = HbText,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }

            // --- Action Buttons Triggers ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Execute code
                HabeshaButton(
                    text = "Execute Code ▶",
                    onClick = { viewModel.runEditorCode() },
                    modifier = Modifier.weight(1f),
                    isPrimary = true,
                    icon = Icons.Default.PlayArrow
                )

                // AI Assist Diagnosis
                HabeshaButton(
                    text = "AI diagnosis",
                    onClick = { viewModel.runCodeAiHelper() },
                    modifier = Modifier.weight(1f),
                    isPrimary = false,
                    icon = Icons.Default.AutoAwesome
                )
            }

            // --- Output Logging Panel ---
            Box(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HbNavyLight)
                    .border(1.dp, HbBorder, RoundedCornerShape(12.dp))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.02f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = HbTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CONSOLE LOG MONITOR OUTPUT",
                            color = HbTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (consoleOutput.isEmpty()) "Console empty. Execute some codes above representing inputs..." else consoleOutput,
                            color = if (consoleOutput.contains("AI ASSIStant", ignoreCase = true)) HbGoldBright else HbTextMuted,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
