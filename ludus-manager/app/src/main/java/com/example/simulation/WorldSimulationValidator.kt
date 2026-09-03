package com.example.simulation

import com.example.model.*

/**
 * Developer Assertion & Invariant Validation Layer.
 * Validates canonical world state integrity across all systems:
 * - Matchmaking, calendar, underground, recruitment, rankings, and identity uniqueness.
 */
object WorldSimulationValidator {

    data class ValidationResult(
        val isValid: Boolean,
        val violations: List<String>
    )

    fun validateWorldState(state: LudusUiState): ValidationResult {
        val violations = mutableListOf<String>()

        val allGladiators = (state.gladiators + state.persistentFighters).distinctBy { it.id }
        val deadGladiatorIds = allGladiators.filter { it.isDead || !it.isAlive || it.status == GladiatorStatus.DEAD }.map { it.id }.toSet()

        // 1. assert(deadGladiatorNotInActiveMatchmaking)
        state.arenaCalendar.filter { !it.isCompleted }.forEach { bout ->
            if (bout.fighter1Id in deadGladiatorIds) {
                violations.add("Dead gladiator '${bout.fighter1Name}' (${bout.fighter1Id}) is scheduled in active bout '${bout.id}' on day ${bout.day}")
            }
            if (bout.fighter2Id in deadGladiatorIds && bout.fighter2Id != "player") {
                violations.add("Dead gladiator '${bout.fighter2Name}' (${bout.fighter2Id}) is scheduled in active bout '${bout.id}' on day ${bout.day}")
            }
        }

        // 2. assert(deadGladiatorNotInUnderground)
        state.undergroundFights.filter { !it.isCompleted }.forEach { fight ->
            if (fight.opponentFighter.id in deadGladiatorIds || fight.opponentFighter.isDead) {
                violations.add("Dead gladiator '${fight.opponentFighter.fullDisplayName}' (${fight.opponentFighter.id}) is in active underground fight '${fight.id}'")
            }
        }

        // 3. assert(deadGladiatorNotInRecruitment)
        state.marketGladiators.forEach { recruit ->
            if (recruit.id in deadGladiatorIds || recruit.isDead) {
                violations.add("Dead gladiator '${recruit.name}' (${recruit.id}) is in recruitment market")
            }
        }

        // 4. assert(deadGladiatorNotInRanking)
        val livingVenueFighters = state.persistentFighters.filter { it.currentArena == state.currentVenue }
        val rankings = ArenaProgressionEngine.computeLivingRankings(state.currentVenue, livingVenueFighters, state.selectedGladiator)
        rankings.forEach { rank ->
            if (rank.fighterId != null && rank.fighterId in deadGladiatorIds) {
                violations.add("Dead gladiator '${rank.name}' (${rank.fighterId}) appears in active ranking #${rank.rank}")
            }
        }

        // 5. assert(noDuplicateGladiatorIds)
        val seenIds = mutableSetOf<String>()
        state.gladiators.forEach { g ->
            if (!seenIds.add(g.id)) {
                violations.add("Duplicate gladiator ID in player roster: '${g.id}'")
            }
        }
        val persistentIds = mutableSetOf<String>()
        state.persistentFighters.forEach { pf ->
            if (!persistentIds.add(pf.id)) {
                violations.add("Duplicate gladiator ID in persistent fighters: '${pf.id}'")
            }
        }

        // 6. assert(noDuplicateScheduledMatches)
        val boutIds = mutableSetOf<String>()
        state.arenaCalendar.forEach { bout ->
            if (!boutIds.add(bout.id)) {
                violations.add("Duplicate arena calendar bout ID: '${bout.id}'")
            }
        }

        // 7. assert(noFighterInTwoMatchesSameTime)
        val fightsByDay = state.arenaCalendar.filter { !it.isCompleted }.groupBy { it.day }
        fightsByDay.forEach { (day, bouts) ->
            val fightersOnDay = mutableSetOf<String>()
            bouts.forEach { b ->
                if (b.fighter1Id.isNotBlank() && !fightersOnDay.add(b.fighter1Id)) {
                    violations.add("Fighter '${b.fighter1Id}' is scheduled in multiple fights on day $day")
                }
                if (b.fighter2Id.isNotBlank() && b.fighter2Id != "player" && !fightersOnDay.add(b.fighter2Id)) {
                    violations.add("Fighter '${b.fighter2Id}' is scheduled in multiple fights on day $day")
                }
            }
        }

        // 8. assert(bossExistsOnlyOnce)
        val bossNicknames = listOf("The Wolf", "Black Sand", "The Syrian", "Gravedigger", "Red Knife")
        bossNicknames.forEach { bossNick ->
            val matches = state.persistentFighters.filter { it.nickname == bossNick }
            if (matches.size > 1) {
                violations.add("Boss '$bossNick' exists ${matches.size} times in persistent fighters: ${matches.map { it.id }}")
            }
        }

        return ValidationResult(isValid = violations.isEmpty(), violations = violations)
    }
}
