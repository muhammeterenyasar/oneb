package com.example.data.engine

import com.example.model.Gladiator
import com.example.model.LudusState
import com.example.model.RivalLudus

object RivalLeagueEngine {

    fun calculateLeagueStandings(ludusState: LudusState, gladiators: List<Gladiator>): List<RivalLudus> {
        val topPlayerGladiator = gladiators.maxByOrNull { it.totalPowerScore }
        val playerChampionName = topPlayerGladiator?.let { "${it.name} (${it.nickname})" } ?: "Kadro Hazırlanıyor"
        val playerPoints = (ludusState.totalWins * 3) + (ludusState.prestige / 15)

        val baseSchools = listOf(
            RivalLudus(
                id = "ludus_player",
                name = "Ludus Magnus (Sizin Okulunuz)",
                city = ludusState.cityTier.cityName,
                lanistaName = "Siz (${ludusState.reputationTitle})",
                points = playerPoints,
                wins = ludusState.totalWins,
                losses = (ludusState.totalFights - ludusState.totalWins).coerceAtLeast(0),
                schoolDoctrine = "Özelleştirilmiş Taktik & Doktrin",
                championName = playerChampionName,
                badgeIcon = "🏛️",
                isPlayerSchool = true
            ),
            RivalLudus(
                id = "ludus_batiatus",
                name = "Ludus Batiatus",
                city = "Capua",
                lanistaName = "Lentulus Batiatus",
                points = 18 + (ludusState.day * 2) + 6,
                wins = 6 + (ludusState.day / 2),
                losses = 2 + (ludusState.day / 5),
                schoolDoctrine = "⚔️ Agresif Dövüş & Kanlı Hücum",
                championName = "Gannicus (Yenilmez Kelt)",
                badgeIcon = "🦁"
            ),
            RivalLudus(
                id = "ludus_iulius",
                name = "Ludus Iulius Caesaris",
                city = "Roma",
                lanistaName = "Senator Marcus Iulius",
                points = 24 + (ludusState.day * 2) + 12,
                wins = 8 + (ludusState.day / 2),
                losses = 1 + (ludusState.day / 6),
                schoolDoctrine = "🛡️ İmparatorluk Ağır Zırhı & Scutum",
                championName = "Aurelius Invictus (Lejyon Muhafızı)",
                badgeIcon = "🦅"
            ),
            RivalLudus(
                id = "ludus_pompeii",
                name = "Ludus Magna Graecia",
                city = "Pompeii",
                lanistaName = "Demetrius of Rhodes",
                points = 14 + (ludusState.day * 2),
                wins = 4 + (ludusState.day / 3),
                losses = 3 + (ludusState.day / 4),
                schoolDoctrine = "🔱 Retiarius Ağı & Kaçınma Sanatı",
                championName = "Theron Üç Dişli",
                badgeIcon = "🌊"
            ),
            RivalLudus(
                id = "ludus_gallicus",
                name = "Ludus Gallicus",
                city = "Verona",
                lanistaName = "Brennos Baltaustası",
                points = 16 + (ludusState.day * 2) + 2,
                wins = 5 + (ludusState.day / 3),
                losses = 4 + (ludusState.day / 4),
                schoolDoctrine = "🪓 Barbar Ezici Gücü & Berserk",
                championName = "Vercingetorix Çekiç",
                badgeIcon = "🐻"
            ),
            RivalLudus(
                id = "ludus_aegypti",
                name = "Ludus Anubis",
                city = "Alexandria",
                lanistaName = "Ptolemy The Snake",
                points = 12 + (ludusState.day * 2) - 2,
                wins = 4 + (ludusState.day / 3),
                losses = 5 + (ludusState.day / 4),
                schoolDoctrine = "🗡️ Çift Kıvrık Kılıç & Zehirli Vuruş",
                championName = "Khepri Gölge",
                badgeIcon = "🦂"
            )
        )

        return baseSchools.sortedByDescending { it.points }
    }
}
