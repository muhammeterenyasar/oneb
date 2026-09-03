package com.example.model

/**
 * Epistemic information separation: Players and NPCs perceive the world through
 * different degrees of certainty.
 */
enum class EpistemicStatus(val title: String) {
    KNOWN("Bilinen Gerçek"),
    SUSPECTED("Şüphelenilen"),
    RUMORED("Söylenti / Fısıltı"),
    HIDDEN("Gizli / Bilinmiyor"),
    CONFIRMED("Kesinleşti / Kanıtlandı")
}

/**
 * Importance tiers for world events and notifications.
 */
enum class EventImportance {
    BACKGROUND, // Quietly recorded in chronicle/memory
    MINOR,      // Small banner notification
    IMPORTANT,  // Notification + optional interaction
    MAJOR,      // Dedicated event panel
    CRITICAL    // Direct gameplay focus / pause
}

/**
 * Historical event types recorded by the World Memory Engine.
 */
enum class MemoryEventType(val label: String) {
    PLAYER_KILLED_GLADIATOR("Arenada Gladyatör İnfazı"),
    PLAYER_LOST_MATCH("Arenada Mağlubiyet"),
    PLAYER_REFUSED_BRIBE("Rüşvet Reddedildi"),
    PLAYER_ACCEPTED_PATRON("Hamilik Anlaşması"),
    PLAYER_INSULTED_OFFICIAL("Roma Yargıcına Hakaret"),
    PLAYER_RECRUITED_GLADIATOR("Köle / Gladyatör Alımı"),
    PLAYER_FREED_GLADIATOR("Rudis ile Özgürlük Bağışı"),
    PLAYER_BOUGHT_NAMED_WEAPON("Efsanevi Teçhizat Alımı"),
    PLAYER_BETRAYED_ALLY("Müttefike İhanet"),
    PLAYER_ENTERED_UNDERGROUND("Yeraltı Çukuruna İniş"),
    PLAYER_DEFEATED_BOSS("Yeraltı Patronu Bozgunu"),
    PLAYER_LOST_CHAMPION("Ludus Şampiyonunun Ölümü"),
    NPC_KILLED_NPC("Gladyatör Cinayeti"),
    NPC_INJURED_NPC("Ağır Sakatlanma"),
    NPC_LEFT_LUDUS("Ludustan Ayrılış / Kaçış"),
    NPC_SWITCHED_LUDUS("Rakip Ludusa Geçiş"),
    POLITICAL_EVENT("Siyasi Meclis Kararı"),
    ARENA_EVENT("Resmi Arena Müsabakası"),
    MARKET_EVENT("Pazar & Ticaret Hadisesi"),
    RELATIONSHIP_EVENT("Dostluk veya Husumet Gelişmesi"),
    DISCOVERY_EVENT("Gizli Bilgi Keşfi")
}

/**
 * Canonical record in the World Memory Engine preserving causal history.
 */
data class MemoryEntry(
    val id: String,
    val eventType: MemoryEventType,
    val date: Int,
    val location: String,
    val participantIds: List<String>,
    val causeDescription: String,
    val hiddenFacts: String? = null,
    val impactTags: List<String> = emptyList(),
    val importance: EventImportance = EventImportance.MINOR,
    val delayedConsequenceId: String? = null
)

/**
 * Origin sources for rumors.
 */
enum class RumorSource(val label: String, val baseReliability: Float) {
    GLADIATOR("Kışla Gladyatörü", 0.65f),
    STAFF("Ludus Personeli / Hekim", 0.70f),
    MERCHANT("Forum Tüccarı", 0.75f),
    TAVERN_GOSSIP("Subura Meyhane Dedikodusu", 0.40f),
    POLITICAL_CONTACT("Senato Kâtibi / Müttefik", 0.80f),
    ARENA_OFFICIAL("Arena Başyargıcı", 0.85f),
    RIVAL_LUDUS("Rakip Lanista Muhbiri", 0.45f),
    SCOUT("Görevlendirilen Gözcü", 0.90f),
    UNDERGROUND_ORGANIZER("Yeraltı Kaçakçısı", 0.60f),
    PATRON("Patronus / Hami Soylu", 0.85f),
    TRAVELER("Via Appia Seyyahı", 0.50f)
}

/**
 * Objective truth status of a rumor.
 */
enum class RumorTruthStatus(val label: String) {
    TRUE("Doğru"),
    FALSE("Asılsız"),
    PARTIALLY_TRUE("Kısmen Doğru"),
    MISLEADING("Kasıtlı Yanıltma / Tuzak"),
    UNKNOWN("Doğrulanmamış")
}

/**
 * Rumor entity that spreads, distorts, and influences player intelligence.
 */
data class Rumor(
    val id: String,
    val source: RumorSource,
    val targetId: String? = null,
    val subject: String,
    val headline: String,
    val fullGossipText: String,
    val truthStatus: RumorTruthStatus,
    val reliability: Float, // 0.0 to 1.0
    val createdDay: Int,
    val location: String,
    var spreadCount: Int = 1,
    var distortionLevel: Int = 0, // Increases over days as gossip mutates
    var isInvestigated: Boolean = false,
    val investigationCostDenarii: Int = 200,
    val investigationLead: String? = null,
    var isExpired: Boolean = false
) {
    val displayReliability: String
        get() = when {
            reliability >= 0.80f -> "Yüksek Güvenilirlik (%${(reliability * 100).toInt()})"
            reliability >= 0.55f -> "Orta Güvenilirlik (%${(reliability * 100).toInt()})"
            else -> "Şüpheli / Düşük Güvenilirlik (%${(reliability * 100).toInt()})"
        }
}

/**
 * Categories of persistent world mysteries.
 */
enum class MysteryCategory(val label: String, val iconSymbol: String) {
    DISAPPEARANCE("Gizemli Kayboluş", "👣"),
    CONSPIRACY("Siyasi Komplo", "🗡"),
    WEAPON_ORIGIN("Efsanevi Eşya Kökeni", "⚔"),
    SABOTAGE("Ludus Sabotajı", "☠"),
    FORBIDDEN_CULT("Gizli Yeraltı Tarikatı", "🕯"),
    UNEXPLAINED_DEATH("Açıklanamayan Ölüm", "💀"),
    THEFT("Hazine & Teçhizat Hırsızlığı", "💰")
}

/**
 * Lifecycle state of a mystery.
 */
enum class MysteryStatus(val label: String) {
    ACTIVE("Aktif Soruşturma"),
    INVESTIGATING("Gözcüler Sahada"),
    DORMANT("Kayıp İpucu / Beklemede"),
    RESOLVED("Çözümlendi & Aydınlatıldı"),
    FAILED("İpuçları Karartıldı / Başarısız"),
    ABANDONED("Terk Edildi")
}

/**
 * Plausible hypothesis for a mystery.
 */
data class MysteryHypothesis(
    val id: String,
    val description: String,
    val probabilityPercent: Int,
    val isCorrect: Boolean
)

/**
 * Piece of discovered evidence in an investigation.
 */
data class EvidencePiece(
    val id: String,
    val title: String,
    val description: String,
    val discoveredDay: Int,
    val source: String,
    var epistemicStatus: EpistemicStatus = EpistemicStatus.SUSPECTED
)

/**
 * Tactical actions available to investigate a mystery or rumor.
 */
enum class InvestigationActionType(val label: String) {
    SEARCH_DORM("Koğuşu Ara"),
    ASK_AROUND("Etraftan Soruştur"),
    SCOUT_LOCATION("Olay Yerini Gözetle"),
    BRIBE_INFORMANT("Muhbire Rüşvet Ver"),
    USE_POLITICAL_FAVOR("Siyasi Nüfuz Kullan"),
    CONSULT_UNDERGROUND("Yeraltı Çetelerine Danış"),
    INTERROGATE_SUSPECT("Şüpheliyi Sorgula"),
    WAIT_AND_OBSERVE("Sessizce Takip Et")
}

/**
 * Concrete action path to uncover facts in a mystery.
 */
data class InvestigationPath(
    val id: String,
    val label: String,
    val actionType: InvestigationActionType,
    val costDenarii: Int = 0,
    val costFavor: Int = 0,
    val description: String,
    val riskNote: String = "Düşük Risk",
    var isExecuted: Boolean = false
)

/**
 * Persistent World Mystery with clues, evidence board, and multiple explanations.
 */
data class WorldMystery(
    val id: String,
    val title: String,
    val category: MysteryCategory,
    val initialClue: String,
    val discoveredDay: Int,
    val knownFacts: MutableList<String> = mutableListOf(),
    val unknownSuspicions: MutableList<String> = mutableListOf(),
    val possibleExplanations: List<MysteryHypothesis>,
    val discoveredEvidence: MutableList<EvidencePiece> = mutableListOf(),
    val investigationPaths: List<InvestigationPath>,
    var status: MysteryStatus = MysteryStatus.ACTIVE,
    var resolutionSummary: String? = null,
    val rewardSummary: String? = null
)

/**
 * Lifecycle status of an ongoing story thread.
 */
enum class StoryThreadStatus(val label: String) {
    ACTIVE("Aktif Gelişme"),
    DORMANT("Sessiz & Pusu"),
    ESCALATING("Tırmanıyor / Tehlike"),
    RESOLVED("Nihayete Erdi"),
    FAILED("Hüsranla Bitti"),
    ABANDONED("Zaman Aşımı")
}

/**
 * Sequential stage of a story thread.
 */
data class StoryStage(
    val stageIndex: Int,
    val title: String,
    val narrativeDescription: String,
    val choices: List<String> = emptyList(),
    val requiredDay: Int = 0
)

/**
 * Unresolved situation or narrative arc progressing across world time.
 */
data class StoryThread(
    val id: String,
    val title: String,
    val synopsis: String,
    val originEventId: String? = null,
    val associatedNpcIds: List<String> = emptyList(),
    val associatedMysteryId: String? = null,
    var status: StoryThreadStatus = StoryThreadStatus.ACTIVE,
    val startDay: Int,
    var lastActivityDay: Int,
    var urgency: Int = 5, // 1 to 10
    var currentStageIndex: Int = 0,
    val stages: List<StoryStage> = emptyList(),
    val connectedThreadIds: MutableList<String> = mutableListOf()
)

/**
 * Types of delayed consequences from player or NPC decisions.
 */
enum class ConsequenceType(val label: String) {
    PRESTIGE_PENALTY("Şan & Prestij Kaybı"),
    TREASURY_FINE("Mali Para Cezası"),
    MERCHANT_PRICE_HIKE("Pazar & Erzak Fiyat Artışı"),
    ARENA_LICENSE_DELAY("Arena Lisansı Gecikmesi"),
    RIVAL_SABOTAGE("Rakip Lanista Sabotajı"),
    POLITICAL_SANCTION("Senato Teftişi"),
    UNDERGROUND_HIT("Yeraltı Suikast Girişimi"),
    ALLY_BOON("Müttefik Yardımı & İkramiye")
}

/**
 * Consequence that matures days after the originating decision.
 */
data class DelayedConsequence(
    val id: String,
    val triggerAction: String,
    val createdDay: Int,
    val maturityDay: Int,
    val targetEntityId: String? = null,
    val narrativeClue: String,
    val consequenceEffectType: ConsequenceType,
    val effectMagnitude: Int,
    var hasTriggered: Boolean = false,
    val resolutionMessage: String
)

/**
 * Memorable actions recorded in a character's memory.
 */
data class CharacterMemoryEvent(
    val day: Int,
    val deedDescription: String,
    val sentimentDelta: Int,
    val emotionalCategory: String // "Minnet", "Hıyanet", "Korku", "İntikam"
)

/**
 * Hidden secret possessed by an NPC.
 */
data class CharacterSecret(
    val id: String,
    val title: String,
    val detail: String,
    val severity: Int, // 1 to 10
    var isDiscoveredByPlayer: Boolean = false
)

/**
 * Autonomous agenda guiding NPC actions.
 */
enum class NpcAgenda(val label: String) {
    ACQUIRE_WEALTH("Zenginleşme & Tefecilik"),
    SEEK_FREEDOM("Özgürlük Rudisi Arayışı"),
    SURPASS_PLAYER("Player Ludusunu Yok Etme"),
    INCREASE_POLITICAL_POWER("Senato Nüfuzunu Büyütme"),
    CONTROL_UNDERGROUND_GAMBLING("Yeraltı Bahislerini Yönetme"),
    AVENGE_FALLEN_KIN("Düşen Gladyatörün İntikamı"),
    SURVIVE_PEACEFULLY("Gözden Uzak Hayatta Kalma")
}

/**
 * Emotional and historical memory matrix for important NPCs.
 */
data class CharacterMemory(
    val npcId: String,
    val npcName: String,
    var trust: Int = 50,       // 0 to 100
    var respect: Int = 50,     // 0 to 100
    var fear: Int = 20,        // 0 to 100
    var hatred: Int = 10,      // 0 to 100
    var gratitude: Int = 10,   // 0 to 100
    var debtOwed: Int = 0,     // Denarii
    var obligationScore: Int = 0,
    var envy: Int = 15,
    var admiration: Int = 40,
    var suspicion: Int = 20,
    val memorableEvents: MutableList<CharacterMemoryEvent> = mutableListOf(),
    var currentAgenda: NpcAgenda = NpcAgenda.SURVIVE_PEACEFULLY,
    var currentGoal: String = "Mevcut konumunu korumak",
    val personalSecrets: MutableList<CharacterSecret> = mutableListOf()
)

/**
 * Teaser item shown in the "Tomorrow's Horizon" curiosity preview.
 */
data class TomorrowPreviewItem(
    val iconSymbol: String,
    val categoryTag: String,
    val headline: String,
    val teaserText: String,
    val isRevealed: Boolean = false
)

/**
 * Navigation tabs for the Story Discovery & World Memory Hub.
 */
enum class StoryHubTab(val title: String, val iconSymbol: String) {
    CHRONICLE("Tarihçe & Annals", "📜"),
    RUMORS("Söylenti Ağı", "🗣"),
    MYSTERIES("Gizemler & Soruşturma", "🔍"),
    STORY_THREADS("Hikâye Kolları", "🧵"),
    CHARACTER_DOSSIERS("Karakter Dosyaları", "👤"),
    DIAGNOSTICS("Teşhis Paneli", "⚙")
}

