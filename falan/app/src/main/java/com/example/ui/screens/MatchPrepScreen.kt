package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun MatchPrepScreen(
    state: MainUiState,
    onSelectGladiator: (Gladiator) -> Unit,
    onSelectOpponent: (EnemyGladiator) -> Unit,
    onSelectFormat: (MatchFormat) -> Unit,
    onSelectTactic: (BattleTactic) -> Unit,
    onSetBet: (Int) -> Unit,
    onTogglePromiseOfFreedom: (Boolean) -> Unit,
    onStartBattle: () -> Unit,
    onAdvancePhase: () -> Unit,
    onNavigateToCalendar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val selectedGladiator = state.selectedGladiator ?: state.gladiators.firstOrNull()
    val scheduledEvent = state.ludusState.currentScheduledEvent
    val isFightDay = state.ludusState.isFightDay
    val selectedOpponent = state.selectedOpponent ?: scheduledEvent?.featuredOpponent ?: state.availableOpponents.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBg)
            .padding(horizontal = 16.dp)
            .testTag("match_prep_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Scheduled Tournament & Arena Level Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                border = BorderStroke(1.2.dp, if (isFightDay) ImmersiveGold else ImmersiveCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    if (isFightDay) ImmersiveTerracottaDeep.copy(alpha = 0.6f) else ImmersiveCardBgSecondary,
                                    ImmersiveCard
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "MATCH LOBBY • ${state.ludusState.cityTier.cityName.uppercase()}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.2.sp
                                    ),
                                    color = ImmersiveGold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ImmersiveGold
                                ) {
                                    Text(
                                        text = "ARENA SEVİYE ${state.ludusState.cityTier.tierNumber}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        ),
                                        color = Color.Black
                                    )
                                }
                            }
                            Text(
                                text = state.ludusState.cityTier.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = ImmersiveTextMuted
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isFightDay) ImmersiveEmerald else ImmersiveTrack,
                            border = BorderStroke(1.dp, if (isFightDay) ImmersiveEmerald else ImmersiveCardBorder)
                        ) {
                            Text(
                                text = if (isFightDay) "⚔️ DÖVÜŞ GÜNÜ" else "⏳ ${state.ludusState.daysUntilNextFight} GÜN KALDI",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                ),
                                color = if (isFightDay) Color.Black else ImmersiveGold
                            )
                        }
                    }

                    // Reputation & Crowd Sentiment Threat Indicator
                    val sentiment = state.ludusState.crowdSentimentLevel
                    val dynamicDiff = state.ludusState.dynamicOpponentDifficultyPercent
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(sentiment.badgeColorHex).copy(alpha = 0.18f),
                            border = BorderStroke(0.8.dp, Color(sentiment.badgeColorHex))
                        ) {
                            Text(
                                text = "${sentiment.icon} ${sentiment.displayName}",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold),
                                color = Color(sentiment.badgeColorHex)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (dynamicDiff > 105) Color(0xFF4A1A00) else Color(0xFF1E1E1E),
                            border = BorderStroke(0.8.dp, if (dynamicDiff > 105) Color(0xFFFF6D00) else ImmersiveBorderSubtle)
                        ) {
                            Text(
                                text = "⚔️ Rakip Tehdidi: %$dynamicDiff",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold),
                                color = if (dynamicDiff > 105) Color(0xFFFFAB40) else ImmersiveTextSecondary
                            )
                        }
                    }

                    state.ludusState.lastDecisionConsequence?.let { consequence ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = consequence,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                            color = ImmersiveGold
                        )
                    }

                    if (scheduledEvent != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = ImmersiveCardBorder, thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "RESMİ ETKİNLİK: ${scheduledEvent.title}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ImmersiveGoldLight
                                )
                                Text(
                                    text = "Ev Sahibi: ${scheduledEvent.hostPatron} • Hedef Gün: ${scheduledEvent.targetDay}. Gün",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = ImmersiveTextSecondary
                                )
                            }
                            Text(
                                text = "+${scheduledEvent.rewardGold} 🪙  +${scheduledEvent.rewardPrestige} 🌿",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveGold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onNavigateToCalendar,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("match_prep_open_calendar_btn"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ImmersiveGold),
                            border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🗓️ Müsabaka Takvimini & Gelecek Dövüşleri İncele",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                            )
                        }
                    }
                }
            }
        }

        // 1. Fighter Selection
        item {
            Text(
                text = "1. MÜSABAKAYA ÇIKACAK GLADYATÖR",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = ImmersiveGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.gladiators, key = { it.id }) { g ->
                    val isSelected = g.id == selectedGladiator?.id
                    val isInjured = g.isInjured
                    Surface(
                        modifier = Modifier
                            .clickable(enabled = !isInjured) { onSelectGladiator(g) }
                            .testTag("match_select_gladiator_${g.id}"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) ImmersiveCardBgSecondary else ImmersiveCard,
                        border = BorderStroke(1.2.dp, if (isSelected) ImmersiveGold else ImmersiveCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = g.name,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (isInjured) ImmersiveTextMuted else if (isSelected) ImmersiveGold else ImmersiveTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${g.age} Yaş)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                    color = ImmersiveTextMuted
                                )
                            }
                            Text(
                                text = "${g.gladiatorClass.displayName} • Güç: ${g.totalPowerScore}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isInjured) ImmersiveTextMuted else ImmersiveTextMuted
                            )
                            if (isInjured) {
                                Text(
                                    text = "Yaralı (Revirde)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = ImmersiveTerracottaLight
                                )
                            } else {
                                FatigueIndicator(fatigue = g.fatigue)
                            }
                        }
                    }
                }
            }
        }

        // 2. Scheduled Opponent & Scouting Card
        item {
            Text(
                text = "2. EŞLEŞEN RAKİP & MAÇ DETAYLARI",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = ImmersiveGold
            )
            Spacer(modifier = Modifier.height(6.dp))

            val enemy = selectedOpponent ?: scheduledEvent?.featuredOpponent
            if (enemy != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scheduled_opponent_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                    border = BorderStroke(1.5.dp, ImmersiveTerracotta)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = ImmersiveTerracotta.copy(alpha = 0.3f),
                                    border = BorderStroke(1.dp, ImmersiveGold),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = when (enemy.gladiatorClass) {
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
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = enemy.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                            color = ImmersiveGold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = ImmersiveTerracotta
                                        ) {
                                            Text(
                                                text = "${enemy.tier.starRating} ${enemy.tier.title}",
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${enemy.title} • ${enemy.ludusOrigin}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = ImmersiveTextMuted
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+${enemy.rewardGold} 🪙",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = ImmersiveGold
                                )
                                Text(
                                    text = "+${enemy.rewardPrestige} 🌿",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ImmersiveEmerald
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        // Stats & Special Trait
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STR: ${enemy.str} | AGI: ${enemy.agi} | STA: ${enemy.sta} | HP: ${enemy.maxHp}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                                color = ImmersiveTextPrimary
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ImmersiveCardBgSecondary,
                                border = BorderStroke(0.8.dp, ImmersiveTerracotta.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "⚔️ ${enemy.traitName}: ${enemy.traitDescription}",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = ImmersiveTerracottaLight
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Match Format Selection
        item {
            Text(
                text = "3. ARENA MAÇ FORMATI",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = ImmersiveGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MatchFormat.entries.forEach { format ->
                    val isSelected = format == state.selectedFormat
                    val isLocked = (format == MatchFormat.TEAM_2V2 && state.ludusState.cityTier.tierNumber < 2) ||
                            (format == MatchFormat.SINE_MISSIO && state.ludusState.cityTier.tierNumber < 3)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isLocked) { onSelectFormat(format) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ImmersiveCardBgSecondary else ImmersiveCard
                        ),
                        border = BorderStroke(1.dp, if (isSelected) ImmersiveGold else ImmersiveCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = format.title,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (isLocked) ImmersiveTextMuted else if (isSelected) ImmersiveGold else ImmersiveTextPrimary
                                )
                                Text(
                                    text = format.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isLocked) ImmersiveTextMuted else ImmersiveTextMuted
                                )
                            }
                            if (isLocked) {
                                Text(
                                    text = "🔒 Kademe ${if (format == MatchFormat.TEAM_2V2) 2 else 3}'de Açılır",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ImmersiveTerracottaLight
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Battle Tactic Selection
        item {
            Text(
                text = "4. SAVAŞ DİREKTİFİ & TAKTİK",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = ImmersiveGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BattleTactic.entries.forEach { tactic ->
                    val isSelected = tactic == state.selectedTactic
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectTactic(tactic) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ImmersiveCardBgSecondary else ImmersiveCard
                        ),
                        border = BorderStroke(1.dp, if (isSelected) ImmersiveGold else ImmersiveCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = tactic.title,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) ImmersiveGold else ImmersiveTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tactic.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = ImmersiveTextMuted
                            )
                        }
                    }
                }
            }
        }

        // 5. Betting System
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                border = BorderStroke(1.dp, ImmersiveCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "💰 ARENA BAHİSİ",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Gladyatörünüzün galibiyetine altın yatırın. 1:1.8 oranında kazanç sağlar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ImmersiveTextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0, 25, 50, 100, 200).forEach { amount ->
                            val isSelected = state.betAmount == amount
                            val canAfford = state.ludusState.gold >= amount
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ImmersiveGold else ImmersiveTrack,
                                border = BorderStroke(1.dp, if (isSelected) ImmersiveGold else ImmersiveCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(enabled = canAfford || amount == 0) { onSetBet(amount) }
                            ) {
                                Text(
                                    text = if (amount == 0) "Yok" else "$amount",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.Black else if (canAfford) ImmersiveTextPrimary else ImmersiveTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Start Fight Action / Advance Schedule Button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            if (isFightDay) {
                Button(
                    onClick = onStartBattle,
                    enabled = selectedGladiator != null && !selectedGladiator.isInjured,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("start_battle_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImmersiveTerracotta,
                        disabledContainerColor = ImmersiveCardBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "⚔️ KANA BULANMIŞ KUMLARA ÇIK (DÖVÜŞÜ BAŞLAT)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
                    )
                }
            } else {
                Button(
                    onClick = onAdvancePhase,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("advance_to_fight_day_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImmersiveGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🏋️ ANTRENMAN YAPARAK GÜNÜ İLERLET (${state.ludusState.daysUntilNextFight} Gün Kaldı)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }
    }
}
