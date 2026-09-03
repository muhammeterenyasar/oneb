package com.example.simulation

import com.example.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object PoliticalEngine {

    fun createInitialFactions(): List<PoliticalFaction> {
        return listOf(
            PoliticalFaction(
                id = PoliticalFactionId.SENATORIAL_ELITE,
                name = "Senato Aristokrasisi (Optimates)",
                leaderTitle = "Senatör Marcus Cassius Longinus",
                description = "Cumhuriyetçi soylular ve Roma'nın en köklü patrician aileleri. Gösterişli, onurlu dövüşleri ve kanunlara sıkı bağlılığı överler.",
                influence = 88,
                wealth = 65000,
                opinionOfPlayer = 20,
                interests = listOf("Asil kan bağı", "Siyasi nüfuz", "Geleneksel ludi"),
                coreGoals = listOf("Plebeian ayaklanmalarını bastırmak", "Senato otoritesini korumak"),
                alliedFactionIds = listOf(PoliticalFactionId.RELIGIOUS_AUTHORITIES, PoliticalFactionId.ARENA_OFFICIALS),
                rivalFactionIds = listOf(PoliticalFactionId.MERCHANT_GUILD, PoliticalFactionId.RIVAL_LUDUSES),
                currentPoliticalIssue = "Yeni gladyatör ithalatına asalet vergisi getirilmesi tasarısı oylanıyor.",
                activePerks = listOf("Senato Locası Davetiyesi (+%15 Şan/Prestige)"),
                activeSanctions = emptyList()
            ),
            PoliticalFaction(
                id = PoliticalFactionId.ARENA_OFFICIALS,
                name = "Arena Yargıçları & Editores",
                leaderTitle = "Belediye Başyargıcı Lucius Decimus Pulcher",
                description = "Capua ve çevre arenaların kapı harçlarını, müsabaka lisanslarını ve dövüş programlarını belirleyen bürokratik kurul.",
                influence = 74,
                wealth = 38000,
                opinionOfPlayer = 5,
                interests = listOf("Kapı hasılatı", "Bürokratik harçlar", "Arena nizamı"),
                coreGoals = listOf("Mali bütçeyi doldurmak", "Kaçak arenaları kapatmak"),
                alliedFactionIds = listOf(PoliticalFactionId.SENATORIAL_ELITE),
                rivalFactionIds = listOf(PoliticalFactionId.RIVAL_LUDUSES),
                currentPoliticalIssue = "Taşra arenalarındaki güvenlik ve lisans harçlarının %25 artırılması gündemde.",
                activePerks = emptyList(),
                activeSanctions = listOf("Standart Kapı Harcı: %100")
            ),
            PoliticalFaction(
                id = PoliticalFactionId.MERCHANT_GUILD,
                name = "Tüccarlar Loncası & Equites",
                leaderTitle = "Banker & Tahıl Taciri Quintus Balbus",
                description = "Akdeniz tahıl filolarını, zırh atölyelerini ve arena bahis çetelerini yöneten altın zengini atlı sınıfı.",
                influence = 68,
                wealth = 92000,
                opinionOfPlayer = 35,
                interests = listOf("Bahis komisyonu", "Ucuz tahıl ithalatı", "Zırh ticareti"),
                coreGoals = listOf("Ticareti tekelleştirmek", "Senato kısıtlamalarını delmek"),
                alliedFactionIds = listOf(PoliticalFactionId.IMPERIAL_ADMINISTRATION),
                rivalFactionIds = listOf(PoliticalFactionId.SENATORIAL_ELITE),
                currentPoliticalIssue = "Sicilya tahıl ambargosu nedeniyle gladyatör erzak maliyetleri yükseliyor.",
                activePerks = listOf("Tüccar İndirimi: Erzak satın alımlarında -%10 altın maliyeti"),
                activeSanctions = emptyList()
            ),
            PoliticalFaction(
                id = PoliticalFactionId.MILITARY,
                name = "Lejyon Komutanlığı & Gaziler",
                leaderTitle = "Lejyon Komutanı Gaius Marius Corvus",
                description = "VI. Demir Lejyon gazileri ve ordu zabitleri. Dövüşlerde süslü oyunları sevmez, sert kılıç ve kalkan disiplinini takdir eder.",
                influence = 80,
                wealth = 45000,
                opinionOfPlayer = 10,
                interests = listOf("Askeri dayanıklılık", "Savaş kahramanları", "Lejyon alımları"),
                coreGoals = listOf("Sınır garnizonlarını güçlendirmek", "Gazilere arazi tahsisi"),
                alliedFactionIds = listOf(PoliticalFactionId.IMPERIAL_ADMINISTRATION),
                rivalFactionIds = listOf(PoliticalFactionId.MERCHANT_GUILD),
                currentPoliticalIssue = "Gladyatörlerin askeri birliklerde yakın dövüş eğitmeni olarak kiralanması tartışılıyor.",
                activePerks = emptyList(),
                activeSanctions = emptyList()
            ),
            PoliticalFaction(
                id = PoliticalFactionId.RELIGIOUS_AUTHORITIES,
                name = "Tapınak Rahipleri (Collegium Pontificum)",
                leaderTitle = "Mars Başrahibi Servius Sulpicius",
                description = "Roma tanrılarının kutsal bayramlarını, kehanetleri ve adakları idare eden din adamları heyeti.",
                influence = 62,
                wealth = 52000,
                opinionOfPlayer = 15,
                interests = listOf("Kutsal adaklar", "Bayram ayinleri", "Uğurlu kehanetler"),
                coreGoals = listOf("Dinsizliği cezalandırmak", "Tapınak hazinelerini büyütmek"),
                alliedFactionIds = listOf(PoliticalFactionId.SENATORIAL_ELITE),
                rivalFactionIds = listOf(PoliticalFactionId.ARENA_OFFICIALS),
                currentPoliticalIssue = "Saturnalia bayramı için 5 gladyatörün tanrılara kurban olarak dövüştürülmesi isteniyor.",
                activePerks = listOf("Mars Kutsaması: Revir iyileşme sürelerinde -%15 süre"),
                activeSanctions = emptyList()
            ),
            PoliticalFaction(
                id = PoliticalFactionId.IMPERIAL_ADMINISTRATION,
                name = "İmparatorluk Sarayı & Vergi Müfettişleri",
                leaderTitle = "İmparatorluk Vekili Tiberius Haterius",
                description = "Roma'daki Sezar adına taşra vilayetlerini denetleyen, vergi toplayan ve Colosseum için gladyatör seçen bürokrasi.",
                influence = 95,
                wealth = 120000,
                opinionOfPlayer = 0,
                interests = listOf("Düzenli vergi", "İmparator sadakati", "Roma Asayişi"),
                coreGoals = listOf("Vergi kaçakçılığını ezmek", "Roma Ludi organizasyonunu teftiş"),
                alliedFactionIds = listOf(PoliticalFactionId.MILITARY, PoliticalFactionId.MERCHANT_GUILD),
                rivalFactionIds = emptyList(),
                currentPoliticalIssue = "Taşra luduslarının mali kayıtları ve vergi kaçırma iddiaları inceleniyor.",
                activePerks = emptyList(),
                activeSanctions = emptyList()
            ),
            PoliticalFaction(
                id = PoliticalFactionId.RIVAL_LUDUSES,
                name = "Rakip Lanista İttifakı",
                leaderTitle = "Lanista Primus Sextus Pomponius",
                description = "Capua ve Campania'daki köklü rakip gladyatör okullarının gizli ittifakı. Sizin yükselişinizi engellemek için lobi yaparlar.",
                influence = 58,
                wealth = 32000,
                opinionOfPlayer = -45,
                interests = listOf("Gladyatör piyasası", "Dövüş bahisleri", "Rakibi ezmek"),
                coreGoals = listOf("Ludusunuzu iflasa sürüklemek", "Şampiyonlarınızı satın almak"),
                alliedFactionIds = emptyList(),
                rivalFactionIds = listOf(PoliticalFactionId.SENATORIAL_ELITE, PoliticalFactionId.ARENA_OFFICIALS),
                currentPoliticalIssue = "Oyuncunun gladyatörlerini doping ve hileli kılıç kullanmakla suçlayan bir dilekçe hazırlıyorlar.",
                activePerks = emptyList(),
                activeSanctions = listOf("Rakip Karalama: Şöhret kazanımı -%10")
            )
        )
    }

    fun createInitialCharacters(): List<PoliticalCharacter> {
        val sec1 = PoliticalSecret(
            id = "sec_pulcher_1",
            targetNpcId = "npc_pulcher",
            targetName = "Lucius Decimus Pulcher",
            title = "Gizli Bahis Komisyonu",
            description = "Başyargıç Pulcher, resmi arena bahislerinden gizlice %8 pay alıp vergi kaçırıyor.",
            category = SecretCategory.CORRUPTION,
            severity = SecretSeverity.MODERATE,
            isVerified = true
        )

        val sec2 = PoliticalSecret(
            id = "sec_pomponius_1",
            targetNpcId = "npc_pomponius",
            targetName = "Sextus Pomponius",
            title = "Gladyatör Zehirleme İtirafı",
            description = "Rakip Lanista Pomponius'un hekimi, kritik maçlar öncesi rakip dövüşçülere zehirli şarap içirdiğini itiraf etti.",
            category = SecretCategory.MATCH_FIXING,
            severity = SecretSeverity.CRITICAL_TREASON,
            isVerified = false
        )

        return listOf(
            PoliticalCharacter(
                id = "npc_cassius",
                name = "Senator Marcus Cassius",
                title = "Capua Senato Kıdemlisi",
                factionId = PoliticalFactionId.SENATORIAL_ELITE,
                wealth = 45000,
                influence = 85,
                prestige = 920,
                personality = "Gururlu, Aristokrat, Hesapçı",
                ambition = 82,
                greed = 45,
                integrity = 70,
                ideology = "Optimates Soyluluğu",
                relationshipWithPlayer = 40,
                favorsOwedToPlayer = 1,
                favorsOwedByPlayer = 0,
                alliedNpcIds = listOf("npc_sulpicius", "npc_corvus"),
                rivalNpcIds = listOf("npc_balbus", "npc_pomponius"),
                enemyNpcIds = listOf("npc_pomponius"),
                knownSecrets = emptyList(),
                personalGoals = "Roma Konsüllüğüne aday olmak için lüks oyunlar finanse etmek istiyor.",
                currentPosition = "Capua Senato Divanı Başkanı",
                isPatron = true,
                monthlyStipend = 850,
                patronObligation = "Önemli maçlarda arenada Cassius sancağı taşımak ve asil tavırlar sergilemek.",
                avatarSymbol = "🏛"
            ),
            PoliticalCharacter(
                id = "npc_pulcher",
                name = "Lucius Decimus Pulcher",
                title = "Capua Arena Başyargıcı",
                factionId = PoliticalFactionId.ARENA_OFFICIALS,
                wealth = 28000,
                influence = 78,
                prestige = 610,
                personality = "Açgözlü, Kuralcı, Rüşvete Açık",
                ambition = 70,
                greed = 88,
                integrity = 30,
                ideology = "Pragmatist Bürokrat",
                relationshipWithPlayer = 10,
                favorsOwedToPlayer = 0,
                favorsOwedByPlayer = 1,
                alliedNpcIds = listOf("npc_cassius"),
                rivalNpcIds = listOf("npc_corvus"),
                enemyNpcIds = emptyList(),
                knownSecrets = listOf(sec1),
                personalGoals = "Arena harçlarından şahsi servet edinip Napoli'de bir sahil villası almak.",
                currentPosition = "Capua ve Campania Baş Editoresi",
                isPatron = false,
                monthlyStipend = 0,
                patronObligation = "",
                avatarSymbol = "⚖"
            ),
            PoliticalCharacter(
                id = "npc_balbus",
                name = "Quintus Cornelius Balbus",
                title = "Tahıl Tekeli & Bahis Baronu",
                factionId = PoliticalFactionId.MERCHANT_GUILD,
                wealth = 95000,
                influence = 72,
                prestige = 540,
                personality = "Kurnaz, Fırsatçı, Paragöz",
                ambition = 90,
                greed = 95,
                integrity = 25,
                ideology = "Piyasa Tüccarlığı",
                relationshipWithPlayer = 30,
                favorsOwedToPlayer = 1,
                favorsOwedByPlayer = 0,
                alliedNpcIds = listOf("npc_haterius"),
                rivalNpcIds = listOf("npc_cassius"),
                enemyNpcIds = emptyList(),
                knownSecrets = emptyList(),
                personalGoals = "Tüm İtalya gladyatör bahis havuzunu tekeline almak.",
                currentPosition = "Equites Ticaret Loncası Başkanı",
                isPatron = false,
                monthlyStipend = 0,
                patronObligation = "",
                avatarSymbol = "🪙"
            ),
            PoliticalCharacter(
                id = "npc_corvus",
                name = "Gaius Marius Corvus",
                title = "VI. Lejyon Kıdemli Tribünü",
                factionId = PoliticalFactionId.MILITARY,
                wealth = 34000,
                influence = 75,
                prestige = 880,
                personality = "Sert, Onurlu, Disiplinli",
                ambition = 60,
                greed = 20,
                integrity = 92,
                ideology = "Cumhuriyetçi Askeri Ahlak",
                relationshipWithPlayer = 15,
                favorsOwedToPlayer = 0,
                favorsOwedByPlayer = 0,
                alliedNpcIds = listOf("npc_cassius"),
                rivalNpcIds = listOf("npc_pulcher", "npc_balbus"),
                enemyNpcIds = emptyList(),
                knownSecrets = emptyList(),
                personalGoals = "Gladyatör dövüşlerini ordu için bir fizik ve cesaret okuluna dönüştürmek.",
                currentPosition = "Campania Garnizon Komutanı",
                isPatron = false,
                monthlyStipend = 0,
                patronObligation = "",
                avatarSymbol = "⚔"
            ),
            PoliticalCharacter(
                id = "npc_sulpicius",
                name = "Servius Sulpicius",
                title = "Mars Tapınağı Başrahibi",
                factionId = PoliticalFactionId.RELIGIOUS_AUTHORITIES,
                wealth = 42000,
                influence = 66,
                prestige = 790,
                personality = "Fanatik, Gelenekçi, Mağrur",
                ambition = 55,
                greed = 50,
                integrity = 75,
                ideology = "Teokratik Muhafazakar",
                relationshipWithPlayer = 20,
                favorsOwedToPlayer = 0,
                favorsOwedByPlayer = 0,
                alliedNpcIds = listOf("npc_cassius"),
                rivalNpcIds = listOf("npc_pomponius"),
                enemyNpcIds = emptyList(),
                knownSecrets = emptyList(),
                personalGoals = "Arenada dökülen kanın Mars'a adanmasını sağlamak ve adak gelirlerini artırmak.",
                currentPosition = "Campania Pontifex Heyeti Başkanı",
                isPatron = false,
                monthlyStipend = 0,
                patronObligation = "",
                avatarSymbol = "🔥"
            ),
            PoliticalCharacter(
                id = "npc_haterius",
                name = "Tiberius Claudius Haterius",
                title = "İmparatorluk Maliye Müfettişi",
                factionId = PoliticalFactionId.IMPERIAL_ADMINISTRATION,
                wealth = 72000,
                influence = 90,
                prestige = 810,
                personality = "Soğukkanlı, Titiz, Acımasız",
                ambition = 85,
                greed = 60,
                integrity = 65,
                ideology = "İmparatorluk Mutlakiyeti",
                relationshipWithPlayer = 0,
                favorsOwedToPlayer = 0,
                favorsOwedByPlayer = 1,
                alliedNpcIds = listOf("npc_balbus"),
                rivalNpcIds = listOf("npc_cassius"),
                enemyNpcIds = emptyList(),
                knownSecrets = emptyList(),
                personalGoals = "Taşra vergi kaçaklarını yakalayıp Roma sarayında Praetor makamına terfi etmek.",
                currentPosition = "Güney İtalya Fiscus Temsilcisi",
                isPatron = false,
                monthlyStipend = 0,
                patronObligation = "",
                avatarSymbol = "🦅"
            ),
            PoliticalCharacter(
                id = "npc_pomponius",
                name = "Sextus Pomponius",
                title = "Domus Auctor Baş Lanistası",
                factionId = PoliticalFactionId.RIVAL_LUDUSES,
                wealth = 51000,
                influence = 65,
                prestige = 690,
                personality = "Hırslı, Kinci, Kurnaz",
                ambition = 95,
                greed = 85,
                integrity = 15,
                ideology = "Vahşi Rekabetçilik",
                relationshipWithPlayer = -60,
                favorsOwedToPlayer = 0,
                favorsOwedByPlayer = 0,
                alliedNpcIds = emptyList(),
                rivalNpcIds = listOf("npc_cassius"),
                enemyNpcIds = listOf("npc_cassius"),
                knownSecrets = listOf(sec2),
                personalGoals = "Sizin ludusunuzu iflasa sürükleyip en iyi gladyatörlerinizi haraç mezat satın almak.",
                currentPosition = "Capua Lanistalar Loncası Sözcüsü",
                isPatron = false,
                monthlyStipend = 0,
                patronObligation = "",
                avatarSymbol = "🐍"
            ),
            PoliticalCharacter(
                id = "npc_valeria",
                name = "Valeria Messalina Minor",
                title = "Patrician Hanımefendisi & Hami",
                factionId = PoliticalFactionId.SENATORIAL_ELITE,
                wealth = 85000,
                influence = 80,
                prestige = 860,
                personality = "Sanatsever, Gururlu, Cömert",
                ambition = 75,
                greed = 30,
                integrity = 80,
                ideology = "Patrician Himayeciliği",
                relationshipWithPlayer = 25,
                favorsOwedToPlayer = 0,
                favorsOwedByPlayer = 0,
                alliedNpcIds = listOf("npc_cassius"),
                rivalNpcIds = emptyList(),
                enemyNpcIds = emptyList(),
                knownSecrets = emptyList(),
                personalGoals = "En şöhretli gladyatörlerin kendi hanedan renkleriyle dövüşmesini sağlamak.",
                currentPosition = "Gens Valeria Aile Reisi",
                isPatron = false,
                monthlyStipend = 1200,
                patronObligation = "Özel davetlerde gladyatörleri sergilemek ve şampiyon arması taşımak.",
                avatarSymbol = "👑"
            )
        )
    }

    fun createInitialNetwork(): List<NetworkConnection> {
        return listOf(
            NetworkConnection("net_1", "npc_cassius", "npc_sulpicius", RelationshipType.FRIENDSHIP, 85, "Eski senato müttefikleri ve dindar Optimates kanadı."),
            NetworkConnection("net_2", "npc_cassius", "npc_corvus", RelationshipType.POLITICAL_ALLIANCE, 75, "Cumhuriyet anayasasını korumak için askeri ve senatoryal pakt."),
            NetworkConnection("net_3", "npc_cassius", "npc_valeria", RelationshipType.FAMILY, 90, "Gens Valeria ve Gens Cassia arasındaki akrabalık bağı."),
            NetworkConnection("net_4", "npc_cassius", "npc_pomponius", RelationshipType.RIVALRY, 95, "Pomponius'un kaba plebeian tarzı Cassius'u iğrendiriyor."),
            NetworkConnection("net_5", "npc_pulcher", "npc_balbus", RelationshipType.BUSINESS, 80, "Arena bahis gelirleri ve bilet komisyonları gizli ortaklığı."),
            NetworkConnection("net_6", "npc_pulcher", "npc_pomponius", RelationshipType.DEBT, 65, "Pomponius, Pulcher'e 8,000 Denarii gizli seçim kredisi verdi."),
            NetworkConnection("net_7", "npc_balbus", "npc_haterius", RelationshipType.PATRONAGE, 70, "Haterius, Balbus'un tahıl filolarına vergi muafiyeti sağlıyor."),
            NetworkConnection("net_8", "npc_corvus", "npc_pulcher", RelationshipType.FEUD, 75, "Corvus, Pulcher'in ordu tedarikçilerinden rüşvet aldığını biliyor.")
        )
    }

    fun createInitialScandals(): List<PoliticalScandal> {
        return listOf(
            PoliticalScandal(
                id = "scandal_1",
                title = "Arena Kapı Harcı ve Bahis Vurgunu",
                description = "Capua arenasında bilet paralarının bir kısmının senatörlere ve yargıçlara aktarıldığı iddiası halk arasında konuşuluyor.",
                involvedNpcIds = listOf("npc_pulcher", "npc_balbus"),
                involvedFactionIds = listOf(PoliticalFactionId.ARENA_OFFICIALS, PoliticalFactionId.MERCHANT_GUILD),
                severity = 65,
                publicAwareness = 45,
                evidenceLevel = 30,
                status = ScandalStatus.BREWING,
                daysRemaining = 6,
                suppressionCostGold = 2200,
                suppressionCostFavor = 1
            )
        )
    }

    fun createInitialCalendar(startDay: Int): List<PoliticalCalendarEntry> {
        return listOf(
            PoliticalCalendarEntry(
                id = "pol_cal_1",
                day = startDay + 2,
                title = "Capua Belediye Seçimleri & Yargıç Oylaması",
                category = "ELECTION",
                factionId = PoliticalFactionId.ARENA_OFFICIALS,
                impactDescription = "Yeni yargıç belirlenecek; kazanacak adaya göre arena lisans harçları değişecek."
            ),
            PoliticalCalendarEntry(
                id = "pol_cal_2",
                day = startDay + 5,
                title = "Ludi Martiales (Mars Kutsal Oyunları)",
                category = "SACRED_FESTIVAL",
                factionId = PoliticalFactionId.RELIGIOUS_AUTHORITIES,
                impactDescription = "Mars Tapınağı tarafından düzenlenen prestijli festival. Galibiyetler +%50 Şan kazandırır."
            ),
            PoliticalCalendarEntry(
                id = "pol_cal_3",
                day = startDay + 9,
                title = "İmparatorluk Fiscus Teftişi & Vergi Günü",
                category = "TAX_COLLECTION",
                factionId = PoliticalFactionId.IMPERIAL_ADMINISTRATION,
                impactDescription = "İmparatorluk müfettişleri tüm ludus kasalarını ve muhasebe kayıtlarını inceleyecek."
            ),
            PoliticalCalendarEntry(
                id = "pol_cal_4",
                day = startDay + 14,
                title = "Senato Gizli Kararnamesi: Colosseum Kota Dağıtımı",
                category = "SENATE_VOTE",
                factionId = PoliticalFactionId.SENATORIAL_ELITE,
                impactDescription = "Roma'ya çağrılacak elit gladyatör okullarının belirleneceği kritik oylama."
            )
        )
    }

    fun createInitialSecrets(): List<PoliticalSecret> {
        return listOf(
            PoliticalSecret(
                id = "sec_player_1",
                targetNpcId = "npc_pulcher",
                targetName = "Lucius Decimus Pulcher",
                title = "Pulcher'in Gizli Bahis Komisyonu",
                description = "Başyargıç Pulcher'in resmi arena bahislerinden gizlice %8 pay alıp vergi kaçırdığına dair muhasebe kopyası.",
                category = SecretCategory.CORRUPTION,
                severity = SecretSeverity.MODERATE,
                isVerified = true
            )
        )
    }

    /**
     * Generates a procedural or chain political event if conditions match.
     */
    fun checkAndGeneratePoliticalEvent(
        day: Int,
        factions: List<PoliticalFaction>,
        characters: List<PoliticalCharacter>,
        scandals: List<PoliticalScandal>,
        patronId: String?,
        currentEvent: PoliticalEvent?
    ): PoliticalEvent? {
        if (currentEvent != null) return null // Wait until current is resolved

        val pulcher = characters.find { it.id == "npc_pulcher" }
        val cassius = characters.find { it.id == "npc_cassius" }
        val pomponius = characters.find { it.id == "npc_pomponius" }
        val balbus = characters.find { it.id == "npc_balbus" }

        // Event 1: Arena Entrance Fee Hike (Direct user request requirement)
        if (day % 6 == 2) {
            return PoliticalEvent(
                id = "ev_fee_hike_$day",
                title = "ARENA GİRİŞ HARCI VE YARGIÇ BASKISI",
                narrative = "Belediye Başyargıcı Pulcher'in emriyle yarınki Capua oyunlarına katılım harcı 3,000 Denarii'ye yükseltildi! Resmi gerekçe 'kamu asayişi ve lejyon koruması', ancak herkes paranın yargıcın cebine gideceğini biliyor.",
                instigatorName = "Lucius Decimus Pulcher",
                instigatorTitle = "Capua Başyargıcı",
                factionId = PoliticalFactionId.ARENA_OFFICIALS,
                isUrgent = true,
                expiresDay = day + 1,
                choices = listOf(
                    PoliticalChoice(
                        id = "choice_pay_fee",
                        label = "Parayı Öde (3,000 Denarii)",
                        costDescription = "-3,000 Denarii",
                        effectDescription = "Yargıç memnun olur, arena kapıları sorunsuz açılır.",
                        requiredGold = 3000,
                        goldDelta = -3000,
                        influenceDelta = +2,
                        reputationDelta = +3,
                        factionOpinionsDelta = mapOf(PoliticalFactionId.ARENA_OFFICIALS to 15),
                        targetNpcId = "npc_pulcher",
                        npcRelationshipDelta = 15,
                        consequenceNarrative = "Harç ödendi. Yargıç Pulcher gülümsedi ve en iyi dövüş sırasını size tahsis etti."
                    ),
                    PoliticalChoice(
                        id = "choice_negotiate_fee",
                        label = "Pazarlık Et & Nüfuz Kullan (1 Lütuf)",
                        costDescription = "-1 Siyasi Lütuf",
                        effectDescription = "Harcı yarıya (1,500) indir, itibarını koru.",
                        requiredFavor = 1,
                        requiredGold = 1500,
                        goldDelta = -1500,
                        favorDelta = -1,
                        influenceDelta = +5,
                        factionOpinionsDelta = mapOf(PoliticalFactionId.ARENA_OFFICIALS to 5),
                        targetNpcId = "npc_pulcher",
                        npcRelationshipDelta = 5,
                        consequenceNarrative = "Siyasi nüfuzunuzu masaya koydunuz. Pulcher geri adım attı ve harcı 1,500'e indirdi."
                    ),
                    PoliticalChoice(
                        id = "choice_refuse_fee",
                        label = "Ödemeyi Reddet & Boykot Et",
                        costDescription = "0 Altın | Yargıç ile sert gerilim",
                        effectDescription = "Yargıç düşman olur, gelecekteki maçlara ceza gelebilir.",
                        influenceDelta = -5,
                        reputationDelta = +10,
                        factionOpinionsDelta = mapOf(
                            PoliticalFactionId.ARENA_OFFICIALS to -25,
                            PoliticalFactionId.SENATORIAL_ELITE to -10
                        ),
                        targetNpcId = "npc_pulcher",
                        npcRelationshipDelta = -30,
                        consequenceNarrative = "Harcı ödemeyi reddettiniz! Pulcher küplere bindi: 'Bu küstahlığın bedelini arenada kanla ödeyeceksin!'",
                        nextEventChainId = "chain_arena_ban_$day"
                    ),
                    PoliticalChoice(
                        id = "choice_bribe_fee",
                        label = "Yargıca Rüşvet Ver (1,800 Denarii)",
                        costDescription = "-1,800 Denarii",
                        effectDescription = "Harcı sildir, gizli lütuf borcu oluştur.",
                        requiredGold = 1800,
                        goldDelta = -1800,
                        favorDelta = +1,
                        reputationDelta = -5,
                        factionOpinionsDelta = mapOf(PoliticalFactionId.ARENA_OFFICIALS to 10),
                        targetNpcId = "npc_pulcher",
                        npcRelationshipDelta = 20,
                        consequenceNarrative = "Gizli kese el değiştirdi. Harç kayıtlardan sessizce silindi. Yargıç artık size bir lütuf borçlu."
                    ),
                    PoliticalChoice(
                        id = "choice_patron_shield",
                        label = "Hami Senatör Cassius'tan Koruma İste",
                        costDescription = "Hami ilişkisi gerektirir",
                        effectDescription = "Cassius yargıcı azarlar, 0 altın harcarsın, Cassius lütuf bekler.",
                        requiredInfluence = 30,
                        influenceDelta = +8,
                        reputationDelta = +5,
                        factionOpinionsDelta = mapOf(
                            PoliticalFactionId.SENATORIAL_ELITE to 5,
                            PoliticalFactionId.ARENA_OFFICIALS to -15
                        ),
                        targetNpcId = "npc_cassius",
                        npcRelationshipDelta = 10,
                        consequenceNarrative = "Senatör Cassius araya girdi: 'Pulcher! Kendi açgözlülüğün için asil oyunları lekeleyemezsin.' Harç tamamen iptal edildi!"
                    )
                )
            )
        }

        // Event 2: Match-Fixing Pressure from Merchant Guild & Rival
        if (day % 7 == 4 && pomponius != null) {
            return PoliticalEvent(
                id = "ev_match_fix_$day",
                title = "ŞİKE VE ŞÖHRET TEKLİFİ: BAHİS MANİPÜLASYONU",
                narrative = "Tahıl tüccarı Balbus ve rakip Lanista Pomponius'un adamları gizlice ludusunuza geldi. Sıradaki maçta şampiyonunuzun 2. rauntta kasıtlı olarak yere düşmesini istiyorlar. Karşılığında büyük bir altın serveti teklif ediliyor.",
                instigatorName = "Quintus Balbus & Pomponius",
                instigatorTitle = "Bahis Konsorsiyumu",
                factionId = PoliticalFactionId.MERCHANT_GUILD,
                isUrgent = false,
                expiresDay = day + 2,
                choices = listOf(
                    PoliticalChoice(
                        id = "choice_accept_fix",
                        label = "Teklifi Kabul Et (+5,000 Denarii)",
                        costDescription = "+5,000 Altın | Gladyatör Şanı Düşer (-250)",
                        effectDescription = "Devasa altın kazancı, ancak ifşa olursa ağır skandal!",
                        goldDelta = +5000,
                        prestigeDelta = -250,
                        reputationDelta = -20,
                        factionOpinionsDelta = mapOf(
                            PoliticalFactionId.MERCHANT_GUILD to 25,
                            PoliticalFactionId.RIVAL_LUDUSES to 15,
                            PoliticalFactionId.SENATORIAL_ELITE to -15
                        ),
                        targetNpcId = "npc_balbus",
                        npcRelationshipDelta = 20,
                        consequenceNarrative = "Kesenin ağzı açıldı, altınlar kasaya girdi. Ancak şampiyonunuz bu utanç verici anlaşmadan ötürü size diş biliyor."
                    ),
                    PoliticalChoice(
                        id = "choice_reject_fix",
                        label = "Teklifi Sertçe Reddet & Kapı Dışarı Et",
                        costDescription = "0 Altın | Onur ve Prestij Artışı",
                        effectDescription = "Şan artar (+200), Tüccar ve rakipler düşmanlaşır.",
                        prestigeDelta = +200,
                        influenceDelta = +6,
                        reputationDelta = +15,
                        factionOpinionsDelta = mapOf(
                            PoliticalFactionId.SENATORIAL_ELITE to 15,
                            PoliticalFactionId.MILITARY to 15,
                            PoliticalFactionId.MERCHANT_GUILD to -20
                        ),
                        targetNpcId = "npc_pomponius",
                        npcRelationshipDelta = -25,
                        consequenceNarrative = "Elçileri kılıç zoruyla kovdunuz! 'Biz satılık piyon değil, Roma'nın aslanlarıyız!' Senatörler onurunuzu duydu."
                    ),
                    PoliticalChoice(
                        id = "choice_expose_fix",
                        label = "Komployu Senato ve Halka İfşa Et",
                        costDescription = "Yüksek Nüfuz Gerektirir",
                        effectDescription = "Büyük skandal patlar, Pomponius ve Balbus soruşturma geçirir.",
                        requiredInfluence = 45,
                        prestigeDelta = +350,
                        influenceDelta = +12,
                        reputationDelta = +25,
                        factionOpinionsDelta = mapOf(
                            PoliticalFactionId.SENATORIAL_ELITE to 20,
                            PoliticalFactionId.IMPERIAL_ADMINISTRATION to 15,
                            PoliticalFactionId.RIVAL_LUDUSES to -40
                        ),
                        consequenceNarrative = "Senato Divanı önünde komplo belgelerini sergilediniz. Halk Pomponius'un konağını taşladı!",
                        triggersScandal = PoliticalScandal(
                            id = "scandal_fix_$day",
                            title = "Pomponius ve Balbus Şike Skandalı",
                            description = "Capua arenasında bahis şikesi organize eden Pomponius hakkında resmi senatoryal tahkikat başlatıldı.",
                            involvedNpcIds = listOf("npc_pomponius", "npc_balbus"),
                            involvedFactionIds = listOf(PoliticalFactionId.RIVAL_LUDUSES, PoliticalFactionId.MERCHANT_GUILD),
                            severity = 80,
                            publicAwareness = 85,
                            evidenceLevel = 90,
                            status = ScandalStatus.ACTIVE_HEADLINE,
                            daysRemaining = 5
                        )
                    )
                )
            )
        }

        // Event 3: Imperial Tax Audit (Curia Caesaris)
        if (day % 10 == 8) {
            return PoliticalEvent(
                id = "ev_tax_audit_$day",
                title = "İMPARATORLUK MALİYE TEFTİŞİ (FISCUS CAESARIS)",
                narrative = "Roma'dan gönderilen kibirli İmparatorluk Maliye Müfettişi Tiberius Haterius, ludusunuzun defterlerini mühürletti. 'Kayıt dışı ödül paraları ve köle alım vergileri' iddiasıyla 4,000 Denarii usulsüzlük cezası kesti.",
                instigatorName = "Tiberius Claudius Haterius",
                instigatorTitle = "İmparatorluk Vekili",
                factionId = PoliticalFactionId.IMPERIAL_ADMINISTRATION,
                isUrgent = true,
                expiresDay = day + 1,
                choices = listOf(
                    PoliticalChoice(
                        id = "choice_pay_tax",
                        label = "Cezayı Öde (4,000 Denarii)",
                        costDescription = "-4,000 Denarii",
                        effectDescription = "İmparatorluk sadakati tasdiklenir, teftiş kalkar.",
                        requiredGold = 4000,
                        goldDelta = -4000,
                        influenceDelta = +4,
                        factionOpinionsDelta = mapOf(PoliticalFactionId.IMPERIAL_ADMINISTRATION to 20),
                        targetNpcId = "npc_haterius",
                        npcRelationshipDelta = 15,
                        consequenceNarrative = "Müfettiş altınları tek tek tarttı ve makbuzunu verdi. Teftiş mühürleri kaldırıldı."
                    ),
                    PoliticalChoice(
                        id = "choice_bribe_auditor",
                        label = "Müfettişi Gizlice Satın Al (2,200 Denarii)",
                        costDescription = "-2,200 Denarii",
                        effectDescription = "Defterler düzeltilir, Haterius ile gizli bağ kurulur.",
                        requiredGold = 2200,
                        goldDelta = -2200,
                        favorDelta = +1,
                        reputationDelta = -8,
                        factionOpinionsDelta = mapOf(PoliticalFactionId.IMPERIAL_ADMINISTRATION to 10),
                        targetNpcId = "npc_haterius",
                        npcRelationshipDelta = 25,
                        consequenceNarrative = "Müfettişin masasına gizlice bırakılan altın kese kayıtlara 'tam ve kusursuz' mührü vurdurdu."
                    ),
                    PoliticalChoice(
                        id = "choice_appeal_senate",
                        label = "Senato'ya İtiraz Et (2 Lütuf Harca)",
                        costDescription = "-2 Siyasi Lütuf",
                        effectDescription = "Senato kararı iptal ettirir, ceza sıfırlanır.",
                        requiredFavor = 2,
                        favorDelta = -2,
                        influenceDelta = +10,
                        factionOpinionsDelta = mapOf(
                            PoliticalFactionId.SENATORIAL_ELITE to 10,
                            PoliticalFactionId.IMPERIAL_ADMINISTRATION to -15
                        ),
                        consequenceNarrative = "Senatör dostlarınız Sezar'ın bürokratına haddini bildirdi. Ceza fermanı yırtıldı."
                    )
                )
            )
        }

        return null
    }

    /**
     * Advances the living political simulation by one day:
     * - Shifts opinions, updates ongoing scandals, simulates background NPC actions.
     * - Generates historical dispatches for the chronicle.
     */
    fun simulatePoliticalDay(
        currentDay: Int,
        factions: List<PoliticalFaction>,
        characters: List<PoliticalCharacter>,
        scandals: List<PoliticalScandal>,
        calendar: List<PoliticalCalendarEntry>,
        playerSecrets: List<PoliticalSecret>,
        resources: PoliticalResourceLedger,
        patronId: String?
    ): PoliticalSimulationDayResult {
        val dispatches = mutableListOf<String>()

        // 1. Advance Scandals
        val updatedScandals = scandals.mapNotNull { scandal ->
            val remaining = scandal.daysRemaining - 1
            if (remaining <= 0) {
                dispatches.add("Soruşturma Raporu: '${scandal.title}' davası senato divanında nihayete erdirildi.")
                null // resolved
            } else {
                // Public awareness fluctuates
                val newAwareness = min(100, scandal.publicAwareness + Random.nextInt(-5, 10))
                scandal.copy(daysRemaining = remaining, publicAwareness = newAwareness)
            }
        }.toMutableList()

        // 2. Background NPC Machinations
        val updatedCharacters = characters.map { character ->
            var newWealth = character.wealth + Random.nextInt(-500, 1200)
            var newInfluence = character.influence
            var newRel = character.relationshipWithPlayer

            // If patron, give small positive drift
            if (character.id == patronId) {
                newRel = min(100, newRel + 1)
            }

            // High ambition NPCs try to gain influence
            if (character.ambition > 70 && Random.nextFloat() < 0.25f) {
                newInfluence = min(100, newInfluence + 1)
            }

            character.copy(
                wealth = max(1000, newWealth),
                influence = newInfluence,
                relationshipWithPlayer = newRel
            )
        }

        // 3. Faction Shifts
        val updatedFactions = factions.map { faction ->
            var op = faction.opinionOfPlayer
            // Slowly gravitate towards zero if player does nothing
            if (op > 20 && Random.nextFloat() < 0.15f) op -= 1
            if (op < -20 && Random.nextFloat() < 0.15f) op += 1
            faction.copy(opinionOfPlayer = op)
        }

        // 4. Random political gossip / intrigue in Capua
        val randomRoll = Random.nextInt(100)
        if (randomRoll < 20) {
            val rumorNpc = updatedCharacters.random()
            dispatches.add("Roma Kulisleri: ${rumorNpc.name} (${rumorNpc.title}) yeni bir siyasi ittifak arayışında.")
        } else if (randomRoll < 35) {
            dispatches.add("Forum Fısıltıları: Senato kulislerinde gladyatör harçları ve tahıl sevkiyatı hararetle tartışılıyor.")
        }

        // 5. Update Calendar events
        val updatedCalendar = calendar.map { cal ->
            if (cal.day == currentDay && !cal.isResolved) {
                dispatches.add("Siyasi Takvim: Bugün '${cal.title}' gerçekleşti! ${cal.impactDescription}")
                cal.copy(isResolved = true)
            } else cal
        }

        // 6. Resources: Passive influence based on reputation and prestige
        val updatedResources = resources.copy(
            influence = min(100, max(10, resources.influence + (if (resources.reputation > 60) 1 else 0)))
        )

        return PoliticalSimulationDayResult(
            updatedFactions = updatedFactions,
            updatedCharacters = updatedCharacters,
            updatedScandals = updatedScandals,
            updatedCalendar = updatedCalendar,
            updatedResources = updatedResources,
            narrativeDispatches = dispatches
        )
    }

    /**
     * Arena Game Mechanics affected by Politics:
     * 1. Entrance Fee Modifier:
     *    - Arena Officials Allied (>50): -30% discount
     *    - Arena Officials Hostile (<-30): +40% penalty
     */
    fun getArenaEntranceFeeModifier(factions: List<PoliticalFaction>): Float {
        val officials = factions.find { it.id == PoliticalFactionId.ARENA_OFFICIALS } ?: return 1.0f
        return when {
            officials.opinionOfPlayer >= 60 -> 0.70f // 30% discount
            officials.opinionOfPlayer >= 25 -> 0.85f // 15% discount
            officials.opinionOfPlayer >= -20 -> 1.00f // normal
            officials.opinionOfPlayer >= -60 -> 1.25f // 25% surcharge
            else -> 1.50f // 50% punitive surcharge!
        }
    }

    /**
     * 2. Prize Purse Multiplier:
     *    - Merchant Guild and Senatorial Elite support grants lucrative sponsor bonuses!
     */
    fun getArenaPurseMultiplier(factions: List<PoliticalFaction>, isPatronActive: Boolean): Float {
        val merchants = factions.find { it.id == PoliticalFactionId.MERCHANT_GUILD }
        val senators = factions.find { it.id == PoliticalFactionId.SENATORIAL_ELITE }

        var mult = 1.0f
        if (isPatronActive) mult += 0.15f
        if (merchants != null && merchants.opinionOfPlayer >= 40) mult += 0.15f
        if (senators != null && senators.opinionOfPlayer >= 50) mult += 0.20f
        if (merchants != null && merchants.opinionOfPlayer <= -40) mult -= 0.15f
        return max(0.6f, mult)
    }

    /**
     * 3. Colosseum & Championship Title Bout Access:
     *    - Requires at least 25 favor/opinion with Senatorial Elite OR Imperial Administration!
     */
    fun canAccessChampionshipBouts(factions: List<PoliticalFaction>): Pair<Boolean, String> {
        val senators = factions.find { it.id == PoliticalFactionId.SENATORIAL_ELITE }
        val imperial = factions.find { it.id == PoliticalFactionId.IMPERIAL_ADMINISTRATION }

        val senOp = senators?.opinionOfPlayer ?: 0
        val impOp = imperial?.opinionOfPlayer ?: 0

        if (senOp >= 20 || impOp >= 15) {
            return Pair(true, "Senato veya Saray desteğiniz sayesinde şampiyona lisansı onaylı.")
        }
        return Pair(false, "Şampiyona için Senato veya Saray onayına (ilişki en az +20) ihtiyacınız var.")
    }
}

data class PoliticalSimulationDayResult(
    val updatedFactions: List<PoliticalFaction>,
    val updatedCharacters: List<PoliticalCharacter>,
    val updatedScandals: List<PoliticalScandal>,
    val updatedCalendar: List<PoliticalCalendarEntry>,
    val updatedResources: PoliticalResourceLedger,
    val narrativeDispatches: List<String>
)
