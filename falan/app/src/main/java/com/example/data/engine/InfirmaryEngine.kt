package com.example.data.engine

import com.example.model.Gladiator
import com.example.model.InjurySeverity
import com.example.model.LudusState
import kotlin.math.max
import kotlin.math.min

object InfirmaryEngine {

    fun applyInstantHealPotion(gladiator: Gladiator): Gladiator {
        return gladiator.copy(
            isInjured = false,
            injurySeverity = InjurySeverity.NONE,
            recoveryDaysLeft = 0,
            currentHp = gladiator.maxHp,
            fatigue = 0,
            hasDisabledLimb = false,
            disabledLimbDesc = null
        )
    }

    fun applyHerbalPoulticeTreatment(
        gladiator: Gladiator,
        state: LudusState,
        cost: Int = 30
    ): Pair<Gladiator, LudusState>? {
        if (state.gold < cost) return null

        val newDaysLeft = max(0, gladiator.recoveryDaysLeft - 1)
        val isHealed = newDaysLeft == 0
        val newHp = min(gladiator.maxHp, gladiator.currentHp + 35)

        val updatedGladiator = gladiator.copy(
            currentHp = newHp,
            recoveryDaysLeft = newDaysLeft,
            isInjured = !isHealed,
            injurySeverity = if (isHealed) InjurySeverity.NONE else gladiator.injurySeverity
        )
        val updatedState = state.copy(gold = state.gold - cost)
        return Pair(updatedGladiator, updatedState)
    }

    fun applyThermalBathTreatment(
        gladiator: Gladiator,
        state: LudusState,
        cost: Int = 40
    ): Pair<Gladiator, LudusState>? {
        if (state.gold < cost) return null

        val updatedGladiator = gladiator.copy(
            fatigue = 0,
            mor = min(100, gladiator.mor + 15),
            currentHp = min(gladiator.maxHp, gladiator.currentHp + 25)
        )
        val updatedState = state.copy(gold = state.gold - cost)
        return Pair(updatedGladiator, updatedState)
    }

    fun performEmergencySurgery(
        gladiator: Gladiator,
        state: LudusState,
        cost: Int = 120
    ): Pair<Gladiator, LudusState>? {
        if (state.physicianLevel < 2 || state.gold < cost) return null

        val newDaysLeft = max(0, gladiator.recoveryDaysLeft - 2)
        val isHealed = newDaysLeft == 0
        val updatedGladiator = gladiator.copy(
            hasDisabledLimb = false,
            disabledLimbDesc = null,
            recoveryDaysLeft = newDaysLeft,
            isInjured = !isHealed,
            injurySeverity = if (isHealed) InjurySeverity.NONE else InjurySeverity.LIGHT,
            currentHp = min(gladiator.maxHp, gladiator.currentHp + 30)
        )
        val updatedState = state.copy(gold = state.gold - cost)
        return Pair(updatedGladiator, updatedState)
    }

    fun applyInstantHealWithGold(
        gladiator: Gladiator,
        state: LudusState,
        cost: Int = 85
    ): Pair<Gladiator, LudusState>? {
        if (state.gold < cost) return null

        val healed = gladiator.copy(
            isInjured = false,
            injurySeverity = InjurySeverity.NONE,
            recoveryDaysLeft = 0,
            currentHp = gladiator.maxHp,
            fatigue = 0,
            hasDisabledLimb = false,
            disabledLimbDesc = null
        )
        val updatedState = state.copy(gold = state.gold - cost)
        return Pair(healed, updatedState)
    }
}
