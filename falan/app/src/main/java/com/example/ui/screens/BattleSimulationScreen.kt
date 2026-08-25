package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.MainUiState
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun BattleSimulationScreen(
    state: MainUiState,
    onSetSpeed: (Float) -> Unit,
    onInstantFinish: () -> Unit,
    onDismissBattle: () -> Unit,
    onCrowdJudgement: (CrowdVerdict) -> Unit,
    onShoutLanista: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val battle = state.activeBattle ?: return

    // Pulse animation for critical moments
    val infiniteTransition = rememberInfiniteTransition(label = "battle_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gladiator_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBg)
            .padding(14.dp)
            .testTag("battle_simulation_screen"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Arena Stage Banner & Speed Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🏟️ ARENA DÖVÜŞÜ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = ImmersiveGold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ImmersiveTerracotta.copy(alpha = 0.25f),
                        border = BorderStroke(0.8.dp, ImmersiveTerracotta.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = battle.matchFormat.title,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                            color = ImmersiveTerracottaLight
                        )
                    }
                }
                Text(
                    text = "Taktik: ${battle.tactic.title} • Tur: ${battle.turnCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ImmersiveTextMuted
                )
            }

            // Speed Control Buttons
            if (!battle.isFinished && !battle.isAwaitingCrowdJudgement) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SpeedButton(label = "1x", isSelected = state.battleSpeed == 1.0f, onClick = { onSetSpeed(1.0f) })
                    SpeedButton(label = "2x", isSelected = state.battleSpeed == 2.0f, onClick = { onSetSpeed(2.0f) })
                    SpeedButton(label = "⚡ Hızlı", isSelected = state.battleSpeed == 0.0f, onClick = onInstantFinish)
                }
            }
        }

        // Crowd Hype Meter
        CrowdHypeMeter(hypeLevel = battle.crowdHype)

        // Pixel Art Arena Combat Stage
        PixelCombatArena(
            battle = battle,
            modifier = Modifier.fillMaxWidth()
        )

        // Interactive Lanista Shouts (Live Coaching during fight)
        if (!battle.isFinished && !battle.isAwaitingCrowdJudgement) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onShoutLanista("HYPE") }
                        .testTag("btn_shout_hype"),
                    color = ImmersiveGold.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🔥", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Bastır! (+Hype)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = ImmersiveGold
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onShoutLanista("DEFENSE") }
                        .testTag("btn_shout_def"),
                    color = Color(0xFF2196F3).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🛡️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Kalkana Geç!",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = Color(0xFF90CAF9)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onShoutLanista("ATTACK") }
                        .testTag("btn_shout_atk"),
                    color = Color(0xFFE53935).copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, Color(0xFFE53935).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "⚡", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Açık Kolla!",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = Color(0xFFFF8A80)
                        )
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // LIVE ACTION COMBAT LOGS FEED (When fight is ongoing)
        // -------------------------------------------------------------
        if (!battle.isAwaitingCrowdJudgement && !battle.isFinished) {
            Text(
                text = "CANLI MAÇ ANLATIMI (COMBAT LOG)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = ImmersiveGold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCardBg),
                border = BorderStroke(1.dp, ImmersiveBorderSubtle)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(battle.logs) { log ->
                        CombatLogItem(log = log)
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // POLLICE VERSO: CROWD & PLAYER JUDGEMENT MODAL (MISSIO vs IUGULA)
        // -------------------------------------------------------------
        if (battle.isAwaitingCrowdJudgement) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pollice_verso_decision_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1410)),
                    border = BorderStroke(2.dp, ImmersiveGold)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title Header
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ImmersiveGold.copy(alpha = 0.20f),
                            border = BorderStroke(1.2.dp, ImmersiveGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "🏛️ POLLICE VERSO • ARENA HÜKMÜ",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.2.sp),
                                    color = ImmersiveGold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        // Drama Description
                        Text(
                            text = "${battle.playerGladiator.name} kılıcını diz çökmüş olan ${battle.enemyGladiator.name}'un boynuna dayadı. Binlerce seyirci ve senatörler başparmağının yönünü bekliyor!",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = ImmersiveTextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // -------------------------------------------------------------
                        // CROWD SENTIMENT GAUGE (0% CLEMENCY <---> 100% BLOODLUST)
                        // -------------------------------------------------------------
                        val bloodlust = battle.crowdBloodlustPercent
                        val isCrowdBloodthirsty = bloodlust >= 55
                        val isCrowdMerciful = bloodlust <= 45
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF100B08)),
                            border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🏟️ SEYİRCİ DUYGUSU (CROWD SENTIMENT)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
                                        color = ImmersiveGold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isCrowdBloodthirsty) Color(0xFF5C1010) else if (isCrowdMerciful) Color(0xFF1B4D2E) else Color(0xFF4A3B18)
                                    ) {
                                        Text(
                                            text = if (isCrowdBloodthirsty) "🩸 KAN İSTİYOR (%$bloodlust)" else if (isCrowdMerciful) "🕊️ BAĞIŞLAMA İSTİYOR (%${100 - bloodlust})" else "⚖️ BÖLÜNMÜŞ (%50)",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                            color = if (isCrowdBloodthirsty) Color(0xFFFF8A80) else if (isCrowdMerciful) Color(0xFF81C784) else ImmersiveGold
                                        )
                                    }
                                }

                                // Dual Progress Sentiment Bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF2C1E14))
                                ) {
                                    // Mercy Side (Green/Gold background base)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFF2E7D32), Color(0xFF4CAF50), Color(0xFFFFB300))
                                                )
                                            )
                                    )
                                    // Bloodlust Side overlay from the right
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(bloodlust / 100f)
                                            .fillMaxHeight()
                                            .align(Alignment.CenterEnd)
                                            .background(
                                                Brush.horizontalGradient(
                                                    listOf(Color(0xFFFFB300), Color(0xFFFF5252), Color(0xFFD50000))
                                                )
                                            )
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "🕊️ MITTE! %${100 - bloodlust} (Bağışla)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                        color = Color(0xFFA5D6A7)
                                    )
                                    Text(
                                        text = "🩸 IUGULA! %$bloodlust (Boynunu Vur)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                        color = Color(0xFFFF8A80)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // -------------------------------------------------------------
                        // GLADIATOR HEALTH & VALOR BREAKDOWN
                        // -------------------------------------------------------------
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Player Health Status
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = RomanCardSecondary),
                                border = BorderStroke(1.dp, ImmersiveCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "⚔️ ${battle.playerGladiator.name}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ImperialRomanRed
                                    )
                                    Text(
                                        text = "Kalan Can: %${(battle.playerHealthPercent * 100).toInt()} (${battle.playerCurrentHp} HP)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        color = ImmersiveTextPrimary
                                    )
                                }
                            }

                            // Defeated Enemy Valor
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = RomanCardSecondary),
                                border = BorderStroke(1.dp, ImmersiveCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "💀 ${battle.enemyGladiator.name}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = RomanInkDark
                                    )
                                    Text(
                                        text = battle.enemyValorTitle,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                        color = ImmersiveTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // -------------------------------------------------------------
                        // DECISION BUTTONS & CONSEQUENCE PREVIEWS (MERCY vs EXECUTION)
                        // -------------------------------------------------------------
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // MISSIO BUTTON (MERCY)
                            Button(
                                onClick = { onCrowdJudgement(CrowdVerdict.MISSIO) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_crowd_missio"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE8F5E9),
                                    contentColor = Color(0xFF1B5E20)
                                ),
                                border = BorderStroke(1.5.dp, Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(10.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = "👍 BAĞIŞLA",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
                                    )
                                    Text(
                                        text = "MERCY (Missio)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        thickness = 0.5.dp,
                                        color = Color(0xFF2E7D32).copy(alpha = 0.4f)
                                    )
                                    Text(
                                        text = "+${battle.calculatedMercyPrestigeGain} 🌿 Prestij",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontWeight = FontWeight.Black),
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = "🛡️ Gelecek Rakipler: DENGELİ & SAYGILI (-%8 Tehdit)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.SemiBold),
                                        color = Color(0xFF2E7D32),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "+20 Gladyatör Morali",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        color = RomanImperialGold
                                    )
                                }
                            }

                            // IUGULA BUTTON (EXECUTION)
                            Button(
                                onClick = { onCrowdJudgement(CrowdVerdict.IUGULA) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_crowd_iugula"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFEBEE),
                                    contentColor = Color(0xFFB71C1C)
                                ),
                                border = BorderStroke(1.5.dp, Color(0xFFC62828)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(10.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = "👎 İNFAZ ET",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
                                    )
                                    Text(
                                        text = "EXECUTION (Iugula)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        thickness = 0.5.dp,
                                        color = Color(0xFFC62828).copy(alpha = 0.4f)
                                    )
                                    Text(
                                        text = "+${battle.calculatedExecutionGoldGain} 🪙 Altın",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontWeight = FontWeight.Black),
                                        color = Color(0xFFB71C1C)
                                    )
                                    Text(
                                        text = "⚔️ Rakip Okullar: İNTİKAM YEMİNİ (+%20 Tehdit)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.SemiBold),
                                        color = Color(0xFFC62828),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "+15 Gladyatör Morali",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                        color = RomanImperialGold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // BATTLE OUTCOME FINISHED CARD (High-contrast, scannable)
        // -------------------------------------------------------------
        if (battle.isFinished && !battle.isAwaitingCrowdJudgement) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (battle.isPlayerVictorious) Color(0xFF1E1710) else Color(0xFF281010)
                    ),
                    border = BorderStroke(1.5.dp, if (battle.isPlayerVictorious) ImmersiveGold else ImmersiveTerracotta)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (battle.isPlayerVictorious) {
                                if (battle.crowdJudgementDecision == CrowdVerdict.IUGULA) "⚔️ KANLI İNFAZ & ZAFER!"
                                else if (battle.crowdJudgementDecision == CrowdVerdict.MISSIO) "🕊️ ASİL MERHAMET & ZAFER!"
                                else "👑 PARLAK ZAFER!"
                            } else "💀 MAĞLUBİYET",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                            color = if (battle.isPlayerVictorious) ImmersiveGold else ImmersiveTerracottaLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (battle.isPlayerVictorious) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ImmersiveGold.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "+${battle.earnedGold} 🪙 Altın",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                        color = ImmersiveGold
                                    )
                                    Text(
                                        text = "+${battle.earnedPrestige} 🌿 Toplam Prestij",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                        color = Color(0xFFA5D6A7)
                                    )
                                }
                            }
                            
                            // Decision impact banner & Future opponent intensity
                            battle.crowdJudgementDecision?.let { decision ->
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (decision == CrowdVerdict.IUGULA) Color(0xFF381010) else Color(0xFF14301C),
                                    border = BorderStroke(1.dp, if (decision == CrowdVerdict.IUGULA) Color(0xFFE53935) else Color(0xFF4CAF50))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (decision == CrowdVerdict.IUGULA) {
                                                "⚔️ İnfaz Sonucu: Şehirdeki rakip luduslar intikam yemini etti! Gelecek rakipler daha tehlikeli ve intikamcı (+%15 Tehdit)."
                                            } else {
                                                "🕊️ Bağışlama Sonucu: Roma halkı ve senatörler cömertliği övdü. Gelecek rakipler onurlu ve dengeli seviyede kaldı (-%8 Tehdit)."
                                            },
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = if (decision == CrowdVerdict.IUGULA) Color(0xFFFFAB91) else Color(0xFFC8E6C9),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            
                            if (battle.wasFreedByPromise) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "🕊️ Azad Vaadi Gerçekleşti! Gladyatör özgürlüğüne kavuştu.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = ImmersiveSuccess
                                )
                            }
                        } else {
                            Text(
                                text = if (battle.sufferedPermanentInjury) "Gladyatörün ağır yara aldı ve revire kaldırıldı." else "Yenilgiye rağmen gladyatörün hayatta kaldı.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ImmersiveTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onDismissBattle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dismiss_battle_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (battle.isPlayerVictorious) ImmersiveGold else ImmersiveCardBg
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🏛️ Ludus'a Geri Dön",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                                color = if (battle.isPlayerVictorious) Color.Black else ImmersiveTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CombatantStageView(
    name: String,
    origin: String,
    gladiatorClass: GladiatorClass,
    tierStar: String,
    traitName: String,
    currentHp: Int,
    maxHp: Int,
    currentStamina: Int,
    isPlayer: Boolean,
    isPulse: Boolean,
    pulseScale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isPlayer) Alignment.Start else Alignment.End
    ) {
        // Avatar circle with class icon
        Surface(
            shape = CircleShape,
            color = if (isPlayer) ImmersiveCardBgSecondary else Color(0xFF38150D),
            border = BorderStroke(1.5.dp, if (isPlayer) ImmersiveGold else ImmersiveTerracotta),
            modifier = Modifier
                .size(46.dp)
                .scale(if (isPulse) pulseScale else 1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = when (gladiatorClass) {
                        GladiatorClass.MURMILLO -> "🛡️"
                        GladiatorClass.RETIARIUS -> "🔱"
                        GladiatorClass.THRAEX -> "⚔️"
                        GladiatorClass.SECUTOR -> "🤺"
                        GladiatorClass.DIMACHAERUS -> "🗡️"
                    },
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
            color = ImmersiveTextPrimary
        )

        // Tier / Trait Badges
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isPlayer) Arrangement.Start else Arrangement.End
        ) {
            Text(
                text = tierStar,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                color = ImmersiveGold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "• $traitName",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = ImmersiveTextMuted
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        // HP Bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "HP", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = ImmersiveTerracottaLight)
            Spacer(modifier = Modifier.width(4.dp))
            LinearProgressIndicator(
                progress = { (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier
                    .width(76.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (currentHp < maxHp * 0.3f) ImmersiveTerracotta else ImmersiveSuccess,
                trackColor = ImmersiveBorderSubtle
            )
        }

        Spacer(modifier = Modifier.height(3.dp))
        // Stamina Bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "STA", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = ImmersiveGold)
            Spacer(modifier = Modifier.width(4.dp))
            LinearProgressIndicator(
                progress = { (currentStamina.toFloat() / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .width(76.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ImmersiveGold,
                trackColor = ImmersiveBorderSubtle
            )
        }
    }
}

@Composable
fun CombatLogItem(
    log: BattleActionLog,
    modifier: Modifier = Modifier
) {
    val bgColor = if (log.isCritical) ImmersiveTerracotta.copy(alpha = 0.2f) else ImmersiveBg.copy(alpha = 0.6f)
    val borderColor = if (log.isCritical) ImmersiveTerracotta.copy(alpha = 0.6f) else ImmersiveBorderSubtle

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        border = BorderStroke(0.8.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
            Text(
                text = log.text,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (log.isCritical) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.5.sp
                ),
                color = if (log.isPlayerAction) ImmersiveGold else ImmersiveTextPrimary
            )
            if (log.crowdReaction != null) {
                Text(
                    text = "🗣️ ${log.crowdReaction}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ImmersiveSuccess
                )
            }
        }
    }
}

@Composable
fun SpeedButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) ImmersiveGold else ImmersiveCardBgSecondary,
        border = BorderStroke(1.dp, if (isSelected) ImmersiveGold else ImmersiveBorderSubtle),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) Color.Black else ImmersiveTextPrimary
        )
    }
}
