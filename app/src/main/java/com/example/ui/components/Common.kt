package com.example.ui.components

import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// --- Ambient background orbs ---
@Composable
fun HabeshaBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HbBlack)
    ) {
        // Floating orbs simulated with gorgeous radial gradients on canvas
        val infiniteTransition = rememberInfiniteTransition(label = "orbs")
        
        val translationX by infiniteTransition.animateFloat(
            initialValue = -50f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(
                animation = tween(12000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orbX"
        )
        val translationY by infiniteTransition.animateFloat(
            initialValue = -30f,
            targetValue = 60f,
            animationSpec = infiniteRepeatable(
                animation = tween(15000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "orbY"
        )

        Canvas(modifier = Modifier.fillMaxSize().blur(100.dp)) {
            val width = size.width
            val height = size.height

            // Top-Left Gold/Indigo Orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(HbGold.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(width * 0.2f + translationX, height * 0.2f + translationY),
                    radius = width * 0.45f
                ),
                radius = width * 0.45f,
                center = Offset(width * 0.2f + translationX, height * 0.2f + translationY)
            )

            // Bottom-Right Cyan Orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(HbGoldBright.copy(alpha = 0.14f), Color.Transparent),
                    center = Offset(width * 0.8f - translationX, height * 0.8f - translationY),
                    radius = width * 0.4f
                ),
                radius = width * 0.4f,
                center = Offset(width * 0.8f - translationX, height * 0.8f - translationY)
            )

            // Violet glow top right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(HbGoldDim.copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(width * 0.75f, height * 0.25f),
                    radius = width * 0.35f
                ),
                radius = width * 0.35f,
                center = Offset(width * 0.75f, height * 0.25f)
            )
        }

        // Draw geometric grid lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepX = 60.dp.toPx()
            val stepY = 60.dp.toPx()
            val columns = (size.width / stepX).toInt()
            val rows = (size.height / stepY).toInt()

            for (i in 0..columns) {
                drawLine(
                    color = Color.White.copy(alpha = 0.02f),
                    start = Offset(i * stepX, 0f),
                    end = Offset(i * stepX, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            for (j in 0..rows) {
                drawLine(
                    color = Color.White.copy(alpha = 0.02f),
                    start = Offset(0f, j * stepY),
                    end = Offset(size.width, j * stepY),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        
        Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            content()
        }
    }
}

// --- Glass Card container ---
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = HbBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(HbGlass)
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .then(clickModifier)
            .padding(16.dp)
    ) {
        content()
    }
}

// --- Animated Gold Progress Bar ---
@Composable
fun GoldProgressBar(
    progress: Float, // 0.0f to 1.0f
    modifier: Modifier = Modifier,
    height: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(800, easing = EaseOutQuad),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(Color.White.copy(alpha = 0.08f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(height / 2))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(HbGoldDim, HbGold, HbGoldBright)
                    )
                )
        )
    }
}

// --- Custom Styled Buttons ---
@Composable
fun HabeshaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    icon: ImageVector? = null
) {
    val backgroundBrush = if (isPrimary) {
        Brush.horizontalGradient(colors = listOf(HbGoldDim, HbGold, HbGoldBright))
    } else {
        Brush.horizontalGradient(colors = listOf(Color.Transparent, Color.Transparent))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundBrush)
            .then(
                if (!isPrimary) Modifier.border(1.dp, HbGold.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                else Modifier
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertized,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isPrimary) HbBlack else HbGold,
                    modifier = Modifier.size(18.dp).padding(end = 6.dp)
                )
            }
            Text(
                text = text,
                color = if (isPrimary) HbBlack else HbGold,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

// --- Dynamic level badge ---
@Composable
fun LevelIndicator(level: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(HbGold.copy(alpha = 0.15f))
            .border(1.dp, HbGold, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "LEVEL ",
                color = HbGoldBright,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = level.toString(),
                color = HbText,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun CustomBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.5.dp, color, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

// --- Ethiopian Decorative Strip ---
@Composable
fun EthiopianFlagStrip(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().height(4.dp)) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(HbGreen))
        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(HbYellow))
        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(HbRed))
    }
}

// --- Interactive circular XP feedback ring ---
@Composable
fun LevelCircularProgress(
    xp: Int,
    level: Int,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    val nextLevelTargetXp = level * 1000
    val prevLevelTargetXp = (level - 1) * 1000
    val progressInsideLevel = (xp - prevLevelTargetXp).toFloat() / 1000f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progressInsideLevel.coerceIn(0f, 1f),
        animationSpec = tween(1200, easing = EaseOutQuad),
        label = "circularProgress"
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Track
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                style = Stroke(width = 8.dp.toPx())
            )
            // Progress Glow Ring
            drawArc(
                brush = Brush.sweepGradient(listOf(HbGoldDim, HbGold, HbGoldBright, HbGoldDim)),
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx())
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LVL",
                color = HbTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = level.toString(),
                color = HbGoldBright,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "${xp % 1000}/1000 XP",
                color = HbText,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// --- Nice Header banner for entire app ---
@Composable
fun HabeshaHeader(onSignOutClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circular monogram mimicking logo.png visual feel
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(HbGreen, HbYellow, HbRed)
                            )
                        )
                        .padding(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(7.dp))
                            .background(HbBlack),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "</>",
                            color = HbGoldBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "HABESHACODE",
                        color = HbGoldBright,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "LEARN . BUILD . INNOVATE",
                        color = HbTextMuted,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            
            IconButton(onClick = onSignOutClick) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Sign Out",
                    tint = HbTextMuted
                )
            }
        }
        EthiopianFlagStrip()
    }
}

// Helper to align row things
private val Alignment.Companion.CenterVertized: Alignment.Vertical
    get() = CenterVertically
