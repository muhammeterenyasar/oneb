package com.example

import com.example.data.engine.ArmoryEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class ArmoryEngineTest {

    @Test
    fun testCatalogHasItems() {
        val catalog = ArmoryEngine.catalog
        assertTrue(catalog.isNotEmpty())
        assertTrue(catalog.any { it.slot == EquipmentSlot.WEAPON })
        assertTrue(catalog.any { it.slot == EquipmentSlot.ARMOR })
        assertTrue(catalog.any { it.slot == EquipmentSlot.RELIC })
    }

    @Test
    fun testCalculateEquipmentBonuses() {
        val gladiator = Gladiator(
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
            equippedWeaponId = "wp_toledo_gladius",
            equippedArmorId = "ar_lorica_segmentata",
            equippedRelicId = "re_jupiter_medallion"
        )

        val bonuses = ArmoryEngine.calculateEquipmentBonuses(gladiator)
        assertTrue(bonuses.totalDamageBonus >= 6)
        assertTrue(bonuses.totalArmorReductionPercent >= 20)
        assertTrue(bonuses.totalCritBonusPercent >= 10)
        assertTrue(bonuses.totalDodgeBonusPercent >= 15)
    }

    @Test
    fun testSilverTongueDiscount() {
        val item = ArmoryEngine.catalog.first()
        val basePrice = item.priceGold
        val discountedPrice = ArmoryEngine.getEffectivePrice(item, 20)
        assertTrue(discountedPrice < basePrice)
        assertEquals((basePrice * 0.8f).toInt(), discountedPrice)
    }
}
