package com.example

import com.example.model.*
import com.example.simulation.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Automated acceptance tests for the Complete UI / UX Overhaul.
 * Verifies:
 * 1. Navigation consolidation to 7 primary destinations with smart mapping.
 * 2. Attention / Inbox intelligence (critical injury, scheduled bout, damaged gear).
 * 3. Pre-flight checklist & "What Changed?" summary generation upon day advance.
 * 4. Pinned gladiators quick-access logic.
 * 5. Integrated profile actions: Training plan updates and Medical injury treatments.
 * 6. 100-Day autonomous simulation stress test verifying zero state corruption.
 */
class UiOverhaulConsistencyTest {

    private lateinit var viewModel: LudusViewModel

    @Before
    fun setup() {
        viewModel = LudusViewModel()
    }

    @Test
    fun test1_navigationConsolidation() {
        val state = viewModel.uiState.value

        // Initial default screen is DASHBOARD (Home)
        assertEquals(ActiveScreen.DASHBOARD, state.currentScreen)

        // Navigate to GLADIATORS (ROSTER)
        viewModel.navigateTo(ActiveScreen.ROSTER)
        assertEquals(ActiveScreen.ROSTER, viewModel.uiState.value.currentScreen)

        // Navigate to LUDUS (FACILITIES)
        viewModel.navigateTo(ActiveScreen.FACILITIES)
        assertEquals(ActiveScreen.FACILITIES, viewModel.uiState.value.currentScreen)

        // Navigate to MARKET (EQUIPMENT_MARKET)
        viewModel.navigateTo(ActiveScreen.EQUIPMENT_MARKET)
        assertEquals(ActiveScreen.EQUIPMENT_MARKET, viewModel.uiState.value.currentScreen)

        // Navigate to ARENA (ARENA_HUB)
        viewModel.navigateTo(ActiveScreen.ARENA_HUB)
        assertEquals(ActiveScreen.ARENA_HUB, viewModel.uiState.value.currentScreen)

        // Navigate to POLITICS
        viewModel.navigateTo(ActiveScreen.POLITICS)
        assertEquals(ActiveScreen.POLITICS, viewModel.uiState.value.currentScreen)

        // Navigate to CHRONICLE
        viewModel.navigateTo(ActiveScreen.CHRONICLE)
        assertEquals(ActiveScreen.CHRONICLE, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun test2_attentionItemsIntelligence() {
        val state = viewModel.uiState.value
        val attention = viewModel.generateAttentionItems(state)

        // Attention items should exist and be prioritized
        assertTrue("Attention items should not be empty", attention.isNotEmpty())

        // Highest priority should be first
        for (i in 0 until attention.size - 1) {
            assertTrue(
                "Items must be sorted by priority ordinal",
                attention[i].priority.ordinal <= attention[i + 1].priority.ordinal
            )
        }

        // Each item must have valid title, action label, and target screen
        attention.forEach { item ->
            assertTrue("Title must not be blank", item.title.isNotBlank())
            assertTrue("Message must not be blank", item.message.isNotBlank())
            assertTrue("Action label must not be blank", item.actionLabel.isNotBlank())
            assertNotNull("Target screen must be specified", item.targetScreen)
        }
    }

    @Test
    fun test3_dayAdvanceSummaryGeneration() {
        val initialDay = viewModel.uiState.value.dominus.dayNumber

        // Open end-day confirmation checklist
        viewModel.openEndDayChecklist()
        assertTrue("End day confirmation dialog should be active", viewModel.uiState.value.showEndDayConfirmation)

        // Confirm end day
        viewModel.confirmEndDay()
        assertFalse("End day confirmation dialog should be closed", viewModel.uiState.value.showEndDayConfirmation)

        val updatedState = viewModel.uiState.value
        assertEquals("Day should increment by 1", initialDay + 1, updatedState.dominus.dayNumber)
        assertTrue("What changed dialog should be displayed", updatedState.showWhatChangedDialog)
        assertNotNull("DayAdvanceSummary must be populated", updatedState.dayAdvanceSummary)

        val summary = updatedState.dayAdvanceSummary!!
        assertEquals(initialDay, summary.fromDay)
        assertEquals(initialDay + 1, summary.toDay)

        // Dismiss what changed dialog
        viewModel.dismissWhatChangedDialog()
        assertFalse(viewModel.uiState.value.showWhatChangedDialog)
    }

    @Test
    fun test4_gladiatorPinning() {
        val firstGladId = viewModel.uiState.value.gladiators.first().id

        // Initially glad_1 is pinned by default
        assertTrue(viewModel.uiState.value.pinnedGladiatorIds.contains(firstGladId))

        // Toggle pin (unpin)
        viewModel.togglePinGladiator(firstGladId)
        assertFalse(viewModel.uiState.value.pinnedGladiatorIds.contains(firstGladId))

        // Toggle pin again (re-pin)
        viewModel.togglePinGladiator(firstGladId)
        assertTrue(viewModel.uiState.value.pinnedGladiatorIds.contains(firstGladId))
    }

    @Test
    fun test5_trainingPlanAndMedicalTreatmentActions() {
        val glad = viewModel.uiState.value.gladiators.first()

        // 1. Update training plan
        viewModel.updateTrainingPlan(glad.id, "Ağır Kalkan & Savunma", "Zengin Protein")
        assertTrue(viewModel.uiState.value.statusMessage.contains("Ağır Kalkan"))

        // 2. Medical treatment test
        // Add a mock injury to glad
        val injuredGlad = glad.copy(
            condition = glad.condition.copy(health = 60),
            injuries = listOf(Injury("Kesik Yara", "Orta", 3, 2))
        )
        // Treat injury
        viewModel.treatInjury(injuredGlad.id, "Kesik Yara")
        assertTrue(viewModel.uiState.value.statusMessage.contains("Medicus"))
    }

    @Test
    fun test6_full100DayStressTestWithUIState() {
        var state = viewModel.uiState.value

        for (day in 1..100) {
            viewModel.advanceDay()
            state = viewModel.uiState.value

            // Verify UI models remain valid
            assertNotNull("DayAdvanceSummary must exist on day $day", state.dayAdvanceSummary)
            assertTrue("Attention items must be evaluated on day $day", state.attentionItems.isNotEmpty())
            assertTrue("Pinned gladiators must be preserved on day $day", state.pinnedGladiatorIds.isNotEmpty())

            // Verify core simulation invariants
            val validation = WorldSimulationValidator.validateWorldState(state)
            assertTrue(
                "World simulation must remain completely consistent on day $day: ${validation.violations}",
                validation.isValid
            )
        }

        assertEquals(112, state.dominus.dayNumber) // Started at 12, advanced 100 days
    }
}
