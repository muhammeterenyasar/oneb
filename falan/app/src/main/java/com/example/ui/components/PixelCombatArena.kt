package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BattleState
import com.example.model.BattleTactic
import com.example.model.GladiatorClass
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Combat Animation States
enum class FighterAnimState {
    IDLE,
    ATTACK_LUNGE,
    STRIKE,
    SLASH_SWING,
    BLOCK_PARRY,
    DODGE_BACKSTEP,
    HURT_KNOCKBACK,
    KNEELING_DEFEAT,
    VICTORY_SALUTE
}

data class FloatingCombatText(
    val id: Long,
    val text: String,
    val isCrit: Boolean,
    val isDodgeOrBlock: Boolean,
    val isPlayerReceiver: Boolean,
    val startXRatio: Float,
    val startYRatio: Float
)

data class BloodSplatter(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val color: Color
)

data class FlyingParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val alpha: Float = 1f
)

/**
 * Pixel-Art Battle Visualization Engine
 * Custom Canvas & Sprite-based combat animation system reflecting real-time tactical choices,
 * class-specific weaponry, damage outcomes, and dynamic arena atmosphere.
 */
@Composable
fun PixelCombatArena(
    battle: BattleState,
    onShoutTactic: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // -------------------------------------------------------------
    // ANIMATION & TIMING CONTROLLERS
    // -------------------------------------------------------------
    val infiniteTransition = rememberInfiniteTransition(label = "arena_infinite_fx")

    // Idle breathing & weapon bobbing cycle
    val breathCycle by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_cycle"
    )

    // Torch light flicker
    val torchFlicker by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(180, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "torch_flicker"
    )

    // Crowd cheer rhythm
    val crowdCheerSway by infiniteTransition.animateFloat(
        initialValue = -3.5f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(380, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crowd_cheer"
    )

    // Tactic Aura Pulse
    val tacticAuraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tactic_aura"
    )

    // -------------------------------------------------------------
    // DYNAMIC COMBAT SIMULATION STATE
    // -------------------------------------------------------------
    var playerState by remember { mutableStateOf(FighterAnimState.IDLE) }
    var enemyState by remember { mutableStateOf(FighterAnimState.IDLE) }
    var playerLungeOffset by remember { mutableStateOf(0f) }
    var enemyLungeOffset by remember { mutableStateOf(0f) }
    var screenShakeOffset by remember { mutableStateOf(0f) }
    var flashWhiteScreen by remember { mutableStateOf(false) }
    var showNetThrow by remember { mutableStateOf(false) }
    var netThrowProgress by remember { mutableStateOf(0f) }

    // Persistent Sand Blood & Particles
    val floatingTexts = remember { mutableStateListOf<FloatingCombatText>() }
    val bloodSplatters = remember { mutableStateListOf<BloodSplatter>() }
    val combatParticles = remember { mutableStateListOf<FlyingParticle>() }

    val latestLog = battle.logs.lastOrNull()

    // React in real-time to each combat turn log
    LaunchedEffect(battle.turnCount, battle.logs.size) {
        if (latestLog == null) return@LaunchedEffect

        // Victory / Defeat Sequence
        if (battle.isAwaitingCrowdJudgement || battle.isFinished) {
            if (battle.isPlayerVictorious || battle.enemyCurrentHp <= 0) {
                playerState = FighterAnimState.VICTORY_SALUTE
                enemyState = FighterAnimState.KNEELING_DEFEAT
            } else {
                playerState = FighterAnimState.KNEELING_DEFEAT
                enemyState = FighterAnimState.VICTORY_SALUTE
            }
            return@LaunchedEffect
        }

        val isPlayerAttacker = latestLog.isPlayerAction
        val isDodge = latestLog.text.contains("çevik") || latestLog.text.contains("sıyrıldı") || latestLog.text.contains("kaçındı")
        val isBlock = latestLog.text.contains("kalkan") || latestLog.text.contains("savuşturdu")
        val isCrit = latestLog.isCritical
        val damage = latestLog.damageDealt

        if (isPlayerAttacker) {
            // Player Attack Lunge
            playerState = FighterAnimState.ATTACK_LUNGE
            playerLungeOffset = 42f

            // Retiarius throws net on crit or attack
            if (battle.playerGladiator.gladiatorClass == GladiatorClass.RETIARIUS) {
                showNetThrow = true
                netThrowProgress = 0f
            }

            delay(120)

            // Slash / Strike
            playerState = if (battle.playerGladiator.gladiatorClass == GladiatorClass.DIMACHAERUS) {
                FighterAnimState.SLASH_SWING
            } else {
                FighterAnimState.STRIKE
            }

            if (isDodge) {
                enemyState = FighterAnimState.DODGE_BACKSTEP
                enemyLungeOffset = 32f
                floatingTexts.add(
                    FloatingCombatText(
                        id = System.currentTimeMillis(),
                        text = "💨 SIYRILDI!",
                        isCrit = false,
                        isDodgeOrBlock = true,
                        isPlayerReceiver = false,
                        startXRatio = 0.72f,
                        startYRatio = 0.45f
                    )
                )
            } else if (isBlock) {
                enemyState = FighterAnimState.BLOCK_PARRY
                floatingTexts.add(
                    FloatingCombatText(
                        id = System.currentTimeMillis(),
                        text = "🛡️ BLOK!",
                        isCrit = false,
                        isDodgeOrBlock = true,
                        isPlayerReceiver = false,
                        startXRatio = 0.72f,
                        startYRatio = 0.45f
                    )
                )
            } else {
                // Direct Hit Impact
                enemyState = FighterAnimState.HURT_KNOCKBACK
                enemyLungeOffset = 20f

                if (isCrit) {
                    screenShakeOffset = 10f
                    flashWhiteScreen = true
                    // Add dynamic blood stains on Colosseum sand
                    val splatterX = 0.68f + Random.nextFloat() * 0.10f
                    val splatterY = 0.80f + Random.nextFloat() * 0.08f
                    bloodSplatters.add(
                        BloodSplatter(
                            xRatio = splatterX,
                            yRatio = splatterY,
                            radius = 16f + Random.nextFloat() * 12f,
                            color = if (Random.nextBoolean()) Color(0xFF8B0000) else Color(0xFF5A0204)
                        )
                    )
                }

                if (damage > 0) {
                    floatingTexts.add(
                        FloatingCombatText(
                            id = System.currentTimeMillis(),
                            text = if (isCrit) "-$damage 💥 KRİTİK!" else "-$damage",
                            isCrit = isCrit,
                            isDodgeOrBlock = false,
                            isPlayerReceiver = false,
                            startXRatio = 0.70f,
                            startYRatio = 0.40f
                        )
                    )
                }
            }

            delay(220)
            showNetThrow = false
            flashWhiteScreen = false
            screenShakeOffset = 0f
            playerLungeOffset = 0f
            enemyLungeOffset = 0f
            playerState = FighterAnimState.IDLE
            enemyState = FighterAnimState.IDLE

        } else {
            // Enemy Attack Lunge
            enemyState = FighterAnimState.ATTACK_LUNGE
            enemyLungeOffset = -42f

            if (battle.enemyGladiator.gladiatorClass == GladiatorClass.RETIARIUS) {
                showNetThrow = true
                netThrowProgress = 1f
            }

            delay(120)

            enemyState = FighterAnimState.STRIKE

            if (isDodge) {
                playerState = FighterAnimState.DODGE_BACKSTEP
                playerLungeOffset = -32f
                floatingTexts.add(
                    FloatingCombatText(
                        id = System.currentTimeMillis(),
                        text = "💨 SIYRILDI!",
                        isCrit = false,
                        isDodgeOrBlock = true,
                        isPlayerReceiver = true,
                        startXRatio = 0.28f,
                        startYRatio = 0.45f
                    )
                )
            } else if (isBlock) {
                playerState = FighterAnimState.BLOCK_PARRY
                floatingTexts.add(
                    FloatingCombatText(
                        id = System.currentTimeMillis(),
                        text = "🛡️ BLOK!",
                        isCrit = false,
                        isDodgeOrBlock = true,
                        isPlayerReceiver = true,
                        startXRatio = 0.28f,
                        startYRatio = 0.45f
                    )
                )
            } else {
                // Player Hurt
                playerState = FighterAnimState.HURT_KNOCKBACK
                playerLungeOffset = -20f

                if (isCrit) {
                    screenShakeOffset = -10f
                    flashWhiteScreen = true
                    val splatterX = 0.24f + Random.nextFloat() * 0.10f
                    val splatterY = 0.80f + Random.nextFloat() * 0.08f
                    bloodSplatters.add(
                        BloodSplatter(
                            xRatio = splatterX,
                            yRatio = splatterY,
                            radius = 16f + Random.nextFloat() * 12f,
                            color = Color(0xFF8B0000)
                        )
                    )
                }

                if (damage > 0) {
                    floatingTexts.add(
                        FloatingCombatText(
                            id = System.currentTimeMillis(),
                            text = if (isCrit) "-$damage 💥 KRİTİK!" else "-$damage",
                            isCrit = isCrit,
                            isDodgeOrBlock = false,
                            isPlayerReceiver = true,
                            startXRatio = 0.28f,
                            startYRatio = 0.40f
                        )
                    )
                }
            }

            delay(220)
            showNetThrow = false
            flashWhiteScreen = false
            screenShakeOffset = 0f
            playerLungeOffset = 0f
            enemyLungeOffset = 0f
            playerState = FighterAnimState.IDLE
            enemyState = FighterAnimState.IDLE
        }
    }

    // Auto-clean floating damage indicators
    LaunchedEffect(floatingTexts.size) {
        if (floatingTexts.isNotEmpty()) {
            delay(1100)
            if (floatingTexts.isNotEmpty()) {
                floatingTexts.removeAt(0)
            }
        }
    }

    // -------------------------------------------------------------
    // UI LAYOUT & CANVAS STAGE
    // -------------------------------------------------------------
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(245.dp)
            .testTag("pixel_combat_arena_stage"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF140D0A)),
        border = BorderStroke(1.5.dp, ImmersiveGold)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Custom Canvas Visualizer Engine
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = screenShakeOffset.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val fighterGroundY = canvasHeight * 0.78f

                // 1. ARENA SKY & COLOSSEUM ARCHITECTURE
                drawColosseumBackground(
                    width = canvasWidth,
                    height = canvasHeight,
                    crowdCheer = crowdCheerSway,
                    torchFlicker = torchFlicker,
                    hypeLevel = battle.crowdHype
                )

                // 2. ARENA SAND FLOOR & DYNAMIC BLOOD STAINS
                drawArenaSandFloor(
                    width = canvasWidth,
                    height = canvasHeight,
                    bloodSplatters = bloodSplatters
                )

                // 3. TACTICAL AURAS & STANCE GLOW
                drawTacticalAura(
                    centerX = canvasWidth * 0.28f + (playerLungeOffset * (canvasWidth / 360f)),
                    centerY = fighterGroundY,
                    tactic = battle.tactic,
                    auraAlpha = tacticAuraAlpha
                )

                // 4. PIXEL GLADIATOR: PLAYER (LEFT)
                val playerBaseX = canvasWidth * 0.28f + (playerLungeOffset * (canvasWidth / 360f))
                val isPlayerLowHp = battle.playerCurrentHp < (battle.playerGladiator.maxHp * 0.35f)
                val isPlayerExhausted = battle.playerCurrentStamina < 20

                drawPixelGladiatorSprite(
                    x = playerBaseX,
                    groundY = fighterGroundY,
                    gladiatorClass = battle.playerGladiator.gladiatorClass,
                    isPlayer = true,
                    animState = playerState,
                    breathOffset = breathCycle,
                    isHurt = playerState == FighterAnimState.HURT_KNOCKBACK,
                    isLowHp = isPlayerLowHp,
                    isExhausted = isPlayerExhausted,
                    primaryColor = Color(0xFFC99700), // Roman Gold / Crimson
                    armorLevel = battle.playerGladiator.armorLevel,
                    tactic = battle.tactic
                )

                // 5. PIXEL GLADIATOR: ENEMY (RIGHT)
                val enemyBaseX = canvasWidth * 0.72f + (enemyLungeOffset * (canvasWidth / 360f))
                val isEnemyLowHp = battle.enemyCurrentHp < (battle.enemyGladiator.maxHp * 0.35f)
                val isEnemyExhausted = battle.enemyCurrentStamina < 20

                drawPixelGladiatorSprite(
                    x = enemyBaseX,
                    groundY = fighterGroundY,
                    gladiatorClass = battle.enemyGladiator.gladiatorClass,
                    isPlayer = false,
                    animState = enemyState,
                    breathOffset = -breathCycle,
                    isHurt = enemyState == FighterAnimState.HURT_KNOCKBACK,
                    isLowHp = isEnemyLowHp,
                    isExhausted = isEnemyExhausted,
                    primaryColor = Color(0xFF9E2A2B), // Imperial Crimson
                    armorLevel = 2,
                    tactic = BattleTactic.AGGRESSIVE
                )

                // 6. RETIARIUS NET THROW EFFECT
                if (showNetThrow) {
                    drawRetiariusNet(
                        startX = if (netThrowProgress == 0f) playerBaseX else enemyBaseX,
                        targetX = if (netThrowProgress == 0f) enemyBaseX else playerBaseX,
                        groundY = fighterGroundY - 30.dp.toPx()
                    )
                }

                // 7. WEAPON SLASH ARCS & CLASH SPARKS
                if (playerState == FighterAnimState.STRIKE || playerState == FighterAnimState.SLASH_SWING ||
                    enemyState == FighterAnimState.STRIKE || enemyState == FighterAnimState.SLASH_SWING
                ) {
                    val clashX = if (playerState == FighterAnimState.STRIKE || playerState == FighterAnimState.SLASH_SWING) {
                        canvasWidth * 0.62f
                    } else {
                        canvasWidth * 0.38f
                    }
                    val clashY = fighterGroundY - 42.dp.toPx()

                    drawClashImpactEffect(
                        x = clashX,
                        y = clashY,
                        isCrit = latestLog?.isCritical == true,
                        gladiatorClass = if (playerState == FighterAnimState.STRIKE || playerState == FighterAnimState.SLASH_SWING) {
                            battle.playerGladiator.gladiatorClass
                        } else {
                            battle.enemyGladiator.gladiatorClass
                        }
                    )
                }

                // 8. CRITICAL HIT FLASH
                if (flashWhiteScreen) {
                    drawRect(
                        color = Color.White.copy(alpha = 0.28f),
                        size = size
                    )
                }
            }

            // Top Status Bar: Pixel Health & Stamina Bars over canvas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player Mini Bar
                PixelGladiatorStatusBadge(
                    name = battle.playerGladiator.name,
                    gladiatorClass = battle.playerGladiator.gladiatorClass,
                    currentHp = battle.playerCurrentHp,
                    maxHp = battle.playerGladiator.maxHp,
                    currentStamina = battle.playerCurrentStamina,
                    isPlayer = true
                )

                // Arena Hype Stamp & Tactic Badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ImmersiveCardBg.copy(alpha = 0.90f),
                        border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔥", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "%${battle.crowdHype} HYPE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.5.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = ImmersiveGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = battle.tactic.title.take(12),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = when (battle.tactic) {
                            BattleTactic.AGGRESSIVE -> Color(0xFFFF8A80)
                            BattleTactic.DEFENSIVE -> Color(0xFF90CAF9)
                            BattleTactic.CROWD_PLEASER -> ImmersiveGold
                            BattleTactic.MAIMING -> Color(0xFFFF5252)
                        }
                    )
                }

                // Enemy Mini Bar
                PixelGladiatorStatusBadge(
                    name = battle.enemyGladiator.name,
                    gladiatorClass = battle.enemyGladiator.gladiatorClass,
                    currentHp = battle.enemyCurrentHp,
                    maxHp = battle.enemyGladiator.maxHp,
                    currentStamina = battle.enemyCurrentStamina,
                    isPlayer = false
                )
            }

            // Floating Damage Numbers & Combat Text Animations
            floatingTexts.forEach { floating ->
                val textAnim = remember { Animatable(0f) }
                LaunchedEffect(floating.id) {
                    textAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(750, easing = LinearOutSlowInEasing)
                    )
                }

                val currentYOffset = -45.dp * textAnim.value
                val currentAlpha = (1f - textAnim.value).coerceIn(0f, 1f)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = if (floating.isPlayerReceiver) 32.dp else 205.dp,
                            top = (95.dp + currentYOffset)
                        )
                ) {
                    Text(
                        text = floating.text,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = if (floating.isCrit) 16.sp else 13.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = when {
                            floating.isCrit -> Color(0xFFFFD54F).copy(alpha = currentAlpha)
                            floating.isDodgeOrBlock -> Color(0xFF64B5F6).copy(alpha = currentAlpha)
                            else -> Color(0xFFFF5252).copy(alpha = currentAlpha)
                        }
                    )
                }
            }

            // Bottom Stage Footer
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚔️ PIXEL ARENA ENGINE • ${battle.turnCount}. HAMLE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 8.5.sp,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = ImmersiveGold.copy(alpha = 0.65f)
                )
            }
        }
    }
}

/**
 * Pixel-Art Status Bar for Health and Stamina
 */
@Composable
private fun PixelGladiatorStatusBadge(
    name: String,
    gladiatorClass: GladiatorClass,
    currentHp: Int,
    maxHp: Int,
    currentStamina: Int,
    isPlayer: Boolean
) {
    val hpFraction = (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)
    val staFraction = (currentStamina.toFloat() / 100f).coerceIn(0f, 1f)

    Column(
        horizontalAlignment = if (isPlayer) Alignment.Start else Alignment.End,
        modifier = Modifier.width(115.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isPlayer) Arrangement.Start else Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isPlayer) {
                Text(text = "🛡️", fontSize = 10.sp)
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = name.take(10),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = if (isPlayer) ImmersiveGold else ImmersiveTextPrimary,
                maxLines = 1
            )
            if (!isPlayer) {
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "⚔️", fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Pixel HP bar with segment border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .background(Color(0xFF22110D), RoundedCornerShape(1.dp))
                .border(1.dp, Color(0xFF44221A), RoundedCornerShape(1.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = hpFraction)
                    .background(
                        if (hpFraction < 0.3f) Color(0xFFD32F2F) else Color(0xFF388E3C),
                        RoundedCornerShape(1.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Pixel STA bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFF22110D), RoundedCornerShape(1.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = staFraction)
                    .background(Color(0xFFFFA000), RoundedCornerShape(1.dp))
            )
        }
    }
}

// -------------------------------------------------------------
// CANVAS PIXEL DRAWING ENGINE
// -------------------------------------------------------------

private fun DrawScope.drawColosseumBackground(
    width: Float,
    height: Float,
    crowdCheer: Float,
    torchFlicker: Float,
    hypeLevel: Int
) {
    // 1. Sky Gradient (Sunset Twilight over Colosseum)
    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF1E1018), // Twilight purple
                Color(0xFF4A1E18), // Terracotta crimson sky
                Color(0xFF6E321E)  // Warm horizon glow
            ),
            startY = 0f,
            endY = height * 0.58f
        ),
        size = Size(width, height * 0.58f)
    )

    // 2. Colosseum Upper Arches & Stone Pillars
    val archWidth = width / 7f
    for (i in 0 until 8) {
        val archX = i * archWidth
        val archH = height * 0.35f

        // Stone pillar
        drawRect(
            color = Color(0xFF2C1814),
            topLeft = Offset(archX, 0f),
            size = Size(archWidth * 0.3f, archH)
        )

        // Arch curve
        drawRoundRect(
            color = Color(0xFF1E100D),
            topLeft = Offset(archX + archWidth * 0.15f, height * 0.08f),
            size = Size(archWidth * 0.7f, archH * 0.75f),
            cornerRadius = CornerRadius(archWidth * 0.35f, archWidth * 0.35f)
        )
    }

    // 3. Pixel Spectators in the Stands
    val crowdY = height * 0.32f
    val spectatorCount = 14
    for (j in 0 until spectatorCount) {
        val headX = (j * (width / spectatorCount.toFloat())) + 10f
        val swayOffset = if (j % 2 == 0) crowdCheer else -crowdCheer
        val headColor = when (j % 4) {
            0 -> Color(0xFFE0A96D) // Skin tone
            1 -> Color(0xFF9E2A2B) // Toga crimson
            2 -> Color(0xFFD4AF37) // Gold laurel
            else -> Color(0xFFE0E0E0) // Patrician white toga
        }

        // Spectator Head
        drawRect(
            color = headColor,
            topLeft = Offset(headX, crowdY + swayOffset),
            size = Size(7.dp.toPx(), 7.dp.toPx())
        )

        // Cheering Arms raised on high hype
        if (hypeLevel > 50 || j % 3 == 0) {
            drawRect(
                color = headColor,
                topLeft = Offset(headX - 4.dp.toPx(), crowdY + swayOffset - 5.dp.toPx()),
                size = Size(3.dp.toPx(), 5.dp.toPx())
            )
            drawRect(
                color = headColor,
                topLeft = Offset(headX + 8.dp.toPx(), crowdY + swayOffset - 5.dp.toPx()),
                size = Size(3.dp.toPx(), 5.dp.toPx())
            )
        }
    }

    // 4. Roman Wall & Railing separating stands from arena floor
    drawRect(
        color = Color(0xFF3E2219),
        topLeft = Offset(0f, height * 0.44f),
        size = Size(width, 10.dp.toPx())
    )

    // Imperial Roman Banners hanging from wall
    listOf(0.2f, 0.5f, 0.8f).forEach { ratio ->
        val bannerX = width * ratio
        val bannerPath = Path().apply {
            moveTo(bannerX, height * 0.44f)
            lineTo(bannerX + 18.dp.toPx(), height * 0.44f)
            lineTo(bannerX + 18.dp.toPx(), height * 0.56f)
            lineTo(bannerX + 9.dp.toPx(), height * 0.60f)
            lineTo(bannerX, height * 0.56f)
            close()
        }
        drawPath(path = bannerPath, color = Color(0xFF800E13))
        // Gold eagle / SPQR emblem on banner
        drawCircle(
            color = Color(0xFFD4AF37),
            radius = 3.dp.toPx(),
            center = Offset(bannerX + 9.dp.toPx(), height * 0.50f)
        )
    }

    // 5. Arena Wall Torches
    listOf(0.08f, 0.92f).forEach { ratio ->
        val torchX = width * ratio
        val torchY = height * 0.40f

        // Torch mount
        drawRect(
            color = Color(0xFF140D0A),
            topLeft = Offset(torchX - 3.dp.toPx(), torchY),
            size = Size(6.dp.toPx(), 16.dp.toPx())
        )

        // Flickering Flame Pixels
        val flameSize = 8.dp.toPx() * torchFlicker
        drawCircle(
            color = Color(0xFFFF5722),
            radius = flameSize,
            center = Offset(torchX, torchY - 4.dp.toPx())
        )
        drawCircle(
            color = Color(0xFFFFD54F),
            radius = flameSize * 0.6f,
            center = Offset(torchX, torchY - 5.dp.toPx())
        )
    }
}

private fun DrawScope.drawArenaSandFloor(
    width: Float,
    height: Float,
    bloodSplatters: List<BloodSplatter>
) {
    val sandStartY = height * 0.50f

    // Sand Ground Gradient
    drawRect(
        brush = Brush.verticalGradient(
            listOf(
                Color(0xFF5C3826), // Dark sand boundary
                Color(0xFF8C5832), // Warm Colosseum golden sand
                Color(0xFF4A2818)  // Foreground shadow
            ),
            startY = sandStartY,
            endY = height
        ),
        topLeft = Offset(0f, sandStartY),
        size = Size(width, height - sandStartY)
    )

    // Sand Texture Speckles (Pixel grains)
    val grainColor = Color(0xFF9E693D)
    for (gx in 0 until 22) {
        val px = (gx * (width / 22f)) + (gx * 7 % 13)
        val py = sandStartY + ((gx * 19) % (height - sandStartY - 10))
        drawRect(
            color = grainColor,
            topLeft = Offset(px, py),
            size = Size(3.dp.toPx(), 2.dp.toPx())
        )
    }

    // Dynamic Blood Splatters from critical hits
    bloodSplatters.forEach { splatter ->
        val px = splatter.xRatio * width
        val py = splatter.yRatio * height
        drawCircle(
            color = splatter.color,
            radius = splatter.radius,
            center = Offset(px, py)
        )
        drawCircle(
            color = Color(0xFF3F0102),
            radius = splatter.radius * 0.5f,
            center = Offset(px + 2f, py + 1f)
        )
    }
}

/**
 * Draws tactical aura representing the active strategy
 */
private fun DrawScope.drawTacticalAura(
    centerX: Float,
    centerY: Float,
    tactic: BattleTactic,
    auraAlpha: Float
) {
    val auraColor = when (tactic) {
        BattleTactic.AGGRESSIVE -> Color(0xFFFF5252).copy(alpha = auraAlpha * 0.45f)
        BattleTactic.DEFENSIVE -> Color(0xFF42A5F5).copy(alpha = auraAlpha * 0.45f)
        BattleTactic.CROWD_PLEASER -> Color(0xFFFFD54F).copy(alpha = auraAlpha * 0.45f)
        BattleTactic.MAIMING -> Color(0xFFD50000).copy(alpha = auraAlpha * 0.55f)
    }

    // Ground Aura Ellipse
    drawOval(
        color = auraColor,
        topLeft = Offset(centerX - 35.dp.toPx(), centerY - 6.dp.toPx()),
        size = Size(70.dp.toPx(), 18.dp.toPx())
    )

    // Upward tactical energy particles
    if (tactic == BattleTactic.AGGRESSIVE) {
        drawCircle(
            color = Color(0xFFFF8A80).copy(alpha = auraAlpha),
            radius = 3.dp.toPx(),
            center = Offset(centerX - 12.dp.toPx(), centerY - 25.dp.toPx())
        )
        drawCircle(
            color = Color(0xFFFFD54F).copy(alpha = auraAlpha),
            radius = 2.5.dp.toPx(),
            center = Offset(centerX + 14.dp.toPx(), centerY - 32.dp.toPx())
        )
    } else if (tactic == BattleTactic.CROWD_PLEASER) {
        // Sparkle stars
        drawCircle(
            color = Color(0xFFFFD700).copy(alpha = auraAlpha),
            radius = 2.5.dp.toPx(),
            center = Offset(centerX - 10.dp.toPx(), centerY - 35.dp.toPx())
        )
    }
}

/**
 * Pixel Gladiator Sprite Renderer
 * Multi-layer procedural pixel art system reflecting class weapons, armors, and physical condition
 */
private fun DrawScope.drawPixelGladiatorSprite(
    x: Float,
    groundY: Float,
    gladiatorClass: GladiatorClass,
    isPlayer: Boolean,
    animState: FighterAnimState,
    breathOffset: Float,
    isHurt: Boolean,
    isLowHp: Boolean,
    isExhausted: Boolean,
    primaryColor: Color,
    armorLevel: Int,
    tactic: BattleTactic
) {
    val pixelSize = 2.85.dp.toPx()
    val facing = if (isPlayer) 1f else -1f // 1 = facing right, -1 = facing left

    // Apply animation offsets
    val bodyBobY = when (animState) {
        FighterAnimState.IDLE -> if (isExhausted) breathOffset * 1.5f else breathOffset
        FighterAnimState.ATTACK_LUNGE -> 4f
        FighterAnimState.STRIKE, FighterAnimState.SLASH_SWING -> 2f
        FighterAnimState.DODGE_BACKSTEP -> -4f
        FighterAnimState.HURT_KNOCKBACK -> -6f
        FighterAnimState.BLOCK_PARRY -> 2f
        FighterAnimState.KNEELING_DEFEAT -> 18f
        FighterAnimState.VICTORY_SALUTE -> -8f
    }

    val fighterCenterY = groundY + bodyBobY

    // 1. Shadow beneath fighter
    val shadowW = if (animState == FighterAnimState.KNEELING_DEFEAT) 38.dp.toPx() else 30.dp.toPx()
    drawOval(
        color = Color(0xFF22110B).copy(alpha = 0.65f),
        topLeft = Offset(x - (shadowW / 2), groundY + 8.dp.toPx()),
        size = Size(shadowW, 8.dp.toPx())
    )

    // Palette Configuration
    val skinColor = when {
        isHurt -> Color(0xFFFF8A80)
        isLowHp -> Color(0xFFD7A77C) // Paler skin when bleeding/hurt
        else -> Color(0xFFE0AC69)
    }
    val leatherColor = Color(0xFF5D4037)
    val steelColor = if (armorLevel >= 3) Color(0xFFFFD700) else Color(0xFFB0BEC5)
    val helmetPlumeColor = primaryColor
    val tunicColor = primaryColor

    // Helper lambda to draw a pixel block relative to gladiator center
    fun drawPixel(gridX: Int, gridY: Int, color: Color, sizeMultiplier: Float = 1f) {
        val actualX = x + (gridX * pixelSize * facing)
        val actualY = fighterCenterY + (gridY * pixelSize)
        drawRect(
            color = color,
            topLeft = Offset(actualX, actualY),
            size = Size(pixelSize * sizeMultiplier, pixelSize * sizeMultiplier)
        )
    }

    // --- LEGS & GREAVES ---
    if (animState == FighterAnimState.KNEELING_DEFEAT) {
        // Kneeling posture
        drawPixel(-2, 2, leatherColor, 2f)
        drawPixel(0, 3, steelColor, 2f)
        drawPixel(2, 4, leatherColor, 2f)
    } else {
        // Standing / Combat Stance Legs
        val legSpread = if (animState == FighterAnimState.ATTACK_LUNGE) 3 else 1
        // Back leg
        drawPixel(-legSpread - 1, 1, leatherColor)
        drawPixel(-legSpread - 1, 2, steelColor)
        drawPixel(-legSpread - 1, 3, leatherColor)
        // Front leg
        drawPixel(legSpread + 1, 1, leatherColor)
        drawPixel(legSpread + 1, 2, steelColor)
        drawPixel(legSpread + 1, 3, leatherColor)
    }

    // --- BALTEUS (BELT & TUNIC) ---
    for (gx in -2..2) {
        drawPixel(gx, 0, Color(0xFFD4AF37)) // Gold Balteus belt
    }
    for (gx in -2..2) {
        drawPixel(gx, -1, tunicColor) // Tunic
    }

    // --- TORSO & CHEST ARMOR ---
    for (gy in -5..-2) {
        for (gx in -2..2) {
            val chestColor = when (gladiatorClass) {
                GladiatorClass.RETIARIUS -> if (gx == -2) steelColor else skinColor // Retiarius Galerus shoulder guard
                GladiatorClass.MURMILLO -> if (gy == -4 || gy == -5) steelColor else tunicColor // Heavy Lorica
                GladiatorClass.SECUTOR -> steelColor
                GladiatorClass.THRAEX -> if (gx == -1 || gx == 1) steelColor else tunicColor
                GladiatorClass.DIMACHAERUS -> leatherColor // Light leather harness
            }
            drawPixel(gx, gy, chestColor)
        }
    }

    // Bloodied Bandages if Low HP (<35%)
    if (isLowHp) {
        drawPixel(0, -3, Color(0xFF8B0000))
        drawPixel(1, -4, Color(0xFFECEFF1))
    }

    // --- HEAD & CLASS-SPECIFIC HELMET ---
    val headCenterY = -8
    for (gy in -9..-6) {
        for (gx in -2..2) {
            when (gladiatorClass) {
                GladiatorClass.RETIARIUS -> {
                    // Unarmored head with gold headband
                    if (gy == -9) drawPixel(gx, gy, Color(0xFF4E342E))
                    else if (gy == -8) drawPixel(gx, gy, Color(0xFFD4AF37))
                    else drawPixel(gx, gy, skinColor)
                }
                GladiatorClass.MURMILLO -> {
                    // Broad-brimmed Murmillo Cassis with fish crest
                    if (gy == -9) drawPixel(gx, gy, helmetPlumeColor)
                    else if (gy == -8) drawPixel(gx, gy, steelColor)
                    else if (gx == 1 && gy == -7) drawPixel(gx, gy, Color(0xFF1A0C08)) // Visor slit
                    else drawPixel(gx, gy, steelColor)
                }
                GladiatorClass.THRAEX -> {
                    // Griffin crested helmet
                    if (gy == -9 && gx == 0) drawPixel(gx, gy, Color(0xFFFFD54F))
                    else if (gy == -8) drawPixel(gx, gy, steelColor)
                    else drawPixel(gx, gy, steelColor)
                }
                GladiatorClass.SECUTOR -> {
                    // Smooth round Secutor helmet with eyeholes
                    if (gy == -7 && (gx == 0 || gx == 1)) drawPixel(gx, gy, Color(0xFF140804))
                    else drawPixel(gx, gy, steelColor)
                }
                GladiatorClass.DIMACHAERUS -> {
                    // Light open visor helmet
                    if (gy == -9) drawPixel(gx, gy, leatherColor)
                    else if (gy == -8) drawPixel(gx, gy, steelColor)
                    else drawPixel(gx, gy, skinColor)
                }
            }
        }
    }

    // Murmillo Crest plumage feather
    if (gladiatorClass == GladiatorClass.MURMILLO) {
        drawPixel(0, -10, helmetPlumeColor)
        drawPixel(-1, -10, helmetPlumeColor)
    }

    // Sweat drops if exhausted (<20 Stamina)
    if (isExhausted) {
        drawPixel(3, -9, Color(0xFF81D4FA))
    }

    // --- LEFT ARM / SHIELD / NET ---
    val shieldX = if (animState == FighterAnimState.BLOCK_PARRY) 3 else 1
    when (gladiatorClass) {
        GladiatorClass.MURMILLO, GladiatorClass.SECUTOR -> {
            // Large Roman Scutum Shield
            val shieldColor = primaryColor
            val shieldBorder = Color(0xFFD4AF37)
            for (sy in -5..1) {
                for (sx in -1..1) {
                    val sColor = if (sx == -1 || sx == 1 || sy == -5 || sy == 1) shieldBorder else shieldColor
                    drawPixel(shieldX + sx, sy, sColor)
                }
            }
            // Golden shield boss (Umbo)
            drawPixel(shieldX, -2, Color(0xFFFFE082))
        }
        GladiatorClass.THRAEX -> {
            // Square Parmula Shield
            for (sy in -4..-1) {
                for (sx in 0..1) {
                    drawPixel(shieldX + sx, sy, Color(0xFFB71C1C))
                }
            }
            drawPixel(shieldX, -2, Color(0xFFFFD700))
        }
        GladiatorClass.RETIARIUS -> {
            // Retiarius weighted Net (Rete) in left hand
            drawPixel(shieldX + 1, -2, Color(0xFF8D6E63))
            drawPixel(shieldX + 2, -1, Color(0xFFBCAAA4))
            drawPixel(shieldX + 1, 0, Color(0xFF8D6E63))
            drawPixel(shieldX + 2, 1, Color(0xFF6D4C41))
        }
        GladiatorClass.DIMACHAERUS -> {
            // Dual Curved Dagger / Sica in off-hand
            drawPixel(shieldX + 1, -3, steelColor)
            drawPixel(shieldX + 2, -4, steelColor)
            drawPixel(shieldX + 1, -2, leatherColor)
        }
    }

    // --- RIGHT ARM & MAIN WEAPON ---
    val weaponArmY = if (animState == FighterAnimState.VICTORY_SALUTE) -9 else -4
    val weaponArmX = -3

    if (animState == FighterAnimState.VICTORY_SALUTE) {
        // Weapon raised high in triumph!
        drawPixel(-2, -7, skinColor)
        drawPixel(-2, -9, skinColor)
        for (wy in -14..-10) {
            drawPixel(-2, wy, steelColor)
        }
        drawPixel(-2, -9, Color(0xFFD4AF37)) // Guard
    } else if (animState == FighterAnimState.STRIKE || animState == FighterAnimState.ATTACK_LUNGE || animState == FighterAnimState.SLASH_SWING) {
        // Thrusting / Slashing forward horizontally!
        drawPixel(-2, weaponArmY, skinColor)
        drawPixel(0, weaponArmY, skinColor)
        drawPixel(2, weaponArmY, skinColor)

        // Weapon Blade
        when (gladiatorClass) {
            GladiatorClass.RETIARIUS -> {
                // Long 3-pronged Trident (Fuscina)
                for (wx in 3..9) {
                    drawPixel(wx, weaponArmY, Color(0xFF795548)) // Wooden shaft
                }
                drawPixel(10, weaponArmY - 1, steelColor) // Top prong
                drawPixel(11, weaponArmY, steelColor)     // Center prong
                drawPixel(10, weaponArmY + 1, steelColor) // Bottom prong
            }
            GladiatorClass.THRAEX -> {
                // Curved Sica blade
                for (wx in 3..6) drawPixel(wx, weaponArmY, steelColor)
                drawPixel(7, weaponArmY - 1, steelColor) // Hook curve
            }
            else -> {
                // Roman Gladius sword
                for (wx in 3..6) {
                    drawPixel(wx, weaponArmY, steelColor)
                }
                drawPixel(7, weaponArmY, Color(0xFFECEFF1)) // Sharp point
                drawPixel(3, weaponArmY - 1, Color(0xFFD4AF37))
                drawPixel(3, weaponArmY + 1, Color(0xFFD4AF37))
            }
        }
    } else {
        // Ready stance weapon
        drawPixel(weaponArmX, weaponArmY, skinColor)
        when (gladiatorClass) {
            GladiatorClass.RETIARIUS -> {
                for (i in 0..6) {
                    drawPixel(-2 + i, -2 - i, Color(0xFF795548))
                }
                drawPixel(5, -9, steelColor)
                drawPixel(4, -10, steelColor)
                drawPixel(6, -8, steelColor)
            }
            else -> {
                drawPixel(-2, -3, Color(0xFFD4AF37))
                drawPixel(-1, -4, steelColor)
                drawPixel(0, -5, steelColor)
                drawPixel(1, -6, Color(0xFFECEFF1))
            }
        }
    }
}

/**
 * Draws the Retiarius thrown weighted net
 */
private fun DrawScope.drawRetiariusNet(startX: Float, targetX: Float, groundY: Float) {
    val netX = (startX + targetX) / 2f
    val netY = groundY - 10.dp.toPx()
    val netSize = 28.dp.toPx()

    // Draw expandable net grid
    val netPath = Path().apply {
        moveTo(netX - netSize / 2, netY - netSize / 2)
        lineTo(netX + netSize / 2, netY - netSize / 2)
        lineTo(netX + netSize / 2, netY + netSize / 2)
        lineTo(netX - netSize / 2, netY + netSize / 2)
        close()
    }
    drawPath(
        path = netPath,
        color = Color(0xFF8D6E63).copy(alpha = 0.75f),
        style = Stroke(width = 2.dp.toPx())
    )

    // Net cross strings
    drawLine(
        color = Color(0xFFBCAAA4),
        start = Offset(netX - netSize / 2, netY),
        end = Offset(netX + netSize / 2, netY),
        strokeWidth = 1.5.dp.toPx()
    )
    drawLine(
        color = Color(0xFFBCAAA4),
        start = Offset(netX, netY - netSize / 2),
        end = Offset(netX, netY + netSize / 2),
        strokeWidth = 1.5.dp.toPx()
    )
}

/**
 * Draws clash impact rays and sparks when weapons strike
 */
private fun DrawScope.drawClashImpactEffect(
    x: Float,
    y: Float,
    isCrit: Boolean,
    gladiatorClass: GladiatorClass
) {
    val impactColor = if (isCrit) Color(0xFFFFD54F) else Color(0xFFFFF9C4)
    val burstRadius = if (isCrit) 26.dp.toPx() else 15.dp.toPx()

    // Central flash
    drawCircle(
        color = Color.White,
        radius = burstRadius * 0.4f,
        center = Offset(x, y)
    )

    // Clashing Spark Star Rays
    for (angle in 0 until 8) {
        val rad = Math.toRadians((angle * 45).toDouble())
        val ex = x + (cos(rad).toFloat() * burstRadius)
        val ey = y + (sin(rad).toFloat() * burstRadius)
        drawLine(
            color = impactColor,
            start = Offset(x, y),
            end = Offset(ex, ey),
            strokeWidth = if (isCrit) 3.5.dp.toPx() else 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Slash Arc Trail for curved weapons
    if (gladiatorClass == GladiatorClass.THRAEX || gladiatorClass == GladiatorClass.DIMACHAERUS) {
        drawArc(
            color = if (isCrit) Color(0xFFFF5252) else Color(0xFFFFD54F),
            startAngle = 180f,
            sweepAngle = 120f,
            useCenter = false,
            topLeft = Offset(x - burstRadius, y - burstRadius),
            size = Size(burstRadius * 2, burstRadius * 2),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
