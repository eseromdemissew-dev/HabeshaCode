package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HbGold,
    secondary = HbYellow,
    tertiary = HbGreen,
    background = HbBlack,
    surface = HbNavy,
    onPrimary = HbBlack,
    onSecondary = HbBlack,
    onBackground = HbText,
    onSurface = HbText,
    error = HbRed,
    outline = HbGoldDim
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Theme for Cyber Visual Atmosphere
    dynamicColor: Boolean = false, // Force consistent branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
