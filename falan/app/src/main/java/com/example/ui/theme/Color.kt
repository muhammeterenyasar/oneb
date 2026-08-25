package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// IMPERIAL ROMAN RED & MARBLE WHITE PALETTE
// ==========================================

// Surfaces & Backgrounds
val RomanMarbleBg = Color(0xFFF8F5F0) // Travertine White
val RomanCardBg = Color(0xFFFFFFFF)   // Pure Marble White
val RomanCardSecondary = Color(0xFFF3EFEA) // Soft Alabaster / Parchment
val RomanSurfaceParchment = Color(0xFFFBF8F2)

val ImmersiveBg = RomanMarbleBg
val ImmersiveCard = RomanCardBg
val ImmersiveCardBg = RomanCardBg
val ImmersiveCardBgSecondary = RomanCardSecondary
val ImmersiveCardBorder = Color(0xFFE2D8CA)
val ImmersiveCardBorderLight = Color(0xFFEDE4D8)
val ImmersiveBorderGold = Color(0xFFC5A059)
val ImmersiveBorderSubtle = Color(0xFFE8E0D5)
val ImmersiveTrack = Color(0xFFEFE9E0)

// High-Contrast Roman Ink / Obsidian Typography
val RomanInkDark = Color(0xFF1A1715)      // Primary Black/Dark Ink
val RomanInkMedium = Color(0xFF423B35)    // Secondary Subtitle
val RomanInkMuted = Color(0xFF6B6259)     // Muted Captions

val ImmersiveTextPrimary = RomanInkDark
val ImmersiveTextSecondary = RomanInkMedium
val ImmersiveTextMuted = RomanInkMuted

// Imperial Roman Red & Crimson Accents (SPQR / Legion)
val ImperialRomanRed = Color(0xFF9E1B32)     // Primary Imperial Crimson
val ImperialRedDark = Color(0xFF7A0018)      // Dark Crimson Headers / Borders
val ImperialRedLight = Color(0xFFBC2E48)     // Lighter Crimson
val ImperialRedSurface = Color(0xFFFDF2F4)   // Subtle Red Tint

val ImmersiveTerracotta = ImperialRomanRed
val ImmersiveTerracottaLight = ImperialRedLight
val ImmersiveTerracottaDark = ImperialRedDark
val ImmersiveTerracottaDeep = ImperialRedSurface
val ImmersiveCrimson = ImperialRomanRed
val ImmersiveCrimsonDark = ImperialRedDark

// Roman Gold & Laurel Accents
val RomanImperialGold = Color(0xFFB8860B)     // Rich Roman Gold
val RomanGoldLightAccent = Color(0xFFD4AF37)  // Bright Gold
val RomanGoldSurface = Color(0xFFFFF9E6)      // Warm Gold Tint

val ImmersiveGold = RomanImperialGold
val ImmersiveGoldLight = RomanGoldLightAccent
val ImmersiveGoldDark = Color(0xFF8B6508)

// Alerts & Notifications
val ImmersiveWarningBg = Color(0xFFFEF2F2)
val ImmersiveWarningText = Color(0xFF991B1B)
val ImmersiveWarningBorder = Color(0xFFFECACA)

// Attributes & Combat Stats
val ImmersiveStr = Color(0xFF9E1B32)  // Strength - Roman Red
val ImmersiveAgi = Color(0xFF0284C7)  // Agility - Aqueduct Blue
val ImmersiveSta = Color(0xFF16A34A)  // Stamina - Laurel Green
val ImmersiveMor = Color(0xFF7E22CE)  // Morale - Tyrian Purple

val ImmersiveEmerald = Color(0xFF16A34A)
val ImmersiveSuccess = ImmersiveEmerald
val ImmersiveEmeraldDark = Color(0xFF15803D)
val ImmersiveSlate800 = Color(0xFF334155)
val ImmersiveSlate400 = Color(0xFF64748B)

// Roman aliases for backwards compatibility
val ImperialCrimson = ImperialRomanRed
val ImperialCrimsonDark = ImperialRedDark
val ImperialCrimsonDeep = ImperialRedSurface

val RomanGold = ImmersiveGold
val RomanGoldLight = ImmersiveGoldLight
val RomanGoldDark = ImmersiveGoldDark

val AntiqueBronze = Color(0xFF5A3E2B)
val TerracottaSand = ImperialRomanRed
val TerracottaBg = RomanSurfaceParchment

val RomanMarbleLight = RomanCardBg
val RomanMarbleSurface = RomanCardBg
val RomanDarkSurface = RomanMarbleBg
val RomanDarkCard = RomanCardBg
val RomanDarkCardBorder = ImmersiveCardBorder

val RomanLaurelGreen = ImmersiveSta
val RomanLaurelDark = ImmersiveEmeraldDark

val BloodRust = ImmersiveTerracottaDark
val RomanSteel = Color(0xFF475569)
val RomanParchment = RomanGoldSurface
