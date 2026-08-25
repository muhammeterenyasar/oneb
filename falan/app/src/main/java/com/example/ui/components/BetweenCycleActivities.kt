package com.example.ui.components

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.MainUiState
import com.example.ui.theme.*

/**
 * Visual chips indicating active combat buffs prepared between cycles
 */
@Composable
fun ActiveCombatBuffsRow(
    ludusState: LudusState,
    modifier: Modifier = Modifier
) {
    val activeBuffs = remember(
        ludusState.scoutedEnemyWeakness,
        ludusState.sharpenedWeapons,
        ludusState.crowdHypeBonus,
        ludusState.rivalWeakenedByPoison,
        ludusState.marsDivineBlessing
    ) {
        mutableListOf<Triple<String, String, Color>>().apply {
            if (ludusState.scoutedEnemyWeakness) {
                add(Triple("🎯 Casusluk", "+%15 Hasar", Color(0xFF64B5F6)))
            }
            if (ludusState.sharpenedWeapons) {
                add(Triple("🗡️ Bilenmiş Silah", "+%15 Kritik", Color(0xFFFFB74D)))
            }
            if (ludusState.crowdHypeBonus) {
                add(Triple("📢 Tellal Desteği", "%80 Başlangıç Hype", Color(0xFF81C784)))
            }
            if (ludusState.rivalWeakenedByPoison) {
                add(Triple("☠️ Gölge Zehri", "-%25 Rakip Canı", Color(0xFFCE93D8)))
            }
            if (ludusState.marsDivineBlessing) {
                add(Triple("🔥 Mars Kutsaması", "+%20 Kritik & Zırh", Color(0xFFEF5350)))
            }
        }
    }

    if (activeBuffs.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
            border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚡", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AKTİF MÜSABAKA HAZIRLIKLARI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = ImmersiveGold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(activeBuffs) { (title, effect, color) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = color.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$title: ",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = color
                                )
                                Text(
                                    text = effect,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ImmersiveTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Hub Card on Overview screen offering between-cycle Roman actions
 */
@Composable
fun BetweenCycleActivitiesHubCard(
    onOpenSparring: () -> Unit,
    onOpenTesserae: () -> Unit,
    onOpenTavern: () -> Unit,
    onOpenDilemma: () -> Unit,
    onOpenEvents: () -> Unit,
    onRollRandomEvent: (() -> Unit)? = null,
    hasDilemma: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
        border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🏛️", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "LUDUS & ROMA AKTİVİTELERİ",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = ImmersiveGold
                        )
                        Text(
                            text = "Döngüler arası gladyatörlerinizi hazırlayın ve şehri keşfedin",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ImmersiveTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Downtime Event / Festival Quick Access Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenEvents() },
                color = Color(0xFF2E1A47).copy(alpha = 0.5f),
                border = BorderStroke(1.dp, Color(0xFFAB47BC).copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "🎪", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ROMA ŞENLİKLERİ & GİZLİ CEMİYET",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD54F),
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = "Seyyar tüccarlar, Mithras yeraltı locası, Mars tapınağı ve Senato",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ImmersiveTextPrimary
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (onRollRandomEvent != null) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onRollRandomEvent() }
                                    .testTag("roll_random_event_btn"),
                                color = ImmersiveGold.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, ImmersiveGold)
                            ) {
                                Text(
                                    text = "🎲 Rastgele Çek",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ImmersiveGold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFAB47BC).copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, Color(0xFFAB47BC))
                        ) {
                            Text(
                                text = "Keşfet >",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE1BEE7)
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dilemma Urgent Banner if available
            if (hasDilemma) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOpenDilemma() },
                    color = ImmersiveCrimson.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, ImmersiveCrimson.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "📜", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "GÜNLÜK ROMA OLAYI BEKLİYOR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ImmersiveCrimson
                                    )
                                )
                                Text(
                                    text = "Bir karar vermeniz gerekiyor (Altın, Moral ve Prestij)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = ImmersiveTextPrimary
                                )
                            }
                        }
                        Text(
                            text = "Görüntüle >",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ImmersiveGold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Action Buttons Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sparring Ring
                ActivityHubButton(
                    icon = "🥋",
                    title = "İç İdman",
                    subtitle = "Zararsız Sparring",
                    onClick = onOpenSparring,
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFF4CAF50)
                )

                // Subura Tavern & Espionage
                ActivityHubButton(
                    icon = "🍷",
                    title = "Subura & Casus",
                    subtitle = "Taverna & Masaj",
                    onClick = onOpenTavern,
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFFE91E63)
                )

                // Tesserae Dice
                ActivityHubButton(
                    icon = "🎲",
                    title = "Tesserae",
                    subtitle = "Roma Kumarı",
                    onClick = onOpenTesserae,
                    modifier = Modifier.weight(1f),
                    accentColor = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
private fun ActivityHubButton(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = ImmersiveCardBorder.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = ImmersiveTextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                color = ImmersiveTextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Daily Dilemma Dialog
 */
@Composable
fun DailyDilemmaDialog(
    dilemma: DailyDilemma?,
    onChooseOption: (DilemmaOption) -> Unit,
    onDismiss: () -> Unit,
    playerGold: Int
) {
    if (dilemma == null) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
            border = BorderStroke(1.5.dp, ImmersiveGold)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = dilemma.iconEmoji, fontSize = 26.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = dilemma.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = ImmersiveGold
                            )
                            Text(
                                text = dilemma.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Serif
                                ),
                                color = ImmersiveTextPrimary
                            )
                        }
                    }
                }

                Divider(color = ImmersiveCardBorder)

                // Story description
                Text(
                    text = dilemma.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 20.sp
                    ),
                    color = ImmersiveTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "SEÇENEĞİNİZİ BELİRLEYİN:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = ImmersiveGold
                )

                // Options list
                dilemma.options.forEach { option ->
                    val canAfford = option.goldCost == 0 || playerGold >= option.goldCost
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = canAfford) {
                                onChooseOption(option)
                                onDismiss()
                            },
                        color = if (canAfford) ImmersiveBg else ImmersiveBg.copy(alpha = 0.5f),
                        border = BorderStroke(
                            1.dp,
                            if (canAfford) ImmersiveGold.copy(alpha = 0.5f) else ImmersiveTextMuted.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (canAfford) ImmersiveGold else ImmersiveTextMuted
                                )
                                if (option.goldCost > 0) {
                                    Text(
                                        text = "-${option.goldCost} 🪙",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (canAfford) ImmersiveCrimson else Color.Gray
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = option.effectDescription,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ImmersiveTextMuted
                            )
                        }
                    }
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Sonra Karar Ver", color = ImmersiveTextMuted)
                }
            }
        }
    }
}

/**
 * Interactive Sparring Arena Dialog
 */
@Composable
fun SparringArenaDialog(
    gladiators: List<Gladiator>,
    selectedFighter1: Gladiator?,
    selectedFighter2: Gladiator?,
    activeSparring: SparringState?,
    onSelectFighters: (Gladiator, Gladiator?) -> Unit,
    onStartSparring: () -> Unit,
    onDismiss: () -> Unit
) {
    var fighter1 by remember(selectedFighter1) { mutableStateOf(selectedFighter1 ?: gladiators.firstOrNull()) }
    var fighter2 by remember(selectedFighter2) { mutableStateOf(selectedFighter2) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
            border = BorderStroke(1.5.dp, ImmersiveGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🥋", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "LUDUS SPARRİNG RİNGİ",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = ImmersiveGold
                            )
                            Text(
                                text = "Tahta kılıçlarla risksiz idman dövüşü & Stat/XP gelişimi",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ImmersiveTextMuted
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = ImmersiveTextMuted)
                    }
                }

                Divider(color = ImmersiveCardBorder)

                if (activeSparring == null) {
                    // Fighter Selection Area
                    Text(
                        text = "1. DÖVÜŞÇÜYÜ SEÇİN (ANA GLADYATÖR):",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveGold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(gladiators) { g ->
                            val isSelected = fighter1?.id == g.id
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        fighter1 = g
                                        if (fighter2?.id == g.id) fighter2 = null
                                        onSelectFighters(g, fighter2)
                                    },
                                color = if (isSelected) ImmersiveGold.copy(alpha = 0.2f) else ImmersiveBg,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) ImmersiveGold else ImmersiveCardBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = g.name,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) ImmersiveGold else ImmersiveTextPrimary
                                    )
                                    Text(
                                        text = "${g.gladiatorClass.displayName} (STR ${g.str} • AGI ${g.agi})",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                        color = ImmersiveTextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "2. SPARRING PARTNERİNİ SEÇİN:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveGold
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            val isDummy = fighter2 == null
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        fighter2 = null
                                        fighter1?.let { onSelectFighters(it, null) }
                                    },
                                color = if (isDummy) ImmersiveGold.copy(alpha = 0.2f) else ImmersiveBg,
                                border = BorderStroke(
                                    1.dp,
                                    if (isDummy) ImmersiveGold else ImmersiveCardBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "🪵 Tahta Kukla (Dummy)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isDummy) ImmersiveGold else ImmersiveTextPrimary
                                    )
                                    Text(
                                        text = "Doctore Lucius Gözetiminde",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                        color = ImmersiveTextMuted
                                    )
                                }
                            }
                        }

                        items(gladiators.filter { it.id != fighter1?.id }) { g ->
                            val isSelected = fighter2?.id == g.id
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        fighter2 = g
                                        fighter1?.let { onSelectFighters(it, g) }
                                    },
                                color = if (isSelected) ImmersiveGold.copy(alpha = 0.2f) else ImmersiveBg,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) ImmersiveGold else ImmersiveCardBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = g.name,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) ImmersiveGold else ImmersiveTextPrimary
                                    )
                                    Text(
                                        text = "${g.gladiatorClass.displayName} (STR ${g.str})",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                        color = ImmersiveTextMuted
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onStartSparring,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "⚔️ SPARRİNG DÜELLOSUNU BAŞLAT (+25 XP)",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Sparring Simulation Result
                    Text(
                        text = "🏆 KAZANAN: ${activeSparring.winnerName}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Serif
                        ),
                        color = ImmersiveGold
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "Kazanımlar: +${activeSparring.xpGained} XP • ${activeSparring.statBoostSummary ?: "+Stat Bonusu"}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF81C784),
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Text(
                        text = "DÜELLO AKIŞI:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveTextMuted
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(activeSparring.rounds) { round ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (round.isDecisiveBlow) ImmersiveGold.copy(alpha = 0.15f) else ImmersiveBg,
                                border = BorderStroke(
                                    1.dp,
                                    if (round.isDecisiveBlow) ImmersiveGold.copy(alpha = 0.4f) else ImmersiveCardBorder
                                )
                            ) {
                                Text(
                                    text = round.text,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = if (round.isDecisiveBlow) ImmersiveGold else ImmersiveTextPrimary,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ringden Çık", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Tesserae Roman Dice Game Dialog
 */
@Composable
fun TesseraeGamblingDialog(
    tesseraeState: TesseraeGameState,
    playerGold: Int,
    onRollDice: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedBet by remember { mutableStateOf(25) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
            border = BorderStroke(1.5.dp, ImmersiveGold)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎲", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "TESSERAE (ROMA ZAR OYUNU)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = ImmersiveGold
                            )
                            Text(
                                text = "Tavernada zarları at, Venüs atışıyla 3x katı altın kazan!",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ImmersiveTextMuted
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = ImmersiveTextMuted)
                    }
                }

                Divider(color = ImmersiveCardBorder)

                // Bet Selector
                Text(
                    text = "BAHİS MİKTARINI SEÇİN:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ImmersiveGold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(10, 25, 50, 100).forEach { bet ->
                        val isSelected = selectedBet == bet
                        val canAfford = playerGold >= bet
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(enabled = canAfford) { selectedBet = bet },
                            color = if (isSelected) ImmersiveGold else if (canAfford) ImmersiveBg else ImmersiveBg.copy(alpha = 0.4f),
                            border = BorderStroke(1.dp, if (isSelected) ImmersiveGold else ImmersiveCardBorder)
                        ) {
                            Text(
                                text = "$bet 🪙",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else if (canAfford) ImmersiveGold else Color.Gray
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Dice Display Board
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = ImmersiveBg,
                    border = BorderStroke(1.dp, ImmersiveCardBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Player Dice
                        Text(
                            text = "SİZİN ZARLARINIZ (Toplam: ${tesseraeState.playerTotal})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveGold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            tesseraeState.playerDice.forEach { die ->
                                DiceFace(number = die, isPlayer = true)
                            }
                        }

                        Divider(color = ImmersiveCardBorder.copy(alpha = 0.5f))

                        // Rival Dice
                        Text(
                            text = "ROMA KUMARBAZININ ZARLARI (Toplam: ${tesseraeState.rivalTotal})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveTextMuted
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            tesseraeState.rivalDice.forEach { die ->
                                DiceFace(number = die, isPlayer = false)
                            }
                        }
                    }
                }

                // Result Notification
                if (tesseraeState.hasPlayedThisCycle) {
                    val isWin = tesseraeState.isPlayerWinner == true
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isWin) Color(0xFF4CAF50).copy(alpha = 0.2f) else ImmersiveCrimson.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, if (isWin) Color(0xFF4CAF50) else ImmersiveCrimson)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isWin) "🎉 ZAFER! ${tesseraeState.rollName}" else "💀 KAYIP: ${tesseraeState.rollName}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isWin) Color(0xFF81C784) else ImmersiveCrimson
                            )
                            if (isWin) {
                                Text(
                                    text = "+${tesseraeState.goldReward} Altın Kazanıldı!",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = ImmersiveGold
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { onRollDice(selectedBet) },
                    enabled = playerGold >= selectedBet,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "🎲 ZAR AT (IACTA ALEA EST - $selectedBet 🪙)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DiceFace(number: Int, isPlayer: Boolean) {
    val diceEmoji = when (number) {
        1 -> "⚀"
        2 -> "⚁"
        3 -> "⚂"
        4 -> "⚃"
        5 -> "⚄"
        else -> "⚅"
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isPlayer) ImmersiveCard else ImmersiveCardBorder.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, if (isPlayer) ImmersiveGold else ImmersiveTextMuted.copy(alpha = 0.5f)),
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = diceEmoji,
                fontSize = 32.sp,
                color = if (isPlayer) ImmersiveGold else ImmersiveTextPrimary
            )
        }
    }
}

/**
 * Subura Tavern & Espionage & Thermae Dialog
 */
@Composable
fun SuburaTavernDialog(
    gladiators: List<Gladiator>,
    ludusState: LudusState,
    onScoutWeakness: () -> Unit,
    onBuyMulsumFeast: () -> Unit,
    onBribeTownCriers: () -> Unit,
    onApplyThermalMassage: (Long) -> Unit,
    onSharpenArsenal: () -> Unit,
    onAccelerateInjuryHeal: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedGladiatorId by remember { mutableStateOf(gladiators.firstOrNull()?.id ?: 0L) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
            border = BorderStroke(1.5.dp, ImmersiveGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🍷", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "SUBURA TAVERNASI & HAMAM (THERMAE)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = ImmersiveGold
                            )
                            Text(
                                text = "Kasa: ${ludusState.gold} 🪙 • Şehir gizemleri ve bakım merkezi",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = ImmersiveTextMuted
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = ImmersiveTextMuted)
                    }
                }

                Divider(color = ImmersiveCardBorder)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Espionage
                    item {
                        TavernServiceItem(
                            icon = "🕵️",
                            title = "Subura Casusu Kirala",
                            effect = "Gelecek maçtaki rakibin sol zırh açığını keşfeder (+%15 Hasar Bonusu).",
                            cost = 25,
                            isAlreadyActive = ludusState.scoutedEnemyWeakness,
                            activeLabel = "Casusluk Raporu Hazır",
                            canAfford = ludusState.gold >= 25,
                            onExecute = onScoutWeakness
                        )
                    }

                    // 2. Mulsum Wine Feast
                    item {
                        TavernServiceItem(
                            icon = "🍷",
                            title = "Mulsum Bal Şarabı Ziyafeti",
                            effect = "Tüm kadronun yorgunluğunu siler (-30 Yorgunluk) ve +25 Moral verir.",
                            cost = 30,
                            isAlreadyActive = false,
                            canAfford = ludusState.gold >= 30,
                            onExecute = onBuyMulsumFeast
                        )
                    }

                    // 3. Town Criers Hype
                    item {
                        TavernServiceItem(
                            icon = "📢",
                            title = "Şehir Tellallarına Rüşvet",
                            effect = "Müsabaka başlangıç hype'ını %80'e fırlatır ve +20 Prestij kazandırır.",
                            cost = 25,
                            isAlreadyActive = ludusState.crowdHypeBonus,
                            activeLabel = "Tellallar Şehirde Haykırıyor",
                            canAfford = ludusState.gold >= 25,
                            onExecute = onBribeTownCriers
                        )
                    }

                    // 4. Weapon Sharpening
                    item {
                        TavernServiceItem(
                            icon = "🗡️",
                            title = "Arsenal Silahlarını Bile",
                            effect = "Çelik gladius ve mızrakları jilet gibi biler (+%15 Kritik Vuruş Şansı).",
                            cost = 20,
                            isAlreadyActive = ludusState.sharpenedWeapons,
                            activeLabel = "Silahlar Jilet Gibi Keskin",
                            canAfford = ludusState.gold >= 20,
                            onExecute = onSharpenArsenal
                        )
                    }

                    // 5. Thermae Thermal Bath & Massage
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = ImmersiveBg),
                            border = BorderStroke(1.dp, ImmersiveCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🛁", fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Hamam Masajı & Şifa (15 🪙)",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ImmersiveGold
                                        )
                                    }
                                }
                                Text(
                                    text = "Seçilen gladyatörün yorgunluğunu -45 düşürür ve +30 HP tazeler.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = ImmersiveTextMuted,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                                )

                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(gladiators) { g ->
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (selectedGladiatorId == g.id) ImmersiveGold.copy(alpha = 0.2f) else ImmersiveCard,
                                            border = BorderStroke(1.dp, if (selectedGladiatorId == g.id) ImmersiveGold else ImmersiveCardBorder),
                                            modifier = Modifier.clickable { selectedGladiatorId = g.id }
                                        ) {
                                            Text(
                                                text = "${g.name} (${g.fatigue}% Yorgun)",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = if (selectedGladiatorId == g.id) ImmersiveGold else ImmersiveTextPrimary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { onApplyThermalMassage(selectedGladiatorId) },
                                    enabled = ludusState.gold >= 15,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Masajı Uygula (-15 🪙)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // 6. Herbal Injury Fast Recover
                    val injuredGladiators = gladiators.filter { it.isInjured }
                    if (injuredGladiators.isNotEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = ImmersiveBg),
                                border = BorderStroke(1.dp, ImmersiveCrimson.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🌿", fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Hekim Çırağının Şifalı Otu (30 🪙)",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ImmersiveCrimson
                                        )
                                    }
                                    Text(
                                        text = "Yaralı gladyatörün iyileşme süresini 1 gün kısaltır ve yaraları kapatır.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = ImmersiveTextMuted,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    injuredGladiators.forEach { injured ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${injured.name} (${injured.recoveryDaysLeft} gün kaldı)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ImmersiveTextPrimary
                                            )
                                            Button(
                                                onClick = { onAccelerateInjuryHeal(injured.id) },
                                                enabled = ludusState.gold >= 30,
                                                colors = ButtonDefaults.buttonColors(containerColor = ImmersiveCrimson),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("Hızlandır (-30 🪙)", color = Color.White, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TavernServiceItem(
    icon: String,
    title: String,
    effect: String,
    cost: Int,
    isAlreadyActive: Boolean,
    activeLabel: String? = null,
    canAfford: Boolean,
    onExecute: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveBg),
        border = BorderStroke(1.dp, if (isAlreadyActive) ImmersiveGold.copy(alpha = 0.5f) else ImmersiveCardBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveGold
                    )
                }
                Text(
                    text = "$cost 🪙",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (canAfford) ImmersiveGold else Color.Gray
                )
            }
            Text(
                text = effect,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = ImmersiveTextMuted,
                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
            )
            if (isAlreadyActive && activeLabel != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ImmersiveGold.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ $activeLabel",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            } else {
                Button(
                    onClick = onExecute,
                    enabled = canAfford,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Satın Al (-$cost 🪙)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

/**
 * Immersive Roman Downtime & Festival Event Modal Dialog
 */
@Composable
fun BetweenCycleEventsDialog(
    event: BetweenCycleEvent?,
    gladiators: List<Gladiator>,
    selectedGladiator: Gladiator?,
    playerGold: Int,
    lastResult: EventResolutionResult?,
    onSelectGladiator: (Gladiator) -> Unit,
    onChooseChoice: (EventDecisionChoice) -> Unit,
    onRollRandomEvent: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    if (event == null) return

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = ImmersiveCard,
            border = BorderStroke(1.dp, Color(event.type.badgeColorHex).copy(alpha = 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("between_cycle_events_dialog")
        ) {
            if (lastResult != null) {
                // Resolution Outcome View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (lastResult.wasRiskTriggered) "⚠️" else "📜",
                            fontSize = 44.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "KARARIN SONUCU",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = ImmersiveGold
                        )
                        Text(
                            text = lastResult.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = ImmersiveTextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Story Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ImmersiveBg,
                            border = BorderStroke(1.dp, ImmersiveCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = lastResult.story,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                color = ImmersiveTextPrimary,
                                modifier = Modifier.padding(14.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Summary of changes
                        if (lastResult.summaryChanges.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "LUDUS & SAVAŞÇI ETKİLERİ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = ImmersiveGold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                lastResult.summaryChanges.forEach { change ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = ImmersiveCardBorder.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "• $change",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (change.startsWith("-") && change.contains("🪙")) ImmersiveCrimson else Color(0xFF81C784),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (onRollRandomEvent != null) {
                            OutlinedButton(
                                onClick = onRollRandomEvent,
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, ImmersiveGold),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🎲 Yeni Olay", color = ImmersiveGold, fontWeight = FontWeight.Bold)
                            }
                        }
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dismiss_event_result_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Kışlaya Dön",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black
                            )
                        }
                    }
                }
            } else {
                // Event Choices View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header with Category Badge & Random Roll trigger
                    item {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(event.type.badgeColorHex).copy(alpha = 0.18f),
                                    border = BorderStroke(1.dp, Color(event.type.badgeColorHex))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = event.type.icon, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = event.type.displayName.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            ),
                                            color = Color(event.type.badgeColorHex)
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (onRollRandomEvent != null) {
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onRollRandomEvent() }
                                                .testTag("dialog_roll_random_event_btn"),
                                            color = ImmersiveGold.copy(alpha = 0.15f),
                                            border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.6f))
                                        ) {
                                            Text(
                                                text = "🎲 Yeni Çek",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = ImmersiveGold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = ImmersiveGold.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "Kasa: $playerGold 🪙",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ImmersiveGold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = ImmersiveGold
                            )
                            Text(
                                text = "📍 ${event.locationName} • ${event.subtitle}",
                                style = MaterialTheme.typography.bodySmall,
                                color = ImmersiveTextMuted
                            )
                        }
                    }

                    // Lore Narrative Card
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ImmersiveBg,
                            border = BorderStroke(1.dp, ImmersiveCardBorder)
                        ) {
                            Text(
                                text = event.narrativeText,
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                color = ImmersiveTextPrimary,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }

                    // Optional Gladiator Selector
                    if (event.requiresTargetGladiator && gladiators.isNotEmpty()) {
                        item {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🎯", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "HEDEF / GÖREVLENDİRİLECEK GLADYATÖR",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        ),
                                        color = ImmersiveGold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(gladiators) { g ->
                                        val isSelected = selectedGladiator?.id == g.id
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onSelectGladiator(g) },
                                            color = if (isSelected) ImmersiveGold.copy(alpha = 0.2f) else ImmersiveCardBorder.copy(alpha = 0.5f),
                                            border = BorderStroke(1.dp, if (isSelected) ImmersiveGold else Color.Transparent)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = g.gladiatorClass.icon, fontSize = 14.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Text(
                                                        text = g.name,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = if (isSelected) ImmersiveGold else ImmersiveTextPrimary
                                                    )
                                                    Text(
                                                        text = "STR:${g.str} AGI:${g.agi} STA:${g.sta}",
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
                    }

                    // Decision Choices List
                    item {
                        Text(
                            text = "SEÇENEKLER & KARARLAR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = ImmersiveGold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(event.choices, key = { it.id }) { choice ->
                        val canAfford = playerGold >= choice.goldCost
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = ImmersiveBg),
                            border = BorderStroke(1.dp, if (canAfford) ImmersiveCardBorder else Color(0xFF3E2723))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = choice.icon, fontSize = 22.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = choice.title,
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = ImmersiveTextPrimary
                                            )
                                            if (choice.targetGladiatorStatBonus != null && selectedGladiator != null) {
                                                Text(
                                                    text = "Hedef: ${selectedGladiator.name} (${choice.targetGladiatorStatBonus})",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                    color = Color(0xFF64B5F6)
                                                )
                                            }
                                        }
                                    }

                                    if (choice.goldCost > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (canAfford) ImmersiveGold.copy(alpha = 0.15f) else ImmersiveCrimson.copy(alpha = 0.15f),
                                            border = BorderStroke(1.dp, if (canAfford) ImmersiveGold else ImmersiveCrimson)
                                        ) {
                                            Text(
                                                text = "-${choice.goldCost} 🪙",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (canAfford) ImmersiveGold else ImmersiveCrimson,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else if (choice.goldReward > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF81C784).copy(alpha = 0.15f),
                                            border = BorderStroke(1.dp, Color(0xFF81C784))
                                        ) {
                                            Text(
                                                text = "+${choice.goldReward} 🪙",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color(0xFF81C784),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = choice.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ImmersiveTextMuted
                                )

                                // Effect Tags
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (choice.prestigeReward != 0) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = ImmersiveGoldLight.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${if (choice.prestigeReward > 0) "+" else ""}${choice.prestigeReward} 🏛️ Prestij",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                color = ImmersiveGold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    if (choice.moraleChange != 0) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFF4CAF50).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "${if (choice.moraleChange > 0) "+" else ""}${choice.moraleChange} 😊 Moral",
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                                color = Color(0xFF81C784),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    if (choice.activatesSharpenedWeapons) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFFB74D).copy(alpha = 0.15f)) {
                                            Text("🗡️ Bilenmiş Silahlar", fontSize = 9.sp, color = Color(0xFFFFB74D), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                    if (choice.activatesScoutBonus) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF64B5F6).copy(alpha = 0.15f)) {
                                            Text("🎯 Casusluk", fontSize = 9.sp, color = Color(0xFF64B5F6), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                    if (choice.activatesCrowdHype) {
                                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF81C784).copy(alpha = 0.15f)) {
                                            Text("📢 Tellal Desteği", fontSize = 9.sp, color = Color(0xFF81C784), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                }

                                if (choice.riskChancePercent > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "⚠️ %${choice.riskChancePercent} Risk: Muhafızlara yakalanma veya rüşvet cezası.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                        color = Color(0xFFFFB74D)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { onChooseChoice(choice) },
                                    enabled = canAfford,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("event_choice_${choice.id}"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (choice.activatesPoison) Color(0xFFAB47BC) else if (choice.activatesMarsBlessing) Color(0xFFE53935) else ImmersiveGold
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    val buttonTextColor = if (choice.activatesPoison || choice.activatesMarsBlessing) Color.White else Color.Black
                                    Text(
                                        text = if (!canAfford) "Yetersiz Altın" else "Kararı Uygula",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = buttonTextColor
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            border = BorderStroke(1.dp, ImmersiveCardBorder),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Daha Sonra Karar Ver (Kapat)", color = ImmersiveTextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
