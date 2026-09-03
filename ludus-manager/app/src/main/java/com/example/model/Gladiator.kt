package com.example.model

enum class GladiatorClass(
    val title: String,
    val description: String,
    val primaryWeapon: String,
    val secondaryWeapon: String,
    val armorStyle: String,
    val spriteResourceName: String = "ic_sprite_murmillo"
) {
    MURMILLO("Murmillo", "Heavy shield & gladius sword. Defensive juggernaut.", "Gladius", "Scutum Shield", "Heavy Helmet & Greave", "ic_sprite_murmillo"),
    THRAEX("Thraex", "Sica curved sword & parma shield. High speed critical master.", "Sica Sword", "Parma Shield", "Crested Helmet & High Greaves", "ic_sprite_thraex"),
    RETIARIUS("Retiarius", "Net & trident. Agile distance and entangling specialist.", "Trident", "Weighted Net", "Galerus Armguard", "ic_sprite_retiarius"),
    SECUTOR("Secutor", "Smooth round helmet chaser, specifically trained against Retiarii.", "Gladius", "Oval Shield", "Smooth Heavy Helmet", "ic_sprite_secutor"),
    HOPLOMACHUS("Hoplomachus", "Long thrusting spear & dagger. Balanced range fighter.", "Hasta Spear", "Round Shield", "Bronze Visor", "ic_sprite_hoplomachus"),
    DIMACHAERUS("Dimachaerus", "Dual gladius blades. Fierce relentless offense.", "Dual Blades", "Parrying Dagger", "Leather Cuirass", "ic_sprite_dimachaerus"),
    BESTIARIUS("Bestiarius", "Beast hunter specialized in ferocious beasts and crowd frenzy.", "Venabulum Spear", "Short Pike", "Reinforced Mail", "ic_sprite_bestiarius"),
    HEAVY_VETERAN("Heavy Veteran", "Seasoned legionary-gladiator with battle-scarred armor.", "Spatha", "Tower Scutum", "Segmentata & Iron Mask", "ic_sprite_murmillo"),
    LIGHT_DUELIST("Light Duelist", "Agile fencing virtuoso dancing around heavy strikes.", "Gladius", "Parrying Pugio", "Leather Vambrace", "ic_sprite_thraex"),
    SPECIAL_ARCHETYPE("Special Archetype", "Exotic foreign champion wielding rare regional armaments.", "Chain Flail", "Bronze Buckler", "Scale Cuirass", "ic_sprite_dimachaerus")
}

enum class GladiatorStatus(val displayName: String) {
    SLAVE("Slave (Servus)"),
    AUCTORATUS("Contracted (Auctoratus)"),
    FREEDMAN("Freedman (Libertus)"),
    RUDIS_HOLDER("Rudis Champion"),
    VETERAN("Veteran"),
    RETIRED("Retired"),
    DEAD("Dead (In Aeternum)")
}

enum class Origin(val region: String, val culturalTrait: String) {
    ITALIA("Italia", "Disciplined legionary tradition"),
    THRAX("Thrace", "Ferocious combat instinct"),
    GAUL("Gaul", "Massive physique and endurance"),
    GERMANIA("Germania", "Unbreakable pain tolerance"),
    HISPANIA("Hispania", "Swift footwork and blade mastery"),
    GREECE("Greece", "Pankration grappling prowess"),
    NUMIDIA("North Africa", "Sun-hardened stamina & reflex"),
    SYRIA("Syria", "Cunning timing and feints")
}

enum class Personality(val label: String, val effect: String) {
    AMBITIOUS("Ambitious", "Seeks glory; gains double morale from victories"),
    DISCIPLINED("Disciplined", "95% obedience to tactical commands in battle"),
    RECKLESS("Reckless", "High critical hit chance, but prone to taking damage"),
    PROUD("Proud", "High crowd appeal; resents benching or low pay"),
    CALM("Calm", "Immune to panic when health drops low"),
    BRUTAL("Brutal", "Excels at causing blood loss; loved by vicious crowds"),
    LOYAL("Loyal", "Will never betray or request transfer"),
    SHOWMAN("Showman", "Dramatically boosts arena hype and betting odds")
}

/**
 * Physical statistics and bodily attributes of a Gladiator.
 */
data class PhysicalStats(
    var strength: Int = 14,          // 1 - 20 rating
    var agility: Int = 13,           // 1 - 20 rating
    var endurance: Int = 14,         // 1 - 20 rating
    var speed: Int = 14,             // 1 - 20 rating
    var reflex: Int = 13,            // 1 - 20 rating
    var painTolerance: Int = 14,     // 1 - 20 rating
    var heightCm: Int = 182,         // In centimeters
    var weightKg: Int = 84,          // In kilograms
    var reachCm: Int = 87,           // Arm reach in cm
    var muscleDensity: Int = 80,     // 1 - 100 percentage
    val bodyType: String = "Muscular"// "Muscular", "Agile", "Colossus", "Lean"
) {
    val bmi: Float get() = if (heightCm > 0) weightKg.toFloat() / ((heightCm / 100f) * (heightCm / 100f)) else 24f
    val athleticScore: Int get() = ((strength * 1.3f) + (speed * 1.1f) + (endurance * 1.2f) + (agility * 1.0f) + reflex + painTolerance).toInt()
    val powerModifier: Float get() = 0.8f + (strength / 20f) * 0.4f
}

/**
 * Training progress, regimen focus, and discipline mastery for a Gladiator.
 */
data class TrainingProgress(
    var currentFocus: String = "Kılıç & Kalkan Çalışması",
    var dailyProgressPercent: Float = 45f,
    var totalTrainingDays: Int = 30,
    var experiencePoints: Int = 400,
    var level: Int = 2,
    var nextLevelThreshold: Int = 1000,
    var dietRegimen: String = "High Protein (Gladiator Barley & Meat)",
    var fatigueAccrued: Int = 15,        // 0 - 100 fatigue accumulation
    var weaponMastery: Int = 55,         // 1 - 100 mastery scale
    var shieldMastery: Int = 50,         // 1 - 100
    var footworkMastery: Int = 52,       // 1 - 100
    var tacticalDiscipline: Int = 60,    // 1 - 100
    var assignedInstructor: String = "Marcus (Doctore)"
) {
    val progressToNextLevel: Float get() = (experiencePoints.toFloat() / nextLevelThreshold.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val isReadyToLevelUp: Boolean get() = experiencePoints >= nextLevelThreshold
    val trainingEfficiencyScore: Int get() = (100 - fatigueAccrued).coerceIn(20, 100)
}

/**
 * Historical combat record, arena accolades, and career performance metrics.
 */
data class HistoricalPerformance(
    var totalMatches: Int = 0,
    var victories: Int = 0,
    var defeats: Int = 0,
    var kills: Int = 0,
    var sparedByCrowd: Int = 0,
    var currentWinStreak: Int = 0,
    var bestWinStreak: Int = 0,
    var crowdApprovalRating: Int = 60,   // 0 - 100 percentage
    var totalDenariiEarned: Int = 0,
    var criticalStrikesDelivered: Int = 0,
    var timesDisarmed: Int = 0,
    var arenasFoughtIn: MutableList<String> = mutableListOf("Capua"),
    var titlesWon: MutableList<String> = mutableListOf()
) {
    val winRatePercent: Float get() = if (totalMatches > 0) (victories.toFloat() / totalMatches.toFloat()) * 100f else 0f
    val lethalityRatePercent: Float get() = if (victories > 0) (kills.toFloat() / victories.toFloat()) * 100f else 0f
    val recordSummary: String get() = "$victories-$defeats ($kills Kills)"
}

/**
 * Visual sprite configuration for in-game rendering of the gladiator.
 */
data class GladiatorSprite(
    val spriteResourceName: String = "ic_sprite_murmillo",
    val primaryColorHex: Long = 0xFF9E2A2B,
    val metalTrimHex: Long = 0xFFD4AF37,
    val stance: String = "Ready Stance",
    val scaleFactor: Float = 1.0f
)

data class GladiatorAttributes(
    // Physical (Scale 1 - 20)
    var strength: Int,
    var speed: Int,
    var agility: Int,
    var endurance: Int,
    var reflex: Int,
    var painTolerance: Int,

    // Combat (Scale 1 - 20)
    var swordsmanship: Int,
    var shieldSkill: Int,
    var grappling: Int,
    var footwork: Int,
    var counterAttack: Int,

    // Mental (Scale 1 - 20)
    var courage: Int,
    var discipline: Int,
    var composure: Int,

    // Hidden Potential (1 - 20)
    val hiddenPotential: Int = 15
)

data class GladiatorCondition(
    var health: Int = 100,
    var stamina: Int = 100,
    var pain: Int = 0,
    var stress: Int = 10,
    var morale: Int = 80,
    var bloodLoss: Int = 0,
    var recovery: Int = 85
)

data class Injury(
    val id: String,
    val name: String,
    val description: String,
    var daysRemaining: Int,
    val severity: String, // "Light", "Moderate", "Severe"
    val statDebuff: String
)

data class Relationship(
    val targetName: String,
    val type: String, // "Rivalry", "Friendship", "Mentorship"
    val description: String
)

data class CareerStats(
    var fights: Int = 0,
    var wins: Int = 0,
    var losses: Int = 0,
    var kills: Int = 0,
    var crowdFavor: Int = 50,
    var denariiEarned: Int = 0
)

/**
 * Complete, Canonical Gladiator entity representing every fighter in the world simulation:
 * - Player ludus gladiators
 * - Circuit contenders and champions
 * - Underground fighters and persistent bosses
 * - Recruitable slaves and freemen
 * - Fallen historical gladiators
 */
data class Gladiator(
    val id: String,
    var name: String,
    val gladiatorClass: GladiatorClass,
    val origin: Origin,
    var status: GladiatorStatus = GladiatorStatus.AUCTORATUS,
    var age: Int = 25,
    val personality: Personality = Personality.DISCIPLINED,
    var rank: Int = 1,
    var monthlyWage: Int = 50,
    var contractMonths: Int = 24,

    // Circuit, Opponent, Narrative & Underground properties:
    var nickname: String = "",
    var ethnicity: String = "Roman",
    var ludusAffiliation: String = "Ludus Valerius",
    var ownerName: String = "Dominus Valerius",
    var tier: OpponentTier = OpponentTier.COMMON,
    var aiPersonality: AiTacticalPersonality = AiTacticalPersonality.VETERAN,
    var signatureTactic: String = "Standard Duel",
    var equipmentSummary: String = "",
    var currentArena: ArenaVenueId = ArenaVenueId.CAPUA,
    var rankingPosition: Int = 1,
    var isChampion: Boolean = false,
    var isRetired: Boolean = false,
    var isUnderground: Boolean = false,
    var fatigue: Int = 0,
    var lastFightDay: Int = 0,
    var fightCountAgainstPlayer: Int = 0,
    var undergroundBossState: BossState? = null,
    val championshipTitles: MutableList<String> = mutableListOf(),
    val notableVictories: MutableList<String> = mutableListOf(),
    val notableDefeats: MutableList<String> = mutableListOf(),
    val knownWeaknesses: MutableList<String> = mutableListOf(),
    val knownInjuries: MutableList<String> = mutableListOf(),
    val recentForm: MutableList<String> = mutableListOf("W", "W", "W", "L", "W"),

    // Requested Core Properties:
    var physicalStats: PhysicalStats = PhysicalStats(),
    var trainingProgress: TrainingProgress = TrainingProgress(),
    var historicalPerformance: HistoricalPerformance = HistoricalPerformance(),

    // Visual Sprite representation:
    var sprite: GladiatorSprite = GladiatorSprite(spriteResourceName = gladiatorClass.spriteResourceName),

    // Dynamic condition & statuses:
    var condition: GladiatorCondition = GladiatorCondition(),
    val injuries: MutableList<Injury> = mutableListOf(),
    val relationships: MutableList<Relationship> = mutableListOf(),

    // Legacy and helper fields for backward compatibility:
    var attributes: GladiatorAttributes = GladiatorAttributes(
        strength = physicalStats.strength,
        speed = physicalStats.speed,
        agility = physicalStats.agility,
        endurance = physicalStats.endurance,
        reflex = physicalStats.reflex,
        painTolerance = physicalStats.painTolerance,
        swordsmanship = 14,
        shieldSkill = 14,
        grappling = 13,
        footwork = 14,
        counterAttack = 14,
        courage = 15,
        discipline = 15,
        composure = 14
    ),
    var careerStats: CareerStats = CareerStats(
        fights = historicalPerformance.totalMatches,
        wins = historicalPerformance.victories,
        losses = historicalPerformance.defeats,
        kills = historicalPerformance.kills,
        crowdFavor = historicalPerformance.crowdApprovalRating,
        denariiEarned = historicalPerformance.totalDenariiEarned
    ),
    var trainingFocus: String = trainingProgress.currentFocus,
    var diet: String = trainingProgress.dietRegimen,
    var heightCm: Int = physicalStats.heightCm,
    var weightKg: Int = physicalStats.weightKg,
    var level: Int = trainingProgress.level,
    var experience: Int = trainingProgress.experiencePoints,
    var isAlive: Boolean = true,
    var deathDay: Int? = null,
    var killedBy: String? = null,
    var causeOfDeath: String? = null,
    var deathArena: String? = null
) {
    val isDead: Boolean get() = !isAlive || status == GladiatorStatus.DEAD
    val isInjured: Boolean get() = injuries.isNotEmpty()
    val isReadyForCombat: Boolean get() = isAlive && !isDead && condition.health >= 50 && condition.stamina >= 40 && injuries.none { it.severity == "Severe" }
    val overallRating: Int
        get() = ((physicalStats.strength + attributes.swordsmanship + attributes.shieldSkill + physicalStats.endurance + attributes.discipline) / 5) * 5

    val fullDisplayName: String
        get() = if (nickname.isNotBlank()) "$name \"$nickname\"" else name

    val recordSummary: String
        get() = "${historicalPerformance.victories}G - ${historicalPerformance.defeats}M (${historicalPerformance.kills} İnfaz)"

    var wins: Int
        get() = historicalPerformance.victories
        set(v) { historicalPerformance.victories = v; careerStats.wins = v }

    var losses: Int
        get() = historicalPerformance.defeats
        set(v) { historicalPerformance.defeats = v; careerStats.losses = v }

    var kills: Int
        get() = historicalPerformance.kills
        set(v) { historicalPerformance.kills = v; careerStats.kills = v }

    var spared: Int
        get() = historicalPerformance.sparedByCrowd
        set(v) { historicalPerformance.sparedByCrowd = v }

    var currentWinStreak: Int
        get() = historicalPerformance.currentWinStreak
        set(v) { historicalPerformance.currentWinStreak = v }

    var prestige: Int
        get() = (crowdApproval * 8) + (wins * 80)
        set(v) { crowdApproval = (v / 10).coerceIn(0, 100) }

    var crowdApproval: Int
        get() = historicalPerformance.crowdApprovalRating
        set(v) { historicalPerformance.crowdApprovalRating = v; careerStats.crowdFavor = v }

    var denariiEarned: Int
        get() = historicalPerformance.totalDenariiEarned
        set(v) { historicalPerformance.totalDenariiEarned = v; careerStats.denariiEarned = v }

    var currentHealth: Int
        get() = condition.health
        set(v) { condition.health = v }

    val strength: Int get() = physicalStats.strength
    val speed: Int get() = physicalStats.speed
    val agility: Int get() = physicalStats.agility
    val endurance: Int get() = physicalStats.endurance
    val reflex: Int get() = physicalStats.reflex
    val painTolerance: Int get() = physicalStats.painTolerance
    val swordsmanship: Int get() = attributes.swordsmanship
    val shieldSkill: Int get() = attributes.shieldSkill
    val discipline: Int get() = attributes.discipline

    val careerTier: CareerTier
        get() = CareerTier.calculateTier(wins, prestige, isChampion, currentArena == ArenaVenueId.ROME)

    fun toGladiator(): Gladiator = this

    constructor(
        id: String,
        name: String,
        nickname: String = "",
        age: Int = 25,
        origin: Origin = Origin.ITALIA,
        ethnicity: String = "Roman",
        gladiatorClass: GladiatorClass = GladiatorClass.MURMILLO,
        ludusAffiliation: String = "Independent",
        ownerName: String = "Lanista",
        tier: OpponentTier = OpponentTier.COMMON,
        aiPersonality: AiTacticalPersonality = AiTacticalPersonality.VETERAN,
        signatureTactic: String = "Balanced Stance",
        equipmentSummary: String = "",
        strength: Int = 14,
        speed: Int = 14,
        agility: Int = 13,
        endurance: Int = 14,
        reflex: Int = 13,
        painTolerance: Int = 14,
        swordsmanship: Int = 14,
        shieldSkill: Int = 14,
        discipline: Int = 14,
        wins: Int = 0,
        losses: Int = 0,
        kills: Int = 0,
        spared: Int = 0,
        currentWinStreak: Int = 0,
        prestige: Int = 300,
        crowdApproval: Int = 60,
        denariiEarned: Int = 0,
        currentArena: ArenaVenueId = ArenaVenueId.CAPUA,
        rankingPosition: Int = 1,
        currentHealth: Int = 100,
        isChampion: Boolean = false,
        championshipTitles: MutableList<String> = mutableListOf(),
        notableVictories: MutableList<String> = mutableListOf(),
        notableDefeats: MutableList<String> = mutableListOf(),
        knownWeaknesses: MutableList<String> = mutableListOf(),
        knownInjuries: MutableList<String> = mutableListOf(),
        recentForm: MutableList<String> = mutableListOf("W", "W", "W", "L", "W"),
        isAlive: Boolean = true,
        isRetired: Boolean = false,
        deathDay: Int? = null,
        killedBy: String? = null,
        causeOfDeath: String? = null,
        deathArena: String? = null,
        isUnderground: Boolean = false,
        fatigue: Int = 0,
        undergroundBossState: BossState? = null
    ) : this(
        id = id,
        name = name,
        gladiatorClass = gladiatorClass,
        origin = origin,
        status = if (!isAlive) GladiatorStatus.DEAD else if (isChampion) GladiatorStatus.RUDIS_HOLDER else GladiatorStatus.AUCTORATUS,
        age = age,
        personality = when (aiPersonality) {
            AiTacticalPersonality.AGGRESSOR, AiTacticalPersonality.BERSERKER -> Personality.BRUTAL
            AiTacticalPersonality.DUELIST, AiTacticalPersonality.VETERAN -> Personality.DISCIPLINED
            AiTacticalPersonality.CROWD_PLAYER -> Personality.SHOWMAN
            AiTacticalPersonality.TANK -> Personality.CALM
            AiTacticalPersonality.TRICKSTER -> Personality.AMBITIOUS
        },
        rank = rankingPosition,
        nickname = nickname,
        ethnicity = ethnicity,
        ludusAffiliation = ludusAffiliation,
        ownerName = ownerName,
        tier = tier,
        aiPersonality = aiPersonality,
        signatureTactic = signatureTactic,
        equipmentSummary = equipmentSummary,
        currentArena = currentArena,
        rankingPosition = rankingPosition,
        isChampion = isChampion,
        isRetired = isRetired,
        isUnderground = isUnderground,
        fatigue = fatigue,
        undergroundBossState = undergroundBossState,
        championshipTitles = championshipTitles,
        notableVictories = notableVictories,
        notableDefeats = notableDefeats,
        knownWeaknesses = knownWeaknesses,
        knownInjuries = knownInjuries,
        recentForm = recentForm,
        physicalStats = PhysicalStats(
            strength = strength,
            speed = speed,
            agility = agility,
            endurance = endurance,
            reflex = reflex,
            painTolerance = painTolerance,
            heightCm = 180 + (strength % 10),
            weightKg = 80 + (strength % 15)
        ),
        trainingProgress = TrainingProgress(
            currentFocus = signatureTactic,
            level = when (tier) {
                OpponentTier.COMMON -> 1
                OpponentTier.UNCOMMON -> 2
                OpponentTier.ELITE -> 3
                OpponentTier.CHAMPION -> 4
                OpponentTier.LEGEND -> 5
            },
            weaponMastery = (swordsmanship * 5).coerceIn(10, 100),
            shieldMastery = (shieldSkill * 5).coerceIn(10, 100)
        ),
        historicalPerformance = HistoricalPerformance(
            totalMatches = wins + losses,
            victories = wins,
            defeats = losses,
            kills = kills,
            sparedByCrowd = spared,
            currentWinStreak = currentWinStreak,
            crowdApprovalRating = crowdApproval,
            totalDenariiEarned = denariiEarned,
            arenasFoughtIn = mutableListOf(currentArena.venueName)
        ),
        attributes = GladiatorAttributes(
            strength = strength,
            speed = speed,
            agility = agility,
            endurance = endurance,
            reflex = reflex,
            painTolerance = painTolerance,
            swordsmanship = swordsmanship,
            shieldSkill = shieldSkill,
            grappling = (strength + agility) / 2,
            footwork = agility,
            counterAttack = reflex,
            courage = 15,
            discipline = discipline,
            composure = discipline
        ),
        condition = GladiatorCondition(health = currentHealth),
        isAlive = isAlive,
        deathDay = deathDay,
        killedBy = killedBy,
        causeOfDeath = causeOfDeath,
        deathArena = deathArena
    )

    init {
        // Sync physical stats with attributes and biometrics
        physicalStats.strength = attributes.strength
        physicalStats.speed = attributes.speed
        physicalStats.agility = attributes.agility
        physicalStats.endurance = attributes.endurance
        physicalStats.reflex = attributes.reflex
        physicalStats.painTolerance = attributes.painTolerance
        physicalStats.heightCm = heightCm
        physicalStats.weightKg = weightKg

        // Sync training progression
        trainingProgress.currentFocus = trainingFocus
        trainingProgress.dietRegimen = diet
        trainingProgress.level = level
        trainingProgress.experiencePoints = experience

        if (careerStats.fights > 0 && historicalPerformance.totalMatches == 0) {
            historicalPerformance.totalMatches = careerStats.fights
            historicalPerformance.victories = careerStats.wins
            historicalPerformance.defeats = careerStats.losses
            historicalPerformance.kills = careerStats.kills
            historicalPerformance.crowdApprovalRating = careerStats.crowdFavor
            historicalPerformance.totalDenariiEarned = careerStats.denariiEarned
        }
    }
}
