package com.example.data

import com.example.data.engine.*
import com.example.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Facade Repository for Ludus Magnus.
 * Coordinates database persistence with specialized game engines:
 * - [TournamentScheduler] for matches and calendar
 * - [BattleEngine] for combat resolution
 * - [EconomyEngine] for finances and upkeep
 * - [EventEngine] for sparring, tesserae, and dilemmas
 * - [DowntimeEventEngine] for between-cycle festivals and narrative choices
 * - [DailyCycleEngine] for day/night phase progression
 * - [InfirmaryEngine] for medical treatments
 * - [ArmoryEngine] for blacksmith gear
 * - [CampaignEngine] for Imperial boss progression
 */
class LudusRepository(
    private val gladiatorDao: GladiatorDao,
    private val ludusStateDao: LudusStateDao,
    private val matchLogDao: MatchLogDao,
    private val teacherDao: TeacherDao
) {
    val gladiatorsFlow: Flow<List<Gladiator>> = gladiatorDao.getAllGladiators().map { list ->
        list.map { it.toDomain() }
    }

    val teachersFlow: Flow<List<Teacher>> = teacherDao.getAllTeachers().map { list ->
        list.map { it.toDomain() }
    }

    val ludusStateFlow: Flow<LudusState> = combine(
        ludusStateDao.getLudusState(),
        teacherDao.getAllTeachers()
    ) { stateEntity, teacherEntities ->
        val teachers = teacherEntities.map { it.toDomain() }
        val base = stateEntity?.toDomain(teachers) ?: LudusState(activeTeachers = teachers)
        val event = generateScheduledEventForArena(base.cityTier, base.day, base.nextScheduledMatchDay)
        val calendar = generateUpcomingScheduledEvents(base.cityTier, base.day, base.nextScheduledMatchDay)
        base.copy(
            activeTeachers = teachers,
            currentScheduledEvent = event,
            upcomingCalendarEvents = calendar
        )
    }

    val recentLogsFlow: Flow<List<MatchLogEntity>> = matchLogDao.getRecentMatchLogs()

    // --- INITIALIZATION ---
    suspend fun initializeIfEmpty() {
        val existingGladiators = gladiatorDao.getAllGladiators().firstOrNull()
        if (existingGladiators.isNullOrEmpty()) {
            val starterGladiators = listOf(
                Gladiator(
                    name = "Marcus",
                    nickname = "Yenilmez Trak",
                    origin = "Trakya",
                    gladiatorClass = GladiatorClass.THRAEX,
                    contractType = GladiatorContractType.SLAVE,
                    dailySalary = 0,
                    priceValue = 180,
                    str = 14, agi = 15, sta = 12, mor = 75,
                    age = 24, fatigue = 10,
                    currentHp = 110, maxHp = 110,
                    wins = 2, losses = 0,
                    trainingFocus = TrainingType.AGILITY,
                    personalityTrait = GladiatorTrait.CROWD_FAVORITE
                ),
                Gladiator(
                    name = "Valerius",
                    nickname = "Demir Duvar",
                    origin = "Roma",
                    gladiatorClass = GladiatorClass.MURMILLO,
                    contractType = GladiatorContractType.CONTRACTED,
                    dailySalary = 10,
                    priceValue = 240,
                    str = 16, agi = 9, sta = 18, mor = 85,
                    age = 29, fatigue = 20,
                    currentHp = 135, maxHp = 135,
                    wins = 4, losses = 1,
                    trainingFocus = TrainingType.STRENGTH,
                    personalityTrait = GladiatorTrait.IRON_WILLED
                ),
                Gladiator(
                    name = "Crixus",
                    nickname = "Ağ Ustası",
                    origin = "Galyalı",
                    gladiatorClass = GladiatorClass.RETIARIUS,
                    contractType = GladiatorContractType.SLAVE,
                    dailySalary = 0,
                    priceValue = 160,
                    str = 11, agi = 17, sta = 10, mor = 65,
                    age = 22, fatigue = 0,
                    currentHp = 95, maxHp = 95,
                    wins = 1, losses = 1,
                    trainingFocus = TrainingType.SPARRING,
                    personalityTrait = GladiatorTrait.BLOODTHIRSTY
                )
            )
            gladiatorDao.insertAllGladiators(starterGladiators.map { GladiatorEntity.fromDomain(it) })
        }

        val existingTeachers = teacherDao.getAllTeachersDirect()
        if (existingTeachers.isEmpty()) {
            val defaultTeacher = Teacher(
                id = 0,
                name = "Doctore Lucius",
                title = "Kıdemli Lanista Başeğitmeni",
                specialty = TeacherSpecialty.MURMILLO_MASTER,
                originGladiatorName = null,
                level = 1,
                statBonusMultiplier = 1.35f,
                dailySalary = 8,
                hireCost = 100,
                description = "Yıllarca Capua arenalarında dövüşmüş kıdemli eğitmen. Güç ve savunma tekniklerini aktarır.",
                isPromotedFromRoster = false
            )
            teacherDao.insertTeacher(TeacherEntity.fromDomain(defaultTeacher))
        }

        val existingState = ludusStateDao.getLudusStateDirect()
        if (existingState == null) {
            val initialState = LudusState(
                day = 1,
                phase = DayPhase.MORNING,
                gold = 450,
                prestige = 30,
                maxGladiatorSlots = 4,
                physicianLevel = 1,
                guardsHired = 1,
                maxGuards = 4,
                dietPlan = DietPlan.BARLEY_PORRIDGE,
                nextScheduledMatchDay = 4,
                activeDebt = 0,
                debtDueDaysLeft = 0,
                threatStage = ThreatStage.NONE,
                cityTier = CityTier.TOWN_RURAL,
                currentDilemma = generateDailyDilemma(1, CityTier.TOWN_RURAL)
            )
            ludusStateDao.insertOrUpdateState(LudusStateEntity.fromDomain(initialState))
        }
    }

    // --- PERSISTENCE HELPERS ---
    suspend fun saveGladiator(gladiator: Gladiator) {
        if (gladiator.id == 0L) {
            gladiatorDao.insertGladiator(GladiatorEntity.fromDomain(gladiator))
        } else {
            gladiatorDao.updateGladiator(GladiatorEntity.fromDomain(gladiator))
        }
    }

    suspend fun removeGladiator(gladiatorId: Long) {
        gladiatorDao.deleteGladiatorById(gladiatorId)
    }

    suspend fun saveLudusState(state: LudusState) {
        ludusStateDao.insertOrUpdateState(LudusStateEntity.fromDomain(state))
    }

    // --- TRAINING & DRILL SYSTEM ---
    fun processGladiatorDrill(
        g: Gladiator,
        drill: SpecificDrill,
        diet: DietPlan,
        activeTeachers: List<Teacher>,
        unlockedPerkIds: List<String> = emptyList()
    ): Pair<Gladiator, String> {
        if (g.isInjured && drill != SpecificDrill.THERMAE_MASSAGE) {
            return Pair(g, "🩹 ${g.name} sakat olduğu için idmana çıkamadı (Revirde).")
        }
        if (g.fatigue >= 100 && drill != SpecificDrill.THERMAE_MASSAGE) {
            return Pair(g, "💤 ${g.name} bitkin düşmüş durumda (%100 Yorgunluk). Dinlenmeli veya hamama gitmeli.")
        }
        if (g.hasTrainedToday && drill != SpecificDrill.THERMAE_MASSAGE) {
            return Pair(g, "✅ ${g.name} bugünkü idmanını zaten tamamladı.")
        }

        val hasIronDiscipline = unlockedPerkIds.contains(LanistaPerk.IRON_DISCIPLINE.id)
        val isExtremeFatigue = g.fatigue >= 80 && drill != SpecificDrill.THERMAE_MASSAGE
        val sufferedFatigueInjury = !hasIronDiscipline && isExtremeFatigue && Random.nextFloat() < 0.25f

        val hasMurmilloCoach = activeTeachers.any { it.specialty == TeacherSpecialty.MURMILLO_MASTER }
        val hasRetiariusCoach = activeTeachers.any { it.specialty == TeacherSpecialty.RETIARIUS_MASTER }
        val hasConditionCoach = activeTeachers.any { it.specialty == TeacherSpecialty.PHYSICAL_CONDITIONING }
        val hasDisciplineCoach = activeTeachers.any { it.specialty == TeacherSpecialty.GLADIATOR_DISCIPLINE }
        val disciplineBonus = if (hasDisciplineCoach) 1.25f else 1.0f
        val perkXpMultiplier = if (hasIronDiscipline) 1.25f else 1.0f
        val traitXpMultiplier = if (g.personalityTrait == GladiatorTrait.SCHOLAR_WARRIOR) 1.20f else 1.0f

        val fatiguePenalty = if (g.fatigue >= 80) 0.6f else if (g.fatigue >= 50) 0.85f else 1.0f

        val strBonusMult = (if (hasMurmilloCoach) 1.30f else 1.0f) * diet.efficiencyMultiplier * disciplineBonus * fatiguePenalty * perkXpMultiplier * traitXpMultiplier
        val agiBonusMult = (if (hasRetiariusCoach) 1.30f else 1.0f) * diet.efficiencyMultiplier * disciplineBonus * fatiguePenalty * perkXpMultiplier * traitXpMultiplier
        val staBonusMult = (if (hasConditionCoach) 1.30f else 1.0f) * diet.efficiencyMultiplier * disciplineBonus * fatiguePenalty * perkXpMultiplier * traitXpMultiplier

        val rawStrXp = (drill.strXpGain * strBonusMult).toInt()
        val rawAgiXp = (drill.agiXpGain * agiBonusMult).toInt()
        val rawStaXp = (drill.staXpGain * staBonusMult).toInt()

        var newStr = g.str
        var newAgi = g.agi
        var newSta = g.sta
        var newMaxHp = g.maxHp + drill.hpBonus

        val totalStrXp = g.strXpProgress + rawStrXp
        val strGained = totalStrXp / 100
        val remStrXp = totalStrXp % 100
        newStr += strGained

        val totalAgiXp = g.agiXpProgress + rawAgiXp
        val agiGained = totalAgiXp / 100
        val remAgiXp = totalAgiXp % 100
        newAgi += agiGained

        val totalStaXp = g.staXpProgress + rawStaXp
        val staGained = totalStaXp / 100
        val remStaXp = totalStaXp % 100
        newSta += staGained

        val baseFatigueCost = drill.fatigueCost
        val effectiveFatigueCost = if (g.personalityTrait == GladiatorTrait.SPARTAN_DISCIPLINE && baseFatigueCost > 0) {
            (baseFatigueCost * 0.75f).toInt()
        } else baseFatigueCost

        val newFatigue = max(0, min(100, g.fatigue + effectiveFatigueCost))
        val newMorale = max(10, min(100, g.mor + drill.moraleBonus + diet.dailyMoraleBonus))
        val completedCount = g.drillsCompletedCount + 1
        val newMastery = 1 + (completedCount / 6)

        val statGains = mutableListOf<String>()
        if (strGained > 0) statGains.add("+$strGained STR")
        if (agiGained > 0) statGains.add("+$agiGained AGI")
        if (staGained > 0) statGains.add("+$staGained STA")
        if (drill.hpBonus > 0) statGains.add("+${drill.hpBonus} Max HP")

        val summaryStr = if (statGains.isNotEmpty()) {
            "⚡ ${drill.title} ile ${statGains.joinToString(", ")} kazandı!"
        } else {
            "🎯 ${drill.title} yapıldı. Gelişim XP'si kaydedildi."
        }

        val updated = g.copy(
            str = newStr,
            agi = newAgi,
            sta = newSta,
            maxHp = newMaxHp,
            currentHp = if (drill == SpecificDrill.THERMAE_MASSAGE) min(newMaxHp, g.currentHp + 35) else g.currentHp,
            fatigue = newFatigue,
            mor = newMorale,
            strXpProgress = remStrXp,
            agiXpProgress = remAgiXp,
            staXpProgress = remStaXp,
            drillsCompletedCount = completedCount,
            drillMasteryLevel = newMastery,
            assignedDrill = drill,
            lastDrillSummary = summaryStr,
            hasTrainedToday = true,
            isInjured = if (sufferedFatigueInjury) true else g.isInjured,
            injurySeverity = if (sufferedFatigueInjury) InjurySeverity.LIGHT else g.injurySeverity,
            recoveryDaysLeft = if (sufferedFatigueInjury) 3 else g.recoveryDaysLeft
        )

        val finalSummary = if (sufferedFatigueInjury) {
            "$summaryStr ⚠️ Aşırı yorgunluk nedeniyle antrenmanda kas yırtılması yaşadı! (3 Gün Revir)"
        } else summaryStr

        return Pair(updated, finalSummary)
    }

    suspend fun executeSingleGladiatorDrill(gladiatorId: Long): String {
        val target = gladiatorDao.getGladiatorById(gladiatorId)?.toDomain() ?: return "Gladyatör bulunamadı."
        val stateEntity = ludusStateDao.getLudusStateDirect() ?: return "Durum okunamadı."
        val activeTeachers = teacherDao.getAllTeachersDirect().map { it.toDomain() }
        val currentState = stateEntity.toDomain(activeTeachers)

        if (target.hasTrainedToday && target.assignedDrill != SpecificDrill.THERMAE_MASSAGE) {
            return "✅ ${target.name} bugünkü idmanını zaten yaptı. Yeni güne kadar dinleniyor."
        }
        if (target.isInjured && target.assignedDrill != SpecificDrill.THERMAE_MASSAGE) {
            return "🩹 ${target.name} sakat olduğu için revirde tedavi görmeli."
        }
        if (target.fatigue >= 100 && target.assignedDrill != SpecificDrill.THERMAE_MASSAGE) {
            return "💤 ${target.name} %100 yorgunluk seviyesinde. İdmana çıkamaz."
        }

        val (updated, summary) = processGladiatorDrill(target, target.assignedDrill, currentState.dietPlan, activeTeachers, currentState.unlockedPerkIds)
        gladiatorDao.updateGladiator(GladiatorEntity.fromDomain(updated))
        return "${target.name}: $summary"
    }

    suspend fun executeAllGladiatorDrills(): String {
        val all = gladiatorDao.getAllGladiators().firstOrNull()?.map { it.toDomain() } ?: return "Kadroda gladyatör yok."
        val stateEntity = ludusStateDao.getLudusStateDirect() ?: return "Durum okunamadı."
        val activeTeachers = teacherDao.getAllTeachersDirect().map { it.toDomain() }
        val currentState = stateEntity.toDomain(activeTeachers)

        val readyGladiators = all.filter { !it.hasTrainedToday && (!it.isInjured || it.assignedDrill == SpecificDrill.THERMAE_MASSAGE) && (it.fatigue < 100 || it.assignedDrill == SpecificDrill.THERMAE_MASSAGE) }
        if (readyGladiators.isEmpty()) {
            return "✅ Kadrodaki tüm gladyatörler bugünkü idmanlarını zaten tamamladı."
        }

        val updatedList = all.map { g ->
            if (!g.hasTrainedToday && (!g.isInjured || g.assignedDrill == SpecificDrill.THERMAE_MASSAGE) && (g.fatigue < 100 || g.assignedDrill == SpecificDrill.THERMAE_MASSAGE)) {
                val (updated, _) = processGladiatorDrill(g, g.assignedDrill, currentState.dietPlan, activeTeachers, currentState.unlockedPerkIds)
                updated
            } else {
                g
            }
        }
        gladiatorDao.insertAllGladiators(updatedList.map { GladiatorEntity.fromDomain(it) })
        return "🏛️ ${readyGladiators.size} gladyatör için günlük tatbikatlar tamamlandı ve gelişim puanları kaydedildi!"
    }

    // --- DAY PHASE PROGRESSION ---
    suspend fun advanceDayPhase(): String {
        val stateEntity = ludusStateDao.getLudusStateDirect() ?: return "Hata: Durum okunamadı"
        val activeTeachers = teacherDao.getAllTeachersDirect().map { it.toDomain() }
        val currentState = stateEntity.toDomain(activeTeachers)
        val allGladiators = gladiatorDao.getAllGladiators().firstOrNull()?.map { it.toDomain() } ?: emptyList()

        val result = DailyCycleEngine.advancePhase(
            currentState = currentState,
            allGladiators = allGladiators,
            activeTeachers = activeTeachers,
            onProcessDrill = { g, drill, diet, teachers, perks ->
                processGladiatorDrill(g, drill, diet, teachers, perks)
            }
        )

        gladiatorDao.insertAllGladiators(result.updatedGladiators.map { GladiatorEntity.fromDomain(it) })
        saveLudusState(result.updatedState)
        return result.summaryMessage
    }

    // --- MANUMIT / FREEDOM ---
    suspend fun manumitGladiator(gladiator: Gladiator): Pair<Int, Int> {
        val currentState = ludusStateDao.getLudusStateDirect()?.toDomain() ?: LudusState()
        val prestigeReward = 40 + (gladiator.wins * 15) + (gladiator.totalPowerScore / 4)
        val patronFundGold = 80 + (gladiator.wins * 25)

        removeGladiator(gladiator.id)
        val updatedState = currentState.copy(
            gold = currentState.gold + patronFundGold,
            prestige = currentState.prestige + prestigeReward,
            freedGladiatorsCount = currentState.freedGladiatorsCount + 1
        )
        saveLudusState(updatedState)
        return Pair(prestigeReward, patronFundGold)
    }

    // --- TOURNAMENTS & MATCH SCHEDULING ---
    fun generateScheduledEventForArena(cityTier: CityTier, day: Int, targetDay: Int): ScheduledArenaEvent =
        TournamentScheduler.generateScheduledEventForArena(cityTier, day, targetDay)

    fun generateUpcomingScheduledEvents(
        currentCityTier: CityTier,
        currentDay: Int,
        nextMatchDay: Int
    ): List<ScheduledArenaEvent> =
        TournamentScheduler.generateUpcomingScheduledEvents(currentCityTier, currentDay, nextMatchDay)

    fun generateOpponentsForCity(
        cityTier: CityTier,
        playerPower: Int = 80,
        day: Int = 1,
        difficultyModifier: Float = 1.0f,
        ruthlessnessScore: Int = 0,
        crowdSentimentScore: Int = 50
    ): List<EnemyGladiator> =
        TournamentScheduler.generateOpponentsForCity(
            cityTier,
            playerPower,
            day,
            difficultyModifier,
            ruthlessnessScore,
            crowdSentimentScore
        )

    // --- MATCH RESOLUTION & COMBAT LOGS ---
    suspend fun recordMatchResolution(battleState: BattleState) {
        val currentState = ludusStateDao.getLudusStateDirect()?.toDomain() ?: LudusState()
        val player = gladiatorDao.getGladiatorById(battleState.playerGladiator.id)?.toDomain() ?: return

        val newGold = currentState.gold + battleState.earnedGold
        var newPrestige = currentState.prestige + battleState.earnedPrestige
        var freedCount = currentState.freedGladiatorsCount

        val matchResultStr = if (battleState.isPlayerVictorious) {
            if (battleState.crowdJudgementDecision == CrowdVerdict.IUGULA) "Zafer (İnfaz)" else "Zafer (Bağışlandı)"
        } else {
            "Yenilgi"
        }

        matchLogDao.insertMatchLog(
            MatchLogEntity(
                day = currentState.day,
                gladiatorName = player.name,
                opponentName = battleState.enemyGladiator.name,
                format = battleState.matchFormat.title,
                tactic = battleState.tactic.title,
                result = matchResultStr,
                goldEarned = battleState.earnedGold,
                prestigeEarned = battleState.earnedPrestige
            )
        )

        val critCount = battleState.logs.count { it.isCritical }
        val matchExcitement = BattleEngine.calculateMatchExcitement(
            battleState.crowdHype,
            critCount,
            battleState.tactic,
            battleState.isPlayerVictorious
        )

        var newDiffModifier = currentState.opponentDifficultyModifier
        var newRuthless = currentState.ruthlessnessScore
        var newMercy = currentState.mercyScore
        var currentSentiment = currentState.crowdSentimentScore
        var consequenceNote = currentState.lastDecisionConsequence

        if (battleState.isPlayerVictorious && battleState.crowdJudgementDecision != null) {
            if (battleState.crowdJudgementDecision == CrowdVerdict.IUGULA) {
                newRuthless += 1
                val crowdWantedBlood = battleState.crowdBloodlustPercent >= 50
                val sentimentShift = if (crowdWantedBlood) -8 else -16
                currentSentiment = (currentSentiment + sentimentShift).coerceIn(0, 100)

                val diffBump = if (crowdWantedBlood) 0.15f else 0.20f
                newDiffModifier = (newDiffModifier + diffBump).coerceAtMost(1.95f)
                val diffPercent = (newDiffModifier * 100).toInt()
                val sentimentName = CrowdSentimentLevel.fromScore(currentSentiment, newRuthless, newMercy).displayName
                consequenceNote = "⚔️ İNFAZ YANKILANDI: ${battleState.enemyGladiator.ludusOrigin} intikam yemini etti! Seyirci: $sentimentName (Gelecek Zorluk: %$diffPercent)."
            } else if (battleState.crowdJudgementDecision == CrowdVerdict.MISSIO) {
                newMercy += 1
                val crowdWantedMercy = battleState.crowdBloodlustPercent < 60
                val sentimentShift = if (crowdWantedMercy) 15 else 8
                currentSentiment = (currentSentiment + sentimentShift).coerceIn(0, 100)

                newDiffModifier = (newDiffModifier - 0.08f).coerceAtLeast(0.85f)
                val diffPercent = (newDiffModifier * 100).toInt()
                val sentimentName = CrowdSentimentLevel.fromScore(currentSentiment, newRuthless, newMercy).displayName
                consequenceNote = "🕊️ ASİL BAĞIŞLAMA: ${battleState.playerGladiator.name}'un merhameti Roma halkı ve senatörlerince alkışlandı. Seyirci: $sentimentName (Gelecek Zorluk: %$diffPercent)."
            }
        } else if (!battleState.isPlayerVictorious) {
            currentSentiment = (currentSentiment - 6).coerceIn(0, 100)
            newDiffModifier = (newDiffModifier + 0.05f).coerceAtMost(1.95f)
            val diffPercent = (newDiffModifier * 100).toInt()
            consequenceNote = "💀 YENİLGİ ETKİSİ: Seyirciler sarsıldı, rakip luduslar cesaretlendi (Gelecek Zorluk: %$diffPercent)."
        }

        if (battleState.isPlayerVictorious && battleState.wasFreedByPromise) {
            val freedomPrestige = 80 + player.wins * 25
            newPrestige += freedomPrestige
            freedCount += 1
            removeGladiator(player.id)
        } else {
            val isKilled = battleState.isPlayerVictorious && (battleState.crowdJudgementDecision == CrowdVerdict.IUGULA || battleState.matchFormat == MatchFormat.SINE_MISSIO)
            val isSpared = battleState.isPlayerVictorious && battleState.crowdJudgementDecision == CrowdVerdict.MISSIO
            val moraleBonus = if (battleState.isPlayerVictorious) { if (isSpared) 20 else 15 } else -20

            var updatedGladiator = player.copy(
                wins = if (battleState.isPlayerVictorious) player.wins + 1 else player.wins,
                losses = if (!battleState.isPlayerVictorious) player.losses + 1 else player.losses,
                kills = if (isKilled) player.kills + 1 else player.kills,
                currentHp = max(1, battleState.playerCurrentHp),
                promiseOfFreedom = false,
                mor = max(10, min(100, player.mor + moraleBonus)),
                experience = player.experience + (if (battleState.isPlayerVictorious) 35 else 15),
                fatigue = min(100, player.fatigue + 35)
            )

            if (battleState.sufferedPermanentInjury || battleState.playerCurrentHp <= 0 || battleState.injurySufferedDesc != null) {
                val physicianLvl = currentState.physicianLevel
                val survivalRate = when (physicianLvl) {
                    3 -> 98
                    2 -> 80
                    else -> 50
                }
                val survived = Random.nextInt(100) < survivalRate || battleState.matchFormat == MatchFormat.LUSUS

                if (!survived && battleState.matchFormat == MatchFormat.SINE_MISSIO) {
                    removeGladiator(player.id)
                    saveLudusState(currentState.copy(
                        gold = newGold,
                        prestige = max(0, newPrestige - 15),
                        totalFights = currentState.totalFights + 1,
                        totalWins = if (battleState.isPlayerVictorious) currentState.totalWins + 1 else currentState.totalWins,
                        opponentDifficultyModifier = newDiffModifier,
                        ruthlessnessScore = newRuthless,
                        mercyScore = newMercy,
                        crowdSentimentScore = currentSentiment,
                        lastMatchExcitement = matchExcitement,
                        lastDecisionConsequence = consequenceNote
                    ))
                    return
                } else {
                    val permLossRate = when (physicianLvl) {
                        3 -> 0
                        2 -> 15
                        else -> 40
                    }
                    val statLoss = if (Random.nextInt(100) < permLossRate) {
                        if (physicianLvl == 1) 4 else 2
                    } else 0

                    updatedGladiator = updatedGladiator.copy(
                        isInjured = true,
                        injurySeverity = if (battleState.matchFormat == MatchFormat.SINE_MISSIO) InjurySeverity.SEVERE else InjurySeverity.LIGHT,
                        recoveryDaysLeft = if (physicianLvl == 3) 2 else if (physicianLvl == 2) 4 else 6,
                        str = max(5, updatedGladiator.str - statLoss),
                        agi = max(5, updatedGladiator.agi - statLoss),
                        hasDisabledLimb = battleState.sufferedPermanentInjury,
                        disabledLimbDesc = if (battleState.sufferedPermanentInjury) "Kalıcı Arena Yaralanması" else null
                    )
                }
            }
            saveGladiator(updatedGladiator)
        }

        val nextFightDay = currentState.day + Random.nextInt(3, 6)

        saveLudusState(
            currentState.copy(
                gold = newGold,
                prestige = newPrestige,
                totalFights = currentState.totalFights + 1,
                totalWins = if (battleState.isPlayerVictorious) currentState.totalWins + 1 else currentState.totalWins,
                freedGladiatorsCount = freedCount,
                nextScheduledMatchDay = nextFightDay,
                opponentDifficultyModifier = newDiffModifier,
                ruthlessnessScore = newRuthless,
                mercyScore = newMercy,
                crowdSentimentScore = currentSentiment,
                lastMatchExcitement = matchExcitement,
                lastDecisionConsequence = consequenceNote,
                scoutedEnemyWeakness = false,
                sharpenedWeapons = false,
                crowdHypeBonus = false,
                rivalWeakenedByPoison = false,
                marsDivineBlessing = false
            )
        )
    }

    // --- RECRUITMENT & STAFF MANAGEMENT ---
    suspend fun promoteGladiatorToTeacher(gladiator: Gladiator): Teacher? {
        if (!gladiator.canPromoteToTeacher) return null
        val specialty = when (gladiator.gladiatorClass) {
            GladiatorClass.MURMILLO, GladiatorClass.SECUTOR -> TeacherSpecialty.MURMILLO_MASTER
            GladiatorClass.RETIARIUS, GladiatorClass.THRAEX -> TeacherSpecialty.RETIARIUS_MASTER
            GladiatorClass.DIMACHAERUS -> TeacherSpecialty.VETERAN_LEGEND
        }
        val promotedTeacher = Teacher(
            id = 0,
            name = gladiator.name,
            title = "Doctore (${gladiator.nickname})",
            specialty = specialty,
            originGladiatorName = "${gladiator.name} (${gladiator.origin})",
            level = max(1, gladiator.wins / 3 + 1),
            statBonusMultiplier = 1.35f + (gladiator.wins * 0.05f),
            dailySalary = 10 + (gladiator.wins * 2),
            hireCost = 0,
            description = "${gladiator.origin} kökenli, ${gladiator.wins} zaferli emekli şampiyonumuz. Artık kışlada yeni nesli eğitiyor.",
            isPromotedFromRoster = true
        )
        val insertedId = teacherDao.insertTeacher(TeacherEntity.fromDomain(promotedTeacher))
        removeGladiator(gladiator.id)
        val prestigeReward = 45 + (gladiator.wins * 15)
        val state = ludusStateDao.getLudusStateDirect()?.toDomain()
        if (state != null) {
            saveLudusState(state.copy(prestige = state.prestige + prestigeReward))
        }
        return promotedTeacher.copy(id = insertedId)
    }

    suspend fun assignGladiatorDrill(gladiatorId: Long, drill: SpecificDrill): Boolean {
        val gladiator = gladiatorDao.getGladiatorById(gladiatorId)?.toDomain() ?: return false
        val updated = gladiator.copy(
            assignedDrill = drill,
            trainingFocus = drill.toLegacyTrainingType()
        )
        gladiatorDao.updateGladiator(GladiatorEntity.fromDomain(updated))
        return true
    }

    suspend fun bulkAssignDrill(drill: SpecificDrill): Boolean {
        val all = gladiatorDao.getAllGladiators().firstOrNull()?.map { it.toDomain() } ?: return false
        val updatedList = all.map {
            it.copy(
                assignedDrill = drill,
                trainingFocus = drill.toLegacyTrainingType()
            )
        }
        gladiatorDao.insertAllGladiators(updatedList.map { GladiatorEntity.fromDomain(it) })
        return true
    }

    suspend fun setDietPlan(diet: DietPlan): Boolean {
        val stateEntity = ludusStateDao.getLudusStateDirect() ?: return false
        val activeTeachers = teacherDao.getAllTeachersDirect().map { it.toDomain() }
        val currentState = stateEntity.toDomain(activeTeachers)
        saveLudusState(currentState.copy(dietPlan = diet))
        return true
    }

    suspend fun recruitGladiator(gladiator: Gladiator, price: Int): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        val currentGladiators = gladiatorDao.getAllGladiators().firstOrNull() ?: emptyList()
        if (currentGladiators.size >= state.maxGladiatorSlots || state.gold < price) return false

        saveGladiator(gladiator)
        saveLudusState(state.copy(gold = state.gold - price))
        return true
    }

    suspend fun hireTeacher(teacher: Teacher): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        if (state.gold < teacher.hireCost) return false
        teacherDao.insertTeacher(TeacherEntity.fromDomain(teacher))
        saveLudusState(state.copy(gold = state.gold - teacher.hireCost))
        return true
    }

    suspend fun dismissTeacher(teacherId: Long): Boolean {
        teacherDao.deleteTeacherById(teacherId)
        return true
    }

    suspend fun upgradePhysician(cost: Int = 120): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        if (state.physicianLevel >= 3 || state.gold < cost) return false
        saveLudusState(state.copy(physicianLevel = state.physicianLevel + 1, gold = state.gold - cost))
        return true
    }

    suspend fun hireGuard(cost: Int = 50): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        if (state.guardsHired >= state.maxGuards || state.gold < cost) return false
        saveLudusState(state.copy(guardsHired = state.guardsHired + 1, gold = state.gold - cost))
        return true
    }

    suspend fun repayDebt(amount: Int): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        if (state.activeDebt <= 0 || state.gold < amount) return false
        val newDebt = max(0, state.activeDebt - amount)
        val newStage = if (newDebt == 0) ThreatStage.NONE else state.threatStage
        saveLudusState(
            state.copy(
                gold = state.gold - amount,
                activeDebt = newDebt,
                threatStage = newStage,
                debtDueDaysLeft = if (newDebt == 0) 0 else state.debtDueDaysLeft
            )
        )
        return true
    }

    // --- INFIRMARY & TREATMENTS (DELEGATED TO InfirmaryEngine) ---
    suspend fun applyInstantHealPotion(gladiatorId: Long): Boolean {
        val gladiator = gladiatorDao.getGladiatorById(gladiatorId)?.toDomain() ?: return false
        val healed = InfirmaryEngine.applyInstantHealPotion(gladiator)
        saveGladiator(healed)
        return true
    }

    suspend fun applyHerbalPoulticeTreatment(gladiatorId: Long, cost: Int = 30): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        val gladiator = gladiatorDao.getGladiatorById(gladiatorId)?.toDomain() ?: return false
        val outcome = InfirmaryEngine.applyHerbalPoulticeTreatment(gladiator, state, cost) ?: return false
        saveGladiator(outcome.first)
        saveLudusState(outcome.second)
        return true
    }

    suspend fun applyThermalBathTreatment(gladiatorId: Long, cost: Int = 40): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        val gladiator = gladiatorDao.getGladiatorById(gladiatorId)?.toDomain() ?: return false
        val outcome = InfirmaryEngine.applyThermalBathTreatment(gladiator, state, cost) ?: return false
        saveGladiator(outcome.first)
        saveLudusState(outcome.second)
        return true
    }

    suspend fun performEmergencySurgery(gladiatorId: Long, cost: Int = 120): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        val gladiator = gladiatorDao.getGladiatorById(gladiatorId)?.toDomain() ?: return false
        val outcome = InfirmaryEngine.performEmergencySurgery(gladiator, state, cost) ?: return false
        saveGladiator(outcome.first)
        saveLudusState(outcome.second)
        return true
    }

    suspend fun applyInstantHealWithGold(gladiatorId: Long, cost: Int = 85): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        val gladiator = gladiatorDao.getGladiatorById(gladiatorId)?.toDomain() ?: return false
        val outcome = InfirmaryEngine.applyInstantHealWithGold(gladiator, state, cost) ?: return false
        saveGladiator(outcome.first)
        saveLudusState(outcome.second)
        return true
    }

    suspend fun expandLudusSlots(): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        saveLudusState(state.copy(maxGladiatorSlots = state.maxGladiatorSlots + 2))
        return true
    }

    suspend fun claimRewardedAdBonus(): Pair<Int, Int> {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return Pair(0, 0)
        val goldBonus = 120
        val prestigeBonus = 15
        saveLudusState(state.copy(gold = state.gold + goldBonus, prestige = state.prestige + prestigeBonus))
        return Pair(goldBonus, prestigeBonus)
    }

    suspend fun activateSenatorSponsorship(days: Int) {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return
        saveLudusState(state.copy(senatorSponsorshipDays = state.senatorSponsorshipDays + days))
    }

    // --- BETWEEN-CYCLE EVENTS (DELEGATED TO DowntimeEventEngine) ---
    fun generateBetweenCycleEvent(day: Int, cityTier: CityTier, ludusState: LudusState, forceRandom: Boolean = false): BetweenCycleEvent =
        DowntimeEventEngine.generateBetweenCycleEvent(day, cityTier, ludusState, forceRandom)

    suspend fun resolveBetweenCycleEventChoice(
        event: BetweenCycleEvent,
        choice: EventDecisionChoice,
        targetGladiatorId: Long?
    ): EventResolutionResult {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain()
            ?: return EventResolutionResult("Hata", "Durum okunamadı")
        val allGladiators = gladiatorDao.getAllGladiators().firstOrNull()?.map { it.toDomain() } ?: emptyList()

        val execResult = DowntimeEventEngine.resolveBetweenCycleEvent(
            event = event,
            choice = choice,
            targetGladiatorId = targetGladiatorId,
            currentState = state,
            currentGladiators = allGladiators
        )

        if (execResult.newGladiatorToInsert != null) {
            gladiatorDao.insertGladiator(GladiatorEntity.fromDomain(execResult.newGladiatorToInsert))
        }
        gladiatorDao.insertAllGladiators(execResult.updatedGladiators.map { GladiatorEntity.fromDomain(it) })
        saveLudusState(execResult.updatedState)

        return execResult.resolutionResult
    }

    // --- DAILY DILEMMAS (DELEGATED TO EventEngine) ---
    fun generateDailyDilemma(day: Int, cityTier: CityTier): DailyDilemma =
        EventEngine.generateDailyDilemma(day, cityTier)

    suspend fun resolveDilemma(option: DilemmaOption): String {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return "Hata: Durum okunamadı"
        if (option.goldCost > 0 && state.gold < option.goldCost) {
            return "Yetersiz altın! Bu seçenek için ${option.goldCost} Altın gerekir."
        }

        val newGold = state.gold - option.goldCost + option.goldReward
        val newPrestige = state.prestige + option.prestigeReward

        val gladiators = gladiatorDao.getAllGladiators().firstOrNull()?.map { it.toDomain() } ?: emptyList()
        if (gladiators.isNotEmpty()) {
            val updated = gladiators.map { g ->
                g.copy(
                    mor = max(10, min(100, g.mor + option.moraleChange)),
                    fatigue = max(0, min(100, g.fatigue + option.fatigueChange))
                )
            }
            gladiatorDao.insertAllGladiators(updated.map { GladiatorEntity.fromDomain(it) })
        }

        saveLudusState(state.copy(gold = newGold, prestige = newPrestige))
        return option.outcomeStory
    }

    // --- SPARRING & TESSERAE (DELEGATED TO EventEngine) ---
    suspend fun performSparring(fighter1Id: Long, fighter2Id: Long?): SparringState {
        val gladiator1 = gladiatorDao.getGladiatorById(fighter1Id)?.toDomain() ?: return SparringState(
            gladiatorOne = Gladiator(name = "Marcus", nickname = "", origin = "", gladiatorClass = GladiatorClass.MURMILLO, contractType = GladiatorContractType.SLAVE, dailySalary = 0, priceValue = 0, str = 10, agi = 10, sta = 10, mor = 50, currentHp = 100, maxHp = 100),
            gladiatorTwo = Gladiator(name = "Kukla", nickname = "", origin = "", gladiatorClass = GladiatorClass.MURMILLO, contractType = GladiatorContractType.SLAVE, dailySalary = 0, priceValue = 0, str = 10, agi = 10, sta = 10, mor = 50, currentHp = 100, maxHp = 100)
        )

        val gladiator2 = if (fighter2Id != null && fighter2Id != fighter1Id) {
            gladiatorDao.getGladiatorById(fighter2Id)?.toDomain()
        } else null

        val activeTeachers = teacherDao.getAllTeachersDirect().map { it.toDomain() }
        val sparringResult = EventEngine.simulateSparring(gladiator1, gladiator2, activeTeachers)

        val statBoostMsg = sparringResult.statBoostSummary ?: "+25 XP"
        val updatedG1 = gladiator1.copy(
            experience = gladiator1.experience + sparringResult.xpGained,
            fatigue = min(100, gladiator1.fatigue + 15),
            mor = min(100, gladiator1.mor + 10),
            str = if (statBoostMsg.contains("STR")) gladiator1.str + 1 else gladiator1.str,
            agi = if (statBoostMsg.contains("AGI")) gladiator1.agi + 1 else gladiator1.agi
        )
        saveGladiator(updatedG1)

        if (gladiator2 != null && gladiator2.id > 0) {
            val updatedG2 = gladiator2.copy(
                experience = gladiator2.experience + 20,
                fatigue = min(100, gladiator2.fatigue + 15),
                mor = min(100, gladiator2.mor + 5)
            )
            saveGladiator(updatedG2)
        }

        return sparringResult.copy(gladiatorOne = updatedG1)
    }

    suspend fun playTesserae(bet: Int): TesseraeGameState {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return TesseraeGameState()
        val result = EventEngine.executeTesseraeRoll(bet, state.gold)

        if (result.hasPlayedThisCycle && result.isPlayerWinner != null) {
            val netChange = if (result.isPlayerWinner == true) result.goldReward - bet else -bet
            saveLudusState(state.copy(gold = max(0, state.gold + netChange)))
        }

        return result
    }

    // --- SUBURA TAVERN & ESPIONAGE ---
    suspend fun scoutOpponentWeakness(): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        val cost = 25
        if (state.gold < cost) return false
        saveLudusState(state.copy(gold = state.gold - cost, scoutedEnemyWeakness = true))
        return true
    }

    suspend fun buyMulsumFeast(): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        val cost = 30
        if (state.gold < cost) return false
        val gladiators = gladiatorDao.getAllGladiators().firstOrNull()?.map { it.toDomain() } ?: emptyList()
        val updated = gladiators.map { g ->
            g.copy(
                mor = min(100, g.mor + 25),
                fatigue = max(0, g.fatigue - 30)
            )
        }
        gladiatorDao.insertAllGladiators(updated.map { GladiatorEntity.fromDomain(it) })
        saveLudusState(state.copy(gold = state.gold - cost))
        return true
    }

    suspend fun bribeTownCriers(): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        val cost = 25
        if (state.gold < cost) return false
        saveLudusState(state.copy(gold = state.gold - cost, prestige = state.prestige + 20, crowdHypeBonus = true))
        return true
    }

    suspend fun applyThermalMassage(gladiatorId: Long): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        val cost = 15
        if (state.gold < cost) return false
        val gladiator = gladiatorDao.getGladiatorById(gladiatorId)?.toDomain() ?: return false
        val updated = gladiator.copy(
            fatigue = max(0, gladiator.fatigue - 45),
            currentHp = min(gladiator.maxHp, gladiator.currentHp + 30),
            mor = min(100, gladiator.mor + 10)
        )
        saveGladiator(updated)
        saveLudusState(state.copy(gold = state.gold - cost))
        return true
    }

    suspend fun sharpenArsenal(): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        val cost = 20
        if (state.gold < cost) return false
        saveLudusState(state.copy(gold = state.gold - cost, sharpenedWeapons = true))
        return true
    }

    suspend fun accelerateInjuryHeal(gladiatorId: Long): Boolean {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return false
        val cost = 30
        if (state.gold < cost) return false
        val gladiator = gladiatorDao.getGladiatorById(gladiatorId)?.toDomain() ?: return false
        if (!gladiator.isInjured) return false
        val newDays = max(0, gladiator.recoveryDaysLeft - 1)
        val isHealed = newDays == 0
        val updated = gladiator.copy(
            recoveryDaysLeft = newDays,
            isInjured = !isHealed,
            injurySeverity = if (isHealed) InjurySeverity.NONE else gladiator.injurySeverity,
            currentHp = if (isHealed) gladiator.maxHp else gladiator.currentHp + 25
        )
        saveGladiator(updated)
        saveLudusState(state.copy(gold = state.gold - cost))
        return true
    }

    // --- ARMORY & BLACKSMITH (DELEGATED TO ArmoryEngine) ---
    suspend fun buyAndEquipItem(gladiatorId: Long, itemId: String): String {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return "Hata: Durum okunamadı."
        val gladiator = gladiatorDao.getGladiatorById(gladiatorId)?.toDomain() ?: return "Gladyatör bulunamadı."
        val item = ArmoryEngine.getItemById(itemId) ?: return "Ekipman bulunamadı."

        val hasSilverTongue = state.unlockedPerkIds.contains(LanistaPerk.SILVER_TONGUE.id)
        val discount = if (hasSilverTongue) LanistaPerk.SILVER_TONGUE.discountPercent else 0
        val effectiveCost = ArmoryEngine.getEffectivePrice(item, discount)

        if (state.gold < effectiveCost) {
            return "Yetersiz Altın! $effectiveCost 🪙 gerekli (Mevcut: ${state.gold} 🪙)."
        }

        val updatedGladiator = when (item.slot) {
            EquipmentSlot.WEAPON -> gladiator.copy(
                equippedWeaponId = item.id,
                weaponLevel = max(gladiator.weaponLevel, item.damageBonus / 3 + 1)
            )
            EquipmentSlot.ARMOR -> gladiator.copy(
                equippedArmorId = item.id,
                armorLevel = max(gladiator.armorLevel, item.armorReductionPercent / 7 + 1),
                maxHp = gladiator.maxHp + item.maxHpBonus,
                currentHp = min(gladiator.maxHp + item.maxHpBonus, gladiator.currentHp + item.maxHpBonus)
            )
            EquipmentSlot.RELIC -> gladiator.copy(
                equippedRelicId = item.id,
                maxHp = gladiator.maxHp + item.maxHpBonus,
                currentHp = min(gladiator.maxHp + item.maxHpBonus, gladiator.currentHp + item.maxHpBonus)
            )
        }

        saveGladiator(updatedGladiator)
        saveLudusState(state.copy(gold = state.gold - effectiveCost))
        return "${item.icon} ${item.name} satın alındı ve ${gladiator.name}'a kuşandırıldı! (-$effectiveCost 🪙)"
    }

    suspend fun unequipItem(gladiatorId: Long, slot: EquipmentSlot): String {
        val gladiator = gladiatorDao.getGladiatorById(gladiatorId)?.toDomain() ?: return "Gladyatör bulunamadı."
        val updatedGladiator = when (slot) {
            EquipmentSlot.WEAPON -> gladiator.copy(equippedWeaponId = null)
            EquipmentSlot.ARMOR -> gladiator.copy(equippedArmorId = null)
            EquipmentSlot.RELIC -> gladiator.copy(equippedRelicId = null)
        }
        saveGladiator(updatedGladiator)
        return "${gladiator.name}'ın ${slot.title} yuvası boşaltıldı."
    }

    // --- LANISTA SKILL TREE & PERKS ---
    suspend fun unlockLanistaPerk(perk: LanistaPerk): String {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return "Hata: Durum okunamadı."
        if (state.unlockedPerkIds.contains(perk.id)) {
            return "${perk.title} zaten aktif!"
        }
        if (state.prestige < perk.prestigeCost) {
            return "Yetersiz Prestij! ${perk.prestigeCost} 🌿 gerekli (Mevcut: ${state.prestige} 🌿)."
        }

        val newUnlocked = state.unlockedPerkIds + perk.id
        saveLudusState(state.copy(prestige = state.prestige - perk.prestigeCost, unlockedPerkIds = newUnlocked))
        return "👑 ${perk.icon} ${perk.title} yeteneği kalıcı olarak açıldı! (-${perk.prestigeCost} 🌿)"
    }

    // --- IMPERIAL CAMPAIGN PROGRESSION (DELEGATED TO CampaignEngine) ---
    suspend fun completeCampaignMission(missionId: String): String {
        val state = ludusStateDao.getLudusStateDirect()?.toDomain() ?: return "Hata: Durum okunamadı."
        val mission = CampaignEngine.allMissions.find { it.id == missionId } ?: return "Görev bulunamadı."
        if (state.completedCampaignMissionIds.contains(missionId)) {
            return "${mission.missionTitle} zaten tamamlanmış."
        }

        val newCompleted = state.completedCampaignMissionIds + missionId
        saveLudusState(
            state.copy(
                gold = state.gold + mission.rewardGold,
                prestige = state.prestige + mission.rewardPrestige,
                completedCampaignMissionIds = newCompleted
            )
        )
        return "🏆 ${mission.missionTitle} ZAFERİ! +${mission.rewardGold} 🪙, +${mission.rewardPrestige} 🌿 ve [${mission.trophyName}] kazanıldı!"
    }
}
