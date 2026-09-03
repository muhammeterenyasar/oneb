package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.simulation.LudusUiState
import com.example.ui.components.GladiatorMiniSprite
import com.example.ui.components.GladiatorSpriteShowcase
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.theme.*

@Composable
fun ArenaHubScreen(
    state: LudusUiState,
    onSelectVenue: (ArenaVenueId) -> Unit,
    onSelectScoutingOpponent: (PersistentFighter) -> Unit,
    onChallengeOpponent: (PersistentFighter, ArenaMatchType) -> Unit,
    onStartScheduledMatch: () -> Unit,
    onAdvanceFightPhase: () -> Unit = {},
    onStartUndergroundFight: (UndergroundFight) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf("HUB") }
    val currentVenue = state.currentVenue
    val venueFighters = state.persistentFighters.filter { it.currentArena == currentVenue && it.isAlive }
    val venueChampion = venueFighters.find { it.isChampion }
        ?: state.persistentFighters.find { it.isChampion && it.currentArena == currentVenue && it.isAlive }
        ?: venueFighters.firstOrNull()
        ?: state.persistentFighters.first()

    val playerGladiator = state.gladiators.find { it.id == state.selectedFighterId }
        ?: state.gladiators.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // TOP NAVIGATION TABS (Football Manager Style Circuit Navigation)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B1410), RoundedCornerShape(4.dp))
                .border(1.dp, RomanBronzeDark, RoundedCornerShape(4.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Triple("HUB", "Arena Merkezi", Icons.Default.Shield),
                Triple("CALENDAR", "Dövüş Takvimi", Icons.Default.CalendarMonth),
                Triple("UNDERGROUND", "Yeraltı Çukurları", Icons.Default.Dangerous),
                Triple("RANKINGS", "Lig Sıralaması", Icons.Default.Leaderboard),
                Triple("SCOUTING", "Rakip İstihbaratı", Icons.Default.Visibility),
                Triple("FALLEN", "Düşenler Anıtı", Icons.Default.Nightlife),
                Triple("RIVALRIES", "Kariyer & Davalar", Icons.Default.Handshake)
            )

            tabs.forEach { (tabId, label, icon) ->
                val isSelected = activeTab == tabId
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (isSelected) RomanCrimson else Color(0xFF261D17)
                        )
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) RomanGold else Color.Transparent,
                            shape = RoundedCornerShape(3.dp)
                        )
                        .clickable { activeTab = tabId }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) RomanGoldLight else RomanParchmentDark,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = label,
                            color = if (isSelected) RomanGoldLight else RomanParchment,
                            fontSize = 8.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // MAIN TAB CONTENT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (activeTab) {
                "HUB" -> ArenaHubOverviewPane(
                    state = state,
                    currentVenue = currentVenue,
                    venueChampion = venueChampion,
                    playerGladiator = playerGladiator,
                    onSelectVenue = onSelectVenue,
                    onChallengeOpponent = onChallengeOpponent,
                    onStartScheduledMatch = onStartScheduledMatch,
                    onAdvanceFightPhase = onAdvanceFightPhase,
                    onSwitchToTab = { activeTab = it },
                    onSwitchToScout = { fighter ->
                        onSelectScoutingOpponent(fighter)
                        activeTab = "SCOUTING"
                    }
                )
                "CALENDAR" -> ArenaCalendarPane(
                    state = state,
                    currentVenue = currentVenue,
                    onStartPlayerMatch = onStartScheduledMatch
                )
                "UNDERGROUND" -> UndergroundArenaPane(
                    state = state,
                    onStartUndergroundFight = onStartUndergroundFight
                )
                "RANKINGS" -> ArenaRankingsPane(
                    state = state,
                    currentVenue = currentVenue,
                    fighters = venueFighters,
                    playerGladiator = playerGladiator,
                    onChallenge = { fighter, matchType -> onChallengeOpponent(fighter, matchType) },
                    onScout = { fighter ->
                        onSelectScoutingOpponent(fighter)
                        activeTab = "SCOUTING"
                    }
                )
                "SCOUTING" -> OpponentScoutingPane(
                    state = state,
                    onSelectOpponent = onSelectScoutingOpponent,
                    onChallenge = { fighter, matchType -> onChallengeOpponent(fighter, matchType) }
                )
                "FALLEN" -> HallOfTheFallenPane(
                    state = state
                )
                "RIVALRIES" -> CareerAndRivalriesPane(
                    state = state,
                    playerGladiator = playerGladiator,
                    onChallengeRival = { rivalFighter ->
                        onChallengeOpponent(rivalFighter, ArenaMatchType.RIVALRY_MATCH)
                    }
                )
            }
        }
    }
}

/**
 * ARENA HUB OVERVIEW TAB:
 * Displays active venue, current champion card, circuit ladder selector, and fight card options.
 */
@Composable
private fun ArenaHubOverviewPane(
    state: LudusUiState,
    currentVenue: ArenaVenueId,
    venueChampion: PersistentFighter,
    playerGladiator: Gladiator,
    onSelectVenue: (ArenaVenueId) -> Unit,
    onChallengeOpponent: (PersistentFighter, ArenaMatchType) -> Unit,
    onStartScheduledMatch: () -> Unit,
    onAdvanceFightPhase: () -> Unit = {},
    onSwitchToTab: (String) -> Unit = {},
    onSwitchToScout: (PersistentFighter) -> Unit
) {
    val venueFighters = state.persistentFighters.filter { it.currentArena == currentVenue && it.isAlive }.ifEmpty { state.persistentFighters.filter { it.isAlive } }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // LEFT COLUMN: Arena Details & Progression Selector
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(
                title = currentVenue.venueName.uppercase(),
                badge = currentVenue.city
            ) {
                Text(
                    text = currentVenue.atmosphericRule,
                    color = RomanGoldLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Kapasite", color = RomanTextSecondary, fontSize = 9.sp)
                        Text(currentVenue.capacity, color = RomanParchment, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Ödül Çarpanı", color = RomanTextSecondary, fontSize = 9.sp)
                        Text("${currentVenue.purseMultiplier}x Denarii", color = RomanGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Zafer Başına Şan", color = RomanTextSecondary, fontSize = 9.sp)
                        Text("+${currentVenue.prestigePerWin} Şan", color = Color(0xFF60A5FA), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Arena Hamisi: ${currentVenue.patronTitle}", color = RomanParchmentDark, fontSize = 9.sp)
            }

            // ARENA PROGRESSION CIRCUIT (Football Manager League Selector)
            RomanCard(title = "İmparatorluk Arena Kademeleri", badge = "Lig Kademesi") {
                ArenaVenueId.values().forEach { venue ->
                    val isCurrent = venue == currentVenue
                    val isUnlocked = state.dominus.prestige >= venue.minPrestige
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (isCurrent) RomanDarkCrimson.copy(alpha = 0.8f) else if (isUnlocked) Color(0xFF221A15) else Color(0xFF16110E)
                            )
                            .border(
                                width = if (isCurrent) 1.dp else 0.5.dp,
                                color = if (isCurrent) RomanGold else if (isUnlocked) RomanBronzeDark else Color.Transparent,
                                shape = RoundedCornerShape(3.dp)
                            )
                            .clickable(enabled = isUnlocked) { onSelectVenue(venue) }
                            .padding(horizontal = 6.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.Stadium else Icons.Default.Lock,
                                contentDescription = venue.venueName,
                                tint = if (isCurrent) RomanGold else if (isUnlocked) RomanParchmentDark else RomanDangerRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Column {
                                Text(
                                    text = venue.venueName,
                                    color = if (isCurrent) RomanGoldLight else if (isUnlocked) RomanParchment else RomanTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = "Gereken Şan: ${venue.minPrestige} • ${venue.city}",
                                    color = RomanTextSecondary,
                                    fontSize = 8.sp
                                )
                            }
                        }
                        if (isCurrent) {
                            Text("AKTİF", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        } else if (!isUnlocked) {
                            Text("KİLİTLİ", color = RomanDangerRed, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }

        // MIDDLE COLUMN: Reigning Champion Spotlight
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(
                title = "ARENA ŞAMPİYONU (REIGNING CHAMPION)",
                badge = venueChampion.tier.displayName
            ) {
                // Champion Sprite Showcase
                GladiatorSpriteShowcase(
                    gladiator = venueChampion.toGladiator(),
                    height = 140.dp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = venueChampion.fullDisplayName,
                            color = RomanGoldLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${venueChampion.ludusAffiliation} • ${venueChampion.gladiatorClass.title} (${venueChampion.origin.region})",
                            color = RomanTextSecondary,
                            fontSize = 9.5.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(RomanCrimson, RoundedCornerShape(3.dp))
                            .border(0.5.dp, RomanGold, RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = venueChampion.recordSummary,
                            color = RomanGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "İmza Tekniği: ${venueChampion.signatureTactic}",
                    color = RomanParchment,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Taktik Kimlik: ${venueChampion.aiPersonality.title} - ${venueChampion.aiPersonality.description}",
                    color = RomanTextSecondary,
                    fontSize = 8.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onSwitchToScout(venueChampion) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C211A)),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier.weight(1f).height(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Detaylı İstihbarat", color = RomanParchment, fontSize = 9.5.sp)
                    }
                    Button(
                        onClick = { onSwitchToTab("CALENDAR") },
                        colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier.weight(1.2f).height(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Unvan Takvimini Gör", color = RomanGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // PLAYER STANDING IN THIS ARENA
            RomanCard(title = "Ludusumuzun Durumu", badge = playerGladiator.name) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Aktif Dövüşçü: ${playerGladiator.name}", color = RomanParchment, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Sınıf: ${playerGladiator.gladiatorClass.title} • Sağlık: %${playerGladiator.condition.health}", color = RomanTextSecondary, fontSize = 9.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Kariyer: ${playerGladiator.careerStats.wins}G - ${playerGladiator.careerStats.losses}M", color = RomanGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Halk Desteği: %${playerGladiator.careerStats.crowdFavor}", color = RomanSuccessGreen, fontSize = 9.sp)
                    }
                }
            }
        }

        // RIGHT COLUMN: Calendar-Driven Scheduled Match & Multi-Phase Progression
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val isFightToday = state.arenaCalendar.any { it.day == state.dominus.dayNumber && it.isPlayerMatch && !it.isCompleted }
            val nextPlayerBout = state.arenaCalendar.firstOrNull { it.isPlayerMatch && !it.isCompleted && it.day >= state.dominus.dayNumber }
            val daysUntil = if (nextPlayerBout != null) (nextPlayerBout.day - state.dominus.dayNumber) else 0

            if (isFightToday) {
                RomanCard(
                    title = "RESMİ MÜSABAKA GÜNÜ: GÜN ${state.dominus.dayNumber}",
                    badge = "BUGÜN ARENADASINIZ"
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RomanDarkCrimson.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .border(1.dp, RomanGold, RoundedCornerShape(4.dp))
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = state.scheduledMatch.arenaName.uppercase(),
                                    color = RomanGoldLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = state.scheduledMatch.matchType,
                                    color = RomanGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Rakip: ${state.scheduledMatch.opponentGladiator.name} (${state.scheduledMatch.opponentLudus})",
                                color = RomanParchment,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Sınıf: ${state.scheduledMatch.opponentGladiator.gladiatorClass.title} • Ödül: ${state.scheduledMatch.basePrizeGold} Denarii (+${state.scheduledMatch.basePrestige} Şan)",
                                color = RomanTextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 4-Phase Progress Indicator
                    Text(
                        text = "MÜSABAKA GÜNÜ FAZ AKIŞI:",
                        color = RomanGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    val phases = listOf(
                        FightDayPhase.MORNING_PREPARATION to "1. Sabah Muayenesi & Zırh",
                        FightDayPhase.AFTERNOON_ARRIVAL to "2. Öğleden Sonra Arenaya Varış",
                        FightDayPhase.PRE_MATCH_BRIEFING to "3. Taktik Brifing & Stance",
                        FightDayPhase.COMBAT_ACTIVE to "4. Kan Kumlarına Çıkış"
                    )

                    phases.forEach { (phase, label) ->
                        val isCurrent = state.fightDayPhase == phase
                        val isPassed = state.fightDayPhase.ordinal > phase.ordinal

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isCurrent) RomanCrimson.copy(alpha = 0.4f) else if (isPassed) Color(0xFF1B2618) else Color(0xFF1E1713),
                                    RoundedCornerShape(3.dp)
                                )
                                .border(
                                    width = if (isCurrent) 1.dp else 0.5.dp,
                                    color = if (isCurrent) RomanGold else if (isPassed) RomanSuccessGreen else RomanBronzeDark,
                                    shape = RoundedCornerShape(3.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = if (isPassed) Icons.Default.CheckCircle else if (isCurrent) Icons.Default.PlayArrow else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (isPassed) RomanSuccessGreen else if (isCurrent) RomanGold else RomanTextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = label,
                                    color = if (isCurrent) RomanGoldLight else if (isPassed) RomanParchment else RomanTextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            if (isCurrent) {
                                Text("ŞU ANKİ AŞAMA", color = RomanGold, fontSize = 8.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val actionBtnText = when (state.fightDayPhase) {
                        FightDayPhase.IDLE -> "Güne Başla (Sabah Muayenesine Gir)"
                        FightDayPhase.MORNING_PREPARATION -> "Arenaya İntikal Et (Öğleden Sonra)"
                        FightDayPhase.AFTERNOON_ARRIVAL -> "Taktik Brifingine Gir"
                        FightDayPhase.PRE_MATCH_BRIEFING, FightDayPhase.COMBAT_ACTIVE, FightDayPhase.POST_MATCH_REPORT -> "ARENAYA ADIM AT (DÖVÜŞ)"
                    }

                    Button(
                        onClick = {
                            if (state.fightDayPhase == FightDayPhase.PRE_MATCH_BRIEFING || state.fightDayPhase == FightDayPhase.COMBAT_ACTIVE) {
                                onStartScheduledMatch()
                            } else {
                                onAdvanceFightPhase()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .border(1.dp, RomanGold, RoundedCornerShape(3.dp))
                    ) {
                        Icon(Icons.Default.SportsKabaddi, contentDescription = null, tint = RomanGoldLight, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(actionBtnText, color = RomanGoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                RomanCard(
                    title = "RESMİ ARENA TAKVİMİ",
                    badge = if (nextPlayerBout != null) "$daysUntil GÜN KALDI" else "GÜN ${state.dominus.dayNumber}"
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF221A15), RoundedCornerShape(3.dp))
                            .border(0.8.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = "Bugün Resmi Dövüş Günü Değil",
                                color = RomanGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Roma arenasında dövüşler rastgele başlatılamaz; dünya takviminin ritmine göre düzenlenir.",
                                color = RomanTextSecondary,
                                fontSize = 8.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (nextPlayerBout != null) {
                        Text(
                            text = "SONRAKİ PLANLANMIŞ MÜSABAKANIZ:",
                            color = RomanGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF281E18), RoundedCornerShape(3.dp))
                                .border(0.8.dp, RomanGold.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                                .padding(6.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "GÜN ${nextPlayerBout.day} (${daysUntil} gün sonra)",
                                        color = RomanGoldLight,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = nextPlayerBout.venueId.city,
                                        color = RomanTextSecondary,
                                        fontSize = 8.5.sp
                                    )
                                }
                                Text(
                                    text = "Rakip: ${nextPlayerBout.fighter1Name} (${nextPlayerBout.fighter1Ludus})",
                                    color = RomanParchment,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Mekan: ${nextPlayerBout.venueId.venueName} • ${nextPlayerBout.matchType.title}",
                                    color = RomanTextSecondary,
                                    fontSize = 8.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lanista Tavsiyesi: Dövüşçünüzü maç gününe kadar revirde dinlendirin veya eksik niteliklerini geliştirmek için antrenman sahasına yollayın.",
                        color = RomanParchmentDark,
                        fontSize = 8.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { onSwitchToTab("CALENDAR") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2019)),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = RomanGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tüm Takvimi İncele", color = RomanParchment, fontSize = 9.5.sp)
                    }
                }
            }

            // SUBURA UNDERGROUND PROMO CARD
            RomanCard(
                title = "SUBURA YERALTI ÇUKURLARI",
                badge = "${state.undergroundFights.size} Dövüş"
            ) {
                Text(
                    text = "Resmi takvim dışı yasadışı kan müsabakaları. Yüksek bahis parası, ölüm tehlikesi ve Praetor devriyelerince basılma riski taşır.",
                    color = RomanTextSecondary,
                    fontSize = 8.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = { onSwitchToTab("UNDERGROUND") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B1812)),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .border(1.dp, RomanCrimson, RoundedCornerShape(3.dp)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Dangerous, contentDescription = null, tint = RomanDangerRed, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Yeraltı Dövüş Çukurlarına İn", color = RomanGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * ARENA RANKINGS TAB:
 * Complete league table / ladder with ratings, form, W-L-K records, and direct challenge/scout buttons.
 */
@Composable
private fun ArenaRankingsPane(
    state: LudusUiState,
    currentVenue: ArenaVenueId,
    fighters: List<PersistentFighter>,
    playerGladiator: Gladiator,
    onChallenge: (PersistentFighter, ArenaMatchType) -> Unit,
    onScout: (PersistentFighter) -> Unit
) {
    val sortedFighters = fighters.sortedByDescending { it.prestige }

    RomanCard(
        title = "${currentVenue.venueName.uppercase()} - RESMİ LİG SIRALAMASI",
        badge = "${sortedFighters.size + 1} Dövüşçü"
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1713))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#", color = RomanTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp))
                Text("Gladyatör & Unvan", color = RomanTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.8f))
                Text("Ludus", color = RomanTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f))
                Text("Sınıf", color = RomanTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                Text("Rekor (G-M-Ö)", color = RomanTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                Text("Son Form", color = RomanTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                Text("Şan Puanı", color = RomanTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                Text("İşlem", color = RomanTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(130.dp))
            }
            HorizontalDivider(color = RomanBronzeDark, thickness = 1.dp)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(sortedFighters) { fighter ->
                    val isChamp = fighter.isChampion
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isChamp) RomanDarkCrimson.copy(alpha = 0.35f) else Color(0xFF191310)
                            )
                            .border(
                                width = if (isChamp) 1.dp else 0.dp,
                                color = if (isChamp) RomanGold.copy(alpha = 0.5f) else Color.Transparent
                            )
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isChamp) "★" else "${fighter.rankingPosition}",
                            color = if (isChamp) RomanGold else RomanParchment,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(22.dp)
                        )

                        // Name & Tier
                        Row(
                            modifier = Modifier.weight(1.8f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            GladiatorMiniSprite(fighter.gladiatorClass, 24.dp)
                            Column {
                                Text(
                                    text = fighter.fullDisplayName,
                                    color = if (isChamp) RomanGoldLight else RomanParchment,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = fighter.tier.displayName,
                                    color = Color(fighter.tier.badgeColorHex),
                                    fontSize = 7.5.sp
                                )
                            }
                        }

                        // Ludus
                        Text(
                            text = fighter.ludusAffiliation,
                            color = RomanTextSecondary,
                            fontSize = 9.sp,
                            modifier = Modifier.weight(1.1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Class
                        Text(
                            text = fighter.gladiatorClass.title,
                            color = RomanParchment,
                            fontSize = 9.sp,
                            modifier = Modifier.weight(0.9f)
                        )

                        // Record
                        Text(
                            text = "${fighter.wins}G - ${fighter.losses}M - ${fighter.kills}Ö",
                            color = RomanGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(0.9f)
                        )

                        // Recent Form badges (W/L)
                        Row(
                            modifier = Modifier.weight(0.9f),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            fighter.recentForm.take(4).forEach { formResult ->
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(
                                            if (formResult == "W") RomanSuccessGreen else RomanDangerRed,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = formResult,
                                        color = Color.White,
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }

                        // Prestige
                        Text(
                            text = "${fighter.prestige} pts",
                            color = Color(0xFF60A5FA),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(0.8f)
                        )

                        // Actions & Calendar Match Indicator
                        val isScheduledOpponent = state.scheduledMatch.opponentGladiator.name == fighter.name
                        val nextBout = state.arenaCalendar.firstOrNull { (it.fighter1Name == fighter.name || it.fighter2Name == fighter.name) && !it.isCompleted }

                        Row(
                            modifier = Modifier.width(140.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onScout(fighter) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C221B)),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.height(24.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                            ) {
                                Text("İstihbarat", fontSize = 8.sp, color = RomanParchment)
                            }
                            if (isScheduledOpponent) {
                                Box(
                                    modifier = Modifier
                                        .background(RomanCrimson, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("RAKİBİNİZ", color = RomanGold, fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                                }
                            } else if (nextBout != null) {
                                Text("Gün ${nextBout.day}", color = RomanTextSecondary, fontSize = 7.5.sp)
                            }
                        }
                    }
                    HorizontalDivider(color = RomanBronzeDark.copy(alpha = 0.3f), thickness = 0.5.dp)
                }
            }
        }
    }
}

/**
 * OPPONENT SCOUTING DOSSIER TAB:
 * Deep intelligence dossier for any selected fighter in the entire Roman database.
 */
@Composable
private fun OpponentScoutingPane(
    state: LudusUiState,
    onSelectOpponent: (PersistentFighter) -> Unit,
    onChallenge: (PersistentFighter, ArenaMatchType) -> Unit
) {
    val selectedFighter = state.selectedOpponentForScouting ?: state.persistentFighters.first()

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // LEFT COLUMN: Opponent Database Selector
        Column(
            modifier = Modifier
                .width(180.dp)
                .fillMaxHeight()
        ) {
            RomanCard(title = "İstihbarat Listesi", badge = "${state.persistentFighters.size} Kayıt") {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(state.persistentFighters) { fighter ->
                        val isChosen = fighter.id == selectedFighter.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (isChosen) RomanDarkCrimson else Color(0xFF1E1713)
                                )
                                .border(
                                    width = if (isChosen) 1.dp else 0.dp,
                                    color = if (isChosen) RomanGold else Color.Transparent,
                                    shape = RoundedCornerShape(3.dp)
                                )
                                .clickable { onSelectOpponent(fighter) }
                                .padding(horizontal = 4.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            GladiatorMiniSprite(fighter.gladiatorClass, 20.dp)
                            Column {
                                Text(
                                    text = fighter.name,
                                    color = if (isChosen) RomanGoldLight else RomanParchment,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${fighter.tier.displayName} • ${fighter.wins}G",
                                    color = Color(fighter.tier.badgeColorHex),
                                    fontSize = 7.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // RIGHT COLUMN: Detailed Dossier (Football Manager Style Scouting Report)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(
                title = "RAKİP İSTİHBARAT DOSYASI (SCOUTING DOSSIER)",
                badge = selectedFighter.tier.displayName
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Portrait sprite
                    Box(modifier = Modifier.width(130.dp)) {
                        GladiatorSpriteShowcase(
                            gladiator = selectedFighter.toGladiator(),
                            height = 140.dp
                        )
                    }

                    // Biography & Origins
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = selectedFighter.fullDisplayName,
                            color = RomanGoldLight,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Ludus: ${selectedFighter.ludusAffiliation} • Sahibi: ${selectedFighter.ownerName}",
                            color = RomanParchment,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Köken: ${selectedFighter.origin.region} (${selectedFighter.ethnicity}) • Yaş: ${selectedFighter.age}",
                            color = RomanTextSecondary,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "Sınıf: ${selectedFighter.gladiatorClass.title} • Ekipman: ${selectedFighter.equipmentSummary}",
                            color = RomanTextSecondary,
                            fontSize = 9.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column {
                                Text("Kariyer Rekoru", color = RomanTextSecondary, fontSize = 8.sp)
                                Text(selectedFighter.recordSummary, color = RomanGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Halk Sevgisi", color = RomanTextSecondary, fontSize = 8.sp)
                                Text("%${selectedFighter.crowdApproval}", color = RomanSuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Şan Puanı", color = RomanTextSecondary, fontSize = 8.sp)
                                Text("${selectedFighter.prestige} pts", color = Color(0xFF60A5FA), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Physical & Combat Attributes Radar Bars
                RomanCard(title = "Fiziksel & Muharebe Nitelikleri (Scale 1-20)", badge = "Lanista Analizi") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            RomanStatBar("Güç (Strength)", selectedFighter.strength, 20, RomanDangerRed)
                            RomanStatBar("Hız (Speed)", selectedFighter.speed, 20, RomanStaminaCyan)
                            RomanStatBar("Çeviklik (Agility)", selectedFighter.agility, 20, Color(0xFF34D399))
                            RomanStatBar("Dayanıklılık (Endurance)", selectedFighter.endurance, 20, Color(0xFFFBBF24))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            RomanStatBar("Kılıç Ustalığı", selectedFighter.swordsmanship, 20, RomanGold)
                            RomanStatBar("Kalkan Becerisi", selectedFighter.shieldSkill, 20, Color(0xFF60A5FA))
                            RomanStatBar("Refleks", selectedFighter.reflex, 20, Color(0xFFA78BFA))
                            RomanStatBar("Disiplin & Soğukkanlılık", selectedFighter.discipline, 20, RomanParchment)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // AI Tactical Personality & Advice
                RomanCard(
                    title = "Yapay Zeka Taktik Davranışı: ${selectedFighter.aiPersonality.title}",
                    badge = "Casus Raporu"
                ) {
                    Text(
                        text = selectedFighter.aiPersonality.description,
                        color = RomanParchment,
                        fontSize = 9.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2A1C14), RoundedCornerShape(3.dp))
                            .border(0.8.dp, RomanGold.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
                            .padding(6.dp)
                    ) {
                        Column {
                            Text(
                                text = "DOÇTORE TAKTİK TAVSİYESİ:",
                                color = RomanGoldLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedFighter.aiPersonality.tacticalAdvice,
                                color = RomanGold,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Weaknesses and Notable Bouts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Bilinen Zayıflıklar:", color = RomanTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        selectedFighter.knownWeaknesses.forEach { w ->
                            Text("• $w", color = RomanDangerRed, fontSize = 8.5.sp)
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Önemli Zaferler:", color = RomanTextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        selectedFighter.notableVictories.take(2).forEach { v ->
                            Text("• $v", color = RomanSuccessGreen, fontSize = 8.5.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                val isScheduledAgainstPlayer = state.scheduledMatch.opponentGladiator.name == selectedFighter.name
                val isFightToday = state.arenaCalendar.any { it.day == state.dominus.dayNumber && it.isPlayerMatch && !it.isCompleted }

                if (isScheduledAgainstPlayer && isFightToday) {
                    Button(
                        onClick = { onChallenge(selectedFighter, ArenaMatchType.STANDARD_DUEL) },
                        colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Icon(Icons.Default.SportsKabaddi, contentDescription = null, tint = RomanGoldLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bu Rakibe Karşı Arenaya Çık!", color = RomanGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (isScheduledAgainstPlayer) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2B2019), RoundedCornerShape(3.dp))
                            .border(1.dp, RomanGold, RoundedCornerShape(3.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "RESMİ TAKVİM EŞLEŞMESİ: Gün ${state.scheduledMatch.matchDateText}",
                            color = RomanGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1C1613), RoundedCornerShape(3.dp))
                            .border(0.5.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Resmi Takvim Dışı: Eşleşmeler lig takvimine göre belirlenir.",
                            color = RomanTextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * ARENA CALENDAR TAB:
 * Living schedule showing ongoing matches, simulated rival bouts, and upcoming festival events.
 */
@Composable
private fun ArenaCalendarPane(
    state: LudusUiState,
    currentVenue: ArenaVenueId,
    onStartPlayerMatch: () -> Unit
) {
    val calendar = state.arenaCalendar

    RomanCard(
        title = "ROMA ARENA DÖVÜŞ TAKVİMİ (CIRCUIT CALENDAR)",
        badge = "Gün ${state.dominus.dayNumber}"
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(calendar) { bout ->
                val isPlayer = bout.isPlayerMatch
                val isToday = bout.day == state.dominus.dayNumber
                val isPast = bout.isCompleted

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            if (isPlayer && isToday) RomanDarkCrimson
                            else if (isPlayer) Color(0xFF2C1E16)
                            else if (isPast) Color(0xFF16120E)
                            else Color(0xFF1E1713)
                        )
                        .border(
                            width = if (isToday) 1.dp else 0.5.dp,
                            color = if (isToday) RomanGold else if (isPlayer) RomanCrimson else RomanBronzeDark,
                            shape = RoundedCornerShape(3.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Day & Venue
                    Column(modifier = Modifier.width(75.dp)) {
                        Text(
                            text = "GÜN ${bout.day}",
                            color = if (isToday) RomanGold else RomanParchment,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = bout.venueId.city,
                            color = RomanTextSecondary,
                            fontSize = 8.sp
                        )
                    }

                    // Match Type & Matchup
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = bout.matchType.title,
                                color = if (isPlayer) RomanGoldLight else RomanParchmentDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isPlayer) {
                                Box(
                                    modifier = Modifier
                                        .background(RomanCrimson, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("LUDUSUMUZUN MAÇI", color = RomanGold, fontSize = 7.5.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        Text(
                            text = "${bout.fighter1Name} (${bout.fighter1Ludus}) VS ${bout.fighter2Name} (${bout.fighter2Ludus})",
                            color = if (isPlayer) RomanGoldLight else RomanParchment,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (bout.resultSummary != null) {
                            Text(
                                text = "Sonuç: ${bout.resultSummary}",
                                color = RomanSuccessGreen,
                                fontSize = 8.5.sp
                            )
                        }
                    }

                    // Action / Status
                    if (isToday && isPlayer && !isPast) {
                        Button(
                            onClick = onStartPlayerMatch,
                            colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text("ARENAYA ÇIK", color = RomanGoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (isPast) {
                        Text("TAMAMLANDI", color = RomanTextSecondary, fontSize = 8.5.sp)
                    } else {
                        Text("BEKLENİYOR", color = RomanParchmentDark, fontSize = 8.5.sp)
                    }
                }
            }
        }
    }
}

/**
 * CHAMPIONSHIPS TAB:
 * Displays all regional titles across the Roman world, reigns, and contender qualification.
 */
@Composable
private fun ArenaChampionshipsPane(
    state: LudusUiState,
    currentVenue: ArenaVenueId,
    onChallengeChampion: (PersistentFighter) -> Unit
) {
    val champions = state.persistentFighters.filter { it.isChampion }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        RomanCard(title = "ROMA İMPARATORLUĞU RESMİ ARENA UNVANLARI", badge = "${champions.size} Unvan") {
            champions.forEach { champ ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF221A15))
                        .border(1.dp, RomanGold.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GladiatorMiniSprite(champ.gladiatorClass, 48.dp)

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = champ.championshipTitles.firstOrNull() ?: "Arena Defne Tacı",
                                color = RomanGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Şampiyon: ${champ.fullDisplayName} (${champ.ludusAffiliation})",
                                color = RomanParchment,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Arena: ${champ.currentArena.venueName} • Rekor: ${champ.recordSummary}",
                                color = RomanTextSecondary,
                                fontSize = 8.5.sp
                            )
                            Text(
                                text = "Başarılı Savunma: ${champ.currentWinStreak} Bouts • Halk Sevgisi: %${champ.crowdApproval}",
                                color = RomanGold,
                                fontSize = 8.5.sp
                            )
                        }

                        Button(
                            onClick = { onChallengeChampion(champ) },
                            colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("Unvan Maçı İste", color = RomanGoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

/**
 * CAREER HISTORY & RIVALRIES TAB:
 * Displays gladiator's historical match logs and persistent rivalries board with rematch triggers.
 */
@Composable
private fun CareerAndRivalriesPane(
    state: LudusUiState,
    playerGladiator: Gladiator,
    onChallengeRival: (PersistentFighter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // COLUMN 1: Active Blood Feuds & Rivalries
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(title = "Kan Davaları & Rakipler (Rivalries)", badge = "${state.activeRivalries.size} Düşman") {
                state.activeRivalries.forEach { rivalry ->
                    val rivalFighter = state.persistentFighters.find { it.id == rivalry.opponentId }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF241914))
                            .border(1.dp, RomanDangerRed.copy(alpha = 0.7f), RoundedCornerShape(3.dp))
                            .padding(6.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${rivalry.opponentName} \"${rivalry.opponentNickname}\"",
                                    color = RomanDangerRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Box(
                                    modifier = Modifier
                                        .background(RomanCrimson, RoundedCornerShape(2.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "Kin: %${rivalry.animosityScore} (${rivalry.animosityTitle})",
                                        color = RomanGold,
                                        fontSize = 7.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = "Ludus: ${rivalry.opponentLudus} • Karşılaşmalar: ${rivalry.playerWins}G - ${rivalry.opponentWins}M",
                                color = RomanParchment,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "Neden: ${rivalry.reason}",
                                color = RomanTextSecondary,
                                fontSize = 8.5.sp
                            )

                            Spacer(modifier = Modifier.height(3.dp))
                            if (rivalFighter != null) {
                                Button(
                                    onClick = { onChallengeRival(rivalFighter) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.fillMaxWidth().height(26.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("İntikam / Rövanş Maçı Başlat", color = RomanGoldLight, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        // COLUMN 2: Permanent Career Match History
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
        ) {
            RomanCard(
                title = "RESMİ MAÇ KAYITLARI (CAREER LOGS)",
                badge = "${state.careerMatchHistory.size} Kayıt"
            ) {
                if (state.careerMatchHistory.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Henüz tamamlanan resmi müsabaka kaydı bulunmuyor. Arenaya çıkarak tarih yazın!",
                            color = RomanTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        items(state.careerMatchHistory) { match ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (match.won) RomanSuccessGreen.copy(alpha = 0.15f) else RomanDangerRed.copy(alpha = 0.15f)
                                    )
                                    .border(
                                        0.5.dp,
                                        if (match.won) RomanSuccessGreen else RomanDangerRed,
                                        RoundedCornerShape(3.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (match.won) "ZAFER vs ${match.opponentName}" else "MAĞLUBİYET vs ${match.opponentName}",
                                        color = if (match.won) RomanSuccessGreen else RomanDangerRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${match.arenaName} • Gün ${match.day} • ${match.matchType.title}",
                                        color = RomanTextSecondary,
                                        fontSize = 8.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "+${match.goldEarned} D • +${match.prestigeGained} Şan",
                                        color = RomanGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = match.opponentOutcome,
                                        color = RomanParchment,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * UNDERGROUND ARENA TAB:
 * Illegal blood pit fights in Subura alleys, sewers, and private estates.
 * High risk, high purse, danger of fatal death and Praetorian raids.
 */
@Composable
private fun UndergroundArenaPane(
    state: LudusUiState,
    onStartUndergroundFight: (UndergroundFight) -> Unit
) {
    val fights = state.undergroundFights

    RomanCard(
        title = "SUBURA YERALTI DÖVÜŞ ÇUKURLARI (ILLEGAL UNDERGROUND)",
        badge = "Gün ${state.dominus.dayNumber}"
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Warning & Atmosphere Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2A1410), RoundedCornerShape(3.dp))
                    .border(1.dp, RomanDangerRed.copy(alpha = 0.7f), RoundedCornerShape(3.dp))
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = RomanDangerRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "YASADIŞI KAN ÇUKURLARI - RESMİ KURALLAR GEÇERSİZDİR",
                            color = RomanGoldLight,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Burada merhamet ve Pollice Verso yoktur. Mağluplar genellikle can verir. Şehir muhafızları (Praetorian) basarsa ağır para cezası uygulanır.",
                            color = RomanParchment,
                            fontSize = 8.5.sp
                        )
                    }
                }
            }

            if (fights.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bugün Subura sokaklarında aktif yeraltı dövüşü bulunmuyor. Yeni dövüşler yarın gece düzenlenecek.",
                        color = RomanTextSecondary,
                        fontSize = 11.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(fights) { fight ->
                        val canAfford = state.dominus.denarii >= fight.entryFee

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (fight.isBossFight) Color(0xFF2E1210) else Color(0xFF1E1410))
                                .border(
                                    if (fight.isBossFight) 1.2.dp else 0.8.dp,
                                    if (fight.isBossFight) RomanGold else RomanDangerRed.copy(alpha = 0.5f),
                                    RoundedCornerShape(3.dp)
                                )
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.2f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = fight.venueName,
                                        color = if (fight.isBossFight) RomanGold else RomanGoldLight,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (fight.isBossFight) {
                                        Box(
                                            modifier = Modifier
                                                .background(RomanGold, RoundedCornerShape(2.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "👑 YERALTI PATRONU",
                                                color = Color.Black,
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .background(RomanCrimson, RoundedCornerShape(2.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = fight.opponentFighter.tier.displayName,
                                                color = RomanGold,
                                                fontSize = 7.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Rakip: ${fight.opponentFighter.fullDisplayName} • ${fight.opponentFighter.gladiatorClass.title}",
                                    color = RomanParchment,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Baskın Riski: %${fight.discoveryRiskPercent} • Düzenleyen: ${fight.organizerName} (${fight.riskLevel})",
                                    color = RomanDangerRed,
                                    fontSize = 8.5.sp
                                )
                                if (fight.storylineSnippet != null) {
                                    Text(
                                        text = "📜 ${fight.storylineSnippet}",
                                        color = RomanGoldLight.copy(alpha = 0.85f),
                                        fontSize = 8.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(130.dp)) {
                                Text(
                                    text = "Ödül: ${fight.purseReward} D",
                                    color = RomanGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Giriş Ücreti: ${fight.entryFee} D",
                                    color = RomanTextSecondary,
                                    fontSize = 8.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { onStartUndergroundFight(fight) },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RomanCrimson,
                                        disabledContainerColor = Color(0xFF281E19)
                                    ),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.height(26.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text(
                                        text = if (canAfford) "ÇUKURA İN" else "YETERSİZ DENARII",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (canAfford) RomanParchment else RomanTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * HALL OF THE FALLEN TAB (DÜŞENLER ANITI):
 * Memorial honoring all gladiators who died in combat.
 * Permanent death guarantee: dead gladiators never return.
 */
@Composable
private fun HallOfTheFallenPane(
    state: LudusUiState
) {
    val memorials = state.fallenGladiators

    RomanCard(
        title = "DÜŞENLER ANITI (MEMORIAL OF THE FALLEN)",
        badge = "${memorials.size} Şehit"
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Epitaph header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161311), RoundedCornerShape(3.dp))
                    .border(1.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "“MORS OMNIA VINCIT” — ÖLÜM HER ŞEYİ FETHEDER",
                        color = RomanGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Kumların kanını emdiği gladyatörler ebedi istirahattedir. Bu anıttakiler kalıcı olarak ölmüştür ve asla yeniden canlanmayacaktır.",
                        color = RomanParchmentDark,
                        fontSize = 8.5.sp
                    )
                }
            }

            if (memorials.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = RomanBronzeDark,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Henüz arenada can veren gladyatör kaydedilmedi.",
                            color = RomanTextSecondary,
                            fontSize = 10.5.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(memorials) { mem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF1B1410))
                                .border(0.8.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val displayName = if (mem.nickname.isNotBlank()) "${mem.name} \"${mem.nickname}\"" else mem.name
                                    Text(
                                        text = "† ${displayName.uppercase()}",
                                        color = RomanGoldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "(${mem.gladiatorClass.title})",
                                        color = RomanTextSecondary,
                                        fontSize = 9.sp
                                    )
                                    Text(
                                        text = "• ${mem.ludusAffiliation}",
                                        color = RomanParchmentDark,
                                        fontSize = 9.sp
                                    )
                                }

                                Text(
                                    text = "“${mem.causeOfDeath} • ${mem.yearAUC}”",
                                    color = RomanGold.copy(alpha = 0.8f),
                                    fontSize = 8.5.sp
                                )

                                Text(
                                    text = "Ölüm: Gün ${mem.diedOnDay} • Arena: ${mem.arenaName} • Katil: ${mem.killedBy}",
                                    color = RomanDangerRed.copy(alpha = 0.9f),
                                    fontSize = 8.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Kariyer Rekoru",
                                    color = RomanTextSecondary,
                                    fontSize = 8.sp
                                )
                                Text(
                                    text = "${mem.recordSummary} (${mem.kills} İnfaz)",
                                    color = RomanGold,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
