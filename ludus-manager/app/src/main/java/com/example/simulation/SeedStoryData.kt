package com.example.simulation

import com.example.model.*

/**
 * Handcrafted initial seeds for World Memory, Mysteries, Rumor Network, and Story Threads.
 * Designed to feel authentic, Roman, suspenseful, and grounded in historical realism.
 */
object SeedStoryData {

    fun createInitialWorldMemory(): MutableList<MemoryEntry> {
        return mutableListOf(
            MemoryEntry(
                id = "mem_init_1",
                eventType = MemoryEventType.DISCOVERY_EVENT,
                date = 1,
                location = "Capua Banliyösü",
                participantIds = listOf("player"),
                causeDescription = "Marcus Aurelius Valerius, babasından kalan eski zeytinyağı ambarını gladyatör kışlasına çevirdi.",
                hiddenFacts = "Arsa üzerinde Senator Cassius'un eski bir ipotek hakkı bulunmaktadır.",
                impactTags = listOf("Kuruluş", "Miras", "Capua"),
                importance = EventImportance.IMPORTANT
            ),
            MemoryEntry(
                id = "mem_init_2",
                eventType = MemoryEventType.ARENA_EVENT,
                date = 5,
                location = "Capua Taşra Arenası",
                participantIds = listOf("glad_1", "fighter_drusus"),
                causeDescription = "Titus, Drusus'a karşı zorlu bir gösteri maçı kazandı ve halkın takdirini topladı.",
                hiddenFacts = "Drusus kalkan kolundaki eski sakatlık yüzünden gardını düşürdü.",
                impactTags = listOf("İlk Zafer", "Drusus", "Kum"),
                importance = EventImportance.MINOR
            ),
            MemoryEntry(
                id = "mem_init_3",
                eventType = MemoryEventType.RELATIONSHIP_EVENT,
                date = 8,
                location = "Capua Forumu",
                participantIds = listOf("player", "fighter_cassian"),
                causeDescription = "Domus Auctor lanistası ve dövüşçüsü Cassian, pazarda açıkça ludusunuza hakaret etti.",
                hiddenFacts = "Cassian'ın sahibi Decimus, Capua arena lisans kurulunu elinde tutuyor.",
                impactTags = listOf("Husumet", "Cassian", "Tehdit"),
                importance = EventImportance.IMPORTANT
            )
        )
    }

    fun createInitialMysteries(): MutableList<WorldMystery> {
        return mutableListOf(
            WorldMystery(
                id = "mystery_empty_bed",
                title = "Boş Kalan Yatak (The Empty Bed)",
                category = MysteryCategory.DISAPPEARANCE,
                initialClue = "Sabah kışla teftişinde gladyatör koğuşundaki yataklardan birinin boş olduğu, şiltesinin düzgünce katlandığı fakat tahta kılıcının yerinde olmadığı görüldü.",
                discoveredDay = 12,
                knownFacts = mutableListOf(
                    "Koğuş kapısının bronz sürgüsü içeriden açılmış; zorlama izi yok.",
                    "Kaçan dövüşçünün sandığındaki gümüş fibula (toka) ve şahsi eşyaları yerinde duruyor."
                ),
                unknownSuspicions = mutableListOf(
                    "Gece nöbetçisi uyuyakaldı mı, yoksa rüşvetle mi susturuldu?",
                    "Bir rakip lanista gizlice transfer teklifi mi sundu?"
                ),
                possibleExplanations = listOf(
                    MysteryHypothesis("hypo_1", "Gece karanlığında Subura'ya firar etti ve kaçak köle hayatı yaşıyor.", 25, false),
                    MysteryHypothesis("hypo_2", "Domus Auctor casusu tarafından parayla ayartılıp rakip kışlaya kaçırıldı.", 55, true),
                    MysteryHypothesis("hypo_3", "Yeraltı kumar çeteleri eski bir bahis borcu için rehin aldı.", 20, false)
                ),
                discoveredEvidence = mutableListOf(
                    EvidencePiece(
                        id = "ev_footprint",
                        title = "Çamurlu Sandalet İzi",
                        description = "Arka bahçe duvarının dibinde kaliteli Gallia derisinden yapılma üçüncü bir şahsın sandalet izleri bulundu.",
                        discoveredDay = 12,
                        source = "Ludus Avlusu",
                        epistemicStatus = EpistemicStatus.CONFIRMED
                    )
                ),
                investigationPaths = listOf(
                    InvestigationPath(
                        id = "path_search_dorm",
                        label = "Koğuşu ve Yatak Şiltesini Ara",
                        actionType = InvestigationActionType.SEARCH_DORM,
                        costDenarii = 0,
                        costFavor = 0,
                        description = "Yatağın altındaki tahtaları ve samanları tek tek kontrol et.",
                        riskNote = "Tehlikesiz"
                    ),
                    InvestigationPath(
                        id = "path_bribe_guard",
                        label = "Gece Nöbetçisini Sorgula & Rüşvet Ver",
                        actionType = InvestigationActionType.BRIBE_INFORMANT,
                        costDenarii = 150,
                        costFavor = 0,
                        description = "Gece kapıda duran nöbetçiye 150 Denarii uzatıp gerçeği anlatmasını iste.",
                        riskNote = "Nöbetçinin sadakati test edilir"
                    ),
                    InvestigationPath(
                        id = "path_scout_subura",
                        label = "Subura Meyhanelerine Gözcü Gönder",
                        actionType = InvestigationActionType.SCOUT_LOCATION,
                        costDenarii = 300,
                        costFavor = 0,
                        description = "Şehirdeki köle tüccarları ve tavernalara haber sal.",
                        riskNote = "Rakip lanistanın kulağına gidebilir"
                    )
                ),
                status = MysteryStatus.ACTIVE,
                resolutionSummary = null,
                rewardSummary = "+150 Prestij, İhanet Eden Muhbirin İfşası ve 500 Denarii Tazminat"
            ),
            WorldMystery(
                id = "mystery_silver_coin",
                title = "Yılan Damgalı Gümüş Sikke (The Neapolis Coin)",
                category = MysteryCategory.WEAPON_ORIGIN,
                initialClue = "Antrenman kumlarının arasında, Roma darphanesine ait olmayan, bir tarafında yılan diğer tarafında kanlı hançer damgası bulunan yabancı bir gümüş sikke bulundu.",
                discoveredDay = 14,
                knownFacts = mutableListOf(
                    "Sikke standart Roma Denarii'sinden daha saf gümüş içeriyor.",
                    "Üzerindeki damga Neapolis korsanlarının ve kaçakçı şebekelerinin gizli simgesidir."
                ),
                unknownSuspicions = mutableListOf(
                    "Kışlaya bu sikkeyi kim getirdi?",
                    "Gladyatörlerden biri gizlice yeraltı organizatörleriyle mi görüşüyor?"
                ),
                possibleExplanations = listOf(
                    MysteryHypothesis("hypo_coin_1", "Korsanlardan satın alınan eski bir ganimet parçası.", 30, false),
                    MysteryHypothesis("hypo_coin_2", "Neapolis yeraltı dövüş çukurlarına davet için kullanılan gizli bir giriş jetonu.", 70, true)
                ),
                discoveredEvidence = mutableListOf(),
                investigationPaths = listOf(
                    InvestigationPath(
                        id = "path_ask_blacksmith",
                        label = "Demirci Servius'a Sikkeyi Göster",
                        actionType = InvestigationActionType.ASK_AROUND,
                        costDenarii = 50,
                        costFavor = 0,
                        description = "Servius metalin ve damganın nereden geldiğini teşhis edebilir.",
                        riskNote = "Risksiz"
                    ),
                    InvestigationPath(
                        id = "path_underground_lead",
                        label = "Yeraltı Kaçakçısı Lucius ile Temasa Geç",
                        actionType = InvestigationActionType.CONSULT_UNDERGROUND,
                        costDenarii = 200,
                        costFavor = 0,
                        description = "Subura şarap mahzenindeki Lucius'a sikkeyi gösterip parolanın anlamını sor.",
                        riskNote = "Yeraltı bağlantısı kurar"
                    )
                ),
                status = MysteryStatus.ACTIVE,
                resolutionSummary = null,
                rewardSummary = "Gizli Neapolis Yeraltı Dövüş Ağına Erişim ve +400 Denarii"
            )
        )
    }

    fun createInitialRumors(): MutableList<Rumor> {
        return mutableListOf(
            Rumor(
                id = "rumor_wolf_subura",
                source = RumorSource.MERCHANT,
                targetId = "boss_wolf_secundus",
                subject = "The Wolf",
                headline = "Subura Mahzenlerinde Yenilmez Bir Cellat",
                fullGossipText = "Tüccarlar Subura'nın eski su kemeri altındaki mahzenlerde 'The Wolf' adında eski bir Thraex'in gizli bahis dövüşlerinde önüne geleni katlettiğini fısıldıyor.",
                truthStatus = RumorTruthStatus.TRUE,
                reliability = 0.80f,
                createdDay = 12,
                location = "Capua Subura",
                spreadCount = 2,
                distortionLevel = 0,
                isInvestigated = false,
                investigationCostDenarii = 150,
                investigationLead = "Kaçakçı Lucius bu dövüşlerin komisyonunu topluyor."
            ),
            Rumor(
                id = "rumor_pompeii_champ_injury",
                source = RumorSource.TAVERN_GOSSIP,
                targetId = "fighter_pompeii_champ",
                subject = "Marcus & Pompeii Arenası",
                headline = "Pompeii Şampiyonunun Kılıç Kolu Sakatlandı",
                fullGossipText = "Via Appia üzerindeki meyhanede konuşulanlara göre Pompeii şampiyonu Marcus antrenmanda ağır sakatlanmış ve bir sonraki unvan maçına çıkamayabilirmiş.",
                truthStatus = RumorTruthStatus.PARTIALLY_TRUE,
                reliability = 0.45f,
                createdDay = 13,
                location = "Pompeii Yolu",
                spreadCount = 1,
                distortionLevel = 0,
                isInvestigated = false,
                investigationCostDenarii = 100,
                investigationLead = "Sakatlık var fakat iddia edildiği kadar ölümcül değil; dikkat dağıtma taktiği olabilir."
            ),
            Rumor(
                id = "rumor_senate_sumptuary_tax",
                source = RumorSource.POLITICAL_CONTACT,
                targetId = "npc_cassius",
                subject = "Senato Vergi Tasarısı",
                headline = "Taşra Luduslarına Ağır İthalat Vergisi Kapıda",
                fullGossipText = "Senatör Cassius'un kâtibi, Roma Senatosu'nun Campania bölgesindeki bağımsız lanistalardan gladyatör başına yıllık 200 Denarii asalet harcı almayı tartıştığını sızdırdı.",
                truthStatus = RumorTruthStatus.TRUE,
                reliability = 0.85f,
                createdDay = 14,
                location = "Roma Curia",
                spreadCount = 3,
                distortionLevel = 0,
                isInvestigated = false,
                investigationCostDenarii = 200,
                investigationLead = "Senatör Marcus Cassius ile dostane ilişkiler kurulursa bu vergi engellenebilir."
            ),
            Rumor(
                id = "rumor_poisoned_grain",
                source = RumorSource.GLADIATOR,
                targetId = null,
                subject = "Pazar Tahılı",
                headline = "Sicilya Tahılında Mantar Salgını",
                fullGossipText = "Koğuştaki dövüşçüler son gelen buğday çuvallarının küflü koktuğunu ve yemekten sonra mide ağrısı çektiklerini söylüyor.",
                truthStatus = RumorTruthStatus.FALSE,
                reliability = 0.35f,
                createdDay = 15,
                location = "Capua Pazarı",
                spreadCount = 1,
                distortionLevel = 1,
                isInvestigated = false,
                investigationCostDenarii = 80,
                investigationLead = "Dövüşçüler sadece aşçının baharatını beğenmedi; tahılda zehir yok."
            )
        )
    }

    fun createInitialStoryThreads(): MutableList<StoryThread> {
        return mutableListOf(
            StoryThread(
                id = "thread_cassian_blood_feud",
                title = "Cassian'ın Kan Davası (Blood Drinker's Vow)",
                synopsis = "Domus Auctor'un kibirli baş gladyatörü Cassian, yerel Capua kumlarında halkın gözü önünde kışlanıza meydan okudu. Geri adım atmak itibar kaybettirecek.",
                originEventId = "mem_init_3",
                associatedNpcIds = listOf("fighter_cassian", "npc_cassius"),
                associatedMysteryId = null,
                status = StoryThreadStatus.ACTIVE,
                startDay = 8,
                lastActivityDay = 12,
                urgency = 7,
                currentStageIndex = 0,
                stages = listOf(
                    StoryStage(
                        stageIndex = 0,
                        title = "Meydan Okuma ve Gergin Bekleyiş",
                        narrativeDescription = "Cassian forumda açıkça lanistanıza hakaret etti ve arenada boyun eğdireceğini ilan etti. Müsabaka takvimde Gün 17 olarak işaretlendi.",
                        choices = listOf("Antrenmanı sertleştir", "Cassian'ın kılıç stilini casusla", "Hakem kuruluna itiraz et"),
                        requiredDay = 12
                    ),
                    StoryStage(
                        stageIndex = 1,
                        title = "Capua Kumlarında Randevu",
                        narrativeDescription = "Gün 17'de Capua Amfitiyatrosu'nda resmi maç günü gelecek. Zafer veya mağlubiyet Capua'daki tüm dengeleri değiştirecek.",
                        choices = listOf("Öldür (Pollice Verso)", "Bağışla (Missio)", "Aşağıla"),
                        requiredDay = 17
                    ),
                    StoryStage(
                        stageIndex = 2,
                        title = "Sonuçların Yankısı",
                        narrativeDescription = "Müsabakanın ardından Cassian ya ölecek, ya intikam yemini edecek, ya da ludusunuzun kudretini kabullenecek.",
                        choices = emptyList(),
                        requiredDay = 20
                    )
                ),
                connectedThreadIds = mutableListOf()
            ),
            StoryThread(
                id = "thread_subura_syndicate",
                title = "Subura Gölgeleri ve The Wolf",
                synopsis = "Capua'nın karanlık sokaklarında kanunsuz bahisler dönüyor. The Wolf adlı gladyatör bu çukurların mutlak hükümdarı olarak nam saldı.",
                originEventId = null,
                associatedNpcIds = listOf("boss_wolf_secundus"),
                associatedMysteryId = "mystery_silver_coin",
                status = StoryThreadStatus.ACTIVE,
                startDay = 10,
                lastActivityDay = 12,
                urgency = 6,
                currentStageIndex = 0,
                stages = listOf(
                    StoryStage(
                        stageIndex = 0,
                        title = "Karanlık Fısıltılar",
                        narrativeDescription = "Subura şarap mahzenlerinde The Wolf'un namı büyüyor. İlgilenirseniz yeraltı çukuruna inebilirsiniz.",
                        choices = listOf("Yeraltı maçını izle", "Kendi dövüşçünü çukura sür", "Muhafızlara ihbar et"),
                        requiredDay = 12
                    )
                ),
                connectedThreadIds = mutableListOf("mystery_silver_coin")
            )
        )
    }

    fun createInitialCharacterMemories(): MutableMap<String, CharacterMemory> {
        val memories = mutableMapOf<String, CharacterMemory>()

        // Senator Cassius
        memories["npc_cassius"] = CharacterMemory(
            npcId = "npc_cassius",
            npcName = "Senatör Marcus Cassius Longinus",
            trust = 45,
            respect = 55,
            fear = 15,
            hatred = 10,
            gratitude = 10,
            debtOwed = 0,
            obligationScore = 15,
            envy = 5,
            admiration = 35,
            suspicion = 25,
            currentAgenda = NpcAgenda.INCREASE_POLITICAL_POWER,
            currentGoal = "Senato Konsüllük seçimlerinde Capua oylarını toplamak",
            personalSecrets = mutableListOf(
                CharacterSecret("sec_cassius_1", "Rüşvetli Vergi Tahsildarlığı", "Campania tahıl ambarlarından gizlice hisse alıyor.", 6, false)
            )
        )

        // Cassian
        memories["fighter_cassian"] = CharacterMemory(
            npcId = "fighter_cassian",
            npcName = "Cassian 'Blood Drinker'",
            trust = 10,
            respect = 40,
            fear = 10,
            hatred = 75,
            gratitude = 0,
            debtOwed = 0,
            obligationScore = 0,
            envy = 50,
            admiration = 20,
            suspicion = 60,
            currentAgenda = NpcAgenda.SURPASS_PLAYER,
            currentGoal = "Titus'u arenanın ortasında herkesin gözü önünde devirmek",
            personalSecrets = mutableListOf(
                CharacterSecret("sec_cassian_1", "Eski Kölelik İsyanı Bağı", "Gençliğinde Spartacus sempatizanı bir köle birliğinde bulunmuştu.", 8, false)
            )
        )

        // The Wolf (Secundus)
        memories["boss_wolf_secundus"] = CharacterMemory(
            npcId = "boss_wolf_secundus",
            npcName = "Secundus 'The Wolf'",
            trust = 20,
            respect = 30,
            fear = 5,
            hatred = 35,
            gratitude = 0,
            debtOwed = 0,
            obligationScore = 0,
            envy = 10,
            admiration = 30,
            suspicion = 45,
            currentAgenda = NpcAgenda.CONTROL_UNDERGROUND_GAMBLING,
            currentGoal = "Capua yeraltı bahis gelirlerinin %30'unu tekeline almak",
            personalSecrets = mutableListOf(
                CharacterSecret("sec_wolf_1", "Kaçak Asker Kimliği", "Eski bir Illyria yardımcı lejyon firarisi.", 7, false)
            )
        )

        // Blacksmith Servius
        memories["merch_servius"] = CharacterMemory(
            npcId = "merch_servius",
            npcName = "Demirci Servius",
            trust = 75,
            respect = 70,
            fear = 10,
            hatred = 5,
            gratitude = 25,
            debtOwed = 0,
            obligationScore = 10,
            envy = 10,
            admiration = 50,
            suspicion = 10,
            currentAgenda = NpcAgenda.ACQUIRE_WEALTH,
            currentGoal = "Capua'nın en büyük dökümhanesini kurmak",
            personalSecrets = mutableListOf()
        )

        return memories
    }

    fun createInitialDelayedConsequences(): MutableList<DelayedConsequence> {
        return mutableListOf(
            DelayedConsequence(
                id = "delayed_cassius_patronage",
                triggerAction = "Hamilik Anlaşması İmzalandı",
                createdDay = 8,
                maturityDay = 18,
                targetEntityId = "npc_cassius",
                narrativeClue = "Senatör Cassius'un kâtibi bir sonraki Senato oturumundan sonra özel bir taleple geleceğini ima etti.",
                consequenceEffectType = ConsequenceType.ALLY_BOON,
                effectMagnitude = 1000,
                hasTriggered = false,
                resolutionMessage = "Senatör Cassius sözünü tuttu: Senato bütçesinden ludusunuza 1,000 Denarii özel gladyatör teşvik primi aktarıldı!"
            )
        )
    }
}
