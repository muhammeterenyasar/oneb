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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.simulation.ActiveScreen
import com.example.simulation.LudusUiState
import com.example.ui.components.GladiatorSpriteShowcase
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.theme.*

/**
 * Unified Gladiator Management Hub.
 * Replaces fragmented standalone Roster, Training, and Medical screens.
 * Features:
 * - Left: Dense, scannable roster table with pinning.
 * - Center: Deep Gladiator Profile with 7 contextual tabs:
 *   Overview, Training, Equipment, Medical, Contract, Relationships, History.
 * - Right: Contextual Action Panel (Next Fight, Train, Rest, Treat, Equip).
 */
@Composable
fun GladiatorHubScreen(
    state: LudusUiState,
    onSelectGladiator: (Gladiator) -> Unit,
    onTogglePin: (String) -> Unit,
    onUpdateTrainingPlan: (String, String, String) -> Unit,
    onTreatInjury: (String, String) -> Unit,
    onRepairItem: (EquipmentItem) -> Unit,
    onNavigate: (ActiveScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedGlad = state.selectedGladiator ?: state.gladiators.firstOrNull() ?: return
    var activeTab by remember { mutableStateOf(GladiatorProfileTab.OVERVIEW) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // =========================================================
        // PANE 1 (LEFT): DENSE ROSTER MANAGEMENT TABLE
        // =========================================================
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
        ) {
            RomanCard(
                title = "Gladyatör Kadrosu",
                badge = "${state.gladiators.size} Dövüşçü",
                modifier = Modifier.fillMaxSize()
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF191310))
                        .padding(horizontal = 4.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "İSİM & SINIF",
                        color = RomanGold,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.4f)
                    )
                    Text(
                        text = "DURUM",
                        color = RomanParchmentDark,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.9f)
                    )
                    Text(
                        text = "SAĞLIK",
                        color = RomanSuccessGreen,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.7f)
                    )
                    Text(
                        text = "KOND.",
                        color = RomanStaminaCyan,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.7f)
                    )
                }

                HorizontalDivider(color = RomanBronzeDark, thickness = 0.8.dp)

                // Roster Rows
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(state.gladiators) { glad ->
                        val isSelected = glad.id == selectedGlad.id
                        val isPinned = state.pinnedGladiatorIds.contains(glad.id)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) Color(0xFF382319) else Color(0xFF1F1814),
                                    RoundedCornerShape(2.dp)
                                )
                                .border(
                                    0.8.dp,
                                    if (isSelected) RomanGold else Color.Transparent,
                                    RoundedCornerShape(2.dp)
                                )
                                .clickable { onSelectGladiator(glad) }
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Name & Class + Pin
                            Row(
                                modifier = Modifier.weight(1.4f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPinned) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Pin",
                                    tint = if (isPinned) RomanGold else RomanParchmentDark.copy(alpha = 0.4f),
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { onTogglePin(glad.id) }
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Column {
                                    Text(
                                        text = glad.name,
                                        color = if (isSelected) RomanGold else RomanParchment,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = glad.gladiatorClass.title,
                                        color = RomanTextSecondary,
                                        fontSize = 8.sp
                                    )
                                }
                            }

                            // Status badge
                            val hasSevereInjury = glad.injuries.any { it.severity == "Ağır" }
                            Text(
                                text = if (hasSevereInjury) "YARALI" else glad.status.displayName,
                                color = if (hasSevereInjury) RomanDangerRed else RomanSuccessGreen,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(0.9f)
                            )

                            // Health
                            Text(
                                text = "%${glad.condition.health}",
                                color = if (glad.condition.health < 50) RomanDangerRed else RomanSuccessGreen,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(0.7f)
                            )

                            // Stamina / Conditioning
                            Text(
                                text = "%${glad.condition.stamina}",
                                color = RomanStaminaCyan,
                                fontSize = 8.5.sp,
                                modifier = Modifier.weight(0.7f)
                            )
                        }
                    }
                }
            }
        }

        // =========================================================
        // PANE 2 (CENTER): UNIFIED GLADIATOR PROFILE (7 TABS)
        // =========================================================
        Column(
            modifier = Modifier
                .weight(1.8f)
                .fillMaxHeight()
        ) {
            RomanCard(
                title = "Gladyatör Profili: ${selectedGlad.name} (${selectedGlad.nickname.ifBlank { selectedGlad.gladiatorClass.title }})",
                badge = "${selectedGlad.origin.displayName} • ${selectedGlad.age} Yaş",
                modifier = Modifier.fillMaxSize()
            ) {
                // Profile Sub-navigation Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF191310), RoundedCornerShape(3.dp))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    GladiatorProfileTab.values().forEach { tab ->
                        val isTabActive = tab == activeTab
                        Button(
                            onClick = { activeTab = tab },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTabActive) RomanCrimson else Color.Transparent,
                                contentColor = if (isTabActive) RomanGold else RomanParchment
                            ),
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(26.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = tab.title,
                                fontSize = 8.sp,
                                fontWeight = if (isTabActive) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (activeTab) {
                        GladiatorProfileTab.OVERVIEW -> GladiatorOverviewTab(selectedGlad)
                        GladiatorProfileTab.TRAINING -> GladiatorTrainingTab(selectedGlad, onUpdateTrainingPlan)
                        GladiatorProfileTab.EQUIPMENT -> GladiatorEquipmentTab(selectedGlad, state, onRepairItem, onNavigate)
                        GladiatorProfileTab.MEDICAL -> GladiatorMedicalTab(selectedGlad, onTreatInjury)
                        GladiatorProfileTab.CONTRACT -> GladiatorContractTab(selectedGlad)
                        GladiatorProfileTab.RELATIONSHIPS -> GladiatorRelationshipsTab(selectedGlad, state)
                        GladiatorProfileTab.HISTORY -> GladiatorHistoryTab(selectedGlad, state)
                    }
                }
            }
        }

        // =========================================================
        // PANE 3 (RIGHT): CONTEXTUAL ACTION & NEXT FIGHT PANEL
        // =========================================================
        Column(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Next Scheduled Fight Card
            val scheduledBout = state.arenaCalendar.find { it.fighter1Id == selectedGlad.id || it.fighter2Id == selectedGlad.id }
            val nextBoutDay = scheduledBout?.day ?: 17
            val daysUntil = nextBoutDay - state.dominus.dayNumber

            RomanCard(
                title = "Sıradaki Müsabaka",
                badge = if (daysUntil == 0) "BUGÜN!" else if (daysUntil == 1) "YARIN!" else "$daysUntil Gün Kaldı"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = scheduledBout?.venueId?.venueName ?: "Capua Amfitiyatrosu",
                        color = RomanGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rakip: ${if (scheduledBout?.fighter1Id == selectedGlad.id) scheduledBout.fighter2Name else scheduledBout?.fighter1Name ?: "Cassian"}",
                        color = RomanParchment,
                        fontSize = 9.5.sp
                    )
                    Text(
                        text = "Format: ${scheduledBout?.matchType?.title ?: "Ölümcül Düello"}",
                        color = RomanTextSecondary,
                        fontSize = 8.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { onNavigate(ActiveScreen.MATCH_PREP) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (daysUntil <= 1) RomanCrimson else Color(0xFF2E221A),
                            contentColor = RomanGold
                        ),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .border(1.dp, RomanGold, RoundedCornerShape(3.dp)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = if (daysUntil <= 1) "⚔ MAÇ HAZIRLIĞI YAP" else "STRATEJİ BELİRLE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Actions & Smart Recommendations
            RomanCard(
                title = "Hızlı Eylemler & Tavsiyeler",
                badge = "Tavsiyeler"
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val gladLoadout = state.gladiatorLoadouts[selectedGlad.id]
                    val hasDamagedShield = gladLoadout?.shield?.let { it.durability < 40 } ?: false

                    if (hasDamagedShield) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2A1C16), RoundedCornerShape(2.dp))
                                .border(0.6.dp, RomanDangerRed, RoundedCornerShape(2.dp))
                                .padding(4.dp)
                        ) {
                            Column {
                                Text(text = "TAVSİYE: Kalkan Ağır Hasarlı!", color = RomanDangerRed, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Dövüşte kırılma riski yüksek.", color = RomanParchment, fontSize = 8.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Button(
                                    onClick = { gladLoadout?.shield?.let { onRepairItem(it) } },
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson, contentColor = RomanParchment),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.fillMaxWidth().height(24.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Demircide Onar (120 D)", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (selectedGlad.condition.stamina < 40) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E242B), RoundedCornerShape(2.dp))
                                .border(0.6.dp, RomanStaminaCyan, RoundedCornerShape(2.dp))
                                .padding(4.dp)
                        ) {
                            Column {
                                Text(text = "TAVSİYE: Yüksek Yorgunluk", color = RomanStaminaCyan, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Antrenmanı 'Dinlenme' moduna alın.", color = RomanParchment, fontSize = 8.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Button(
                                    onClick = { onUpdateTrainingPlan(selectedGlad.id, "Dinlenme & Masaj", "Hafif Erzak") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C3647), contentColor = RomanParchment),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.fillMaxWidth().height(24.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Hemen Dinlendir", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { onNavigate(ActiveScreen.EQUIPMENT_MARKET) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF261D17), contentColor = RomanGold),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.fillMaxWidth().height(28.dp).border(0.6.dp, RomanBronze, RoundedCornerShape(2.dp)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Pazarda Ekipman Ara", fontSize = 8.5.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 1: OVERVIEW
// -------------------------------------------------------------
@Composable
private fun GladiatorOverviewTab(glad: Gladiator) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        GladiatorSpriteShowcase(gladiator = glad, height = 110.dp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                RomanStatBar(label = "Kuvvet (STR)", current = glad.physicalStats.strength, max = 20, color = RomanDangerRed)
                RomanStatBar(label = "Çeviklik (AGI)", current = glad.physicalStats.agility, max = 20, color = RomanStaminaCyan)
                RomanStatBar(label = "Hız (SPD)", current = glad.physicalStats.speed, max = 20, color = RomanGoldLight)
            }
            Column(modifier = Modifier.weight(1f)) {
                RomanStatBar(label = "Dayanıklılık", current = glad.physicalStats.endurance, max = 20, color = RomanSuccessGreen)
                RomanStatBar(label = "Kılıç Ustalığı", current = glad.attributes.swordsmanship, max = 100, color = RomanGold)
                RomanStatBar(label = "Kalkan & Savunma", current = glad.attributes.shieldSkill, max = 100, color = RomanBronze)
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 2: TRAINING
// -------------------------------------------------------------
@Composable
private fun GladiatorTrainingTab(
    glad: Gladiator,
    onUpdateTrainingPlan: (String, String, String) -> Unit
) {
    var selectedFocus by remember(glad.id) { mutableStateOf("Dövüş Temelleri") }
    var selectedDiet by remember(glad.id) { mutableStateOf("Standart Hububat") }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "ANTRENMAN ODAĞI SEÇİMİ", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        val focuses = listOf("Dövüş Temelleri", "Ağır Kalkan & Savunma", "Kondisyon & Hız", "Ölümcül Vuruş", "Dinlenme & Masaj")

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            focuses.forEach { focus ->
                val isSel = focus == selectedFocus
                Button(
                    onClick = {
                        selectedFocus = focus
                        onUpdateTrainingPlan(glad.id, selectedFocus, selectedDiet)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSel) RomanCrimson else Color(0xFF221814),
                        contentColor = if (isSel) RomanGold else RomanParchment
                    ),
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.weight(1f).height(28.dp).border(0.6.dp, if (isSel) RomanGold else RomanBronzeDark, RoundedCornerShape(2.dp)),
                    contentPadding = PaddingValues(2.dp)
                ) {
                    Text(text = focus, fontSize = 7.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        Text(text = "DİYET & BESLENME PLANI", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        val diets = listOf("Standart Hububat", "Et & Bakliyat (+Kuvvet)", "Zengin Protein (+Yenilenme)")
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            diets.forEach { diet ->
                val isSel = diet == selectedDiet
                Button(
                    onClick = {
                        selectedDiet = diet
                        onUpdateTrainingPlan(glad.id, selectedFocus, selectedDiet)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSel) RomanCrimson else Color(0xFF221814),
                        contentColor = if (isSel) RomanGold else RomanParchment
                    ),
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.weight(1f).height(28.dp).border(0.6.dp, if (isSel) RomanGold else RomanBronzeDark, RoundedCornerShape(2.dp)),
                    contentPadding = PaddingValues(2.dp)
                ) {
                    Text(text = diet, fontSize = 7.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        Text(text = "Aktif Plan: $selectedFocus | Diyet: $selectedDiet", color = RomanSuccessGreen, fontSize = 9.sp)
    }
}

// -------------------------------------------------------------
// SUB-TAB 3: EQUIPMENT
// -------------------------------------------------------------
@Composable
private fun GladiatorEquipmentTab(
    glad: Gladiator,
    state: LudusUiState,
    onRepairItem: (EquipmentItem) -> Unit,
    onNavigate: (ActiveScreen) -> Unit
) {
    val loadout = state.gladiatorLoadouts[glad.id]
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "KUŞANILAN 9-SLOT TEÇHİZAT", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)

        listOf(
            "Ana Silah" to loadout?.mainHand,
            "Kalkan / Yan Silah" to loadout?.offHand,
            "Kask" to loadout?.helmet,
            "Gövde Zırhı" to loadout?.bodyArmor,
            "Bacak Zırhı (Greaves)" to loadout?.greaves
        ).forEach { (slot, item) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF201814), RoundedCornerShape(2.dp))
                    .border(0.6.dp, RomanBronzeDark, RoundedCornerShape(2.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = slot, color = RomanGoldLight, fontSize = 8.sp)
                    Text(text = item?.name ?: "Boş", color = RomanParchment, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                if (item != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Dayanıklılık: %${item.durability}", color = if (item.durability < 40) RomanDangerRed else RomanSuccessGreen, fontSize = 8.sp)
                        if (item.durability < 100) {
                            Button(
                                onClick = { onRepairItem(item) },
                                colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson, contentColor = RomanParchment),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.height(20.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                            ) {
                                Text("Onar", fontSize = 7.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 4: MEDICAL
// -------------------------------------------------------------
@Composable
private fun GladiatorMedicalTab(
    glad: Gladiator,
    onTreatInjury: (String, String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "SAĞLIK & SAKATLIK RAPORU", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text = "Genel Sağlık: %${glad.condition.health} | Yorgunluk: %${100 - glad.condition.stamina}", color = RomanParchment, fontSize = 9.sp)

        if (glad.injuries.isEmpty()) {
            Text(text = "Gladyatörün hiçbir aktif sakatlığı veya açık yarası bulunmuyor. Arenaya çıkmaya tamamen hazır.", color = RomanSuccessGreen, fontSize = 8.5.sp)
        } else {
            glad.injuries.forEach { inj ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF281814), RoundedCornerShape(2.dp))
                        .border(0.6.dp, RomanDangerRed, RoundedCornerShape(2.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = inj.name, color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Ağırlık: ${inj.severity} • İyileşme: ${inj.recoveryDaysLeft} Gün", color = RomanDangerRed, fontSize = 8.sp)
                    }
                    Button(
                        onClick = { onTreatInjury(glad.id, inj.name) },
                        colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson, contentColor = RomanParchment),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.height(22.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text("Medicus Tedavisi", fontSize = 7.5.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 5: CONTRACT & LOYALTY
// -------------------------------------------------------------
@Composable
private fun GladiatorContractTab(glad: Gladiator) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = "HUKUKİ STATÜ & SÖZLEŞME", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text = "Statü: ${glad.status.displayName}", color = RomanParchment, fontSize = 9.sp)
        Text(text = "Maaş / İaşe Payı: Günlük 15 Denarii", color = RomanParchmentDark, fontSize = 8.5.sp)
        Text(text = "Sadakat Seviyesi: %${glad.condition.morale}", color = RomanGoldLight, fontSize = 8.5.sp)
        Text(text = "Özgürlük Rudisi İlerlemesi: %45 (Halkın desteği ve şampiyonluk unvanı gerekiyor)", color = RomanTextSecondary, fontSize = 8.sp)
    }
}

// -------------------------------------------------------------
// SUB-TAB 6: RELATIONSHIPS
// -------------------------------------------------------------
@Composable
private fun GladiatorRelationshipsTab(glad: Gladiator, state: LudusUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "ARENA VE KIŞLA İLİŞKİLERİ", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        state.activeRivalries.forEach { rivalry ->
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF221814)).padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Husumet: ${rivalry.opponentName}", color = RomanCrimson, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                Text(text = "Anlaşmazlık: %${rivalry.animosityScore}", color = RomanGold, fontSize = 8.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 7: HISTORY
// -------------------------------------------------------------
@Composable
private fun GladiatorHistoryTab(glad: Gladiator, state: LudusUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "KARİYER VE SİCİL KAYDI", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text = "Kariyer Özeti: ${glad.recordSummary}", color = RomanGoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text = "Galibiyet: ${glad.wins} | Mağlubiyet: ${glad.losses} | İnfaz (Kills): ${glad.kills} | Bağışlanma: ${glad.spared}", color = RomanParchment, fontSize = 8.5.sp)
        Text(text = "Kazanılan Toplam Altın: ${glad.denariiEarned} Denarii", color = RomanParchmentDark, fontSize = 8.sp)
    }
}
