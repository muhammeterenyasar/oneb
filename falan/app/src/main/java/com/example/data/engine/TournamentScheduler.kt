package com.example.data.engine

import com.example.model.*
import kotlin.math.max
import kotlin.random.Random

/**
 * Isolated Arena Tournament & Opponent Generator Engine.
 * Generates structured arena schedules, regional championship bosses, and balanced enemy gladiators.
 */
object TournamentScheduler {

    private data class ScheduledConfig(
        val title: String,
        val host: String,
        val format: MatchFormat,
        val bossTier: EnemyTier,
        val baseGold: Int,
        val basePrestige: Int,
        val rule: String,
        val advice: String,
        val focus: TrainingType,
        val bonusGold: Int,
        val hype: String
    )

    fun generateScheduledEventForArena(cityTier: CityTier, day: Int, targetDay: Int): ScheduledArenaEvent {
        val config = when (cityTier) {
            CityTier.TOWN_RURAL -> ScheduledConfig(
                title = "Kasaba Çaylaklar Munerası",
                host = "Magistrat Servius",
                format = MatchFormat.LUSUS,
                bossTier = EnemyTier.VETERAN,
                baseGold = 160 + (day * 12),
                basePrestige = 35 + (day * 3),
                rule = "Standart Munera (1v1 Düello)",
                advice = "Rakip çaylak ama dirençli. Çeviklik talimi yaparak savuşturma şansını artırın.",
                focus = TrainingType.AGILITY,
                bonusGold = 40,
                hype = "Kasaba Halkı Heyecanlı 🏟️"
            )
            CityTier.CAPUA_POMPEII -> ScheduledConfig(
                title = "Capua Amfitiyatrosu Büyük Oyunları",
                host = "Lanista Lentulus Batiatus",
                format = MatchFormat.TEAM_2V2,
                bossTier = EnemyTier.ELITE,
                baseGold = 420 + (day * 20),
                basePrestige = 85 + (day * 6),
                rule = "2v2 Eşli Takım Dövüşü",
                advice = "Kondisyon ve dayanıklılık önemli. Ağır talim ile Dayanıklılık (STA) geliştirin.",
                focus = TrainingType.STAMINA,
                bonusGold = 90,
                hype = "Capua Soyluları Bahis Yapıyor 🎲"
            )
            CityTier.VERONA -> ScheduledConfig(
                title = "Verona Senato Munera Kupası",
                host = "Senatör Gaius Cornelius",
                format = MatchFormat.SINE_MISSIO,
                bossTier = EnemyTier.CHAMPION_BOSS,
                baseGold = 900 + (day * 35),
                basePrestige = 180 + (day * 10),
                rule = "Sine Missio (Ölüm Kalım Karşılaşması)",
                advice = "Karşılaşacağınız şampiyon zırhlı! Güç (STR) ve Savunma taktiklerine odaklanın.",
                focus = TrainingType.STRENGTH,
                bonusGold = 200,
                hype = "Senatör Locası ve Yüksek Prestij 🏛️"
            )
            CityTier.ROME_COLOSSEUM -> ScheduledConfig(
                title = "Roma Colosseum İmparatorluk Şampiyonası",
                host = "İmparatorluk Başyargıcı Caesar",
                format = MatchFormat.SINE_MISSIO,
                bossTier = EnemyTier.CHAMPION_BOSS,
                baseGold = 2200 + (day * 60),
                basePrestige = 450 + (day * 20),
                rule = "İmparatorluk Nihai Şampiyonluğu",
                advice = "Efsanevi Colosseum boss'u! Tüm statları zirveye taşıyın ve dinlenmeyi ihmal etmeyin.",
                focus = TrainingType.STRENGTH,
                bonusGold = 500,
                hype = "50.000 Roma Vatandaşı ve Sezar 👑"
            )
        }

        val bossOpponent = generateOpponentsForCity(cityTier, 90, targetDay).firstOrNull { it.tier == config.bossTier }
            ?: generateOpponentsForCity(cityTier, 90, targetDay).last()

        return ScheduledArenaEvent(
            id = "event_${cityTier.name}_$targetDay",
            title = config.title,
            arenaName = cityTier.arenaLevelName,
            cityTier = cityTier,
            targetDay = targetDay,
            hostPatron = config.host,
            description = "${cityTier.cityName} halkı ve soyluları bu büyük müsabakayı bekliyor. Kazanan ludus'a altın ve büyük prestij bahşedilecek!",
            matchFormat = config.format,
            enemyTier = config.bossTier,
            featuredOpponent = bossOpponent,
            rewardGold = config.baseGold,
            rewardPrestige = config.basePrestige,
            isCompleted = false,
            specialRule = config.rule,
            tacticalAdvice = config.advice,
            recommendedFocus = config.focus,
            patronBonusGold = config.bonusGold,
            tierLevel = cityTier.tierNumber,
            crowdHypeText = config.hype
        )
    }

    fun generateUpcomingScheduledEvents(
        currentCityTier: CityTier,
        currentDay: Int,
        nextMatchDay: Int
    ): List<ScheduledArenaEvent> {
        val events = mutableListOf<ScheduledArenaEvent>()

        // 1. Next Immediate Fight (Target Day)
        events.add(generateScheduledEventForArena(currentCityTier, currentDay, nextMatchDay))

        // 2. Next Regional Clash (+4 Days)
        val day2 = nextMatchDay + 4
        val tier2 = if (currentCityTier.tierNumber >= 2) currentCityTier else CityTier.CAPUA_POMPEII
        val boss2 = generateOpponentsForCity(tier2, 95, day2).firstOrNull { it.tier == EnemyTier.ELITE }
            ?: generateOpponentsForCity(tier2, 95, day2).last()
        events.add(
            ScheduledArenaEvent(
                id = "event_cal_2_$day2",
                title = if (currentCityTier == CityTier.TOWN_RURAL) "Capua Amfitiyatrosu Bölgesel Eleme Oyunları" else "Bölgesel Şampiyonlar Mücadelesi",
                arenaName = tier2.arenaLevelName,
                cityTier = tier2,
                targetDay = day2,
                hostPatron = "Lanista Lentulus Batiatus",
                description = "Capua amfitiyatrosu seyircileri en güçlü takımları arıyor. 2v2 eşli takım dövüşü düzenlenecek.",
                matchFormat = MatchFormat.TEAM_2V2,
                enemyTier = EnemyTier.ELITE,
                featuredOpponent = boss2,
                rewardGold = 450 + (day2 * 18),
                rewardPrestige = 90 + (day2 * 5),
                specialRule = "2v2 Eşli Takım Dövüşü • Sinerji Bonusu",
                tacticalAdvice = "Takım arkadaşı desteği kritik. Dayanıklılık (STA) ve Savunma taktiği ile dövüşün.",
                recommendedFocus = TrainingType.STAMINA,
                patronBonusGold = 90,
                tierLevel = 2,
                crowdHypeText = "Yoğun İlgi & Bahisler Açıldı 🎲"
            )
        )

        // 3. Senator's Grand Munera (+8 Days)
        val day3 = nextMatchDay + 8
        val tier3 = if (currentCityTier.tierNumber >= 3) currentCityTier else CityTier.VERONA
        val boss3 = generateOpponentsForCity(tier3, 100, day3).firstOrNull { it.tier == EnemyTier.CHAMPION_BOSS }
            ?: generateOpponentsForCity(tier3, 100, day3).last()
        events.add(
            ScheduledArenaEvent(
                id = "event_cal_3_$day3",
                title = "Verona Senato Özel Munera Kupası",
                arenaName = tier3.arenaLevelName,
                cityTier = tier3,
                targetDay = day3,
                hostPatron = "Senatör Gaius Cornelius",
                description = "Roma Senatosu'nun önde gelen üyeleri şerefine düzenlenen yüksek tansiyonlu dövüş.",
                matchFormat = MatchFormat.SINE_MISSIO,
                enemyTier = EnemyTier.CHAMPION_BOSS,
                featuredOpponent = boss3,
                rewardGold = 950 + (day3 * 30),
                rewardPrestige = 190 + (day3 * 8),
                specialRule = "Sine Missio (Merhametsiz Dövüş) • Teslim Olmak Yok",
                tacticalAdvice = "Ölümcül karşılaşma! Gladyatörün HP'si tam olmalı, Çeviklik ve Hançer savuşturması çalıştırın.",
                recommendedFocus = TrainingType.AGILITY,
                patronBonusGold = 180,
                tierLevel = 3,
                crowdHypeText = "Senatör Özel Locası & Yüksek Bahis 🏛️"
            )
        )

        // 4. Imperial Colosseum Preliminaries (+13 Days)
        val day4 = nextMatchDay + 13
        val tier4 = CityTier.ROME_COLOSSEUM
        val boss4 = generateOpponentsForCity(tier4, 110, day4).firstOrNull { it.tier == EnemyTier.CHAMPION_BOSS }
            ?: generateOpponentsForCity(tier4, 110, day4).last()
        events.add(
            ScheduledArenaEvent(
                id = "event_cal_4_$day4",
                title = "Roma İmparatorluk Ön Eleme Munerası",
                arenaName = "Roma Flavius Amfitiyatrosu",
                cityTier = tier4,
                targetDay = day4,
                hostPatron = "Praetor Titus Quintus",
                description = "İmparatorluk Colosseum'una davetiye kazanmak için 50.000 Roma vatandaşının önünde eleme savaşı.",
                matchFormat = MatchFormat.SINE_MISSIO,
                enemyTier = EnemyTier.CHAMPION_BOSS,
                featuredOpponent = boss4,
                rewardGold = 1800 + (day4 * 45),
                rewardPrestige = 340 + (day4 * 14),
                specialRule = "İmparatorluk Jürisi • Seyirci Alkış Bonusu",
                tacticalAdvice = "Ağır zırhlı ve acımasız şampiyon. Güç (STR) ve Karşı Saldırı ile zırh delmeye odaklanın.",
                recommendedFocus = TrainingType.STRENGTH,
                patronBonusGold = 350,
                tierLevel = 4,
                crowdHypeText = "İmparatorluk Başyargıcı İzleyecek 👑"
            )
        )

        return events
    }

    fun generateOpponentsForCity(
        cityTier: CityTier,
        playerPower: Int = 80,
        day: Int = 1,
        difficultyModifier: Float = 1.0f,
        ruthlessnessScore: Int = 0,
        crowdSentimentScore: Int = 50
    ): List<EnemyGladiator> {
        val firstNames = listOf(
            "Spiculus", "Flamma", "Marcus", "Aulus", "Gannicus", "Oenomaus", "Castus", "Decimus",
            "Aetius", "Varro", "Tiberius", "Brixius", "Drusus", "Commodus", "Vercingetorix",
            "Severus", "Maximus", "Lucius", "Aurelius", "Herakles", "Ignis", "Nero", "Spartacus",
            "Draco", "Vulkan", "Titus", "Valerius", "Caius", "Brutus", "Gorgon", "Crixus", "Hermes"
        ).shuffled()

        val epicTitles = listOf(
            "Kumların Celladı", "Kemik Kıran", "Yenilmez Trak", "Colosseum Aslanı",
            "Kuzeyin Kurdu", "Gölge Avcısı", "Demir Duvar", "Kana Susamış"
        ).shuffled()

        val vendettaTitles = listOf(
            "İntikam Yemini Eden", "Kan Taciri", "Lanista Avcısı", "Ölüm Meleği", "Cellat Katili"
        ).shuffled()

        val honorTitles = listOf(
            "Roma Muhafızı", "Asil Düellocu", "Senato Şampiyonu", "Zafer Kartalı", "Erdemli Gladyatör"
        ).shuffled()

        val ludusHouses = when (cityTier) {
            CityTier.TOWN_RURAL -> listOf("Kasaba Eğitmenleri", "Yerel Lanista Grubu", "Kırsal Arena Birliği")
            CityTier.CAPUA_POMPEII -> listOf("Ludus Batiatus (Capua)", "Capua Aslanları", "Pompeii Muhafızları")
            CityTier.VERONA -> listOf("Verona Lejyonu", "Kuzey Muhafızları", "Senato Dövüşçüleri")
            CityTier.ROME_COLOSSEUM -> listOf("İmparatorluk Ludus Magnus", "Sezar'ın Şampiyonları", "Roma Colosseum Eliti")
        }.shuffled()

        val traits = listOf(
            Pair("Zırh Delici", "Kritik vuruşları savunmayı tamamen delip geçer."),
            Pair("Demir Kalkan", "Gelen tüm saldırılardan %25 daha az hasar alır."),
            Pair("Ağ Tuzağı", "Rakibin çevikliğini kısıtlayarak blok şansını düşürür."),
            Pair("Öfke Patlaması", "Canı azaldığında +%40 daha yüksek hasar vurur."),
            Pair("Zehirli Uç", "Her vuruşta rakibin dayanıklılığını tüketir."),
            Pair("Çevik Kaçınma", "Darbelerden sıyrılma ve karşı hamle şansı %25 daha yüksektir.")
        ).shuffled()

        val baseTierStat = when (cityTier) {
            CityTier.TOWN_RURAL -> 11 + (day / 8)
            CityTier.CAPUA_POMPEII -> 16 + (day / 6)
            CityTier.VERONA -> 22 + (day / 5)
            CityTier.ROME_COLOSSEUM -> 28 + (day / 4)
        }

        val sentimentLevel = CrowdSentimentLevel.fromScore(crowdSentimentScore, ruthlessnessScore, 0)
        val combinedDifficultyMod = (difficultyModifier + sentimentLevel.threatModifier).coerceIn(0.75f, 2.30f)

        val classes = GladiatorClass.entries
        val tiers = listOf(EnemyTier.NOVICE, EnemyTier.VETERAN, EnemyTier.ELITE, EnemyTier.CHAMPION_BOSS)

        return tiers.mapIndexed { index, tier ->
            val gClass = classes[(index + Random.nextInt(classes.size)) % classes.size]
            val isVendettaTarget = combinedDifficultyMod >= 1.20f && index >= 1
            val isHonorTarget = sentimentLevel == CrowdSentimentLevel.IMPERIAL_CLEMENTIA || sentimentLevel == CrowdSentimentLevel.ENTHUSIASTIC_FAVORITE

            val (traitName, traitDesc) = if (isVendettaTarget) {
                Pair("İntikam Hırsı", "Önceki infaz edilen gladyatörlerin intikamı için savaşıyor! +%25 Ekstra Saldırı ve Kritik Gücü.")
            } else if (isHonorTarget && index == 3) {
                Pair("Asil Düello Disiplini", "Roma'nın en asil arenalarında yetişmiş efsanevi şampiyon tekniği.")
            } else {
                traits[index % traits.size]
            }

            val name = firstNames[index % firstNames.size]
            val title = if (isVendettaTarget) {
                "⚔️ ${vendettaTitles[index % vendettaTitles.size]}"
            } else if (isHonorTarget && index >= 2) {
                "🏛️ ${honorTitles[index % honorTitles.size]}"
            } else if (tier == EnemyTier.CHAMPION_BOSS) {
                "👑 ${epicTitles[index % epicTitles.size]}"
            } else {
                "${gClass.displayName} • ${epicTitles[index % epicTitles.size]}"
            }
            val ludus = ludusHouses[index % ludusHouses.size]

            val tierStatBonus = when (tier) {
                EnemyTier.NOVICE -> -2
                EnemyTier.VETERAN -> 2
                EnemyTier.ELITE -> 6
                EnemyTier.CHAMPION_BOSS -> 12
            }

            val statVariance = Random.nextInt(-1, 3)
            val baseStatCalc = max(8, baseTierStat + tierStatBonus + statVariance)
            val effectiveStat = (baseStatCalc * combinedDifficultyMod).toInt()

            val str = effectiveStat + if (gClass == GladiatorClass.MURMILLO || tier == EnemyTier.CHAMPION_BOSS) 3 else 0
            val agi = effectiveStat + if (gClass == GladiatorClass.RETIARIUS || gClass == GladiatorClass.THRAEX) 3 else 0
            val sta = effectiveStat + if (gClass == GladiatorClass.SECUTOR || tier == EnemyTier.CHAMPION_BOSS) 4 else 0

            val baseHp = (90 + sta * 4)
            val finalHp = ((baseHp * tier.hpMultiplier) * combinedDifficultyMod).toInt()

            val rewardMultiplier = when (tier) {
                EnemyTier.NOVICE -> 1.0f
                EnemyTier.VETERAN -> 1.4f
                EnemyTier.ELITE -> 2.0f
                EnemyTier.CHAMPION_BOSS -> 3.2f
            }

            val baseGold = ((cityTier.tierNumber * 70) + Random.nextInt(25, 55)) * rewardMultiplier * (if (combinedDifficultyMod > 1.1f) 1.25f else 1.0f) * sentimentLevel.goldBonusMult
            val basePrestige = ((cityTier.tierNumber * 18) + Random.nextInt(8, 20)) * rewardMultiplier * (if (combinedDifficultyMod > 1.1f) 1.30f else 1.0f) * sentimentLevel.prestigeBonusMult
            val bonusLevel = if (combinedDifficultyMod >= 1.25f) 2 else if (combinedDifficultyMod >= 1.10f) 1 else 0

            EnemyGladiator(
                name = name,
                title = title,
                ludusOrigin = ludus,
                gladiatorClass = gClass,
                tier = tier,
                level = cityTier.tierNumber * 2 + index + bonusLevel,
                traitName = traitName,
                traitDescription = traitDesc,
                str = str,
                agi = agi,
                sta = sta,
                mor = 75 + index * 5,
                maxHp = finalHp,
                currentHp = finalHp,
                rewardGold = baseGold.toInt(),
                rewardPrestige = basePrestige.toInt()
            )
        }
    }
}
