package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.MainUiState
import com.example.ui.components.*
import com.example.ui.theme.*

enum class MarketCategory(val title: String, val icon: String) {
    SLAVES("Köle Pazarı", "🛒"),
    TEACHERS("Eğitmenler", "🎓"),
    FACILITIES("Tefeci & Tesis", "🏛️")
}

@Composable
fun MarketAndDebtScreen(
    state: MainUiState,
    onRecruitGladiator: (Gladiator) -> Unit,
    onHireTeacher: (Teacher) -> Unit,
    onDismissTeacher: (Long) -> Unit,
    onUpgradePhysician: () -> Unit,
    onHireGuard: () -> Unit,
    onRepayDebt: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(MarketCategory.SLAVES) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBg)
            .padding(horizontal = 16.dp)
            .testTag("market_and_debt_screen")
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Clean Category Selector Tabs
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = ImmersiveCard,
            border = BorderStroke(1.dp, ImmersiveCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MarketCategory.entries.forEach { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedCategory = category }
                            .testTag("market_tab_${category.name.lowercase()}"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) ImmersiveGold else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = category.icon, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) Color.Black else ImmersiveTextSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            when (selectedCategory) {
                MarketCategory.SLAVES -> {
                    // Gladiator Slave & Contract Market
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ALINABİLİR DÖVÜŞÇÜLER",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = ImmersiveGold
                            )
                            Text(
                                text = "Kapasite: ${state.gladiators.size}/${state.ludusState.maxGladiatorSlots}",
                                style = MaterialTheme.typography.labelSmall,
                                color = ImmersiveTextMuted
                            )
                        }
                    }

                    if (state.marketCandidates.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ImmersiveCard,
                                border = BorderStroke(1.dp, ImmersiveCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Pazardaki tüm gladyatörler satın alındı. Ertesi gün yeni adaylar gelecek.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ImmersiveTextMuted
                                )
                            }
                        }
                    } else {
                        items(state.marketCandidates) { candidate ->
                            GladiatorMarketCard(
                                candidate = candidate,
                                canAfford = state.ludusState.gold >= candidate.priceValue,
                                hasSlot = state.gladiators.size < state.ludusState.maxGladiatorSlots,
                                onBuy = { onRecruitGladiator(candidate) }
                            )
                        }
                    }
                }

                MarketCategory.TEACHERS -> {
                    // Active Teachers
                    if (state.ludusState.activeTeachers.isNotEmpty()) {
                        item {
                            Text(
                                text = "MEVCUT KADRODAKİ DOCTORE'LER (${state.ludusState.activeTeachers.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = ImmersiveGoldLight
                            )
                        }
                        items(state.ludusState.activeTeachers, key = { it.id }) { teacher ->
                            TeacherCard(
                                teacher = teacher,
                                onDismiss = { onDismissTeacher(teacher.id) }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    // Available Teachers for Hire
                    item {
                        Text(
                            text = "KİRALANABİLİR UZMAN EĞİTMENLER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = ImmersiveGold
                        )
                    }

                    if (state.availableTeachersForHire.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ImmersiveCard,
                                border = BorderStroke(1.dp, ImmersiveCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Tüm uzman eğitmenler kiralandı. Yaşlanan şampiyon gladyatörlerinizi Doctore'ye terfi ettirebilirsiniz.",
                                    modifier = Modifier.padding(14.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ImmersiveTextMuted
                                )
                            }
                        }
                    } else {
                        items(state.availableTeachersForHire, key = { it.id }) { teacher ->
                            val canAfford = state.ludusState.gold >= teacher.hireCost
                            TeacherCard(
                                teacher = teacher,
                                canAfford = canAfford,
                                onHire = { onHireTeacher(teacher) }
                            )
                        }
                    }
                }

                MarketCategory.FACILITIES -> {
                    // Usurer & Threat Crisis Panel
                    item {
                        UsurerCrisisPanel(
                            ludusState = state.ludusState,
                            onRepayDebt = onRepayDebt
                        )
                    }

                    // Physician Clinic Upgrades
                    item {
                        PhysicianUpgradeCard(
                            currentLevel = state.ludusState.physicianLevel,
                            playerGold = state.ludusState.gold,
                            onUpgrade = onUpgradePhysician
                        )
                    }

                    // Guard Garrison Upgrades
                    item {
                        GuardGarrisonCard(
                            guardsHired = state.ludusState.guardsHired,
                            maxGuards = state.ludusState.maxGuards,
                            physicianLevel = state.ludusState.physicianLevel,
                            playerGold = state.ludusState.gold,
                            onHireGuard = onHireGuard
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UsurerCrisisPanel(
    ludusState: LudusState,
    onRepayDebt: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasDebt = ludusState.activeDebt > 0
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasDebt) ImmersiveTerracotta.copy(alpha = 0.15f) else ImmersiveCard
        ),
        border = BorderStroke(1.2.dp, if (hasDebt) ImmersiveTerracotta else ImmersiveCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚖️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TEFECİ (FAENERATOR) & BORÇ",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                        color = ImmersiveGold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (hasDebt) ImmersiveTerracotta else ImmersiveEmerald.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, if (hasDebt) ImmersiveTerracotta else ImmersiveEmerald)
                ) {
                    Text(
                        text = if (hasDebt) "Borç Aktif" else "Temiz Bakiye",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (hasDebt) Color.White else ImmersiveEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (hasDebt) {
                Text(
                    text = "Tefeciye toplam borcunuz: ${ludusState.activeDebt} Altın. Günde faiz işler. Ödenmezse sızma ve sabotaj tehdidi artar!",
                    style = MaterialTheme.typography.bodySmall,
                    color = ImmersiveTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onRepayDebt(50) },
                        enabled = ludusState.gold >= 50 && ludusState.activeDebt >= 50,
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("50 🪙 Öde", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                    Button(
                        onClick = { onRepayDebt(ludusState.activeDebt) },
                        enabled = ludusState.gold >= ludusState.activeDebt,
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveEmerald, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tümünü Kapat", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            } else {
                Text(
                    text = "Şu anda tefeciye borcunuz bulunmuyor. Tehdit seviyesi asgari düzeyde.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ImmersiveTextMuted
                )
            }
        }
    }
}

@Composable
fun PhysicianUpgradeCard(
    currentLevel: Int,
    playerGold: Int,
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier
) {
    val maxLevel = 3
    val isMax = currentLevel >= maxLevel
    val upgradeCost = when (currentLevel) {
        1 -> 120
        2 -> 250
        else -> 0
    }
    val canAfford = playerGold >= upgradeCost

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
        border = BorderStroke(1.dp, ImmersiveCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🩺", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HEKİM ÇADIRI (REVİR) GELİŞTİRME",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveGold
                    )
                }
                Text(
                    text = "Lv $currentLevel / $maxLevel",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ImmersiveSta
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hekiminizi yükselterek gladyatörlerin yaralanma sonrası iyileşme süresini hızlandırın ve uzuv kaybı riskini asgari düzeye indirin.",
                style = MaterialTheme.typography.bodySmall,
                color = ImmersiveTextMuted
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onUpgrade,
                enabled = !isMax && canAfford,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upgrade_physician_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImmersiveGold,
                    contentColor = Color.Black,
                    disabledContainerColor = ImmersiveCardBorder
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isMax) "Maksimum Hekim Seviyesi" else "Hekimi Yükselt ($upgradeCost 🪙)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun GuardGarrisonCard(
    guardsHired: Int,
    maxGuards: Int,
    physicianLevel: Int,
    playerGold: Int,
    onHireGuard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMax = guardsHired >= maxGuards
    val hireCost = 45
    val canAfford = playerGold >= hireCost
    val detectionChance = (guardsHired * 30 + physicianLevel * 10).coerceAtMost(90)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
        border = BorderStroke(1.dp, ImmersiveCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🛡️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MUHAFIZ BİRLİĞİ (GÜVENLİK)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveGold
                    )
                }
                Text(
                    text = "$guardsHired / $maxGuards Muhafız",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ImmersiveSta
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Muhafızlar rakip ludus casuslarını, zehirleme girişimlerini ve tefeci haydutlarını engeller. Mevcut Engelleme Oranı: %$detectionChance.",
                style = MaterialTheme.typography.bodySmall,
                color = ImmersiveTextMuted
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onHireGuard,
                enabled = !isMax && canAfford,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hire_guard_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImmersiveGold,
                    contentColor = Color.Black,
                    disabledContainerColor = ImmersiveCardBorder
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isMax) "Maksimum Muhafız Kapasitesi" else "Muhafız Kirala ($hireCost 🪙)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
