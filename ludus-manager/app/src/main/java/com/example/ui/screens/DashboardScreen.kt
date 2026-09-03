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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttentionItem
import com.example.model.AttentionPriority
import com.example.model.Gladiator
import com.example.simulation.ActiveScreen
import com.example.simulation.LudusUiState
import com.example.ui.components.GladiatorAvatarCanvas
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.theme.*

/**
 * Overhauled Home Screen.
 * Answers the core questions:
 * - What is happening right now? (Today & Next)
 * - What requires my immediate attention? (Attention / Inbox System)
 * - What should I probably do next? (1-Click Action Shortcuts)
 */
@Composable
fun DashboardScreen(
    state: LudusUiState,
    onNavigate: (ActiveScreen) -> Unit,
    onSelectGladiator: (Gladiator) -> Unit,
    onExecuteAttentionAction: (AttentionItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // =========================================================
        // COLUMN 1 (LEFT): TODAY & PINNED FAVORITES
        // =========================================================
        Column(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Dominus Card
            RomanCard(title = "Bugün (Lanista Özeti)", badge = "${state.dominus.dayNumber} ${state.dominus.monthName}") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = state.dominus.name,
                        color = RomanGold,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Konum: ${state.dominus.currentCity.uppercase()} • ${state.dominus.yearAUC}",
                        color = RomanParchmentDark,
                        fontSize = 9.sp
                    )
                    HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Hazine:", color = RomanTextSecondary, fontSize = 9.sp)
                        Text(text = "${state.dominus.denarii} Denarii", color = RomanGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Şan / Prestij:", color = RomanTextSecondary, fontSize = 9.sp)
                        Text(text = "${state.dominus.prestige}", color = RomanParchment, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Tahıl Ambarı:", color = RomanTextSecondary, fontSize = 9.sp)
                        Text(text = "${state.dominus.foodWheat} Ölçek", color = RomanGoldLight, fontSize = 9.sp)
                    }
                }
            }

            // Pinned Gladiators Quick-Access
            val pinnedGladiators = state.gladiators.filter { state.pinnedGladiatorIds.contains(it.id) }
            RomanCard(
                title = "Önemli Gladyatörler",
                badge = "${pinnedGladiators.size} Sabitlendi"
            ) {
                if (pinnedGladiators.isEmpty()) {
                    Text(
                        text = "Henüz sabitlenen gladyatör yok. Kadro tablosundan yıldız ikonuna tıklayarak favorilerinizi buraya sabitleyebilirsiniz.",
                        color = RomanTextSecondary,
                        fontSize = 8.5.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        pinnedGladiators.forEach { glad ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF241A14), RoundedCornerShape(3.dp))
                                    .border(0.6.dp, RomanBronze, RoundedCornerShape(3.dp))
                                    .clickable {
                                        onSelectGladiator(glad)
                                        onNavigate(ActiveScreen.ROSTER)
                                    }
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = glad.name, color = RomanGold, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${glad.gladiatorClass.title} • Sağlık: %${glad.condition.health}", color = RomanParchmentDark, fontSize = 8.sp)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = "Git", tint = RomanGold, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // =========================================================
        // COLUMN 2 (CENTER): UNIFIED ATTENTION / INBOX SYSTEM
        // =========================================================
        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
        ) {
            RomanCard(
                title = "Dikkat Merkezi (Attention & Inbox)",
                badge = "${state.attentionItems.size} Konu Bekliyor",
                modifier = Modifier.fillMaxSize()
            ) {
                if (state.attentionItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Harika! Şu anda kışlada acil bir karar veya kriz bulunmuyor. Gladyatörleriniz antrenmanda.",
                            color = RomanTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(state.attentionItems) { item ->
                            val priorityColor = when (item.priority) {
                                AttentionPriority.CRITICAL -> RomanDangerRed
                                AttentionPriority.IMPORTANT -> RomanGold
                                AttentionPriority.RELEVANT -> Color(0xFF3B82F6)
                                AttentionPriority.BACKGROUND -> RomanParchmentDark
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF221814), RoundedCornerShape(3.dp))
                                    .border(0.8.dp, priorityColor.copy(alpha = 0.7f), RoundedCornerShape(3.dp))
                                    .padding(6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(priorityColor.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = item.priority.label.uppercase(),
                                                color = priorityColor,
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                        Text(
                                            text = item.title,
                                            color = RomanGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.message,
                                        color = RomanParchment,
                                        fontSize = 8.5.sp,
                                        lineHeight = 12.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Button(
                                    onClick = {
                                        if (item.targetGladiatorId != null) {
                                            state.gladiators.find { it.id == item.targetGladiatorId }?.let { onSelectGladiator(it) }
                                        }
                                        onExecuteAttentionAction(item)
                                        onNavigate(item.targetScreen)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RomanCrimson,
                                        contentColor = RomanParchment
                                    ),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text(text = item.actionLabel, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // =========================================================
        // COLUMN 3 (RIGHT): NEXT FIGHT SPOTLIGHT & TOMORROW HORIZON
        // =========================================================
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val isFightToday = state.arenaCalendar.any { it.day == state.dominus.dayNumber && it.isPlayerMatch && !it.isCompleted }
            val nextPlayerBout = state.arenaCalendar.firstOrNull { it.isPlayerMatch && !it.isCompleted && it.day >= state.dominus.dayNumber }
            val daysUntil = if (nextPlayerBout != null) (nextPlayerBout.day - state.dominus.dayNumber) else 0

            // Next Bout Card
            RomanCard(
                title = if (isFightToday) "BUGÜN: RESMİ MAÇ GÜNÜ" else "Sıradaki Resmi Müsabaka",
                badge = if (isFightToday) "DÖVÜŞ VAKTİ" else if (daysUntil > 0) "$daysUntil GÜN KALDI" else "Planlandı"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentFighter = state.gladiators.find { it.id == state.selectedFighterId } ?: state.gladiators.first()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        GladiatorAvatarCanvas(currentFighter.gladiatorClass, 42.dp)
                        Text(text = currentFighter.name, color = RomanGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = currentFighter.gladiatorClass.title, color = RomanTextSecondary, fontSize = 8.sp)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(if (isFightToday) RomanCrimson else Color(0xFF2E221B), RoundedCornerShape(14.dp))
                                .border(1.dp, RomanGold, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "VS", color = RomanGold, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    val opponent = state.scheduledMatch.opponentGladiator
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        GladiatorAvatarCanvas(opponent.gladiatorClass, 42.dp)
                        Text(text = opponent.name, color = RomanDangerRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = opponent.gladiatorClass.title, color = RomanTextSecondary, fontSize = 8.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = { onNavigate(ActiveScreen.MATCH_PREP) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFightToday) RomanCrimson else Color(0xFF2E221A),
                        contentColor = RomanGold
                    ),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier.fillMaxWidth().height(32.dp).border(1.dp, RomanGold, RoundedCornerShape(3.dp)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (isFightToday) "⚔ MAÇ HAZIRLIĞI & ARENAYA GİR" else "MÜSABAKA STRATEJİSİ",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Tomorrow Horizon Preview
            RomanCard(
                title = "Yarın Ne Olacak? (Ufuk & Gözcüler)",
                badge = "Gün ${state.dominus.dayNumber + 1}"
            ) {
                if (state.tomorrowPreviews.isEmpty()) {
                    Text(
                        text = "Gözcüler yarın için olağanüstü bir gelişme bildirmedi.",
                        color = RomanTextSecondary,
                        fontSize = 8.5.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        state.tomorrowPreviews.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF221814), RoundedCornerShape(2.dp))
                                    .border(0.6.dp, RomanBronzeDark, RoundedCornerShape(2.dp))
                                    .padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = item.iconSymbol, fontSize = 12.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.headline, color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(text = item.teaserText, color = RomanParchment, fontSize = 8.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
