package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simulation.ActiveScreen
import com.example.simulation.LudusViewModel
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanHeaderBar
import com.example.ui.components.RomanNavRail
import com.example.ui.dialogs.*
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LudusTheme {
                LudusApp()
            }
        }
    }
}

@Composable
fun LudusApp(viewModel: LudusViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = RomanBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Global Bar (Shown on all management screens)
                if (state.currentScreen != ActiveScreen.LIVE_COMBAT) {
                    RomanHeaderBar(
                        dominus = state.dominus,
                        onAdvanceDay = { viewModel.openEndDayChecklist() },
                        onOpenSearch = { viewModel.openSearchDialog() },
                        onOpenAttention = { viewModel.navigateTo(ActiveScreen.DASHBOARD) },
                        attentionCount = state.attentionItems.size
                    )
                }

                // Main Content Body with Navigation Rail
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Navigation Rail (Hidden during live arena combat for full immersion)
                    if (state.currentScreen != ActiveScreen.LIVE_COMBAT) {
                        RomanNavRail(
                            currentScreen = state.currentScreen,
                            onNavigate = { screen -> viewModel.navigateTo(screen) }
                        )
                    }

                    // Dynamic Screen Content
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        when (state.currentScreen) {
                            ActiveScreen.DASHBOARD -> DashboardScreen(
                                state = state,
                                onNavigate = { viewModel.navigateTo(it) },
                                onSelectGladiator = { viewModel.selectGladiator(it) },
                                onExecuteAttentionAction = { viewModel.executeAttentionAction(it) }
                            )
                            ActiveScreen.ROSTER, ActiveScreen.GLADIATOR_PROFILE, ActiveScreen.TRAINING, ActiveScreen.MEDICAL -> GladiatorHubScreen(
                                state = state,
                                onSelectGladiator = { viewModel.selectGladiator(it) },
                                onTogglePin = { viewModel.togglePinGladiator(it) },
                                onUpdateTrainingPlan = { id, focus, diet -> viewModel.updateTrainingPlan(id, focus, diet) },
                                onTreatInjury = { gladId, injName -> viewModel.treatInjury(gladId, injName) },
                                onRepairItem = { item -> viewModel.repairItem(item, state.selectedGladiator?.id) },
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            ActiveScreen.ARENA_HUB -> ArenaHubScreen(
                                state = state,
                                onSelectVenue = { viewModel.selectVenue(it) },
                                onSelectScoutingOpponent = { viewModel.selectScoutingOpponent(it) },
                                onChallengeOpponent = { fighter, matchType -> viewModel.challengeOpponent(fighter, matchType) },
                                onStartScheduledMatch = { viewModel.prepareScheduledMatch() },
                                onAdvanceFightPhase = { viewModel.advanceFightDayPhase() },
                                onStartUndergroundFight = { viewModel.startUndergroundFight(it) }
                            )
                            ActiveScreen.MATCH_PREP -> MatchPrepScreen(
                                state = state,
                                onSelectFighter = { viewModel.setSelectedFighter(it) },
                                onSetStance = { viewModel.setStance(it) },
                                onSetTarget = { viewModel.setTarget(it) },
                                onStartMatch = { viewModel.startMatch() }
                            )
                            ActiveScreen.LIVE_COMBAT -> LiveCombatScreen(
                                state = state,
                                onSendCommand = { viewModel.sendTacticalCommand(it) },
                                onTogglePause = { viewModel.toggleCombatPause() },
                                onSetSpeed = { viewModel.setCombatSpeed(it) },
                                onDecidePolliceVerso = { spared, bribe -> viewModel.decidePolliceVerso(spared, bribe) }
                            )
                            ActiveScreen.POST_MATCH -> PostMatchScreen(
                                state = state,
                                onReturnToDashboard = { viewModel.navigateTo(ActiveScreen.DASHBOARD) }
                            )
                            ActiveScreen.FACILITIES, ActiveScreen.STAFF, ActiveScreen.ECONOMY -> LudusHubScreen(
                                state = state,
                                onUpgradeFacility = { viewModel.upgradeFacility(it) }
                            )
                            ActiveScreen.POLITICS -> PoliticsScreen(
                                state = state,
                                onResolveChoice = { viewModel.resolvePoliticalChoice(it) },
                                onDismissEvent = { viewModel.dismissPoliticalEvent() },
                                onSelectNpc = { viewModel.selectPoliticalNpc(it) },
                                onSelectFaction = { viewModel.selectPoliticalFaction(it) },
                                onExecuteInteraction = { npcId, act, secId -> viewModel.executePoliticalInteraction(npcId, act, secId) },
                                onSuppressScandal = { scandalId, useFavor -> viewModel.suppressScandal(scandalId, useFavor) },
                                onExposeSecret = { viewModel.exposeSecret(it) }
                            )
                            ActiveScreen.WORLD_MAP -> WorldMapScreen(state = state)
                            ActiveScreen.CHRONICLE -> StoryHubScreen(
                                state = state,
                                onSelectTab = { viewModel.selectStoryHubTab(it) },
                                onSelectMystery = { viewModel.selectMystery(it) },
                                onSelectRumor = { viewModel.selectRumor(it) },
                                onInvestigateMystery = { mId, pId -> viewModel.investigateMystery(mId, pId) },
                                onInvestigateRumor = { viewModel.investigateRumor(it) },
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                            ActiveScreen.EQUIPMENT_MARKET, ActiveScreen.RECRUITMENT -> MarketHubScreen(
                                state = state,
                                onPurchaseGladiator = { viewModel.purchaseMarketGladiator(it) },
                                onSelectMerchant = { viewModel.selectMarketMerchant(it) },
                                onSelectMarketCategory = { viewModel.setMarketCategory(it) },
                                onSelectMarketItem = { viewModel.selectMarketItem(it) },
                                onBuyItem = { viewModel.buyMarketItem(state.selectedMerchantId, it, state.selectedGladiator?.id ?: "glad_1") },
                                onSellItem = { viewModel.sellArmoryItem(it, state.selectedMerchantId) },
                                onRepairItem = { viewModel.repairItem(it, state.selectedGladiator?.id) },
                                onPlaceAuctionBid = { auc, amt -> viewModel.placeAuctionBid(auc.id, amt) },
                                onSubmitCommission = { viewModel.submitCustomCommission(it) }
                            )
                        }
                    }
                }
            }

            // Procedural Event Modal (e.g. Night sabotage, Senator visit)
            state.activeEvent?.let { event ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    RomanCard(
                        title = "ROMA OLAYI (EVENT)",
                        badge = event.title,
                        modifier = Modifier.width(460.dp)
                    ) {
                        Text(
                            text = event.title.uppercase(),
                            color = RomanGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = event.description,
                            color = RomanParchment,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.resolveEvent(chooseOptionA = true) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RomanCrimson,
                                    contentColor = RomanParchment
                                ),
                                shape = RoundedCornerShape(3.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Text(
                                    text = event.optionA,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                onClick = { viewModel.resolveEvent(chooseOptionA = false) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RomanBronzeDark,
                                    contentColor = RomanParchment
                                ),
                                shape = RoundedCornerShape(3.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Text(
                                    text = event.optionB,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Political Dilemma Modal
            state.activePoliticalEvent?.let { polEvent ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    RomanCard(
                        title = "SENATUS CONSULTUM: SİYASİ KRİZ",
                        badge = if (polEvent.isUrgent) "ACİL MÜDAHALE GEREKLİ" else "DİPLOMATİK TALEP",
                        modifier = Modifier.width(520.dp)
                    ) {
                        Text(
                            text = polEvent.title.uppercase(),
                            color = RomanGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = polEvent.narrative,
                            color = RomanParchment,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            polEvent.choices.forEach { choice ->
                                Button(
                                    onClick = { viewModel.resolvePoliticalChoice(choice) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RomanBronzeDark,
                                        contentColor = RomanParchment
                                    ),
                                    shape = RoundedCornerShape(3.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = choice.label,
                                            color = RomanGoldLight,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = choice.effectDescription,
                                            color = RomanParchmentDark,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 1. End Day Pre-flight Checklist Dialog
            if (state.showEndDayConfirmation) {
                EndDayChecklistDialog(
                    state = state,
                    onConfirmAdvance = { viewModel.confirmEndDay() },
                    onDismiss = { viewModel.dismissEndDayChecklist() }
                )
            }

            // 2. Post-advance "What Changed?" Recap Dialog
            if (state.showWhatChangedDialog && state.dayAdvanceSummary != null) {
                WhatChangedDialog(
                    summary = state.dayAdvanceSummary!!,
                    onDismiss = { viewModel.dismissWhatChangedDialog() }
                )
            }

            // 3. Global Quick Search Dialog
            if (state.showSearchDialog) {
                GlobalSearchDialog(
                    state = state,
                    onNavigateToResult = { screen, entId ->
                        if (entId != null && (screen == ActiveScreen.ROSTER || screen == ActiveScreen.GLADIATOR_PROFILE)) {
                            state.gladiators.find { it.id == entId }?.let { viewModel.selectGladiator(it) }
                        }
                        viewModel.navigateTo(screen)
                    },
                    onDismiss = { viewModel.dismissSearchDialog() }
                )
            }
        }
    }
}
