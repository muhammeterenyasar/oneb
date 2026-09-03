package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArenaBloodSplatter
import com.example.model.CombatFighterState
import com.example.model.GladiatorClass
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Top-Down (Bird's-Eye View) Colosseum Arena Canvas
 * Renders the Roman amphitheater from directly above, complete with:
 * - Elliptical sand arena (Harena) with slave rake lines & central Roman laurel medallion
 * - Surrounding stone tiered stands (Cavea) and Imperial Box (Pulvinar)
 * - West Gate (Porta Triumphalis) & East Gate (Porta Libitinensis)
 * - Four cardinal burning bronze braziers with flickering firelight
 * - Real-time blood splatters on the sand
 * - Fully rotated 360° top-down gladiators with class-specific helmets, shields, weapons,
 *   attack slash arcs, defensive shield blocks, and stagger effects.
 */
@Composable
fun ArenaCanvas(
    playerState: CombatFighterState,
    opponentState: CombatFighterState,
    bloodSplatters: List<ArenaBloodSplatter> = emptyList(),
    arenaName: String = "Capua Amphitheatre",
    onSpriteSheetClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Ambient torchlight flicker animation
    val infiniteTransition = rememberInfiniteTransition(label = "TorchFlicker")
    val torchGlow by infiniteTransition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TorchGlow"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, RomanBronze, RoundedCornerShape(6.dp))
            .background(Color(0xFF140E0A), RoundedCornerShape(6.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            // 1. OUTER STONE SEATING & CAVEA TIERS (BIRD'S-EYE VIEW)
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF28201A), Color(0xFF140F0C)),
                    center = Offset(cx, cy),
                    radius = maxOf(w, h) * 0.7f
                ),
                size = size
            )

            // Radial stone spectator steps / tiers
            val tierSteps = 5
            for (i in 0 until tierSteps) {
                val insetX = w * (0.015f * i)
                val insetY = h * (0.02f * i)
                drawOval(
                    color = if (i % 2 == 0) Color(0xFF2E241D) else Color(0xFF221A15),
                    topLeft = Offset(insetX, insetY),
                    size = Size(w - insetX * 2, h - insetY * 2),
                    style = Stroke(width = 4.5f)
                )
            }

            // Radial seating partition dividers (every 30 degrees)
            for (angleDeg in 0 until 360 step 30) {
                val rad = Math.toRadians(angleDeg.toDouble())
                val outerX = cx + (w * 0.48f) * cos(rad).toFloat()
                val outerY = cy + (h * 0.48f) * sin(rad).toFloat()
                val innerX = cx + (w * 0.42f) * cos(rad).toFloat()
                val innerY = cy + (h * 0.42f) * sin(rad).toFloat()
                drawLine(
                    color = Color(0xFF1B140F),
                    start = Offset(innerX, innerY),
                    end = Offset(outerX, outerY),
                    strokeWidth = 2f
                )
            }

            // 2. PODIUM WALL & PERIMETER SAFETY BARRIER
            val arenaMarginX = w * 0.08f
            val arenaMarginY = h * 0.10f
            val arenaW = w - arenaMarginX * 2
            val arenaH = h - arenaMarginY * 2

            // High podium stone border (dark drop shadow onto sand)
            drawOval(
                color = Color(0xFF0F0B08),
                topLeft = Offset(arenaMarginX - 4f, arenaMarginY - 3f),
                size = Size(arenaW + 8f, arenaH + 6f)
            )

            // 3. ARENA SAND FLOOR (HARENA)
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFB58F60), // Sunlit center sand
                        Color(0xFF997346), // Mid sand
                        Color(0xFF6B4D2B), // Shadowed sand near perimeter podium
                    ),
                    center = Offset(cx, cy),
                    radius = arenaW * 0.55f
                ),
                topLeft = Offset(arenaMarginX, arenaMarginY),
                size = Size(arenaW, arenaH)
            )

            // Sand rake concentric tracks (concentric circular grooves left by arena rakers)
            for (r in listOf(0.20f, 0.35f, 0.48f, 0.62f, 0.75f)) {
                val grooveW = arenaW * r
                val grooveH = arenaH * r
                drawOval(
                    color = Color(0xFF86623A).copy(alpha = 0.35f),
                    topLeft = Offset(cx - grooveW / 2f, cy - grooveH / 2f),
                    size = Size(grooveW, grooveH),
                    style = Stroke(width = 1.2f)
                )
            }

            // Central Inscribed Roman Combat Ring & Laurel Emblem
            val centerRingW = arenaW * 0.28f
            val centerRingH = arenaH * 0.28f
            drawOval(
                color = Color(0xFF7A5832).copy(alpha = 0.45f),
                topLeft = Offset(cx - centerRingW / 2f, cy - centerRingH / 2f),
                size = Size(centerRingW, centerRingH),
                style = Stroke(width = 2.5f)
            )
            // Laurel leaf cross lines in center
            drawLine(
                color = Color(0xFF7A5832).copy(alpha = 0.35f),
                start = Offset(cx - centerRingW * 0.4f, cy),
                end = Offset(cx + centerRingW * 0.4f, cy),
                strokeWidth = 1.5f
            )
            drawLine(
                color = Color(0xFF7A5832).copy(alpha = 0.35f),
                start = Offset(cx, cy - centerRingH * 0.4f),
                end = Offset(cx, cy + centerRingH * 0.4f),
                strokeWidth = 1.5f
            )

            // Palisade bronze safety spikes rim around podium wall
            drawOval(
                color = RomanBronze,
                topLeft = Offset(arenaMarginX, arenaMarginY),
                size = Size(arenaW, arenaH),
                style = Stroke(width = 3.5f)
            )

            // 4. ARCHITECTURAL GATES & IMPERIAL BOX (TOP-DOWN)
            // A) Imperial Box (Pulvinar) at Top Rim (Editor & Vestal Virgins VIP seating)
            drawRoundRect(
                color = RomanCrimson,
                topLeft = Offset(cx - 36f, arenaMarginY - 14f),
                size = Size(72f, 16f),
                cornerRadius = CornerRadius(3f, 3f)
            )
            drawRect(
                color = RomanGold,
                topLeft = Offset(cx - 32f, arenaMarginY - 2f),
                size = Size(64f, 3f)
            )
            // Golden laurel crown emblem at center of imperial box
            drawCircle(
                color = RomanGold,
                radius = 3.5f,
                center = Offset(cx, arenaMarginY - 6f)
            )

            // B) West Gate: Porta Triumphalis (Gladiators enter)
            drawRect(
                color = Color(0xFF1E1712),
                topLeft = Offset(arenaMarginX - 10f, cy - 24f),
                size = Size(14f, 48f)
            )
            // Iron portcullis grating lines
            for (step in 0..4) {
                drawLine(
                    color = RomanBronzeDark,
                    start = Offset(arenaMarginX - 10f, cy - 20f + step * 10f),
                    end = Offset(arenaMarginX + 4f, cy - 20f + step * 10f),
                    strokeWidth = 2f
                )
            }

            // C) East Gate: Porta Libitinensis (Gate of the Dead)
            drawRect(
                color = Color(0xFF16100B),
                topLeft = Offset(arenaMarginX + arenaW - 4f, cy - 24f),
                size = Size(14f, 48f)
            )
            // Iron chains / skull mortuary marker
            drawCircle(
                color = RomanDangerRed.copy(alpha = 0.7f),
                radius = 4f,
                center = Offset(arenaMarginX + arenaW + 3f, cy)
            )

            // 5. FOUR CARDINAL BRAZIERS (NORTH, SOUTH, EAST, WEST)
            val brazierLocations = listOf(
                Offset(cx, arenaMarginY - 2f),                 // North
                Offset(cx, arenaMarginY + arenaH + 2f),        // South
                Offset(arenaMarginX - 2f, cy),                 // West
                Offset(arenaMarginX + arenaW + 2f, cy)         // East
            )
            brazierLocations.forEach { bPos ->
                // Firelight glow onto sand
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFB703).copy(alpha = 0.45f * torchGlow),
                            Color(0xFFFB8500).copy(alpha = 0.22f * torchGlow),
                            Color.Transparent
                        ),
                        center = bPos,
                        radius = 38f * torchGlow
                    ),
                    radius = 38f * torchGlow,
                    center = bPos
                )
                // Bronze bowl
                drawCircle(color = RomanBronzeDark, radius = 7f, center = bPos)
                drawCircle(color = RomanGoldDark, radius = 5.5f, center = bPos, style = Stroke(1.5f))
                // Fiery burning coals
                drawCircle(color = Color(0xFFD90429), radius = 4f, center = bPos)
                drawCircle(color = Color(0xFFFFD166), radius = 2f, center = bPos)
            }

            // 6. ACCUMULATED ARENA BLOOD SPLATTERS (ON THE SAND)
            bloodSplatters.forEach { splatter ->
                val sx = arenaMarginX + (splatter.x * arenaW)
                val sy = arenaMarginY + (splatter.y * arenaH)
                // Coagulated dark blood pool
                drawCircle(
                    color = Color(0xFF580C1F).copy(alpha = splatter.alpha),
                    radius = splatter.radius,
                    center = Offset(sx, sy)
                )
                // Fresh bright crimson core
                drawCircle(
                    color = Color(0xFF9E2A2B).copy(alpha = splatter.alpha * 0.85f),
                    radius = splatter.radius * 0.55f,
                    center = Offset(sx + 1f, sy + 1f)
                )
                // Minor satellite droplets around splatter
                drawCircle(
                    color = Color(0xFF580C1F).copy(alpha = splatter.alpha * 0.7f),
                    radius = splatter.radius * 0.25f,
                    center = Offset(sx - splatter.radius * 0.8f, sy + splatter.radius * 0.6f)
                )
            }

            // Extra blood pool if any fighter is bleeding heavily
            if (playerState.bloodLoss > 0.5f) {
                val px = arenaMarginX + (playerState.posX * arenaW)
                val py = arenaMarginY + (playerState.posY * arenaH)
                drawCircle(
                    color = Color(0xFF720026).copy(alpha = 0.5f),
                    radius = 12f,
                    center = Offset(px, py + 8f)
                )
            }
            if (opponentState.bloodLoss > 0.5f) {
                val ox = arenaMarginX + (opponentState.posX * arenaW)
                val oy = arenaMarginY + (opponentState.posY * arenaH)
                drawCircle(
                    color = Color(0xFF720026).copy(alpha = 0.5f),
                    radius = 12f,
                    center = Offset(ox, oy + 8f)
                )
            }

            // 7. RENDER PLAYER GLADIATOR (TOP-DOWN 360°)
            val playerPixelX = arenaMarginX + (playerState.posX * arenaW)
            val playerPixelY = arenaMarginY + (playerState.posY * arenaH)
            drawTopDownGladiator(
                fighter = playerState,
                isPlayer = true,
                x = playerPixelX,
                y = playerPixelY
            )

            // 8. RENDER OPPONENT GLADIATOR (TOP-DOWN 360°)
            val opponentPixelX = arenaMarginX + (opponentState.posX * arenaW)
            val opponentPixelY = arenaMarginY + (opponentState.posY * arenaH)
            drawTopDownGladiator(
                fighter = opponentState,
                isPlayer = false,
                x = opponentPixelX,
                y = opponentPixelY
            )

            // 9. TOP-DOWN COMBAT DISTANCE LINE & CLASH POINT
            if (!playerState.isDown && !opponentState.isDown) {
                // Dynamic tactical distance line
                drawLine(
                    color = RomanGold.copy(alpha = 0.18f),
                    start = Offset(playerPixelX, playerPixelY),
                    end = Offset(opponentPixelX, opponentPixelY),
                    strokeWidth = 1.2f
                )
            }
        }

        // Top-Down Mode Badge in the corner
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(6.dp)
                .background(Color(0xFF1E1712).copy(alpha = 0.85f), RoundedCornerShape(3.dp))
                .border(0.6.dp, RomanBronze, RoundedCornerShape(3.dp))
                .then(
                    if (onSpriteSheetClick != null) Modifier.clickable { onSpriteSheetClick() }
                    else Modifier
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (onSpriteSheetClick != null) "⚔ ARENA KUŞBAKIŞI • SPRITE TABLOSU 🔍" else "⚔ ARENA KUŞBAKIŞI (TOP-DOWN)",
                color = RomanGold,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Procedural Top-Down Gladiator Renderer
 * Renders a bird's-eye view of a gladiator, rotated 360° to match dynamic facingAngle.
 * Renders shoulders, arms, class-specific helmet crests, shields, and weapons from above.
 */
private fun DrawScope.drawTopDownGladiator(
    fighter: CombatFighterState,
    isPlayer: Boolean,
    x: Float,
    y: Float
) {
    val gladClass = fighter.gladiator.gladiatorClass
    val isDown = fighter.isDown

    // A) Cast shadow on the sand
    drawOval(
        color = Color(0xFF1B140F).copy(alpha = 0.55f),
        topLeft = Offset(x - 20f, y - 14f),
        size = Size(40f, 28f)
    )

    // B) If knocked down / defeated on the sand
    if (isDown) {
        // Prone silhouette sprawled flat
        drawOval(
            color = if (isPlayer) Color(0xFF6B1D1D) else Color(0xFF1F2937),
            topLeft = Offset(x - 24f, y - 10f),
            size = Size(48f, 20f)
        )
        // Fallen helmet lying next to fighter
        drawCircle(
            color = RomanGold,
            radius = 8f,
            center = Offset(x + 18f, y - 8f)
        )
        // Dropped shield in the sand
        drawOval(
            color = if (isPlayer) Color(0xFFC59B27) else Color(0xFF8C6D3B),
            topLeft = Offset(x - 28f, y + 6f),
            size = Size(18f, 12f)
        )
        // Red critical defeat flash
        drawCircle(
            color = RomanDangerRed.copy(alpha = 0.8f),
            radius = 16f,
            center = Offset(x, y)
        )
        return
    }

    // C) ACTIVE FIGHTER (STANDING & CLASHING)
    // Wrap drawing with dynamic rotation around fighter's center (x, y)
    // The model is authored facing East (0 degrees).
    withTransform({
        rotate(degrees = fighter.facingAngle, pivot = Offset(x, y))
    }) {
        val tunicColor = if (isPlayer) Color(0xFF8B1E1E) else Color(0xFF1E3A5F)
        val skinColor = Color(0xFFC68B59)
        val bronzeColor = Color(0xFFD4AF37)
        val ironColor = Color(0xFFE2E8F0)

        // 1. SHOULDERS & TORSO (BIRD'S-EYE VIEW)
        // Broad muscular shoulders spanning from (y - 16) to (y + 16)
        drawOval(
            color = tunicColor,
            topLeft = Offset(x - 9f, y - 16f),
            size = Size(18f, 32f)
        )
        // Leather chest harness / balteus strap diagonally across chest
        drawLine(
            color = Color(0xFF3E2723),
            start = Offset(x - 6f, y - 14f),
            end = Offset(x + 4f, y + 14f),
            strokeWidth = 3f
        )
        // Bronze buckle at harness center
        drawCircle(color = bronzeColor, radius = 2.5f, center = Offset(x - 1f, y))

        // 2. LEFT ARM & SHIELD (TOP-DOWN)
        // Left arm extends along the top side (y - 14f)
        drawOval(
            color = skinColor,
            topLeft = Offset(x - 2f, y - 18f),
            size = Size(12f, 7f)
        )

        val shieldForwardOffset = if (fighter.isBlocking) 14f else 8f
        val shieldCenterY = y - (if (fighter.isBlocking) 4f else 16f)

        when (gladClass) {
            GladiatorClass.MURMILLO, GladiatorClass.SECUTOR -> {
                // Curved rectangular Scutum shield seen from above
                val shieldColor = if (isPlayer) Color(0xFFC59B27) else Color(0xFF8C6D3B)
                drawRoundRect(
                    color = shieldColor,
                    topLeft = Offset(x + shieldForwardOffset, shieldCenterY - 11f),
                    size = Size(7f, 22f),
                    cornerRadius = CornerRadius(2.5f, 2.5f)
                )
                // Golden bronze umbo (boss) on the shield face
                drawCircle(
                    color = Color(0xFFFFD166),
                    radius = 3.5f,
                    center = Offset(x + shieldForwardOffset + 3.5f, shieldCenterY)
                )
            }
            GladiatorClass.THRAEX -> {
                // Small square Parma shield
                drawRoundRect(
                    color = Color(0xFF9E2A2B),
                    topLeft = Offset(x + shieldForwardOffset, shieldCenterY - 8f),
                    size = Size(6f, 16f),
                    cornerRadius = CornerRadius(1.5f, 1.5f)
                )
                drawCircle(color = bronzeColor, radius = 2.5f, center = Offset(x + shieldForwardOffset + 3f, shieldCenterY))
            }
            GladiatorClass.HOPLOMACHUS -> {
                // Circular bronze Hoplon shield
                drawOval(
                    color = bronzeColor,
                    topLeft = Offset(x + shieldForwardOffset - 2f, shieldCenterY - 9f),
                    size = Size(8f, 18f)
                )
                drawCircle(color = Color(0xFF332211), radius = 2.5f, center = Offset(x + shieldForwardOffset + 2f, shieldCenterY))
            }
            GladiatorClass.RETIARIUS -> {
                // Weighted Net (Rete) coiled in left hand or thrown out
                if (fighter.isAttacking) {
                    // Net thrown forward toward opponent!
                    val netPath = Path().apply {
                        moveTo(x + 10f, y - 14f)
                        lineTo(x + 36f, y - 26f)
                        lineTo(x + 42f, y - 4f)
                        lineTo(x + 32f, y + 16f)
                        close()
                    }
                    drawPath(
                        path = netPath,
                        color = Color(0xFFD4A373).copy(alpha = 0.55f),
                        style = Stroke(width = 1.5f)
                    )
                    // Lead weights at net perimeter
                    drawCircle(color = Color(0xFF4A4E69), radius = 2.5f, center = Offset(x + 36f, y - 26f))
                    drawCircle(color = Color(0xFF4A4E69), radius = 2.5f, center = Offset(x + 42f, y - 4f))
                    drawCircle(color = Color(0xFF4A4E69), radius = 2.5f, center = Offset(x + 32f, y + 16f))
                } else {
                    // Net bundled in hand
                    drawCircle(
                        color = Color(0xFFD4A373),
                        radius = 6f,
                        center = Offset(x + 6f, y - 14f),
                        style = Stroke(1.5f)
                    )
                }
            }
            GladiatorClass.DIMACHAERUS -> {
                // Second Gladius dagger in left hand
                drawLine(
                    color = ironColor,
                    start = Offset(x + 6f, y - 14f),
                    end = Offset(x + 22f, y - 16f),
                    strokeWidth = 2.5f
                )
            }
            GladiatorClass.BESTIARIUS -> {
                // Animal hide wrap guard
                drawOval(
                    color = Color(0xFF7F5539),
                    topLeft = Offset(x + shieldForwardOffset, shieldCenterY - 7f),
                    size = Size(6f, 14f)
                )
            }
            GladiatorClass.HEAVY_VETERAN -> {
                // Large tower scutum shield
                drawRoundRect(
                    color = Color(0xFF8B0000),
                    topLeft = Offset(x + shieldForwardOffset, shieldCenterY - 12f),
                    size = Size(8f, 24f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }
            GladiatorClass.LIGHT_DUELIST -> {
                // Small parrying buckler
                drawCircle(
                    color = Color(0xFFC59B27),
                    radius = 4f,
                    center = Offset(x + shieldForwardOffset + 2f, shieldCenterY)
                )
            }
            GladiatorClass.SPECIAL_ARCHETYPE -> {
                // Bronze buckler
                drawCircle(
                    color = Color(0xFFCD7F32),
                    radius = 5f,
                    center = Offset(x + shieldForwardOffset + 2f, shieldCenterY)
                )
            }
        }

        // Defensive Shield Block Glow/Sparks
        if (fighter.isBlocking) {
            drawArc(
                color = Color(0xFF60A5FA).copy(alpha = 0.75f),
                startAngle = -45f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(x + 10f, y - 18f),
                size = Size(18f, 36f),
                style = Stroke(width = 3.5f)
            )
        }

        // 3. RIGHT ARM & WEAPON (TOP-DOWN)
        // Right arm extends along the bottom side (y + 14f)
        val armSegmentLength = if (fighter.isAttacking) 16f else 10f
        drawOval(
            color = skinColor,
            topLeft = Offset(x, y + 11f),
            size = Size(armSegmentLength, 7f)
        )
        // Segmented bronze arm guard (manica) on the weapon arm
        drawRoundRect(
            color = bronzeColor,
            topLeft = Offset(x - 2f, y + 11f),
            size = Size(8f, 7f),
            cornerRadius = CornerRadius(1f, 1f)
        )

        // Weapon Blade
        val weaponStartX = x + armSegmentLength - 2f
        val weaponStartY = y + 14f
        val attackLunge = if (fighter.isAttacking) 26f else 12f
        val weaponEndX = weaponStartX + attackLunge

        when (gladClass) {
            GladiatorClass.RETIARIUS -> {
                // Long Trident (Fuscina)
                val shaftEnd = weaponStartX + (if (fighter.isAttacking) 36f else 22f)
                // Wooden shaft
                drawLine(
                    color = Color(0xFF8D6E63),
                    start = Offset(weaponStartX - 6f, weaponStartY),
                    end = Offset(shaftEnd, weaponStartY),
                    strokeWidth = 2.5f
                )
                // Triple barbed steel prongs
                drawLine(color = ironColor, start = Offset(shaftEnd, weaponStartY - 5f), end = Offset(shaftEnd + 8f, weaponStartY - 5f), strokeWidth = 2f)
                drawLine(color = ironColor, start = Offset(shaftEnd, weaponStartY), end = Offset(shaftEnd + 10f, weaponStartY), strokeWidth = 2.5f)
                drawLine(color = ironColor, start = Offset(shaftEnd, weaponStartY + 5f), end = Offset(shaftEnd + 8f, weaponStartY + 5f), strokeWidth = 2f)
                drawLine(color = ironColor, start = Offset(shaftEnd, weaponStartY - 5f), end = Offset(shaftEnd, weaponStartY + 5f), strokeWidth = 2f)
            }
            GladiatorClass.HOPLOMACHUS, GladiatorClass.BESTIARIUS -> {
                // Thrusting Spear (Hasta / Venabulum)
                val spearLength = if (fighter.isAttacking) 42f else 26f
                drawLine(
                    color = Color(0xFF795548),
                    start = Offset(weaponStartX - 6f, weaponStartY),
                    end = Offset(weaponStartX + spearLength, weaponStartY),
                    strokeWidth = 2.5f
                )
                // Bronze leaf blade head
                drawLine(
                    color = bronzeColor,
                    start = Offset(weaponStartX + spearLength, weaponStartY),
                    end = Offset(weaponStartX + spearLength + 8f, weaponStartY),
                    strokeWidth = 4f
                )
            }
            GladiatorClass.THRAEX -> {
                // Curved Sica Blade
                drawLine(
                    color = ironColor,
                    start = Offset(weaponStartX, weaponStartY),
                    end = Offset(weaponEndX, weaponStartY + 4f),
                    strokeWidth = 3f
                )
                // Forward curved sickle tip
                drawLine(
                    color = ironColor,
                    start = Offset(weaponEndX, weaponStartY + 4f),
                    end = Offset(weaponEndX + 6f, weaponStartY - 3f),
                    strokeWidth = 2.5f
                )
            }
            else -> {
                // Roman Gladius straight double-edged blade
                drawLine(
                    color = ironColor,
                    start = Offset(weaponStartX, weaponStartY),
                    end = Offset(weaponEndX, weaponStartY),
                    strokeWidth = 3.5f
                )
                // Bronze pommel & crossguard
                drawCircle(color = bronzeColor, radius = 2f, center = Offset(weaponStartX, weaponStartY))
                drawLine(
                    color = bronzeColor,
                    start = Offset(weaponStartX + 2f, weaponStartY - 3f),
                    end = Offset(weaponStartX + 2f, weaponStartY + 3f),
                    strokeWidth = 2f
                )
            }
        }

        // ATTACK STRIKE TRAIL / SHOCKWAVE
        if (fighter.isAttacking) {
            // Golden slash whoosh arc
            drawArc(
                color = Color(0xFFFFD166).copy(alpha = 0.85f),
                startAngle = -35f,
                sweepAngle = 70f,
                useCenter = false,
                topLeft = Offset(weaponEndX - 8f, y - 18f),
                size = Size(26f, 36f),
                style = Stroke(width = 3.5f)
            )
            // Strike impact flash spark
            drawCircle(
                color = Color(0xFFFFF275),
                radius = 7f,
                center = Offset(weaponEndX + 6f, weaponStartY)
            )
        }

        // 4. HEAD & DISTINCT CLASS HELMET FROM ABOVE (TOP-DOWN VIEW)
        // Center of the fighter is (x, y)
        when (gladClass) {
            GladiatorClass.MURMILLO -> {
                // Bronze round helmet dome
                drawCircle(color = bronzeColor, radius = 9f, center = Offset(x, y))
                // Large iconic fish-fin crest running along the spine/center of helmet
                drawLine(
                    color = Color(0xFFFFD166),
                    start = Offset(x - 11f, y),
                    end = Offset(x + 11f, y),
                    strokeWidth = 4.5f
                )
                // Dark eye aperture visor brim
                drawArc(
                    color = Color(0xFF1E1712),
                    startAngle = -60f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(x + 4f, y - 7f),
                    size = Size(6f, 14f),
                    style = Stroke(2f)
                )
            }
            GladiatorClass.THRAEX -> {
                // Griffin crest with red/bronze plumage wings
                drawCircle(color = bronzeColor, radius = 8.5f, center = Offset(x, y))
                // Griffin curved ridge
                drawLine(
                    color = Color(0xFFE63946),
                    start = Offset(x - 9f, y),
                    end = Offset(x + 10f, y),
                    strokeWidth = 3f
                )
                // Side plumage feathers
                drawLine(color = Color(0xFFE63946), start = Offset(x - 2f, y - 8f), end = Offset(x - 6f, y - 12f), strokeWidth = 2f)
                drawLine(color = Color(0xFFE63946), start = Offset(x - 2f, y + 8f), end = Offset(x - 6f, y + 12f), strokeWidth = 2f)
            }
            GladiatorClass.RETIARIUS -> {
                // Unhelmeted bare head with dark curly hair
                drawCircle(color = Color(0xFF2B1D0F), radius = 8.5f, center = Offset(x, y))
                // White/gold cloth headband
                drawArc(
                    color = Color(0xFFF1F5F9),
                    startAngle = 120f,
                    sweepAngle = 120f,
                    useCenter = false,
                    topLeft = Offset(x - 8f, y - 8f),
                    size = Size(16f, 16f),
                    style = Stroke(2f)
                )
                // Large bronze Galerus shoulder shield on left shoulder (y - 14f)
                drawRoundRect(
                    color = bronzeColor,
                    topLeft = Offset(x - 6f, y - 18f),
                    size = Size(10f, 6f),
                    cornerRadius = CornerRadius(2f, 2f)
                )
            }
            GladiatorClass.SECUTOR -> {
                // Smooth aerodynamic egg-shaped helmet with no crest (to avoid net entanglement)
                drawOval(
                    color = bronzeColor,
                    topLeft = Offset(x - 9f, y - 7.5f),
                    size = Size(19f, 15f)
                )
                // Two small round eye-hole apertures
                drawCircle(color = Color(0xFF16100B), radius = 1.5f, center = Offset(x + 7f, y - 2.5f))
                drawCircle(color = Color(0xFF16100B), radius = 1.5f, center = Offset(x + 7f, y + 2.5f))
            }
            GladiatorClass.HOPLOMACHUS -> {
                // Visored helmet with twin tall side plumes
                drawCircle(color = bronzeColor, radius = 8.5f, center = Offset(x, y))
                drawCircle(color = Color(0xFFFFD166), radius = 3.5f, center = Offset(x, y))
                // Side black horsehair plumes
                drawLine(color = Color(0xFF1A1A1A), start = Offset(x, y - 7f), end = Offset(x - 4f, y - 13f), strokeWidth = 2.5f)
                drawLine(color = Color(0xFF1A1A1A), start = Offset(x, y + 7f), end = Offset(x - 4f, y + 13f), strokeWidth = 2.5f)
            }
            GladiatorClass.DIMACHAERUS -> {
                // Leather hood cowl and twin sheaths on back
                drawCircle(color = Color(0xFF3E2723), radius = 8.5f, center = Offset(x, y))
                // Crossed sheath straps
                drawLine(color = Color(0xFF1B140F), start = Offset(x - 8f, y - 6f), end = Offset(x - 2f, y + 6f), strokeWidth = 2f)
                drawLine(color = Color(0xFF1B140F), start = Offset(x - 8f, y + 6f), end = Offset(x - 2f, y - 6f), strokeWidth = 2f)
            }
            GladiatorClass.BESTIARIUS -> {
                // Spotted pelt draped over head & shoulders
                drawCircle(color = Color(0xFFD4A373), radius = 8.5f, center = Offset(x, y))
                // Spots on pelt
                drawCircle(color = Color(0xFF4A2810), radius = 1.5f, center = Offset(x - 3f, y - 3f))
                drawCircle(color = Color(0xFF4A2810), radius = 1.5f, center = Offset(x - 2f, y + 3f))
            }
            GladiatorClass.HEAVY_VETERAN -> {
                // Iron segmented visor helmet
                drawCircle(color = Color(0xFF4A5568), radius = 9f, center = Offset(x, y))
                drawLine(color = Color(0xFFE2E8F0), start = Offset(x - 9f, y), end = Offset(x + 9f, y), strokeWidth = 3f)
            }
            GladiatorClass.LIGHT_DUELIST -> {
                // Open bronze headband with feather
                drawCircle(color = Color(0xFFD4A373), radius = 8f, center = Offset(x, y))
                drawCircle(color = Color(0xFFC59B27), radius = 8.5f, center = Offset(x, y), style = Stroke(1.5f))
            }
            GladiatorClass.SPECIAL_ARCHETYPE -> {
                // Exotic crested mask
                drawCircle(color = Color(0xFFB45309), radius = 9f, center = Offset(x, y))
                drawLine(color = Color(0xFFF59E0B), start = Offset(x - 8f, y - 2f), end = Offset(x + 8f, y - 2f), strokeWidth = 2.5f)
            }
        }

        // 5. STAGGER / HIT RECOIL FLASH EFFECT
        if (fighter.isStaggered) {
            drawCircle(
                color = RomanDangerRed.copy(alpha = 0.75f),
                radius = 22f,
                center = Offset(x, y)
            )
        }
    }

    // D) OVERHEAD TOP-DOWN STATUS ARCS (NOT ROTATED - ALWAYS CLEAR TO PLAYER)
    val ringRadius = 24f
    // Health Arc (Behind fighter)
    val healthPct = (fighter.currentHealth / 100f).coerceIn(0f, 1f)
    drawArc(
        color = Color(0xFF1A120C),
        startAngle = 135f,
        sweepAngle = 270f,
        useCenter = false,
        topLeft = Offset(x - ringRadius, y - ringRadius),
        size = Size(ringRadius * 2, ringRadius * 2),
        style = Stroke(width = 3.5f)
    )
    drawArc(
        color = if (healthPct > 0.4f) RomanSuccessGreen else RomanDangerRed,
        startAngle = 135f,
        sweepAngle = 270f * healthPct,
        useCenter = false,
        topLeft = Offset(x - ringRadius, y - ringRadius),
        size = Size(ringRadius * 2, ringRadius * 2),
        style = Stroke(width = 3.5f)
    )

    // Stamina Arc (Inner ring)
    val stamRadius = 19f
    val stamPct = (fighter.currentStamina / 100f).coerceIn(0f, 1f)
    drawArc(
        color = RomanStaminaCyan.copy(alpha = 0.85f),
        startAngle = 135f,
        sweepAngle = 270f * stamPct,
        useCenter = false,
        topLeft = Offset(x - stamRadius, y - stamRadius),
        size = Size(stamRadius * 2, stamRadius * 2),
        style = Stroke(width = 2.2f)
    )
}

