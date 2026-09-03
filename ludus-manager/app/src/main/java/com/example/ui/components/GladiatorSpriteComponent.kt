package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Gladiator
import com.example.model.GladiatorClass
import com.example.model.GladiatorStatus
import com.example.ui.theme.*

/**
 * Returns the corresponding vector drawable resource for a given Gladiator class.
 */
fun getGladiatorSpriteDrawableRes(gladiatorClass: GladiatorClass): Int {
    return when (gladiatorClass) {
        GladiatorClass.MURMILLO -> R.drawable.ic_sprite_murmillo
        GladiatorClass.THRAEX -> R.drawable.ic_sprite_thraex
        GladiatorClass.RETIARIUS -> R.drawable.ic_sprite_retiarius
        GladiatorClass.SECUTOR -> R.drawable.ic_sprite_secutor
        GladiatorClass.HOPLOMACHUS -> R.drawable.ic_sprite_hoplomachus
        GladiatorClass.DIMACHAERUS -> R.drawable.ic_sprite_dimachaerus
        GladiatorClass.BESTIARIUS -> R.drawable.ic_sprite_bestiarius
        GladiatorClass.HEAVY_VETERAN -> R.drawable.ic_sprite_murmillo
        GladiatorClass.LIGHT_DUELIST -> R.drawable.ic_sprite_thraex
        GladiatorClass.SPECIAL_ARCHETYPE -> R.drawable.ic_sprite_dimachaerus
    }
}

/**
 * High-definition gladiator sprite showcase with animated stance,
 * stone pedestal, combat status aura, and class armaments badge.
 */
@Composable
fun GladiatorSpriteShowcase(
    gladiator: Gladiator,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GladiatorStanceAnimation")
    val verticalOffset by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IdleBobbing"
    )
    val weaponGleamAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WeaponGleam"
    )

    val isChampion = gladiator.status == GladiatorStatus.RUDIS_HOLDER || gladiator.historicalPerformance.currentWinStreak >= 3
    val isWounded = gladiator.condition.health < 50 || gladiator.isInjured

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(6.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        if (isChampion) Color(0xFF4A3816) else Color(0xFF2C1E16),
                        Color(0xFF140F0C)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                color = if (isChampion) RomanGold else RomanBronze,
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background rays / aura for champions
        if (isChampion) {
            Box(
                modifier = Modifier
                    .size(height * 0.9f)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(RomanGold.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            )
        }

        // Stone Pedestal / Plinth Base
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
                .width(130.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF423326), Color(0xFF1F1710))
                    )
                )
                .border(0.8.dp, RomanBronzeDark, RoundedCornerShape(4.dp))
        )

        // The Gladiator Vector Sprite Image with Idle Bobbing
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = verticalOffset.dp)
        ) {
            Image(
                painter = painterResource(id = getGladiatorSpriteDrawableRes(gladiator.gladiatorClass)),
                contentDescription = "${gladiator.name} sprite - ${gladiator.gladiatorClass.title}",
                modifier = Modifier.size(height * 0.78f),
                contentScale = ContentScale.Fit
            )
        }

        // Class and Weapon Badge overlay
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(6.dp)
                .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(3.dp))
                .border(0.5.dp, RomanGold, RoundedCornerShape(3.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${gladiator.gladiatorClass.title.uppercase()} • ${gladiator.gladiatorClass.primaryWeapon}",
                color = RomanGoldLight,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Status Badge (Champion / Wounded)
        if (isChampion) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                .padding(6.dp)
                    .background(RomanCrimson, RoundedCornerShape(3.dp))
                    .border(0.5.dp, RomanGold, RoundedCornerShape(3.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "★ SERİ: ${gladiator.historicalPerformance.currentWinStreak} ZAFER",
                    color = RomanGold,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black
                )
            }
        } else if (isWounded) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .background(RomanDangerRed.copy(alpha = 0.85f), RoundedCornerShape(3.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "YARALI / BİTKİN",
                    color = RomanParchment,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Subtitle plinth inscription
        Text(
            text = "« ${gladiator.name.uppercase()} • S.P.Q.R. »",
            color = RomanGoldLight.copy(alpha = 0.8f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 2.dp)
        )
    }
}

/**
 * Compact mini sprite for table rows, selection chips, and combat indicators.
 */
@Composable
fun GladiatorMiniSprite(
    gladiatorClass: GladiatorClass,
    size: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF221914))
            .border(0.8.dp, RomanBronze, RoundedCornerShape(4.dp))
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = getGladiatorSpriteDrawableRes(gladiatorClass)),
            contentDescription = gladiatorClass.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
