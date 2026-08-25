package com.example

import com.example.data.engine.BattleEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class BattleEngineTest {

    @Test
    fun calculateDamage_aggressiveTactic_increasesDamage() {
        val baseDamage = BattleEngine.calculateDamage(
            attackerStr = 15,
            defenderSta = 10,
            defenderAgi = 10,
            tactic = BattleTactic.AGGRESSIVE,
            isCrit = false,
            isAttackerPlayer = true
        )
        val defensiveDamage = BattleEngine.calculateDamage(
            attackerStr = 15,
            defenderSta = 10,
            defenderAgi = 10,
            tactic = BattleTactic.DEFENSIVE,
            isCrit = false,
            isAttackerPlayer = true
        )
        assertTrue(baseDamage >= defensiveDamage)
    }

    @Test
    fun calculateDamage_criticalHit_increasesDamageSignificantly() {
        val normalDamage = BattleEngine.calculateDamage(
            attackerStr = 20,
            defenderSta = 10,
            defenderAgi = 10,
            tactic = BattleTactic.AGGRESSIVE,
            isCrit = false,
            isAttackerPlayer = true
        )
        val critDamage = BattleEngine.calculateDamage(
            attackerStr = 20,
            defenderSta = 10,
            defenderAgi = 10,
            tactic = BattleTactic.AGGRESSIVE,
            isCrit = true,
            isAttackerPlayer = true
        )
        assertTrue(critDamage > normalDamage)
    }

    @Test
    fun calculateDamage_lanistaShoutAttack_boostsDamage() {
        val unbuffedDamage = BattleEngine.calculateDamage(
            attackerStr = 18,
            defenderSta = 12,
            defenderAgi = 10,
            tactic = BattleTactic.AGGRESSIVE,
            isCrit = false,
            isAttackerPlayer = true,
            lanistaShoutBonus = null
        )
        val buffedDamage = BattleEngine.calculateDamage(
            attackerStr = 18,
            defenderSta = 12,
            defenderAgi = 10,
            tactic = BattleTactic.AGGRESSIVE,
            isCrit = false,
            isAttackerPlayer = true,
            lanistaShoutBonus = "ATTACK"
        )
        assertTrue(buffedDamage >= unbuffedDamage)
    }

    @Test
    fun calculateMatchExcitement_clampsBetween25And100() {
        val lowExcitement = BattleEngine.calculateMatchExcitement(
            crowdHype = 0,
            critCount = 0,
            tactic = BattleTactic.DEFENSIVE,
            isVictorious = false
        )
        assertEquals(25, lowExcitement)

        val highExcitement = BattleEngine.calculateMatchExcitement(
            crowdHype = 150,
            critCount = 10,
            tactic = BattleTactic.CROWD_PLEASER,
            isVictorious = true
        )
        assertEquals(100, highExcitement)
    }

    @Test
    fun calculatePolliceVersoRewards_scalesWithCityTier() {
        val (ruralPrestige, ruralGold) = BattleEngine.calculatePolliceVersoRewards(
            basePrestige = 50,
            baseGold = 100,
            cityTier = CityTier.TOWN_RURAL
        )
        val (colosseumPrestige, colosseumGold) = BattleEngine.calculatePolliceVersoRewards(
            basePrestige = 50,
            baseGold = 100,
            cityTier = CityTier.ROME_COLOSSEUM
        )
        assertTrue(colosseumPrestige > ruralPrestige)
        assertTrue(colosseumGold > ruralGold)
    }
}
