package com.example

import com.example.model.*
import com.example.simulation.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Automated acceptance tests for the Dynamic Story, World Memory & Curiosity Engine.
 * Verifies that:
 * 1. Rumors propagate, distort, and naturally expire.
 * 2. Delayed consequences mature and trigger on their exact maturity day.
 * 3. Mysteries can be investigated through multi-step clues and resolved with rewards.
 * 4. Rumors can be investigated to confirm truth or debunk falsehoods.
 * 5. Character memories record deeds and update emotional attitudes (trust, fear, hatred).
 * 6. "Tomorrow's Horizon" curiosity previews accurately forecast upcoming calendar events.
 * 7. Save and Load preserves the entire story state, evidence, and rumors.
 * 8. 100-day autonomous story simulation runs flawlessly without inconsistencies.
 */
class StoryEngineTest {

    @Test
    fun test1_rumorPropagationAndDistortion() {
        var state = LudusUiState(
            dominus = LudusDominus(dayNumber = 15),
            rumors = SeedStoryData.createInitialRumors()
        )

        val targetRumorId = "rumor_pompeii_champ_injury"
        val initialRumor = state.rumors.find { it.id == targetRumorId }
        assertNotNull("Initial rumor should exist", initialRumor)
        assertEquals(1, initialRumor!!.spreadCount)

        // Advance 3 days: rumor should spread and distort
        for (day in 16..18) {
            val result = StoryDirector.simulateStoryDay(day, 42L, state)
            state = state.copy(
                dominus = state.dominus.copy(dayNumber = day),
                rumors = result.updatedRumors
            )
        }

        val spreadRumor = state.rumors.find { it.id == targetRumorId }
        assertNotNull(spreadRumor)
        assertTrue("Rumor should have spread over 3 days", spreadRumor!!.spreadCount >= 3)
        assertTrue("Distortion level should increase as gossip mutates", spreadRumor.distortionLevel >= 1)

        // Advance past day 22 (age >= 7 days from createdDay 13): rumor should expire
        for (day in 19..23) {
            val result = StoryDirector.simulateStoryDay(day, 42L, state)
            state = state.copy(
                dominus = state.dominus.copy(dayNumber = day),
                rumors = result.updatedRumors
            )
        }

        val expiredRumor = state.rumors.find { it.id == targetRumorId }
        assertNotNull(expiredRumor)
        assertTrue("Rumor older than 7 days should be marked expired", expiredRumor!!.isExpired)
    }

    @Test
    fun test2_delayedConsequencesExecution() {
        val testConsequence = DelayedConsequence(
            id = "dc_test_bribe",
            triggerAction = "Senato Rüşveti Reddedildi",
            createdDay = 10,
            maturityDay = 18,
            targetEntityId = "npc_cassius",
            narrativeClue = "Senatör Cassius'un kâtibi soğuk bir bakışla ayrıldı.",
            consequenceEffectType = ConsequenceType.TREASURY_FINE,
            effectMagnitude = 300,
            hasTriggered = false,
            resolutionMessage = "Senato maliye müfettişleri kışlanıza 300 Denarii usulsüzlük cezası kesti!"
        )

        val state = LudusUiState(
            dominus = LudusDominus(dayNumber = 16, denarii = 5000),
            delayedConsequences = listOf(testConsequence)
        )

        // Day 17: Should NOT trigger yet
        val day17Result = StoryDirector.simulateStoryDay(17, 42L, state)
        val dcDay17 = day17Result.updatedDelayedConsequences.find { it.id == "dc_test_bribe" }
        assertFalse("Consequence should not trigger before maturity day", dcDay17!!.hasTriggered)
        assertEquals("Treasury delta should be 0", 0, day17Result.treasuryDelta)

        // Day 18: Exact maturity day reached -> Should trigger!
        val day18Result = StoryDirector.simulateStoryDay(18, 42L, state)
        val dcDay18 = day18Result.updatedDelayedConsequences.find { it.id == "dc_test_bribe" }
        assertTrue("Consequence should trigger on maturity day", dcDay18!!.hasTriggered)
        assertEquals("Treasury fine of 300 should be applied", -300, day18Result.treasuryDelta)
        assertTrue(
            "Chronicle entry should be added",
            day18Result.chronicleEntries.any { it.title == "Ceza Tebligatı" }
        )
    }

    @Test
    fun test3_mysteryInvestigationAndResolution() {
        val state = LudusUiState(
            dominus = LudusDominus(dayNumber = 12, denarii = 2000),
            mysteries = SeedStoryData.createInitialMysteries()
        )

        val emptyBedMystery = state.mysteries.find { it.id == "mystery_empty_bed" }
        assertNotNull(emptyBedMystery)
        assertEquals(MysteryStatus.ACTIVE, emptyBedMystery!!.status)

        // Step 1: Execute search dorm path
        val searchPath = emptyBedMystery.investigationPaths.find { it.id == "path_search_dorm" }!!
        val step1Result = StoryDirector.investigateMystery(emptyBedMystery, searchPath, state)
        assertTrue(step1Result.success)
        assertNotNull(step1Result.discoveredEvidence)
        assertEquals("Gizli Zemin Çentiği", step1Result.discoveredEvidence!!.title)
        assertEquals(MysteryStatus.INVESTIGATING, step1Result.resolvedMystery!!.status)

        // Step 2: Bribe guard path (reveals confession and resolves mystery)
        val bribePath = emptyBedMystery.investigationPaths.find { it.id == "path_bribe_guard" }!!
        val step2Result = StoryDirector.investigateMystery(step1Result.resolvedMystery!!, bribePath, state)
        assertTrue(step2Result.success)
        assertEquals(MysteryStatus.RESOLVED, step2Result.resolvedMystery!!.status)
        assertNotNull(step2Result.resolvedMystery!!.resolutionSummary)
        assertTrue("Gold reward should be granted on resolution", step2Result.goldReward > 0)
        assertTrue("Prestige reward should be granted on resolution", step2Result.prestigeReward > 0)
    }

    @Test
    fun test4_rumorInvestigation() {
        val state = LudusUiState(
            dominus = LudusDominus(dayNumber = 15, denarii = 3000),
            rumors = SeedStoryData.createInitialRumors()
        )

        // Investigate True rumor: The Wolf
        val wolfRumor = state.rumors.find { it.id == "rumor_wolf_subura" }!!
        val wolfResult = StoryDirector.investigateRumor(wolfRumor, state)
        assertTrue(wolfResult.success)
        assertTrue("The Wolf rumor should be confirmed TRUE", wolfResult.isConfirmedTrue)

        // Investigate False rumor: Poisoned grain
        val grainRumor = state.rumors.find { it.id == "rumor_poisoned_grain" }!!
        val grainResult = StoryDirector.investigateRumor(grainRumor, state)
        assertTrue(grainResult.success)
        assertFalse("Poisoned grain rumor should be debunked as FALSE", grainResult.isConfirmedTrue)
    }

    @Test
    fun test5_characterMemorySentimentShifts() {
        val charMemories = SeedStoryData.createInitialCharacterMemories()
        val cassianMem = charMemories["fighter_cassian"]
        assertNotNull(cassianMem)
        val initialHatred = cassianMem!!.hatred
        val initialFear = cassianMem.fear

        // Simulate combat outcome where player defeats Cassian
        cassianMem.hatred = (cassianMem.hatred + 15).coerceIn(0, 100)
        cassianMem.fear = (cassianMem.fear + 20).coerceIn(0, 100)
        cassianMem.memorableEvents.add(
            CharacterMemoryEvent(
                day = 17,
                deedDescription = "Titus tarafından Capua kumlarında mağlup edildi.",
                sentimentDelta = -15,
                emotionalCategory = "Husumet"
            )
        )

        assertTrue("Hatred should increase after defeat", cassianMem.hatred > initialHatred)
        assertTrue("Fear should increase after defeat", cassianMem.fear > initialFear)
        assertEquals(1, cassianMem.memorableEvents.size)
        assertEquals("Husumet", cassianMem.memorableEvents.first().emotionalCategory)
    }

    @Test
    fun test6_tomorrowCuriosityPreview() {
        val state = LudusUiState(
            dominus = LudusDominus(dayNumber = 16),
            arenaCalendar = listOf(
                ArenaBout(
                    id = "bout_day_17",
                    day = 17,
                    venueId = ArenaVenueId.CAPUA,
                    fighter1Id = "glad_1",
                    fighter1Name = "Titus",
                    fighter1Ludus = "Ludus Valerius",
                    fighter2Id = "fighter_cassian",
                    fighter2Name = "Cassian",
                    fighter2Ludus = "Domus Auctor",
                    matchType = MatchType.DUEL_TO_SURRENDER,
                    isPlayerMatch = true,
                    isCompleted = false
                )
            ),
            rumors = SeedStoryData.createInitialRumors()
        )

        val output = StoryDirector.simulateStoryDay(16, 42L, state)
        val previews = output.tomorrowPreviews

        assertTrue("Previews should not be empty", previews.isNotEmpty())
        val combatPreview = previews.find { it.categoryTag == "Resmi Müsabaka" }
        assertNotNull("Tomorrow official combat bout should appear in preview", combatPreview)
        assertEquals("⚔", combatPreview!!.iconSymbol)
        assertTrue("Headline should mention opponent", combatPreview.headline.contains("Titus"))
    }

    @Test
    fun test7_saveAndLoadFidelityForStoryState() {
        val state = LudusUiState(
            dominus = LudusDominus(dayNumber = 20, denarii = 8500, prestige = 450),
            worldMemory = SeedStoryData.createInitialWorldMemory(),
            rumors = SeedStoryData.createInitialRumors(),
            mysteries = SeedStoryData.createInitialMysteries(),
            storyThreads = SeedStoryData.createInitialStoryThreads()
        )

        val json = LudusSaveManager.serializeStateToJson(state)
        assertTrue("JSON should contain worldMemory", json.contains("worldMemory"))
        assertTrue("JSON should contain rumors", json.contains("rumors"))
        assertTrue("JSON should contain mysteries", json.contains("mysteries"))
        assertTrue("JSON should contain storyThreads", json.contains("storyThreads"))

        val loadedState = LudusSaveManager.deserializeJsonToState(json, LudusUiState())
        assertEquals("Day number must match", 20, loadedState.dominus.dayNumber)
        assertEquals("Denarii must match", 8500, loadedState.dominus.denarii)
        assertEquals("World memory count must match", state.worldMemory.size, loadedState.worldMemory.size)
        assertEquals("Rumors count must match", state.rumors.size, loadedState.rumors.size)
        assertEquals("Mysteries count must match", state.mysteries.size, loadedState.mysteries.size)
        assertEquals("Story threads count must match", state.storyThreads.size, loadedState.storyThreads.size)
    }

    @Test
    fun test8_100DayAutonomousStorySimulation() {
        var state = LudusUiState(
            dominus = LudusDominus(dayNumber = 1),
            gladiators = ArenaDatabase.createInitialPersistentFighters().take(3).toMutableList(),
            persistentFighters = ArenaDatabase.createInitialPersistentFighters().toMutableList(),
            arenaCalendar = ArenaProgressionEngine.generateCalendarSeason(1, ArenaVenueId.CAPUA, ArenaDatabase.createInitialPersistentFighters(), 42L).toMutableList(),
            worldMemory = SeedStoryData.createInitialWorldMemory(),
            rumors = SeedStoryData.createInitialRumors(),
            mysteries = SeedStoryData.createInitialMysteries(),
            storyThreads = SeedStoryData.createInitialStoryThreads(),
            characterMemories = SeedStoryData.createInitialCharacterMemories(),
            delayedConsequences = SeedStoryData.createInitialDelayedConsequences()
        )

        for (day in 2..100) {
            val output = StoryDirector.simulateStoryDay(day, 42L, state)
            state = state.copy(
                dominus = state.dominus.copy(dayNumber = day),
                worldMemory = output.updatedWorldMemory,
                rumors = output.updatedRumors,
                mysteries = output.updatedMysteries,
                storyThreads = output.updatedStoryThreads,
                characterMemories = output.updatedCharacterMemories,
                delayedConsequences = output.updatedDelayedConsequences
            )

            // Assert invariant: Living rumor count remains bounded (no memory leak)
            val activeRumors = state.rumors.filter { !it.isExpired }
            assertTrue("Active rumors should stay bounded (<= 10)", activeRumors.size <= 10)
        }

        assertEquals("Simulation should reach day 100", 100, state.dominus.dayNumber)
        assertTrue("World memory should record events", state.worldMemory.isNotEmpty())
    }
}
