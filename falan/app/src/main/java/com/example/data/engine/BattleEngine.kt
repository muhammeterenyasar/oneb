package com.example.data.engine

import com.example.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Isolated Combat Engine for Roman Arena Battles.
 * Handles damage calculation, critical strikes, parries, Lanista shout bonuses,
 * equipment stats, Pollice Verso crowd bloodlust, and match resolution statistics.
 */
object BattleEngine {

    /**
     * Calculates single-strike damage with tactical, class, and equipment multipliers.
     */
    fun calculateDamage(
        attackerStr: Int,
        defenderSta: Int,
        defenderAgi: Int,
        tactic: BattleTactic,
        isCrit: Boolean,
        isAttackerPlayer: Boolean,
        lanistaShoutBonus: String? = null,
        scoutedWeakness: Boolean = false,
        sharpenedWeapons: Boolean = false,
        marsBlessing: Boolean = false,
        equipmentDamageBonus: Int = 0,
        defenderArmorReductionPercent: Int = 0
    ): Int {
        val baseDamage = (attackerStr + equipmentDamageBonus) * 1.5f + Random.nextInt(4, 10)
        val armorReduction = defenderSta * 0.45f
        var damage = max(4f, baseDamage - armorReduction)

        // Equipment armor reduction from defender
        if (defenderArmorReductionPercent > 0) {
            val reductionMult = (100 - defenderArmorReductionPercent).coerceIn(40, 100).toFloat() / 100f
            damage *= reductionMult
        }

        // Tactic Modifiers
        damage *= when (tactic) {
            BattleTactic.AGGRESSIVE -> 1.30f
            BattleTactic.MAIMING -> 1.20f
            BattleTactic.DEFENSIVE -> 0.85f
            BattleTactic.CROWD_PLEASER -> 1.05f
        }

        // Active Pre-Match Buffs
        if (isAttackerPlayer) {
            if (scoutedWeakness) damage *= 1.15f
            if (marsBlessing) damage *= 1.10f
            if (lanistaShoutBonus == "ATTACK") damage *= 1.25f
        }

        // Critical Strike Modifier
        if (isCrit) {
            damage *= 1.75f
        }

        return damage.toInt().coerceAtLeast(1)
    }

    /**
     * Determines whether an attack scores a critical hit.
     */
    fun checkCriticalHit(
        attackerAgi: Int,
        tactic: BattleTactic,
        sharpenedWeapons: Boolean = false,
        marsBlessing: Boolean = false,
        equipmentCritBonus: Int = 0
    ): Boolean {
        var critChance = 8 + (attackerAgi * 0.8f) + equipmentCritBonus
        if (tactic == BattleTactic.AGGRESSIVE || tactic == BattleTactic.MAIMING) {
            critChance += 12f
        }
        if (sharpenedWeapons) critChance += 15f
        if (marsBlessing) critChance += 10f

        return Random.nextFloat() * 100f < critChance.coerceIn(5f, 75f)
    }

    /**
     * Determines whether the defender dodges or blocks the attack.
     */
    fun checkDefenseOutcome(
        defenderAgi: Int,
        defenderSta: Int,
        defenderClass: GladiatorClass,
        isDefensiveTactic: Boolean,
        lanistaShoutBonus: String? = null,
        equipmentDodgeBonus: Int = 0
    ): Pair<Boolean, Boolean> {
        var dodgeChance = (defenderAgi * 0.75f) + equipmentDodgeBonus
        var blockChance = (defenderSta * 0.65f)

        if (defenderClass == GladiatorClass.MURMILLO || defenderClass == GladiatorClass.SECUTOR) {
            blockChance += 15f
        }
        if (defenderClass == GladiatorClass.RETIARIUS || defenderClass == GladiatorClass.THRAEX) {
            dodgeChance += 15f
        }
        if (isDefensiveTactic) {
            blockChance += 20f
            dodgeChance += 10f
        }
        if (lanistaShoutBonus == "DEFENSE") {
            blockChance += 25f
        }

        val isDodge = Random.nextFloat() * 100f < dodgeChance.coerceIn(5f, 55f)
        val isBlock = !isDodge && (Random.nextFloat() * 100f < blockChance.coerceIn(5f, 50f))

        return Pair(isDodge, isBlock)
    }

    /**
     * Calculates excitement rating and crowd sentiment shifts for a finished match.
     */
    fun calculateMatchExcitement(
        crowdHype: Int,
        critCount: Int,
        tactic: BattleTactic,
        isVictorious: Boolean
    ): Int {
        val tacticBonus = when (tactic) {
            BattleTactic.CROWD_PLEASER -> 16
            BattleTactic.AGGRESSIVE -> 8
            BattleTactic.MAIMING -> 5
            BattleTactic.DEFENSIVE -> 2
        }
        return ((crowdHype / 2) + (critCount * 6) + tacticBonus + (if (isVictorious) 10 else 0)).coerceIn(25, 100)
    }

    /**
     * Calculates mercy prestige rewards and execution gold bonuses with Lanista perks.
     */
    fun calculatePolliceVersoRewards(
        basePrestige: Int,
        baseGold: Int,
        cityTier: CityTier,
        executionBonusPercent: Int = 0
    ): Pair<Int, Int> {
        val mercyPrestige = basePrestige + (cityTier.tierNumber * 25)
        var executionGold = baseGold + (cityTier.tierNumber * 45)
        if (executionBonusPercent > 0) {
            executionGold += (executionGold * (executionBonusPercent.toFloat() / 100f)).toInt()
        }
        return Pair(mercyPrestige, executionGold)
    }
}
