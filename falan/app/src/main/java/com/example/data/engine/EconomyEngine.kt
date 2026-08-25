package com.example.data.engine

import com.example.model.*
import kotlin.math.max

/**
 * Isolated Economy Engine for Ludus Magnus.
 * Handles daily food costs, gladiator salaries, physician and guard upkeep,
 * usurer debt interest, and sponsorship revenue.
 */
object EconomyEngine {

    data class DailyExpensesBreakdown(
        val foodCost: Int,
        val salaryCost: Int,
        val guardCost: Int,
        val physicianUpkeep: Int,
        val teacherSalaries: Int,
        val totalExpenses: Int
    )

    fun calculateDailyExpenses(
        gladiators: List<Gladiator>,
        activeTeachers: List<Teacher>,
        guardsHired: Int,
        physicianLevel: Int,
        dietPlan: DietPlan
    ): DailyExpensesBreakdown {
        val foodCost = gladiators.size * dietPlan.dailyCostPerGladiator
        val salaryCost = gladiators.filter { it.contractType == GladiatorContractType.CONTRACTED }.sumOf { it.dailySalary }
        val guardCost = guardsHired * 6
        val physicianUpkeep = physicianLevel * 8
        val teacherSalaries = activeTeachers.sumOf { it.dailySalary }
        val total = foodCost + salaryCost + guardCost + physicianUpkeep + teacherSalaries

        return DailyExpensesBreakdown(
            foodCost = foodCost,
            salaryCost = salaryCost,
            guardCost = guardCost,
            physicianUpkeep = physicianUpkeep,
            teacherSalaries = teacherSalaries,
            totalExpenses = total
        )
    }

    fun calculateSenatorSponsorshipBonus(senatorSponsorshipDays: Int): Int {
        return if (senatorSponsorshipDays > 0) 70 else 0
    }

    fun calculateDebtInterest(activeDebt: Int, rate: Float = 0.15f): Int {
        return if (activeDebt > 0) (activeDebt * rate).toInt() else 0
    }

    fun calculateDoctorUpgradeCost(currentLevel: Int): Int {
        return when (currentLevel) {
            1 -> 120
            2 -> 250
            else -> 0
        }
    }
}
