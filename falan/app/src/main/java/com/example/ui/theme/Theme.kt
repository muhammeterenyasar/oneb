package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RomanImperialColorScheme = lightColorScheme(
    primary = ImperialRomanRed,
    onPrimary = Color.White,
    primaryContainer = ImperialRedSurface,
    onPrimaryContainer = ImperialRedDark,
    secondary = RomanImperialGold,
    onSecondary = Color.White,
    secondaryContainer = RomanGoldSurface,
    onSecondaryContainer = RomanImperialGold,
    tertiary = ImmersiveSta,
    onTertiary = Color.White,
    background = RomanMarbleBg,
    onBackground = RomanInkDark,
    surface = RomanCardBg,
    onSurface = RomanInkDark,
    surfaceVariant = RomanCardSecondary,
    onSurfaceVariant = RomanInkMedium,
    error = ImmersiveWarningText,
    onError = Color.White,
    outline = ImmersiveCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RomanImperialColorScheme,
        typography = Typography,
        content = content
    )
}
