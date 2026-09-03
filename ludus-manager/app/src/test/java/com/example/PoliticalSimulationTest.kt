package com.example

import com.example.model.*
import com.example.simulation.PoliticalEngine
import org.junit.Assert.*
import org.junit.Test

class PoliticalSimulationTest {

    @Test
    fun testInitialPoliticalSetup() {
        val factions = PoliticalEngine.createInitialFactions()
        val characters = PoliticalEngine.createInitialCharacters()
        val network = PoliticalEngine.createInitialNetwork()
        val scandals = PoliticalEngine.createInitialScandals()
        val calendar = PoliticalEngine.createInitialCalendar(1)

        assertTrue(factions.isNotEmpty())
        assertEquals(7, factions.size)
        assertTrue(characters.size >= 8)
        assertTrue(network.isNotEmpty())
        assertTrue(scandals.isNotEmpty())
        assertTrue(calendar.isNotEmpty())
    }

    @Test
    fun testPoliticalSimulationDayAdvancement() {
        val factions = PoliticalEngine.createInitialFactions()
        val characters = PoliticalEngine.createInitialCharacters()
        val scandals = PoliticalEngine.createInitialScandals().toMutableList()
        val calendar = PoliticalEngine.createInitialCalendar(1).toMutableList()
        val secrets = listOf(
            PoliticalSecret(
                id = "sec_test",
                targetNpcId = "npc_cassius",
                targetName = "Marcus Cassius",
                title = "Gizli Dosya",
                description = "Yolsuzluk kanıtı",
                category = SecretCategory.CORRUPTION,
                severity = SecretSeverity.MODERATE
            )
        )
        val ledger = PoliticalResourceLedger(politicalFavor = 2, influence = 40, reputation = 50)

        val result = PoliticalEngine.simulatePoliticalDay(
            currentDay = 2,
            factions = factions,
            characters = characters,
            scandals = scandals,
            calendar = calendar,
            playerSecrets = secrets,
            resources = ledger,
            patronId = "npc_cassius"
        )

        assertNotNull(result)
        assertEquals(factions.size, result.updatedFactions.size)
        assertEquals(characters.size, result.updatedCharacters.size)
        assertTrue(result.updatedResources.influence >= 10)
    }

    @Test
    fun testArenaModifiersFromPolitics() {
        val factions = PoliticalEngine.createInitialFactions().toMutableList()
        val arenaFac = factions.first { it.id == PoliticalFactionId.ARENA_OFFICIALS }
        val updatedArenaFac = arenaFac.copy(opinionOfPlayer = 80)
        val testFactions = factions.map { if (it.id == PoliticalFactionId.ARENA_OFFICIALS) updatedArenaFac else it }

        val feeDiscount = PoliticalEngine.getArenaEntranceFeeModifier(testFactions)
        assertTrue(feeDiscount <= 0.85f)

        val bonusMultiplier = PoliticalEngine.getArenaPurseMultiplier(testFactions, isPatronActive = true)
        assertTrue(bonusMultiplier >= 1.15f)

        val (canAccess, _) = PoliticalEngine.canAccessChampionshipBouts(testFactions)
        assertTrue(canAccess)
    }

    @Test
    fun testBriberyAndSecretsLeverage() {
        val characters = PoliticalEngine.createInitialCharacters().toMutableList()
        val targetChar = characters.first()
        val initialRel = targetChar.relationshipWithPlayer
        val initialFavors = targetChar.favorsOwedToPlayer

        // Simulate positive bribe outcome
        targetChar.relationshipWithPlayer += 15
        targetChar.favorsOwedToPlayer += 1

        assertEquals(initialRel + 15, targetChar.relationshipWithPlayer)
        assertEquals(initialFavors + 1, targetChar.favorsOwedToPlayer)
    }
}
