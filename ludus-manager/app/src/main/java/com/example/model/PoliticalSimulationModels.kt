package com.example.model

/**
 * Represents the major political, economic, and military factions in the Roman Empire.
 */
enum class PoliticalFactionId(
    val title: String,
    val latinName: String,
    val leaderArchetype: String,
    val primaryInterest: String
) {
    SENATORIAL_ELITE(
        title = "Senato Aristokrasisi",
        latinName = "Optimates & Nobiles",
        leaderArchetype = "Kıdemli Senatörler",
        primaryInterest = "Geleneksel Roma onuru, senatör ayrıcalıkları ve soyluluk"
    ),
    ARENA_OFFICIALS(
        title = "Arena Yargıçları & Editores",
        latinName = "Magistratus & Munerarii",
        leaderArchetype = "Belediye Yargıçları",
        primaryInterest = "Bilet hasılatı, arena düzeni, lisans harçları ve prestij"
    ),
    MERCHANT_GUILD(
        title = "Tüccarlar & Equites",
        latinName = "Ordo Equester & Mercatores",
        leaderArchetype = "Deniz Tüccarları & Bankerler",
        primaryInterest = "Tahıl sevkiyatı, bahis tekelleri, ticaret rotaları ve kâr"
    ),
    MILITARY(
        title = "Roma Lejyonları & Gaziler",
        latinName = "Legiones & Veterani",
        leaderArchetype = "Lejyon Komutanları & Tribünler",
        primaryInterest = "Sert dövüş disiplini, askeri nizam ve sınır güvenliği"
    ),
    RELIGIOUS_AUTHORITIES(
        title = "Tapınak Rahipleri & Vestaller",
        latinName = "Collegium Pontificum",
        leaderArchetype = "Mars ve Jüpiter Başrahipleri",
        primaryInterest = "Kutsal bayram kurbanları, tanrıların gazabını dindirme ve adaklar"
    ),
    IMPERIAL_ADMINISTRATION(
        title = "İmparatorluk Sarayı & Maliye",
        latinName = "Fiscus & Curia Caesaris",
        leaderArchetype = "İmparatorluk Vekilleri & Maliyeciler",
        primaryInterest = "Vergi tahsilatı, İmparator fermanlarına itaat ve Roma asayişi"
    ),
    RIVAL_LUDUSES(
        title = "Rakip Lanista İttifakı",
        latinName = "Societas Lanistarum",
        leaderArchetype = "Kıdemli Lanistalar",
        primaryInterest = "Pazar tekeli, gladyatör transferleri ve rakibi saf dışı bırakma"
    )
}

/**
 * Dynamic political faction with shifting opinions, influence, and perks/sanctions.
 */
data class PoliticalFaction(
    val id: PoliticalFactionId,
    val name: String,
    val leaderTitle: String,
    val description: String,
    var influence: Int, // 0 - 100
    var wealth: Int,
    var opinionOfPlayer: Int, // -100 (Hostile) to +100 (Allied)
    val interests: List<String>,
    val coreGoals: List<String>,
    val alliedFactionIds: List<PoliticalFactionId>,
    val rivalFactionIds: List<PoliticalFactionId>,
    var currentPoliticalIssue: String,
    val activePerks: List<String> = emptyList(),
    val activeSanctions: List<String> = emptyList()
) {
    val standingCategory: String
        get() = when {
            opinionOfPlayer >= 60 -> "Sadık Müttefik (+${opinionOfPlayer})"
            opinionOfPlayer >= 25 -> "Dostane (+${opinionOfPlayer})"
            opinionOfPlayer >= -20 -> "Nötr (${opinionOfPlayer})"
            opinionOfPlayer >= -60 -> "Soğuk & Kuşkulu (${opinionOfPlayer})"
            else -> "Açık Düşman (${opinionOfPlayer})"
        }
}

/**
 * Secret severity and categorization.
 */
enum class SecretSeverity(val label: String, val leverageScore: Int) {
    MINOR("Hafif Kabahat", 15),
    MODERATE("Ağır Yolsuzluk", 35),
    CRITICAL_TREASON("Vatana İhanet & Komplo", 70)
}

enum class SecretCategory(val label: String) {
    CORRUPTION("Rüşvet & İrtikap"),
    MATCH_FIXING("Arena Şikesi & Zehirleme"),
    SACRILEGE("Tapınak Saygısızlığı"),
    TAX_FRAUD("Vergi Kaçakçılığı"),
    ILLEGAL_POACHING("Kaçak Gladyatör Kaçırma"),
    CONSPIRACY("Senato Suikastı & Komplo")
}

data class PoliticalSecret(
    val id: String,
    val targetNpcId: String,
    val targetName: String,
    val title: String,
    val description: String,
    val category: SecretCategory,
    val severity: SecretSeverity,
    var isVerified: Boolean = true,
    var isExposed: Boolean = false
)

enum class ScandalStatus(val title: String) {
    BREWING("Gizli Soruşturma"),
    ACTIVE_HEADLINE("Kamuoyu Çalkantısı"),
    SUPPRESSED("Rüşvetle Örtbas Edildi"),
    RESOLVED("Adalet Teftişi Tamamlandı")
}

data class PoliticalScandal(
    val id: String,
    val title: String,
    val description: String,
    val involvedNpcIds: List<String>,
    val involvedFactionIds: List<PoliticalFactionId>,
    var severity: Int, // 0 - 100
    var publicAwareness: Int, // 0 - 100
    var evidenceLevel: Int, // 0 - 100
    var status: ScandalStatus = ScandalStatus.ACTIVE_HEADLINE,
    var daysRemaining: Int = 5,
    val suppressionCostGold: Int = 1800,
    val suppressionCostFavor: Int = 2
)

enum class RelationshipType(val label: String, val colorHex: Long) {
    FRIENDSHIP("Kadim Dostluk", 0xFF2E7D32),
    PATRONAGE("Hamilik (Patronus)", 0xFFC59B27),
    BUSINESS("Ticari Ortaklık", 0xFF0288D1),
    FAMILY("Akrabalık / Gens", 0xFF8E24AA),
    POLITICAL_ALLIANCE("Siyasi Pakt", 0xFFD87D2A),
    RIVALRY("Siyasi Rekabet", 0xFFD32F2F),
    DEBT("Mali Borç / İpotek", 0xFFE65100),
    BLACKMAIL("Şantaj Kıskacı", 0xFF6A1B9A),
    FEUD("Kan Davası", 0xFFB71C1C)
}

data class NetworkConnection(
    val id: String,
    val fromNpcId: String,
    val toNpcId: String,
    val type: RelationshipType,
    val strength: Int, // 1 - 100
    val description: String
)

/**
 * Deep Political NPC with personal traits, agendas, favors, and connection webs.
 */
data class PoliticalCharacter(
    val id: String,
    val name: String,
    val title: String,
    val factionId: PoliticalFactionId,
    var wealth: Int,
    var influence: Int, // 0 - 100
    var prestige: Int,
    val personality: String,
    var ambition: Int, // 0 - 100
    var greed: Int, // 0 - 100
    var integrity: Int, // 0 - 100
    val ideology: String,
    var relationshipWithPlayer: Int, // -100 to +100
    var favorsOwedToPlayer: Int = 0,
    var favorsOwedByPlayer: Int = 0,
    val alliedNpcIds: List<String> = emptyList(),
    val rivalNpcIds: List<String> = emptyList(),
    val enemyNpcIds: List<String> = emptyList(),
    var knownSecrets: List<PoliticalSecret> = emptyList(),
    val personalGoals: String,
    val currentPosition: String,
    var isPatron: Boolean = false,
    val monthlyStipend: Int = 0,
    val patronObligation: String = "",
    val avatarSymbol: String = "🏛"
) {
    val relationshipStatus: String
        get() = when {
            relationshipWithPlayer >= 70 -> "Sadık Destekçi (+${relationshipWithPlayer})"
            relationshipWithPlayer >= 30 -> "İttifak Halinde (+${relationshipWithPlayer})"
            relationshipWithPlayer >= -20 -> "Mesafeli / Tarafsız (${relationshipWithPlayer})"
            relationshipWithPlayer >= -60 -> "Düşmanca Tavır (${relationshipWithPlayer})"
            else -> "Amansız Düşman (${relationshipWithPlayer})"
        }
}

/**
 * Resource ledger encompassing all political currencies.
 */
data class PoliticalResourceLedger(
    var politicalFavor: Int = 3, // Spendable points
    var influence: Int = 42, // Auctoritas (0-100)
    var reputation: Int = 55, // Honestas (0-100)
    var discoveredSecretsCount: Int = 1,
    var debtsOwedByPlayerCount: Int = 1,
    var debtsOwedToPlayerCount: Int = 0
)

/**
 * Choice and consequence data structures.
 */
data class PoliticalChoice(
    val id: String,
    val label: String,
    val costDescription: String,
    val effectDescription: String,
    val requiredGold: Int = 0,
    val requiredFavor: Int = 0,
    val requiredInfluence: Int = 0,
    val requiredSecretId: String? = null,
    val goldDelta: Int = 0,
    val prestigeDelta: Int = 0,
    val favorDelta: Int = 0,
    val influenceDelta: Int = 0,
    val reputationDelta: Int = 0,
    val factionOpinionsDelta: Map<PoliticalFactionId, Int> = emptyMap(),
    val targetNpcId: String? = null,
    val npcRelationshipDelta: Int = 0,
    val consequenceNarrative: String,
    val triggersScandal: PoliticalScandal? = null,
    val nextEventChainId: String? = null
)

data class PoliticalEvent(
    val id: String,
    val title: String,
    val narrative: String,
    val instigatorName: String,
    val instigatorTitle: String,
    val factionId: PoliticalFactionId,
    val chainStep: Int = 1,
    val chainId: String? = null,
    val choices: List<PoliticalChoice>,
    val isUrgent: Boolean = false,
    val expiresDay: Int
)

data class PoliticalCalendarEntry(
    val id: String,
    val day: Int,
    val title: String,
    val category: String, // ELECTION, SENATE_VOTE, SACRED_FESTIVAL, IMPERIAL_INSPECTION, TAX_COLLECTION
    val factionId: PoliticalFactionId,
    val impactDescription: String,
    var isResolved: Boolean = false
)

enum class PoliticalInteractionType(val title: String, val icon: String, val description: String) {
    TALK_FLATTERY("Sohbet & İltifat", "🗣", "İtibarını öv, nabız yokla ve ilişkiyi ısıt"),
    GIVE_GOLD_GIFT("Altın Bahşiş & Hediye", "🎁", "1,000 Denarii değerinde gümüş kupa ve şarap sun"),
    BRIBE_OFFICIAL("Gizli Rüşvet Ver", "💰", "2,500 Denarii ile göz yummasını veya lehte oy vermesini sağla"),
    REQUEST_FAVOR_CASH("Mali Lütuf İste", "🪙", "1 Lütuf hakkını 1,500 Denarii acil hibe için bozdur"),
    REQUEST_FAVOR_LICENSE("Özel Arena Lisansı İste", "📜", "Bölgesel şampiyona ve büyük arenalar için bürokratik muafiyet talep et"),
    REQUEST_FAVOR_SABOTAGE("Rakip Lanistayı Baltala", "🗡", "Rakip ludus gladyatörlerine teftiş veya antrenörlerine baskı uygulat"),
    ASSIGN_GLADIATOR_ESCORT("Gladyatör Muhafız Tahsis Et", "🛡", "Senatöre sokak koruması için 1 gladyatör tahsis et (+30 İlişki, -1 Dövüşçü kondisyonu)"),
    ASK_PATRONAGE("Hamilik (Patronus) Teklif Et", "🤝", "Resmi himayesine gir; aylık gelir ve siyasi kalkan kazan"),
    RENOUNCE_PATRONAGE("Hamilik Bağını Kopar", "❌", "Himayeden tek taraflı ayrıl (İlişki sert düşer)"),
    INVESTIGATE_NPC("Gizli Casus Gönder", "🕵", "500 Denarii karşılığı karanlık sırlarını ve yolsuzluklarını araştır"),
    BLACKMAIL_WITH_SECRET("Şantaj Yap & Tehdit Et", "📜", "Elindeki gizli dosya ile lehte karar veya 2 Lütuf zorla"),
    HOST_SENATE_BANQUET("Lüks Ziyafet Düzenle", "🍷", "Ludusta 3,000 Denarii karşılığı ziyafet vererek tüm senatörleri ağırla")
}
