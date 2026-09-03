package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TacticalCommand
import com.example.simulation.LudusUiState
import com.example.ui.components.ArenaCanvas
import com.example.ui.components.GladiatorAvatarCanvas
import com.example.ui.components.GladiatorMiniSprite
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.components.TopDownSpriteSheetDialog
import com.example.ui.theme.*

@Composable
fun LiveCombatScreen(
    state: LudusUiState,
    onSendCommand: (TacticalCommand) -> Unit,
    onTogglePause: () -> Unit,
    onSetSpeed: (Float) -> Unit,
    onDecidePolliceVerso: (Boolean, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val engine = state.activeCombatEngine ?: return
    val playerFighter = engine.playerState
    val opponentFighter = engine.opponentState
    val logListState = rememberLazyListState()
    var showSpriteSheetDialog by remember { mutableStateOf(false) }

    // Auto-scroll combat logs
    LaunchedEffect(engine.logs.size) {
        if (engine.logs.isNotEmpty()) {
            logListState.animateScrollToItem(engine.logs.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF16120F))
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 1. COMBAT TOP BAR: Timer, Crowd gauges, Speed controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .background(Color(0xFF221A15), RoundedCornerShape(3.dp))
                    .border(0.8.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Arena & Timer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = engine.arenaName.uppercase(),
                        color = RomanGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "SÜRE: %02d:%02d".format(engine.tick / 60, engine.tick % 60),
                        color = RomanParchment,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Crowd meters
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "Kalabalık Coşkusu:", color = RomanTextSecondary, fontSize = 10.sp)
                        Text(text = "%${engine.crowdExcitement}", color = RomanGoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "Kan Arzusu:", color = RomanTextSecondary, fontSize = 10.sp)
                        Text(text = "%${engine.crowdBloodlust}", color = RomanDangerRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Play / Pause & Speed controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onTogglePause,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = if (state.isCombatPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause/Play",
                            tint = RomanGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                if (state.combatSpeedMultiplier == 1.0f) RomanCrimson else RomanBronzeDark,
                                RoundedCornerShape(2.dp)
                            )
                            .clickable { onSetSpeed(1.0f) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "1x", color = RomanParchment, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                if (state.combatSpeedMultiplier == 2.0f) RomanCrimson else RomanBronzeDark,
                                RoundedCornerShape(2.dp)
                            )
                            .clickable { onSetSpeed(2.0f) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "2x", color = RomanParchment, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Inspect Top-Down Sprite Sheet Button
                    Surface(
                        modifier = Modifier.clickable { showSpriteSheetDialog = true },
                        shape = RoundedCornerShape(2.dp),
                        color = RomanGoldDark.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(0.6.dp, RomanGold)
                    ) {
                        Text(
                            text = "⚔ SPRITE TABLOSU",
                            color = RomanGoldLight,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // 2. MAIN COMBAT VIEW: Player Status | Arena Canvas | Opponent Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Left Column: Player Gladiator Condition
                Column(
                    modifier = Modifier
                        .width(135.dp)
                        .fillMaxHeight()
                ) {
                    RomanCard(
                        title = playerFighter.gladiator.name,
                        badge = "Bizimki",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GladiatorMiniSprite(playerFighter.gladiator.gladiatorClass, 36.dp)
                            Column {
                                Text(
                                    text = playerFighter.gladiator.gladiatorClass.title,
                                    color = RomanParchmentDark,
                                    fontSize = 9.sp
                                )
                                Text(
                                    text = "Vuruş: ${engine.playerHits}",
                                    color = RomanGold,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        RomanStatBar("Sağlık", playerFighter.currentHealth.toInt(), 100, RomanSuccessGreen)
                        RomanStatBar("Dayanıklılık", playerFighter.currentStamina.toInt(), 100, RomanStaminaCyan)
                        if (playerFighter.bloodLoss > 0) {
                            Text(
                                text = "Kan Kaybı: -${"%.1f".format(playerFighter.bloodLoss)}/sn",
                                color = RomanDangerRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (playerFighter.isBlocking) {
                            Text(text = "🛡 Kalkan Arkasında", color = Color(0xFF60A5FA), fontSize = 9.sp)
                        }
                    }
                }

                // Center Column: Top-Down Arena Canvas + Live Play-by-Play Combat Log
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ArenaCanvas(
                        playerState = playerFighter,
                        opponentState = opponentFighter,
                        bloodSplatters = engine.bloodSplatters,
                        arenaName = engine.arenaName,
                        onSpriteSheetClick = { showSpriteSheetDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.35f)
                    )

                    // Text-based Play-by-play Combat Log (FM Style)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.65f)
                            .background(Color(0xFF140F0C), RoundedCornerShape(3.dp))
                            .border(0.6.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                            .padding(4.dp)
                    ) {
                        LazyColumn(
                            state = logListState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(engine.logs) { log ->
                                val textColor = when {
                                    log.bloodEffect -> RomanDangerRed
                                    log.isCritical -> RomanGoldLight
                                    log.isPlayerAction -> RomanParchment
                                    else -> RomanTextSecondary
                                }
                                Text(
                                    text = "[%02d:%02d] %s".format(log.timeSeconds / 60, log.timeSeconds % 60, log.text),
                                    color = textColor,
                                    fontSize = 9.5.sp,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }

                // Right Column: Opponent Gladiator Condition
                Column(
                    modifier = Modifier
                        .width(135.dp)
                        .fillMaxHeight()
                ) {
                    RomanCard(
                        title = opponentFighter.gladiator.name,
                        badge = "Rakip",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GladiatorMiniSprite(opponentFighter.gladiator.gladiatorClass, 36.dp)
                            Column {
                                Text(
                                    text = opponentFighter.gladiator.gladiatorClass.title,
                                    color = RomanParchmentDark,
                                    fontSize = 9.sp
                                )
                                Text(
                                    text = "Domus Auctor",
                                    color = RomanDangerRed,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        RomanStatBar("Sağlık", opponentFighter.currentHealth.toInt(), 100, RomanDangerRed)
                        RomanStatBar("Dayanıklılık", opponentFighter.currentStamina.toInt(), 100, RomanStaminaCyan)
                        if (opponentFighter.bloodLoss > 0) {
                            Text(
                                text = "Kan Kaybı: -${"%.1f".format(opponentFighter.bloodLoss)}/sn",
                                color = RomanDangerRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (opponentFighter.isDown) {
                            Text(text = "☠ DÜŞTÜ / YENİLDİ", color = RomanDangerRed, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            // 3. TACTICAL COMMAND BAR (Interactive Lanista Orders)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(Color(0xFF1E1713), RoundedCornerShape(3.dp))
                    .border(0.8.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TacticalCommand.values().forEach { command ->
                    Button(
                        onClick = { onSendCommand(command) },
                        enabled = !engine.isFinished && !engine.polliceVersoState.isActive,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (command) {
                                TacticalCommand.FINISH_HIM -> RomanCrimson
                                TacticalCommand.RAISE_SHIELD, TacticalCommand.KEEP_DISTANCE -> RomanBronzeDark
                                else -> Color(0xFF2E221B)
                            },
                            contentColor = RomanParchment
                        ),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(0.85f)
                            .border(0.5.dp, RomanBronze, RoundedCornerShape(2.dp)),
                        contentPadding = PaddingValues(1.dp)
                    ) {
                        Text(
                            text = command.title,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // POLLICE VERSO DECISION MODAL
        if (engine.polliceVersoState.isActive && !engine.polliceVersoState.hasDecided) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                RomanCard(
                    title = "POLICE VERSO (HÜKÜM ANI)",
                    badge = "Capua Kalabalığı Karar Veriyor",
                    modifier = Modifier.width(420.dp)
                ) {
                    Text(
                        text = "${engine.polliceVersoState.fallenFighterName} DİZLERİNİN ÜZERİNE ÇÖKTÜ!",
                        color = RomanGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Kalabalık ayağa kalktı. Yüzlerce başparmak havada! Seyircilerin %${engine.polliceVersoState.crowdMercyPercent}'i Missio (bağışlama) talep ediyor.",
                        color = RomanParchment,
                        fontSize = 10.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Option 1: Missio (Spare)
                        Button(
                            onClick = { onDecidePolliceVerso(true, false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RomanSuccessGreen,
                                contentColor = RomanParchment
                            ),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Text(text = "👍 BAĞIŞLA (MISSIO)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }

                        // Option 2: Bribe Referee
                        Button(
                            onClick = { onDecidePolliceVerso(true, true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RomanGoldDark,
                                contentColor = RomanParchment
                            ),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Text(text = "💰 RÜŞVET VER (500)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        // Option 3: Execute (Pollice Verso)
                        Button(
                            onClick = { onDecidePolliceVerso(false, false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RomanCrimson,
                                contentColor = RomanParchment
                            ),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Text(text = "👎 İNFAZ ET (ÖLÜM)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // TOP-DOWN SPRITE SHEET INSPECTION DIALOG
        if (showSpriteSheetDialog) {
            TopDownSpriteSheetDialog(
                onDismissRequest = { showSpriteSheetDialog = false }
            )
        }
    }
}
