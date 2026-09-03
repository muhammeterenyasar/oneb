package com.example.model

data class StaffMember(
    val id: String,
    val role: String,
    val name: String,
    var level: Int,
    val specialty: String,
    var monthlyWage: Int,
    val perkDescription: String
)

data class Facility(
    val id: String,
    val name: String,
    var level: Int,
    val maxLevel: Int = 5,
    val upgradeCost: Int,
    val description: String,
    val currentBonus: String,
    val nextBonus: String
)

data class ChronicleEntry(
    val yearAUC: String,
    val title: String,
    val description: String,
    val isGlory: Boolean = true
)

data class RivalLudus(
    val id: String,
    val name: String,
    val lanista: String,
    val city: String,
    var reputation: Int,
    val championName: String,
    val championClass: GladiatorClass,
    val rivalryDescription: String
)

data class Patron(
    val id: String,
    val name: String,
    val title: String, // e.g. "Senator of Capua", "Magistrate", "Wine Merchant Magnate"
    var favor: Int, // 0 - 100
    val monthlyStipend: Int,
    val demand: String,
    val benefit: String
)

data class CityProgression(
    val tier: Int,
    val cityName: String,
    val arenaName: String,
    val prestigeRequired: Int,
    val purseMultiplier: Float,
    val isUnlocked: Boolean,
    val description: String
)

data class ScheduledMatch(
    val id: String,
    val arenaCity: String,
    val arenaName: String,
    val matchDateText: String,
    val opponentLudus: String,
    val opponentGladiator: Gladiator,
    val basePrizeGold: Int,
    val basePrestige: Int,
    val matchType: String = "1v1 Deathmatch"
)

data class LudusEvent(
    val id: String,
    val title: String,
    val description: String,
    val optionA: String,
    val optionB: String,
    val costA: Int = 0,
    val prestigeChangeA: Int = 0,
    val costB: Int = 0,
    val prestigeChangeB: Int = 0
)

data class LudusDominus(
    var name: String = "Marcus Aurelius Valerius",
    var denarii: Int = 12500,
    var prestige: Int = 1250,
    var popularity: Int = 1080,
    var fear: Int = 620,
    var loyalty: Int = 75,
    var foodWheat: Int = 2340,
    var dayNumber: Int = 15,
    var monthName: String = "Martius",
    var yearAUC: String = "69 A.U.C.",
    var currentCity: String = "Capua"
)

data class EconomyReport(
    val matchRewards: Int = 4250,
    val bettingWins: Int = 1800,
    val sponsorStipend: Int = 1500,
    val gladiatorWages: Int = -1600,
    val staffWages: Int = -1100,
    val foodAndMaintenance: Int = -850
) {
    val netProfit: Int get() = matchRewards + bettingWins + sponsorStipend + gladiatorWages + staffWages + foodAndMaintenance
}
