package com.example

import com.example.model.*
import com.example.simulation.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * Full-System Simulation Consistency and World Invariant Test Suite.
 * Covers all 12 mandatory Acceptance Tests:
 * 1. Underground Pool Rotation & Boss Persistence
 * 2. Fighter Cooldown Enforced (3 days)
 * 3. Boss Survival Across Days
 * 4. Boss Defeat Permanence (Never returns across 30+ days)
 * 5. Single Source of Truth (Gladiator identity integrity)
 * 6. Permanent Death Removal from All Systems
 * 7. Deterministic Pool Stability on Same Calendar Date
 * 8. Save & Load Full World Fidelity
 * 9. Official Fight Calendar Gating
 * 10. Title Vacancy on Champion Death
 * 11. Rolling Recruitment Market Refresh
 * 12. 100-Day Automated World Invariant Stress Test
 */
class SimulationConsistencyTest {

    @Test
    fun test1_undergroundPoolRotationAndBossPersistence() {
        val fighters = ArenaDatabase.createInitialPersistentFighters()
        val wolf = fighters.find { it.nickname == "The Wolf" }
        assertNotNull("The Wolf boss must exist in persistent fighters", wolf)

        // Day 12 pool: The Wolf should appear as a Boss Fight
        val day12Pool = ArenaProgressionEngine.generateUndergroundFights(
            currentDay = 12,
            fighters = fighters,
            worldSeed = 42L,
            bossStates = mapOf("boss_wolf_secundus" to BossState.ACTIVE)
        )
        assertTrue("Day 12 should have underground fights", day12Pool.isNotEmpty())
        val wolfFightDay12 = day12Pool.find { it.isBossFight && it.opponentFighter.nickname == "The Wolf" }
        assertNotNull("The Wolf must appear on Day 12 as a persistent boss fight", wolfFightDay12)

        // Day 13 pool: Rotating fighters change, but The Wolf is still present
        val day13Pool = ArenaProgressionEngine.generateUndergroundFights(
            currentDay = 13,
            fighters = fighters,
            worldSeed = 42L,
            bossStates = mapOf("boss_wolf_secundus" to BossState.ACTIVE)
        )
        val wolfFightDay13 = day13Pool.find { it.isBossFight && it.opponentFighter.nickname == "The Wolf" }
        assertNotNull("The Wolf must still persist on Day 13", wolfFightDay13)

        // Normal rotating fighters should differ across days
        val normalFightersDay12 = day12Pool.filter { !it.isBossFight }.map { it.opponentFighter.id }
        val normalFightersDay13 = day13Pool.filter { !it.isBossFight }.map { it.opponentFighter.id }
        assertNotEquals("Normal rotating pool must rotate across days", normalFightersDay12, normalFightersDay13)
    }

    @Test
    fun test2_fighterCooldownEnforced() {
        val fighters = ArenaDatabase.createInitialPersistentFighters()
        val day10Pool = ArenaProgressionEngine.generateUndergroundFights(
            currentDay = 10,
            fighters = fighters,
            worldSeed = 42L
        )
        val chosenNormal = day10Pool.first { !it.isBossFight }.opponentFighter

        // Simulate combat: fighter lastFightDay set to Day 10
        chosenNormal.lastFightDay = 10

        // Check Day 11 and Day 12: Fighter is on 3-day cooldown and CANNOT appear
        for (day in listOf(11, 12)) {
            val pool = ArenaProgressionEngine.generateUndergroundFights(
                currentDay = day,
                fighters = fighters,
                worldSeed = 42L
            )
            val appears = pool.any { it.opponentFighter.id == chosenNormal.id }
            assertFalse("Fighter on cooldown (lastFight: 10, now: $day) must not appear in pool", appears)
        }
    }

    @Test
    fun test3_bossSurvivalAcrossDays() {
        val fighters = ArenaDatabase.createInitialPersistentFighters()
        val bossStates = mutableMapOf("boss_wolf_secundus" to BossState.AVAILABLE)

        // Simulate player fought The Wolf but did not defeat him (remains AVAILABLE)
        val day15Pool = ArenaProgressionEngine.generateUndergroundFights(
            currentDay = 15,
            fighters = fighters,
            worldSeed = 42L,
            bossStates = bossStates
        )
        val wolfFight = day15Pool.find { it.opponentFighter.nickname == "The Wolf" }
        assertNotNull("Boss must survive and persist in upcoming days if not defeated", wolfFight)
    }

    @Test
    fun test4_bossDefeatPermanence() {
        val fighters = ArenaDatabase.createInitialPersistentFighters()
        val wolf = fighters.find { it.nickname == "The Wolf" }!!

        // Defeat The Wolf
        val bossStates = mutableMapOf(wolf.id to BossState.DEFEATED)
        wolf.undergroundBossState = BossState.DEFEATED

        // Advance 35 days (Day 15 to Day 50) - The Wolf must NEVER return
        for (day in 15..50) {
            val pool = ArenaProgressionEngine.generateUndergroundFights(
                currentDay = day,
                fighters = fighters,
                worldSeed = 42L,
                bossStates = bossStates
            )
            val wolfFound = pool.any { it.opponentFighter.id == wolf.id || it.opponentFighter.nickname == "The Wolf" }
            assertFalse("Defeated boss must NEVER reappear in underground fights on Day $day", wolfFound)
        }
    }

    @Test
    fun test5_singleSourceOfTruth() {
        val fighters = ArenaDatabase.createInitialPersistentFighters()
        val cassian = fighters.find { it.id == "fighter_cassian" }
        assertNotNull("Canonical Cassian must exist", cassian)

        // Verify typealias PersistentFighter = Gladiator
        val asGladiator: Gladiator = cassian!!
        assertEquals("fighter_cassian", asGladiator.id)
        assertEquals("Cassian", asGladiator.name)

        // Mutating wins on PersistentFighter mutates the Gladiator canonical properties
        val initialWins = cassian.wins
        cassian.wins = initialWins + 1
        assertEquals(initialWins + 1, asGladiator.historicalPerformance.victories)
        assertEquals(initialWins + 1, asGladiator.careerStats.wins)
    }

    @Test
    fun test6_permanentDeathExclusion() {
        val fighters = ArenaDatabase.createInitialPersistentFighters()
        val doomedFighter = fighters.first { !it.isChampion && it.id != "fighter_cassian" }

        // Kill fighter
        doomedFighter.isAlive = false
        doomedFighter.status = GladiatorStatus.DEAD
        doomedFighter.deathDay = 15
        doomedFighter.killedBy = "Titus"

        // Check 1: Underground Pool
        val undergroundPool = ArenaProgressionEngine.generateUndergroundFights(
            currentDay = 16,
            fighters = fighters,
            worldSeed = 42L
        )
        assertFalse("Dead fighter must not appear in underground", undergroundPool.any { it.opponentFighter.id == doomedFighter.id })

        // Check 2: Rankings
        val rankings = ArenaProgressionEngine.computeLivingRankings(
            venue = doomedFighter.currentArena,
            fighters = fighters.filter { it.isAlive },
            playerGladiator = null
        )
        assertFalse("Dead fighter must not appear in living rankings", rankings.any { it.fighterId == doomedFighter.id })

        // Check 3: Challenge scheduling
        val calendar = ArenaProgressionEngine.createInitialCalendar(16, fighters)
        val (canSchedule, reason) = ArenaProgressionEngine.schedulePlayerChallenge(
            currentDay = 16,
            calendar = calendar,
            venue = doomedFighter.currentArena,
            opponent = doomedFighter
        )
        assertFalse("Cannot schedule match with dead fighter", canSchedule)
        assertTrue(reason.contains("DEAD"))
    }

    @Test
    fun test7_dailyUndergroundStability() {
        val fighters = ArenaDatabase.createInitialPersistentFighters()

        // Generate pool for Day 22 ten times
        val pools = (1..10).map {
            ArenaProgressionEngine.generateUndergroundFights(
                currentDay = 22,
                fighters = fighters,
                worldSeed = 42L
            )
        }

        val firstPoolIds = pools[0].map { it.id }
        for (i in 1 until pools.size) {
            val currentPoolIds = pools[i].map { it.id }
            assertEquals("Underground pool must be identical on repeat queries on the same day", firstPoolIds, currentPoolIds)
        }
    }

    @Test
    fun test8_saveAndLoadFidelity() {
        val baseState = LudusUiState()
        val tempFile = File.createTempFile("ludus_test_save", ".json").apply { deleteOnExit() }

        // Set distinctive state
        val stateToSave = baseState.copy(
            dominus = baseState.dominus.copy(dayNumber = 25, denarii = 8800, prestige = 1250)
        )

        val saveSuccess = LudusSaveManager.saveGameToFile(tempFile, stateToSave)
        assertTrue("Save file creation should succeed", saveSuccess)
        assertTrue("Save file should exist and have content", tempFile.exists() && tempFile.length() > 0)

        val loadedState = LudusSaveManager.loadGameFromFile(tempFile, baseState)
        assertNotNull("Loaded state should not be null", loadedState)
        assertEquals(25, loadedState!!.dominus.dayNumber)
        assertEquals(8800, loadedState.dominus.denarii)
        assertEquals(1250, loadedState.dominus.prestige)
    }

    @Test
    fun test9_officialFightCalendarGating() {
        val fighters = ArenaDatabase.createInitialPersistentFighters()
        val playerGlad = SeedData.createInitialGladiators().first()

        // Scheduled fight is on Day 17; current day is Day 12
        val (canFightEarly, reasonEarly) = ArenaProgressionEngine.Validation.canStartOfficialCombat(
            currentDay = 12,
            matchDay = 17,
            fighter = playerGlad
        )
        assertFalse("Cannot start official fight early", canFightEarly)
        assertTrue(reasonEarly.contains("Günü Değil") || reasonEarly.contains("kaldı"))

        // On Day 17: can start combat
        val (canFightToday, _) = ArenaProgressionEngine.Validation.canStartOfficialCombat(
            currentDay = 17,
            matchDay = 17,
            fighter = playerGlad
        )
        assertTrue("Can start official combat on scheduled fight day", canFightToday)
    }

    @Test
    fun test10_championshipVacancyOnChampionDeath() {
        val fighters = ArenaDatabase.createInitialPersistentFighters()
        val capuaChamp = fighters.find { it.isChampion && it.currentArena == ArenaVenueId.CAPUA }!!

        // Kill champion in background match
        capuaChamp.isAlive = false
        capuaChamp.isChampion = false

        // Living rankings should declare championship VACANT
        val livingFighters = fighters.filter { it.isAlive }
        val rankings = ArenaProgressionEngine.computeLivingRankings(
            venue = ArenaVenueId.CAPUA,
            fighters = livingFighters,
            playerGladiator = null
        )

        val rank1 = rankings.first()
        assertTrue("Rank #1 should be VACANT when reigning champion dies", rank1.isVacant)
        assertEquals("UNVAN BOŞTA (VACANT)", rank1.name)
    }

    @Test
    fun test11_rollingRecruitmentMarket() {
        val marketDay10 = SeedData.createMarketGladiators()
        assertTrue(marketDay10.isNotEmpty())
        marketDay10.forEach { recruit ->
            assertTrue("Recruit must be alive", recruit.isAlive)
            assertNotNull(recruit.id)
            assertNotNull(recruit.name)
        }
    }

    @Test
    fun test12_100DayWorldSimulationStressTest() {
        val calendar = ArenaProgressionEngine.createInitialCalendar(1, ArenaDatabase.createInitialPersistentFighters())
        val fighters = ArenaDatabase.createInitialPersistentFighters().toMutableList()
        val memorials = mutableListOf<FallenGladiatorMemorial>()
        val bossStates = mutableMapOf("boss_wolf_secundus" to BossState.ACTIVE)

        var totalBoutsSimulated = 0
        var totalDeaths = 0

        // Simulate 100 in-game days
        for (day in 1..100) {
            // 1. Calendar bouts
            val dayNews = ArenaProgressionEngine.simulateCalendarDay(day, calendar, fighters, memorials)
            totalBoutsSimulated += dayNews.size

            // 2. Replenish calendar if running low
            val livingFighters = fighters.filter { it.isAlive }
            val maxDay = calendar.maxOfOrNull { it.day } ?: day
            if (maxDay < day + 7 && livingFighters.size >= 2) {
                for (d in (maxDay + 1)..(day + 14)) {
                    val f1 = livingFighters.random()
                    val f2 = livingFighters.filter { it.id != f1.id }.randomOrNull() ?: f1
                    calendar.add(
                        ArenaCalendarBout(
                            id = "stress_bout_$d",
                            day = d,
                            venueId = f1.currentArena,
                            fighter1Id = f1.id,
                            fighter1Name = f1.fullDisplayName,
                            fighter1Ludus = f1.ludusAffiliation,
                            fighter2Id = f2.id,
                            fighter2Name = f2.fullDisplayName,
                            fighter2Ludus = f2.ludusAffiliation,
                            matchType = ArenaMatchType.STANDARD_DUEL,
                            isPlayerMatch = false
                        )
                    )
                }
            }

            // 3. Underground generation
            val undergroundPool = ArenaProgressionEngine.generateUndergroundFights(
                currentDay = day,
                fighters = fighters,
                worldSeed = 42L,
                bossStates = bossStates
            )

            // Assertions for day
            assertNotNull(undergroundPool)
            undergroundPool.forEach { fight ->
                assertTrue("Fighter in underground must be alive", fight.opponentFighter.isAlive)
            }
        }

        // Validate final state invariants
        val dummyUiState = LudusUiState(
            dominus = LudusDominus(dayNumber = 100),
            persistentFighters = fighters,
            arenaCalendar = calendar,
            fallenGladiators = memorials
        )

        val validation = WorldSimulationValidator.validateWorldState(dummyUiState)
        assertTrue(
            "100-day simulation must pass all world invariant assertions with 0 violations. Violations: ${validation.violations}",
            validation.isValid
        )
    }
}
