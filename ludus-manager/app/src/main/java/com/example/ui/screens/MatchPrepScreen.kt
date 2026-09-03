package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.model.CombatStance
import com.example.model.CombatTarget
import com.example.model.OpponentTier
import com.example.simulation.EquipmentEngine
import com.example.simulation.LudusUiState
import com.example.ui.components.GladiatorAvatarCanvas
import com.example.ui.components.GladiatorMiniSprite
import com.example.ui.components.GladiatorTopDownSpriteSheetCard
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.components.TopDownSpriteSheetDialog
import com.example.ui.theme.*

@Composable
fun MatchPrepScreen(
    state: LudusUiState,
    onSelectFighter: (String) -> Unit,
    onSetStance: (CombatStance) -> Unit,
    onSetTarget: (CombatTarget) -> Unit,
    onStartMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedFighter = state.gladiators.find { it.id == state.selectedFighterId }
        ?: state.gladiators.first()
    val opponent = state.scheduledMatch.opponentGladiator
    var showSpriteSheetDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // COLUMN 1: Gladiator Selection & Current Fighter Condition
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(title = "Dövüşçü Seçimi", badge = "Kadro") {
                Text(
                    text = "Arenaya çıkacak gladyatörü belirleyin:",
                    color = RomanTextSecondary,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                state.gladiators.forEach { glad ->
                    val isChosen = glad.id == selectedFighter.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isChosen) RomanDarkCrimson else Color.Transparent,
                                RoundedCornerShape(3.dp)
                            )
                            .border(
                                width = if (isChosen) 1.dp else 0.dp,
                                color = if (isChosen) RomanGold else Color.Transparent,
                                shape = RoundedCornerShape(3.dp)
                            )
                            .clickable { onSelectFighter(glad.id) }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            GladiatorMiniSprite(glad.gladiatorClass, 28.dp)
                            Column {
                                Text(
                                    text = glad.name,
                                    color = if (isChosen) RomanGoldLight else RomanParchment,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = glad.gladiatorClass.title,
                                    color = RomanTextSecondary,
                                    fontSize = 9.sp
                                )
                            }
                        }
                        Text(
                            text = "HP %${glad.condition.health}",
                            color = if (glad.condition.health > 50) RomanSuccessGreen else RomanDangerRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(color = RomanBronzeDark.copy(alpha = 0.3f), thickness = 0.5.dp)
                }

                Spacer(modifier = Modifier.height(6.dp))
                RomanStatBar("Sağlık", selectedFighter.condition.health, 100, RomanSuccessGreen)
                RomanStatBar("Dayanıklılık", selectedFighter.condition.stamina, 100, RomanStaminaCyan)
                RomanStatBar("Moral", selectedFighter.condition.morale, 100, RomanGold)
            }
        }

        // COLUMN 2: Tactical Plan & Instructions (Football Manager Tactics style)
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(title = "Taktik Talimatlar (Match Strategy)", badge = "Lanista Planı") {
                Text(
                    text = "DÖVÜŞ DURUŞU (COMBAT STANCE)",
                    color = RomanGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                CombatStance.values().forEach { stance ->
                    val isSelected = state.selectedStance == stance
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color(0xFF381515) else Color(0xFF1E1714),
                                RoundedCornerShape(2.dp)
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.5.dp,
                                color = if (isSelected) RomanGold else RomanBronzeDark,
                                shape = RoundedCornerShape(2.dp)
                            )
                            .clickable { onSetStance(stance) }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSetStance(stance) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = RomanGold,
                                unselectedColor = RomanBronzeDark
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = stance.label,
                                color = if (isSelected) RomanGoldLight else RomanParchment,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stance.desc,
                                color = RomanTextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "ÖNCELİKLİ HEDEF (TACTICAL AIM)",
                    color = RomanGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                CombatTarget.values().forEach { target ->
                    val isSelected = state.selectedTarget == target
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color(0xFF381515) else Color(0xFF1E1714),
                                RoundedCornerShape(2.dp)
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.5.dp,
                                color = if (isSelected) RomanGold else RomanBronzeDark,
                                shape = RoundedCornerShape(2.dp)
                            )
                            .clickable { onSetTarget(target) }
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSetTarget(target) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = RomanGold,
                                unselectedColor = RomanBronzeDark
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = target.label,
                            color = if (isSelected) RomanGoldLight else RomanParchment,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }

            // Top-Down Pixel Art Sprite Sheet Showcase & Inspector Card
            GladiatorTopDownSpriteSheetCard(
                onInspectClick = { showSpriteSheetDialog = true }
            )
        }

        // COLUMN 3: Match Details, Opponent Intel & Start Match Button
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val activeOpp = state.activeOpponentFighter
            val oppDisplayName = if (activeOpp != null) activeOpp.fullDisplayName else opponent.name
            val oppTactic = activeOpp?.signatureTactic ?: "Klasik gladyatör hücumu"
            val oppPersonality = activeOpp?.aiPersonality?.title ?: "Dengeli Muharip"

            RomanCard(
                title = "Rakip İstihbaratı",
                badge = "${state.selectedMatchType.title} • ${state.scheduledMatch.opponentLudus}"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GladiatorMiniSprite(opponent.gladiatorClass, 50.dp)
                    Column {
                        Text(
                            text = oppDisplayName,
                            color = RomanDangerRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${opponent.gladiatorClass.title} (${opponent.origin.region})",
                            color = RomanParchmentDark,
                            fontSize = 9.5.sp
                        )
                        Text(
                            text = "Kayıt: ${opponent.historicalPerformance.victories}G - ${opponent.historicalPerformance.defeats}M (${opponent.historicalPerformance.kills} İnfaz)",
                            color = RomanGoldLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Taktik Kimlik: $oppPersonality",
                            color = RomanGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "İmza Tekniği: $oppTactic",
                    color = RomanParchment,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))
                RomanStatBar("Rakip Güç (Strength)", opponent.physicalStats.strength, 20, RomanDangerRed)
                RomanStatBar("Rakip Hız (Speed)", opponent.physicalStats.speed, 20, RomanWarningAmber)
                RomanStatBar("Rakip Dayanıklılık (Endurance)", opponent.physicalStats.endurance, 20, RomanSuccessGreen)
                RomanStatBar("Rakip Kılıç (Swordsmanship)", opponent.attributes.swordsmanship, 20, RomanGoldDark)

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Müsabaka Kuralı: ${state.currentVenue.atmosphericRule}",
                    color = RomanGoldLight,
                    fontSize = 8.5.sp
                )
            }

            // Opponent Equipment Scouting Card (Requirement 19)
            val oppTier = activeOpp?.tier ?: OpponentTier.COMMON
            val scoutedOppLoadout = remember(opponent.id, oppTier) {
                EquipmentEngine.createLoadoutForOpponent(opponent, oppTier, state.currentVenue)
            }

            RomanCard(
                title = "Teçhizat İstihbaratı",
                badge = "${opponent.gladiatorClass.title} Zırhı"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Silah:", color = RomanTextMuted, fontSize = 8.sp)
                        Text(text = scoutedOppLoadout.mainHand?.name ?: "Gladius", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(text = "Kalkan:", color = RomanTextMuted, fontSize = 8.sp)
                        Text(text = scoutedOppLoadout.shield?.name ?: "Scutum", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(text = "Gövde:", color = RomanTextMuted, fontSize = 8.sp)
                        Text(text = scoutedOppLoadout.bodyArmor?.name ?: "Hamata", color = RomanParchment, fontSize = 9.sp)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(4.dp))

                // Tactical Counter Advice based on opponent's equipment
                val counterAdvice = when {
                    scoutedOppLoadout.shield != null && scoutedOppLoadout.shield!!.weightKg >= 4.0f ->
                        "⚔ Taktik Not: Ağır kalkan taşıyor. Blok gücü yüksek fakat dönüşü yavaş. Mesafeyi koruyup nefesini tüketin veya ayaklarına saldırın."
                    scoutedOppLoadout.helmet?.hearingPercent ?: 100 < 75 ->
                        "⚔ Taktik Not: Kapalı gladyatör miğferi takıyor (İşitme <%75). Köşelerden ve arkadan yapılacak çevirmelerde hakem/koç uyarılarını duyamaz."
                    scoutedOppLoadout.bodyArmor == null || scoutedOppLoadout.bodyArmor!!.effectiveProtection < 15 ->
                        "⚔ Taktik Not: Zırhsız/Hafif zırhlı. Gövdeye odaklanan doğrudan saplama darbeleri yüksek kanamaya yol açacaktır."
                    else ->
                        "⚔ Taktik Not: Dengeli lejyoner donanımı. Zırh eklem yerlerine ve baldırlara hedef alarak gardını düşürün."
                }
                Text(
                    text = counterAdvice,
                    color = RomanGoldLight,
                    fontSize = 8.5.sp,
                    lineHeight = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            RomanCard(title = "Ödül & Bahis") {
                Text(
                    text = "🏆 Zafer Ödülü: ${state.scheduledMatch.basePrizeGold} Denarii (${state.selectedMatchType.goldMultiplier}x)",
                    color = RomanGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "🎖 Prestij Bonusu: +${state.scheduledMatch.basePrestige} Şan",
                    color = RomanGoldLight,
                    fontSize = 10.sp
                )
                Text(
                    text = "👥 Arena: ${state.currentVenue.venueName} (${state.currentVenue.capacity} seyirci)",
                    color = RomanTextSecondary,
                    fontSize = 9.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onStartMatch,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RomanCrimson,
                        contentColor = RomanParchment
                    ),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .border(1.dp, RomanGold, RoundedCornerShape(3.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Fight",
                        tint = RomanGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ARENAYA ÇIK! (START COMBAT)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }

    if (showSpriteSheetDialog) {
        TopDownSpriteSheetDialog(
            onDismissRequest = { showSpriteSheetDialog = false }
        )
    }
}
