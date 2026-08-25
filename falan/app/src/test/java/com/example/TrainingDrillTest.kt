package com.example

import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class TrainingDrillTest {

    private val testGladiator = Gladiator(
        id = 1L,
        name = "Marcus",
        nickname = "The Iron",
        origin = "Thrace",
        gladiatorClass = GladiatorClass.MURMILLO,
        contractType = GladiatorContractType.SLAVE,
        dailySalary = 0,
        priceValue = 120,
        str = 14,
        agi = 10,
        sta = 12,
        mor = 80,
        currentHp = 100,
        maxHp = 100,
        fatigue = 0,
        assignedDrill = SpecificDrill.PALUS_WOODEN_POST,
        hasTrainedToday = false
    )

    @Test
    fun testGladiatorCareerRankProgression() {
        val tiro = testGladiator.copy(wins = 1)
        assertEquals(GladiatorCareerRank.TIRO, tiro.careerRank)

        val gregarius = testGladiator.copy(wins = 4)
        assertEquals(GladiatorCareerRank.GREGARIUS, gregarius.careerRank)

        val veteranus = testGladiator.copy(wins = 7)
        assertEquals(GladiatorCareerRank.VETERANUS, veteranus.careerRank)

        val champion = testGladiator.copy(wins = 12)
        assertEquals(GladiatorCareerRank.CHAMPION, champion.careerRank)

        val legend = testGladiator.copy(wins = 18)
        assertEquals(GladiatorCareerRank.PRIMUS_PALUS, legend.careerRank)
    }

    @Test
    fun testTrainingSetsHasTrainedToday() {
        val g = testGladiator.copy(hasTrainedToday = false)
        assertFalse(g.hasTrainedToday)

        val updated = g.copy(hasTrainedToday = true)
        assertTrue(updated.hasTrainedToday)
    }
}
