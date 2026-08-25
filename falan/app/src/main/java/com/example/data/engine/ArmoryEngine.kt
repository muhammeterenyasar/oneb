package com.example.data.engine

import com.example.model.EquipmentItem
import com.example.model.EquipmentRarity
import com.example.model.EquipmentSlot
import com.example.model.Gladiator

data class EquipmentBonusStats(
    val totalDamageBonus: Int = 0,
    val totalArmorReductionPercent: Int = 0,
    val totalCritBonusPercent: Int = 0,
    val totalDodgeBonusPercent: Int = 0,
    val totalMaxHpBonus: Int = 0
)

object ArmoryEngine {

    val catalog: List<EquipmentItem> = listOf(
        // WEAPONS
        EquipmentItem(
            id = "wp_toledo_gladius",
            name = "Toledo Çeliği Gladius",
            slot = EquipmentSlot.WEAPON,
            rarity = EquipmentRarity.RARE,
            damageBonus = 6,
            critBonusPercent = 10,
            priceGold = 160,
            description = "İberya'nın dövme yüksek karbonlu çeliğinden üretilmiş keskin piyade kılıcı.",
            icon = "⚔️"
        ),
        EquipmentItem(
            id = "wp_spartan_sica",
            name = "Sparta Kıvrık Sica Kılıcı",
            slot = EquipmentSlot.WEAPON,
            rarity = EquipmentRarity.EPIC,
            damageBonus = 8,
            critBonusPercent = 20,
            priceGold = 280,
            description = "Kalkanların arkasına dolanarak ölümcül yaralar açan eğri gladyatör hançeri.",
            icon = "🗡️"
        ),
        EquipmentItem(
            id = "wp_neptune_trident",
            name = "Neptün'ün Üç Dişli Mızrağı",
            slot = EquipmentSlot.WEAPON,
            rarity = EquipmentRarity.LEGENDARY,
            damageBonus = 12,
            critBonusPercent = 15,
            dodgeBonusPercent = 10,
            priceGold = 450,
            description = "Arenanın efsanevi Retiarius ustaları için tasarlanmış bronz süslemeli mızrak.",
            icon = "🔱"
        ),
        EquipmentItem(
            id = "wp_celtic_spatha",
            name = "Kelt Ağır Süvari Spatha'sı",
            slot = EquipmentSlot.WEAPON,
            rarity = EquipmentRarity.COMMON,
            damageBonus = 4,
            critBonusPercent = 5,
            priceGold = 90,
            description = "Geniş menzilli, ağır vuruşlu sağlam demir kılıç.",
            icon = "🗡️"
        ),

        // ARMOR & HELMETS
        EquipmentItem(
            id = "ar_lorica_segmentata",
            name = "İmparatorluk Lorica Segmentata",
            slot = EquipmentSlot.ARMOR,
            rarity = EquipmentRarity.EPIC,
            armorReductionPercent = 20,
            maxHpBonus = 35,
            priceGold = 260,
            description = "Roma lejyonlarının demir plaka zırhı. Göğüs ve hayati organ darbelerini emerek hasarı kırar.",
            icon = "🛡️"
        ),
        EquipmentItem(
            id = "ar_beast_galea",
            name = "Vahşi Canavar Galea Miğferi",
            slot = EquipmentSlot.ARMOR,
            rarity = EquipmentRarity.RARE,
            armorReductionPercent = 12,
            maxHpBonus = 20,
            critBonusPercent = 5,
            priceGold = 175,
            description = "Aslan kabartmalı bronz vizörlü miğfer; rakibe korku salar.",
            icon = "⛑️"
        ),
        EquipmentItem(
            id = "ar_scutum_titan",
            name = "Murmillo Titan Kalkanı",
            slot = EquipmentSlot.ARMOR,
            rarity = EquipmentRarity.LEGENDARY,
            armorReductionPercent = 28,
            maxHpBonus = 50,
            priceGold = 480,
            description = "Meşe ve deri takviyeli devasa lejyon kalkanı; saldırıları taş gibi savuşturur.",
            icon = "🛡️"
        ),
        EquipmentItem(
            id = "ar_leather_manica",
            name = "Sertleştirilmiş Deri Manica Kolçağı",
            slot = EquipmentSlot.ARMOR,
            rarity = EquipmentRarity.COMMON,
            armorReductionPercent = 8,
            maxHpBonus = 15,
            priceGold = 80,
            description = "Kılıç kolunu koruyan perçinli deri segmentler.",
            icon = "🥋"
        ),

        // RELICS & CHARMS
        EquipmentItem(
            id = "re_jupiter_medallion",
            name = "Jüpiter Şimşek Madalyonu",
            slot = EquipmentSlot.RELIC,
            rarity = EquipmentRarity.LEGENDARY,
            dodgeBonusPercent = 15,
            critBonusPercent = 12,
            damageBonus = 5,
            priceGold = 500,
            description = "Kutsanmış altın madalyon. Savaşçının zihnini ve reflekslerini şimşek hızına ulaştırır.",
            icon = "⚡"
        ),
        EquipmentItem(
            id = "re_eye_of_mars",
            name = "Mars'ın Kanlı Gözü Tılsımı",
            slot = EquipmentSlot.RELIC,
            rarity = EquipmentRarity.EPIC,
            damageBonus = 8,
            critBonusPercent = 15,
            priceGold = 320,
            description = "Savaş tanrısı Mars'ın tapınağında kutsanan yakut taş; her darbede vahşet gücü verir.",
            icon = "🔥"
        ),
        EquipmentItem(
            id = "re_aesculapius_herb",
            name = "Aesculapius Şifa Kesesi",
            slot = EquipmentSlot.RELIC,
            rarity = EquipmentRarity.RARE,
            maxHpBonus = 40,
            armorReductionPercent = 10,
            priceGold = 190,
            description = "Eski şifacı otlarıyla doldurulmuş deri kese; can kaybını yavaşlatır.",
            icon = "🌿"
        ),
        EquipmentItem(
            id = "re_fortuna_coin",
            name = "Fortuna Şans Triketi",
            slot = EquipmentSlot.RELIC,
            rarity = EquipmentRarity.COMMON,
            dodgeBonusPercent = 8,
            priceGold = 95,
            description = "Kader tanrıçası Fortuna'nın simgesi olan antika gümüş sikke.",
            icon = "🪙"
        )
    )

    fun getItemById(id: String?): EquipmentItem? {
        if (id == null) return null
        return catalog.find { it.id == id }
    }

    fun calculateEquipmentBonuses(g: Gladiator): EquipmentBonusStats {
        val weapon = getItemById(g.equippedWeaponId)
        val armor = getItemById(g.equippedArmorId)
        val relic = getItemById(g.equippedRelicId)

        val items = listOfNotNull(weapon, armor, relic)
        return EquipmentBonusStats(
            totalDamageBonus = items.sumOf { it.damageBonus },
            totalArmorReductionPercent = items.sumOf { it.armorReductionPercent },
            totalCritBonusPercent = items.sumOf { it.critBonusPercent },
            totalDodgeBonusPercent = items.sumOf { it.dodgeBonusPercent },
            totalMaxHpBonus = items.sumOf { it.maxHpBonus }
        )
    }

    fun getEffectivePrice(item: EquipmentItem, discountPercent: Int): Int {
        if (discountPercent <= 0) return item.priceGold
        val discount = (item.priceGold * (discountPercent.toFloat() / 100f)).toInt()
        return (item.priceGold - discount).coerceAtLeast(10)
    }
}
