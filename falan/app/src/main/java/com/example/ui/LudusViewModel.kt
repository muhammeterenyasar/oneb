package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LudusDatabase
import com.example.data.LudusRepository
import com.example.data.MatchLogEntity
import com.example.data.engine.ArmoryEngine
import com.example.data.engine.BattleEngine
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

data class MainUiState(
    val currentScreen: AppScreen = AppScreen.LUDUS_OVERVIEW,
    val navigationStack: List<AppScreen> = listOf(AppScreen.LUDUS_OVERVIEW),
    val gladiators: List<Gladiator> = emptyList(),
    val ludusState: LudusState = LudusState(),
    val recentLogs: List<MatchLogEntity> = emptyList(),
    val marketCandidates: List<Gladiator> = emptyList(),
    val availableTeachersForHire: List<Teacher> = emptyList(),
    val availableOpponents: List<EnemyGladiator> = emptyList(),
    val activeBattle: BattleState? = null,
    val selectedGladiator: Gladiator? = null,
    val selectedOpponent: EnemyGladiator? = null,
    val selectedTactic: BattleTactic = BattleTactic.AGGRESSIVE,
    val selectedFormat: MatchFormat = MatchFormat.LUSUS,
    val betAmount: Int = 0,
    val promiseOfFreedomChecked: Boolean = false,
    val battleSpeed: Float = 1.0f,
    val notificationMessage: String? = null,
    val isSimulationRunning: Boolean = false,
    val dailyCycleModalMessage: String? = null,

    // Between-Cycle Interactive States
    val showSparringDialog: Boolean = false,
    val activeSparring: SparringState? = null,
    val sparringFighter1: Gladiator? = null,
    val sparringFighter2: Gladiator? = null,

    val showTesseraeDialog: Boolean = false,
    val activeTesserae: TesseraeGameState = TesseraeGameState(),

    val showSuburaTavernDialog: Boolean = false,
    val showDilemmaDialog: Boolean = false,
    val lastDilemmaOutcome: String? = null,

    val showBetweenCycleEventDialog: Boolean = false,
    val activeBetweenCycleEvent: BetweenCycleEvent? = null,
    val selectedEventGladiator: Gladiator? = null,
    val lastEventResult: EventResolutionResult? = null,

    // Training Drill Arena specific UI state
    val selectedTrainingGladiatorId: Long? = null,
    val selectedDrillCategory: DrillCategory = DrillCategory.STRENGTH,
    val lastDrillExecutionOutcome: String? = null
)

class LudusViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LudusDatabase.getDatabase(application)
    private val repository = LudusRepository(
        database.gladiatorDao(),
        database.ludusStateDao(),
        database.matchLogDao(),
        database.teacherDao()
    )

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var battleJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeIfEmpty()
            refreshMarketCandidates()
            refreshTeacherCandidates()
            refreshOpponents()
        }

        viewModelScope.launch {
            combine(
                repository.gladiatorsFlow,
                repository.ludusStateFlow,
                repository.recentLogsFlow
            ) { gladiators, ludusState, logs ->
                _uiState.update { current ->
                    val updatedSelected = gladiators.find { it.id == current.selectedGladiator?.id }
                        ?: current.selectedGladiator ?: gladiators.firstOrNull()
                    
                    // Auto-sync opponent with scheduled event if not manually selected
                    val scheduledOpponent = ludusState.currentScheduledEvent?.featuredOpponent
                    val activeOpponent = current.selectedOpponent ?: scheduledOpponent ?: current.availableOpponents.firstOrNull()

                    current.copy(
                        gladiators = gladiators,
                        ludusState = ludusState,
                        recentLogs = logs,
                        selectedGladiator = updatedSelected,
                        selectedOpponent = activeOpponent,
                        selectedFormat = ludusState.currentScheduledEvent?.matchFormat ?: current.selectedFormat
                    )
                }
            }.collect()
        }
    }

    // Centralized Navigation System
    fun navigateTo(screen: AppScreen) {
        _uiState.update { current ->
            if (current.currentScreen == screen) current
            else current.copy(
                currentScreen = screen,
                navigationStack = current.navigationStack + screen
            )
        }
    }

    fun navigateToRoute(route: String) {
        val screen = AppScreen.fromRoute(route)
        navigateTo(screen)
    }

    fun navigateBack(): Boolean {
        val current = _uiState.value
        if (current.navigationStack.size > 1) {
            val newStack = current.navigationStack.dropLast(1)
            val prevScreen = newStack.last()
            _uiState.update { it.copy(currentScreen = prevScreen, navigationStack = newStack) }
            return true
        }
        return false
    }

    fun selectGladiator(gladiator: Gladiator) {
        _uiState.update { it.copy(selectedGladiator = gladiator) }
    }

    fun selectOpponent(opponent: EnemyGladiator) {
        _uiState.update { it.copy(selectedOpponent = opponent) }
    }

    fun selectTactic(tactic: BattleTactic) {
        _uiState.update { it.copy(selectedTactic = tactic) }
    }

    fun selectFormat(format: MatchFormat) {
        _uiState.update { it.copy(selectedFormat = format) }
    }

    fun setBetAmount(amount: Int) {
        val maxBet = _uiState.value.ludusState.gold
        _uiState.update { it.copy(betAmount = max(0, min(amount, maxBet))) }
    }

    fun togglePromiseOfFreedom(enable: Boolean) {
        _uiState.update { it.copy(promiseOfFreedomChecked = enable) }
    }

    fun setBattleSpeed(speed: Float) {
        _uiState.update { it.copy(battleSpeed = speed) }
    }

    fun clearNotification() {
        _uiState.update { it.copy(notificationMessage = null, dailyCycleModalMessage = null) }
    }

    fun refreshOpponents() {
        val currentTier = _uiState.value.ludusState.cityTier
        val topGladiatorPower = _uiState.value.gladiators.maxOfOrNull { it.totalPowerScore } ?: 80
        val day = _uiState.value.ludusState.day
        val diffMod = _uiState.value.ludusState.opponentDifficultyModifier
        val ruthless = _uiState.value.ludusState.ruthlessnessScore
        val sentiment = _uiState.value.ludusState.crowdSentimentScore
        val opponents = repository.generateOpponentsForCity(
            cityTier = currentTier,
            playerPower = topGladiatorPower,
            day = day,
            difficultyModifier = diffMod,
            ruthlessnessScore = ruthless,
            crowdSentimentScore = sentiment
        )
        _uiState.update {
            it.copy(
                availableOpponents = opponents,
                selectedOpponent = opponents.firstOrNull()
            )
        }
    }

    fun refreshTeacherCandidates() {
        val teachers = listOf(
            Teacher(
                id = 101,
                name = "Doctore Quintus",
                title = "Retiarius & Hız Eğitmeni",
                specialty = TeacherSpecialty.RETIARIUS_MASTER,
                level = 2,
                statBonusMultiplier = 1.35f,
                dailySalary = 12,
                hireCost = 150,
                description = "Pompeii arenasında 12 zafer kazanmış eski şampiyon. Çeviklik ve savuşturma sanatını öğretir."
            ),
            Teacher(
                id = 102,
                name = "Eğitmen Galen",
                title = "Hekim & Kondisyoner",
                specialty = TeacherSpecialty.PHYSICAL_CONDITIONING,
                level = 2,
                statBonusMultiplier = 1.30f,
                dailySalary = 14,
                hireCost = 180,
                description = "Yunan tıp ve jimnastik ustası. Gladyatörlerin dayanıklılığını artırır ve sakatlıkları önler."
            ),
            Teacher(
                id = 103,
                name = "Doctore Leonidas",
                title = "Spartan Disiplin Ustası",
                specialty = TeacherSpecialty.GLADIATOR_DISCIPLINE,
                level = 3,
                statBonusMultiplier = 1.45f,
                dailySalary = 18,
                hireCost = 250,
                description = "Demir disiplin uygular. Antrenmanların verimini zirveye çıkarır ve gladyatörlerin moralini çelik gibi tutar."
            )
        )
        _uiState.update { it.copy(availableTeachersForHire = teachers) }
    }

    fun refreshMarketCandidates() {
        val names = listOf("Thrax", "Castor", "Pollux", "Drusus", "Varro", "Spartan", "Felix", "Gallus", "Lucius", "Nero", "Titus", "Brixius")
        val origins = listOf("Trakya", "Galyalı", "Hispania", "Numidya", "Roma", "Germanya", "Yunan")
        val classes = GladiatorClass.entries

        val candidates = (0..3).map { index ->
            val isContract = index % 2 == 1
            val gClass = classes[Random.nextInt(classes.size)]
            val str = Random.nextInt(11, 18)
            val agi = Random.nextInt(10, 18)
            val sta = Random.nextInt(11, 18)
            val mor = if (isContract) Random.nextInt(75, 95) else Random.nextInt(45, 65)
            val basePrice = (str + agi + sta) * 8 + (if (isContract) 80 else 140)
            val age = Random.nextInt(19, 31)
            val trait = GladiatorTrait.entries[Random.nextInt(GladiatorTrait.entries.size)]

            Gladiator(
                id = 0,
                name = names[Random.nextInt(names.size)],
                nickname = "${gClass.displayName} Adayı",
                origin = origins[Random.nextInt(origins.size)],
                gladiatorClass = gClass,
                contractType = if (isContract) GladiatorContractType.CONTRACTED else GladiatorContractType.SLAVE,
                dailySalary = if (isContract) Random.nextInt(8, 16) else 0,
                priceValue = basePrice,
                str = str,
                agi = agi,
                sta = sta,
                mor = mor,
                age = age,
                fatigue = 0,
                currentHp = 100 + sta * 3,
                maxHp = 100 + sta * 3,
                personalityTrait = trait
            )
        }
        _uiState.update { it.copy(marketCandidates = candidates) }
    }

    // --- Advanced Specific Drill Training System (Strength / Agility / Stamina Drills) ---
    fun selectTrainingGladiator(gladiatorId: Long) {
        _uiState.update { it.copy(selectedTrainingGladiatorId = gladiatorId) }
    }

    fun setSelectedDrillCategory(category: DrillCategory) {
        _uiState.update { it.copy(selectedDrillCategory = category) }
    }

    fun assignSpecificDrill(gladiatorId: Long, drill: SpecificDrill) {
        viewModelScope.launch {
            val gladiator = _uiState.value.gladiators.find { it.id == gladiatorId } ?: return@launch
            if (gladiator.isInjured && drill != SpecificDrill.THERMAE_MASSAGE) {
                _uiState.update { it.copy(notificationMessage = "⚠️ ${gladiator.name} sakat! Ağır tatbikatlara katılamaz; sadece Thermae Hamamı atanabilir.") }
                return@launch
            }
            repository.assignGladiatorDrill(gladiatorId, drill)
            _uiState.update {
                it.copy(
                    notificationMessage = "🎯 ${gladiator.name} -> [${drill.icon} ${drill.title}] tatbikatına atandı (${drill.targetStat})."
                )
            }
        }
    }

    fun bulkAssignDrillToAll(drill: SpecificDrill) {
        viewModelScope.launch {
            repository.bulkAssignDrill(drill)
            _uiState.update {
                it.copy(
                    notificationMessage = "📢 Tüm kadroya [${drill.icon} ${drill.title}] tatbikatı atandı!"
                )
            }
        }
    }

    fun executeSingleGladiatorDrillNow(gladiatorId: Long) {
        viewModelScope.launch {
            val summary = repository.executeSingleGladiatorDrill(gladiatorId)
            _uiState.update {
                it.copy(
                    lastDrillExecutionOutcome = summary,
                    notificationMessage = "⚔️ İdman Tamamlandı: $summary"
                )
            }
        }
    }

    fun executeAllDrillsNow() {
        viewModelScope.launch {
            val summary = repository.executeAllGladiatorDrills()
            _uiState.update {
                it.copy(
                    lastDrillExecutionOutcome = summary,
                    notificationMessage = summary
                )
            }
        }
    }

    fun dismissDrillOutcome() {
        _uiState.update { it.copy(lastDrillExecutionOutcome = null) }
    }

    // Set morning training focus (Strength, Agility, Endurance, Sparring, Rest)
    fun assignTrainingFocus(gladiatorId: Long, focus: TrainingType) {
        viewModelScope.launch {
            val gladiator = _uiState.value.gladiators.find { it.id == gladiatorId } ?: return@launch
            if (gladiator.isInjured && focus != TrainingType.REST) {
                _uiState.update { it.copy(notificationMessage = "Yaralı gladyatör antrenman yapamaz! Sadece Hamam/Dinlenme seçilebilir.") }
                return@launch
            }
            val updated = gladiator.copy(trainingFocus = focus, isTraining = true)
            repository.saveGladiator(updated)
            _uiState.update { it.copy(notificationMessage = "${gladiator.name} için [${focus.title}] odaklı antrenman planlandı.") }
        }
    }

    // Compatibility overload for simple stat assignment
    fun assignTraining(gladiatorId: Long, statName: String) {
        val focus = when (statName.lowercase()) {
            "str" -> TrainingType.STRENGTH
            "agi" -> TrainingType.AGILITY
            "sta" -> TrainingType.ENDURANCE
            "mor", "rest" -> TrainingType.REST
            "sparring" -> TrainingType.SPARRING
            else -> TrainingType.STRENGTH
        }
        assignTrainingFocus(gladiatorId, focus)
    }

    // Set Diet Plan
    fun setDietPlan(diet: DietPlan) {
        viewModelScope.launch {
            repository.setDietPlan(diet)
            _uiState.update { it.copy(notificationMessage = "Ludus Beslenme Düzeni Değiştirildi: ${diet.title}") }
        }
    }

    // Hire Teacher
    fun hireTeacher(teacher: Teacher) {
        viewModelScope.launch {
            val success = repository.hireTeacher(teacher)
            if (success) {
                _uiState.update {
                    it.copy(
                        notificationMessage = "🎓 ${teacher.name} (${teacher.specialty.displayName}) kadroya katıldı! Antrenman verimi arttı."
                    )
                }
            } else {
                _uiState.update { it.copy(notificationMessage = "Yetersiz altın! Eğitmeni kiralamak için ${teacher.hireCost} Altın gerekir.") }
            }
        }
    }

    // Promote Veteran Gladiator to Doctore / Teacher
    fun promoteGladiatorToTeacher(gladiator: Gladiator) {
        viewModelScope.launch {
            val teacher = repository.promoteGladiatorToTeacher(gladiator)
            if (teacher != null) {
                _uiState.update {
                    it.copy(
                        notificationMessage = "👑 BÜYÜK TERFİ: Efsane ${gladiator.name} arenadan emekli oldu ve Ludus Başeğitmeni (Doctore) unvanı aldı! Okul genelinde büyük moral ve prestij kazanıldı."
                    )
                }
            } else {
                _uiState.update { it.copy(notificationMessage = "Bu gladyatör henüz eğitmenlik için yeterli tecrübeye sahip değil (En az 3 galibiyet veya 26 yaş gerekir).") }
            }
        }
    }

    // Dismiss Teacher
    fun dismissTeacher(teacherId: Long) {
        viewModelScope.launch {
            repository.dismissTeacher(teacherId)
            _uiState.update { it.copy(notificationMessage = "Eğitmen görevden alındı.") }
        }
    }

    // Advance Phase in Ludus
    fun advanceDayPhase() {
        viewModelScope.launch {
            val summary = repository.advanceDayPhase()
            refreshOpponents()
            refreshMarketCandidates()
            _uiState.update { it.copy(dailyCycleModalMessage = summary) }
        }
    }

    // Manumit Gladiator
    fun manumitGladiator(gladiator: Gladiator) {
        viewModelScope.launch {
            val (prestige, gold) = repository.manumitGladiator(gladiator)
            _uiState.update {
                it.copy(
                    notificationMessage = "🕊️ ${gladiator.name} azad edildi ve halkın sevgilisi oldu! Kazanç: +$prestige Prestij, +$gold Altın fonu."
                )
            }
        }
    }

    // Start Battle Simulation
    fun startBattleSimulation(campaignMissionId: String? = null) {
        val player = _uiState.value.selectedGladiator ?: return
        val enemy = _uiState.value.selectedOpponent ?: return
        val tactic = _uiState.value.selectedTactic
        val format = _uiState.value.selectedFormat
        val bet = _uiState.value.betAmount
        val promiseOfFreedom = _uiState.value.promiseOfFreedomChecked
        val scheduled = _uiState.value.ludusState.currentScheduledEvent

        if (player.isInjured) {
            _uiState.update { it.copy(notificationMessage = "Yaralı gladyatör arenaya çıkamaz!") }
            return
        }

        // Deduct bet if placed
        if (bet > 0) {
            val currentState = _uiState.value.ludusState
            if (currentState.gold < bet) {
                _uiState.update { it.copy(notificationMessage = "Bahis için yeterli altınınız yok!") }
                return
            }
            viewModelScope.launch {
                repository.saveLudusState(currentState.copy(gold = currentState.gold - bet))
            }
        }

        // Initial Battle State
        val ludusState = _uiState.value.ludusState
        val startingLogs = mutableListOf<BattleActionLog>()
        startingLogs.add(
            BattleActionLog(
                text = "🎺 Borazanlar çaldı! ${player.name} (${tactic.title}) vs ${enemy.name} (${enemy.ludusOrigin}) arenaya girdi!",
                isPlayerAction = true,
                crowdReaction = if (ludusState.crowdHypeBonus) "📢 Tellalların çağrısıyla arena adınızı haykırıyor!" else "Kalabalık tezahürat yapıyor!"
            )
        )
        if (ludusState.scoutedEnemyWeakness) {
            startingLogs.add(
                BattleActionLog(
                    text = "🎯 CASUS BİLGİSİ: Rakibin sol tarafındaki zırh açığı değerlendirildi (+%15 Hasar)!",
                    isPlayerAction = true
                )
            )
        }
        if (ludusState.sharpenedWeapons) {
            startingLogs.add(
                BattleActionLog(
                    text = "🗡️ BİLENMİŞ ARSENAL: Çelik kılıç ve mızraklar jilet gibi keskin (+%15 Kritik)!",
                    isPlayerAction = true
                )
            )
        }
        if (ludusState.rivalWeakenedByPoison) {
            startingLogs.add(
                BattleActionLog(
                    text = "☠️ GİZLİ CEMİYET ZEHRİ: Rakibin şarabına katılan belladonna zehri etkisini gösterdi (-%25 Başlangıç Canı)!",
                    isPlayerAction = true
                )
            )
        }
        if (ludusState.marsDivineBlessing) {
            startingLogs.add(
                BattleActionLog(
                    text = "🔥 MARS İLAHİ KUTSAMASI: Tapınaktaki kan kurbanı dövüşçünüzün etrafında alevden bir kalkan oluşturdu (+%20 Kritik & Sakatlanmazlık)!",
                    isPlayerAction = true
                )
            )
        }

        val enemyEffectiveHp = if (ludusState.rivalWeakenedByPoison) (enemy.currentHp * 0.75f).toInt().coerceAtLeast(20) else enemy.currentHp

        val isCrowdFavorite = player.personalityTrait == GladiatorTrait.CROWD_FAVORITE
        val baseHype = if (ludusState.crowdHypeBonus) 80 else 50
        val startingHype = if (isCrowdFavorite) (baseHype + 20).coerceAtMost(100) else baseHype

        val initialBattle = BattleState(
            playerGladiator = player,
            enemyGladiator = enemy,
            tactic = tactic,
            matchFormat = format,
            betAmount = bet,
            scheduledEvent = scheduled,
            playerCurrentHp = player.currentHp,
            playerCurrentStamina = 100,
            enemyCurrentHp = enemyEffectiveHp,
            enemyCurrentStamina = 100,
            crowdHype = startingHype,
            wasFreedByPromise = promiseOfFreedom,
            campaignMissionId = campaignMissionId,
            logs = startingLogs
        )

        _uiState.update {
            it.copy(
                activeBattle = initialBattle,
                isSimulationRunning = true
            )
        }

        runBattleLoop()
    }

    private fun runBattleLoop() {
        battleJob?.cancel()
        battleJob = viewModelScope.launch {
            var currentBattle = _uiState.value.activeBattle ?: return@launch

            // Calculate equipment bonuses for the player gladiator
            val playerEquipBonuses = ArmoryEngine.calculateEquipmentBonuses(currentBattle.playerGladiator)

            var pStr = currentBattle.playerGladiator.str.toFloat() + playerEquipBonuses.totalDamageBonus
            var pAgi = currentBattle.playerGladiator.agi.toFloat()
            var pSta = currentBattle.playerGladiator.sta.toFloat()
            val playerArmorReduction = playerEquipBonuses.totalArmorReductionPercent
            val playerCritEquipBonus = playerEquipBonuses.totalCritBonusPercent
            val playerDodgeEquipBonus = playerEquipBonuses.totalDodgeBonusPercent

            val hasScoutBonus = _uiState.value.ludusState.scoutedEnemyWeakness
            val hasSharpenBonus = _uiState.value.ludusState.sharpenedWeapons
            val hasMarsBonus = _uiState.value.ludusState.marsDivineBlessing

            // Promise of Freedom bonus (+30% moral & combat bonus)
            if (currentBattle.wasFreedByPromise) {
                pStr *= 1.30f
                pAgi *= 1.30f
                pSta *= 1.30f
            }

            val tactic = currentBattle.tactic
            val enemy = currentBattle.enemyGladiator
            val hasIronSkin = enemy.traitName == "Demir Kalkan"
            val hasArmorBreaker = enemy.traitName == "Zırh Delici"
            val hasBerserk = enemy.traitName == "Öfke Patlaması"
            val hasPoisonBlade = enemy.traitName == "Zehirli Uç"
            val hasSwiftDodge = enemy.traitName == "Çevik Kaçınma"

            var turn = 0
            while (!currentBattle.isFinished && currentBattle.playerCurrentHp > 0 && currentBattle.enemyCurrentHp > 0) {
                turn++
                val delayTime = when (_uiState.value.battleSpeed) {
                    0.0f -> 0L
                    2.0f -> 450L
                    else -> 900L
                }
                if (delayTime > 0) {
                    delay(delayTime)
                }

                // Determine attacker by agility and random roll
                val playerFirst = (pAgi + Random.nextInt(10)) >= (enemy.agi + Random.nextInt(10))

                val newLogs = currentBattle.logs.toMutableList()
                var pCurHp = currentBattle.playerCurrentHp
                var eCurHp = currentBattle.enemyCurrentHp
                var pCurStamina = currentBattle.playerCurrentStamina
                var eCurStamina = currentBattle.enemyCurrentStamina
                var hype = currentBattle.crowdHype

                fun executePlayerAttack() {
                    if (pCurHp <= 0) return
                    val (isDodge, isBlock) = BattleEngine.checkDefenseOutcome(
                        defenderAgi = enemy.agi,
                        defenderSta = enemy.sta,
                        defenderClass = enemy.gladiatorClass,
                        isDefensiveTactic = false,
                        lanistaShoutBonus = null,
                        equipmentDodgeBonus = if (hasSwiftDodge) 20 else 0
                    )
                    if (isDodge) {
                        newLogs.add(
                            BattleActionLog(
                                text = "💨 ${enemy.name} çevik bir taklayla ${currentBattle.playerGladiator.name}'ın hamlesinden sıyrıldı!",
                                isPlayerAction = true,
                                crowdReaction = "Seyirciler ıslıkladı!"
                            )
                        )
                        return
                    }

                    val isCrit = BattleEngine.checkCriticalHit(
                        attackerAgi = pAgi.toInt(),
                        tactic = tactic,
                        sharpenedWeapons = hasSharpenBonus,
                        marsBlessing = hasMarsBonus,
                        equipmentCritBonus = playerCritEquipBonus
                    )

                    var damage = BattleEngine.calculateDamage(
                        attackerStr = pStr.toInt() + (currentBattle.playerGladiator.weaponLevel * 2),
                        defenderSta = enemy.sta,
                        defenderAgi = enemy.agi,
                        tactic = tactic,
                        isCrit = isCrit,
                        isAttackerPlayer = true,
                        lanistaShoutBonus = currentBattle.lanistaShoutBonus,
                        scoutedWeakness = hasScoutBonus,
                        sharpenedWeapons = hasSharpenBonus,
                        marsBlessing = hasMarsBonus,
                        equipmentDamageBonus = 0,
                        defenderArmorReductionPercent = if (hasIronSkin) -25 else 0
                    )

                    if (isBlock) {
                        damage = max(2, damage / 2)
                    }

                    // Personality Trait Combat Effects
                    if (currentBattle.playerGladiator.personalityTrait == GladiatorTrait.IRON_WILLED && pCurHp < (currentBattle.playerGladiator.maxHp * 0.35f)) {
                        damage = (damage * 1.25f).toInt()
                    }
                    if (currentBattle.playerGladiator.personalityTrait == GladiatorTrait.BLOODTHIRSTY && isCrit) {
                        damage = (damage * 1.20f).toInt()
                        hype = min(100, hype + 20)
                    }

                    eCurHp = max(0, eCurHp - damage)
                    pCurStamina = max(0, pCurStamina - 8)
                    hype = min(100, hype + (if (isCrit) 15 else 6))

                    val desc = if (isCrit) {
                        "💥 KRİTİK VURUŞ! ${currentBattle.playerGladiator.name} sarsıcı bir darbeyle ${enemy.name}'a $damage hasar verdi!"
                    } else if (isBlock) {
                        "🛡️ ${enemy.name} kalkanıyla darbeyi yumuşattı ($damage hasar aldı)."
                    } else {
                        "⚔️ ${currentBattle.playerGladiator.name} ustaca hamleyle ${enemy.name}'ı $damage hasarla yaraladı."
                    }

                    newLogs.add(
                        BattleActionLog(
                            text = desc,
                            isPlayerAction = true,
                            isCritical = isCrit,
                            damageDealt = damage,
                            crowdReaction = if (isCrit) "KUM KANLANDI! ARENA ÇOŞTU!" else null
                        )
                    )
                }

                fun executeEnemyAttack() {
                    if (eCurHp <= 0) return

                    val (isDodge, isBlock) = BattleEngine.checkDefenseOutcome(
                        defenderAgi = pAgi.toInt(),
                        defenderSta = pSta.toInt(),
                        defenderClass = currentBattle.playerGladiator.gladiatorClass,
                        isDefensiveTactic = tactic == BattleTactic.DEFENSIVE,
                        lanistaShoutBonus = currentBattle.lanistaShoutBonus,
                        equipmentDodgeBonus = playerDodgeEquipBonus
                    )

                    if (isDodge) {
                        newLogs.add(
                            BattleActionLog(
                                text = "💨 ${currentBattle.playerGladiator.name} çevik bir hareketle ${enemy.name}'ın saldırısından kaçtı!",
                                isPlayerAction = false,
                                crowdReaction = "Seyirciler alkışladı!"
                            )
                        )
                        return
                    }

                    val isCrit = BattleEngine.checkCriticalHit(
                        attackerAgi = enemy.agi,
                        tactic = BattleTactic.AGGRESSIVE,
                        equipmentCritBonus = if (hasArmorBreaker) 15 else 0
                    )

                    var damage = BattleEngine.calculateDamage(
                        attackerStr = enemy.str,
                        defenderSta = pSta.toInt(),
                        defenderAgi = pAgi.toInt(),
                        tactic = BattleTactic.AGGRESSIVE,
                        isCrit = isCrit,
                        isAttackerPlayer = false,
                        lanistaShoutBonus = currentBattle.lanistaShoutBonus,
                        defenderArmorReductionPercent = playerArmorReduction
                    )

                    if (isBlock) {
                        damage = max(2, damage / 2)
                    }

                    if (hasBerserk && eCurHp < (enemy.maxHp * 0.4f)) {
                        damage = (damage * 1.40f).toInt()
                    }

                    pCurHp = max(0, pCurHp - damage)
                    eCurStamina = max(0, eCurStamina - 8)
                    if (hasPoisonBlade) pCurStamina = max(0, pCurStamina - 10)

                    val desc = if (isCrit) {
                        "⚡ TEHLİKE! ${enemy.name} savunmayı delerek ${currentBattle.playerGladiator.name}'a $damage hasar vurdu!"
                    } else if (isBlock) {
                        "🛡️ ${currentBattle.playerGladiator.name} siper alarak darbeyi hafifletti ($damage hasar)."
                    } else {
                        "🛡️ ${enemy.name} karşı saldırı yaparak $damage hasar verdi."
                    }

                    newLogs.add(
                        BattleActionLog(
                            text = desc,
                            isPlayerAction = false,
                            isCritical = isCrit,
                            damageDealt = damage
                        )
                    )
                }

                if (playerFirst) {
                    executePlayerAttack()
                    if (eCurHp > 0) executeEnemyAttack()
                } else {
                    executeEnemyAttack()
                    if (pCurHp > 0) executePlayerAttack()
                }

                // Check finish conditions
                val isFinished = pCurHp <= 0 || eCurHp <= 0
                val isVictory = eCurHp <= 0
                val isDefeat = pCurHp <= 0

                // If player won and match format is not Sine Missio -> Ask crowd judgement!
                val awaitsCrowd = isVictory && currentBattle.matchFormat != MatchFormat.LUSUS

                if (isFinished) {
                    if (awaitsCrowd) {
                        currentBattle = currentBattle.copy(
                            playerCurrentHp = pCurHp,
                            playerCurrentStamina = pCurStamina,
                            enemyCurrentHp = eCurHp,
                            enemyCurrentStamina = eCurStamina,
                            crowdHype = hype,
                            turnCount = turn,
                            logs = newLogs,
                            isAwaitingCrowdJudgement = true
                        )
                        _uiState.update { it.copy(activeBattle = currentBattle) }
                        break
                    } else {
                        // Conclude directly
                        concludeBattle(
                            baseBattle = currentBattle.copy(
                                playerCurrentHp = pCurHp,
                                enemyCurrentHp = eCurHp,
                                crowdHype = hype,
                                turnCount = turn,
                                logs = newLogs
                            ),
                            isVictory = isVictory,
                            isDefeat = isDefeat,
                            judgement = if (currentBattle.matchFormat == MatchFormat.LUSUS) CrowdVerdict.MISSIO else CrowdVerdict.IUGULA
                        )
                        break
                    }
                } else {
                    currentBattle = currentBattle.copy(
                        playerCurrentHp = pCurHp,
                        playerCurrentStamina = pCurStamina,
                        enemyCurrentHp = eCurHp,
                        enemyCurrentStamina = eCurStamina,
                        crowdHype = hype,
                        turnCount = turn,
                        logs = newLogs
                    )
                    _uiState.update { it.copy(activeBattle = currentBattle) }
                }
            }
        }
    }

    // Resolve crowd verdict (Pollice Verso)
    fun resolveCrowdJudgement(verdict: CrowdVerdict) {
        val currentBattle = _uiState.value.activeBattle ?: return
        val newLogs = currentBattle.logs.toMutableList()
        val chant = if (verdict == CrowdVerdict.IUGULA) {
            "👎 POLLICE VERSO: LANISTA HÜKMÜ • 'IUGULA! (BOYNUNU VUR!)' — Kılıç havaya kalktı ve infaz gerçekleşti!"
        } else {
            "👍 POLLICE VERSO: LANISTA HÜKMÜ • 'MISSIO! (CANINI BAĞIŞLA!)' — ${currentBattle.playerGladiator.name} kılıcını kınına soktu ve rakibini ayağa kaldırdı!"
        }
        newLogs.add(BattleActionLog(text = chant, isPlayerAction = true, crowdReaction = if (verdict == CrowdVerdict.IUGULA) "Tribünler kanla coştu!" else "Halk erdemli zaferi ayakta alkışladı!"))
        
        concludeBattle(
            baseBattle = currentBattle.copy(logs = newLogs),
            isVictory = true,
            isDefeat = false,
            judgement = verdict
        )
    }

    private fun concludeBattle(
        baseBattle: BattleState,
        isVictory: Boolean,
        isDefeat: Boolean,
        judgement: CrowdVerdict
    ) {
        val format = baseBattle.matchFormat
        val enemy = baseBattle.enemyGladiator
        val bet = baseBattle.betAmount
        val betGain = if (isVictory && bet > 0) bet * 2 else 0

        val hasBloodEmperor = _uiState.value.ludusState.unlockedPerkIds.contains(LanistaPerk.BLOOD_EMPEROR.id)
        val bloodEmperorMultiplier = if (hasBloodEmperor && judgement == CrowdVerdict.IUGULA) 1.5f else 1.0f

        val earnedGold = if (isVictory) {
            val baseG = if (judgement == CrowdVerdict.IUGULA) {
                baseBattle.calculatedExecutionGoldGain
            } else {
                format.rewardGoldBase + (baseBattle.scheduledEvent?.rewardGold ?: enemy.rewardGold) + betGain + 25
            }
            (baseG * bloodEmperorMultiplier).toInt()
        } else 0

        val earnedPrestige = if (isVictory) {
            if (judgement == CrowdVerdict.MISSIO) {
                baseBattle.calculatedMercyPrestigeGain
            } else {
                baseBattle.calculatedExecutionPrestigeGain
            }
        } else -10

        val summary = if (isVictory) {
            if (judgement == CrowdVerdict.IUGULA) {
                val crowdLiked = baseBattle.crowdBloodlustPercent >= 50
                val crowdNote = if (crowdLiked) "Seyircinin kan arzusuna uyuldu." else "Seyircinin bağışlama talebine rağmen infaz edildi (İntikam riski arttı!)."
                "🏆 KANLI İNFAZ ZAFERİ! ${baseBattle.playerGladiator.name}, ${enemy.name}'un boynunu vurdu. $crowdNote (+$earnedGold 🪙 Altın, +$earnedPrestige 🌿 Prestij)"
            } else {
                val crowdLiked = baseBattle.crowdBloodlustPercent < 60
                val crowdNote = if (crowdLiked) "Halkın bağışlama isteği onurlandırıldı, büyük itibar kazanıldı." else "Kan isteyen ayaktakımına rağmen soylu bir merhamet gösterildi."
                "🕊️ ASİL MERHAMET ZAFERİ! ${baseBattle.playerGladiator.name}, ${enemy.name}'un canını bağışladı. $crowdNote (+$earnedGold 🪙 Altın, +$earnedPrestige 🌿 Prestij)"
            }
        } else {
            "💀 YENİLGİ! ${baseBattle.playerGladiator.name} arenada mağlup oldu ve acil hekim müdahalesine alındı."
        }

        val completedBattle = baseBattle.copy(
            isFinished = true,
            isAwaitingCrowdJudgement = false,
            isPlayerVictorious = isVictory,
            playerSurrenderedOrDied = isDefeat,
            crowdJudgementDecision = judgement,
            earnedGold = earnedGold,
            earnedPrestige = max(0, earnedPrestige),
            outcomeSummary = summary
        )

        _uiState.update { it.copy(activeBattle = completedBattle, isSimulationRunning = false) }

        viewModelScope.launch {
            repository.recordMatchResolution(completedBattle)
            if (isVictory && completedBattle.campaignMissionId != null) {
                val campaignResult = repository.completeCampaignMission(completedBattle.campaignMissionId)
                _uiState.update { it.copy(notificationMessage = campaignResult) }
            }
            refreshOpponents()
        }
    }

    fun instantFinishBattle() {
        val current = _uiState.value.activeBattle ?: return
        if (current.isFinished) return
        setBattleSpeed(0.0f)
    }

    fun shoutLanistaTactic(shoutType: String) {
        val current = _uiState.value.activeBattle ?: return
        if (current.isFinished || current.isAwaitingCrowdJudgement) return

        val newLogs = current.logs.toMutableList()
        val text = when (shoutType) {
            "HYPE" -> "📢 LANISTA HAYKIRIŞI: 'Roma bizi izliyor! Bastır ve arenayı sars!' (+10 Hype)"
            "DEFENSE" -> "🛡️ LANISTA KOMUTU: 'Kalkanını göğsünden ayırma, açığını kapat!' (Savunma Uyarısı)"
            "ATTACK" -> "⚡ LANISTA KOMUTU: 'Gözünü ayırma, açık gördüğün an sapla!' (Kritik Uyarısı)"
            else -> "📢 LANISTA: 'Dövüşün ve Ludus'un onuru için!'"
        }
        val addedHype = if (shoutType == "HYPE") 10 else 4
        newLogs.add(BattleActionLog(text = text, isPlayerAction = true, crowdReaction = "Seyirciler alkışladı!"))

        _uiState.update {
            it.copy(
                activeBattle = current.copy(
                    crowdHype = (current.crowdHype + addedHype).coerceAtMost(100),
                    logs = newLogs
                )
            )
        }
    }

    fun dismissBattle() {
        battleJob?.cancel()
        _uiState.update { it.copy(activeBattle = null, isSimulationRunning = false) }
    }

    // Market Actions
    fun recruitGladiator(candidate: Gladiator) {
        viewModelScope.launch {
            val success = repository.recruitGladiator(candidate, candidate.priceValue)
            if (success) {
                _uiState.update {
                    it.copy(
                        marketCandidates = it.marketCandidates.filter { g -> g.name != candidate.name },
                        notificationMessage = "${candidate.name} kadroya dahil edildi!"
                    )
                }
            } else {
                _uiState.update { it.copy(notificationMessage = "Yetersiz altın veya azami kontenjan dolu!") }
            }
        }
    }

    fun upgradePhysician() {
        val currentLevel = _uiState.value.ludusState.physicianLevel
        val cost = when (currentLevel) {
            1 -> 250
            2 -> 600
            else -> 0
        }
        if (cost == 0) return
        viewModelScope.launch {
            val success = repository.upgradePhysician(cost)
            if (success) {
                _uiState.update { it.copy(notificationMessage = "Hekim kadrosu yükseltildi!") }
            } else {
                _uiState.update { it.copy(notificationMessage = "Hekim terfisi için yeterli altın yok!") }
            }
        }
    }

    fun hireGuard() {
        val cost = 120
        viewModelScope.launch {
            val success = repository.hireGuard(cost)
            if (success) {
                _uiState.update { it.copy(notificationMessage = "Ludus'a yeni muhafız yerleştirildi!") }
            } else {
                _uiState.update { it.copy(notificationMessage = "Muhafız kiralamak için yeterli altın yok!") }
            }
        }
    }

    fun repayDebt(amount: Int) {
        viewModelScope.launch {
            val success = repository.repayDebt(amount)
            if (success) {
                _uiState.update { it.copy(notificationMessage = "$amount Altın borç ödendi!") }
            } else {
                _uiState.update { it.copy(notificationMessage = "Borç ödemesi için bakiye yetersiz!") }
            }
        }
    }

    // Shop Actions
    fun buyInstantHealPotion(gladiatorId: Long) {
        viewModelScope.launch {
            repository.applyInstantHealPotion(gladiatorId)
            _uiState.update { it.copy(notificationMessage = "Şifalı iksir uygulandı! Gladyatör tamamen iyileşti.") }
        }
    }

    fun applyHerbalPoulticeTreatment(gladiatorId: Long) {
        viewModelScope.launch {
            val success = repository.applyHerbalPoulticeTreatment(gladiatorId, cost = 30)
            if (success) {
                _uiState.update { it.copy(notificationMessage = "🌿 Şifalı bitki sargısı uygulandı (-1 Gün İyileşme, +35 HP, -30 Altın)!") }
            } else {
                _uiState.update { it.copy(notificationMessage = "Tedavi için yeterli altın yok!") }
            }
        }
    }

    fun applyThermalBathTreatment(gladiatorId: Long) {
        viewModelScope.launch {
            val success = repository.applyThermalBathTreatment(gladiatorId, cost = 40)
            if (success) {
                _uiState.update { it.copy(notificationMessage = "🏺 Termal hamam terapisi uygulandı (Yorgunluk sıfırlandı, +15 Moral, -40 Altın)!") }
            } else {
                _uiState.update { it.copy(notificationMessage = "Hamam terapisi için yeterli altın yok!") }
            }
        }
    }

    fun performEmergencySurgery(gladiatorId: Long) {
        viewModelScope.launch {
            val success = repository.performEmergencySurgery(gladiatorId, cost = 120)
            if (success) {
                _uiState.update { it.copy(notificationMessage = "⚡ Cerrahi operasyon başarılı! Kalıcı sakatlık giderildi (-120 Altın).") }
            } else {
                val pLvl = _uiState.value.ludusState.physicianLevel
                val msg = if (pLvl < 2) "Cerrahi müdahale için en az Seviye 2 Lejyon Cerrahı gereklidir!" else "Ameliyat masrafı için yeterli altın yok!"
                _uiState.update { it.copy(notificationMessage = msg) }
            }
        }
    }

    fun applyInstantHealWithGold(gladiatorId: Long) {
        viewModelScope.launch {
            val success = repository.applyInstantHealWithGold(gladiatorId, cost = 85)
            if (success) {
                _uiState.update { it.copy(notificationMessage = "🧪 Antik mucize iksir uygulandı! Gladyatör tamamen iyileşti (-85 Altın).") }
            } else {
                _uiState.update { it.copy(notificationMessage = "İksir için yeterli altın yok!") }
            }
        }
    }

    fun buyExpansionSlots() {
        viewModelScope.launch {
            repository.expandLudusSlots()
            _uiState.update { it.copy(notificationMessage = "Ludus kışlası genişletildi! +2 Gladyatör kontenjanı açıldı.") }
        }
    }

    fun claimRewardedAd() {
        viewModelScope.launch {
            val (gold, prestige) = repository.claimRewardedAdBonus()
            _uiState.update { it.copy(notificationMessage = "Ödüllü destek alındı: +$gold Altın, +$prestige Prestij!") }
        }
    }

    fun buySenatorSponsorship() {
        viewModelScope.launch {
            repository.activateSenatorSponsorship(14)
            _uiState.update { it.copy(notificationMessage = "Senatör Marcus ile 14 günlük cömert sponsorluk anlaşması imzalandı!") }
        }
    }

    // --- Between-Cycle Sparring Actions ---
    fun openSparringDialog(f1: Gladiator? = null, f2: Gladiator? = null) {
        val g1 = f1 ?: _uiState.value.selectedGladiator ?: _uiState.value.gladiators.firstOrNull()
        val g2 = f2 ?: _uiState.value.gladiators.firstOrNull { it.id != g1?.id }
        _uiState.update {
            it.copy(
                showSparringDialog = true,
                sparringFighter1 = g1,
                sparringFighter2 = g2,
                activeSparring = null
            )
        }
    }

    fun closeSparringDialog() {
        _uiState.update { it.copy(showSparringDialog = false, activeSparring = null) }
    }

    fun setSparringFighters(f1: Gladiator, f2: Gladiator?) {
        _uiState.update { it.copy(sparringFighter1 = f1, sparringFighter2 = f2) }
    }

    fun executeSparring() {
        val f1 = _uiState.value.sparringFighter1 ?: return
        val f2 = _uiState.value.sparringFighter2
        viewModelScope.launch {
            val result = repository.performSparring(f1.id, f2?.id)
            _uiState.update { it.copy(activeSparring = result) }
        }
    }

    // --- Between-Cycle Tesserae Gambling ---
    fun openTesseraeDialog() {
        _uiState.update { it.copy(showTesseraeDialog = true) }
    }

    fun closeTesseraeDialog() {
        _uiState.update { it.copy(showTesseraeDialog = false) }
    }

    fun rollTesserae(bet: Int) {
        viewModelScope.launch {
            val state = repository.playTesserae(bet)
            _uiState.update { it.copy(activeTesserae = state) }
        }
    }

    // --- Between-Cycle Subura Tavern & Espionage Actions ---
    fun openSuburaTavernDialog() {
        _uiState.update { it.copy(showSuburaTavernDialog = true) }
    }

    fun closeSuburaTavernDialog() {
        _uiState.update { it.copy(showSuburaTavernDialog = false) }
    }

    fun scoutOpponentWeakness() {
        viewModelScope.launch {
            val success = repository.scoutOpponentWeakness()
            if (success) {
                _uiState.update { it.copy(notificationMessage = "🎯 Casus Subura'dan döndü! Rakibin zırh açığı tespit edildi (+%15 Hasar Bonusu).") }
            } else {
                _uiState.update { it.copy(notificationMessage = "Yetersiz altın! Casus kiralamak 25 Altın gerektirir.") }
            }
        }
    }

    fun buyMulsumFeast() {
        viewModelScope.launch {
            val success = repository.buyMulsumFeast()
            if (success) {
                _uiState.update { it.copy(notificationMessage = "🍷 Mulsum Şöleni verildi! Tüm kadronun yorgunluğu silindi ve +25 Moral kazandılar.") }
            } else {
                _uiState.update { it.copy(notificationMessage = "Yetersiz altın! Mulsum şöleni 30 Altın gerektirir.") }
            }
        }
    }

    fun bribeTownCriers() {
        viewModelScope.launch {
            val success = repository.bribeTownCriers()
            if (success) {
                _uiState.update { it.copy(notificationMessage = "📢 Şehir tellallarına rüşvet verildi! +20 Prestij kazanıldı ve tribünler arkanızda olacak.") }
            } else {
                _uiState.update { it.copy(notificationMessage = "Yetersiz altın! Tellalları tutmak 25 Altın gerektirir.") }
            }
        }
    }

    fun applyThermalMassage(gladiatorId: Long) {
        viewModelScope.launch {
            val success = repository.applyThermalMassage(gladiatorId)
            if (success) {
                _uiState.update { it.copy(notificationMessage = "🛁 Hamam ve masaj uygulandı! Yorgunluk sıfırlandı, can yenilendi.") }
            } else {
                _uiState.update { it.copy(notificationMessage = "Yetersiz altın! Hamam bakımı 15 Altın gerektirir.") }
            }
        }
    }

    fun sharpenArsenal() {
        viewModelScope.launch {
            val success = repository.sharpenArsenal()
            if (success) {
                _uiState.update { it.copy(notificationMessage = "🗡️ Silahlar bilendi! Sıradaki dövüşte +%15 Kritik vuruş şansı aktif.") }
            } else {
                _uiState.update { it.copy(notificationMessage = "Yetersiz altın! Silah bileme 20 Altın gerektirir.") }
            }
        }
    }

    fun accelerateInjuryHeal(gladiatorId: Long) {
        viewModelScope.launch {
            val success = repository.accelerateInjuryHeal(gladiatorId)
            if (success) {
                _uiState.update { it.copy(notificationMessage = "🌿 Özel bitkisel tedavi uygulandı! İyileşme süresi 1 gün kısaltıldı.") }
            } else {
                _uiState.update { it.copy(notificationMessage = "Yetersiz altın veya gladyatör yaralı değil.") }
            }
        }
    }

    // --- Daily Dilemmas ---
    fun openDilemmaDialog() {
        _uiState.update { it.copy(showDilemmaDialog = true) }
    }

    fun closeDilemmaDialog() {
        _uiState.update { it.copy(showDilemmaDialog = false) }
    }

    fun chooseDilemmaOption(option: DilemmaOption) {
        viewModelScope.launch {
            val outcome = repository.resolveDilemma(option)
            _uiState.update { it.copy(lastDilemmaOutcome = outcome, notificationMessage = outcome) }
        }
    }

    // --- Between-Cycle Events & Festivals ---
    fun openBetweenCycleEventsDialog(forceNew: Boolean = false) {
        val currentDay = _uiState.value.ludusState.day
        val cityTier = _uiState.value.ludusState.cityTier
        val existingEvent = _uiState.value.activeBetweenCycleEvent
        val eventToOpen = if (existingEvent == null || forceNew) {
            repository.generateBetweenCycleEvent(currentDay, cityTier, _uiState.value.ludusState, forceRandom = forceNew)
        } else {
            existingEvent
        }

        val firstAvailableGladiator = _uiState.value.gladiators.firstOrNull { !it.isInjured } ?: _uiState.value.gladiators.firstOrNull()

        _uiState.update {
            it.copy(
                showBetweenCycleEventDialog = true,
                activeBetweenCycleEvent = eventToOpen,
                selectedEventGladiator = firstAvailableGladiator,
                lastEventResult = null
            )
        }
    }

    fun rollRandomDowntimeEvent() {
        val currentDay = _uiState.value.ludusState.day
        val cityTier = _uiState.value.ludusState.cityTier
        val newEvent = repository.generateBetweenCycleEvent(currentDay, cityTier, _uiState.value.ludusState, forceRandom = true)
        val firstAvailableGladiator = _uiState.value.gladiators.firstOrNull { !it.isInjured } ?: _uiState.value.gladiators.firstOrNull()

        _uiState.update {
            it.copy(
                showBetweenCycleEventDialog = true,
                activeBetweenCycleEvent = newEvent,
                selectedEventGladiator = firstAvailableGladiator,
                lastEventResult = null,
                notificationMessage = "🎲 Yeni Rastgele Olay / Ziyaretçi: ${newEvent.title}"
            )
        }
    }

    fun closeBetweenCycleEventsDialog() {
        _uiState.update {
            it.copy(
                showBetweenCycleEventDialog = false,
                lastEventResult = null
            )
        }
    }

    fun selectEventGladiator(gladiator: Gladiator) {
        _uiState.update { it.copy(selectedEventGladiator = gladiator) }
    }

    fun chooseBetweenCycleEventChoice(choice: EventDecisionChoice) {
        val currentEvent = _uiState.value.activeBetweenCycleEvent ?: return
        val targetGladiator = _uiState.value.selectedEventGladiator

        viewModelScope.launch {
            val result = repository.resolveBetweenCycleEventChoice(
                event = currentEvent,
                choice = choice,
                targetGladiatorId = targetGladiator?.id
            )
            _uiState.update {
                it.copy(
                    lastEventResult = result,
                    notificationMessage = "📜 ${result.title}: Karar uygulandı."
                )
            }
        }
    }

    // --- ARMORY & EQUIPMENT ---
    fun buyAndEquipItem(gladiatorId: Long, itemId: String) {
        viewModelScope.launch {
            val result = repository.buyAndEquipItem(gladiatorId, itemId)
            _uiState.update { it.copy(notificationMessage = result) }
        }
    }

    fun unequipItem(gladiatorId: Long, slot: EquipmentSlot) {
        viewModelScope.launch {
            val result = repository.unequipItem(gladiatorId, slot)
            _uiState.update { it.copy(notificationMessage = result) }
        }
    }

    // --- LANISTA PERKS ---
    fun unlockLanistaPerk(perk: LanistaPerk) {
        viewModelScope.launch {
            val result = repository.unlockLanistaPerk(perk)
            _uiState.update { it.copy(notificationMessage = result) }
        }
    }

    // --- CAMPAIGN & BOSS FIGHTS ---
    fun startCampaignBossFight(mission: CampaignMission, gladiator: Gladiator) {
        selectGladiator(gladiator)
        selectOpponent(mission.bossEnemy)
        selectFormat(MatchFormat.SINE_MISSIO)
        startBattleSimulation(campaignMissionId = mission.id)
    }

    // --- UNDERGROUND & VENATIO FIGHTS ---
    fun startUndergroundFight(gladiator: Gladiator, fightType: UndergroundFightType) {
        val enemy = when (fightType) {
            UndergroundFightType.VENATIO_BEASTS -> EnemyGladiator(
                name = "Vahşi Nemea Aslanı",
                title = "Afrika Arenası Yırtıcısı",
                ludusOrigin = "Vahşi Hayvan Kafesleri",
                gladiatorClass = GladiatorClass.DIMACHAERUS,
                tier = EnemyTier.VETERAN,
                level = 4,
                traitName = "Yırtıcı Pençe & Hız",
                str = 17, agi = 18, sta = 15, mor = 95,
                maxHp = 160, currentHp = 160,
                rewardGold = fightType.goldReward, rewardPrestige = fightType.prestigeReward
            )
            UndergroundFightType.NIGHT_PIT_DUEL -> EnemyGladiator(
                name = "Gölge Cellat Gorgon",
                title = "Yeraltı Kan Çukuru Efendisi",
                ludusOrigin = "Subura Kan Çukuru",
                gladiatorClass = GladiatorClass.SECUTOR,
                tier = EnemyTier.ELITE,
                level = 6,
                traitName = "Kural Tanımaz & Ağır Darbe",
                str = 20, agi = 15, sta = 20, mor = 90,
                maxHp = 210, currentHp = 210,
                rewardGold = fightType.goldReward, rewardPrestige = fightType.prestigeReward
            )
        }
        selectGladiator(gladiator)
        selectOpponent(enemy)
        selectFormat(MatchFormat.SINE_MISSIO)
        startBattleSimulation()
    }
}

