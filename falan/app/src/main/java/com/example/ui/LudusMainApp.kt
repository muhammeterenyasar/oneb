package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppScreen
import com.example.model.Gladiator
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*

@Composable
fun LudusMainApp(
    viewModel: LudusViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // If notification comes, show snackbar
    LaunchedEffect(state.notificationMessage) {
        state.notificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBg),
        topBar = {
            Column {
                RomanHeaderBanner(
                    ludusState = state.ludusState,
                    onAdvancePhaseClick = { viewModel.advanceDayPhase() }
                )
                ThreatWarningBanner(
                    ludusState = state.ludusState,
                    onManageDebtClick = { viewModel.navigateTo(AppScreen.MARKET_AND_FACILITIES) }
                )
            }
        },
        bottomBar = {
            // Only show bottom navigation if not in active battle simulation
            if (state.activeBattle == null) {
                Surface(
                    color = ImmersiveCard,
                    border = BorderStroke(1.dp, ImmersiveCardBorder)
                ) {
                    NavigationBar(
                        containerColor = ImmersiveCard,
                        contentColor = ImmersiveTextPrimary,
                        tonalElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ludus_bottom_navigation")
                    ) {
                        AppScreen.entries.filter { it.isPrimaryBottomNav }.forEach { screen ->
                            val isSelected = state.currentScreen == screen
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(screen) },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title,
                                        tint = if (isSelected) ImperialRomanRed else ImmersiveTextMuted
                                    )
                                },
                                label = {
                                    Text(
                                        text = screen.shortTitle,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            fontSize = 10.sp,
                                            letterSpacing = 0.3.sp
                                        ),
                                        color = if (isSelected) ImperialRomanRed else ImmersiveTextMuted
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = ImperialRedSurface,
                                    selectedIconColor = ImperialRomanRed,
                                    selectedTextColor = ImperialRomanRed,
                                    unselectedIconColor = ImmersiveTextMuted,
                                    unselectedTextColor = ImmersiveTextMuted
                                ),
                                modifier = Modifier.testTag("nav_screen_${screen.route}")
                            )
                        }
                    }

                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ImmersiveBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // If Battle Simulation is active, display Battle Screen
            if (state.activeBattle != null) {
                BattleSimulationScreen(
                    state = state,
                    onSetSpeed = { viewModel.setBattleSpeed(it) },
                    onInstantFinish = { viewModel.instantFinishBattle() },
                    onDismissBattle = {
                        viewModel.dismissBattle()
                        viewModel.navigateTo(AppScreen.LUDUS_OVERVIEW)
                    },
                    onCrowdJudgement = { viewModel.resolveCrowdJudgement(it) },
                    onShoutLanista = { viewModel.shoutLanistaTactic(it) }
                )
            } else {
                // Centralized Screen-Switching Navigation Body
                AnimatedContent(
                    targetState = state.currentScreen,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "screen_transition"
                ) { targetScreen ->
                    when (targetScreen) {
                        AppScreen.LUDUS_OVERVIEW -> LudusOverviewScreen(
                            state = state,
                            onGladiatorClick = { gladiator ->
                                viewModel.selectGladiator(gladiator)
                                viewModel.navigateTo(AppScreen.TRAINING_ARENA)
                            },
                            onAssignTrainingFocus = { id, focus -> viewModel.assignTrainingFocus(id, focus) },
                            onSelectDiet = { diet -> viewModel.setDietPlan(diet) },
                            onPromoteToTeacher = { gladiator -> viewModel.promoteGladiatorToTeacher(gladiator) },
                            onNavigateToTraining = { viewModel.navigateTo(AppScreen.TRAINING_ARENA) },
                            onNavigateToArena = { viewModel.navigateTo(AppScreen.MATCH_LOBBY) },
                            onNavigateToMarket = { viewModel.navigateTo(AppScreen.MARKET_AND_FACILITIES) },
                            onNavigateToPhysician = { viewModel.navigateTo(AppScreen.PHYSICIAN_TENT) },
                            onNavigateToCalendar = { viewModel.navigateTo(AppScreen.SCHEDULED_FIGHTS) },
                            onNavigateToArmory = { viewModel.navigateTo(AppScreen.ARMORY_EQUIPMENT) },
                            onNavigateToHallOfFame = { viewModel.navigateTo(AppScreen.HALL_OF_FAME) },
                            onNavigateToLeague = { viewModel.navigateTo(AppScreen.RIVAL_LEAGUE) },
                            onUnlockPerk = { viewModel.unlockLanistaPerk(it) },
                            onOpenSparring = { viewModel.openSparringDialog() },
                            onOpenTesserae = { viewModel.openTesseraeDialog() },
                            onOpenTavern = { viewModel.openSuburaTavernDialog() },
                            onOpenDilemma = { viewModel.openDilemmaDialog() },
                            onOpenEvents = { viewModel.openBetweenCycleEventsDialog() },
                            onRollRandomEvent = { viewModel.rollRandomDowntimeEvent() }
                        )

                        AppScreen.TRAINING_ARENA -> TrainingArenaScreen(
                            state = state,
                            onSelectGladiator = { viewModel.selectGladiator(it) },
                            onAssignTrainingFocus = { id, focus -> viewModel.assignTrainingFocus(id, focus) },
                            onAssignSpecificDrill = { id, drill -> viewModel.assignSpecificDrill(id, drill) },
                            onBulkAssignDrill = { drill -> viewModel.bulkAssignDrillToAll(drill) },
                            onExecuteDrillNow = { id -> viewModel.executeSingleGladiatorDrillNow(id) },
                            onExecuteAllDrillsNow = { viewModel.executeAllDrillsNow() },
                            onSelectDrillCategory = { cat -> viewModel.setSelectedDrillCategory(cat) },
                            onDismissDrillOutcome = { viewModel.dismissDrillOutcome() },
                            onOpenSparring = { viewModel.openSparringDialog() },
                            onManumitGladiator = { viewModel.manumitGladiator(it) },
                            onPromoteToTeacher = { viewModel.promoteGladiatorToTeacher(it) },
                            onTogglePromiseOfFreedom = { id, enable -> viewModel.togglePromiseOfFreedom(enable) },
                            onInstantHealClick = { viewModel.buyInstantHealPotion(it) },
                            onNavigateToMarket = { viewModel.navigateTo(AppScreen.MARKET_AND_FACILITIES) },
                            onNavigateToPhysician = { viewModel.navigateTo(AppScreen.PHYSICIAN_TENT) },
                            onNavigateToCalendar = { viewModel.navigateTo(AppScreen.SCHEDULED_FIGHTS) }
                        )
                        AppScreen.PHYSICIAN_TENT -> PhysicianTentScreen(
                            state = state,
                            onUpgradePhysician = { viewModel.upgradePhysician() },
                            onApplyHerbalPoultice = { viewModel.applyHerbalPoulticeTreatment(it) },
                            onApplyThermalBath = { viewModel.applyThermalBathTreatment(it) },
                            onPerformSurgery = { viewModel.performEmergencySurgery(it) },
                            onInstantHealWithGold = { viewModel.applyInstantHealWithGold(it) }
                        )
                        AppScreen.MATCH_LOBBY -> MatchPrepScreen(
                            state = state,
                            onSelectGladiator = { viewModel.selectGladiator(it) },
                            onSelectOpponent = { viewModel.selectOpponent(it) },
                            onSelectFormat = { viewModel.selectFormat(it) },
                            onSelectTactic = { viewModel.selectTactic(it) },
                            onSetBet = { viewModel.setBetAmount(it) },
                            onTogglePromiseOfFreedom = { viewModel.togglePromiseOfFreedom(it) },
                            onStartBattle = { viewModel.startBattleSimulation() },
                            onAdvancePhase = { viewModel.advanceDayPhase() },
                            onNavigateToCalendar = { viewModel.navigateTo(AppScreen.SCHEDULED_FIGHTS) }
                        )
                        AppScreen.SCHEDULED_FIGHTS -> ScheduledFightsCalendarScreen(
                            state = state,
                            onNavigateToMatchLobby = { viewModel.navigateTo(AppScreen.MATCH_LOBBY) },
                            onNavigateToTrainingArena = { viewModel.navigateTo(AppScreen.TRAINING_ARENA) },
                            onNavigateToPhysicianTent = { viewModel.navigateTo(AppScreen.PHYSICIAN_TENT) },
                            onAssignTrainingFocus = { id, focus -> viewModel.assignTrainingFocus(id, focus) },
                            onSelectOpponent = { opponent -> viewModel.selectOpponent(opponent) }
                        )
                        AppScreen.MARKET_AND_FACILITIES -> MarketAndDebtScreen(
                            state = state,
                            onRecruitGladiator = { viewModel.recruitGladiator(it) },
                            onHireTeacher = { viewModel.hireTeacher(it) },
                            onDismissTeacher = { viewModel.dismissTeacher(it) },
                            onUpgradePhysician = { viewModel.upgradePhysician() },
                            onHireGuard = { viewModel.hireGuard() },
                            onRepayDebt = { viewModel.repayDebt(it) }
                        )
                        AppScreen.IMPERIAL_SHOP -> LudusShopScreen(
                            state = state,
                            onBuyInstantHeal = { viewModel.buyInstantHealPotion(it) },
                            onBuyExpansion = { viewModel.buyExpansionSlots() },
                            onClaimRewardedAd = { viewModel.claimRewardedAd() },
                            onBuySenatorSponsorship = { viewModel.buySenatorSponsorship() },
                            onRepayDebt = { viewModel.repayDebt(it) }
                        )
                        AppScreen.IMPERIAL_CAMPAIGN -> ImperialCampaignScreen(
                            state = state,
                            onStartBossFight = { mission, gladiator -> viewModel.startCampaignBossFight(mission, gladiator) }
                        )
                        AppScreen.ARMORY_EQUIPMENT -> ArmoryEquipmentScreen(
                            state = state,
                            onSelectGladiator = { viewModel.selectGladiator(it) },
                            onBuyAndEquip = { gladiatorId, itemId -> viewModel.buyAndEquipItem(gladiatorId, itemId) },
                            onUnequip = { gladiatorId, slot -> viewModel.unequipItem(gladiatorId, slot) }
                        )
                        AppScreen.HALL_OF_FAME -> HallOfFameScreen(
                            state = state
                        )
                        AppScreen.RIVAL_LEAGUE -> RivalLeagueScreen(
                            state = state
                        )
                    }
                }
            }


            // Daily Cycle Modal / Notification Dialog
            if (state.dailyCycleModalMessage != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.clearNotification() },
                    title = {
                        Text(
                            text = "🏛️ GÜN RAPORU & HESAP KESİMİ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = ImmersiveGold
                        )
                    },
                    text = {
                        Text(
                            text = state.dailyCycleModalMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ImmersiveTextPrimary
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.clearNotification() },
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold)
                        ) {
                            Text("Tamam", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    },
                    containerColor = ImmersiveCard,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Interactive Sparring Dialog
            if (state.showSparringDialog) {
                SparringArenaDialog(
                    gladiators = state.gladiators,
                    selectedFighter1 = state.sparringFighter1,
                    selectedFighter2 = state.sparringFighter2,
                    activeSparring = state.activeSparring,
                    onSelectFighters = { f1, f2 -> viewModel.setSparringFighters(f1, f2) },
                    onStartSparring = { viewModel.executeSparring() },
                    onDismiss = { viewModel.closeSparringDialog() }
                )
            }

            // Interactive Tesserae Dice Game Dialog
            if (state.showTesseraeDialog) {
                TesseraeGamblingDialog(
                    tesseraeState = state.activeTesserae,
                    playerGold = state.ludusState.gold,
                    onRollDice = { bet -> viewModel.rollTesserae(bet) },
                    onDismiss = { viewModel.closeTesseraeDialog() }
                )
            }

            // Subura Tavern & Espionage Dialog
            if (state.showSuburaTavernDialog) {
                SuburaTavernDialog(
                    gladiators = state.gladiators,
                    ludusState = state.ludusState,
                    onScoutWeakness = { viewModel.scoutOpponentWeakness() },
                    onBuyMulsumFeast = { viewModel.buyMulsumFeast() },
                    onBribeTownCriers = { viewModel.bribeTownCriers() },
                    onApplyThermalMassage = { id -> viewModel.applyThermalMassage(id) },
                    onSharpenArsenal = { viewModel.sharpenArsenal() },
                    onAccelerateInjuryHeal = { id -> viewModel.accelerateInjuryHeal(id) },
                    onDismiss = { viewModel.closeSuburaTavernDialog() }
                )
            }

            // Daily Dilemma Dialog
            if (state.showDilemmaDialog || state.ludusState.currentDilemma != null && state.showDilemmaDialog) {
                DailyDilemmaDialog(
                    dilemma = state.ludusState.currentDilemma,
                    playerGold = state.ludusState.gold,
                    onChooseOption = { option ->
                        viewModel.chooseDilemmaOption(option)
                        viewModel.closeDilemmaDialog()
                    },
                    onDismiss = { viewModel.closeDilemmaDialog() }
                )
            }

            // Between-Cycle Events & Festivals Dialog
            if (state.showBetweenCycleEventDialog) {
                BetweenCycleEventsDialog(
                    event = state.activeBetweenCycleEvent,
                    gladiators = state.gladiators,
                    selectedGladiator = state.selectedEventGladiator,
                    playerGold = state.ludusState.gold,
                    lastResult = state.lastEventResult,
                    onSelectGladiator = { viewModel.selectEventGladiator(it) },
                    onChooseChoice = { viewModel.chooseBetweenCycleEventChoice(it) },
                    onRollRandomEvent = { viewModel.rollRandomDowntimeEvent() },
                    onDismiss = { viewModel.closeBetweenCycleEventsDialog() }
                )
            }
        }
    }
}

