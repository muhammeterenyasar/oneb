package com.example.simulation

import com.example.model.*
import kotlin.random.Random

/**
 * Core engine governing the calendar-driven, persistent gladiatorial world simulation.
 * Enforces hard rules:
 * - Official fights occur ONLY on scheduled calendar dates.
 * - Death is permanent: dead gladiators are removed from all active pools, matchmaking, and rankings.
 * - Dead champions leave a VACANT title until a contender tournament/match resolves.
 * - Illegal Underground Bouts provide high-risk/high-reward fights outside the calendar.
 * - Non-player gladiators fight, win, lose, sustain injuries, and die on calendar days.
 */
object ArenaProgressionEngine {

    /**
     * Hard Validation Rules enforcing simulation integrity.
     */
    object Validation {
        fun canGladiatorBeScheduled(fighter: PersistentFighter): Pair<Boolean, String?> {
            if (!fighter.isAlive) {
                return false to "Dövüşçü vefat etmiştir (Status: DEAD). Müsabakaya dahil edilemez."
            }
            if (fighter.isRetired) {
                return false to "Dövüşçü emekliye ayrılmıştır (Status: RETIRED)."
            }
            if (fighter.currentHealth < 30) {
                return false to "Dövüşçünün sağlığı çok düşüktür (%${fighter.currentHealth})."
            }
            return true to null
        }

        fun canStartOfficialCombat(
            currentDay: Int,
            scheduledBoutDay: Int,
            playerGladiator: Gladiator
        ): Pair<Boolean, String> {
            if (playerGladiator.isDead) {
                return false to "Gladyatörünüz hayatta değildir (Dead). Arenaya çıkamaz."
            }
            if (playerGladiator.injuries.any { it.severity == "Severe" }) {
                return false to "Gladyatörünüzün ağır bir sakatlığı var! Hekim Lucius izin vermiyor."
            }
            if (currentDay < scheduledBoutDay) {
                val remaining = scheduledBoutDay - currentDay
                return false to "Bugün resmi dövüş günü değildir! Resmi müsabakanız Gün $scheduledBoutDay'de ($remaining gün sonra). Kışlada hazırlanın veya günü ilerletin."
            }
            if (currentDay > scheduledBoutDay) {
                return false to "Bu müsabakanın tarihi geçmiştir."
            }
            return true to "Resmi Dövüş Günü Başladı!"
        }

        fun checkOverqualification(gladiatorPrestige: Int, venue: ArenaVenueId): String? {
            if (gladiatorPrestige > 3000 && venue == ArenaVenueId.LOCAL_PIT) {
                return "Capua yetkilileri ve kalabalık, gladyatörünüzü yerel çukurlar için fazla nitelikli (overqualified) görüyor. Prestijli arenalara yönelmelisiniz."
            }
            return null
        }
    }

    /**
     * Generates a persistent rolling calendar for the circuit.
     * Follows the exact historical cadence:
     * DAY 12: Marcus vs Dama
     * DAY 14: Felix vs Gaius
     * DAY 17: YOUR LUDUS (Player vs Cassian or Challenger)
     * DAY 20: Championship Qualifier
     * DAY 23: Festival Games
     * DAY 27: YOUR LUDUS
     * etc.
     */
    fun createInitialCalendar(
        currentDay: Int,
        fighters: List<PersistentFighter>
    ): MutableList<ArenaCalendarBout> {
        val bouts = mutableListOf<ArenaCalendarBout>()

        // Pre-configured historical narrative calendar
        val marcus = fighters.find { it.id == "fighter_pompeii_champ" } ?: fighters[0]
        val dama = fighters.find { it.id == "fighter_dama" } ?: fighters[1]
        val felix = fighters.find { it.id == "fighter_felix" } ?: fighters[2]
        val gaius = fighters.find { it.id == "fighter_capua_champ" } ?: fighters[3]
        val cassian = fighters.find { it.id == "fighter_cassian" } ?: fighters[4]
        val drusus = fighters.find { it.id == "fighter_drusus" } ?: fighters[5]
        val aulus = fighters.find { it.id == "fighter_aulus" } ?: fighters[0]

        // Day 12: Marcus vs Dama
        bouts.add(
            ArenaCalendarBout(
                id = "bout_day_12",
                day = 12,
                venueId = ArenaVenueId.CAPUA,
                fighter1Id = marcus.id,
                fighter1Name = marcus.fullDisplayName,
                fighter1Ludus = marcus.ludusAffiliation,
                fighter2Id = dama.id,
                fighter2Name = dama.fullDisplayName,
                fighter2Ludus = dama.ludusAffiliation,
                matchType = ArenaMatchType.STANDARD_DUEL,
                isPlayerMatch = false
            )
        )

        // Day 14: Felix vs Gaius
        bouts.add(
            ArenaCalendarBout(
                id = "bout_day_14",
                day = 14,
                venueId = ArenaVenueId.CAPUA,
                fighter1Id = felix.id,
                fighter1Name = felix.fullDisplayName,
                fighter1Ludus = felix.ludusAffiliation,
                fighter2Id = gaius.id,
                fighter2Name = gaius.fullDisplayName,
                fighter2Ludus = gaius.ludusAffiliation,
                matchType = ArenaMatchType.EXHIBITION,
                isPlayerMatch = false
            )
        )

        // Day 17: YOUR LUDUS vs Cassian
        bouts.add(
            ArenaCalendarBout(
                id = "bout_day_17",
                day = 17,
                venueId = ArenaVenueId.CAPUA,
                fighter1Id = cassian.id,
                fighter1Name = cassian.fullDisplayName,
                fighter1Ludus = cassian.ludusAffiliation,
                fighter2Id = "player",
                fighter2Name = "Titus (Your Ludus)",
                fighter2Ludus = "Ludus Valerius",
                matchType = ArenaMatchType.RIVALRY_MATCH,
                isPlayerMatch = true
            )
        )

        // Day 20: Championship Qualifier (Drusus vs Aulus)
        bouts.add(
            ArenaCalendarBout(
                id = "bout_day_20",
                day = 20,
                venueId = ArenaVenueId.CAPUA,
                fighter1Id = drusus.id,
                fighter1Name = drusus.fullDisplayName,
                fighter1Ludus = drusus.ludusAffiliation,
                fighter2Id = aulus.id,
                fighter2Name = aulus.fullDisplayName,
                fighter2Ludus = aulus.ludusAffiliation,
                matchType = ArenaMatchType.CHAMPIONSHIP_MATCH,
                isPlayerMatch = false
            )
        )

        // Day 23: Festival Games
        bouts.add(
            ArenaCalendarBout(
                id = "bout_day_23",
                day = 23,
                venueId = ArenaVenueId.POMPEII,
                fighter1Id = marcus.id,
                fighter1Name = marcus.fullDisplayName,
                fighter1Ludus = marcus.ludusAffiliation,
                fighter2Id = felix.id,
                fighter2Name = felix.fullDisplayName,
                fighter2Ludus = felix.ludusAffiliation,
                matchType = ArenaMatchType.SPECIAL_FESTIVAL,
                isPlayerMatch = false
            )
        )

        // Day 27: YOUR LUDUS official bout
        val opponent27 = fighters.filter { it.isAlive && it.id != "fighter_cassian" && it.currentArena == ArenaVenueId.CAPUA }.randomOrNull() ?: drusus
        bouts.add(
            ArenaCalendarBout(
                id = "bout_day_27",
                day = 27,
                venueId = ArenaVenueId.CAPUA,
                fighter1Id = opponent27.id,
                fighter1Name = opponent27.fullDisplayName,
                fighter1Ludus = opponent27.ludusAffiliation,
                fighter2Id = "player",
                fighter2Name = "Titus (Your Ludus)",
                fighter2Ludus = "Ludus Valerius",
                matchType = ArenaMatchType.STANDARD_DUEL,
                isPlayerMatch = true
            )
        )

        // Day 31: Title Contender Bout
        bouts.add(
            ArenaCalendarBout(
                id = "bout_day_31",
                day = 31,
                venueId = ArenaVenueId.CAPUA,
                fighter1Id = gaius.id,
                fighter1Name = gaius.fullDisplayName,
                fighter1Ludus = gaius.ludusAffiliation,
                fighter2Id = drusus.id,
                fighter2Name = drusus.fullDisplayName,
                fighter2Ludus = drusus.ludusAffiliation,
                matchType = ArenaMatchType.CHAMPIONSHIP_MATCH,
                isPlayerMatch = false
            )
        )

        // Day 35: YOUR LUDUS Championship Contender
        bouts.add(
            ArenaCalendarBout(
                id = "bout_day_35",
                day = 35,
                venueId = ArenaVenueId.CAPUA,
                fighter1Id = gaius.id,
                fighter1Name = gaius.fullDisplayName,
                fighter1Ludus = gaius.ludusAffiliation,
                fighter2Id = "player",
                fighter2Name = "Titus (Your Ludus)",
                fighter2Ludus = "Ludus Valerius",
                matchType = ArenaMatchType.CHAMPIONSHIP_MATCH,
                isPlayerMatch = true
            )
        )

        return bouts
    }

    /**
     * Simulates background calendar matches on day advancement.
     * NPC gladiators fight with real stakes: record updates, injuries, permanent death!
     * When a champion dies: title becomes VACANT and a championship qualification match is scheduled.
     */
    fun simulateCalendarDay(
        currentDay: Int,
        calendar: MutableList<ArenaCalendarBout>,
        fighters: MutableList<PersistentFighter>,
        memorials: MutableList<FallenGladiatorMemorial>
    ): List<String> {
        val newsReports = mutableListOf<String>()

        val todaysBouts = calendar.filter { it.day == currentDay && !it.isPlayerMatch && !it.isCompleted }

        for (bout in todaysBouts) {
            bout.isCompleted = true
            val f1 = fighters.find { it.id == bout.fighter1Id }
            val f2 = fighters.find { it.id == bout.fighter2Id }

            if (f1 == null || f2 == null) {
                bout.resultSummary = "Müsabaka iptal edildi (dövüşçü bulunamadı)."
                continue
            }

            // Hard Rule: Dead fighters cannot fight
            if (!f1.isAlive || !f2.isAlive) {
                val deadOne = if (!f1.isAlive) f1 else f2
                bout.resultSummary = "İPTAL: ${deadOne.fullDisplayName} daha önce hayatını kaybettiği için müsabaka iptal edildi."
                newsReports.add("TAKVİM İPTALİ: ${deadOne.fullDisplayName} vefat ettiği için ${bout.venueId.venueName}'ndaki müsabaka programdan çıkarıldı.")
                continue
            }

            // Resolve combat tactically
            val f1Score = f1.strength * 1.2f + f1.swordsmanship * 1.5f + f1.speed * 1.0f + Random.nextInt(5, 25)
            val f2Score = f2.strength * 1.2f + f2.swordsmanship * 1.5f + f2.speed * 1.0f + Random.nextInt(5, 25)
            val f1Won = f1Score >= f2Score

            val winner = if (f1Won) f1 else f2
            val loser = if (f1Won) f2 else f1

            winner.wins++
            winner.currentWinStreak++
            winner.prestige += 160
            winner.recentForm.add(0, "W")
            if (winner.recentForm.size > 5) winner.recentForm.removeAt(5)

            loser.losses++
            loser.currentWinStreak = 0
            loser.prestige = maxOf(100, loser.prestige - 80)
            loser.recentForm.add(0, "L")
            if (loser.recentForm.size > 5) loser.recentForm.removeAt(5)

            // Fatality roll: 12% mortal chance in official arena bouts
            val isFatal = Random.nextFloat() < 0.12f

            if (isFatal) {
                winner.kills++
                loser.isAlive = false
                loser.deathDay = currentDay
                loser.killedBy = winner.fullDisplayName
                loser.causeOfDeath = "Kritik kılıç darbesi & Pollice Verso idamı"
                loser.deathArena = bout.venueId.venueName

                // Record permanent memorial
                val memorial = FallenGladiatorMemorial(
                    id = "fallen_${loser.id}_${System.currentTimeMillis()}",
                    name = loser.name,
                    nickname = loser.nickname,
                    gladiatorClass = loser.gladiatorClass,
                    origin = loser.origin,
                    ludusAffiliation = loser.ludusAffiliation,
                    recordSummary = "${loser.wins}G - ${loser.losses}M (${loser.kills} İnfaz)",
                    kills = loser.kills,
                    diedOnDay = currentDay,
                    arenaName = bout.venueId.venueName,
                    killedBy = winner.fullDisplayName,
                    causeOfDeath = loser.causeOfDeath ?: "Ölümcül yara",
                    wasChampion = loser.isChampion
                )
                memorials.add(0, memorial)

                // If deceased was champion: Title is VACANT!
                val wasChamp = loser.isChampion
                if (wasChamp) {
                    loser.isChampion = false
                    // Schedule a vacant title contender tournament 5 days later
                    val vacancyDay = currentDay + 5
                    val remainingTopContender = fighters.filter { it.isAlive && it.currentArena == bout.venueId && it.id != winner.id }.maxByOrNull { it.prestige }
                    if (remainingTopContender != null) {
                        calendar.add(
                            ArenaCalendarBout(
                                id = "bout_vacancy_$vacancyDay",
                                day = vacancyDay,
                                venueId = bout.venueId,
                                fighter1Id = winner.id,
                                fighter1Name = winner.fullDisplayName,
                                fighter1Ludus = winner.ludusAffiliation,
                                fighter2Id = remainingTopContender.id,
                                fighter2Name = remainingTopContender.fullDisplayName,
                                fighter2Ludus = remainingTopContender.ludusAffiliation,
                                matchType = ArenaMatchType.CHAMPIONSHIP_MATCH,
                                isPlayerMatch = false
                            )
                        )
                    }
                    newsReports.add("💀 ŞAMPİYONUN ÖLÜMÜ: ${loser.fullDisplayName}, ${bout.venueId.venueName}'nda ${winner.fullDisplayName} tarafından katledildi! #1 UNVAN BOŞTA (VACANT) İLAN EDİLDİ!")
                } else {
                    newsReports.add("💀 ARENA ÖLÜMÜ: ${loser.fullDisplayName}, ${bout.venueId.venueName}'nda ${winner.fullDisplayName} tarafından kılıçtan geçirildi.")
                }

                bout.winnerName = winner.fullDisplayName
                bout.resultSummary = "${winner.fullDisplayName}, rakibi ${loser.fullDisplayName}'yi öldürerek zafer kazandı."
            } else {
                bout.winnerName = winner.fullDisplayName
                bout.resultSummary = "${winner.fullDisplayName}, ${loser.fullDisplayName}'yi puanla mağlup etti (Missio bağışlandı)."
                newsReports.add("ARENA SONUCU: ${winner.fullDisplayName} zafer kazandı. Rakip: ${loser.fullDisplayName} (${bout.venueId.venueName})")
            }
        }

        return newsReports
    }

    /**
     * Generates deterministic, persistent illegal underground fight offerings for the current date.
     * Hard Rules:
     * - Rotating normal fighters change deterministically each day based on worldSeed + currentDay.
     * - Fighters on cooldown (fought within 3 days) or injured/dead cannot be selected.
     * - Persistent Bosses (The Wolf, Black Sand, The Syrian, Gravedigger, Red Knife) follow storyline availability.
     * - Bosses persist across days until defeated or killed. Once defeated, they NEVER reappear.
     * - Re-opening the screen on the same date produces the identical pool.
     */
    fun generateUndergroundFights(
        currentDay: Int,
        fighters: List<Gladiator>,
        worldSeed: Long = 42L,
        bossStates: Map<String, BossState> = emptyMap()
    ): List<UndergroundFight> {
        val fights = mutableListOf<UndergroundFight>()

        // 1. Check Persistent Boss Availability
        // Boss 1: Secundus ("The Wolf") - Capua Subura, Days 10+
        val wolf = fighters.find { it.id == "boss_wolf_secundus" || (it.name == "Secundus" && it.nickname == "The Wolf") }
        val wolfState = bossStates[wolf?.id ?: "boss_wolf_secundus"] ?: wolf?.undergroundBossState ?: BossState.ACTIVE
        val isWolfAvailable = wolf != null && wolf.isAlive && currentDay >= 10 &&
                wolfState != BossState.DEFEATED && wolfState != BossState.DEAD && wolfState != BossState.STORY_RESOLVED

        if (isWolfAvailable) {
            fights.add(
                UndergroundFight(
                    id = "underground_boss_wolf_${currentDay}",
                    title = "🐺 PATRON MEYDAN OKUMASI: 'The Wolf'",
                    venueName = "Subura Eski Şarap Mahzeni",
                    organizerName = "Kaçakçı Lucius",
                    organizerRole = "Subura Suç Şebekesi Elebaşı",
                    opponentFighter = wolf!!,
                    entryFee = 500,
                    purseReward = 3200,
                    riskLevel = "ÖLÜMCÜL (LETHAL)",
                    discoveryRiskPercent = 30,
                    atmosphere = "Meşale dumanı, kaba bahisçiler ve keskin kılıçlar. The Wolf avını bekliyor!",
                    dayOffered = currentDay,
                    warningNote = "The Wolf yenilmez bir yeraltı celladıdır! Onu alt ederseniz hikâyesi sonlanır; kaybederseniz dövüşçünüz için af yoktur!",
                    isCompleted = false,
                    isBossFight = true,
                    bossState = BossState.AVAILABLE,
                    storylineSnippet = "The Wolf Subura'nın dar sokaklarında nam salmış bir katildir. Yenilirse efsanesi ebediyen biter."
                )
            )
        }

        // Boss 2: Aetius ("Black Sand") - Capua Quarries, Days 18+
        val blackSand = fighters.find { it.id == "boss_black_sand_aetius" || it.nickname == "Black Sand" }
        val blackSandState = bossStates[blackSand?.id ?: "boss_black_sand_aetius"] ?: blackSand?.undergroundBossState ?: BossState.ACTIVE
        val isBlackSandAvailable = blackSand != null && blackSand.isAlive && currentDay >= 18 &&
                blackSandState != BossState.DEFEATED && blackSandState != BossState.DEAD && blackSandState != BossState.STORY_RESOLVED

        if (isBlackSandAvailable && fights.size < 2) {
            fights.add(
                UndergroundFight(
                    id = "underground_boss_black_sand_${currentDay}",
                    title = "⚒ PATRON DÖVÜŞÜ: 'Black Sand'",
                    venueName = "Capua Taş Ocakları Tabanı",
                    organizerName = "Yozlaşmış Yüzbaşı Varo",
                    organizerRole = "Gece Devriyesi Komutanı",
                    opponentFighter = blackSand!!,
                    entryFee = 600,
                    purseReward = 3800,
                    riskLevel = "ÖLÜMCÜL (LETHAL)",
                    discoveryRiskPercent = 25,
                    atmosphere = "Tozlu kayalık zemin ve meşaleler. Black Sand rakiplerini taş duvarlara vurarak parçalar.",
                    dayOffered = currentDay,
                    warningNote = "Black Sand bir kule gibi sağlamdır. Zırhını aşmak imkânsıza yakındır!",
                    isCompleted = false,
                    isBossFight = true,
                    bossState = BossState.AVAILABLE,
                    storylineSnippet = "Taş ocaklarında yenilgi yüzü görmemiş Galli bir dev. Onu deviren tüm Capua'da yankı uyandırır."
                )
            )
        }

        // 2. Query valid rotating normal fighters
        // Must be alive, not a champion, not a boss, health >= 50, no severe injuries, cooldown >= 3 days
        val bossIds = setOf("boss_wolf_secundus", "boss_black_sand_aetius", "boss_syrian_tariq", "boss_gravedigger_maurus", "boss_red_knife_servius")
        val candidateFighters = fighters.filter { fighter ->
            fighter.isAlive &&
            fighter.status != GladiatorStatus.DEAD &&
            !fighter.isChampion &&
            fighter.id !in bossIds &&
            fighter.nickname !in listOf("The Wolf", "Black Sand", "The Syrian", "Gravedigger", "Red Knife") &&
            fighter.condition.health >= 50 &&
            fighter.injuries.none { it.severity == "Severe" } &&
            (currentDay - fighter.lastFightDay) >= 3
        }

        // Deterministic daily RNG seeded by currentDay and worldSeed
        val dailyRng = kotlin.random.Random(worldSeed + currentDay * 10007L)
        val shuffledCandidates = candidateFighters.shuffled(dailyRng)

        val neededNormalFights = (3 - fights.size).coerceAtLeast(1)
        val selectedFighters = shuffledCandidates.take(neededNormalFights)

        val venuesAndOrganizers = listOf(
            Triple("Subura Mahzen Dövüşü", "Subura Eski Şarap Mahzeni", Pair("Kaçakçı Lucius", "Karaborsa Elebaşı")),
            Triple("Taş Ocağı Kan Bahsi", "Capua Taş Ocakları Tabanı", Pair("Nöbetçi Yüzbaşı Varo", "Gece Devriyesi Komutanı")),
            Triple("Liman Hangarı İnfazı", "Neapolis Gizli Rıhtımı", Pair("Korsan Kaptanı Barba", "Kaçakçı Elebaşı"))
        )

        selectedFighters.forEachIndexed { index, fighter ->
            val venueInfo = venuesAndOrganizers[(index + currentDay) % venuesAndOrganizers.size]
            val fee = 250 + (fighter.physicalStats.strength * 10)
            val purse = (fee * 4.5f).toInt()

            fights.add(
                UndergroundFight(
                    id = "underground_${currentDay}_${fighter.id}",
                    title = venueInfo.first,
                    venueName = venueInfo.second,
                    organizerName = venueInfo.third.first,
                    organizerRole = venueInfo.third.second,
                    opponentFighter = fighter,
                    entryFee = fee,
                    purseReward = purse,
                    riskLevel = if (fighter.physicalStats.strength >= 15) "ÖLÜMCÜL (LETHAL)" else "YÜKSEK RİSK",
                    discoveryRiskPercent = 20 + (index * 4),
                    atmosphere = "Karanlık sokakların derinliklerinde meşale ışığında yapılan kanlı bir bahis dövüşü.",
                    dayOffered = currentDay,
                    warningNote = "Resmi kurallar geçersizdir. Dövüşçünüz yaralanabilir veya ölümcül darbeler alabilir.",
                    isCompleted = false,
                    isBossFight = false,
                    bossState = null,
                    storylineSnippet = null
                )
            )
        }

        return fights
    }

    /**
     * Schedules a player challenge on the official calendar on an upcoming date.
     * Prevents instant on-demand arena bouts and respects calendar pacing.
     */
    fun schedulePlayerChallenge(
        currentDay: Int,
        calendar: MutableList<ArenaCalendarBout>,
        venue: ArenaVenueId,
        opponent: Gladiator,
        matchType: ArenaMatchType = ArenaMatchType.STANDARD_DUEL
    ): Pair<Boolean, String> {
        if (!opponent.isAlive) {
            return false to "Dövüşçü vefat etmiştir (Status: DEAD). Müsabaka ayarlanamaz."
        }
        if (opponent.isRetired) {
            return false to "Dövüşçü emekliye ayrılmıştır (Status: RETIRED)."
        }

        // Find next available date: at least 2 days out, and player doesn't already fight that day
        var targetDay = currentDay + 2
        while (calendar.any { it.day == targetDay && (it.isPlayerMatch || it.fighter1Id == opponent.id || it.fighter2Id == opponent.id) }) {
            targetDay++
        }

        val newBout = ArenaCalendarBout(
            id = "bout_challenge_${targetDay}_${opponent.id}",
            day = targetDay,
            venueId = venue,
            fighter1Id = opponent.id,
            fighter1Name = opponent.fullDisplayName,
            fighter1Ludus = opponent.ludusAffiliation,
            fighter2Id = "player",
            fighter2Name = "Titus (Your Ludus)",
            fighter2Ludus = "Ludus Valerius",
            matchType = matchType,
            isPlayerMatch = true,
            isCompleted = false
        )

        calendar.add(newBout)
        calendar.sortBy { it.day }

        val remaining = targetDay - currentDay
        return true to "Meydan okuma kabul edildi! Müsabakanız Gün $targetDay tarihinde ($remaining gün sonra) ${venue.venueName}'nda gerçekleşecek."
    }

    /**
     * Computes the living leaderboard rankings for an arena venue.
     * HARD RULE: Dead gladiators never appear in active rankings.
     * If reigning champion is dead: rank #1 displays CHAMPIONSHIP VACANT.
     */
    fun computeLivingRankings(
        venue: ArenaVenueId,
        allFighters: List<PersistentFighter>,
        playerGladiator: Gladiator?
    ): List<ArenaRankingEntry> {
        val livingVenueFighters = allFighters.filter { it.isAlive && it.currentArena == venue }
            .sortedByDescending { it.prestige + (it.wins * 100) }

        val hasLivingChampion = livingVenueFighters.any { it.isChampion }
        val rankingEntries = mutableListOf<ArenaRankingEntry>()

        var currentRank = 1

        if (!hasLivingChampion) {
            // Vacant championship slot!
            rankingEntries.add(
                ArenaRankingEntry(
                    rank = currentRank++,
                    fighterId = null,
                    name = "UNVAN BOŞTA (CHAMPIONSHIP VACANT)",
                    nickname = "Eleme Maçı Bekleniyor",
                    ludus = "Capua Senato Heyeti",
                    gladiatorClass = null,
                    tier = null,
                    recordSummary = "Unvan Sahipsiz",
                    prestige = 0,
                    recentForm = emptyList(),
                    isPlayer = false,
                    isChampion = true,
                    isVacant = true
                )
            )
        }

        for (fighter in livingVenueFighters) {
            rankingEntries.add(
                ArenaRankingEntry(
                    rank = currentRank++,
                    fighterId = fighter.id,
                    name = fighter.name,
                    nickname = fighter.nickname,
                    ludus = fighter.ludusAffiliation,
                    gladiatorClass = fighter.gladiatorClass,
                    tier = fighter.tier,
                    recordSummary = fighter.recordSummary,
                    prestige = fighter.prestige,
                    recentForm = fighter.recentForm,
                    isPlayer = false,
                    isChampion = fighter.isChampion,
                    isVacant = false
                )
            )
        }

        // Insert player gladiator into appropriate ranking bracket if alive
        if (playerGladiator != null && playerGladiator.isAlive) {
            val playerWins = playerGladiator.careerStats.wins
            val playerPrestige = playerWins * 200 + 500
            val playerRecord = "${playerWins}G - ${playerGladiator.careerStats.losses}M"

            // Estimate player rank around #2 - #4 based on wins
            val insertIdx = if (playerWins >= 10) 1 else if (playerWins >= 5) 2 else 3
            val safeIdx = insertIdx.coerceAtMost(rankingEntries.size)

            val playerEntry = ArenaRankingEntry(
                rank = safeIdx + 1,
                fighterId = playerGladiator.id,
                name = playerGladiator.name,
                nickname = "Your Champion",
                ludus = "Ludus Valerius",
                gladiatorClass = playerGladiator.gladiatorClass,
                tier = OpponentTier.ELITE,
                recordSummary = playerRecord,
                prestige = playerPrestige,
                recentForm = listOf("W", "W", "W"),
                isPlayer = true,
                isChampion = false,
                isVacant = false
            )
            rankingEntries.add(safeIdx, playerEntry)
        }

        // Re-index ranks 1..N
        return rankingEntries.mapIndexed { index, entry ->
            entry.copy(rank = index + 1)
        }
    }
}
