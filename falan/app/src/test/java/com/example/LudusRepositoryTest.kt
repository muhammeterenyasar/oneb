package com.example

import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class LudusRepositoryTest {

    private val baseGladiator = Gladiator(
        id = 1L,
        name = "Spartacus",
        nickname = "Champion",
        origin = "Thrace",
        gladiatorClass = GladiatorClass.MURMILLO,
        contractType = GladiatorContractType.SLAVE,
        dailySalary = 0,
        priceValue = 200,
        str = 15,
        agi = 12,
        sta = 14,
        mor = 85,
        currentHp = 120,
        maxHp = 120,
        wins = 4,
        age = 26
    )

    @Test
    fun testGladiatorDrillMapping() {
        val drill = SpecificDrill.PALUS_WOODEN_POST
        val trainingType = drill.toLegacyTrainingType()
        assertEquals(TrainingType.STRENGTH, trainingType)

        val updated = baseGladiator.copy(
            assignedDrill = drill,
            trainingFocus = drill.toLegacyTrainingType()
        )
        assertEquals(SpecificDrill.PALUS_WOODEN_POST, updated.assignedDrill)
        assertEquals(TrainingType.STRENGTH, updated.trainingFocus)
    }

    @Test
    fun testPromotionEligibility() {
        // Can promote if wins >= 3 or age >= 26
        val eligibleGladiator = baseGladiator.copy(wins = 5, age = 28)
        assertTrue(eligibleGladiator.canPromoteToTeacher)

        val nonEligibleGladiator = baseGladiator.copy(wins = 1, age = 20)
        assertFalse(nonEligibleGladiator.canPromoteToTeacher)
    }

    @Test
    fun testDietPlanSwitching() {
        val defaultState = LudusState(dietPlan = DietPlan.BARLEY_PORRIDGE)
        assertEquals(DietPlan.BARLEY_PORRIDGE, defaultState.dietPlan)

        val updatedState = defaultState.copy(dietPlan = DietPlan.SAGINA_PROTEIN)
        assertEquals(DietPlan.SAGINA_PROTEIN, updatedState.dietPlan)
        assertEquals(6, updatedState.dietPlan.dailyCostPerGladiator)
    }
}
