package com.example

import com.example.data.engine.EventEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class EventEngineTest {

    @Test
    fun executeTesseraeRoll_rejectsWhenGoldInsufficient() {
        val result = EventEngine.executeTesseraeRoll(betAmount = 50, playerGold = 20)
        assertFalse(result.hasPlayedThisCycle)
        assertEquals(0, result.goldReward)
    }

    @Test
    fun executeTesseraeRoll_resolvesDiceProperly() {
        val result = EventEngine.executeTesseraeRoll(betAmount = 25, playerGold = 100)
        assertTrue(result.hasPlayedThisCycle)
        assertEquals(3, result.playerDice.size)
        assertEquals(3, result.rivalDice.size)
        assertNotNull(result.isPlayerWinner)
        if (result.isPlayerWinner == true) {
            assertTrue(result.goldReward > 0)
        } else {
            assertEquals(0, result.goldReward)
        }
    }

    @Test
    fun simulateSparring_generatesThreeRoundsAndWinner() {
        val fighter1 = Gladiator(
            id = 1,
            name = "Spartacus",
            nickname = "",
            origin = "Trakya",
            gladiatorClass = GladiatorClass.THRAEX,
            contractType = GladiatorContractType.SLAVE,
            dailySalary = 0,
            priceValue = 100,
            str = 15,
            agi = 16,
            sta = 14,
            mor = 80,
            currentHp = 100,
            maxHp = 100
        )
        val fighter2 = Gladiator(
            id = 2,
            name = "Crixus",
            nickname = "",
            origin = "Galya",
            gladiatorClass = GladiatorClass.MURMILLO,
            contractType = GladiatorContractType.SLAVE,
            dailySalary = 0,
            priceValue = 100,
            str = 14,
            agi = 12,
            sta = 16,
            mor = 80,
            currentHp = 100,
            maxHp = 100
        )

        val result = EventEngine.simulateSparring(
            fighter1 = fighter1,
            fighter2 = fighter2,
            activeTeachers = emptyList()
        )

        assertTrue(result.isComplete)
        assertEquals(3, result.rounds.size)
        assertNotNull(result.winnerName)
        assertTrue(result.xpGained >= 25)
    }
}
