package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.ui.theme.*
import kotlin.math.ceil

enum class InfirmaryFilter(val title: String, val icon: String) {
    INJURED("Sakatlar & Yaralılar", "🩹"),
    ALL("Tüm Kadro", "👥"),
    FATIGUED("Yorgunlar", "💤")
}

@Composable
fun PhysicianTentScreen(
    state: MainUiState,
    onUpgradePhysician: () -> Unit,
    onApplyHerbalPoultice: (Long) -> Unit,
    onApplyThermalBath: (Long) -> Unit,
    onPerformSurgery: (Long) -> Unit,
    onInstantHealWithGold: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(InfirmaryFilter.INJURED) }
    val ludusState = state.ludusState
    val physicianLevel = ludusState.physicianLevel
    val playerGold = ludusState.gold

    val injuredGladiators = state.gladiators.filter { it.isInjured || it.hasDisabledLimb || it.currentHp < it.maxHp }
    val fatiguedGladiators = state.gladiators.filter { it.fatigue >= 30 }

    val displayedGladiators = when (selectedFilter) {
        InfirmaryFilter.INJURED -> if (injuredGladiators.isNotEmpty()) injuredGladiators else state.gladiators
        InfirmaryFilter.ALL -> state.gladiators
        InfirmaryFilter.FATIGUED -> if (fatiguedGladiators.isNotEmpty()) fatiguedGladiators else state.gladiators
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBg)
            .padding(horizontal = 16.dp)
            .testTag("physician_tent_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // --- 1. HERO HEADER BANNER ---
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ImmersiveCard,
                border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f)),
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2E7D32).copy(alpha = 0.25f),
                                    ImmersiveCard
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("⚕️", fontSize = 18.sp)
                                    }
                                }
                                Column {
                                    Text(
                                        text = "HEKİM ÇADIRI (VALETUDINARIUM)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        ),
                                        color = ImmersiveGold
                                    )
                                    Text(
                                        text = "Gladyatör Şifahanesi & Sakatlık İyileşme Merkezi",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = ImmersiveTextMuted
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ImmersiveGold.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "$playerGold 🪙",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = ImmersiveGold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        // Summary Indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (injuredGladiators.isNotEmpty()) Color(0xFFE57373).copy(alpha = 0.15f) else Color(0xFF81C784).copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, if (injuredGladiators.isNotEmpty()) Color(0xFFE57373) else Color(0xFF81C784)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (injuredGladiators.isNotEmpty()) "${injuredGladiators.size} Yaralı / Hasta" else "Tüm Kadro Sağlıklı",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (injuredGladiators.isNotEmpty()) Color(0xFFEF5350) else Color(0xFF81C784)
                                    )
                                    Text(
                                        text = if (injuredGladiators.isNotEmpty()) "Tedavi Bekliyor" else "Arenaya Hazır",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                        color = ImmersiveTextMuted
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = ImmersiveGold.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.4f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Hekim Seviyesi $physicianLevel / 3",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ImmersiveGold
                                    )
                                    Text(
                                        text = "Günlük Gider: ${physicianLevel * 8} 🪙",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                        color = ImmersiveTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 2. PHYSICIAN STATUS & PROMOTION CARD ---
        item {
            val doctorName = when (physicianLevel) {
                1 -> "Şifacı Çırağı (Tiro Medicus)"
                2 -> "Roma Lejyon Cerrahı (Chirurgus Legionis)"
                3 -> "Yunan Başhekimi & Filozof (Archiater Graecus)"
                else -> "Başhekim"
            }
            val doctorDesc = when (physicianLevel) {
                1 -> "Basit sargı bezi ve tuzlu suyla temel müdahale yapar. İyileşme yavaştır ve ağır darbelerde gladyatörlerin kalıcı stat kaybetme riski %40'tır."
                2 -> "Savaş meydanlarında uzuv dikmiş deneyimli hekim. Kırıkları ve derin yaraları düzenli sarar. Kalıcı stat kaybı riski %15'e düşer, cerrahi ameliyat yapabilir."
                3 -> "Galen ve Hipokrat tıbbı uzmanı. Antik bitkisel iksirler ve kusursuz cerrahiyle kalıcı stat kaybını tamamen önler (%0 Risk), her gece 2x hızla iyileştirir."
                else -> ""
            }
            val upgradeCost = when (physicianLevel) {
                1 -> 250
                2 -> 600
                else -> 0
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ImmersiveCard,
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
                        Column {
                            Text(
                                text = "LUDUS HEKİMİ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = ImmersiveTextMuted
                            )
                            Text(
                                text = doctorName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveGoldLight
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF4CAF50))
                        ) {
                            Text(
                                text = "Kademe $physicianLevel",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF81C784),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = doctorDesc,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                        color = ImmersiveTextPrimary
                    )

                    if (physicianLevel < 3) {
                        Button(
                            onClick = onUpgradePhysician,
                            enabled = playerGold >= upgradeCost,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upgrade_physician_tent_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Hekimi Terfi Ettir (Kademe ${physicianLevel + 1}) - $upgradeCost 🪙",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ImmersiveGold.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, ImmersiveGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "👑 En Üst Düzey Başhekim Kadrosu Aktif",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                color = ImmersiveGold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- 3. RECOVERY SPEED & PERMANENT STAT LOSS RISK MATRIX ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔬 HEKİM SEVİYESİ ETKİ & RİSK ANALİZİ",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        ),
                        color = ImmersiveGold
                    )
                    Text(
                        text = "Seviye Karşılaştırması",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = ImmersiveTextMuted
                    )
                }

                // Comparative Cards for Tier 1, 2, 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PhysicianTierComparisonCard(
                        tier = 1,
                        title = "Çırak Şifacı",
                        isActive = physicianLevel == 1,
                        speedText = "0.5x Gün/Gece",
                        speedDesc = "%50 Şansla -1 Gün",
                        statLossPercent = 40,
                        statLossSeverity = "-4 STR / -4 AGI",
                        survivalRate = "%50 Şans",
                        downtimeText = "6 Gün Temel",
                        modifier = Modifier.weight(1f)
                    )
                    PhysicianTierComparisonCard(
                        tier = 2,
                        title = "Lejyon Cerrahı",
                        isActive = physicianLevel == 2,
                        speedText = "1.0x Gün/Gece",
                        speedDesc = "Her Gece Garanti -1 Gün",
                        statLossPercent = 15,
                        statLossSeverity = "-2 STR / -2 AGI",
                        survivalRate = "%80 Şans",
                        downtimeText = "4 Gün Temel",
                        modifier = Modifier.weight(1f)
                    )
                    PhysicianTierComparisonCard(
                        tier = 3,
                        title = "Yunan Başhekimi",
                        isActive = physicianLevel == 3,
                        speedText = "2.0x Çift Hız!",
                        speedDesc = "Her Gece -2 Gün Hızlı",
                        statLossPercent = 0,
                        statLossSeverity = "Sıfır Hasar Kaybı",
                        survivalRate = "%98 Şans",
                        downtimeText = "2 Gün Temel",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Active Risk Summary Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when (physicianLevel) {
                        3 -> Color(0xFF2E7D32).copy(alpha = 0.2f)
                        2 -> Color(0xFFF57C00).copy(alpha = 0.2f)
                        else -> Color(0xFFC62828).copy(alpha = 0.2f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when (physicianLevel) {
                            3 -> Color(0xFF81C784)
                            2 -> Color(0xFFFFB74D)
                            else -> Color(0xFFEF5350)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (physicianLevel) {
                                3 -> "🛡️"
                                2 -> "⚖️"
                                else -> "⚠️"
                            },
                            fontSize = 22.sp
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = when (physicianLevel) {
                                    3 -> "Tam Koruma: Kalıcı Stat Kaybı Riski %0 (Sıfır Risk)"
                                    2 -> "Orta Koruma: Kalıcı Stat Kaybı Riski %15"
                                    else -> "Yüksek Tehlike: Kalıcı Stat Kaybı Riski %40!"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = when (physicianLevel) {
                                    3 -> Color(0xFF81C784)
                                    2 -> Color(0xFFFFB74D)
                                    else -> Color(0xFFEF5350)
                                }
                            )
                            Text(
                                text = when (physicianLevel) {
                                    3 -> "Gladyatörleriniz ağır darbeler alsa bile şifalı bitkiler ve ileri cerrahi sayesinde güç ve hız kaybetmez."
                                    2 -> "Ağır yaralanan gladyatörlerin %15 ihtimalle -2 STR ve -2 AGI kaybetme riski mevcuttur."
                                    else -> "Dikkat! Seviye 1 hekimle ağır sakatlanan savaşçıların %40 ihtimalle -4 STR ve -4 AGI kaybetme riski vardır. Hekiminizi terfi ettirmeniz önerilir!"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ImmersiveTextPrimary
                            )
                        }
                    }
                }
            }
        }

        // --- 4. PATIENT FILTER CHIPS ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfirmaryFilter.entries.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    val count = when (filter) {
                        InfirmaryFilter.INJURED -> injuredGladiators.size
                        InfirmaryFilter.ALL -> state.gladiators.size
                        InfirmaryFilter.FATIGUED -> fatiguedGladiators.size
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) ImmersiveGold.copy(alpha = 0.2f) else ImmersiveCard,
                        border = BorderStroke(1.dp, if (isSelected) ImmersiveGold else ImmersiveCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedFilter = filter }
                            .testTag("filter_infirmary_${filter.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = filter.icon, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${filter.title} ($count)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 10.sp
                                ),
                                color = if (isSelected) ImmersiveGold else ImmersiveTextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // --- 5. PATIENTS ROSTER & TREATMENT CARDS ---
        if (displayedGladiators.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ImmersiveCard,
                    border = BorderStroke(1.dp, ImmersiveCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✨", fontSize = 32.sp)
                        Text(
                            text = "Bu Kriterde Gladyatör Bulunmuyor",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveGoldLight
                        )
                        Text(
                            text = "Tüm savaşçılar sağlıklı ve formda.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ImmersiveTextMuted
                        )
                    }
                }
            }
        } else {
            items(displayedGladiators, key = { it.id }) { gladiator ->
                GladiatorInfirmaryCard(
                    gladiator = gladiator,
                    physicianLevel = physicianLevel,
                    playerGold = playerGold,
                    onApplyHerbalPoultice = { onApplyHerbalPoultice(gladiator.id) },
                    onApplyThermalBath = { onApplyThermalBath(gladiator.id) },
                    onPerformSurgery = { onPerformSurgery(gladiator.id) },
                    onInstantHealWithGold = { onInstantHealWithGold(gladiator.id) }
                )
            }
        }

        // --- 6. ROMAN MEDICINE & HERBAL APOTHECARY LORE ---
        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ImmersiveCard.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, ImmersiveCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏺", fontSize = 16.sp)
                        Text(
                            text = "Antik Roma Tıp ve Ecza Rehberi",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveGold
                        )
                    }
                    Text(
                        text = "• Posca ve Bal Sargısı: Gladyatörlerin açık kesiklerini sirke, su ve Mısır balı ile dezenfekte ederek iltihabı önler.\n• Termal Kükürt Havuzu: Şiddetli idman sonrası kas spazmlarını ve yorgunluğu sıfırlar.\n• Arpa Külü İçeceği: Kırık kemiklerin ve tendonların kaynamasını iki kat hızlandırır.\n• Yunan Cerrahi Teknikleri: Seviye 3 Başhekim nezaretinde yapılan müdahaleler kalıcı stat kaybını sıfırlar.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 15.sp),
                        color = ImmersiveTextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun PhysicianTierComparisonCard(
    tier: Int,
    title: String,
    isActive: Boolean,
    speedText: String,
    speedDesc: String,
    statLossPercent: Int,
    statLossSeverity: String,
    survivalRate: String,
    downtimeText: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isActive) ImmersiveCardBgSecondary else ImmersiveCard,
        border = BorderStroke(
            if (isActive) 1.5.dp else 1.dp,
            if (isActive) ImmersiveGold else ImmersiveCardBorder
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isActive) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ImmersiveGold,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "MEVCUT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        ),
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            } else {
                Text(
                    text = "Kademe $tier",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    color = ImmersiveTextMuted
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center
                ),
                color = if (isActive) ImmersiveGoldLight else ImmersiveTextPrimary,
                maxLines = 1
            )

            HorizontalDivider(color = ImmersiveCardBorder, thickness = 0.5.dp)

            // Speed Metric
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "İyileşme Hızı",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                    color = ImmersiveTextMuted
                )
                Text(
                    text = speedText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = if (tier == 3) Color(0xFF81C784) else if (tier == 2) ImmersiveGold else Color(0xFFEF5350)
                    )
                )
            }

            // Stat Loss Risk Metric
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Stat Kaybı Riski",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                    color = ImmersiveTextMuted
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (statLossPercent == 0) Color(0xFF4CAF50).copy(alpha = 0.2f) else if (statLossPercent <= 15) Color(0xFFFFB74D).copy(alpha = 0.2f) else Color(0xFFE57373).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "%$statLossPercent Risk",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp
                        ),
                        color = if (statLossPercent == 0) Color(0xFF81C784) else if (statLossPercent <= 15) Color(0xFFFFB74D) else Color(0xFFEF5350),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
                Text(
                    text = statLossSeverity,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 7.5.sp),
                    color = ImmersiveTextMuted,
                    textAlign = TextAlign.Center
                )
            }

            // Survival Rate
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Kurtarma Şansı",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                    color = ImmersiveTextMuted
                )
                Text(
                    text = survivalRate,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    color = ImmersiveTextPrimary
                )
            }
        }
    }
}

@Composable
fun GladiatorInfirmaryCard(
    gladiator: Gladiator,
    physicianLevel: Int,
    playerGold: Int,
    onApplyHerbalPoultice: () -> Unit,
    onApplyThermalBath: () -> Unit,
    onPerformSurgery: () -> Unit,
    onInstantHealWithGold: () -> Unit
) {
    val hpPercent = if (gladiator.maxHp > 0) (gladiator.currentHp.toFloat() / gladiator.maxHp).coerceIn(0f, 1f) else 0f
    val fatiguePercent = (gladiator.fatigue.toFloat() / 100f).coerceIn(0f, 1f)

    val currentStatLossRisk = when (physicianLevel) {
        3 -> 0
        2 -> 15
        else -> 40
    }

    val estimatedDaysToHeal = when {
        gladiator.recoveryDaysLeft <= 0 -> 0
        physicianLevel == 3 -> ceil(gladiator.recoveryDaysLeft / 2.0).toInt()
        physicianLevel == 2 -> gladiator.recoveryDaysLeft
        else -> gladiator.recoveryDaysLeft * 2 // Because 50% chance per night
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ImmersiveCard,
        border = BorderStroke(
            1.dp,
            if (gladiator.isInjured) Color(0xFFE57373).copy(alpha = 0.5f) else ImmersiveCardBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Avatar, Name, Nickname, Injury Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = ImmersiveGold.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.4f)),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(gladiator.gladiatorClass.icon, fontSize = 18.sp)
                        }
                    }
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = gladiator.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveTextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ImmersiveCardBorder
                            ) {
                                Text(
                                    text = gladiator.gladiatorClass.displayName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                    color = ImmersiveTextMuted,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "\"${gladiator.nickname}\" • ${gladiator.origin}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = ImmersiveGoldLight
                        )
                    }
                }

                // Injury Severity Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        gladiator.injurySeverity == InjurySeverity.CRITICAL -> Color(0xFFB71C1C).copy(alpha = 0.25f)
                        gladiator.injurySeverity == InjurySeverity.SEVERE -> Color(0xFFC62828).copy(alpha = 0.2f)
                        gladiator.injurySeverity == InjurySeverity.LIGHT -> Color(0xFFFF8F00).copy(alpha = 0.2f)
                        gladiator.isInjured -> Color(0xFFFF8F00).copy(alpha = 0.2f)
                        else -> Color(0xFF2E7D32).copy(alpha = 0.2f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            gladiator.injurySeverity == InjurySeverity.CRITICAL -> Color(0xFFEF5350)
                            gladiator.injurySeverity == InjurySeverity.SEVERE -> Color(0xFFEF5350)
                            gladiator.injurySeverity == InjurySeverity.LIGHT -> Color(0xFFFFB74D)
                            gladiator.isInjured -> Color(0xFFFFB74D)
                            else -> Color(0xFF81C784)
                        }
                    )
                ) {
                    Text(
                        text = when {
                            gladiator.injurySeverity == InjurySeverity.CRITICAL -> "💀 Kritik Sakatlık"
                            gladiator.injurySeverity == InjurySeverity.SEVERE -> "🔴 Ağır Yaralı"
                            gladiator.injurySeverity == InjurySeverity.LIGHT -> "🟡 Hafif Yaralı"
                            gladiator.isInjured -> "🩹 Yaralı (${gladiator.recoveryDaysLeft} Gün)"
                            else -> "🟢 Sağlıklı"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = when {
                            gladiator.injurySeverity == InjurySeverity.CRITICAL || gladiator.injurySeverity == InjurySeverity.SEVERE -> Color(0xFFEF5350)
                            gladiator.isInjured -> Color(0xFFFFB74D)
                            else -> Color(0xFF81C784)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Health & Fatigue Bars
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // HP Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sağlık (HP): ${gladiator.currentHp} / ${gladiator.maxHp}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = ImmersiveTextPrimary
                    )
                    Text(
                        text = "${(hpPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = if (hpPercent > 0.6f) Color(0xFF81C784) else if (hpPercent > 0.3f) Color(0xFFFFB74D) else Color(0xFFEF5350)
                    )
                }
                LinearProgressIndicator(
                    progress = { hpPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (hpPercent > 0.6f) Color(0xFF81C784) else if (hpPercent > 0.3f) Color(0xFFFFB74D) else Color(0xFFEF5350),
                    trackColor = ImmersiveCardBorder
                )

                // Fatigue Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Yorgunluk: ${gladiator.fatigue} / 100",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = ImmersiveTextMuted
                    )
                    Text(
                        text = if (gladiator.fatigue >= 60) "Bitkin 💤" else if (gladiator.fatigue >= 30) "Yorgun" else "Diri ⚡",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                        color = if (gladiator.fatigue >= 60) Color(0xFFEF5350) else if (gladiator.fatigue >= 30) Color(0xFFFFB74D) else Color(0xFF81C784)
                    )
                }
                LinearProgressIndicator(
                    progress = { fatiguePercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (fatiguePercent >= 0.6f) Color(0xFFEF5350) else if (fatiguePercent >= 0.3f) Color(0xFFFFB74D) else Color(0xFF64B5F6),
                    trackColor = ImmersiveCardBorder
                )
            }

            // Injury & Risk Detail Section
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ImmersiveBg.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, ImmersiveCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Kalıcı Stat Kaybı Riski:",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = ImmersiveTextMuted
                        )
                        Text(
                            text = "%$currentStatLossRisk (Mevcut Hekimle)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentStatLossRisk == 0) Color(0xFF81C784) else if (currentStatLossRisk <= 15) Color(0xFFFFB74D) else Color(0xFFEF5350)
                            )
                        )
                    }

                    if (gladiator.isInjured) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Kalan İyileşme Süresi:",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ImmersiveTextMuted
                            )
                            Text(
                                text = "${gladiator.recoveryDaysLeft} Gün (Tahmini $estimatedDaysToHeal Gece)",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = ImmersiveGold
                            )
                        }
                    }

                    if (gladiator.hasDisabledLimb) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚠️", fontSize = 10.sp)
                            Text(
                                text = gladiator.disabledLimbDesc ?: "Kalıcı Sakat Uzuv Hasarı Mevcut",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = Color(0xFFEF5350)
                            )
                        }
                    }
                }
            }

            // Treatment Actions
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "HEKİM MÜDAHALELERİ & TEDAVİLER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = ImmersiveGold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Herbal Poultice Button
                    OutlinedButton(
                        onClick = onApplyHerbalPoultice,
                        enabled = playerGold >= 30 && (gladiator.isInjured || gladiator.currentHp < gladiator.maxHp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF81C784)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("herbal_poultice_btn_${gladiator.id}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🌿 Bitki Sargısı", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = Color(0xFF81C784))
                            Text("-1 Gün / +35 HP (30 🪙)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp), color = ImmersiveTextMuted)
                        }
                    }

                    // Thermal Bath Button
                    OutlinedButton(
                        onClick = onApplyThermalBath,
                        enabled = playerGold >= 40 && (gladiator.fatigue > 0 || gladiator.mor < 100),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF64B5F6)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("thermal_bath_btn_${gladiator.id}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏺 Hamam Masajı", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = Color(0xFF64B5F6))
                            Text("0 Yorgunluk (40 🪙)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp), color = ImmersiveTextMuted)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Emergency Surgery Button (Only if has disabled limb or severe injury, requires physician level >= 2)
                    if (gladiator.hasDisabledLimb || gladiator.injurySeverity == InjurySeverity.SEVERE || gladiator.injurySeverity == InjurySeverity.CRITICAL) {
                        Button(
                            onClick = onPerformSurgery,
                            enabled = playerGold >= 120 && physicianLevel >= 2,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("emergency_surgery_btn_${gladiator.id}")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⚡ Acil Cerrahi", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = Color.White)
                                Text(if (physicianLevel < 2) "Seviye 2 Hekim Şart" else "Uzuv Kurtar (120 🪙)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp), color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }

                    // Instant Miracle Heal with Gold
                    Button(
                        onClick = onInstantHealWithGold,
                        enabled = playerGold >= 85 && (gladiator.isInjured || gladiator.currentHp < gladiator.maxHp || gladiator.fatigue > 0),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("instant_miracle_heal_btn_${gladiator.id}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🧪 Antik Mucize İksir", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = Color.Black)
                            Text("Tam İyileşme (85 🪙)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp), color = Color.Black.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}
