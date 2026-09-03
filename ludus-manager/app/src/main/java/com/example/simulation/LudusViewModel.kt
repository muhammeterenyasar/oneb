package com.example.simulation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

enum class ActiveScreen {
    DASHBOARD,
    ROSTER,
    GLADIATOR_PROFILE,
    TRAINING,
    MEDICAL,
    RECRUITMENT,
    FACILITIES,
    STAFF,
    ARENA_HUB,
    MATCH_PREP,
    LIVE_COMBAT,
    POST_MATCH,
    POLITICS,
    WORLD_MAP,
    CHRONICLE,
    ECONOMY,
    EQUIPMENT_MARKET
}

data class LudusUiState(
    val dominus: LudusDominus = LudusDominus(dayNumber = 12),
    val currentScreen: ActiveScreen = ActiveScreen.DASHBOARD,
    val currentVenue: ArenaVenueId = ArenaVenueId.CAPUA,
    val persistentFighters: List<PersistentFighter> = ArenaDatabase.createInitialPersistentFighters(),
    val activeRivalries: List<RivalryEntry> = ArenaDatabase.createInitialRivalries(),
    val arenaCalendar: List<ArenaCalendarBout> = ArenaProgressionEngine.createInitialCalendar(12, ArenaDatabase.createInitialPersistentFighters()),
    val undergroundFights: List<UndergroundFight> = ArenaProgressionEngine.generateUndergroundFights(12, ArenaDatabase.createInitialPersistentFighters()),
    val fallenGladiators: List<FallenGladiatorMemorial> = emptyList(),
    val fightDayPhase: FightDayPhase = FightDayPhase.IDLE,
    val isUndergroundActive: Boolean = false,
    val selectedUndergroundFight: UndergroundFight? = null,
    val careerMatchHistory: List<CareerMatchRecord> = emptyList(),
    val selectedOpponentForScouting: PersistentFighter? = null,
    val selectedMatchType: ArenaMatchType = ArenaMatchType.STANDARD_DUEL,
    val activeOpponentFighter: PersistentFighter? = null,
    val gladiators: List<Gladiator> = SeedData.createInitialGladiators(),
    val marketGladiators: List<Gladiator> = SeedData.createMarketGladiators(),
    val facilities: List<Facility> = SeedData.createInitialFacilities(),
    val staff: List<StaffMember> = SeedData.createInitialStaff(),
    val cities: List<CityProgression> = SeedData.createInitialCities(),
    val patrons: List<Patron> = SeedData.createInitialPatrons(),
    val rivals: List<RivalLudus> = SeedData.createInitialRivals(),
    val chronicles: List<ChronicleEntry> = SeedData.createInitialChronicles(),
    val politicalFactions: List<PoliticalFaction> = PoliticalEngine.createInitialFactions(),
    val politicalCharacters: List<PoliticalCharacter> = PoliticalEngine.createInitialCharacters(),
    val politicalNetwork: List<NetworkConnection> = PoliticalEngine.createInitialNetwork(),
    val activeScandals: List<PoliticalScandal> = PoliticalEngine.createInitialScandals(),
    val playerSecrets: List<PoliticalSecret> = PoliticalEngine.createInitialSecrets(),
    val politicalCalendar: List<PoliticalCalendarEntry> = PoliticalEngine.createInitialCalendar(12),
    val activePoliticalEvent: PoliticalEvent? = null,
    val politicalResources: PoliticalResourceLedger = PoliticalResourceLedger(),
    val selectedPoliticalNpc: PoliticalCharacter? = null,
    val selectedPoliticalFaction: PoliticalFaction? = null,
    val activePatronId: String? = "npc_cassius",
    val scheduledMatch: ScheduledMatch = ScheduledMatch(
        id = "bout_day_17",
        arenaCity = "Capua",
        arenaName = "Capua Taşra Arenası",
        matchDateText = "Gün 17 • Capua Amfitiyatrosu (Kan Davası)",
        opponentLudus = "Domus Auctor",
        opponentGladiator = SeedData.createRivalFighter(),
        basePrizeGold = 2400,
        basePrestige = 280,
        matchType = "Rivalry Deathmatch"
    ),
    val selectedGladiator: Gladiator? = null,
    val selectedFighterId: String = "glad_1",
    val selectedStance: CombatStance = CombatStance.BALANCED,
    val selectedTarget: CombatTarget = CombatTarget.BLEED_DRAIN,
    val activeCombatEngine: CombatEngine? = null,
    val lastMatchResult: MatchResult? = null,
    val activeEvent: LudusEvent? = null,
    val statusMessage: String? = null,
    val isCombatRunning: Boolean = false,
    val isCombatPaused: Boolean = false,
    val combatSpeedMultiplier: Float = 1.0f,

    // Equipment & Roman Market Economy System
    val merchants: List<RomanMerchant> = EquipmentEngine.createInitialMerchants(),
    val selectedMerchantId: String = "merch_servius",
    val activeMarketTab: MarketTab = MarketTab.MERCHANTS,
    val selectedMarketCategory: EquipmentCategory? = null,
    val selectedMarketItem: EquipmentItem? = null,
    val comparisonGladiatorId: String = "glad_1",
    val auctions: List<EquipmentAuction> = EquipmentEngine.createInitialAuctions(),
    val commissions: List<CustomCommission> = emptyList(),
    val equipmentMarketEvents: List<EquipmentMarketEvent> = EquipmentEngine.createInitialMarketEvents(),
    val priceTrends: List<PriceTrend> = EquipmentEngine.createPriceTrends(),
    val gladiatorLoadouts: Map<String, GladiatorLoadout> = emptyMap(),
    val gladiatorPreferences: Map<String, GladiatorEquipmentPreference> = emptyMap(),
    val ludusArmory: List<EquipmentItem> = emptyList(),
    val bossStates: Map<String, BossState> = mapOf("boss_wolf_secundus" to BossState.ACTIVE),

    // Dynamic Story, World Memory & Curiosity Engine State
    val worldMemory: List<MemoryEntry> = SeedStoryData.createInitialWorldMemory(),
    val rumors: List<Rumor> = SeedStoryData.createInitialRumors(),
    val mysteries: List<WorldMystery> = SeedStoryData.createInitialMysteries(),
    val storyThreads: List<StoryThread> = SeedStoryData.createInitialStoryThreads(),
    val characterMemories: Map<String, CharacterMemory> = SeedStoryData.createInitialCharacterMemories(),
    val delayedConsequences: List<DelayedConsequence> = SeedStoryData.createInitialDelayedConsequences(),
    val tomorrowPreviews: List<TomorrowPreviewItem> = emptyList(),
    val selectedMysteryId: String? = "mystery_empty_bed",
    val selectedStoryHubTab: StoryHubTab = StoryHubTab.CHRONICLE,
    val selectedRumorId: String? = null,

    // Complete UI/UX Overhaul State (Attention, Search, End Day & What Changed)
    val attentionItems: List<AttentionItem> = emptyList(),
    val smartRecommendations: List<SmartRecommendation> = emptyList(),
    val dayAdvanceSummary: DayAdvanceSummary? = null,
    val showEndDayConfirmation: Boolean = false,
    val showWhatChangedDialog: Boolean = false,
    val showSearchDialog: Boolean = false,
    val pinnedGladiatorIds: Set<String> = setOf("glad_1")
)

class LudusViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LudusUiState())
    val uiState: StateFlow<LudusUiState> = _uiState.asStateFlow()

    private var combatLoopJob: Job? = null

    init {
        // Default selected gladiator to Titus
        val defaultGlad = _uiState.value.gladiators.firstOrNull()
        // Find opponent for player's upcoming bout (Day 17 vs Cassian)
        val defaultOpponent = _uiState.value.persistentFighters.find { it.id == "fighter_cassian" }
            ?: _uiState.value.persistentFighters.first()

        val scheduledMatch = ScheduledMatch(
            id = "bout_day_17",
            arenaCity = ArenaVenueId.CAPUA.city,
            arenaName = ArenaVenueId.CAPUA.venueName,
            matchDateText = "Gün 17 • Capua Amfitiyatrosu (Kan Davası)",
            opponentLudus = defaultOpponent.ludusAffiliation,
            opponentGladiator = defaultOpponent.toGladiator(),
            basePrizeGold = 2400,
            basePrestige = 280,
            matchType = "Rivalry Deathmatch"
        )

        val initialLoadouts = _uiState.value.gladiators.associate { glad ->
            glad.id to EquipmentEngine.createDefaultLoadoutForGladiator(glad)
        }
        val initialPreferences = _uiState.value.gladiators.associate { glad ->
            glad.id to EquipmentEngine.createPreferenceForGladiator(glad)
        }
        val firstItem = _uiState.value.merchants.firstOrNull()?.inventory?.firstOrNull()
        val initialStory = StoryDirector.simulateStoryDay(12, 42L, _uiState.value)

        _uiState.value = _uiState.value.copy(
            selectedGladiator = defaultGlad,
            selectedFighterId = defaultGlad?.id ?: "glad_1",
            activeOpponentFighter = defaultOpponent,
            scheduledMatch = scheduledMatch,
            gladiatorLoadouts = initialLoadouts,
            gladiatorPreferences = initialPreferences,
            selectedMarketItem = firstItem,
            comparisonGladiatorId = defaultGlad?.id ?: "glad_1",
            tomorrowPreviews = initialStory.tomorrowPreviews
        )
        val initialAttention = generateAttentionItems(_uiState.value)
        _uiState.value = _uiState.value.copy(attentionItems = initialAttention)
    }

    fun selectVenue(venue: ArenaVenueId) {
        val venueFighter = _uiState.value.persistentFighters.find { it.currentArena == venue }
            ?: _uiState.value.persistentFighters.first()
        _uiState.value = _uiState.value.copy(
            currentVenue = venue,
            statusMessage = "${venue.venueName} arenasına geçildi."
        )
    }

    fun selectScoutingOpponent(fighter: PersistentFighter) {
        _uiState.value = _uiState.value.copy(
            selectedOpponentForScouting = fighter
        )
    }

    fun challengeOpponent(fighter: PersistentFighter, matchType: ArenaMatchType = ArenaMatchType.STANDARD_DUEL) {
        val currentDay = _uiState.value.dominus.dayNumber
        val calendarCopy = _uiState.value.arenaCalendar.toMutableList()
        val (success, message) = ArenaProgressionEngine.schedulePlayerChallenge(
            currentDay = currentDay,
            calendar = calendarCopy,
            venue = fighter.currentArena,
            opponent = fighter,
            matchType = matchType
        )

        if (success) {
            val updatedChronicles = _uiState.value.chronicles.toMutableList()
            updatedChronicles.add(0, ChronicleEntry(_uiState.value.dominus.yearAUC, "Meydan Okuma", message, isGlory = false))
            _uiState.value = _uiState.value.copy(
                arenaCalendar = calendarCopy,
                chronicles = updatedChronicles,
                statusMessage = message
            )
        } else {
            _uiState.value = _uiState.value.copy(statusMessage = message)
        }
    }

    fun prepareScheduledMatch() {
        val currentDay = _uiState.value.dominus.dayNumber
        val todayBout = _uiState.value.arenaCalendar.find { it.day == currentDay && it.isPlayerMatch && !it.isCompleted }
        val playerGlad = _uiState.value.gladiators.find { it.id == _uiState.value.selectedFighterId }
            ?: _uiState.value.gladiators.first()

        if (todayBout == null) {
            val nextBout = _uiState.value.arenaCalendar.find { it.isPlayerMatch && !it.isCompleted && it.day > currentDay }
            val daysLeft = if (nextBout != null) nextBout.day - currentDay else 5
            val oppName = nextBout?.let { b ->
                val f = _uiState.value.persistentFighters.find { it.id == b.fighter1Id || it.id == b.fighter2Id }
                f?.fullDisplayName ?: b.fighter1Name
            } ?: "Cassian"
            _uiState.value = _uiState.value.copy(
                statusMessage = "Bugün resmi dövüş günü değildir! Bir sonraki müsabakanız Gün ${nextBout?.day ?: 17}'de ($oppName - $daysLeft gün kaldı). Kışlada antrenman yapın veya günü ilerletin."
            )
            return
        }

        val opponentFighter = _uiState.value.persistentFighters.find { it.id == todayBout.fighter1Id || it.id == todayBout.fighter2Id }
            ?: _uiState.value.persistentFighters.first()

        val (canFight, errorReason) = ArenaProgressionEngine.Validation.canStartOfficialCombat(currentDay, todayBout.day, playerGlad)
        if (!canFight) {
            _uiState.value = _uiState.value.copy(statusMessage = errorReason)
            return
        }

        val currentVenue = todayBout.venueId
        val purseMultiplier = PoliticalEngine.getArenaPurseMultiplier(_uiState.value.politicalFactions, _uiState.value.activePatronId != null)
        val baseGold = (2200 * currentVenue.purseMultiplier * todayBout.matchType.goldMultiplier * purseMultiplier).toInt()
        val basePrestige = (currentVenue.prestigePerWin * todayBout.matchType.prestigeMultiplier).toInt()

        val newScheduledMatch = ScheduledMatch(
            id = todayBout.id,
            arenaCity = currentVenue.city,
            arenaName = currentVenue.venueName,
            matchDateText = "Gün $currentDay • ${todayBout.matchType.title}",
            opponentLudus = opponentFighter.ludusAffiliation,
            opponentGladiator = opponentFighter.toGladiator(),
            basePrizeGold = baseGold,
            basePrestige = basePrestige,
            matchType = todayBout.matchType.title
        )

        _uiState.value = _uiState.value.copy(
            activeOpponentFighter = opponentFighter,
            selectedMatchType = todayBout.matchType,
            currentVenue = currentVenue,
            scheduledMatch = newScheduledMatch,
            isUndergroundActive = false,
            fightDayPhase = FightDayPhase.MORNING_PREPARATION,
            currentScreen = ActiveScreen.MATCH_PREP,
            statusMessage = "⚔ DÖVÜŞ GÜNÜ GELDİ! ${opponentFighter.fullDisplayName} ile resmi müsabaka başlıyor."
        )
    }

    fun prepareUndergroundMatch(fight: UndergroundFight) {
        val playerGlad = _uiState.value.gladiators.find { it.id == _uiState.value.selectedFighterId }
            ?: _uiState.value.gladiators.first()

        if (playerGlad.isDead) {
            _uiState.value = _uiState.value.copy(statusMessage = "Dövüşçünüz vefat etmiştir (DEAD). Arenaya çıkamaz.")
            return
        }
        if (_uiState.value.dominus.denarii < fight.entryFee) {
            _uiState.value = _uiState.value.copy(statusMessage = "Yeraltı dövüşüne giriş için ${fight.entryFee} Denarii gerekli! Kasanız yetersiz.")
            return
        }

        // Deduct entry fee
        val updatedDominus = _uiState.value.dominus.copy(denarii = _uiState.value.dominus.denarii - fight.entryFee)

        val newScheduledMatch = ScheduledMatch(
            id = fight.id,
            arenaCity = "Subura Yeraltı",
            arenaName = fight.venueName,
            matchDateText = "Gizli Gece Dövüşü • ${fight.title}",
            opponentLudus = "Yeraltı Çetesi (${fight.organizerName})",
            opponentGladiator = fight.opponentFighter.toGladiator(),
            basePrizeGold = fight.purseReward,
            basePrestige = 80,
            matchType = "Yeraltı Kan Dövüşü (${fight.riskLevel})"
        )

        _uiState.value = _uiState.value.copy(
            dominus = updatedDominus,
            activeOpponentFighter = fight.opponentFighter,
            selectedUndergroundFight = fight,
            isUndergroundActive = true,
            scheduledMatch = newScheduledMatch,
            selectedMatchType = ArenaMatchType.STANDARD_DUEL,
            fightDayPhase = FightDayPhase.COMBAT_ACTIVE,
            currentScreen = ActiveScreen.MATCH_PREP,
            statusMessage = "⚠ YERALTI DÖVÜŞÜ: ${fight.entryFee} Denarii bahis yatırıldı. ${fight.organizerName} sizi ringe çağırıyor!"
        )
    }

    fun startUndergroundFight(fight: UndergroundFight) {
        prepareUndergroundMatch(fight)
    }

    fun advanceFightDayPhase() {
        val currentPhase = _uiState.value.fightDayPhase
        val nextPhase = when (currentPhase) {
            FightDayPhase.IDLE -> FightDayPhase.MORNING_PREPARATION
            FightDayPhase.MORNING_PREPARATION -> FightDayPhase.AFTERNOON_ARRIVAL
            FightDayPhase.AFTERNOON_ARRIVAL -> FightDayPhase.PRE_MATCH_BRIEFING
            FightDayPhase.PRE_MATCH_BRIEFING -> FightDayPhase.COMBAT_ACTIVE
            FightDayPhase.COMBAT_ACTIVE -> FightDayPhase.POST_MATCH_REPORT
            FightDayPhase.POST_MATCH_REPORT -> FightDayPhase.IDLE
        }
        _uiState.value = _uiState.value.copy(fightDayPhase = nextPhase)
    }

    fun navigateTo(screen: ActiveScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun selectGladiator(gladiator: Gladiator) {
        _uiState.value = _uiState.value.copy(
            selectedGladiator = gladiator,
            selectedFighterId = gladiator.id
        )
    }

    fun setStance(stance: CombatStance) {
        _uiState.value = _uiState.value.copy(selectedStance = stance)
    }

    fun setTarget(target: CombatTarget) {
        _uiState.value = _uiState.value.copy(selectedTarget = target)
    }

    fun setSelectedFighter(fighterId: String) {
        val gladiator = _uiState.value.gladiators.find { it.id == fighterId }
        _uiState.value = _uiState.value.copy(
            selectedFighterId = fighterId,
            selectedGladiator = gladiator ?: _uiState.value.selectedGladiator
        )
    }

    fun advanceDay() {
        val currentDominus = _uiState.value.dominus
        val newDay = currentDominus.dayNumber + 1

        // Natural recovery & training stat gains
        val updatedGladiators = _uiState.value.gladiators.map { glad ->
            val cond = glad.condition
            // Natural health & stamina recover
            val newHealth = min(100, cond.health + 8)
            val newStamina = min(100, cond.stamina + 12)
            val newStress = max(0, cond.stress - 5)

            // Tick injuries
            val updatedInjuries = glad.injuries.mapNotNull { inj ->
                if (inj.daysRemaining > 1) {
                    inj.copy(daysRemaining = inj.daysRemaining - 1)
                } else null
            }.toMutableList()

            // Slight training growth
            val attrs = glad.attributes
            if (newStamina > 30) {
                when (glad.trainingFocus) {
                    "Güç Antrenmanı" -> attrs.strength = min(20, attrs.strength + 1)
                    "Kılıç & Kalkan Çalışması" -> attrs.swordsmanship = min(20, attrs.swordsmanship + 1)
                    "Çeviklik & Ayak Oyunları" -> attrs.speed = min(20, attrs.speed + 1)
                    "Dayanıklılık" -> attrs.endurance = min(20, attrs.endurance + 1)
                }
            }

            glad.copy(
                condition = cond.copy(
                    health = newHealth,
                    stamina = newStamina,
                    stress = newStress
                ),
                injuries = updatedInjuries
            )
        }

        // Daily upkeep expenses (Food & basic staff maintenance)
        val dailyCost = 60
        val newGold = max(0, currentDominus.denarii - dailyCost)
        val newFood = max(0, currentDominus.foodWheat - 25)

        // Random Procedural Events generator
        var triggeredEvent: LudusEvent? = null
        if (newDay % 3 == 0) {
            triggeredEvent = generateProceduralEvent(newDay)
        }

        // Advance World Calendar Simulation for other gladiators on the circuit
        val calendarCopy = _uiState.value.arenaCalendar.map { it.copy() }.toMutableList()
        val fightersCopy = _uiState.value.persistentFighters.map { it.copy() }.toMutableList()
        val memorialsCopy = _uiState.value.fallenGladiators.toMutableList()
        val worldNews = ArenaProgressionEngine.simulateCalendarDay(newDay, calendarCopy, fightersCopy, memorialsCopy)

        // Advance Living Political Simulation
        val polResult = PoliticalEngine.simulatePoliticalDay(
            currentDay = newDay,
            factions = _uiState.value.politicalFactions,
            characters = _uiState.value.politicalCharacters,
            scandals = _uiState.value.activeScandals,
            calendar = _uiState.value.politicalCalendar,
            playerSecrets = _uiState.value.playerSecrets,
            resources = _uiState.value.politicalResources,
            patronId = _uiState.value.activePatronId
        )

        // Patron monthly stipend
        val patronStipend = if (_uiState.value.activePatronId != null && newDay % 10 == 0) {
            val patron = polResult.updatedCharacters.find { it.id == _uiState.value.activePatronId }
            patron?.monthlyStipend ?: 0
        } else 0
        val finalGold = newGold + patronStipend

        // Check for interactive political event
        val triggeredPolEvent = PoliticalEngine.checkAndGeneratePoliticalEvent(
            day = newDay,
            factions = polResult.updatedFactions,
            characters = polResult.updatedCharacters,
            scandals = polResult.updatedScandals,
            patronId = _uiState.value.activePatronId,
            currentEvent = _uiState.value.activePoliticalEvent
        )

        // Add news to chronicles
        val updatedChronicles = _uiState.value.chronicles.toMutableList()
        worldNews.forEach { news ->
            updatedChronicles.add(0, ChronicleEntry(currentDominus.yearAUC, "Arena Bülteni", news, isGlory = false))
        }
        polResult.narrativeDispatches.forEach { dispatch ->
            updatedChronicles.add(0, ChronicleEntry(currentDominus.yearAUC, "Siyasi Kulis", dispatch, isGlory = false))
        }
        if (patronStipend > 0) {
            updatedChronicles.add(0, ChronicleEntry(currentDominus.yearAUC, "Hami Yardımı", "Haminiz kasanıza $patronStipend Denarii aylık ödenek aktardı.", isGlory = true))
        }

        // Keep 14 days of calendar populated with LIVING fighters only
        val livingFighters = fightersCopy.filter { it.isAlive }
        val maxDayOnCal = calendarCopy.maxOfOrNull { it.day } ?: newDay
        if (maxDayOnCal < newDay + 7 && livingFighters.isNotEmpty()) {
            for (d in (maxDayOnCal + 1)..(newDay + 14)) {
                val f1 = livingFighters.random()
                val f2 = livingFighters.filter { it.id != f1.id }.randomOrNull() ?: f1
                val isPlayerDay = (d % 5 == 2) // Scheduled rhythm
                calendarCopy.add(
                    ArenaCalendarBout(
                        id = "bout_day_$d",
                        day = d,
                        venueId = f1.currentArena,
                        fighter1Id = f1.id,
                        fighter1Name = f1.fullDisplayName,
                        fighter1Ludus = f1.ludusAffiliation,
                        fighter2Id = if (isPlayerDay) "player" else f2.id,
                        fighter2Name = if (isPlayerDay) "Titus (Your Ludus)" else f2.fullDisplayName,
                        fighter2Ludus = if (isPlayerDay) "Ludus Tuus" else f2.ludusAffiliation,
                        matchType = if (d % 7 == 0) ArenaMatchType.CHAMPIONSHIP_MATCH else ArenaMatchType.STANDARD_DUEL,
                        isPlayerMatch = isPlayerDay
                    )
                )
            }
        }

        // Generate dynamic deterministic underground fights for the new day
        val currentBossStates = _uiState.value.bossStates.toMutableMap()
        val newUndergroundFights = ArenaProgressionEngine.generateUndergroundFights(
            currentDay = newDay,
            fighters = fightersCopy,
            worldSeed = 42L,
            bossStates = currentBossStates
        )

        // Recruitment Market: Refresh rolling pool every 5 days with new arrivals from Roman provinces
        val updatedMarketGladiators = if (newDay % 5 == 0 || _uiState.value.marketGladiators.isEmpty()) {
            generateRecruitmentPool(newDay)
        } else {
            _uiState.value.marketGladiators
        }

        // Check player fight schedule notification
        val isPlayerFightToday = calendarCopy.any { it.day == newDay && it.isPlayerMatch && !it.isCompleted }
        val isPlayerFightTomorrow = calendarCopy.any { it.day == (newDay + 1) && it.isPlayerMatch && !it.isCompleted }

        // Equipment Market: Advance Auctions
        val (updatedAuctions, auctionResolutions) = EquipmentEngine.advanceAuctions(
            auctions = _uiState.value.auctions,
            dayNumber = newDay,
            playerDenarii = finalGold
        )
        var goldAfterAuctions = finalGold
        val armoryAfterAuctions = _uiState.value.ludusArmory.toMutableList()
        auctionResolutions.forEach { res ->
            if (res.isPlayerWinner) {
                goldAfterAuctions = max(0, goldAfterAuctions - res.finalPrice)
                armoryAfterAuctions.add(res.auction.item)
                updatedChronicles.add(0, ChronicleEntry(currentDominus.yearAUC, "Müzayede Zaferi", res.message, isGlory = true))
            } else {
                updatedChronicles.add(0, ChronicleEntry(currentDominus.yearAUC, "Müzayede Sonucu", res.message, isGlory = false))
            }
        }

        // Equipment Market: Advance Custom Blacksmith Commissions
        val (updatedCommissions, finishedCommissionItems) = EquipmentEngine.advanceCommissions(
            commissions = _uiState.value.commissions
        )
        finishedCommissionItems.forEach { item ->
            armoryAfterAuctions.add(item)
            updatedChronicles.add(0, ChronicleEntry(currentDominus.yearAUC, "Özel Sipariş Teslim Edildi", "${item.name} ocağı tamamlandı ve cephaneliğinize teslim edildi!", isGlory = true))
        }

        // Equipment Market: Advance Events
        val updatedMarketEvents = _uiState.value.equipmentMarketEvents.mapNotNull { ev ->
            if (ev.daysRemaining > 1) ev.copy(daysRemaining = ev.daysRemaining - 1) else null
        }.toMutableList()
        val randomEvent = EquipmentEngine.generateRandomMarketEvent(newDay, updatedMarketEvents)
        if (randomEvent != null) {
            updatedMarketEvents.add(randomEvent)
            updatedChronicles.add(0, ChronicleEntry(currentDominus.yearAUC, "Piyasa Havadisi", "${randomEvent.title}: ${randomEvent.description}", isGlory = false))
        }

        // Equipment Market: Rotate & Restock Inventories Every 4 Days
        val updatedMerchants = if (newDay % 4 == 0) {
            EquipmentEngine.restockMerchantInventories(_uiState.value.merchants, newDay, currentDominus.prestige)
        } else {
            _uiState.value.merchants
        }

        // Update Gladiator Morale from Equipment Preferences
        val gladiatorLoadoutsCopy = _uiState.value.gladiatorLoadouts
        val gladiatorPrefs = _uiState.value.gladiatorPreferences
        val gladiatorsWithPrefMorale = updatedGladiators.map { glad ->
            val loadout = gladiatorLoadoutsCopy[glad.id]
            val pref = gladiatorPrefs[glad.id]
            var moraleMod = 0
            if (pref != null && loadout != null) {
                if (pref.hatesHeavyArmor && (loadout.bodyArmor?.mobilityPenalty ?: 0) >= 4) {
                    moraleMod -= 3 // Hates heavy armor
                }
                if (loadout.mainHand?.type == pref.favoredWeaponType) {
                    moraleMod += 2 // Loves favored weapon
                }
                if (loadout.mainHand?.isNamedArtifact == true || loadout.helmet?.isNamedArtifact == true) {
                    moraleMod += 4 // Pride from wielding legendary relics
                }
            }
            if (moraleMod != 0) {
                glad.copy(condition = glad.condition.copy(morale = (glad.condition.morale + moraleMod).coerceIn(10, 100)))
            } else glad
        }

        // Story Director: Simulate Emergent World Story, Rumors, Delayed Consequences & Mysteries
        val storyResult = StoryDirector.simulateStoryDay(
            currentDay = newDay,
            worldSeed = 42L,
            state = _uiState.value
        )
        storyResult.chronicleEntries.forEach { entry ->
            updatedChronicles.add(0, entry)
        }
        val finalGoldAfterStory = max(0, goldAfterAuctions + storyResult.treasuryDelta)
        val finalPrestigeAfterStory = max(0, currentDominus.prestige + storyResult.prestigeDelta)

        val statusMsg = when {
            isPlayerFightToday -> "⚔ BUGÜN DÖVÜŞ GÜNÜ! Capua Arenası'nda resmi müsabakanız var!"
            isPlayerFightTomorrow -> "⏳ YARIN RESMİ MÜSABAKA GÜNÜ! Dövüşçünüzü dinlendirin ve hekime muayene ettirin."
            storyResult.dispatchedNarratives.isNotEmpty() -> storyResult.dispatchedNarratives.first()
            else -> "Gün $newDay: Siyasi kulisler, tüccarlar ve arena simülasyonu güncellendi."
        }

        val recoveries = mutableListOf<String>()
        gladiatorsWithPrefMorale.forEach { g ->
            if (g.condition.health >= 90 && g.injuries.isEmpty()) {
                recoveries.add("${g.name} mükemmel formuna kavuştu.")
            }
        }
        val marketChanges = mutableListOf<String>()
        if (updatedMarketEvents.isNotEmpty()) {
            marketChanges.add("Yeni piyasa olayı: ${updatedMarketEvents.first().headline}")
        } else {
            marketChanges.add("Forum tüccarları fiyatlarını ve stoklarını tazeledi.")
        }
        val boutResults = mutableListOf<String>()
        if (simulatedResults.isNotEmpty()) {
            boutResults.add(simulatedResults.first())
        }
        val storyAlerts = storyResult.dispatchedNarratives

        val daySummary = DayAdvanceSummary(
            fromDay = currentDay,
            toDay = newDay,
            recoveries = recoveries.take(2),
            marketChanges = marketChanges.take(2),
            boutResults = boutResults.take(2),
            politicalChanges = if (triggeredPolEvent != null) listOf(triggeredPolEvent.title) else emptyList(),
            storyAlerts = storyAlerts.take(2)
        )

        _uiState.value = _uiState.value.copy(
            dominus = currentDominus.copy(
                dayNumber = newDay,
                denarii = finalGoldAfterStory,
                prestige = finalPrestigeAfterStory,
                foodWheat = newFood
            ),
            gladiators = gladiatorsWithPrefMorale,
            persistentFighters = fightersCopy,
            arenaCalendar = calendarCopy,
            undergroundFights = newUndergroundFights,
            marketGladiators = updatedMarketGladiators,
            bossStates = currentBossStates,
            fallenGladiators = memorialsCopy,
            chronicles = updatedChronicles,
            activeEvent = triggeredEvent,
            activePoliticalEvent = triggeredPolEvent ?: _uiState.value.activePoliticalEvent,
            politicalFactions = polResult.updatedFactions,
            politicalCharacters = polResult.updatedCharacters,
            activeScandals = polResult.updatedScandals,
            politicalCalendar = polResult.updatedCalendar,
            politicalResources = polResult.updatedResources,
            merchants = updatedMerchants,
            auctions = updatedAuctions,
            commissions = updatedCommissions,
            equipmentMarketEvents = updatedMarketEvents,
            ludusArmory = armoryAfterAuctions,
            worldMemory = storyResult.updatedWorldMemory,
            rumors = storyResult.updatedRumors,
            mysteries = storyResult.updatedMysteries,
            storyThreads = storyResult.updatedStoryThreads,
            characterMemories = storyResult.updatedCharacterMemories,
            delayedConsequences = storyResult.updatedDelayedConsequences,
            tomorrowPreviews = storyResult.tomorrowPreviews,
            dayAdvanceSummary = daySummary,
            showWhatChangedDialog = true,
            showEndDayConfirmation = false,
            statusMessage = statusMsg
        )

        val newAttention = generateAttentionItems(_uiState.value)
        _uiState.value = _uiState.value.copy(attentionItems = newAttention)

        // Developer World Invariant Validation Layer check
        val validation = WorldSimulationValidator.validateWorldState(_uiState.value)
        if (!validation.isValid) {
            println("SIMULATION INVARIANT WARNINGS: " + validation.violations.joinToString("; "))
        }
    }

    private fun generateProceduralEvent(day: Int): LudusEvent {
        val events = listOf(
            LudusEvent(
                id = "evt_1",
                title = "Gece Sabotaj İhbarı",
                description = "Capua sokaklarındaki muhbirler, rakip Ludus Domus Auctor'un koğuşlara zehirli su koymak için casus tuttuğunu fısıldadı!",
                optionA = "Nöbetçileri ikiye katla (100 Denarii)",
                optionB = "Risk al, olağan devriyeye devam et",
                costA = 100,
                prestigeChangeA = 20,
                prestigeChangeB = -40
            ),
            LudusEvent(
                id = "evt_2",
                title = "Senator Cassius'un Ziyareti",
                description = "Senator Cassius ludusunuzu ziyaret etti ve gladyatörlerinizin formunu teftiş etmek istiyor. Şarap ve ziyafet bekliyor.",
                optionA = "Asil ziyafet düzenle (250 Denarii)",
                optionB = "Mütevazı karşıla (0 Denarii)",
                costA = 250,
                prestigeChangeA = 60,
                prestigeChangeB = -15
            ),
            LudusEvent(
                id = "evt_3",
                title = "Köleler Arasında Kavga",
                description = "Akşam yemeğinde iki gladyatör paylaşılamayan et porsiyonu yüzünden birbirine girdi. Biri hafif yaralandı.",
                optionA = "Hekim Lucius'a muayene ettir (50 Denarii)",
                optionB = "Kendi haline bırak, sert disiplin uygula",
                costA = 50,
                prestigeChangeA = 10,
                prestigeChangeB = -10
            )
        )
        return events[day % events.size]
    }

    fun resolveEvent(chooseOptionA: Boolean) {
        val event = _uiState.value.activeEvent ?: return
        val dominus = _uiState.value.dominus
        val cost = if (chooseOptionA) event.costA else event.costB
        val prestigeDelta = if (chooseOptionA) event.prestigeChangeA else event.prestigeChangeB

        _uiState.value = _uiState.value.copy(
            dominus = dominus.copy(
                denarii = max(0, dominus.denarii - cost),
                prestige = max(0, dominus.prestige + prestigeDelta)
            ),
            activeEvent = null,
            statusMessage = "Olay karara bağlandı."
        )
    }

    fun startMatch() {
        val fighter = _uiState.value.gladiators.find { it.id == _uiState.value.selectedFighterId }
            ?: _uiState.value.gladiators.first()

        if (fighter.isDead) {
            _uiState.value = _uiState.value.copy(statusMessage = "Dövüşçü vefat etmiştir (DEAD)! Arenaya çıkamaz.")
            return
        }

        val opponent = _uiState.value.scheduledMatch.opponentGladiator
        val activeOpponent = _uiState.value.activeOpponentFighter

        val playerLoadout = _uiState.value.gladiatorLoadouts[fighter.id]
        val opponentLoadout = activeOpponent?.let {
            EquipmentEngine.createLoadoutForOpponent(opponent, it.tier, _uiState.value.currentVenue)
        } ?: EquipmentEngine.createLoadoutForOpponent(opponent, OpponentTier.COMMON, _uiState.value.currentVenue)

        val engine = CombatEngine(
            playerGladiator = fighter,
            opponentGladiator = opponent,
            stance = _uiState.value.selectedStance,
            target = _uiState.value.selectedTarget,
            arenaName = _uiState.value.scheduledMatch.arenaName,
            aiPersonality = activeOpponent?.aiPersonality ?: AiTacticalPersonality.VETERAN,
            matchType = _uiState.value.selectedMatchType,
            playerLoadout = playerLoadout,
            opponentLoadout = opponentLoadout
        )

        _uiState.value = _uiState.value.copy(
            activeCombatEngine = engine,
            fightDayPhase = FightDayPhase.COMBAT_ACTIVE,
            currentScreen = ActiveScreen.LIVE_COMBAT,
            isCombatRunning = true,
            isCombatPaused = false
        )

        runCombatTicker()
    }

    private fun runCombatTicker() {
        combatLoopJob?.cancel()
        combatLoopJob = viewModelScope.launch {
            while (_uiState.value.isCombatRunning) {
                val engine = _uiState.value.activeCombatEngine
                if (engine == null || engine.isFinished || _uiState.value.isCombatPaused) {
                    if (engine?.isFinished == true && !engine.polliceVersoState.isActive) {
                        // Combat ended without pollice verso
                        endCombat()
                        break
                    }
                    delay(300)
                    continue
                }

                engine.stepTick()
                // Force UI state emission by re-assigning engine reference
                _uiState.value = _uiState.value.copy(activeCombatEngine = engine)

                if (engine.polliceVersoState.isActive) {
                    // Halt timer for player/crowd pollice verso decision
                    _uiState.value = _uiState.value.copy(isCombatPaused = true)
                }

                val tickInterval = (500L / _uiState.value.combatSpeedMultiplier).toLong()
                delay(tickInterval)
            }
        }
    }

    fun sendTacticalCommand(command: TacticalCommand) {
        val engine = _uiState.value.activeCombatEngine ?: return
        val feedback = engine.executeTacticalCommand(command)
        _uiState.value = _uiState.value.copy(
            activeCombatEngine = engine,
            statusMessage = feedback
        )
    }

    fun toggleCombatPause() {
        _uiState.value = _uiState.value.copy(isCombatPaused = !_uiState.value.isCombatPaused)
    }

    fun setCombatSpeed(multiplier: Float) {
        _uiState.value = _uiState.value.copy(combatSpeedMultiplier = multiplier)
    }

    fun decidePolliceVerso(spareFighter: Boolean, useBribe: Boolean = false) {
        val engine = _uiState.value.activeCombatEngine ?: return
        if (useBribe && _uiState.value.dominus.denarii >= 500) {
            _uiState.value = _uiState.value.copy(
                dominus = _uiState.value.dominus.copy(
                    denarii = _uiState.value.dominus.denarii - 500
                )
            )
            engine.decidePolliceVerso(spareFighter = true, usedBribe = true)
        } else {
            engine.decidePolliceVerso(spareFighter = spareFighter)
        }
        _uiState.value = _uiState.value.copy(activeCombatEngine = engine)

        viewModelScope.launch {
            delay(1500)
            endCombat()
        }
    }

    private fun endCombat() {
        combatLoopJob?.cancel()
        val engine = _uiState.value.activeCombatEngine ?: return
        val result = engine.buildMatchResult()

        val currentDominus = _uiState.value.dominus
        val activeOpponent = _uiState.value.activeOpponentFighter
        val memorials = _uiState.value.fallenGladiators.toMutableList()
        val isUnderground = _uiState.value.isUndergroundActive
        val undergroundFight = _uiState.value.selectedUndergroundFight

        var goldReward = result.goldReward
        var prestigeReward = result.prestigeReward
        if (isUnderground && undergroundFight != null && result.playerWon) {
            goldReward = undergroundFight.purseReward
            prestigeReward = 40
        }

        var newGold = currentDominus.denarii + goldReward
        var newPrestige = currentDominus.prestige + prestigeReward
        val newPopularity = currentDominus.popularity + (result.crowdFavorDelta * 10)

        // Underground Police Raid Discovery Check
        var undergroundDiscoveryMessage: String? = null
        if (isUnderground && undergroundFight != null) {
            val roll = kotlin.random.Random.nextInt(100)
            if (roll < undergroundFight.discoveryRiskPercent) {
                val fine = 1000
                newGold = max(0, newGold - fine)
                newPrestige = max(0, newPrestige - 30)
                undergroundDiscoveryMessage = "YERALTI BASKINI: Praetor devriyeleri dövüş mahzenini bastı! $fine Denarii ceza kesildi, asillerin gözünde -30 prestij kaybettiniz."
            }
        }

        // Fatality check for player
        val playerDied = !result.playerWon && !result.opponentSpared

        val updatedGladiators = _uiState.value.gladiators.map { glad ->
            if (glad.id == engine.playerGladiator.id) {
                if (playerDied) {
                    val deadMemorial = FallenGladiatorMemorial(
                        id = "mem_player_${glad.id}_${System.currentTimeMillis()}",
                        name = glad.name,
                        nickname = "The Unbroken",
                        gladiatorClass = glad.gladiatorClass,
                        origin = glad.origin,
                        ludusAffiliation = "Ludus Valerius",
                        recordSummary = "${glad.careerStats.wins}G - ${glad.careerStats.losses + 1}M",
                        kills = glad.careerStats.kills,
                        diedOnDay = currentDominus.dayNumber,
                        arenaName = result.arenaName,
                        killedBy = result.opponentName,
                        causeOfDeath = "Arenada infaz (Pollice Verso)",
                        wasChampion = false,
                        yearAUC = currentDominus.yearAUC
                    )
                    memorials.add(0, deadMemorial)

                    glad.copy(
                        status = GladiatorStatus.DEAD,
                        isAlive = false,
                        deathDay = currentDominus.dayNumber,
                        killedBy = result.opponentName,
                        causeOfDeath = "Arenada infaz (Pollice Verso)",
                        deathArena = result.arenaName,
                        condition = glad.condition.copy(health = 0, stamina = 0)
                    )
                } else {
                    val staminaDrain = if (isUnderground) 35 else 0
                    val cond = glad.condition.copy(
                        health = max(10, engine.playerState.currentHealth.toInt()),
                        stamina = max(5, engine.playerState.currentStamina.toInt() - staminaDrain),
                        morale = min(100, max(20, glad.condition.morale + (if (result.playerWon) 15 else -20)))
                    )
                    val updatedStats = glad.careerStats.copy(
                        fights = glad.careerStats.fights + 1,
                        wins = glad.careerStats.wins + (if (result.playerWon) 1 else 0),
                        losses = glad.careerStats.losses + (if (!result.playerWon) 1 else 0),
                        crowdFavor = min(100, glad.careerStats.crowdFavor + result.crowdFavorDelta),
                        denariiEarned = glad.careerStats.denariiEarned + goldReward
                    )
                    val injuries = glad.injuries.toMutableList()
                    if (result.injurySuffered != null) {
                        injuries.add(result.injurySuffered)
                    }
                    glad.copy(condition = cond, careerStats = updatedStats, injuries = injuries)
                }
            } else glad
        }

        // Opponent Fatality & Boss State Check
        val opponentDied = result.playerWon && !result.opponentSpared
        val updatedBossStates = _uiState.value.bossStates.toMutableMap()
        val isBossFight = activeOpponent != null && (
            activeOpponent.undergroundBossState != null ||
            activeOpponent.nickname in listOf("The Wolf", "Black Sand", "The Syrian", "Gravedigger", "Red Knife") ||
            activeOpponent.id.startsWith("boss_")
        )

        val updatedFighters = _uiState.value.persistentFighters.map { fighter ->
            if (fighter.id == activeOpponent?.id) {
                val finalBossState = if (opponentDied) {
                    BossState.DEAD
                } else if (result.playerWon && isBossFight) {
                    BossState.DEFEATED
                } else fighter.undergroundBossState

                if (isBossFight && finalBossState != null) {
                    updatedBossStates[fighter.id] = finalBossState
                }

                if (opponentDied) {
                    val memorial = FallenGladiatorMemorial(
                        id = "mem_${fighter.id}_${System.currentTimeMillis()}",
                        name = fighter.name,
                        nickname = fighter.nickname,
                        gladiatorClass = fighter.gladiatorClass,
                        origin = fighter.origin,
                        ludusAffiliation = fighter.ludusAffiliation,
                        recordSummary = "${fighter.wins}G - ${fighter.losses + 1}M",
                        kills = fighter.kills,
                        diedOnDay = currentDominus.dayNumber,
                        arenaName = result.arenaName,
                        killedBy = engine.playerGladiator.name,
                        causeOfDeath = "Arenada Pollice Verso infazı",
                        wasChampion = fighter.isChampion,
                        yearAUC = currentDominus.yearAUC
                    )
                    memorials.add(0, memorial)

                    fighter.copy(
                        isAlive = false,
                        deathDay = currentDominus.dayNumber,
                        killedBy = engine.playerGladiator.name,
                        causeOfDeath = "Arenada Pollice Verso infazı",
                        deathArena = result.arenaName,
                        isChampion = false, // Title becomes vacant if champion dies!
                        losses = fighter.losses + 1,
                        currentWinStreak = 0,
                        undergroundBossState = BossState.DEAD
                    )
                } else {
                    val newWins = fighter.wins + (if (!result.playerWon) 1 else 0)
                    val newLosses = fighter.losses + (if (result.playerWon) 1 else 0)
                    val newStreak = if (!result.playerWon) fighter.currentWinStreak + 1 else 0
                    val form = fighter.recentForm.toMutableList()
                    form.add(0, if (result.playerWon) "L" else "W")
                    if (form.size > 5) form.removeAt(5)

                    val newPrestigeFighter = if (result.playerWon) max(100, fighter.prestige - 100) else fighter.prestige + 200
                    val newSpared = fighter.spared + (if (result.opponentSpared) 1 else 0)

                    fighter.copy(
                        wins = newWins,
                        losses = newLosses,
                        currentWinStreak = newStreak,
                        recentForm = form,
                        prestige = newPrestigeFighter,
                        spared = newSpared,
                        lastFightDay = if (isUnderground) currentDominus.dayNumber else fighter.lastFightDay,
                        fightCountAgainstPlayer = fighter.fightCountAgainstPlayer + 1,
                        undergroundBossState = finalBossState
                    )
                }
            } else fighter
        }

        // Mark underground fight completed if active
        val updatedUndergroundFights = _uiState.value.undergroundFights.map { fight ->
            if (isUnderground && (fight.id == undergroundFight?.id || fight.opponentFighter.id == activeOpponent?.id)) {
                fight.copy(isCompleted = true)
            } else fight
        }

        // Add Chronicles
        val updatedChronicles = _uiState.value.chronicles.toMutableList()
        val chronicleText = if (result.playerWon) {
            if (opponentDied) {
                "${engine.playerGladiator.name}, ${result.arenaName}'nda rakibi ${result.opponentName}'yi infaz etti (Pollice Verso)! Zafer ödülü: $goldReward Denarii."
            } else {
                "${engine.playerGladiator.name}, ${result.arenaName}'nda ${result.opponentName}'yi dize getirdi. Canı bağışlandı (Missio). Zafer ödülü: $goldReward Denarii."
            }
        } else {
            if (playerDied) {
                "${engine.playerGladiator.name}, ${result.arenaName}'nda ${result.opponentName} tarafından katledildi. Ludus derin bir yasa gömüldü."
            } else {
                "${engine.playerGladiator.name}, ${result.arenaName}'nda mağlup oldu fakat hakem canını bağışladı."
            }
        }
        updatedChronicles.add(0, ChronicleEntry(currentDominus.yearAUC, if (result.playerWon) "Arenada Zafer!" else "Mağlubiyet", chronicleText, result.playerWon))

        if (opponentDied && activeOpponent?.isChampion == true) {
            updatedChronicles.add(0, ChronicleEntry(currentDominus.yearAUC, "ŞAMPİYON KATLEDİLDİ", "${activeOpponent.fullDisplayName} öldü! ${activeOpponent.currentArena.venueName} Şampiyonluk Unvanı BOŞTA kaldı!", isGlory = true))
        }
        if (undergroundDiscoveryMessage != null) {
            updatedChronicles.add(0, ChronicleEntry(currentDominus.yearAUC, "Yeraltı Baskını", undergroundDiscoveryMessage, isGlory = false))
        }

        // Record permanent career match history entry
        val careerRecord = CareerMatchRecord(
            matchId = "rec_${System.currentTimeMillis()}",
            day = currentDominus.dayNumber,
            arenaName = result.arenaName,
            playerFighterName = engine.playerGladiator.name,
            opponentName = result.opponentName,
            opponentNickname = activeOpponent?.nickname ?: "",
            opponentLudus = activeOpponent?.ludusAffiliation ?: "Roman Lanista",
            opponentClass = activeOpponent?.gladiatorClass ?: engine.opponentGladiator.gladiatorClass,
            matchType = _uiState.value.selectedMatchType,
            won = result.playerWon,
            durationSeconds = result.durationSeconds,
            hitsLanded = result.playerHits,
            blocksExecuted = result.playerBlocks,
            criticalsLanded = result.playerCriticals,
            goldEarned = goldReward,
            prestigeGained = prestigeReward,
            crowdReaction = if (result.crowdFavorDelta >= 0) "Coşkulu Tezahürat" else "Yuhalamalar",
            injurySuffered = result.injurySuffered?.name,
            opponentOutcome = if (result.playerWon) (if (result.opponentSpared) "Bağışlandı (Missio)" else "İnfaz Edildi (Pollice Verso)") else (if (playerDied) "Ölümcül Vuruş Yaptı" else "Zafer Kazandı")
        )
        val updatedCareerHistory = _uiState.value.careerMatchHistory.toMutableList().apply {
            add(0, careerRecord)
        }

        // Update Rivalry if applicable
        val updatedRivalries = _uiState.value.activeRivalries.map { rivalry ->
            if (rivalry.opponentId == activeOpponent?.id) {
                val newPlayerWins = rivalry.playerWins + (if (result.playerWon) 1 else 0)
                val newOppWins = rivalry.opponentWins + (if (!result.playerWon) 1 else 0)
                val newAnimosity = min(100, rivalry.animosityScore + 15)
                rivalry.copy(
                    boutsFought = rivalry.boutsFought + 1,
                    playerWins = newPlayerWins,
                    opponentWins = newOppWins,
                    animosityScore = newAnimosity,
                    lastEncounterDateText = "Gün ${currentDominus.dayNumber}",
                    rematchDemanded = !result.playerWon && !opponentDied
                )
            } else rivalry
        }

        // Update Calendar: mark completed if official bout
        val updatedCalendar = _uiState.value.arenaCalendar.map { bout ->
            if (bout.day == currentDominus.dayNumber && bout.isPlayerMatch && !bout.isCompleted) {
                bout.copy(
                    isCompleted = true,
                    winnerName = if (result.playerWon) engine.playerGladiator.name else result.opponentName,
                    resultSummary = if (result.playerWon) "${engine.playerGladiator.name} kazandı (${if (opponentDied) "İnfaz" else "Missio"})." else "${result.opponentName} kazandı."
                )
            } else bout
        }

        // Find next upcoming player bout on the calendar
        val nextPlayerBout = updatedCalendar.firstOrNull { it.isPlayerMatch && !it.isCompleted && it.day > currentDominus.dayNumber }
        val nextOpponent = if (nextPlayerBout != null) {
            updatedFighters.find { (it.id == nextPlayerBout.fighter1Id || it.id == nextPlayerBout.fighter2Id) && it.isAlive }
                ?: updatedFighters.firstOrNull { it.isAlive }
                ?: updatedFighters.first()
        } else activeOpponent ?: updatedFighters.first()

        val nextScheduledMatch = if (nextPlayerBout != null) {
            ScheduledMatch(
                id = nextPlayerBout.id,
                arenaCity = nextPlayerBout.venueId.city,
                arenaName = nextPlayerBout.venueId.venueName,
                matchDateText = "Gün ${nextPlayerBout.day} • ${nextPlayerBout.matchType.title}",
                opponentLudus = nextOpponent.ludusAffiliation,
                opponentGladiator = nextOpponent.toGladiator(),
                basePrizeGold = 2400,
                basePrestige = 280,
                matchType = nextPlayerBout.matchType.title
            )
        } else {
            _uiState.value.scheduledMatch
        }

        // Persist combat durability wear back to player's gladiator loadout
        val currentLoadouts = _uiState.value.gladiatorLoadouts.toMutableMap()
        engine.playerLoadout?.let { updatedLoadout ->
            currentLoadouts[engine.playerGladiator.id] = updatedLoadout
        }

        // Add World Memory entry for combat
        val newMemories = _uiState.value.worldMemory.toMutableList()
        val memType = if (result.playerWon) {
            if (opponentDied) MemoryEventType.PLAYER_KILLED_GLADIATOR
            else if (isUnderground && isBossFight) MemoryEventType.PLAYER_DEFEATED_BOSS
            else MemoryEventType.ARENA_EVENT
        } else {
            if (playerDied) MemoryEventType.PLAYER_LOST_CHAMPION
            else MemoryEventType.PLAYER_LOST_MATCH
        }
        newMemories.add(0, MemoryEntry(
            id = "mem_combat_${System.currentTimeMillis()}",
            eventType = memType,
            date = currentDominus.dayNumber,
            location = result.arenaName,
            participantIds = listOf(engine.playerGladiator.id, activeOpponent?.id ?: "opponent"),
            causeDescription = chronicleText,
            hiddenFacts = if (isUnderground) "Subura yeraltı çukurunda gerçekleşti." else null,
            impactTags = listOf(if (result.playerWon) "Zafer" else "Mağlubiyet", result.arenaName),
            importance = if (opponentDied || playerDied || isBossFight) EventImportance.MAJOR else EventImportance.MINOR
        ))

        // Update Character Memory if opponent has a memory profile
        val updatedCharMemories = _uiState.value.characterMemories.toMutableMap()
        activeOpponent?.let { opp ->
            val existingMem = updatedCharMemories[opp.id]
            if (existingMem != null) {
                val hatredDelta = if (result.playerWon) 15 else -5
                val fearDelta = if (result.playerWon) 20 else -10
                val respectDelta = if (result.playerWon) 10 else -10
                existingMem.hatred = (existingMem.hatred + hatredDelta).coerceIn(0, 100)
                existingMem.fear = (existingMem.fear + fearDelta).coerceIn(0, 100)
                existingMem.respect = (existingMem.respect + respectDelta).coerceIn(0, 100)
                existingMem.memorableEvents.add(
                    CharacterMemoryEvent(
                        day = currentDominus.dayNumber,
                        deedDescription = if (result.playerWon) "Arenada mağlup edildi." else "Arenada zafer kazandı.",
                        sentimentDelta = if (result.playerWon) -15 else 10,
                        emotionalCategory = if (result.playerWon) "Husumet" else "Kibir"
                    )
                )
            }
        }

        _uiState.value = _uiState.value.copy(
            dominus = currentDominus.copy(
                denarii = newGold,
                prestige = newPrestige,
                popularity = newPopularity
            ),
            gladiators = updatedGladiators,
            persistentFighters = updatedFighters,
            fallenGladiators = memorials,
            activeRivalries = updatedRivalries,
            arenaCalendar = updatedCalendar,
            scheduledMatch = nextScheduledMatch,
            careerMatchHistory = updatedCareerHistory,
            chronicles = updatedChronicles,
            worldMemory = newMemories,
            characterMemories = updatedCharMemories,
            lastMatchResult = result,
            isCombatRunning = false,
            isUndergroundActive = false,
            selectedUndergroundFight = null,
            undergroundFights = updatedUndergroundFights,
            bossStates = updatedBossStates,
            gladiatorLoadouts = currentLoadouts,
            fightDayPhase = FightDayPhase.IDLE,
            currentScreen = ActiveScreen.POST_MATCH,
            statusMessage = if (playerDied) "⚠ Gladyatörünüz arenanın kumlarında can verdi! (STATUS: DEAD)" else if (opponentDied) "⚔ Rakip ${activeOpponent?.name} infaz edildi ve Düşenler Anıtı'na kaydedildi!" else "Müsabaka tamamlandı."
        )
    }

    fun upgradeFacility(facilityId: String) {
        val facility = _uiState.value.facilities.find { it.id == facilityId } ?: return
        if (_uiState.value.dominus.denarii < facility.upgradeCost) {
            _uiState.value = _uiState.value.copy(statusMessage = "Yetersiz Denarii!")
            return
        }

        val updatedFacilities = _uiState.value.facilities.map {
            if (it.id == facilityId && it.level < it.maxLevel) {
                it.copy(
                    level = it.level + 1,
                    upgradeCost = (it.upgradeCost * 1.5).toInt(),
                    currentBonus = it.nextBonus,
                    nextBonus = "Seviye ${it.level + 2} İleri Verim"
                )
            } else it
        }

        _uiState.value = _uiState.value.copy(
            dominus = _uiState.value.dominus.copy(
                denarii = _uiState.value.dominus.denarii - facility.upgradeCost,
                prestige = _uiState.value.dominus.prestige + 50
            ),
            facilities = updatedFacilities,
            statusMessage = "${facility.name} Seviye ${facility.level + 1}'e yükseltildi!"
        )
    }

    fun purchaseMarketGladiator(marketGladId: String) {
        val glad = _uiState.value.marketGladiators.find { it.id == marketGladId } ?: return
        val cost = 1500
        if (_uiState.value.dominus.denarii < cost) {
            _uiState.value = _uiState.value.copy(statusMessage = "Yetersiz Denarii! (Gereken: $cost)")
            return
        }

        val updatedGladiators = _uiState.value.gladiators.toMutableList().apply { add(glad) }
        val updatedMarket = _uiState.value.marketGladiators.filter { it.id != marketGladId }

        val currentLoadouts = _uiState.value.gladiatorLoadouts.toMutableMap()
        currentLoadouts[glad.id] = EquipmentEngine.createInitialLoadoutForGladiator(glad)

        val currentPrefs = _uiState.value.gladiatorPreferences.toMutableMap()
        currentPrefs[glad.id] = GladiatorEquipmentPreference(
            gladiatorId = glad.id,
            favoredWeaponType = when (glad.gladiatorClass) {
                GladiatorClass.THRAEX -> EquipmentType.SICA
                GladiatorClass.RETIARIUS -> EquipmentType.TRIDENT
                GladiatorClass.HOPLOMACHUS -> EquipmentType.SPEAR
                else -> EquipmentType.GLADIUS
            },
            hatesHeavyArmor = glad.gladiatorClass == GladiatorClass.RETIARIUS || glad.gladiatorClass == GladiatorClass.LIGHT_DUELIST,
            demandsShield = glad.gladiatorClass == GladiatorClass.MURMILLO || glad.gladiatorClass == GladiatorClass.THRAEX
        )

        _uiState.value = _uiState.value.copy(
            dominus = _uiState.value.dominus.copy(
                denarii = _uiState.value.dominus.denarii - cost
            ),
            gladiators = updatedGladiators,
            marketGladiators = updatedMarket,
            gladiatorLoadouts = currentLoadouts,
            gladiatorPreferences = currentPrefs,
            statusMessage = "${glad.name} ludusunuza katıldı! Başlangıç teçhizatı kuşandırıldı."
        )
    }

    private fun generateRecruitmentPool(day: Int): List<Gladiator> {
        val names = listOf(
            "Marcus", "Drusus", "Felix", "Aulus", "Decimus", "Vettius", "Maximus", "Gannicus",
            "Crixus", "Oenomaus", "Castus", "Spartacus", "Atticus", "Bato", "Marcellus"
        )
        val origins = listOf(Origin.THRAX, Origin.GAUL, Origin.GERMANIA, Origin.HISPANIA, Origin.NUMIDIA, Origin.GREECE, Origin.SYRIA)
        val classes = listOf(GladiatorClass.MURMILLO, GladiatorClass.THRAEX, GladiatorClass.RETIARIUS, GladiatorClass.SECUTOR, GladiatorClass.HOPLOMACHUS, GladiatorClass.DIMACHAERUS)

        val rng = kotlin.random.Random(day * 733L)
        val count = rng.nextInt(3, 5)

        return (1..count).map { idx ->
            val name = names.shuffled(rng).first()
            val origin = origins.shuffled(rng).first()
            val gladClass = classes.shuffled(rng).first()
            val age = rng.nextInt(19, 30)
            val strength = rng.nextInt(12, 17)
            val speed = rng.nextInt(12, 17)

            Gladiator(
                id = "recruit_${day}_${idx}_${name.lowercase()}",
                name = name,
                gladiatorClass = gladClass,
                origin = origin,
                status = if (rng.nextBoolean()) GladiatorStatus.SLAVE else GladiatorStatus.AUCTORATUS,
                age = age,
                personality = Personality.values().random(rng),
                monthlyWage = 40 + rng.nextInt(10, 50),
                rank = 1,
                physicalStats = PhysicalStats(
                    strength = strength,
                    speed = speed,
                    agility = rng.nextInt(11, 16),
                    endurance = rng.nextInt(12, 17),
                    reflex = rng.nextInt(11, 16),
                    painTolerance = rng.nextInt(12, 18),
                    heightCm = 175 + rng.nextInt(0, 18),
                    weightKg = 75 + rng.nextInt(0, 22)
                ),
                attributes = GladiatorAttributes(
                    strength = strength,
                    speed = speed,
                    agility = rng.nextInt(11, 16),
                    endurance = rng.nextInt(12, 17),
                    reflex = rng.nextInt(11, 16),
                    painTolerance = rng.nextInt(12, 18),
                    swordsmanship = rng.nextInt(11, 16),
                    shieldSkill = rng.nextInt(10, 15),
                    grappling = rng.nextInt(10, 15),
                    footwork = rng.nextInt(11, 15),
                    counterAttack = rng.nextInt(11, 15),
                    courage = rng.nextInt(12, 17),
                    discipline = rng.nextInt(10, 16),
                    composure = rng.nextInt(10, 15)
                )
            )
        }
    }

    fun saveGame(file: java.io.File? = null): Boolean {
        val targetFile = file ?: java.io.File("ludus_save.json")
        return LudusSaveManager.saveGameToFile(targetFile, _uiState.value)
    }

    fun loadGame(file: java.io.File? = null): Boolean {
        val targetFile = file ?: java.io.File("ludus_save.json")
        val loaded = LudusSaveManager.loadGameFromFile(targetFile, _uiState.value) ?: return false
        _uiState.value = loaded
        return true
    }

    fun updateTrainingPlan(gladiatorId: String, focus: String, diet: String) {
        val updatedGladiators = _uiState.value.gladiators.map {
            if (it.id == gladiatorId) {
                it.copy(trainingFocus = focus, diet = diet)
            } else it
        }
        _uiState.value = _uiState.value.copy(
            gladiators = updatedGladiators,
            selectedGladiator = updatedGladiators.find { it.id == gladiatorId } ?: _uiState.value.selectedGladiator,
            statusMessage = "Antrenman ve diyet planı güncellendi."
        )
    }

    fun treatInjury(gladiatorId: String, injuryId: String) {
        val cost = 80
        if (_uiState.value.dominus.denarii < cost) {
            _uiState.value = _uiState.value.copy(statusMessage = "Tedavi için yetersiz Denarii!")
            return
        }

        val updatedGladiators = _uiState.value.gladiators.map { glad ->
            if (glad.id == gladiatorId) {
                val updatedInjuries = glad.injuries.mapNotNull {
                    if (it.id == injuryId) {
                        if (it.daysRemaining <= 2) null else it.copy(daysRemaining = it.daysRemaining - 2)
                    } else it
                }.toMutableList()
                glad.copy(injuries = updatedInjuries)
            } else glad
        }

        _uiState.value = _uiState.value.copy(
            dominus = _uiState.value.dominus.copy(denarii = _uiState.value.dominus.denarii - cost),
            gladiators = updatedGladiators,
            selectedGladiator = updatedGladiators.find { it.id == gladiatorId } ?: _uiState.value.selectedGladiator,
            statusMessage = "Medicus Lucius tedaviyi uyguladı. İyileşme hızlandı."
        )
    }

    // =========================================================
    // POLITICAL SIMULATION ACTIONS
    // =========================================================

    fun resolvePoliticalChoice(choice: PoliticalChoice) {
        val currentDominus = _uiState.value.dominus
        val currentResources = _uiState.value.politicalResources

        // Check costs
        if (choice.requiredGold > 0 && currentDominus.denarii < choice.requiredGold) {
            _uiState.value = _uiState.value.copy(statusMessage = "Yetersiz Denarii! Bu siyasi kararı uygulayamazsınız.")
            return
        }
        if (choice.requiredFavor > 0 && currentResources.politicalFavor < choice.requiredFavor) {
            _uiState.value = _uiState.value.copy(statusMessage = "Yetersiz Siyasi Lütuf! Daha fazla nüfuz veya senatör borcu gerekiyor.")
            return
        }
        if (choice.requiredInfluence > 0 && currentResources.influence < choice.requiredInfluence) {
            _uiState.value = _uiState.value.copy(statusMessage = "Yetersiz Nüfuz (Auctoritas)! Gerekli: ${choice.requiredInfluence}")
            return
        }

        // Apply Gold & Prestige
        val newGold = max(0, currentDominus.denarii + choice.goldDelta)
        val newPrestige = max(0, currentDominus.prestige + choice.prestigeDelta)

        // Apply Political Resources
        val newResources = currentResources.copy(
            politicalFavor = max(0, currentResources.politicalFavor + choice.favorDelta),
            influence = min(100, max(0, currentResources.influence + choice.influenceDelta)),
            reputation = min(100, max(0, currentResources.reputation + choice.reputationDelta))
        )

        // Apply Faction Opinions
        val updatedFactions = _uiState.value.politicalFactions.map { faction ->
            val delta = choice.factionOpinionsDelta[faction.id] ?: 0
            if (delta != 0) {
                faction.copy(opinionOfPlayer = min(100, max(-100, faction.opinionOfPlayer + delta)))
            } else faction
        }

        // Apply NPC Relationship
        val updatedCharacters = _uiState.value.politicalCharacters.map { character ->
            if (character.id == choice.targetNpcId) {
                character.copy(relationshipWithPlayer = min(100, max(-100, character.relationshipWithPlayer + choice.npcRelationshipDelta)))
            } else character
        }

        // Handle Scandal Trigger
        val updatedScandals = _uiState.value.activeScandals.toMutableList()
        if (choice.triggersScandal != null) {
            updatedScandals.add(0, choice.triggersScandal)
        }

        // Add Chronicle Entry
        val updatedChronicles = _uiState.value.chronicles.toMutableList().apply {
            add(
                0,
                ChronicleEntry(
                    yearAUC = currentDominus.yearAUC,
                    title = "Siyasi Karar: ${choice.label}",
                    description = choice.consequenceNarrative,
                    isGlory = choice.prestigeDelta >= 0 && choice.reputationDelta >= 0
                )
            )
        }

        _uiState.value = _uiState.value.copy(
            dominus = currentDominus.copy(denarii = newGold, prestige = newPrestige),
            politicalResources = newResources,
            politicalFactions = updatedFactions,
            politicalCharacters = updatedCharacters,
            activeScandals = updatedScandals,
            chronicles = updatedChronicles,
            activePoliticalEvent = null,
            statusMessage = choice.consequenceNarrative
        )
    }

    fun dismissPoliticalEvent() {
        _uiState.value = _uiState.value.copy(activePoliticalEvent = null)
    }

    fun selectPoliticalNpc(character: PoliticalCharacter?) {
        _uiState.value = _uiState.value.copy(selectedPoliticalNpc = character)
    }

    fun selectPoliticalFaction(faction: PoliticalFaction?) {
        _uiState.value = _uiState.value.copy(selectedPoliticalFaction = faction)
    }

    fun executePoliticalInteraction(npcId: String, interaction: PoliticalInteractionType, secretId: String? = null) {
        val npc = _uiState.value.politicalCharacters.find { it.id == npcId } ?: return
        val currentDominus = _uiState.value.dominus
        val currentResources = _uiState.value.politicalResources

        when (interaction) {
            PoliticalInteractionType.TALK_FLATTERY -> {
                val updatedChars = _uiState.value.politicalCharacters.map {
                    if (it.id == npcId) it.copy(relationshipWithPlayer = min(100, it.relationshipWithPlayer + 5))
                    else it
                }
                _uiState.value = _uiState.value.copy(
                    politicalCharacters = updatedChars,
                    selectedPoliticalNpc = updatedChars.find { it.id == npcId },
                    statusMessage = "${npc.name} ile Forum revaklarında sohbet ettiniz. Size karşı daha samimi."
                )
            }

            PoliticalInteractionType.GIVE_GOLD_GIFT -> {
                val cost = 1000
                if (currentDominus.denarii < cost) {
                    _uiState.value = _uiState.value.copy(statusMessage = "Hediye sunmak için 1,000 Denarii gerekiyor!")
                    return
                }
                val updatedChars = _uiState.value.politicalCharacters.map {
                    if (it.id == npcId) it.copy(
                        relationshipWithPlayer = min(100, it.relationshipWithPlayer + 20),
                        wealth = it.wealth + cost
                    ) else it
                }
                _uiState.value = _uiState.value.copy(
                    dominus = currentDominus.copy(denarii = currentDominus.denarii - cost),
                    politicalCharacters = updatedChars,
                    politicalResources = currentResources.copy(reputation = min(100, currentResources.reputation + 4)),
                    selectedPoliticalNpc = updatedChars.find { it.id == npcId },
                    statusMessage = "${npc.name}'a gümüş kupa ve Falernian şarabı hediye ettiniz (+20 İlişki)."
                )
            }

            PoliticalInteractionType.BRIBE_OFFICIAL -> {
                val cost = 2500
                if (currentDominus.denarii < cost) {
                    _uiState.value = _uiState.value.copy(statusMessage = "Rüşvet için 2,500 Denarii gerekiyor!")
                    return
                }
                val updatedChars = _uiState.value.politicalCharacters.map {
                    if (it.id == npcId) it.copy(
                        relationshipWithPlayer = min(100, it.relationshipWithPlayer + 30),
                        favorsOwedToPlayer = it.favorsOwedToPlayer + 1,
                        wealth = it.wealth + cost
                    ) else it
                }
                _uiState.value = _uiState.value.copy(
                    dominus = currentDominus.copy(denarii = currentDominus.denarii - cost),
                    politicalCharacters = updatedChars,
                    politicalResources = currentResources.copy(
                        politicalFavor = currentResources.politicalFavor + 1,
                        reputation = max(0, currentResources.reputation - 5)
                    ),
                    selectedPoliticalNpc = updatedChars.find { it.id == npcId },
                    statusMessage = "${npc.name}'a gizli altın kesesi verildi. Artık size 1 Lütuf borçlu!"
                )
            }

            PoliticalInteractionType.REQUEST_FAVOR_CASH -> {
                if (npc.favorsOwedToPlayer <= 0) {
                    _uiState.value = _uiState.value.copy(statusMessage = "${npc.name} size hiçbir lütuf borçlu değil!")
                    return
                }
                val cashGranted = 1500
                val updatedChars = _uiState.value.politicalCharacters.map {
                    if (it.id == npcId) it.copy(favorsOwedToPlayer = it.favorsOwedToPlayer - 1)
                    else it
                }
                _uiState.value = _uiState.value.copy(
                    dominus = currentDominus.copy(denarii = currentDominus.denarii + cashGranted),
                    politicalCharacters = updatedChars,
                    selectedPoliticalNpc = updatedChars.find { it.id == npcId },
                    statusMessage = "${npc.name} lütuf borcunu ödedi: Kasanıza $cashGranted Denarii aktarıldı."
                )
            }

            PoliticalInteractionType.REQUEST_FAVOR_LICENSE -> {
                if (npc.favorsOwedToPlayer <= 0) {
                    _uiState.value = _uiState.value.copy(statusMessage = "${npc.name} size lütuf borçlu değil!")
                    return
                }
                val updatedChars = _uiState.value.politicalCharacters.map {
                    if (it.id == npcId) it.copy(favorsOwedToPlayer = it.favorsOwedToPlayer - 1)
                    else it
                }
                val updatedFactions = _uiState.value.politicalFactions.map {
                    if (it.id == PoliticalFactionId.ARENA_OFFICIALS) it.copy(opinionOfPlayer = min(100, it.opinionOfPlayer + 25))
                    else it
                }
                _uiState.value = _uiState.value.copy(
                    politicalCharacters = updatedChars,
                    politicalFactions = updatedFactions,
                    selectedPoliticalNpc = updatedChars.find { it.id == npcId },
                    statusMessage = "${npc.name} araya girdi: Arena Yargıçları ile ilişkiler +25 düzeldi ve lisans engeli kalktı."
                )
            }

            PoliticalInteractionType.REQUEST_FAVOR_SABOTAGE -> {
                if (npc.favorsOwedToPlayer <= 0) {
                    _uiState.value = _uiState.value.copy(statusMessage = "${npc.name} size lütuf borçlu değil!")
                    return
                }
                val updatedChars = _uiState.value.politicalCharacters.map {
                    if (it.id == npcId) it.copy(favorsOwedToPlayer = it.favorsOwedToPlayer - 1)
                    else it
                }
                val updatedFactions = _uiState.value.politicalFactions.map {
                    if (it.id == PoliticalFactionId.RIVAL_LUDUSES) it.copy(opinionOfPlayer = max(-100, it.opinionOfPlayer - 20), influence = max(10, it.influence - 10))
                    else it
                }
                _uiState.value = _uiState.value.copy(
                    politicalCharacters = updatedChars,
                    politicalFactions = updatedFactions,
                    selectedPoliticalNpc = updatedChars.find { it.id == npcId },
                    statusMessage = "${npc.name}'in baskısıyla rakip lanistaların nüfuzu kırıldı!"
                )
            }

            PoliticalInteractionType.ASSIGN_GLADIATOR_ESCORT -> {
                val updatedChars = _uiState.value.politicalCharacters.map {
                    if (it.id == npcId) it.copy(
                        relationshipWithPlayer = min(100, it.relationshipWithPlayer + 30),
                        favorsOwedToPlayer = it.favorsOwedToPlayer + 1
                    ) else it
                }
                _uiState.value = _uiState.value.copy(
                    politicalCharacters = updatedChars,
                    politicalResources = currentResources.copy(
                        politicalFavor = currentResources.politicalFavor + 1,
                        influence = min(100, currentResources.influence + 5)
                    ),
                    selectedPoliticalNpc = updatedChars.find { it.id == npcId },
                    statusMessage = "${npc.name}'a Capua sokaklarında korumalık yapacak elit bir gladyatör tahsis edildi (+30 İlişki, +1 Lütuf)."
                )
            }

            PoliticalInteractionType.ASK_PATRONAGE -> {
                val updatedChars = _uiState.value.politicalCharacters.map {
                    if (it.id == npcId) it.copy(
                        isPatron = true,
                        relationshipWithPlayer = min(100, it.relationshipWithPlayer + 25)
                    ) else if (it.id == _uiState.value.activePatronId) it.copy(isPatron = false)
                    else it
                }
                _uiState.value = _uiState.value.copy(
                    activePatronId = npcId,
                    politicalCharacters = updatedChars,
                    selectedPoliticalNpc = updatedChars.find { it.id == npcId },
                    statusMessage = "${npc.name} resmi haminiz (Patronus) oldu! Aylık düzenli ödenek ve siyasi koruma devrede."
                )
            }

            PoliticalInteractionType.RENOUNCE_PATRONAGE -> {
                val updatedChars = _uiState.value.politicalCharacters.map {
                    if (it.id == npcId) it.copy(
                        isPatron = false,
                        relationshipWithPlayer = max(-100, it.relationshipWithPlayer - 35)
                    ) else it
                }
                _uiState.value = _uiState.value.copy(
                    activePatronId = null,
                    politicalCharacters = updatedChars,
                    selectedPoliticalNpc = updatedChars.find { it.id == npcId },
                    statusMessage = "${npc.name} ile hamilik bağını bozdunuz. Kendisi bu ayrılığa çok öfkelendi (-35 İlişki)!"
                )
            }

            PoliticalInteractionType.INVESTIGATE_NPC -> {
                val cost = 500
                if (currentDominus.denarii < cost) {
                    _uiState.value = _uiState.value.copy(statusMessage = "Casus tutmak için 500 Denarii gerekiyor!")
                    return
                }
                // Check if target has secrets
                val targetSecret = npc.knownSecrets.firstOrNull { s -> _uiState.value.playerSecrets.none { it.id == s.id } }
                if (targetSecret != null) {
                    val updatedSecrets = _uiState.value.playerSecrets.toMutableList().apply { add(0, targetSecret) }
                    _uiState.value = _uiState.value.copy(
                        dominus = currentDominus.copy(denarii = currentDominus.denarii - cost),
                        playerSecrets = updatedSecrets,
                        politicalResources = currentResources.copy(discoveredSecretsCount = updatedSecrets.size),
                        statusMessage = "Casuslarınız ${npc.name} hakkında kritik bir sır ele geçirdi: '${targetSecret.title}'!"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        dominus = currentDominus.copy(denarii = currentDominus.denarii - cost),
                        statusMessage = "Casuslar araştırdı ancak ${npc.name} hakkında yeni bir şantaj belgesi bulamadı."
                    )
                }
            }

            PoliticalInteractionType.BLACKMAIL_WITH_SECRET -> {
                val secret = _uiState.value.playerSecrets.find { it.id == secretId }
                    ?: _uiState.value.playerSecrets.find { it.targetNpcId == npcId }
                if (secret == null) {
                    _uiState.value = _uiState.value.copy(statusMessage = "${npc.name} hakkında elinizde geçerli bir şantaj belgesi yok!")
                    return
                }
                val updatedChars = _uiState.value.politicalCharacters.map {
                    if (it.id == npcId) it.copy(
                        relationshipWithPlayer = max(-100, it.relationshipWithPlayer - 30),
                        favorsOwedToPlayer = it.favorsOwedToPlayer + 2
                    ) else it
                }
                _uiState.value = _uiState.value.copy(
                    politicalCharacters = updatedChars,
                    politicalResources = currentResources.copy(
                        politicalFavor = currentResources.politicalFavor + 2,
                        influence = min(100, currentResources.influence + 8)
                    ),
                    selectedPoliticalNpc = updatedChars.find { it.id == npcId },
                    statusMessage = "'${secret.title}' dosyası ile ${npc.name}'ı köşeye sıkıştırdınız! Size zorunlu 2 Lütuf borçlandı."
                )
            }

            PoliticalInteractionType.HOST_SENATE_BANQUET -> {
                val cost = 3000
                if (currentDominus.denarii < cost) {
                    _uiState.value = _uiState.value.copy(statusMessage = "Senato ziyafeti için 3,000 Denarii gerekiyor!")
                    return
                }
                val updatedChars = _uiState.value.politicalCharacters.map {
                    if (it.factionId == PoliticalFactionId.SENATORIAL_ELITE) {
                        it.copy(relationshipWithPlayer = min(100, it.relationshipWithPlayer + 15))
                    } else it
                }
                val updatedFactions = _uiState.value.politicalFactions.map {
                    if (it.id == PoliticalFactionId.SENATORIAL_ELITE) it.copy(opinionOfPlayer = min(100, it.opinionOfPlayer + 20))
                    else it
                }
                _uiState.value = _uiState.value.copy(
                    dominus = currentDominus.copy(denarii = currentDominus.denarii - cost, prestige = currentDominus.prestige + 250),
                    politicalCharacters = updatedChars,
                    politicalFactions = updatedFactions,
                    politicalResources = currentResources.copy(
                        influence = min(100, currentResources.influence + 12),
                        reputation = min(100, currentResources.reputation + 10)
                    ),
                    statusMessage = "Muhteşem bir Senato ziyafeti düzenlendi. Roma soyluları ludusunuzu takdirle alkışladı (+20 Senato Görüşü, +250 Şan)."
                )
            }
        }
    }

    fun suppressScandal(scandalId: String, useFavor: Boolean) {
        val scandal = _uiState.value.activeScandals.find { it.id == scandalId } ?: return
        val currentDominus = _uiState.value.dominus
        val currentResources = _uiState.value.politicalResources

        if (useFavor) {
            if (currentResources.politicalFavor < scandal.suppressionCostFavor) {
                _uiState.value = _uiState.value.copy(statusMessage = "Örtbas için ${scandal.suppressionCostFavor} Siyasi Lütuf gerekiyor!")
                return
            }
            val updatedScandals = _uiState.value.activeScandals.map {
                if (it.id == scandalId) it.copy(status = ScandalStatus.SUPPRESSED, publicAwareness = 0)
                else it
            }
            _uiState.value = _uiState.value.copy(
                politicalResources = currentResources.copy(politicalFavor = currentResources.politicalFavor - scandal.suppressionCostFavor),
                activeScandals = updatedScandals,
                statusMessage = "'${scandal.title}' siyasi lütuflarla senato arşivlerine gömüldü."
            )
        } else {
            if (currentDominus.denarii < scandal.suppressionCostGold) {
                _uiState.value = _uiState.value.copy(statusMessage = "Örtbas için ${scandal.suppressionCostGold} Denarii gerekiyor!")
                return
            }
            val updatedScandals = _uiState.value.activeScandals.map {
                if (it.id == scandalId) it.copy(status = ScandalStatus.SUPPRESSED, publicAwareness = 0)
                else it
            }
            _uiState.value = _uiState.value.copy(
                dominus = currentDominus.copy(denarii = currentDominus.denarii - scandal.suppressionCostGold),
                activeScandals = updatedScandals,
                statusMessage = "'${scandal.title}' rüşvet ve sus payı ile kapatıldı."
            )
        }
    }

    fun exposeSecret(secretId: String) {
        val secret = _uiState.value.playerSecrets.find { it.id == secretId } ?: return
        val targetChar = _uiState.value.politicalCharacters.find { it.id == secret.targetNpcId }

        val newScandal = PoliticalScandal(
            id = "scandal_exp_${System.currentTimeMillis()}",
            title = "${secret.targetName}: ${secret.title}",
            description = secret.description,
            involvedNpcIds = listOf(secret.targetNpcId),
            involvedFactionIds = if (targetChar != null) listOf(targetChar.factionId) else emptyList(),
            severity = secret.severity.leverageScore,
            publicAwareness = 80,
            evidenceLevel = 90,
            status = ScandalStatus.ACTIVE_HEADLINE,
            daysRemaining = 6
        )

        val updatedSecrets = _uiState.value.playerSecrets.map {
            if (it.id == secretId) it.copy(isExposed = true) else it
        }

        val updatedChars = _uiState.value.politicalCharacters.map {
            if (it.id == secret.targetNpcId) {
                it.copy(
                    influence = max(5, it.influence - 25),
                    relationshipWithPlayer = -90
                )
            } else it
        }

        val updatedChronicles = _uiState.value.chronicles.toMutableList().apply {
            add(
                0,
                ChronicleEntry(
                    yearAUC = _uiState.value.dominus.yearAUC,
                    title = "Skandal İfşası: ${secret.title}",
                    description = "Kamuoyuna sızdırdığınız belgelerle ${secret.targetName}'in siyasi kariyeri ağır yara aldı!",
                    isGlory = true
                )
            )
        }

        _uiState.value = _uiState.value.copy(
            playerSecrets = updatedSecrets,
            politicalCharacters = updatedChars,
            activeScandals = _uiState.value.activeScandals.toMutableList().apply { add(0, newScandal) },
            chronicles = updatedChronicles,
            dominus = _uiState.value.dominus.copy(prestige = _uiState.value.dominus.prestige + 300),
            politicalResources = _uiState.value.politicalResources.copy(
                influence = min(100, _uiState.value.politicalResources.influence + 10),
                reputation = min(100, _uiState.value.politicalResources.reputation + 15)
            ),
            statusMessage = "'${secret.title}' ifşa edildi! Halk ve senato ayağa kalktı (+300 Şan)."
        )
    }

    // -------------------------------------------------------------
    // EQUIPMENT & ROMAN MARKET SYSTEM ACTIONS
    // -------------------------------------------------------------
    fun selectMarketMerchant(merchantId: String) {
        val merchant = _uiState.value.merchants.find { it.id == merchantId }
        val firstItem = merchant?.inventory?.firstOrNull()
        _uiState.value = _uiState.value.copy(
            selectedMerchantId = merchantId,
            selectedMarketItem = firstItem ?: _uiState.value.selectedMarketItem
        )
    }

    fun setMarketTab(tab: MarketTab) {
        val defaultItem = when (tab) {
            MarketTab.MERCHANTS -> _uiState.value.merchants.find { it.id == _uiState.value.selectedMerchantId }?.inventory?.firstOrNull()
            MarketTab.USED_MARKET -> _uiState.value.merchants.find { it.id == "merch_hanno" }?.inventory?.firstOrNull()
            MarketTab.AUCTIONS -> _uiState.value.auctions.firstOrNull()?.item
            MarketTab.ARMORY_REPAIRS -> _uiState.value.ludusArmory.firstOrNull()
            MarketTab.COMMISSIONS -> null
        }
        _uiState.value = _uiState.value.copy(
            activeMarketTab = tab,
            selectedMarketItem = defaultItem ?: _uiState.value.selectedMarketItem
        )
    }

    fun setMarketCategory(category: EquipmentCategory?) {
        _uiState.value = _uiState.value.copy(selectedMarketCategory = category)
    }

    fun selectMarketItem(item: EquipmentItem?) {
        _uiState.value = _uiState.value.copy(selectedMarketItem = item)
    }

    fun setComparisonGladiator(gladiatorId: String) {
        _uiState.value = _uiState.value.copy(comparisonGladiatorId = gladiatorId)
    }

    fun buyMarketItem(merchantId: String, item: EquipmentItem, equipToGladiatorId: String? = null) {
        val merchant = _uiState.value.merchants.find { it.id == merchantId } ?: return
        val price = EquipmentEngine.calculateDynamicPrice(
            item = item,
            merchant = merchant,
            activeEvents = _uiState.value.equipmentMarketEvents,
            politicalFactions = _uiState.value.politicalFactions
        )

        if (_uiState.value.dominus.denarii < price) {
            _uiState.value = _uiState.value.copy(
                statusMessage = "Yetersiz Denarii! Bu teçhizat için $price Denarii gerekiyor (Kasanız: ${_uiState.value.dominus.denarii})."
            )
            return
        }

        // Deduct money & update merchant relationship
        val updatedDominus = _uiState.value.dominus.copy(
            denarii = _uiState.value.dominus.denarii - price,
            prestige = _uiState.value.dominus.prestige + (item.prestigeBonus / 2)
        )

        val updatedMerchants = _uiState.value.merchants.map { m ->
            if (m.id == merchantId) {
                val updatedInv = m.inventory.toMutableList()
                updatedInv.remove(item)
                m.copy(
                    inventory = updatedInv,
                    playerRelationship = min(100, m.playerRelationship + 3)
                )
            } else m
        }

        val armoryCopy = _uiState.value.ludusArmory.toMutableList()
        val loadoutsCopy = _uiState.value.gladiatorLoadouts.toMutableMap()
        var notificationText = "${item.name} satın alındı (-$price Denarii)."

        if (equipToGladiatorId != null) {
            val glad = _uiState.value.gladiators.find { it.id == equipToGladiatorId }
            if (glad != null) {
                val (isCompatible, reason) = checkClassCompatibility(glad.gladiatorClass, item)
                if (isCompatible) {
                    val currentLoadout = loadoutsCopy[glad.id] ?: EquipmentEngine.createDefaultLoadoutForGladiator(glad)
                    val displacedItem = currentLoadout.getItemInSlot(item.slot)
                    displacedItem?.let { armoryCopy.add(it) }

                    loadoutsCopy[glad.id] = currentLoadout.setItemInSlot(item.slot, item)
                    notificationText = "${item.name} satın alındı ve ${glad.name}'e kuşandırıldı! (-$price Denarii)"
                } else {
                    armoryCopy.add(item)
                    notificationText = "${item.name} satın alındı fakat ${glad.name} için uygun değil: $reason. Cephaneliğe kaldırıldı."
                }
            } else {
                armoryCopy.add(item)
            }
        } else {
            armoryCopy.add(item)
        }

        _uiState.value = _uiState.value.copy(
            dominus = updatedDominus,
            merchants = updatedMerchants,
            ludusArmory = armoryCopy,
            gladiatorLoadouts = loadoutsCopy,
            statusMessage = notificationText
        )
    }

    fun sellArmoryItem(item: EquipmentItem, merchantId: String? = null) {
        val targetMerchantId = merchantId ?: _uiState.value.selectedMerchantId
        val merchant = _uiState.value.merchants.find { it.id == targetMerchantId } ?: _uiState.value.merchants.first()

        val salePrice = EquipmentEngine.calculateDynamicSalePrice(
            item = item,
            merchant = merchant,
            activeEvents = _uiState.value.equipmentMarketEvents,
            politicalFactions = _uiState.value.politicalFactions
        )

        val updatedDominus = _uiState.value.dominus.copy(denarii = _uiState.value.dominus.denarii + salePrice)
        val armoryCopy = _uiState.value.ludusArmory.toMutableList().apply { remove(item) }

        val usedCopy = item.copy(isUsed = true, sellerMerchantId = "player")
        val updatedMerchants = _uiState.value.merchants.map { m ->
            if (m.id == merchant.id) {
                val inv = m.inventory.toMutableList().apply { add(0, usedCopy) }
                m.copy(inventory = inv, playerRelationship = min(100, m.playerRelationship + 1))
            } else m
        }

        _uiState.value = _uiState.value.copy(
            dominus = updatedDominus,
            merchants = updatedMerchants,
            ludusArmory = armoryCopy,
            statusMessage = "${item.name}, ${merchant.name}'e $salePrice Denarii bedelle satıldı."
        )
    }

    fun repairItem(item: EquipmentItem, gladiatorId: String? = null) {
        val cost = item.repairCost
        if (cost <= 0) {
            _uiState.value = _uiState.value.copy(statusMessage = "Bu teçhizat zaten kusursuz durumda, onarım gerekmez.")
            return
        }
        if (_uiState.value.dominus.denarii < cost) {
            _uiState.value = _uiState.value.copy(statusMessage = "Onarım için $cost Denarii gerekli! Kasanız yetersiz.")
            return
        }

        val updatedDominus = _uiState.value.dominus.copy(denarii = _uiState.value.dominus.denarii - cost)
        val repairedItem = item.copy(currentDurability = item.maxDurability)

        val loadoutsCopy = _uiState.value.gladiatorLoadouts.toMutableMap()
        val armoryCopy = _uiState.value.ludusArmory.toMutableList()

        if (gladiatorId != null) {
            val currentLoadout = loadoutsCopy[gladiatorId]
            if (currentLoadout != null) {
                loadoutsCopy[gladiatorId] = currentLoadout.setItemInSlot(item.slot, repairedItem)
            }
        } else {
            val idx = armoryCopy.indexOfFirst { it.id == item.id }
            if (idx >= 0) armoryCopy[idx] = repairedItem else armoryCopy.add(repairedItem)
        }

        _uiState.value = _uiState.value.copy(
            dominus = updatedDominus,
            gladiatorLoadouts = loadoutsCopy,
            ludusArmory = armoryCopy,
            selectedMarketItem = repairedItem,
            statusMessage = "${item.name} usta ellerde onarıldı ve bilendi (-$cost Denarii)."
        )
    }

    fun repairAllForGladiator(gladiatorId: String) {
        val loadout = _uiState.value.gladiatorLoadouts[gladiatorId] ?: return
        val glad = _uiState.value.gladiators.find { it.id == gladiatorId }
        val equippedItems = listOfNotNull(
            loadout.helmet, loadout.bodyArmor, loadout.leftArm, loadout.rightArm,
            loadout.mainHand, loadout.offHand, loadout.shield, loadout.legs, loadout.accessory
        )

        val totalCost = equippedItems.sumOf { it.repairCost }
        if (totalCost <= 0) {
            _uiState.value = _uiState.value.copy(statusMessage = "${glad?.name ?: "Dövüşçü"}'in tüm teçhizatları zaten kusursuz durumda.")
            return
        }
        if (_uiState.value.dominus.denarii < totalCost) {
            _uiState.value = _uiState.value.copy(statusMessage = "Tüm seti onarmak için $totalCost Denarii gerekli!")
            return
        }

        val updatedDominus = _uiState.value.dominus.copy(denarii = _uiState.value.dominus.denarii - totalCost)
        val repairedLoadout = loadout.copy(
            helmet = loadout.helmet?.copy(currentDurability = loadout.helmet!!.maxDurability),
            bodyArmor = loadout.bodyArmor?.copy(currentDurability = loadout.bodyArmor!!.maxDurability),
            leftArm = loadout.leftArm?.copy(currentDurability = loadout.leftArm!!.maxDurability),
            rightArm = loadout.rightArm?.copy(currentDurability = loadout.rightArm!!.maxDurability),
            mainHand = loadout.mainHand?.copy(currentDurability = loadout.mainHand!!.maxDurability),
            offHand = loadout.offHand?.copy(currentDurability = loadout.offHand!!.maxDurability),
            shield = loadout.shield?.copy(currentDurability = loadout.shield!!.maxDurability),
            legs = loadout.legs?.copy(currentDurability = loadout.legs!!.maxDurability),
            accessory = loadout.accessory?.copy(currentDurability = loadout.accessory!!.maxDurability)
        )

        val loadoutsCopy = _uiState.value.gladiatorLoadouts.toMutableMap()
        loadoutsCopy[gladiatorId] = repairedLoadout

        _uiState.value = _uiState.value.copy(
            dominus = updatedDominus,
            gladiatorLoadouts = loadoutsCopy,
            statusMessage = "${glad?.name ?: "Gladyatör"}'in tüm teçhizat seti baştan aşağı onarıldı (-$totalCost Denarii)."
        )
    }

    fun repairAllArmory() {
        val totalCost = _uiState.value.ludusArmory.sumOf { it.repairCost }
        if (totalCost <= 0) {
            _uiState.value = _uiState.value.copy(statusMessage = "Cephanelikteki tüm teçhizatlar tam sağlam durumda.")
            return
        }
        if (_uiState.value.dominus.denarii < totalCost) {
            _uiState.value = _uiState.value.copy(statusMessage = "Cephaneliğin bakımı için $totalCost Denarii gerekli!")
            return
        }

        val updatedDominus = _uiState.value.dominus.copy(denarii = _uiState.value.dominus.denarii - totalCost)
        val repairedArmory = _uiState.value.ludusArmory.map { it.copy(currentDurability = it.maxDurability) }

        _uiState.value = _uiState.value.copy(
            dominus = updatedDominus,
            ludusArmory = repairedArmory,
            statusMessage = "Cephanelikteki tüm teçhizatlar onarıldı ve yağlandı (-$totalCost Denarii)."
        )
    }

    fun equipArmoryItem(gladiatorId: String, item: EquipmentItem) {
        val glad = _uiState.value.gladiators.find { it.id == gladiatorId } ?: return
        val (isCompatible, reason) = checkClassCompatibility(glad.gladiatorClass, item)
        if (!isCompatible) {
            _uiState.value = _uiState.value.copy(statusMessage = "Kuşanılamaz: $reason")
            return
        }

        val loadoutsCopy = _uiState.value.gladiatorLoadouts.toMutableMap()
        val currentLoadout = loadoutsCopy[gladiatorId] ?: EquipmentEngine.createDefaultLoadoutForGladiator(glad)
        val armoryCopy = _uiState.value.ludusArmory.toMutableList().apply { remove(item) }

        val displacedItem = currentLoadout.getItemInSlot(item.slot)
        displacedItem?.let { armoryCopy.add(it) }

        loadoutsCopy[gladiatorId] = currentLoadout.setItemInSlot(item.slot, item)

        _uiState.value = _uiState.value.copy(
            gladiatorLoadouts = loadoutsCopy,
            ludusArmory = armoryCopy,
            statusMessage = "${item.name}, ${glad.name}'e kuşandırıldı."
        )
    }

    fun unequipSlotItem(gladiatorId: String, slot: EquipmentSlot) {
        val loadoutsCopy = _uiState.value.gladiatorLoadouts.toMutableMap()
        val currentLoadout = loadoutsCopy[gladiatorId] ?: return
        val item = currentLoadout.getItemInSlot(slot) ?: return

        loadoutsCopy[gladiatorId] = currentLoadout.setItemInSlot(slot, null)
        val armoryCopy = _uiState.value.ludusArmory.toMutableList().apply { add(item) }

        _uiState.value = _uiState.value.copy(
            gladiatorLoadouts = loadoutsCopy,
            ludusArmory = armoryCopy,
            statusMessage = "${item.name} çıkarıldı ve cephaneliğe kaldırıldı."
        )
    }

    fun placeAuctionBid(auctionId: String, bidAmount: Int) {
        val auction = _uiState.value.auctions.find { it.id == auctionId } ?: return
        val minBid = auction.currentBid + auction.minBidIncrement

        if (bidAmount < minBid) {
            _uiState.value = _uiState.value.copy(statusMessage = "Asgari pey: $minBid Denarii olmalıdır!")
            return
        }
        if (_uiState.value.dominus.denarii < bidAmount) {
            _uiState.value = _uiState.value.copy(statusMessage = "Kasanızda $bidAmount Denarii bulunmuyor!")
            return
        }

        val updatedAuctions = _uiState.value.auctions.map { auc ->
            if (auc.id == auctionId) {
                val biddersList = auc.bidders.toMutableList().apply {
                    if (!contains("Titus Lanista (You)")) add("Titus Lanista (You)")
                }
                auc.copy(
                    currentBid = bidAmount,
                    highBidder = "Titus Lanista (You)",
                    isPlayerHighBidder = true,
                    bidders = biddersList
                )
            } else auc
        }

        _uiState.value = _uiState.value.copy(
            auctions = updatedAuctions,
            statusMessage = "${auction.item.name} müzayedesine $bidAmount Denarii pey sürüldü! En yüksek teklif sizsiniz."
        )
    }

    fun submitCustomCommission(commission: CustomCommission) {
        if (_uiState.value.dominus.denarii < commission.totalCost) {
            _uiState.value = _uiState.value.copy(statusMessage = "Özel sipariş için ${commission.totalCost} Denarii gerekli! Kasanız yetersiz.")
            return
        }

        val updatedDominus = _uiState.value.dominus.copy(denarii = _uiState.value.dominus.denarii - commission.totalCost)
        val updatedCommissions = _uiState.value.commissions.toMutableList().apply { add(commission) }

        _uiState.value = _uiState.value.copy(
            dominus = updatedDominus,
            commissions = updatedCommissions,
            statusMessage = "Demirci ${commission.smithName} ocağı yaktı! '${commission.weaponName}' siparişi ${commission.totalDaysRequired} gün içinde tamamlanacak."
        )
    }

    // -------------------------------------------------------------
    // STORY ENGINE & MYSTERY INVESTIGATION ACTIONS
    // -------------------------------------------------------------

    fun selectStoryHubTab(tab: StoryHubTab) {
        _uiState.value = _uiState.value.copy(selectedStoryHubTab = tab)
    }

    fun selectMystery(mysteryId: String?) {
        _uiState.value = _uiState.value.copy(selectedMysteryId = mysteryId)
    }

    fun selectRumor(rumorId: String?) {
        _uiState.value = _uiState.value.copy(selectedRumorId = rumorId)
    }

    fun investigateMystery(mysteryId: String, pathId: String) {
        val mystery = _uiState.value.mysteries.find { it.id == mysteryId } ?: return
        val path = mystery.investigationPaths.find { it.id == pathId } ?: return

        val result = StoryDirector.investigateMystery(mystery, path, _uiState.value)
        if (!result.success) {
            _uiState.value = _uiState.value.copy(statusMessage = result.message)
            return
        }

        val updatedMysteries = _uiState.value.mysteries.map {
            if (it.id == mysteryId) result.resolvedMystery ?: it else it
        }

        val updatedChronicles = _uiState.value.chronicles.toMutableList()
        val newMemories = _uiState.value.worldMemory.toMutableList()

        if (result.resolvedMystery?.status == MysteryStatus.RESOLVED) {
            updatedChronicles.add(0, ChronicleEntry(
                _uiState.value.dominus.yearAUC,
                "Gizem Aydınlatıldı",
                result.resolvedMystery.resolutionSummary ?: "Soruşturma başarıyla tamamlandı.",
                true
            ))
            newMemories.add(0, MemoryEntry(
                id = "mem_resolve_${mysteryId}_${System.currentTimeMillis()}",
                eventType = MemoryEventType.DISCOVERY_EVENT,
                date = _uiState.value.dominus.dayNumber,
                location = "Capua",
                participantIds = listOf("player"),
                causeDescription = result.resolvedMystery.resolutionSummary ?: "Gizem çözüldü.",
                importance = EventImportance.IMPORTANT
            ))
        }

        val newGold = (_uiState.value.dominus.denarii - path.costDenarii + result.goldReward).coerceAtLeast(0)
        val newPrestige = _uiState.value.dominus.prestige + result.prestigeReward

        _uiState.value = _uiState.value.copy(
            dominus = _uiState.value.dominus.copy(denarii = newGold, prestige = newPrestige),
            mysteries = updatedMysteries,
            worldMemory = newMemories,
            chronicles = updatedChronicles,
            statusMessage = result.message
        )
    }

    fun investigateRumor(rumorId: String) {
        val rumor = _uiState.value.rumors.find { it.id == rumorId } ?: return

        val result = StoryDirector.investigateRumor(rumor, _uiState.value)
        if (!result.success) {
            _uiState.value = _uiState.value.copy(statusMessage = result.message)
            return
        }

        val updatedRumors = _uiState.value.rumors.map {
            if (it.id == rumorId) it.copy(isInvestigated = true) else it
        }

        val updatedChronicles = _uiState.value.chronicles.toMutableList()
        updatedChronicles.add(0, ChronicleEntry(
            _uiState.value.dominus.yearAUC,
            "İstihbarat Raporu",
            "${rumor.headline} -> ${result.revealedTruth}",
            result.isConfirmedTrue
        ))

        val newGold = (_uiState.value.dominus.denarii - rumor.investigationCostDenarii).coerceAtLeast(0)

        _uiState.value = _uiState.value.copy(
            dominus = _uiState.value.dominus.copy(denarii = newGold),
            rumors = updatedRumors,
            chronicles = updatedChronicles,
            statusMessage = result.revealedTruth
        )
    }

    // -------------------------------------------------------------
    // COMPLETE UI / UX OVERHAUL: ATTENTION, SEARCH & MANAGEMENT ACTIONS
    // -------------------------------------------------------------

    fun generateAttentionItems(state: LudusUiState): List<AttentionItem> {
        val list = mutableListOf<AttentionItem>()
        val currentDay = state.dominus.dayNumber

        // 1. Scheduled Bout Today or Tomorrow
        val scheduledBout = state.arenaCalendar.find { it.isPlayerMatch && !it.isCompleted && (it.day == currentDay || it.day == currentDay + 1) }
        if (scheduledBout != null) {
            val isToday = scheduledBout.day == currentDay
            list.add(
                AttentionItem(
                    id = "attn_bout_${scheduledBout.id}",
                    title = if (isToday) "BUGÜN RESMİ MAÇ VAKTİ!" else "Yarın Resmi Arena Maçı",
                    message = "${scheduledBout.venueId.venueName}'nda maçınız var. Rakip: ${scheduledBout.fighter2Name}.",
                    priority = if (isToday) AttentionPriority.CRITICAL else AttentionPriority.IMPORTANT,
                    actionLabel = "Maça Hazırlan",
                    targetScreen = ActiveScreen.MATCH_PREP
                )
            )
        }

        // 2. Severe & Minor Injuries
        state.gladiators.forEach { glad ->
            val severeInjury = glad.injuries.find { it.severity == "Ağır" }
            if (severeInjury != null) {
                list.add(
                    AttentionItem(
                        id = "attn_inj_${glad.id}",
                        title = "${glad.name} Ağır Yaralı!",
                        message = "${severeInjury.name} sakatlığı var. Arenaya çıkamaz, acil hekim operasyonu gerekiyor.",
                        priority = AttentionPriority.CRITICAL,
                        actionLabel = "Tedavi Et",
                        targetScreen = ActiveScreen.ROSTER,
                        targetGladiatorId = glad.id
                    )
                )
            } else if (glad.injuries.isNotEmpty()) {
                list.add(
                    AttentionItem(
                        id = "attn_inj_minor_${glad.id}",
                        title = "${glad.name} Sakatlık Bakımı",
                        message = "Hafif yaralanma mevcut. Hekim kontrolleri ile iyileşme süresi hızlandırılabilir.",
                        priority = AttentionPriority.RELEVANT,
                        actionLabel = "Revire Git",
                        targetScreen = ActiveScreen.ROSTER,
                        targetGladiatorId = glad.id
                    )
                )
            }
        }

        // 3. Damaged Equipment Alert
        state.gladiatorLoadouts.forEach { (gladId, loadout) ->
            val glad = state.gladiators.find { it.id == gladId }
            if (glad != null) {
                val damagedItem = listOfNotNull(loadout.mainHand, loadout.offHand, loadout.shield, loadout.helmet, loadout.bodyArmor).find { it.durability < 35 }
                if (damagedItem != null) {
                    list.add(
                        AttentionItem(
                            id = "attn_gear_${glad.id}",
                            title = "${glad.name}'in ${damagedItem.name} Hasarlı!",
                            message = "Dayanıklılık %${damagedItem.durability}'ye düştü. Kırılma riski yüksek.",
                            priority = AttentionPriority.IMPORTANT,
                            actionLabel = "Demircide Onar",
                            targetScreen = ActiveScreen.EQUIPMENT_MARKET,
                            targetGladiatorId = glad.id
                        )
                    )
                }
            }
        }

        // 4. Low Wheat Storage
        if (state.dominus.foodWheat < 350) {
            list.add(
                AttentionItem(
                    id = "attn_low_wheat",
                    title = "Tahıl Ambarı Kritik Seviyede!",
                    message = "Kalan buğday: ${state.dominus.foodWheat} ölçek. Kışla erzağı tükenmek üzere.",
                    priority = AttentionPriority.IMPORTANT,
                    actionLabel = "Pazardan Al",
                    targetScreen = ActiveScreen.EQUIPMENT_MARKET
                )
            )
        }

        // 5. Active Mysteries
        val activeMystery = state.mysteries.find { it.status == MysteryStatus.ACTIVE }
        if (activeMystery != null) {
            list.add(
                AttentionItem(
                    id = "attn_myst_${activeMystery.id}",
                    title = "Gizem: ${activeMystery.title}",
                    message = activeMystery.description,
                    priority = AttentionPriority.RELEVANT,
                    actionLabel = "İpuçlarını İncele",
                    targetScreen = ActiveScreen.CHRONICLE
                )
            )
        }

        return list.sortedBy { it.priority.ordinal }
    }

    fun togglePinGladiator(gladiatorId: String) {
        val current = _uiState.value.pinnedGladiatorIds.toMutableSet()
        if (current.contains(gladiatorId)) {
            current.remove(gladiatorId)
        } else {
            current.add(gladiatorId)
        }
        _uiState.value = _uiState.value.copy(pinnedGladiatorIds = current)
    }

    fun openEndDayChecklist() {
        _uiState.value = _uiState.value.copy(showEndDayConfirmation = true)
    }

    fun dismissEndDayChecklist() {
        _uiState.value = _uiState.value.copy(showEndDayConfirmation = false)
    }

    fun confirmEndDay() {
        _uiState.value = _uiState.value.copy(showEndDayConfirmation = false)
        advanceDay()
    }

    fun openSearchDialog() {
        _uiState.value = _uiState.value.copy(showSearchDialog = true)
    }

    fun dismissSearchDialog() {
        _uiState.value = _uiState.value.copy(showSearchDialog = false)
    }

    fun dismissWhatChangedDialog() {
        _uiState.value = _uiState.value.copy(showWhatChangedDialog = false)
    }

    fun executeAttentionAction(item: AttentionItem) {
        if (item.targetGladiatorId != null) {
            val targetGlad = _uiState.value.gladiators.find { it.id == item.targetGladiatorId }
            if (targetGlad != null) {
                selectGladiator(targetGlad)
            }
        }
        navigateTo(item.targetScreen)
    }

    fun updateTrainingPlan(gladiatorId: String, focus: String, diet: String) {
        _uiState.value = _uiState.value.copy(
            statusMessage = "Gladyatör talim planı güncellendi: $focus ($diet)"
        )
    }

    fun treatInjury(gladiatorId: String, injuryName: String) {
        val cost = 80
        if (_uiState.value.dominus.denarii < cost) {
            _uiState.value = _uiState.value.copy(statusMessage = "Hekim tedavisi için yeterli altın yok ($cost D gerekli).")
            return
        }

        val updatedGlads = _uiState.value.gladiators.map { glad ->
            if (glad.id == gladiatorId) {
                val updatedInjuries = glad.injuries.filterNot { it.name == injuryName }
                glad.copy(
                    condition = glad.condition.copy(health = min(100, glad.condition.health + 20)),
                    injuries = updatedInjuries
                )
            } else glad
        }

        _uiState.value = _uiState.value.copy(
            dominus = _uiState.value.dominus.copy(denarii = _uiState.value.dominus.denarii - cost),
            gladiators = updatedGlads,
            statusMessage = "Medicus tedavisi uygulandı. Yaranın iyileşme süreci hızlandırıldı."
        )
        val refreshedAttention = generateAttentionItems(_uiState.value)
        _uiState.value = _uiState.value.copy(attentionItems = refreshedAttention)
    }
}


