package com.example

import com.example.data.engine.CampaignEngine
import org.junit.Assert.*
import org.junit.Test

class CampaignEngineTest {

    @Test
    fun testAllChaptersHaveMissions() {
        val chapter1 = CampaignEngine.getMissionsForChapter(1)
        val chapter2 = CampaignEngine.getMissionsForChapter(2)
        val chapter3 = CampaignEngine.getMissionsForChapter(3)
        val chapter4 = CampaignEngine.getMissionsForChapter(4)

        assertTrue(chapter1.isNotEmpty())
        assertTrue(chapter2.isNotEmpty())
        assertTrue(chapter3.isNotEmpty())
        assertTrue(chapter4.isNotEmpty())
        assertEquals(8, CampaignEngine.allMissions.size)
    }

    @Test
    fun testMissionUnlockProgression() {
        val mission1 = CampaignEngine.allMissions[0]
        val mission2 = CampaignEngine.allMissions[1]

        // Mission 1 is unlocked initially
        assertTrue(CampaignEngine.isMissionUnlocked(mission1, emptyList()))

        // Mission 2 is locked without mission 1 completion
        assertFalse(CampaignEngine.isMissionUnlocked(mission2, emptyList()))

        // Mission 2 is unlocked when mission 1 is completed
        assertTrue(CampaignEngine.isMissionUnlocked(mission2, listOf(mission1.id)))
    }
}
