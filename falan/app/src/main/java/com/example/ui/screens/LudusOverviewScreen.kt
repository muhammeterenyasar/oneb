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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.MainUiState
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun LudusOverviewScreen(
    state: MainUiState,
    onGladiatorClick: (Gladiator) -> Unit,
    onAssignTrainingFocus: (Long, TrainingType) -> Unit,
    onSelectDiet: (DietPlan) -> Unit,
    onPromoteToTeacher: (Gladiator) -> Unit,
    onNavigateToTraining: () -> Unit,
    onNavigateToArena: () -> Unit,
    onNavigateToMarket: () -> Unit,
    onNavigateToPhysician: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToCampaign: () -> Unit = {},
    onNavigateToArmory: () -> Unit = {},
    onNavigateToHallOfFame: () -> Unit = {},
    onNavigateToLeague: () -> Unit = {},
    onUnlockPerk: (LanistaPerk) -> Unit = {},
    onOpenSparring: () -> Unit,
    onOpenTesserae: () -> Unit,
    onOpenTavern: () -> Unit,
    onOpenDilemma: () -> Unit,
    onOpenEvents: () -> Unit,
    onRollRandomEvent: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("ludus_overview_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp)
    ) {
        // 1. Scheduled Tournament & Match Status Banner
        item {
            ScheduledTournamentCountdownCard(
                ludusState = state.ludusState,
                onNavigateToArena = onNavigateToArena,
                onNavigateToCalendar = onNavigateToCalendar
            )
        }

        // 2. Primary Screen Switcher Quick Navigation Hub (4 Pillars)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                border = BorderStroke(1.dp, ImmersiveCardBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🏛️ OKUL EMRİ & HIZLI GEÇİŞ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = ImperialRomanRed
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 1: Training & Arena
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToTraining,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("nav_to_training_arena_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RomanCardSecondary,
                                contentColor = ImperialRomanRed
                            ),
                            border = BorderStroke(1.dp, ImmersiveCardBorder),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🏋️", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Training Arena",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                                )
                                Text(
                                    text = "Talim & Kadro",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                    color = ImmersiveTextMuted
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToArena,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("nav_to_match_lobby_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.ludusState.isFightDay) ImperialRedSurface else RomanCardSecondary,
                                contentColor = if (state.ludusState.isFightDay) ImperialRomanRed else RomanInkDark
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (state.ludusState.isFightDay) ImperialRomanRed else ImmersiveCardBorder
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = if (state.ludusState.isFightDay) "🔥" else "⚔️", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Match Lobby",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                                )
                                Text(
                                    text = if (state.ludusState.isFightDay) "Dövüş Günü!" else "Müsabaka",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                    color = if (state.ludusState.isFightDay) ImperialRomanRed else ImmersiveTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 2: Armory & Campaign
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToArmory,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RomanGoldSurface,
                                contentColor = RomanImperialGold
                            ),
                            border = BorderStroke(1.dp, ImmersiveBorderGold),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🛡️", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Cephanelik & Demirci",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                                )
                                Text(
                                    text = "Silah & Zırh Kuşan",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                    color = ImmersiveTextMuted
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToCampaign,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ImperialRedSurface,
                                contentColor = ImperialRomanRed
                            ),
                            border = BorderStroke(1.dp, ImperialRedDark),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🏆", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Büyük Roma Seferi",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                                )
                                Text(
                                    text = "${state.ludusState.completedCampaignMissionIds.size}/8 Boss Zaferi",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                    color = ImmersiveTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Row 3: League Standings & Hall of Fame
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToLeague,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RomanCardSecondary,
                                contentColor = ImperialRomanRed
                            ),
                            border = BorderStroke(1.dp, ImmersiveCardBorder),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "📊", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Okul Sıralaması",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                                )
                                Text(
                                    text = "6 Büyük Ludus Ligi",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                    color = ImmersiveTextMuted
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToHallOfFame,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RomanGoldSurface,
                                contentColor = RomanImperialGold
                            ),
                            border = BorderStroke(1.dp, ImmersiveBorderGold),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "👑", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Efsaneler Salonu",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                                )
                                Text(
                                    text = "Pantheon & Emekliler",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                    color = ImmersiveTextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Lanista Skill Tree & Doctrine Card
        item {
            LanistaSkillTreeCard(
                unlockedPerkIds = state.ludusState.unlockedPerkIds,
                currentPrestige = state.ludusState.prestige,
                onUnlockPerk = onUnlockPerk
            )
        }


        // 2. Crowd Sentiment & Opponent Difficulty Atmosphere Card
        item {
            CrowdSentimentAtmosphereCard(ludusState = state.ludusState)
        }

        // 3. Active Combat Buffs Prepared Between Cycles (if any)
        item {
            ActiveCombatBuffsRow(ludusState = state.ludusState)
        }

        // 4. Compact Roster Overview Card (Navigates to Roster tab for deep management)
        item {
            GladiatorRosterSummaryCard(
                gladiators = state.gladiators,
                maxSlots = state.ludusState.maxGladiatorSlots,
                onGladiatorClick = onGladiatorClick
            )
        }

        // 5. Roman & Ludus Activities Hub Card (Sparring, Tesserae, Tavern, Festivals)
        item {
            BetweenCycleActivitiesHubCard(
                onOpenSparring = onOpenSparring,
                onOpenTesserae = onOpenTesserae,
                onOpenTavern = onOpenTavern,
                onOpenDilemma = onOpenDilemma,
                onOpenEvents = onOpenEvents,
                onRollRandomEvent = onRollRandomEvent,
                hasDilemma = state.ludusState.currentDilemma != null
            )
        }

        // 6. Morning Diet Plan Selector (Only in Morning Phase)
        if (state.ludusState.phase == DayPhase.MORNING) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                    border = BorderStroke(1.dp, ImmersiveCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        DietPlanSelector(
                            currentDiet = state.ludusState.dietPlan,
                            onSelectDiet = onSelectDiet
                        )
                    }
                }
            }
        }

        // 7. Ludus Facilities (Physician & Guards Status)
        item {
            FacilitiesStatusRow(
                ludusState = state.ludusState,
                onPhysicianClick = onNavigateToPhysician,
                onMarketClick = onNavigateToMarket
            )
        }

        // 8. Ludus Stats & Records Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = ImmersiveCard,
                border = BorderStroke(1.dp, ImmersiveCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏛️ OKUL BİLANÇOSU & BAŞARILAR",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = ImmersiveGold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Toplam Dövüş", style = MaterialTheme.typography.bodySmall, color = ImmersiveTextMuted)
                            Text(
                                text = "${state.ludusState.totalFights}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                color = ImmersiveGold
                            )
                        }
                        Column {
                            Text(text = "Kazanılan Zafer", style = MaterialTheme.typography.bodySmall, color = ImmersiveTextMuted)
                            Text(
                                text = "${state.ludusState.totalWins}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                color = ImmersiveEmerald
                            )
                        }
                        Column {
                            Text(text = "Azad Edilen", style = MaterialTheme.typography.bodySmall, color = ImmersiveTextMuted)
                            Text(
                                text = "${state.ludusState.freedGladiatorsCount} Gladyatör",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                color = ImmersiveGoldLight
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Sleek, scannable preview card showing active fighters with a quick-action link to the Roster tab.
 */
@Composable
fun GladiatorRosterSummaryCard(
    gladiators: List<Gladiator>,
    maxSlots: Int,
    onGladiatorClick: (Gladiator) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ImmersiveCard,
        border = BorderStroke(1.dp, ImmersiveCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚔️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DÖVÜŞÇÜ KADROSU (${gladiators.size}/$maxSlots)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = ImmersiveGold
                    )
                }

                if (gladiators.isNotEmpty()) {
                    TextButton(
                        onClick = { onGladiatorClick(gladiators.first()) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Kadroyu Yönet",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveGold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = ImmersiveGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (gladiators.isEmpty()) {
                Text(
                    text = "Henüz kadroda gladyatör bulunmuyor. Pazardan yeni köleler kiralayabilirsiniz.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ImmersiveTextMuted
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(gladiators, key = { it.id }) { gladiator ->
                        Surface(
                            modifier = Modifier
                                .width(150.dp)
                                .clickable { onGladiatorClick(gladiator) },
                            shape = RoundedCornerShape(12.dp),
                            color = ImmersiveCardBgSecondary,
                            border = BorderStroke(
                                1.dp,
                                if (gladiator.isInjured) ImmersiveWarningBorder else ImmersiveCardBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = gladiator.name,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        ),
                                        color = ImmersiveTextPrimary,
                                        maxLines = 1
                                    )
                                    if (gladiator.isInjured) {
                                        Text(text = "🩸", fontSize = 11.sp)
                                    } else if (gladiator.canPromoteToTeacher) {
                                        Text(text = "👑", fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${gladiator.gladiatorClass.displayName} • Güç: ${gladiator.totalPowerScore}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = ImmersiveGoldLight
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "HP: ${gladiator.currentHp}/${gladiator.maxHp}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = if (gladiator.currentHp < gladiator.maxHp / 2) ImmersiveWarningText else ImmersiveEmerald
                                    )
                                    Text(
                                        text = "Yorg: %${gladiator.fatigue}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = if (gladiator.fatigue > 60) ImmersiveWarningText else ImmersiveTextMuted
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (gladiator.currentHp.toFloat() / gladiator.maxHp.toFloat()).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(CircleShape),
                                    color = if (gladiator.currentHp < gladiator.maxHp / 2) ImmersiveWarningText else ImmersiveEmerald,
                                    trackColor = ImmersiveTrack
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FacilitiesStatusRow(
    ludusState: LudusState,
    onPhysicianClick: () -> Unit = {},
    onMarketClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Physician Tent Status
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable { onPhysicianClick() },
            shape = RoundedCornerShape(14.dp),
            color = ImmersiveCard,
            border = BorderStroke(1.dp, ImmersiveCardBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🩺", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hekim Çadırı",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveGold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                val doctorName = when (ludusState.physicianLevel) {
                    3 -> "Yunan Başhekimi (Hızlı)"
                    2 -> "Ordu Cerrahı (Normal)"
                    else -> "Şifacı Çırağı (Yavaş)"
                }
                Text(
                    text = doctorName,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = ImmersiveTextPrimary
                )
                Text(
                    text = "Seviye ${ludusState.physicianLevel}/3",
                    style = MaterialTheme.typography.labelSmall,
                    color = ImmersiveSta
                )
            }
        }

        // Guard Garrison Status
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable { onMarketClick() },
            shape = RoundedCornerShape(14.dp),
            color = ImmersiveCard,
            border = BorderStroke(1.dp, ImmersiveCardBorder)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🛡️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Muhafız Birliği",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveGold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                val detectionChance = (ludusState.guardsHired * 30 + ludusState.physicianLevel * 10).coerceAtMost(90)
                Text(
                    text = "${ludusState.guardsHired}/${ludusState.maxGuards} Muhafız",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = ImmersiveTextPrimary
                )
                Text(
                    text = "Sızma Tespiti: %$detectionChance",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (detectionChance > 50) ImmersiveSta else ImmersiveGoldLight
                )
            }
        }
    }
}
