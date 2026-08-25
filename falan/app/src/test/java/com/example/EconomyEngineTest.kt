package com.example

import com.example.data.engine.EconomyEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class EconomyEngineTest {

    @Test
    fun calculateDailyExpenses_sumsAllUpkeepProperly() {
        val gladiators = listOf(
            Gladiator(
                name = "G1",
                nickname = "",
                origin = "",
                gladiatorClass = GladiatorClass.MURMILLO,
                contractType = GladiatorContractType.CONTRACTED,
                dailySalary = 15,
                priceValue = 100,
                str = 10,
                agi = 10,
                sta = 10,
                mor = 50,
                currentHp = 100,
                maxHp = 100
            ),
            Gladiator(
                name = "G2",
                nickname = "",
                origin = "",
                gladiatorClass = GladiatorClass.THRAEX,
                contractType = GladiatorContractType.SLAVE,
                dailySalary = 0,
                priceValue = 100,
                str = 10,
                agi = 10,
                sta = 10,
                mor = 50,
                currentHp = 100,
                maxHp = 100
            )
        )
        val teachers = listOf(
            Teacher(
                id = 1,
                name = "T1",
                title = "",
                specialty = TeacherSpecialty.MURMILLO_MASTER,
                level = 1,
                statBonusMultiplier = 1.3f,
                dailySalary = 10,
                hireCost = 100,
                description = ""
            )
        )

        val breakdown = EconomyEngine.calculateDailyExpenses(
            gladiators = gladiators,
            activeTeachers = teachers,
            guardsHired = 2,
            physicianLevel = 1,
            dietPlan = DietPlan.BARLEY_PORRIDGE // 2 gold per gladiator
        )

        assertEquals(4, breakdown.foodCost) // 2 * 2
        assertEquals(15, breakdown.salaryCost) // 1 contracted gladiator
        assertEquals(12, breakdown.guardCost) // 2 * 6
        assertEquals(8, breakdown.physicianUpkeep) // 1 * 8
        assertEquals(10, breakdown.teacherSalaries) // 10
        assertEquals(49, breakdown.totalExpenses)
    }

    @Test
    fun calculateSenatorSponsorshipBonus_returnsBonusWhenActive() {
        assertEquals(70, EconomyEngine.calculateSenatorSponsorshipBonus(3))
        assertEquals(0, EconomyEngine.calculateSenatorSponsorshipBonus(0))
    }

    @Test
    fun calculateDebtInterest_appliesFifteenPercent() {
        assertEquals(15, EconomyEngine.calculateDebtInterest(100))
        assertEquals(0, EconomyEngine.calculateDebtInterest(0))
    }

    @Test
    fun calculateDoctorUpgradeCost_returnsExpectedTierCosts() {
        assertEquals(120, EconomyEngine.calculateDoctorUpgradeCost(1))
        assertEquals(250, EconomyEngine.calculateDoctorUpgradeCost(2))
        assertEquals(0, EconomyEngine.calculateDoctorUpgradeCost(3))
    }
}
