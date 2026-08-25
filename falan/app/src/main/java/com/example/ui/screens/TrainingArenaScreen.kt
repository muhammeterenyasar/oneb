package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.MainUiState
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun TrainingArenaScreen(
    state: MainUiState,
    onSelectGladiator: (Gladiator) -> Unit,
    onAssignTrainingFocus: (Long, TrainingType) -> Unit,
    onAssignSpecificDrill: (Long, SpecificDrill) -> Unit = { _, _ -> },
    onBulkAssignDrill: (SpecificDrill) -> Unit = {},
    onExecuteDrillNow: (Long) -> Unit = {},
    onExecuteAllDrillsNow: () -> Unit = {},
    onSelectDrillCategory: (DrillCategory) -> Unit = {},
    onDismissDrillOutcome: () -> Unit = {},
    onOpenSparring: () -> Unit,
    onManumitGladiator: (Gladiator) -> Unit,
    onPromoteToTeacher: (Gladiator) -> Unit,
    onTogglePromiseOfFreedom: (Long, Boolean) -> Unit,
    onInstantHealClick: (Long) -> Unit,
    onNavigateToMarket: () -> Unit,
    onNavigateToPhysician: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val selectedGladiator = state.selectedGladiator ?: state.gladiators.firstOrNull()
    var selectedCategory by remember { mutableStateOf(DrillCategory.STRENGTH) }
    var showManumitConfirmDialog by remember { mutableStateOf(false) }
    var showPromoteConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBg)
            .padding(horizontal = 16.dp)
            .testTag("training_arena_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp)
    ) {
        // 1. Training Arena Header & Downtime Drills Overview
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                border = BorderStroke(1.2.dp, ImmersiveGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ImmersiveCardBgSecondary,
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🏛️", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "TRAINING ARENA",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.5.sp
                                    ),
                                    color = ImmersiveGold
                                )
                                Text(
                                    text = "Özel Tatbikatlar & Gelişim Takibi (Downtime Drills)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ImmersiveTextMuted
                                )
                            }
                        }

                        // Sparring Action Quick Button
                        Button(
                            onClick = onOpenSparring,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ImmersiveGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("start_sparring_button")
                        ) {
                            Text(
                                text = "⚔️ Sparring",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = ImmersiveCardBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Training Atmosphere and Doctores info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Aktif Eğitmenler",
                                style = MaterialTheme.typography.labelSmall,
                                color = ImmersiveTextMuted
                            )
                            Text(
                                text = "${state.ludusState.activeTeachers.size} Doctore",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveGoldLight
                            )
                        }
                        Column {
                            Text(
                                text = "Beslenme Düzeni",
                                style = MaterialTheme.typography.labelSmall,
                                color = ImmersiveTextMuted
                            )
                            Text(
                                text = state.ludusState.dietPlan.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveEmerald
                            )
                        }
                        Column {
                            Text(
                                text = "Kadro Sayısı",
                                style = MaterialTheme.typography.labelSmall,
                                color = ImmersiveTextMuted
                            )
                            Text(
                                text = "${state.gladiators.size} Gladyatör",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveSta
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Global Bulk Actions Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onExecuteAllDrillsNow,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("execute_all_drills_button"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ImmersiveGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ImmersiveGold),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text(
                                text = "⚡ Tüm Kadroyu Çalıştır",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToMarket,
                            modifier = Modifier.testTag("hire_coach_button"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ImmersiveCardBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ImmersiveTextSecondary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🎓 Eğitmen",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToPhysician,
                            modifier = Modifier.testTag("go_to_physician_button"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF81C784)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🩺 Hekim",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToCalendar,
                            modifier = Modifier.testTag("go_to_calendar_button"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ImmersiveGold),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🗓️ Takvim",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // Live Drill Outcome Banner (if executed)
        if (state.lastDrillExecutionOutcome != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ImmersiveCardBgSecondary,
                    border = BorderStroke(1.2.dp, ImmersiveEmerald),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("drill_outcome_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🏆", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "TATBİKAT TAMAMLANDI!",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = ImmersiveEmerald
                                )
                                Text(
                                    text = state.lastDrillExecutionOutcome,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ImmersiveTextPrimary
                                )
                            }
                        }
                        IconButton(onClick = onDismissDrillOutcome) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Kapat",
                                tint = ImmersiveTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Gladiator Carousel Selector
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GLADYATÖR SEÇİMİ & PROFİL",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = ImmersiveGold
                    )
                    Text(
                        text = "${state.gladiators.size} Dövüşçü",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ImmersiveTextMuted
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.gladiators, key = { it.id }) { g ->
                        val isSelected = g.id == selectedGladiator?.id
                        Surface(
                            modifier = Modifier
                                .clickable { onSelectGladiator(g) }
                                .testTag("select_gladiator_${g.id}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ImmersiveCardBgSecondary else ImmersiveCard,
                            border = BorderStroke(1.2.dp, if (isSelected) ImmersiveGold else ImmersiveCardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = g.gladiatorClass.icon,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = g.name,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) ImmersiveGold else ImmersiveTextPrimary
                                    )
                                    if (g.isInjured) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "🩸", fontSize = 11.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${g.assignedDrill.icon} ${g.assignedDrill.title}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = if (isSelected) ImmersiveGoldLight else ImmersiveTextMuted,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedGladiator != null) {
            // 3. Main Gladiator Hero Profile Card
            item {
                GladiatorHeroCard(gladiator = selectedGladiator)
            }

            // 4. Drill Mastery & XP Progress Tracking Dashboard
            item {
                DrillMasteryAndProgressCard(
                    gladiator = selectedGladiator,
                    onExecuteDrillNow = { onExecuteDrillNow(selectedGladiator.id) }
                )
            }

            // 5. Specific Drill Categories Filter Tabs (Strength / Agility / Stamina / Recovery)
            item {
                Column {
                    Text(
                        text = "TATBİKAT SEÇİMİ (DRILL ASSIGNMENT)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = ImmersiveGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(DrillCategory.entries) { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(cat.colorHex).copy(alpha = 0.25f) else ImmersiveCard,
                                border = BorderStroke(
                                    1.2.dp,
                                    if (isSelected) Color(cat.colorHex) else ImmersiveCardBorder
                                ),
                                modifier = Modifier
                                    .clickable {
                                        selectedCategory = cat
                                        onSelectDrillCategory(cat)
                                    }
                                    .testTag("drill_tab_${cat.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = cat.icon, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cat.shortTitle,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                            fontSize = 12.sp
                                        ),
                                        color = if (isSelected) Color(cat.colorHex) else ImmersiveTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Drill List for Selected Category
            val drillsInCurrentCategory = SpecificDrill.entries.filter { it.category == selectedCategory }
            items(drillsInCurrentCategory, key = { it.id }) { drill ->
                val isAssigned = selectedGladiator.assignedDrill == drill
                DrillCard(
                    drill = drill,
                    isAssigned = isAssigned,
                    onAssign = {
                        onAssignSpecificDrill(selectedGladiator.id, drill)
                        onAssignTrainingFocus(selectedGladiator.id, drill.toLegacyTrainingType())
                    },
                    onBulkAssign = {
                        onBulkAssignDrill(drill)
                    }
                )
            }

            // 7. Attributes & Condition Breakdown
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                    border = BorderStroke(1.dp, ImmersiveCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NİTELİKLER & KONDİSYON",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveGold
                            )
                            FatigueIndicator(fatigue = selectedGladiator.fatigue)
                        }

                        GladiatorStatRow(
                            label = "GÜÇ (STR)",
                            value = selectedGladiator.str,
                            color = ImmersiveStr,
                            icon = Icons.Default.FitnessCenter
                        )

                        GladiatorStatRow(
                            label = "ÇEVİKLİK (AGI)",
                            value = selectedGladiator.agi,
                            color = ImmersiveAgi,
                            icon = Icons.Default.DirectionsRun
                        )

                        GladiatorStatRow(
                            label = "DAYANIKLILIK (STA)",
                            value = selectedGladiator.sta,
                            color = ImmersiveSta,
                            icon = Icons.Default.Shield
                        )

                        GladiatorStatRow(
                            label = "MORAL (MOR)",
                            value = selectedGladiator.mor,
                            maxValue = 100,
                            color = ImmersiveMor,
                            icon = Icons.Default.Favorite
                        )
                    }
                }
            }

            // 8. Medical Recovery & Herbal Treatment Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                    border = BorderStroke(1.dp, if (selectedGladiator.isInjured) ImmersiveTerracotta.copy(alpha = 0.5f) else ImmersiveCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SAĞLIK & REVİR DURUMU",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveGold
                            )
                            InjuryChip(gladiator = selectedGladiator)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Can: ${selectedGladiator.currentHp} / ${selectedGladiator.maxHp} HP",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ImmersiveTextPrimary
                            )
                            Text(
                                text = "Savaş Güç Puanı: ${selectedGladiator.totalPowerScore}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveGold
                            )
                        }

                        if (selectedGladiator.hasDisabledLimb) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ImmersiveTerracotta.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, ImmersiveTerracotta)
                            ) {
                                Text(
                                    text = "⚠️ ${selectedGladiator.disabledLimbDesc ?: "İşlevsiz Uzuv"}: Kalıcı stat cezası taşımaktadır.",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ImmersiveTerracottaLight
                                )
                            }
                        }

                        if (selectedGladiator.isInjured) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onInstantHealClick(selectedGladiator.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("heal_potion_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ImmersiveEmerald.copy(alpha = 0.3f),
                                    contentColor = ImmersiveEmerald
                                ),
                                border = BorderStroke(1.dp, ImmersiveEmerald),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "🧪 Şifalı İksir ile Anında İyileştir",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }

            // 9. Promotion to Doctore Card (if eligible)
            if (selectedGladiator.canPromoteToTeacher) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ImmersiveCardBgSecondary),
                        border = BorderStroke(1.2.dp, ImmersiveGold)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👑", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "DOCTORE / EĞİTMENLİK TERFİSİ",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                                    color = ImmersiveGold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${selectedGladiator.name} arena tecrübesi (${selectedGladiator.wins} Galibiyet, ${selectedGladiator.age} Yaş) ile efsane mertebesine ulaştı. Onu arenadan emekli ederek Başeğitmen (Doctore) yapabilirsiniz.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ImmersiveTextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showPromoteConfirmDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("promote_gladiator_to_teacher_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ImmersiveGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "👑 ${selectedGladiator.name}'ı Doctore (Eğitmen) Yap",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
                                )
                            }
                        }
                    }
                }
            }

            // 10. Azad (Manumission) Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                    border = BorderStroke(1.2.dp, ImmersiveGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🕊️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AZAD & ÖZGÜRLÜK MEKANİĞİ",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                                color = ImmersiveGold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (selectedGladiator.contractType == GladiatorContractType.SLAVE) {
                                "Köle gladyatörünüzü arenaya çıkmadan önce veya doğrudan azad edebilirsiniz. Azad edilen gladyatör ludus'unuza devasa Prestij ve halk desteği fonu bırakır."
                            } else {
                                "Sözleşmeli gladyatör aylık maaş alır. Sözleşmesi sona erdiğinde dilediğinizde uzatabilir veya yollarınızı ayırabilirsiniz."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = ImmersiveTextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (selectedGladiator.contractType == GladiatorContractType.SLAVE) {
                            Button(
                                onClick = { showManumitConfirmDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("manumit_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = ImmersiveTerracotta, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "🕊️ ${selectedGladiator.name}'ı Şimdi Azad Et (+Prestij & Fon)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Promotion to Teacher
    if (showPromoteConfirmDialog && selectedGladiator != null) {
        val estimatedPrestige = 45 + (selectedGladiator.wins * 15)

        AlertDialog(
            onDismissRequest = { showPromoteConfirmDialog = false },
            title = {
                Text(
                    text = "Doctore (Eğitmen) Terfisi",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ImmersiveGold
                )
            },
            text = {
                Column {
                    Text(
                        text = "${selectedGladiator.name} arenadan onurlu bir şekilde emekli olacak ve okulun daimi Başeğitmeni (Doctore) olacaktır.",
                        color = ImmersiveTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Kazanımlar:\n• +$estimatedPrestige Prestij Puanı\n• Tüm okul gladyatörlerine kalıcı antrenman bonusu\n• Genç dövüşçülere moral ve tecrübe aktarımı",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveEmerald
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPromoteConfirmDialog = false
                        onPromoteToTeacher(selectedGladiator)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold, contentColor = Color.Black)
                ) {
                    Text("Terfiyi Onayla")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPromoteConfirmDialog = false }) {
                    Text("Vazgeç", color = ImmersiveTextMuted)
                }
            },
            containerColor = ImmersiveCard,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Confirmation Dialog for Manumission
    if (showManumitConfirmDialog && selectedGladiator != null) {
        val estimatedPrestige = 40 + (selectedGladiator.wins * 15) + (selectedGladiator.totalPowerScore / 4)
        val estimatedGold = 80 + (selectedGladiator.wins * 25)

        AlertDialog(
            onDismissRequest = { showManumitConfirmDialog = false },
            title = {
                Text(
                    text = "Gladyatörü Azad Et (Özgür Bırak)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ImmersiveGold
                )
            },
            text = {
                Column {
                    Text(
                        text = "${selectedGladiator.name} özgür bir Roma vatandaşı olacak ve okulunuzdan ayrılacaktır.",
                        color = ImmersiveTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Kazanılacak Ödüller:\n• +$estimatedPrestige Prestij Puanı\n• +$estimatedGold Altın Şükran Fonu",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveEmerald
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showManumitConfirmDialog = false
                        onManumitGladiator(selectedGladiator)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveTerracotta)
                ) {
                    Text("Onayla ve Azad Et", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManumitConfirmDialog = false }) {
                    Text("Vazgeç", color = ImmersiveTextMuted)
                }
            },
            containerColor = ImmersiveCard,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun DrillMasteryAndProgressCard(
    gladiator: Gladiator,
    onExecuteDrillNow: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("drill_mastery_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
        border = BorderStroke(1.2.dp, ImmersiveGold)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Mastery Title & Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🥋", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TATBİKAT GELİŞİMİ & KADEME",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                            color = ImmersiveGold
                        )
                    }
                    Text(
                        text = gladiator.drillMasteryTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ImmersiveCardBgSecondary,
                    border = BorderStroke(1.dp, ImmersiveCardBorder)
                ) {
                    Text(
                        text = "${gladiator.drillsCompletedCount} Tatbikat",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveGoldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 Stat Progress Bars (Strength, Agility, Stamina)
            StatXpProgressBar(
                statName = "GÜÇ (STR)",
                currentStat = gladiator.str,
                currentXp = gladiator.strXpProgress,
                xpColor = ImmersiveStr,
                icon = "💪"
            )

            Spacer(modifier = Modifier.height(8.dp))

            StatXpProgressBar(
                statName = "ÇEVİKLİK (AGI)",
                currentStat = gladiator.agi,
                currentXp = gladiator.agiXpProgress,
                xpColor = ImmersiveAgi,
                icon = "⚡"
            )

            Spacer(modifier = Modifier.height(8.dp))

            StatXpProgressBar(
                statName = "KONDİSYON (STA)",
                currentStat = gladiator.sta,
                currentXp = gladiator.staXpProgress,
                xpColor = ImmersiveSta,
                icon = "🛡️"
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = ImmersiveCardBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Assigned Drill Banner & Execute Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ATANAN GÜNCEL TATBİKAT",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = ImmersiveTextMuted
                    )
                    Text(
                        text = "${gladiator.assignedDrill.icon} ${gladiator.assignedDrill.title}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                        color = ImmersiveGold
                    )
                    Text(
                        text = "Ödül: ${gladiator.assignedDrill.rewardBadge} (${gladiator.assignedDrill.fatigueCost} Yorgunluk)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ImmersiveTextSecondary
                    )
                }

                val isHamam = gladiator.assignedDrill == com.example.model.SpecificDrill.THERMAE_MASSAGE
                val isBlockedFromTraining = (!isHamam && (gladiator.hasTrainedToday || gladiator.isInjured || gladiator.fatigue >= 100))

                val buttonText = when {
                    gladiator.hasTrainedToday && !isHamam -> "✅ İdman Yapıldı (Dinleniyor)"
                    gladiator.isInjured && !isHamam -> "🩹 Sakat (Revirde)"
                    gladiator.fatigue >= 100 && !isHamam -> "💤 Bitkin (%100)"
                    isHamam -> "🏛️ Hamama Git"
                    else -> "▶ İdmanı Yap"
                }

                Button(
                    onClick = onExecuteDrillNow,
                    enabled = !isBlockedFromTraining,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHamam) ImmersiveGold else ImperialRomanRed,
                        contentColor = Color.White,
                        disabledContainerColor = ImmersiveTrack,
                        disabledContentColor = ImmersiveTextMuted
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("execute_drill_now_button")
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                    )
                }
            }
        }
    }
}


@Composable
fun StatXpProgressBar(
    statName: String,
    currentStat: Int,
    currentXp: Int,
    xpColor: Color,
    icon: String
) {
    val progress = (currentXp.toFloat() / 100f).coerceIn(0f, 1f)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = statName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ImmersiveTextPrimary
                )
            }
            Text(
                text = "$currentStat ($currentXp/100 XP)",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = xpColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = xpColor,
            trackColor = ImmersiveTrack
        )
    }
}

@Composable
fun DrillCard(
    drill: SpecificDrill,
    isAssigned: Boolean,
    onAssign: () -> Unit,
    onBulkAssign: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("drill_card_${drill.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAssigned) ImmersiveCardBgSecondary else ImmersiveCard
        ),
        border = BorderStroke(
            1.2.dp,
            if (isAssigned) ImmersiveGold else ImmersiveCardBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Text(text = drill.icon, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = drill.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isAssigned) ImmersiveGold else ImmersiveTextPrimary
                        )
                        Text(
                            text = drill.latinName,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = ImmersiveTextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isAssigned) ImmersiveGold else ImmersiveTrack,
                    border = BorderStroke(1.dp, if (isAssigned) ImmersiveGold else ImmersiveCardBorder)
                ) {
                    Text(
                        text = drill.rewardBadge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = if (isAssigned) Color.Black else ImmersiveEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = drill.description,
                style = MaterialTheme.typography.bodySmall,
                color = ImmersiveTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Hedef: ${drill.targetStat}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = ImmersiveGoldLight
                    )
                    Text(
                        text = if (drill.fatigueCost >= 0) "Yorgunluk: +${drill.fatigueCost}" else "Dinlenme: ${drill.fatigueCost}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = if (drill.fatigueCost > 20) ImmersiveTerracottaLight else ImmersiveTextMuted
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onBulkAssign,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, ImmersiveCardBorder),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("bulk_assign_${drill.id}")
                    ) {
                        Text(
                            text = "Tüm Kadro",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = ImmersiveTextSecondary
                        )
                    }

                    Button(
                        onClick = onAssign,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAssigned) ImmersiveGold else ImmersiveCardBgSecondary,
                            contentColor = if (isAssigned) Color.Black else ImmersiveGold
                        ),
                        border = BorderStroke(1.dp, ImmersiveGold),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("assign_drill_${drill.id}")
                    ) {
                        Text(
                            text = if (isAssigned) "✓ Atandı" else "Ata",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
