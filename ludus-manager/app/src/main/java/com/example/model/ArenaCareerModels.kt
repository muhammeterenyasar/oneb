package com.example.model

/**
 * Multi-Tier categorization for gladiators across the Roman circuit.
 */
enum class OpponentTier(val displayName: String, val badgeColorHex: Long) {
    COMMON("Common (Tiro)", 0xFF8D8D8D),
    UNCOMMON("Uncommon (Veteran)", 0xFF4ADE80),
    ELITE("Elite (Primus Palus)", 0xFF60A5FA),
    CHAMPION("Arena Champion", 0xFFF59E0B),
    LEGEND("Living Legend (Rudis)", 0xFFEF4444)
}

/**
 * Opponent AI Personality profiles with distinct tactical tendencies.
 */
enum class AiTacticalPersonality(
    val title: String,
    val description: String,
    val tacticalAdvice: String,
    val aggressionBias: Float,
    val counterBias: Float,
    val crowdOrientation: Float
) {
    AGGRESSOR(
        title = "Aggressor (Hücumcu)",
        description = "Rushes relentlessly, sacrifices stamina for high strike volume and overwhelming pressure.",
        tacticalAdvice = "Turtle with Defensive stance and parry with Counter-Strike. Wait until their lungs burn.",
        aggressionBias = 0.85f,
        counterBias = 0.20f,
        crowdOrientation = 0.40f
    ),
    DUELIST(
        title = "Duelist (Tek Dövüş Ustası)",
        description = "Patient, precise swordsman who circles out of reach and strikes only during openings.",
        tacticalAdvice = "Use Distance stance to close the gap or employ Bleed & Drain to force reckless lunges.",
        aggressionBias = 0.40f,
        counterBias = 0.85f,
        crowdOrientation = 0.50f
    ),
    TANK(
        title = "Tank / Iron Wall (Demir Duvar)",
        description = "Hunkers behind heavy shields, absorbs punishment, and grinds opponents down.",
        tacticalAdvice = "Target the shield with Shield Crush, or keep high movement to flank their blind angle.",
        aggressionBias = 0.25f,
        counterBias = 0.55f,
        crowdOrientation = 0.30f
    ),
    BERSERKER(
        title = "Berserker (Çılgın Savaşçı)",
        description = "Becomes exponentially faster and more violent as wounds and blood loss accumulate.",
        tacticalAdvice = "Do not leave them wounded at low HP; execute Vital Finish rapidly before frenzy peaks.",
        aggressionBias = 0.90f,
        counterBias = 0.15f,
        crowdOrientation = 0.70f
    ),
    VETERAN(
        title = "Veteran (Kıdemli Usta)",
        description = "Conserves stamina masterfully, never overcommits, and punishes every player tactical mistake.",
        tacticalAdvice = "Maintain Balanced stance. Avoid risky Finish attempts unless they are genuinely staggered.",
        aggressionBias = 0.50f,
        counterBias = 0.75f,
        crowdOrientation = 0.55f
    ),
    TRICKSTER(
        title = "Trickster (Hilebaz / Çevik)",
        description = "Employs feints, unpredictable retreats, kick maneuvers, and sudden explosive lunges.",
        tacticalAdvice = "Hold your ground with Raise Shield. Do not chase them into sand traps or blind spots.",
        aggressionBias = 0.45f,
        counterBias = 0.65f,
        crowdOrientation = 0.80f
    ),
    CROWD_PLAYER(
        title = "Crowd Player (Şovmen)",
        description = "Feeds on audience roars, pumps fists to gain morale, and delivers theatrical execution blows.",
        tacticalAdvice = "Use Taunt Crowd yourself to steal audience favor, weakening their crowd-driven momentum.",
        aggressionBias = 0.60f,
        counterBias = 0.40f,
        crowdOrientation = 0.95f
    )
}

/**
 * Career progression ladder of Roman arenas from provincial sandpits to Rome's Colosseum.
 */
enum class ArenaVenueId(
    val venueName: String,
    val city: String,
    val minPrestige: Int,
    val purseMultiplier: Float,
    val prestigePerWin: Int,
    val capacity: String,
    val patronTitle: String,
    val atmosphericRule: String
) {
    LOCAL_PIT(
        venueName = "Capua Suburb Sand Pit",
        city = "Capua Suburbs",
        minPrestige = 0,
        purseMultiplier = 1.0f,
        prestigePerWin = 50,
        capacity = "800 Plebeians",
        patronTitle = "Tavern Guild Master",
        atmosphericRule = "Rough uneven sand; higher stamina drain."
    ),
    PROVINCIAL(
        venueName = "Campania Provincial Ring",
        city = "Campania",
        minPrestige = 800,
        purseMultiplier = 1.4f,
        prestigePerWin = 90,
        capacity = "3,500 Spectators",
        patronTitle = "Magistrate Cassius",
        atmosphericRule = "Standard arena rules with modest betting house presence."
    ),
    CAPUA(
        venueName = "Capua Amphitheatre of Spartacus",
        city = "Capua",
        minPrestige = 2000,
        purseMultiplier = 2.0f,
        prestigePerWin = 150,
        capacity = "12,000 Spectators",
        patronTitle = "Senator Lentulus Batiatus",
        atmosphericRule = "Passionate connoisseurs; high prestige for glorious executions."
    ),
    POMPEII(
        venueName = "Pompeii Great Amphitheatre",
        city = "Pompeii",
        minPrestige = 3800,
        purseMultiplier = 2.8f,
        prestigePerWin = 220,
        capacity = "20,000 Spectators",
        patronTitle = "Merchant Prince Quintus",
        atmosphericRule = "Heavily commercialized; wealthy patrons offer massive win bonuses."
    ),
    NEAPOLIS(
        venueName = "Neapolis Maritime Arena",
        city = "Neapolis",
        minPrestige = 6000,
        purseMultiplier = 3.6f,
        prestigePerWin = 300,
        capacity = "25,000 Spectators",
        patronTitle = "Archon of the Hellenic League",
        atmosphericRule = "Greek-styled tactical bouts; crowd favors technical mastery and agility."
    ),
    REGIONAL_ELITE(
        venueName = "Etruria Grand Arena",
        city = "Etruria",
        minPrestige = 9000,
        purseMultiplier = 4.5f,
        prestigePerWin = 420,
        capacity = "32,000 Spectators",
        patronTitle = "Proconsul Valerius",
        atmosphericRule = "Elite tournament circuit; brutal survival and gauntlet bouts."
    ),
    ROME(
        venueName = "Circus Maximus & Forum Arena",
        city = "Roma",
        minPrestige = 14000,
        purseMultiplier = 6.0f,
        prestigePerWin = 650,
        capacity = "45,000 Romans",
        patronTitle = "Imperial Praetor",
        atmosphericRule = "Senatorial eyes watching; political reputation at stake."
    ),
    COLOSSEUM(
        venueName = "Flavian Amphitheatre (Colosseum)",
        city = "Roma",
        minPrestige = 22000,
        purseMultiplier = 9.0f,
        prestigePerWin = 1000,
        capacity = "65,000 Citizens & Emperor",
        patronTitle = "Emperor Titus Caesar",
        atmosphericRule = "Imperial Munus; legendary immortality or death before Caesar."
    )
}

/**
 * Different match types offering diverse rule sets, payouts, and risks.
 */
enum class ArenaMatchType(
    val title: String,
    val description: String,
    val goldMultiplier: Float,
    val prestigeMultiplier: Float,
    val isChampionship: Boolean = false,
    val isRematch: Boolean = false
) {
    STANDARD_DUEL("Standard Duel (1v1)", "Honor-bound 1v1 bout according to Roman arena tradition.", 1.0f, 1.0f),
    CHAMPIONSHIP_MATCH("Championship Title Bout", "Sanctioned clash for the Arena Laurel Crown.", 2.5f, 3.0f, isChampionship = true),
    RIVALRY_MATCH("Blood Feud / Rivalry Match", "Personal score to settle; immense crowd tension.", 1.8f, 2.0f),
    REMATCH("Vindication Rematch", "Challenging a previous vanquisher or granting revenge.", 1.5f, 1.5f, isRematch = true),
    EXHIBITION("Patron's Exhibition", "Sponsored by a wealthy patrician with lavish purses.", 2.0f, 0.8f),
    TAG_TEAM_2V2("Gladiator Pair Bout (2v2)", "Two gladiators fighting in tandem against a rival ludus pair.", 1.7f, 1.6f),
    GAUNTLET("Gauntlet / Survival", "Fight successive challengers without rest intervals.", 2.8f, 2.8f),
    BEAST_HUNT("Venatio / Beast Hunt", "Confront wild beasts alongside arena condemned.", 1.9f, 1.4f),
    SPECIAL_FESTIVAL("Festival of Mars Spectacular", "Grand holiday games with Emperor's gold prize.", 3.0f, 2.5f)
}

/**
 * Persistent Boss / Storyline State Machine.
 */
enum class BossState {
    DORMANT,
    ACTIVE,
    AVAILABLE,
    CHALLENGED,
    SCHEDULED,
    DEFEATED,
    DEAD,
    ESCAPED,
    LEFT_CITY,
    RETIRED,
    STORY_RESOLVED
}

/**
 * Single Source of Truth: All fighters are Gladiator instances.
 * PersistentFighter is a typealias ensuring 100% backward compatibility.
 */
typealias PersistentFighter = Gladiator

/**
 * Arena ranking leaderboard entry.
 */
data class ArenaRankingEntry(
    val rank: Int,
    val fighterId: String?,
    val name: String,
    val nickname: String,
    val ludus: String,
    val gladiatorClass: GladiatorClass?,
    val tier: OpponentTier?,
    val recordSummary: String,
    val prestige: Int,
    val recentForm: List<String>,
    val isPlayer: Boolean,
    val isChampion: Boolean,
    val isVacant: Boolean = false
)

/**
 * Career progression tiers for all gladiators.
 */
enum class CareerTier(
    val title: String,
    val rankTierLabel: String,
    val description: String,
    val minWins: Int,
    val minPrestige: Int
) {
    NEWCOMER("Çaylak (Tiro)", "Derecesiz", "Arenanın kanlı kumlarına yeni adım atmış acemi dövüşçü.", 0, 0),
    PROSPECT("Umut Vadeden (Novicius)", "Yerel Kum Havuzu", "Taktiği ve refleksleri umut vadeden genç yetenek.", 3, 300),
    CONTENDER("Aday (Veteranus)", "Taşra İlk 10", "Çetin müsabakalardan sağ çıkmış dayanıklı savaşçı.", 5, 800),
    TOP_10("Sıralamada İlk 10", "Taşra İlk 5", "Şehrin en gözde arenalarında boy gösteren elit gladyatör.", 8, 1500),
    TOP_5("Seçkin İlk 5", "Şampiyonluk Adayı", "Taşra şampiyonunun kapısını zorlayan korkusuz gladyatör.", 12, 2500),
    CHAMPIONSHIP_CONTENDER("Baş Meydan Okuyan", "#1 Challenger", "Capua Defne Çelengi unvan maçına hak kazanmış resmi rakip.", 16, 4000),
    ARENA_CHAMPION("Arena Şampiyonu", "Reigning Champion", "Capua amfitiyatrosunun hükümdarı; halkın idolü.", 20, 6000),
    REGIONAL_CHAMPION("Bölgesel Şampiyon", "Campania Efendisi", "Campania ve Etruria arenalarının tartışmasız lideri.", 25, 10000),
    IMPERIAL_CONTENDER("İmparatorluk Adayı", "Roma Davetlisi", "Circus Maximus ve Roma Colosseum'una çağrılan seçilmiş gladyatör.", 30, 16000),
    LEGEND("Yaşayan Efsane (Rudis)", "Ölümsüz İkon", "Tahta kılıç Rudis ile onurlandırılmış ölümsüz arenacı.", 40, 25000);

    companion object {
        fun calculateTier(wins: Int, prestige: Int, isChampion: Boolean, isRegionalChampion: Boolean): CareerTier {
            return when {
                wins >= 40 && prestige >= 25000 -> LEGEND
                wins >= 30 && prestige >= 16000 -> IMPERIAL_CONTENDER
                isRegionalChampion || (wins >= 25 && prestige >= 10000) -> REGIONAL_CHAMPION
                isChampion || (wins >= 20 && prestige >= 6000) -> ARENA_CHAMPION
                wins >= 16 && prestige >= 4000 -> CHAMPIONSHIP_CONTENDER
                wins >= 12 && prestige >= 2500 -> TOP_5
                wins >= 8 && prestige >= 1500 -> TOP_10
                wins >= 5 && prestige >= 800 -> CONTENDER
                wins >= 3 && prestige >= 300 -> PROSPECT
                else -> NEWCOMER
            }
        }
    }
}

/**
 * Underground illegal combat opportunities.
 */
data class UndergroundFight(
    val id: String,
    val title: String,
    val venueName: String,
    val organizerName: String,
    val organizerRole: String,
    val opponentFighter: PersistentFighter,
    val entryFee: Int,
    val purseReward: Int,
    val riskLevel: String, // "ORTA DÜZEY", "YÜKSEK RİSK", "ÖLÜMCÜL (LETHAL)"
    val discoveryRiskPercent: Int,
    val atmosphere: String,
    val dayOffered: Int,
    val warningNote: String,
    var isCompleted: Boolean = false,
    val isBossFight: Boolean = false,
    val bossState: BossState? = null,
    val storylineSnippet: String? = null
)

/**
 * Permanent Hall of the Fallen memorial recording deceased gladiators.
 */
data class FallenGladiatorMemorial(
    val id: String,
    val name: String,
    val nickname: String,
    val gladiatorClass: GladiatorClass,
    val origin: Origin,
    val ludusAffiliation: String,
    val recordSummary: String,
    val kills: Int,
    val diedOnDay: Int,
    val arenaName: String,
    val killedBy: String,
    val causeOfDeath: String,
    val wasChampion: Boolean = false,
    val yearAUC: String = "69 A.U.C."
)

/**
 * Dedicated event flow for scheduled fight days.
 */
enum class FightDayPhase {
    IDLE,
    MORNING_PREPARATION,
    AFTERNOON_ARRIVAL,
    PRE_MATCH_BRIEFING,
    COMBAT_ACTIVE,
    POST_MATCH_REPORT
}

/**
 * Rivalry tracking between player's ludus and specific opponents.
 */
data class RivalryEntry(
    val opponentId: String,
    val opponentName: String,
    val opponentNickname: String,
    val opponentLudus: String,
    val animosityScore: Int, // 0 to 100
    val animosityTitle: String, // "Grudge", "Fierce Rival", "Bitter Nemesis", "Blood Vendetta"
    val boutsFought: Int,
    val playerWins: Int,
    val opponentWins: Int,
    val reason: String,
    val lastEncounterDateText: String,
    val rematchDemanded: Boolean
)

/**
 * Living calendar schedule item for the arena circuit.
 */
data class ArenaCalendarBout(
    val id: String,
    val day: Int,
    val venueId: ArenaVenueId,
    val fighter1Id: String,
    val fighter1Name: String,
    val fighter1Ludus: String,
    val fighter2Id: String,
    val fighter2Name: String,
    val fighter2Ludus: String,
    val matchType: ArenaMatchType,
    val isPlayerMatch: Boolean = false,
    var isCompleted: Boolean = false,
    var resultSummary: String? = null,
    var winnerName: String? = null
)

/**
 * Historical record of a completed gladiator match.
 */
data class CareerMatchRecord(
    val matchId: String,
    val day: Int,
    val arenaName: String,
    val playerFighterName: String,
    val opponentName: String,
    val opponentNickname: String,
    val opponentLudus: String,
    val opponentClass: GladiatorClass,
    val matchType: ArenaMatchType,
    val won: Boolean,
    val durationSeconds: Int,
    val hitsLanded: Int,
    val blocksExecuted: Int,
    val criticalsLanded: Int,
    val goldEarned: Int,
    val prestigeGained: Int,
    val crowdReaction: String,
    val injurySuffered: String?,
    val opponentOutcome: String // "Killed", "Spared (Missio)", "Injured", "Fled"
)
