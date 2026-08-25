package com.example.data.engine

import com.example.model.*
import kotlin.math.max
import kotlin.random.Random

/**
 * Isolated Roman Events, Dilemmas, Subura Tavern & Minigames Engine.
 */
object EventEngine {

    fun executeTesseraeRoll(betAmount: Int, playerGold: Int): TesseraeGameState {
        if (playerGold < betAmount) {
            return TesseraeGameState(
                betAmount = betAmount,
                hasPlayedThisCycle = false,
                isPlayerWinner = null,
                goldReward = 0,
                rollName = "Yetersiz Altın"
            )
        }

        val p1 = Random.nextInt(1, 7)
        val p2 = Random.nextInt(1, 7)
        val p3 = Random.nextInt(1, 7)
        val pTotal = p1 + p2 + p3

        val r1 = Random.nextInt(1, 7)
        val r2 = Random.nextInt(1, 7)
        val r3 = Random.nextInt(1, 7)
        val rTotal = r1 + r2 + r3

        val isVenus = (p1 == 6 && p2 == 6 && p3 == 6) || (p1 == p2 && p2 == p3)
        val isDog = (p1 == 1 && p2 == 1 && p3 == 1)

        val isWinner = if (isVenus) true else if (isDog) false else pTotal >= rTotal
        val multiplier = if (isVenus) 3.0f else 1.8f
        val reward = if (isWinner) (betAmount * multiplier).toInt() else 0

        val rollName = when {
            isVenus -> "VENÜS ATIŞI! (3x Katı Kazanç)"
            isDog -> "KÖPEK ATIŞI! (Kayıp)"
            isWinner -> "Şanslı Roma Zarı (Zafer)"
            else -> "Kumarbazın Zarı (Kayıp)"
        }

        return TesseraeGameState(
            betAmount = betAmount,
            playerDice = listOf(p1, p2, p3),
            rivalDice = listOf(r1, r2, r3),
            playerTotal = pTotal,
            rivalTotal = rTotal,
            isPlayerWinner = isWinner,
            goldReward = reward,
            rollName = rollName,
            hasPlayedThisCycle = true
        )
    }

    fun simulateSparring(
        fighter1: Gladiator,
        fighter2: Gladiator?,
        activeTeachers: List<Teacher>
    ): SparringState {
        val opponent = fighter2 ?: Gladiator(
            id = -1,
            name = "Doctore Tahta Kuklası",
            nickname = "İdman Kuklası",
            origin = "Roma",
            gladiatorClass = GladiatorClass.MURMILLO,
            contractType = GladiatorContractType.SLAVE,
            dailySalary = 0,
            priceValue = 0,
            str = 10,
            agi = 10,
            sta = 10,
            mor = 50,
            age = 20,
            fatigue = 0,
            currentHp = 100,
            maxHp = 100,
            wins = 0,
            losses = 0
        )

        val hasMasterCoach = activeTeachers.any {
            it.specialty == TeacherSpecialty.VETERAN_LEGEND || it.specialty == TeacherSpecialty.GLADIATOR_DISCIPLINE
        }
        val coachMultiplier = if (hasMasterCoach) 1.35f else 1.0f
        val xpGain = (25 * coachMultiplier).toInt()

        val p1Roll = fighter1.str + fighter1.agi + Random.nextInt(1, 10)
        val p2Roll = opponent.str + opponent.agi + Random.nextInt(1, 10)
        val agiCheck = fighter1.agi + Random.nextInt(1, 10) >= opponent.agi + Random.nextInt(1, 10)

        val logs = mutableListOf<SparringLog>()
        if (p1Roll >= p2Roll) {
            logs.add(SparringLog("⚔️ ${fighter1.name} hızlı bir adımla kalkan aralığından tahta gladius ile dokundu!", isGladiatorOneAction = true))
        } else {
            logs.add(SparringLog("🛡️ ${opponent.name} darbeyi ustaca savuşturup sert bir karşı hamle yaptı.", isGladiatorOneAction = false))
        }

        if (agiCheck) {
            logs.add(SparringLog("💨 ${fighter1.name} çevik ayak oyunlarıyla rakibin arkasına sarkıp puan aldı!", isGladiatorOneAction = true))
        } else {
            logs.add(SparringLog("💥 ${opponent.name} ağırlığını koyarak ring ortasında baskı kurdu.", isGladiatorOneAction = false))
        }

        val winnerIsG1 = p1Roll + (if (agiCheck) 4 else 0) >= p2Roll
        val winnerName = if (winnerIsG1) fighter1.name else opponent.name
        logs.add(SparringLog("🎯 SON HAMLE: $winnerName kusursuz bir silahsızlandırma manevrasıyla idman düellosunu kazandı!", isGladiatorOneAction = winnerIsG1, isDecisiveBlow = true))

        val statBoostMsg = if (Random.nextBoolean()) "+1 STR & +$xpGain XP" else "+1 AGI & +$xpGain XP"

        return SparringState(
            gladiatorOne = fighter1,
            gladiatorTwo = opponent,
            rounds = logs,
            winnerName = winnerName,
            xpGained = xpGain,
            statBoostSummary = statBoostMsg,
            isComplete = true
        )
    }

    fun generateDailyDilemma(day: Int, cityTier: CityTier): DailyDilemma {
        val dilemmas = listOf(
            DailyDilemma(
                id = "senator_escort_$day",
                title = "Senatör Marcus'un Özel Muhafız Talebi",
                category = "Senato & Siyaset",
                iconEmoji = "🏛️",
                description = "Capua Senatörü Marcus, forumdaki gergin oturum için en güçlü gladyatörünüzü 1 günlüğüne özel muhafız kiralamak istiyor. Cömert altın ve senato nüfuzu vaat ediyor.",
                options = listOf(
                    DilemmaOption(
                        id = "opt_accept",
                        label = "Talebi Kabul Et",
                        effectDescription = "+85 Altın, +15 Prestij (Kadroda +25 Yorgunluk)",
                        goldReward = 85,
                        prestigeReward = 15,
                        fatigueChange = 25,
                        outcomeStory = "Gladyatörünüz Senatörü suikast girişiminden korudu! Senatör Marcus kasaya 85 Altın ve senato desteği bahşetti."
                    ),
                    DilemmaOption(
                        id = "opt_decline",
                        label = "Nazikçe Reddet",
                        effectDescription = "+15 Gladyatör Morali (Dövüşçüler arenaya odaklanır)",
                        moraleChange = 15,
                        outcomeStory = "Dövüşçülerinizi politik entrikalardan uzak tuttunuz. Gladyatörleriniz saygınızı takdir etti (+15 Moral)."
                    )
                )
            ),
            DailyDilemma(
                id = "tavern_brawl_$day",
                title = "Subura Tavernasında Kutlama & Gerilim",
                category = "Halk & Sosyal",
                iconEmoji = "🍷",
                description = "Akşam antrenmanından sonra gladyatörler yerel Subura tavernasına uğradı. Rakip okulun destekçileriyle laf dalaşı başladı.",
                options = listOf(
                    DilemmaOption(
                        id = "opt_wine",
                        label = "Tüm Masaya Mulsum Şarabı Ismarla (-25 🪙)",
                        effectDescription = "Gerilimi tatlıya bağlar, tüm kadroya +25 Moral kazandırır.",
                        goldCost = 25,
                        moraleChange = 25,
                        outcomeStory = "Bal ve baharatlı Mulsum şarabı havayı dağıttı! Şarkılar söylendi, okulun birliği güçlendi."
                    ),
                    DilemmaOption(
                        id = "opt_discipline",
                        label = "Kışlaya Geri Çağır & Disiplin Uygula",
                        effectDescription = "+10 XP Disiplin Bonusu (Sıfır Masraf)",
                        fatigueChange = -10,
                        outcomeStory = "Doctore askerleri derhal topladı. Disiplin korundu, gereksiz yorgunluk önlendi."
                    )
                )
            ),
            DailyDilemma(
                id = "grain_merchant_$day",
                title = "İskenderiye Buğday Tacirinin Teklifi",
                category = "Ticaret & Lojistik",
                iconEmoji = "🌾",
                description = "Ostia limanından gelen bir Mısır tüccarı, yüksek proteinli arpa ve kuru bakla stoğunu toptan indirimle sunuyor.",
                options = listOf(
                    DilemmaOption(
                        id = "opt_buy_grain",
                        label = "Toptan Bakla Stoğu Satın Al (-35 🪙)",
                        effectDescription = "Gelişmiş Sagina besin desteği: Tüm kadro canını %40 yeniler, +15 Moral.",
                        goldCost = 35,
                        moraleChange = 15,
                        fatigueChange = -25,
                        outcomeStory = "Zengin proteinli tahıl stoğu gladyatörlerin kaslarını güçlendirdi ve kondisyonlarını tazeledi!"
                    ),
                    DilemmaOption(
                        id = "opt_pass_grain",
                        label = "Standart Erzakla Devam Et",
                        effectDescription = "Masraf yapılmaz.",
                        outcomeStory = "Standart kiler erzakı kullanılmaya devam edildi."
                    )
                )
            ),
            DailyDilemma(
                id = "veteran_legionary_$day",
                title = "Gazi Lejyoner Ziyareti",
                category = "Askeri Tecrübe",
                iconEmoji = "🛡️",
                description = "Germanya cephesinden emekli gazi bir centurion, okulunuza uğrayıp gladyatörlerinize lezyonel kalkan formasyonunu öğretmeyi teklif ediyor.",
                options = listOf(
                    DilemmaOption(
                        id = "opt_train_legion",
                        label = "Taktik Dersi Aldır (-30 🪙)",
                        effectDescription = "Tüm gladyatörlere +25 XP ve +1 Savunma/STR!",
                        goldCost = 30,
                        prestigeReward = 10,
                        outcomeStory = "Gazi centurion kalkan açısı ve savuşturma taktiklerini gösterdi. Kadro dövüş bilincini artırdı!"
                    ),
                    DilemmaOption(
                        id = "opt_decline_legion",
                        label = "Gerek Yok",
                        effectDescription = "Masrafsız geçilir.",
                        outcomeStory = "Geleneksel bireysel gladyatör antrenmanına sadık kalındı."
                    )
                )
            ),
            DailyDilemma(
                id = "pompeii_festival_$day",
                title = "Pompeii Bahar Şenliği Gösterisi",
                category = "Şenlik & Gösteri",
                iconEmoji = "🎭",
                description = "Yerel aedile, yaklaşan festivalde halkı eğlendirmek için zararsız bir tahta kılıç akrobasi gösterisi düzenlemenizi istiyor.",
                options = listOf(
                    DilemmaOption(
                        id = "opt_show",
                        label = "Gösteri Ekibi Gönder",
                        effectDescription = "+50 Altın, +25 Prestij (Seyirci ilgisi zirve yapar)",
                        goldReward = 50,
                        prestigeReward = 25,
                        fatigueChange = 15,
                        outcomeStory = "Halk gladyatörlerinizin hünerlerine hayran kaldı! Şehir meydanında adınız yankılandı."
                    ),
                    DilemmaOption(
                        id = "opt_rest",
                        label = "Dinlenmeye Ayır",
                        effectDescription = "Yorgunluk düşer (-20 Yorgunluk)",
                        fatigueChange = -20,
                        outcomeStory = "Dövüşçüler tam dinlenmeyle bir sonraki resmi arena turnuvasına hazırlandı."
                    )
                )
            )
        )
        return dilemmas[(day + cityTier.tierNumber) % dilemmas.size]
    }
}
