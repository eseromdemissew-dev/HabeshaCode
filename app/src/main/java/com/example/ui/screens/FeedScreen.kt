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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Post
import com.example.ui.components.EthiopianFlagStrip
import com.example.ui.components.GlassCard
import com.example.ui.components.HabeshaButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(viewModel: MainViewModel) {
    val posts by viewModel.allPosts.collectAsState()
    val profile by viewModel.currentUserProfile.collectAsState()

    var typedPostContent by remember { mutableStateOf("") }
    var typedPostCode by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("JavaScript") }
    var attachCodeBlock by remember { mutableStateOf(false) }

    val languages = listOf("JavaScript", "Python", "HTML", "C++")
    var langDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toolbar header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "COMMUNITY FEED 💬",
                    color = HbText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "DMs, code snippets, and group discussions",
                    color = HbTextMuted,
                    fontSize = 11.sp
                )
            }
        }
        
        EthiopianFlagStrip()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Write Post Area
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SHARE AN INSIGHT OR QUESTION",
                    color = HbGoldBright,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = typedPostContent,
                    onValueChange = { typedPostContent = it },
                    placeholder = { Text("What are you building today? Ask for codings help...", color = Color.White.copy(alpha = 0.3f), fontSize = 13.sp) },
                    textStyle = TextStyle(color = HbText, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HbGold,
                        unfocusedBorderColor = HbBorder,
                        focusedLabelColor = HbGold,
                        unfocusedLabelColor = HbTextMuted
                    ),
                    modifier = Modifier.fillMaxWidth().height(80.dp)
                )

                // Optional code block attachment
                if (attachCodeBlock) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ATTACHING CODE BLOCK",
                            color = HbGoldBright,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Box {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(HbGold)
                                    .clickable { langDropdownExpanded = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = selectedLanguage,
                                    color = HbBlack,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            DropdownMenu(
                                expanded = langDropdownExpanded,
                                onDismissRequest = { langDropdownExpanded = false },
                                containerColor = HbNavy
                            ) {
                                languages.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text(lang, color = HbText, fontSize = 12.sp) },
                                        onClick = {
                                            selectedLanguage = lang
                                            langDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = typedPostCode,
                        onValueChange = { typedPostCode = it },
                        placeholder = { Text("Paste code snippet here...", color = Color.White.copy(alpha = 0.3f), fontSize = 11.sp) },
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = HbText),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HbGold,
                            unfocusedBorderColor = HbBorder
                        ),
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { attachCodeBlock = !attachCodeBlock }) {
                        Icon(
                            imageVector = if (attachCodeBlock) Icons.Default.CodeOff else Icons.Default.Code,
                            contentDescription = "Attach Code",
                            tint = if (attachCodeBlock) HbGoldBright else HbTextMuted
                        )
                    }

                    HabeshaButton(
                        text = "Publish Post →",
                        onClick = {
                            if (typedPostContent.trim().isNotEmpty()) {
                                viewModel.submitPost(
                                    content = typedPostContent,
                                    code = if (attachCodeBlock) typedPostCode else null,
                                    codeLang = if (attachCodeBlock) selectedLanguage else null
                                )
                                typedPostContent = ""
                                typedPostCode = ""
                                attachCodeBlock = false
                            } else {
                                viewModel.showToast("Post content cannot be empty!")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "COMMUNITY CHANNELS TIMELINE",
                color = HbTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Timeline Items
            if (posts.isEmpty()) {
                // Pre-populated local fake seeds on timeline
                val seeds = listOf(
                    Post("1", "selam_dev", "Selamawit Kebede", "SK", "Akam! I just completed the complete curriculum modules for React Modern Frontend and received my certified graduation diploma! Highly recommend doing the interactive Code exercises 🚀", null, null, 14, false),
                    Post("2", "yonas_build", "Yonas Tesfaye", "YT", "Look at this clean closure binding I wrote inside JavaScript loops! Any tips to make it run faster?", "const runScope = () => {\n  let level = 5;\n  return () => level * 100;\n}", "JavaScript", 28, false)
                )

                seeds.forEach { p ->
                    FeedPostItem(p, onLike = {
                        viewModel.showToast("Upvoted post!")
                    })
                }
            } else {
                posts.forEach { p ->
                    FeedPostItem(p, onLike = {
                        viewModel.likePost(p.id, profile?.id ?: "")
                    })
                }
            }
        }
    }
}

@Composable
fun FeedPostItem(post: Post, onLike: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar fallback
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(HbGold),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.avatarUrl.take(2),
                        color = HbBlack,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = post.username,
                        color = HbText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@${post.userId} . Just now",
                        color = HbTextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.content,
                color = HbText,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // If has code snippet
            if (!post.codeContent.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(HbBlack)
                        .border(1.dp, HbBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = post.codeLanguage?.uppercase() ?: "SNIPPET",
                            color = HbGoldBright,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = post.codeContent,
                            color = Color(0xFFA6E22E), // neon green syntax mock
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Likes
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (post.isLiked) HbRed else HbTextMuted,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onLike() }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${post.likesCount} upvotes",
                    color = HbTextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
