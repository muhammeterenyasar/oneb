package com.example

import com.example.model.LanistaPerk
import org.junit.Assert.*
import org.junit.Test

class LanistaPerksTest {

    @Test
    fun testAllLanistaPerksHaveValidCosts() {
        val perks = LanistaPerk.entries
        assertTrue(perks.isNotEmpty())
        for (perk in perks) {
            assertTrue(perk.prestigeCost > 0)
            assertTrue(perk.title.isNotBlank())
            assertTrue(perk.icon.isNotBlank())
        }
    }
}
