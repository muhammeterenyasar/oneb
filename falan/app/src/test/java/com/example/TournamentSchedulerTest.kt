package com.example

import com.example.data.engine.TournamentScheduler
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class TournamentSchedulerTest {

    @Test
    fun generateScheduledEventForArena_returnsAppropriateTierEvents() {
        val ruralEvent = TournamentScheduler.generateScheduledEventForArena(
            cityTier = CityTier.TOWN_RURAL,
            day = 1,
            targetDay = 4
        )
        assertEquals(CityTier.TOWN_RURAL, ruralEvent.cityTier)
        assertEquals(4, ruralEvent.targetDay)
        assertEquals(EnemyTier.VETERAN, ruralEvent.enemyTier)

        val colosseumEvent = TournamentScheduler.generateScheduledEventForArena(
            cityTier = CityTier.ROME_COLOSSEUM,
            day = 20,
            targetDay = 24
        )
        assertEquals(CityTier.ROME_COLOSSEUM, colosseumEvent.cityTier)
        assertEquals(EnemyTier.CHAMPION_BOSS, colosseumEvent.enemyTier)
        assertTrue(colosseumEvent.rewardGold > ruralEvent.rewardGold)
    }

    @Test
    fun generateUpcomingScheduledEvents_returnsFiveSequentialEvents() {
        val calendar = TournamentScheduler.generateUpcomingScheduledEvents(
            currentCityTier = CityTier.TOWN_RURAL,
            currentDay = 1,
            nextMatchDay = 4
        )
        assertEquals(4, calendar.size)
        assertTrue(calendar[0].targetDay < calendar[1].targetDay)
        assertTrue(calendar[1].targetDay < calendar[2].targetDay)
        assertTrue(calendar[2].targetDay < calendar[3].targetDay)
    }

    @Test
    fun generateOpponentsForCity_generatesFourGradedOpponents() {
        val opponents = TournamentScheduler.generateOpponentsForCity(
            cityTier = CityTier.CAPUA_POMPEII,
            playerPower = 85,
            day = 5,
            difficultyModifier = 1.0f,
            ruthlessnessScore = 0,
            crowdSentimentScore = 50
        )
        assertEquals(4, opponents.size)
        assertEquals(EnemyTier.NOVICE, opponents[0].tier)
        assertEquals(EnemyTier.VETERAN, opponents[1].tier)
        assertEquals(EnemyTier.ELITE, opponents[2].tier)
        assertEquals(EnemyTier.CHAMPION_BOSS, opponents[3].tier)
        assertTrue(opponents[3].maxHp > opponents[0].maxHp)
    }
}
