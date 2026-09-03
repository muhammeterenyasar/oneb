package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Gladiator
import com.example.simulation.ActiveScreen
import com.example.simulation.LudusUiState
import com.example.ui.components.GladiatorAvatarCanvas
import com.example.ui.components.GladiatorMiniSprite
import com.example.ui.components.GladiatorSpriteShowcase
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.theme.*

@Composable
fun RosterScreen(
    state: LudusUiState,
    onSelectGladiator: (Gladiator) -> Unit,
    onNavigate: (ActiveScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedGlad = state.selectedGladiator ?: state.gladiators.first()

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // LEFT PANE: Football Manager Style Roster Table
        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
        ) {
            RomanCard(
                title = "Kadro Tablosu (Roster Overview)",
                badge = "${state.gladiators.size} Dövüşçü Mevcut",
                modifier = Modifier.fillMaxSize()
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF191310))
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "İSİM & SINIF",
                        color = RomanGold,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.4f)
                    )
                    Text(
                        text = "STATÜ",
                        color = RomanParchmentDark,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.1f)
                    )
                    Text(
                        text = "SAĞLIK",
                        color = RomanSuccessGreen,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.7f)
                    )
                    Text(
                        text = "STAMİNA",
                        color = RomanStaminaCyan,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.7f)
                    )
                    Text(
                        text = "MORAL",
                        color = RomanGoldLight,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.7f)
                    )
                    Text(
                        text = "DURUM",
                        color = RomanParchment,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.8f)
                    )
                }
                HorizontalDivider(color = RomanBronze, thickness = 0.8.dp)

                // Table Rows
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(state.gladiators) { glad ->
                        val isSelected = glad.id == selectedGlad.id
                        val rowBg = if (isSelected) Color(0xFF3B1818) else Color.Transparent

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowBg, RoundedCornerShape(2.dp))
                                .border(
                                    width = if (isSelected) 1.dp else 0.dp,
                                    color = if (isSelected) RomanGold else Color.Transparent,
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .clickable { onSelectGladiator(glad) }
                                .padding(horizontal = 4.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Name & Class
                            Row(
                                modifier = Modifier.weight(1.4f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                GladiatorMiniSprite(glad.gladiatorClass, 28.dp)
                                Column {
                                    Text(
                                        text = glad.name,
                                        color = if (isSelected) RomanGoldLight else RomanParchment,
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

                            // Status
                            Text(
                                text = glad.status.displayName.split(" ")[0],
                                color = RomanTextSecondary,
                                fontSize = 10.sp,
                                modifier = Modifier.weight(1.1f)
                            )

                            // Health
                            Text(
                                text = "%${glad.condition.health}",
                                color = if (glad.condition.health > 50) RomanSuccessGreen else RomanDangerRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(0.7f)
                            )

                            // Stamina
                            Text(
                                text = "%${glad.condition.stamina}",
                                color = RomanStaminaCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(0.7f)
                            )

                            // Morale
                            Text(
                                text = "%${glad.condition.morale}",
                                color = RomanGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(0.7f)
                            )

                            // Condition / Injury badge
                            if (glad.isInjured) {
                                Box(
                                    modifier = Modifier
                                        .weight(0.8f)
                                        .background(RomanDangerRed, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "YARALI",
                                        color = RomanParchment,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .weight(0.8f)
                                        .background(RomanSuccessGreen.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "HAZIR",
                                        color = RomanSuccessGreen,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = RomanBronzeDark.copy(alpha = 0.3f), thickness = 0.5.dp)
                    }
                }
            }
        }

        // RIGHT PANE: Selected Gladiator Profile Inspector
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(
                title = "Gladyatör: ${selectedGlad.name}",
                badge = selectedGlad.status.displayName
            ) {
                // 1. Hero Sprite Showcase with Pedestal & Idle Animation
                GladiatorSpriteShowcase(
                    gladiator = selectedGlad,
                    height = 140.dp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 2. Profile Tabs State
                var activeTab by remember { mutableStateOf(0) } // 0: Physical, 1: Training, 2: Historical

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1612), RoundedCornerShape(3.dp))
                        .border(0.6.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val tabs = listOf("FİZİKSEL", "ANTRENMAN", "TARİHSEL", "TEÇHİZAT")
                    tabs.forEachIndexed { index, label ->
                        val isTabSelected = activeTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isTabSelected) RomanCrimson else Color.Transparent)
                                .clickable { activeTab = index }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isTabSelected) RomanGoldLight else RomanTextSecondary,
                                fontSize = 8.5.sp,
                                fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 3. Tab Contents
                when (activeTab) {
                    0 -> {
                        // TAB 0: PHYSICAL STATS
                        Text(
                            text = "BEDENSEL NİTELİKLER (PHYSICAL STATS)",
                            color = RomanGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))

                        RomanStatBar("Güç (Strength)", selectedGlad.physicalStats.strength, 20, RomanCrimson)
                        RomanStatBar("Hız (Speed)", selectedGlad.physicalStats.speed, 20, RomanWarningAmber)
                        RomanStatBar("Çeviklik (Agility)", selectedGlad.physicalStats.agility, 20, Color(0xFF38BDF8))
                        RomanStatBar("Dayanıklılık (Endurance)", selectedGlad.physicalStats.endurance, 20, RomanSuccessGreen)
                        RomanStatBar("Refleks (Reflex)", selectedGlad.physicalStats.reflex, 20, RomanGoldLight)
                        RomanStatBar("Acı Eşiği (Pain Tol.)", selectedGlad.physicalStats.painTolerance, 20, Color(0xFFF43F5E))

                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = RomanBronzeDark, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Physical metrics grid
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = "Boy: ${selectedGlad.physicalStats.heightCm} cm", color = RomanParchment, fontSize = 9.5.sp)
                                Text(text = "Kilo: ${selectedGlad.physicalStats.weightKg} kg", color = RomanParchment, fontSize = 9.5.sp)
                            }
                            Column {
                                Text(text = "Erişim: ${selectedGlad.physicalStats.reachCm} cm", color = RomanParchment, fontSize = 9.5.sp)
                                Text(text = "BMI: ${"%.1f".format(selectedGlad.physicalStats.bmi)}", color = RomanGoldLight, fontSize = 9.5.sp)
                            }
                            Column {
                                Text(text = "Kas Yoğunluğu: %${selectedGlad.physicalStats.muscleDensity}", color = RomanGold, fontSize = 9.5.sp)
                                Text(text = "Yapı: ${selectedGlad.physicalStats.bodyType}", color = RomanSuccessGreen, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "★ Atletik Güç İndeksi: ${selectedGlad.physicalStats.athleticScore} PTS",
                            color = RomanGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    1 -> {
                        // TAB 1: TRAINING & SKILL PROGRESSION
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TALİM & GELİŞİM (TRAINING & DRILLS)",
                                color = RomanGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${selectedGlad.trainingProgress.completedDrillCount} Talim Tamamlandı",
                                color = RomanParchmentDark,
                                fontSize = 8.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))

                        // Mastery XP Progress Bar
                        val xpProgress = (selectedGlad.trainingProgress.currentMasteryXp.toFloat() / selectedGlad.trainingProgress.xpToNextLevel.toFloat()).coerceIn(0f, 1f)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Seviye ${selectedGlad.trainingProgress.masteryLevel} Ustalık", color = RomanGoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${selectedGlad.trainingProgress.currentMasteryXp}/${selectedGlad.trainingProgress.xpToNextLevel} XP", color = RomanParchment, fontSize = 9.sp)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF140E0C))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(xpProgress)
                                    .background(RomanGold)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Odak: ${selectedGlad.trainingProgress.currentFocus}", color = RomanGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Diyet: ${selectedGlad.trainingProgress.dietRegimen}", color = RomanParchmentDark, fontSize = 9.sp)
                        Text(text = "Eğitmen: ${selectedGlad.trainingProgress.assignedInstructor}", color = RomanTextSecondary, fontSize = 9.sp)

                        Spacer(modifier = Modifier.height(4.dp))
                        RomanStatBar("Silah Ustalığı", selectedGlad.trainingProgress.weaponMastery, 100, RomanCrimson)
                        RomanStatBar("Kalkan Savunması", selectedGlad.trainingProgress.shieldMastery, 100, Color(0xFF60A5FA))
                        RomanStatBar("Ayak Hareketleri", selectedGlad.trainingProgress.footworkMastery, 100, RomanWarningAmber)
                        RomanStatBar("Taktik Disiplin", selectedGlad.trainingProgress.tacticalDiscipline, 100, RomanSuccessGreen)

                        Spacer(modifier = Modifier.height(2.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Yorgunluk: %${selectedGlad.trainingProgress.fatigueAccrued}", color = if (selectedGlad.trainingProgress.fatigueAccrued > 40) RomanDangerRed else RomanTextSecondary, fontSize = 9.sp)
                            Text(text = "Antrenman Verimi: %${selectedGlad.trainingProgress.trainingEfficiencyScore}", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    2 -> {
                        // TAB 2: HISTORICAL PERFORMANCE
                        Text(
                            text = "ARENA KARİYERİ (HISTORICAL PERFORMANCE)",
                            color = RomanGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = "Kayıt: ${selectedGlad.historicalPerformance.victories}G - ${selectedGlad.historicalPerformance.defeats}M", color = RomanSuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Galibiyet Oranı: %${"%.0f".format(selectedGlad.historicalPerformance.winRatePercent)}", color = RomanGoldLight, fontSize = 9.sp)
                            }
                            Column {
                                Text(text = "İnfaz (Kills): ${selectedGlad.historicalPerformance.kills}", color = RomanDangerRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Ölümcüllük: %${"%.0f".format(selectedGlad.historicalPerformance.lethalityRatePercent)}", color = RomanParchmentDark, fontSize = 9.sp)
                            }
                            Column {
                                Text(text = "Halk Onayı: %${selectedGlad.historicalPerformance.crowdApprovalRating}", color = RomanGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Af / Bağışlanma: ${selectedGlad.historicalPerformance.sparedByCrowd}", color = RomanParchment, fontSize = 9.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = RomanBronzeDark, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Mevcut Seri: ${selectedGlad.historicalPerformance.currentWinStreak} Zafer", color = RomanGoldLight, fontSize = 9.5.sp)
                            Text(text = "En İyi Seri: ${selectedGlad.historicalPerformance.bestWinStreak} Zafer", color = RomanGold, fontSize = 9.5.sp)
                        }
                        Text(text = "Toplam Kazanç: ${selectedGlad.historicalPerformance.totalDenariiEarned} Denarii", color = RomanGoldLight, fontSize = 9.5.sp)
                        Text(text = "Kritik Vuruşlar: ${selectedGlad.historicalPerformance.criticalStrikesDelivered} | Silahsız Kalma: ${selectedGlad.historicalPerformance.timesDisarmed}", color = RomanParchmentDark, fontSize = 9.sp)
                        Text(text = "Dövüşülen Arenalar: ${selectedGlad.historicalPerformance.arenasFoughtIn.joinToString(", ")}", color = RomanTextSecondary, fontSize = 9.sp)
                        if (selectedGlad.historicalPerformance.titlesWon.isNotEmpty()) {
                            Text(text = "Unvanlar: ${selectedGlad.historicalPerformance.titlesWon.joinToString(", ")}", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    3 -> {
                        // TAB 3: TEÇHİZAT & LOADOUT (EQUIPMENT)
                        val loadout = state.gladiatorLoadouts[selectedGlad.id]
                        val pref = state.gladiatorPreferences[selectedGlad.id]

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ARENA TEÇHİZATI (9-SLOT LOADOUT)",
                                color = RomanGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (pref != null) {
                                Text(
                                    text = "Favori: ${pref.favoredWeaponType.name}",
                                    color = RomanGoldLight,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        if (loadout == null) {
                            Text(text = "Kuşanılmış teçhizat bulunmuyor.", color = RomanTextMuted, fontSize = 9.sp)
                        } else {
                            // Metrics summary bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1B1411), RoundedCornerShape(2.dp))
                                    .border(0.5.dp, RomanBronzeDark, RoundedCornerShape(2.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Ağırlık: ${"%.1f".format(loadout.totalWeightKg)} kg", color = RomanParchment, fontSize = 8.5.sp)
                                Text(text = "Koruma: ${loadout.totalProtection}", color = RomanGold, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Hız Kaybı: -${loadout.totalMobilityPenalty}", color = if (loadout.totalMobilityPenalty > 3) RomanDangerRed else RomanSuccessGreen, fontSize = 8.5.sp)
                                Text(text = "Nefes: +%${loadout.totalStaminaDrainPercent}", color = RomanStaminaCyan, fontSize = 8.5.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // 9 Slots Grid (Row of 3 items each)
                            val slots = listOf(
                                "Miğfer" to loadout.helmet,
                                "Gövde Zırhı" to loadout.bodyArmor,
                                "Ana Silah" to loadout.mainHand,
                                "Kalkan" to loadout.shield,
                                "İkinci Silah" to loadout.offHand,
                                "Sağ Kol (Manica)" to loadout.rightArm,
                                "Sol Kol" to loadout.leftArm,
                                "Bacak Zırhı" to loadout.legs,
                                "Aksesuar" to loadout.accessory
                            )

                            slots.chunked(3).forEach { rowSlots ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    rowSlots.forEach { (slotName, item) ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(Color(0xFF221714), RoundedCornerShape(2.dp))
                                                .border(0.5.dp, if (item != null) Color(item.quality.colorHex) else RomanBronzeDark, RoundedCornerShape(2.dp))
                                                .padding(3.dp)
                                        ) {
                                            Column {
                                                Text(text = slotName, color = RomanTextMuted, fontSize = 7.5.sp)
                                                Text(
                                                    text = item?.name ?: "— Boş —",
                                                    color = if (item != null) RomanParchment else RomanTextMuted,
                                                    fontSize = 8.5.sp,
                                                    fontWeight = if (item != null) FontWeight.Bold else FontWeight.Normal,
                                                    maxLines = 1
                                                )
                                                if (item != null) {
                                                    val dur = if (item.maxDurability > 0) ((item.currentDurability.toFloat() / item.maxDurability) * 100).toInt() else 0
                                                    Text(
                                                        text = "%$dur Sağlam (${item.conditionState.displayName})",
                                                        color = if (dur > 60) RomanSuccessGreen else if (dur > 30) RomanWarningAmber else RomanDangerRed,
                                                        fontSize = 7.5.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                            }

                            // Preference reactions
                            if (pref != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                if (pref.hatesHeavyArmor && (loadout.bodyArmor?.mobilityPenalty ?: 0) >= 4) {
                                    Text(
                                        text = "⚠ ${selectedGlad.name} ağır zırhtan nefret ediyor (-3 Moral)!",
                                        color = RomanDangerRed,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (loadout.mainHand?.type == pref.favoredWeaponType) {
                                    Text(
                                        text = "✓ Tercih ettiği silahı (${pref.favoredWeaponType.name}) kuşanmış (+2 Moral).",
                                        color = RomanSuccessGreen,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(4.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = { onNavigate(ActiveScreen.TRAINING) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RomanCrimson,
                            contentColor = RomanParchment
                        ),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "ANTRENMAN", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onNavigate(ActiveScreen.MEDICAL) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RomanBronzeDark,
                            contentColor = RomanParchment
                        ),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "REVİR", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { onNavigate(ActiveScreen.EQUIPMENT_MARKET) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RomanGoldDark,
                            contentColor = RomanParchment
                        ),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(30.dp)
                            .border(0.6.dp, RomanGold, RoundedCornerShape(3.dp)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "TEÇHİZAT PAZARI", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
