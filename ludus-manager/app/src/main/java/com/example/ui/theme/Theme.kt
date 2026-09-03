package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RomanColorScheme = darkColorScheme(
    primary = RomanGold,
    onPrimary = RomanDarkCrimson,
    primaryContainer = RomanCrimson,
    onPrimaryContainer = RomanParchment,
    secondary = RomanBronze,
    onSecondary = RomanBackground,
    secondaryContainer = RomanBronzeDark,
    onSecondaryContainer = RomanGoldLight,
    tertiary = RomanCrimson,
    onTertiary = RomanParchment,
    background = RomanBackground,
    onBackground = RomanTextPrimary,
    surface = RomanSurface,
    onSurface = RomanTextPrimary,
    surfaceVariant = RomanSurfaceVariant,
    onSurfaceVariant = RomanTextSecondary,
    error = RomanDangerRed,
    onError = RomanParchment
)

@Composable
fun LudusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RomanColorScheme,
        typography = Typography,
        content = content
    )
}
