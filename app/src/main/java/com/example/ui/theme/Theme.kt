package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ClubScarlet,
    secondary = GoldAccent,
    tertiary = ClubVelvetDark,
    background = DarkObsidian,
    surface = CharcoalSurface,
    onPrimary = Color.White,
    onSecondary = DarkObsidian,
    onBackground = Color.White,
    onSurface = Color.White,
    outline = GoldAccent
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark mode as club standard
    content: @Composable () -> Unit,
) {
    // Standardize to luxury DarkColorScheme to ensure flawless global look
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
