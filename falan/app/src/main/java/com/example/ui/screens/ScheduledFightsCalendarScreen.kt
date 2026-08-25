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
import com.example.model.*
import com.example.ui.MainUiState
import com.example.ui.components.*
import com.example.ui.theme.*

private enum class CalendarViewTab(val title: String, val icon: String) {
    SCHEDULED_EVENTS("Müsabaka Takvimi", "🗓️"),
    CYCLE_PLANNER("Talim Planlayıcı", "🎯"),
    CHAMPIONSHIP_ROADMAP("Şampiyona Haritası", "🏆")
}

private enum class EventFilter(val title: String) {
    ALL("Tüm Dövüşler"),
    URGENT("Bu Hafta"),
    TEAM_2V2("2v2 Takım"),
    SINE_MISSIO("Sine Missio")
}

@Composable
fun ScheduledFightsCalendarScreen(
    state: MainUiState,
    onNavigateToMatchLobby: () -> Unit,
    onNavigateToTrainingArena: () -> Unit,
    onNavigateToPhysicianTent: () -> Unit,
    onAssignTrainingFocus: (Long, TrainingType) -> Unit,
    onSelectOpponent: ((EnemyGladiator) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(CalendarViewTab.SCHEDULED_EVENTS) }
    var selectedFilter by remember { mutableStateOf(EventFilter.ALL) }
    var selectedDayFilter by remember { mutableStateOf<Int?>(null) }
    var expandedEventId by remember { mutableStateOf<String?>(null) }

    val ludusState = state.ludusState
    val currentDay = ludusState.day
    val nextMatchDay = ludusState.nextScheduledMatchDay
    val daysUntilNext = ludusState.daysUntilNextFight
    val isMatchDay = ludusState.isFightDay

    val calendarEvents = if (ludusState.upcomingCalendarEvents.isNotEmpty()) {
        ludusState.upcomingCalendarEvents
    } else {
        listOfNotNull(ludusState.currentScheduledEvent)
    }

    val filteredEvents = calendarEvents.filter { event ->
        val matchesDay = selectedDayFilter == null || event.targetDay == selectedDayFilter
        val matchesFilter = when (selectedFilter) {
            EventFilter.ALL -> true
            EventFilter.URGENT -> event.targetDay <= currentDay + 4
            EventFilter.TEAM_2V2 -> event.matchFormat == MatchFormat.TEAM_2V2
            EventFilter.SINE_MISSIO -> event.matchFormat == MatchFormat.SINE_MISSIO
        }
        matchesDay && matchesFilter
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBg)
            .padding(horizontal = 16.dp)
            .testTag("scheduled_fights_calendar_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // 1. Calendar Hero Header & Match Status Banner
        item {
            CalendarHeroHeaderCard(
                ludusState = ludusState,
                isMatchDay = isMatchDay,
                daysUntilNext = daysUntilNext,
                onNavigateToMatchLobby = onNavigateToMatchLobby,
                onNavigateToTrainingArena = onNavigateToTrainingArena,
                onNavigateToPhysicianTent = onNavigateToPhysicianTent
            )
        }

        // 2. View Mode Tabs (Takvim | Planlayıcı | Şampiyona)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ImmersiveCard)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CalendarViewTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedTab = tab }
                            .testTag("calendar_tab_${tab.name.lowercase()}"),
                        color = if (isSelected) ImmersiveGold else Color.Transparent,
                        contentColor = if (isSelected) Color.Black else ImmersiveTextMuted
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = tab.icon, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Tab Content Switching
        when (selectedTab) {
            CalendarViewTab.SCHEDULED_EVENTS -> {
                // 3. Day-by-Day Interactive Timeline Ribbon
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🗓️ GÜNLÜK ZAMAN ÇİZELGESİ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = ImmersiveGold
                            )
                            if (selectedDayFilter != null) {
                                Text(
                                    text = "Filtreyi Temizle",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = ImmersiveTerracottaLight,
                                    modifier = Modifier.clickable { selectedDayFilter = null }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val timelineDays = (currentDay..(currentDay + 18)).toList()
                            items(timelineDays) { dayNum ->
                                val eventOnDay = calendarEvents.find { it.targetDay == dayNum }
                                val isCurrentDay = dayNum == currentDay
                                val isSelected = selectedDayFilter == dayNum

                                TimelineDayItem(
                                    day = dayNum,
                                    isCurrentDay = isCurrentDay,
                                    hasEvent = eventOnDay != null,
                                    eventFormat = eventOnDay?.matchFormat,
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedDayFilter = if (selectedDayFilter == dayNum) null else dayNum
                                    }
                                )
                            }
                        }
                    }
                }

                // 4. Filter Chips
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        EventFilter.entries.forEach { filter ->
                            val isSelected = selectedFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedFilter = filter },
                                label = {
                                    Text(
                                        text = filter.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ImmersiveGold.copy(alpha = 0.25f),
                                    selectedLabelColor = ImmersiveGold,
                                    containerColor = ImmersiveCard,
                                    labelColor = ImmersiveTextMuted
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) ImmersiveGold else ImmersiveCardBorder
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                // 5. Events List
                if (filteredEvents.isEmpty()) {
                    item {
                        EmptyCalendarEventsCard(
                            onResetFilter = {
                                selectedFilter = EventFilter.ALL
                                selectedDayFilter = null
                            }
                        )
                    }
                } else {
                    items(filteredEvents, key = { it.id }) { event ->
                        val isExpanded = expandedEventId == event.id || event.targetDay == nextMatchDay
                        ScheduledArenaEventCard(
                            event = event,
                            currentDay = currentDay,
                            isExpanded = isExpanded,
                            onToggleExpand = {
                                expandedEventId = if (expandedEventId == event.id) null else event.id
                            },
                            onNavigateToArena = {
                                onSelectOpponent?.invoke(event.featuredOpponent)
                                onNavigateToMatchLobby()
                            },
                            onFocusTraining = {
                                selectedTab = CalendarViewTab.CYCLE_PLANNER
                            }
                        )
                    }
                }
            }

            CalendarViewTab.CYCLE_PLANNER -> {
                item {
                    TrainingCyclePlannerAssistant(
                        ludusState = ludusState,
                        gladiators = state.gladiators,
                        nextEvent = calendarEvents.firstOrNull(),
                        onAssignTrainingFocus = onAssignTrainingFocus,
                        onNavigateToPhysicianTent = onNavigateToPhysicianTent,
                        onNavigateToTrainingArena = onNavigateToTrainingArena
                    )
                }
            }

            CalendarViewTab.CHAMPIONSHIP_ROADMAP -> {
                item {
                    ChampionshipRoadmapCard(
                        ludusState = ludusState
                    )
                }
            }
        }
    }
}

// ------------------------------------------------------------------------------------------------
// Subcomponents
// ------------------------------------------------------------------------------------------------

@Composable
private fun CalendarHeroHeaderCard(
    ludusState: LudusState,
    isMatchDay: Boolean,
    daysUntilNext: Int,
    onNavigateToMatchLobby: () -> Unit,
    onNavigateToTrainingArena: () -> Unit,
    onNavigateToPhysicianTent: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
        border = BorderStroke(1.2.dp, if (isMatchDay) ImmersiveGold else ImmersiveCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            if (isMatchDay) ImmersiveTerracottaDeep.copy(alpha = 0.5f) else ImmersiveCardBgSecondary,
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SCHEDULED FIGHTS CALENDAR",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            ),
                            color = ImmersiveGold
                        )
                    }
                    Text(
                        text = "Müsabaka Takvimi & Talim Döngüsü Stratejisi",
                        style = MaterialTheme.typography.bodySmall,
                        color = ImmersiveTextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isMatchDay) ImmersiveEmerald else ImmersiveTrack,
                    border = BorderStroke(1.dp, if (isMatchDay) ImmersiveEmerald else ImmersiveCardBorder)
                ) {
                    Text(
                        text = if (isMatchDay) "⚔️ DÖVÜŞ GÜNÜ" else "⏳ ${daysUntilNext} GÜN KALDI",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        ),
                        color = if (isMatchDay) Color.Black else ImmersiveGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ImmersiveTrack)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "MEVCUT FAZ",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = ImmersiveTextMuted
                    )
                    Text(
                        text = "${ludusState.day}. GÜN (${ludusState.phase.title})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = ImmersiveTextPrimary
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(ImmersiveCardBorder))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "BÖLGE & ARENA",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = ImmersiveTextMuted
                    )
                    Text(
                        text = ludusState.cityTier.cityName,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = ImmersiveGold
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(ImmersiveCardBorder))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLANLANAN MAÇ",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = ImmersiveTextMuted
                    )
                    Text(
                        text = "${ludusState.upcomingCalendarEvents.size.coerceAtLeast(1)} Etkinlik",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = ImmersiveTerracottaLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fast Hub Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToMatchLobby,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(38.dp)
                        .testTag("cal_goto_lobby_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMatchDay) ImmersiveGold else ImmersiveTerracotta,
                        contentColor = if (isMatchDay) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isMatchDay) Icons.Default.SportsMartialArts else Icons.Default.Gavel,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isMatchDay) "ARENAYA GİR & DÖVÜŞ" else "MAÇ LOBİSİ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.5.sp)
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToTrainingArena,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("cal_goto_training_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ImmersiveGold),
                    border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text(
                        text = "🏋️ Talim Alanı",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToPhysicianTent,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("cal_goto_physician_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF81C784)),
                    border = BorderStroke(1.dp, Color(0xFF81C784).copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text(
                        text = "🌿 Hekim",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineDayItem(
    day: Int,
    isCurrentDay: Boolean,
    hasEvent: Boolean,
    eventFormat: MatchFormat?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(64.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag("timeline_day_$day"),
        shape = RoundedCornerShape(10.dp),
        color = when {
            isSelected -> ImmersiveGold.copy(alpha = 0.25f)
            hasEvent -> ImmersiveTerracottaDeep.copy(alpha = 0.35f)
            isCurrentDay -> ImmersiveCardBgSecondary
            else -> ImmersiveCard
        },
        border = BorderStroke(
            width = if (isSelected || isCurrentDay) 1.5.dp else 1.dp,
            color = when {
                isSelected -> ImmersiveGold
                hasEvent -> ImmersiveGold.copy(alpha = 0.8f)
                isCurrentDay -> ImmersiveEmerald
                else -> ImmersiveCardBorder
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isCurrentDay) "BUGÜN" else "GÜN $day",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 9.sp
                ),
                color = when {
                    isCurrentDay -> ImmersiveEmerald
                    hasEvent -> ImmersiveGold
                    else -> ImmersiveTextMuted
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when {
                    hasEvent && eventFormat == MatchFormat.SINE_MISSIO -> "💀"
                    hasEvent && eventFormat == MatchFormat.TEAM_2V2 -> "⚔️⚔️"
                    hasEvent -> "⚔️"
                    isCurrentDay -> "📍"
                    else -> "🏋️"
                },
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when {
                    hasEvent -> "DÖVÜŞ"
                    isCurrentDay -> "Talim"
                    else -> "İdman"
                },
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold),
                color = if (hasEvent) ImmersiveTerracottaLight else ImmersiveTextMuted
            )
        }
    }
}

@Composable
private fun ScheduledArenaEventCard(
    event: ScheduledArenaEvent,
    currentDay: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onNavigateToArena: () -> Unit,
    onFocusTraining: () -> Unit
) {
    val daysLeft = (event.targetDay - currentDay).coerceAtLeast(0)
    val isToday = daysLeft == 0
    val boss = event.featuredOpponent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scheduled_event_card_${event.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
        border = BorderStroke(
            width = if (isToday) 1.5.dp else 1.dp,
            color = if (isToday) ImmersiveGold else ImmersiveCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isToday) ImmersiveEmerald else ImmersiveGold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GÜN ${event.targetDay} • ${event.cityTier.cityName.uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = ImmersiveGold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isToday) ImmersiveEmerald.copy(alpha = 0.2f) else ImmersiveTrack,
                    border = BorderStroke(1.dp, if (isToday) ImmersiveEmerald else ImmersiveCardBorder)
                ) {
                    Text(
                        text = if (isToday) "🔥 BUGÜN!" else "🗓️ ${daysLeft} GÜN KALDI",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        ),
                        color = if (isToday) ImmersiveEmerald else ImmersiveTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Host
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                ),
                color = ImmersiveTextPrimary
            )
            Text(
                text = "Düzenleyen: ${event.hostPatron} • Arena: ${event.arenaName}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ImmersiveTextMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Badges Row (Format, Special Rule, Hype)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ImmersiveTerracotta.copy(alpha = 0.2f),
                    border = BorderStroke(0.8.dp, ImmersiveTerracotta)
                ) {
                    Text(
                        text = event.matchFormat.title,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold),
                        color = ImmersiveTerracottaLight
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ImmersiveGold.copy(alpha = 0.15f),
                    border = BorderStroke(0.8.dp, ImmersiveGold.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = event.crowdHypeText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold),
                        color = ImmersiveGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Featured Opponent Dossier Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ImmersiveTrack)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = ImmersiveCardBgSecondary,
                        border = BorderStroke(1.dp, ImmersiveCardBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = when (boss.gladiatorClass) {
                                    GladiatorClass.MURMILLO -> "🛡️"
                                    GladiatorClass.RETIARIUS -> "🔱"
                                    GladiatorClass.THRAEX -> "🗡️"
                                    GladiatorClass.DIMACHAERUS -> "⚔️"
                                    GladiatorClass.SAMNITE -> "🪓"
                                },
                                fontSize = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "RAKİP ŞAMPİYON: ${boss.title}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                            color = ImmersiveTerracottaLight
                        )
                        Text(
                            text = "${boss.gladiatorClass.displayName} • ${boss.traitName} (${boss.combatStance.title})",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                            color = ImmersiveTextMuted
                        )
                        Text(
                            text = "Tahmini Güç: STR ${boss.str} | AGI ${boss.agi} | STA ${boss.sta} | HP ${boss.maxHp}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 9.5.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = ImmersiveTextSecondary
                        )
                    }
                }
            }

            // Expanded Details (Reward Tiers & Tactical Scouting)
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    // Reward Tiers Card
                    RewardTiersBreakdownCard(event = event)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tactical Scouting Advice
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E1710),
                        border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🧠", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CASUSLUK RAPORU & TALİM TAVSİYESİ",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = ImmersiveGold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = event.tacticalAdvice,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ImmersiveTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Önerilen Talim Odağı:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = ImmersiveTextMuted
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ImmersiveGold.copy(alpha = 0.2f),
                                    border = BorderStroke(0.8.dp, ImmersiveGold)
                                ) {
                                    Text(
                                        text = "${event.recommendedFocus.icon} ${event.recommendedFocus.title}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        ),
                                        color = ImmersiveGold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onToggleExpand,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = if (isExpanded) "Daha Az Detay ▲" else "Ödül Kademeleri & Taktik Detaylar ▼",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        ),
                        color = ImmersiveGold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!isToday) {
                        OutlinedButton(
                            onClick = onFocusTraining,
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ImmersiveGold),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "🎯 Talim Planla",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            )
                        }
                    }

                    Button(
                        onClick = onNavigateToArena,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isToday) ImmersiveGold else ImmersiveTerracotta,
                            contentColor = if (isToday) Color.Black else Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text(
                            text = if (isToday) "⚔️ DÖVÜŞE GİR" else "Seç & Lobiye Git",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardTiersBreakdownCard(event: ScheduledArenaEvent) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ImmersiveCardBgSecondary)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💰 ÖDÜL KADEMELERİ & KAZANÇ HAVUZU",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = ImmersiveGold
            )
            Text(
                text = "Bahis: 1:2.4 Potansiyel",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = ImmersiveTextMuted
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Tier 1: Legendary Victory
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🥇", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Efsanevi Zafer (Seyirci > 80):",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ImmersiveGold
                )
            }
            Text(
                text = "+${(event.rewardGold * 1.35).toInt() + event.patronBonusGold} 🪙  +${(event.rewardPrestige * 1.4).toInt()} 🏛️",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                ),
                color = ImmersiveGold
            )
        }

        // Tier 2: Standard Decisive Victory
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🥈", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Standart Galibiyet:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ImmersiveTextPrimary
                )
            }
            Text(
                text = "+${event.rewardGold} 🪙  +${event.rewardPrestige} 🏛️",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                ),
                color = ImmersiveTextPrimary
            )
        }

        // Tier 3: Survival / Defeat
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🥉", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Mağlubiyet / Şerefli Hayatta Kalma:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ImmersiveTextMuted
                )
            }
            Text(
                text = "+${(event.rewardGold * 0.25).toInt()} 🪙 (Katılım)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp
                ),
                color = ImmersiveTextMuted
            )
        }
    }
}

@Composable
private fun TrainingCyclePlannerAssistant(
    ludusState: LudusState,
    gladiators: List<Gladiator>,
    nextEvent: ScheduledArenaEvent?,
    onAssignTrainingFocus: (Long, TrainingType) -> Unit,
    onNavigateToPhysicianTent: () -> Unit,
    onNavigateToTrainingArena: () -> Unit
) {
    val daysUntilNext = ludusState.daysUntilNextFight

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Routine Guide Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
            border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🎯 DÖNGÜSEL TALİM YOL HARİTASI (KALAN ${daysUntilNext} GÜN)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = ImmersiveGold
                )
                Text(
                    text = "Müsabaka gününe en yüksek kondisyon ve sıfır yorgunluk ile çıkmak için adımları takip edin.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = ImmersiveTextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Step 1: Heavy Drills
                CycleStepItem(
                    stepNumber = "1",
                    title = "Ağır Stat & Kondisyon İdmanı",
                    timing = "Bugün & Yarın (Kalan 2+ Gün)",
                    description = "Gladyatörün STR/AGI statlarını artırın. Karşı rakip ${nextEvent?.featuredOpponent?.title ?: "Şampiyon"} için ${nextEvent?.recommendedFocus?.title ?: "Çeviklik"} tavsiye edilir.",
                    icon = "🏋️",
                    badgeColor = ImmersiveGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Step 2: Sparring & Tactical Tuning
                CycleStepItem(
                    stepNumber = "2",
                    title = "Sparring & Silah Antrenmanı",
                    timing = "Müsabakadan 1 Gün Önce",
                    description = "Dövüş tecrübesi kazandırın. Gladyatörlerinizin moralini ve taktik uyumunu yükseltin.",
                    icon = "⚔️",
                    badgeColor = ImmersiveTerracottaLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Step 3: Medical Bath & Rest
                CycleStepItem(
                    stepNumber = "3",
                    title = "Hekim Çadırı Masajı & Dinlenme",
                    timing = "Müsabaka Arifesi",
                    description = "Yorgunluğu %0-%10 seviyesine indirin. Yüksek yorgunluk arenada %40'a varan hasar kaybına yol açar!",
                    icon = "🌿",
                    badgeColor = Color(0xFF81C784)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Step 4: Arena Match
                CycleStepItem(
                    stepNumber = "4",
                    title = "Arena Müsabakası & Büyük Zafer",
                    timing = "Müsabaka Günü",
                    description = "Tam HP ve dinç gladyatörünüzle arenaya çıkıp altın ve prestij kazanın!",
                    icon = "🏆",
                    badgeColor = ImmersiveGold
                )
            }
        }

        // Roster Readiness Matrix Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
            border = BorderStroke(1.dp, ImmersiveCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "👥 KADRO HAZIRLIK MATRİSİ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = ImmersiveGold
                        )
                        Text(
                            text = "Gladyatörlerin dövüşe hazır olma durumu ve talim odağı",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                            color = ImmersiveTextMuted
                        )
                    }

                    TextButton(onClick = onNavigateToTrainingArena) {
                        Text(
                            text = "Talimhane ➔",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (gladiators.isEmpty()) {
                    Text(
                        text = "Kadronuzda gladyatör bulunmuyor. Pazar & Tesisler ekranından köle satın alın.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ImmersiveTextMuted
                    )
                } else {
                    gladiators.forEach { gladiator ->
                        GladiatorReadinessRow(
                            gladiator = gladiator,
                            onAssignTrainingFocus = { focus ->
                                onAssignTrainingFocus(gladiator.id, focus)
                            },
                            onNavigateToPhysicianTent = onNavigateToPhysicianTent
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CycleStepItem(
    stepNumber: String,
    title: String,
    timing: String,
    description: String,
    icon: String,
    badgeColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ImmersiveTrack)
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = badgeColor.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, badgeColor)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = icon,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$stepNumber. $title",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                    color = ImmersiveTextPrimary
                )
                Text(
                    text = timing,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    color = badgeColor
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = ImmersiveTextMuted
            )
        }
    }
}

@Composable
private fun GladiatorReadinessRow(
    gladiator: Gladiator,
    onAssignTrainingFocus: (TrainingType) -> Unit,
    onNavigateToPhysicianTent: () -> Unit
) {
    val hpPercent = (gladiator.currentHp.toFloat() / gladiator.maxHp.coerceAtLeast(1)).coerceIn(0f, 1f)
    val fatigue = gladiator.fatigue
    val isInjured = gladiator.isInjured
    val readinessScore = calculateReadinessScore(gladiator)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = ImmersiveCardBgSecondary,
        border = BorderStroke(
            1.dp,
            if (isInjured) ImmersiveTerracotta else ImmersiveCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = gladiator.name,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                            color = ImmersiveTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${gladiator.gladiatorClass.displayName})",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = ImmersiveGold
                        )
                        if (isInjured) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "🩹 SAKAT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Black), color = ImmersiveTerracotta)
                        }
                    }
                    Text(
                        text = "HP: ${gladiator.currentHp}/${gladiator.maxHp} • Yorgunluk: %$fatigue • STR ${gladiator.str} | AGI ${gladiator.agi}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = ImmersiveTextMuted
                    )
                }

                // Readiness Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        readinessScore >= 80 -> ImmersiveEmerald.copy(alpha = 0.2f)
                        readinessScore >= 50 -> ImmersiveGold.copy(alpha = 0.2f)
                        else -> ImmersiveTerracotta.copy(alpha = 0.2f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            readinessScore >= 80 -> ImmersiveEmerald
                            readinessScore >= 50 -> ImmersiveGold
                            else -> ImmersiveTerracotta
                        }
                    )
                ) {
                    Text(
                        text = "Hazırlık: %$readinessScore",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 9.5.sp
                        ),
                        color = when {
                            readinessScore >= 80 -> ImmersiveEmerald
                            readinessScore >= 50 -> ImmersiveGold
                            else -> ImmersiveTerracottaLight
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Training Focus Selector Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Talim:",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold),
                    color = ImmersiveTextMuted
                )

                listOf(
                    TrainingType.STRENGTH,
                    TrainingType.AGILITY,
                    TrainingType.STAMINA,
                    TrainingType.DEFENSE,
                    TrainingType.REST
                ).forEach { focus ->
                    val isSelected = gladiator.trainingFocus == focus
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onAssignTrainingFocus(focus) },
                        color = if (isSelected) ImmersiveGold else ImmersiveTrack,
                        border = BorderStroke(
                            0.8.dp,
                            if (isSelected) ImmersiveGold else ImmersiveCardBorder
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${focus.icon} ${focus.title}",
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                            ),
                            color = if (isSelected) Color.Black else ImmersiveTextSecondary
                        )
                    }
                }
            }
        }
    }
}

private fun calculateReadinessScore(gladiator: Gladiator): Int {
    if (gladiator.isInjured) return 25
    val hpScore = ((gladiator.currentHp.toFloat() / gladiator.maxHp.coerceAtLeast(1)) * 50).toInt()
    val fatiguePenalty = (gladiator.fatigue * 0.4f).toInt()
    val moraleBonus = (gladiator.mor * 0.1f).toInt()
    return (hpScore + 40 - fatiguePenalty + moraleBonus).coerceIn(10, 100)
}

@Composable
private fun ChampionshipRoadmapCard(ludusState: LudusState) {
    val currentTier = ludusState.cityTier

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
            border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🏆 ROMA ARENA ŞAMPİYONA YOL HARİTASI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = ImmersiveGold
                )
                Text(
                    text = "Küçük kasaba muneralarından Roma Colosseum İmparatorluk Şampiyonluğuna giden yol.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = ImmersiveTextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                CityTier.entries.forEachIndexed { index, tier ->
                    val isUnlocked = tier.tierNumber <= currentTier.tierNumber
                    val isCurrent = tier == currentTier

                    RoadmapTierItem(
                        tier = tier,
                        isUnlocked = isUnlocked,
                        isCurrent = isCurrent,
                        isLast = index == CityTier.entries.size - 1
                    )
                }
            }
        }
    }
}

@Composable
private fun RoadmapTierItem(
    tier: CityTier,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = when {
                    isCurrent -> ImmersiveGold
                    isUnlocked -> ImmersiveEmerald
                    else -> ImmersiveTrack
                },
                border = BorderStroke(1.dp, if (isUnlocked) ImmersiveGold else ImmersiveCardBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isUnlocked) "🏛️" else "🔒",
                        fontSize = 14.sp
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(if (isUnlocked) ImmersiveGold.copy(alpha = 0.5f) else ImmersiveCardBorder)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${tier.tierNumber}. SEVİYE: ${tier.cityName.uppercase()}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = if (isCurrent) ImmersiveGold else if (isUnlocked) ImmersiveTextPrimary else ImmersiveTextMuted
                )
                if (isCurrent) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ImmersiveGold.copy(alpha = 0.2f),
                        border = BorderStroke(0.8.dp, ImmersiveGold)
                    ) {
                        Text(
                            text = "MEVCUT BÖLGE",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 8.5.sp
                            ),
                            color = ImmersiveGold
                        )
                    }
                }
            }

            Text(
                text = tier.arenaLevelName,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = if (isUnlocked) ImmersiveTerracottaLight else ImmersiveTextMuted
            )

            Text(
                text = tier.title,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = ImmersiveTextMuted
            )
        }
    }
}

@Composable
private fun EmptyCalendarEventsCard(onResetFilter: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
        border = BorderStroke(1.dp, ImmersiveCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🗓️", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Seçilen Filtreye Uygun Müsabaka Bulunamadı",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = ImmersiveTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gelecek günlerde daha fazla etkinlik yer alacak.",
                style = MaterialTheme.typography.bodySmall,
                color = ImmersiveTextMuted
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onResetFilter,
                colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Tüm Müsabakaları Göster", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
