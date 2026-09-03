package com.example.simulation

import com.example.model.*
import kotlin.math.max
import kotlin.random.Random

/**
 * The Story Director: The central observer and emergent narrative engine.
 * Monitors the world simulation and produces believable, grounded, and interconnected events.
 * Follows the core directive:
 * "NORMALITY -> CURIOSITY -> DISCOVERY -> SURPRISE -> CONSEQUENCE -> MEMORY -> ANTICIPATION"
 */
object StoryDirector {

    data class StorySimulationOutput(
        val updatedWorldMemory: List<MemoryEntry>,
        val updatedRumors: List<Rumor>,
        val updatedMysteries: List<WorldMystery>,
        val updatedStoryThreads: List<StoryThread>,
        val updatedCharacterMemories: Map<String, CharacterMemory>,
        val updatedDelayedConsequences: List<DelayedConsequence>,
        val dispatchedNarratives: List<String>,
        val chronicleEntries: List<ChronicleEntry>,
        val tomorrowPreviews: List<TomorrowPreviewItem>,
        val treasuryDelta: Int,
        val prestigeDelta: Int
    )

    data class MysteryInvestigationResult(
        val success: Boolean,
        val message: String,
        val discoveredEvidence: EvidencePiece?,
        val resolvedMystery: WorldMystery?,
        val goldReward: Int = 0,
        val prestigeReward: Int = 0
    )

    data class RumorInvestigationResult(
        val success: Boolean,
        val message: String,
        val revealedTruth: String,
        val isConfirmedTrue: Boolean
    )

    /**
     * Primary daily simulation cycle for the story engine.
     */
    fun simulateStoryDay(
        currentDay: Int,
        worldSeed: Long,
        state: LudusUiState
    ): StorySimulationOutput {
        val deterministicRng = Random(worldSeed + currentDay * 4391L)

        val memoryList = state.worldMemory.toMutableList()
        val rumorsList = state.rumors.toMutableList()
        val mysteriesList = state.mysteries.toMutableList()
        val threadsList = state.storyThreads.toMutableList()
        val charMemories = state.characterMemories.toMutableMap()
        val consequences = state.delayedConsequences.toMutableList()

        val dispatchedNarratives = mutableListOf<String>()
        val chronicleEntries = mutableListOf<ChronicleEntry>()
        var treasuryDelta = 0
        var prestigeDelta = 0

        // =========================================================
        // 1. RUMOR PROPAGATION, DISTORTION & AGING
        // =========================================================
        val activeRumors = rumorsList.map { rumor ->
            if (rumor.isExpired) return@map rumor

            val age = currentDay - rumor.createdDay
            if (age >= 7) {
                // Expire rumors older than 7 days
                rumor.copy(isExpired = true)
            } else {
                val newSpread = rumor.spreadCount + 1
                // Distort unverified rumors as they travel
                val shouldDistort = newSpread >= 3 && rumor.truthStatus != RumorTruthStatus.TRUE && rumor.distortionLevel == 0
                val newDistortion = if (shouldDistort) rumor.distortionLevel + 1 else rumor.distortionLevel
                val newHeadline = if (shouldDistort) {
                    when {
                        rumor.headline.contains("Sakatlandı") -> "Pompeii Şampiyonunun Bacağı Kangren Oldu!"
                        rumor.headline.contains("Vergi") -> "Senato Bütün Bağımsız Kışlalara El Koyacak!"
                        else -> "${rumor.headline} (Şehirde Yayılıyor)"
                    }
                } else rumor.headline

                rumor.copy(
                    spreadCount = newSpread,
                    distortionLevel = newDistortion,
                    headline = newHeadline
                )
            }
        }.toMutableList()

        // Spawn occasional new grounded rumors (if active pool < 5)
        val livingRumorCount = activeRumors.count { !it.isExpired }
        if (livingRumorCount < 4 && deterministicRng.nextFloat() < 0.35f) {
            val generatedRumor = generateProceduralRumor(currentDay, deterministicRng, state)
            if (generatedRumor != null) {
                activeRumors.add(0, generatedRumor)
                dispatchedNarratives.add("Fısıltı: ${generatedRumor.headline}")
            }
        }

        // =========================================================
        // 2. DELAYED CONSEQUENCES MATURITY
        // =========================================================
        consequences.forEachIndexed { index, dc ->
            if (!dc.hasTriggered && currentDay >= dc.maturityDay) {
                consequences[index] = dc.copy(hasTriggered = true)

                when (dc.consequenceEffectType) {
                    ConsequenceType.ALLY_BOON -> {
                        treasuryDelta += dc.effectMagnitude
                        chronicleEntries.add(
                            ChronicleEntry(state.dominus.yearAUC, "Müttefik Desteği", dc.resolutionMessage, true)
                        )
                    }
                    ConsequenceType.TREASURY_FINE -> {
                        treasuryDelta -= dc.effectMagnitude
                        chronicleEntries.add(
                            ChronicleEntry(state.dominus.yearAUC, "Ceza Tebligatı", dc.resolutionMessage, false)
                        )
                    }
                    ConsequenceType.PRESTIGE_PENALTY -> {
                        prestigeDelta -= dc.effectMagnitude
                        chronicleEntries.add(
                            ChronicleEntry(state.dominus.yearAUC, "İtibar Kaybı", dc.resolutionMessage, false)
                        )
                    }
                    else -> {
                        chronicleEntries.add(
                            ChronicleEntry(state.dominus.yearAUC, "Gecikmeli Gelişme", dc.resolutionMessage, false)
                        )
                    }
                }
                dispatchedNarratives.add("Gecikmeli Hadise: ${dc.resolutionMessage}")
            }
        }

        // =========================================================
        // 3. STORY THREADS & MYSTERIES PROGRESSION
        // =========================================================
        threadsList.forEachIndexed { idx, thread ->
            if (thread.status == StoryThreadStatus.ACTIVE) {
                // Check if next stage day has arrived
                val nextStage = thread.stages.getOrNull(thread.currentStageIndex + 1)
                if (nextStage != null && currentDay >= nextStage.requiredDay) {
                    threadsList[idx] = thread.copy(
                        currentStageIndex = thread.currentStageIndex + 1,
                        lastActivityDay = currentDay,
                        status = if (thread.currentStageIndex + 1 >= thread.stages.size - 1) StoryThreadStatus.ESCALATING else StoryThreadStatus.ACTIVE
                    )
                    dispatchedNarratives.add("Hikâye Gelişmesi: ${thread.title} - ${nextStage.title}")
                }
            }
        }

        // =========================================================
        // 4. "TOMORROW'S HORIZON" CURIOSITY PREVIEWS
        // =========================================================
        val tomorrowDay = currentDay + 1
        val previews = generateTomorrowPreviews(tomorrowDay, state, activeRumors, consequences)

        return StorySimulationOutput(
            updatedWorldMemory = memoryList,
            updatedRumors = activeRumors,
            updatedMysteries = mysteriesList,
            updatedStoryThreads = threadsList,
            updatedCharacterMemories = charMemories,
            updatedDelayedConsequences = consequences,
            dispatchedNarratives = dispatchedNarratives,
            chronicleEntries = chronicleEntries,
            tomorrowPreviews = previews,
            treasuryDelta = treasuryDelta,
            prestigeDelta = prestigeDelta
        )
    }

    /**
     * Generates 3-5 preview teasers for tomorrow's events fostering curiosity and anticipation.
     */
    private fun generateTomorrowPreviews(
        tomorrowDay: Int,
        state: LudusUiState,
        activeRumors: List<Rumor>,
        consequences: List<DelayedConsequence>
    ): List<TomorrowPreviewItem> {
        val previews = mutableListOf<TomorrowPreviewItem>()

        // 1. Official Calendar Bout check
        val tomorrowBout = state.arenaCalendar.find { it.day == tomorrowDay && it.isPlayerMatch && !it.isCompleted }
        if (tomorrowBout != null) {
            previews.add(
                TomorrowPreviewItem(
                    iconSymbol = "⚔",
                    categoryTag = "Resmi Müsabaka",
                    headline = "${tomorrowBout.fighter1Name} ile Randevu",
                    teaserText = "${tomorrowBout.venueId.venueName}'nda resmi unvan/derece müsabakası.",
                    isRevealed = true
                )
            )
        } else {
            val npcBout = state.arenaCalendar.find { it.day == tomorrowDay && !it.isPlayerMatch && !it.isCompleted }
            if (npcBout != null) {
                previews.add(
                    TomorrowPreviewItem(
                        iconSymbol = "⚔",
                        categoryTag = "Arena Programı",
                        headline = "${npcBout.fighter1Name} vs ${npcBout.fighter2Name}",
                        teaserText = "${npcBout.venueId.venueName} amfitiyatrosunda büyük seyirci kitlesi bekleniyor.",
                        isRevealed = true
                    )
                )
            }
        }

        // 2. Pending consequence check
        val pendingConsequence = consequences.find { !it.hasTriggered && it.maturityDay == tomorrowDay }
        if (pendingConsequence != null) {
            previews.add(
                TomorrowPreviewItem(
                    iconSymbol = "⏳",
                    categoryTag = "Gecikmeli Yankı",
                    headline = "Eski Bir Kararın Sonucu",
                    teaserText = pendingConsequence.narrativeClue,
                    isRevealed = false
                )
            )
        }

        // 3. Senate & Politics check
        val senateSession = state.politicalCalendar.find { it.day == tomorrowDay }
        if (senateSession != null) {
            previews.add(
                TomorrowPreviewItem(
                    iconSymbol = "🏛",
                    categoryTag = "Curia Meclisi",
                    headline = senateSession.title,
                    teaserText = senateSession.description,
                    isRevealed = true
                )
            )
        }

        // 4. Forum / Market Caravan check
        if (tomorrowDay % 4 == 0) {
            previews.add(
                TomorrowPreviewItem(
                    iconSymbol = "💰",
                    categoryTag = "Pazar & Lojistik",
                    headline = "Doğu Ticaret Kervanı Limana Yanaşıyor",
                    teaserText = "Iberia ve Şam çeliğinden yeni silah sevkiyatı Foruma ulaşacak.",
                    isRevealed = true
                )
            )
        }

        // 5. Underground & Mysterious whisper
        val activeRumor = activeRumors.find { !it.isExpired && !it.isInvestigated }
        if (activeRumor != null && previews.size < 4) {
            previews.add(
                TomorrowPreviewItem(
                    iconSymbol = "❓",
                    categoryTag = "Şehir Fısıltısı",
                    headline = activeRumor.headline,
                    teaserText = "${activeRumor.location} civarında dikkat çekici gelişmeler var.",
                    isRevealed = false
                )
            )
        }

        return previews.take(4)
    }

    /**
     * Executes player investigation on a mystery path.
     */
    fun investigateMystery(
        mystery: WorldMystery,
        path: InvestigationPath,
        state: LudusUiState
    ): MysteryInvestigationResult {
        if (state.dominus.denarii < path.costDenarii) {
            return MysteryInvestigationResult(
                success = false,
                message = "Yetersiz Denarii! Soruşturma için ${path.costDenarii} Denarii gerekli.",
                discoveredEvidence = null,
                resolvedMystery = null
            )
        }

        val updatedPaths = mystery.investigationPaths.map {
            if (it.id == path.id) it.copy(isExecuted = true) else it
        }

        // Determine discovered evidence based on path
        val evidence = when (path.id) {
            "path_search_dorm" -> {
                EvidencePiece(
                    id = "ev_dorm_scratch",
                    title = "Gizli Zemin Çentiği",
                    description = "Yatağın altındaki tahtada Domus Auctor armasına benzeyen bir yılan simgesi kazınmış.",
                    discoveredDay = state.dominus.dayNumber,
                    source = "Yatak Altı",
                    epistemicStatus = EpistemicStatus.CONFIRMED
                )
            }
            "path_bribe_guard" -> {
                EvidencePiece(
                    id = "ev_guard_confession",
                    title = "Nöbetçinin İtirafı",
                    description = "Gece nöbetçisi ağlayarak konuştu: 'Domus Auctor'un lanistası Decimus bana 100 gümüş sikke verdi ve arka kapıyı aralık bırakmamı söyledi!'",
                    discoveredDay = state.dominus.dayNumber,
                    source = "Rüşvet Verilen Nöbetçi",
                    epistemicStatus = EpistemicStatus.CONFIRMED
                )
            }
            "path_scout_subura" -> {
                EvidencePiece(
                    id = "ev_auctor_witness",
                    title = "Meyhane Görgü Tanığı",
                    description = "Subura meyhanecisi, Domus Auctor muhafızlarının kukuletalı bir gladyatörü arabaya bindirip götürdüğünü gördüğünü doğruladı.",
                    discoveredDay = state.dominus.dayNumber,
                    source = "Subura Gözcüsü",
                    epistemicStatus = EpistemicStatus.CONFIRMED
                )
            }
            "path_ask_blacksmith" -> {
                EvidencePiece(
                    id = "ev_coin_metal",
                    title = "Servius'un Maden Raporu",
                    description = "Demirci Servius sikkeyi asitle test etti: 'Bu saf Neapolis korsan gümüşü. Limandaki Kaçakçı Barba'nın gizli mührünü taşıyor.'",
                    discoveredDay = state.dominus.dayNumber,
                    source = "Demirci Servius",
                    epistemicStatus = EpistemicStatus.CONFIRMED
                )
            }
            "path_underground_lead" -> {
                EvidencePiece(
                    id = "ev_smuggler_pass",
                    title = "Kaçakçı Şifresi",
                    description = "Kaçakçı Lucius sikkeyi görünce sırıttı: 'Bu sikke seni Neapolis yeraltı hangarına sokar. Orada kan dökmek için dövüşçü arıyorlar.'",
                    discoveredDay = state.dominus.dayNumber,
                    source = "Kaçakçı Lucius",
                    epistemicStatus = EpistemicStatus.CONFIRMED
                )
            }
            else -> {
                EvidencePiece(
                    id = "ev_generic_${System.currentTimeMillis()}",
                    title = "Kritik Belge",
                    description = "Olayın perde arkasındaki gerçekleri aydınlatan önemli bir ipucu ele geçirildi.",
                    discoveredDay = state.dominus.dayNumber,
                    source = "Soruşturma",
                    epistemicStatus = EpistemicStatus.CONFIRMED
                )
            }
        }

        val updatedEvidence = mystery.discoveredEvidence.toMutableList()
        if (updatedEvidence.none { it.id == evidence.id }) {
            updatedEvidence.add(evidence)
        }

        // Check if mystery is resolved (2+ evidence pieces discovered or confession obtained)
        val isResolved = updatedEvidence.any { it.id == "ev_guard_confession" || it.id == "ev_smuggler_pass" } || updatedEvidence.size >= 2
        val resolutionSummary = if (isResolved) {
            when (mystery.id) {
                "mystery_empty_bed" -> "Soruşturma tamamlandı! Gladyatörün Domus Auctor lanistası Decimus tarafından rüşvetle kaçırıldığı kanıtlandı. Şehir yargıcına şikayet hakkı ve tazminat kazanıldı."
                "mystery_silver_coin" -> "Gizem çözüldü! Sikke Neapolis yeraltı kaçakçı birliğinin gizli giriş jetonudur. Yeraltı dövüş çukurları ve kaçak silah ticareti açığa çıkarıldı."
                else -> "Tüm deliller birleşti ve gizem aydınlatıldı."
            }
        } else null

        val updatedMystery = mystery.copy(
            discoveredEvidence = updatedEvidence,
            investigationPaths = updatedPaths,
            status = if (isResolved) MysteryStatus.RESOLVED else MysteryStatus.INVESTIGATING,
            resolutionSummary = resolutionSummary
        )

        return MysteryInvestigationResult(
            success = true,
            message = "Soruşturma yapıldı: '${evidence.title}' delili dosyaya eklendi!",
            discoveredEvidence = evidence,
            resolvedMystery = updatedMystery,
            goldReward = if (isResolved) 500 else 0,
            prestigeReward = if (isResolved) 150 else 0
        )
    }

    /**
     * Executes player investigation on a rumor.
     */
    fun investigateRumor(rumor: Rumor, state: LudusUiState): RumorInvestigationResult {
        if (state.dominus.denarii < rumor.investigationCostDenarii) {
            return RumorInvestigationResult(
                success = false,
                message = "Yetersiz Denarii! Muhbire ${rumor.investigationCostDenarii} Denarii ödenmesi gerekiyor.",
                revealedTruth = "",
                isConfirmedTrue = false
            )
        }

        val isConfirmedTrue = rumor.truthStatus == RumorTruthStatus.TRUE || rumor.truthStatus == RumorTruthStatus.PARTIALLY_TRUE
        val revealedTruth = when (rumor.truthStatus) {
            RumorTruthStatus.TRUE -> "Gözcüler teyit etti: Söylenti KESİNLİKLE DOĞRU! ${rumor.investigationLead ?: ""}"
            RumorTruthStatus.PARTIALLY_TRUE -> "Tahkikat sonucu: Söylenti KISMEN DOĞRU. Olay var ancak dedikodulardaki kadar abartılı değil. ${rumor.investigationLead ?: ""}"
            RumorTruthStatus.FALSE -> "Tahkikat sonucu: Söylenti TAMAMEN ASILSIZ! Kasıtlı veya uydurma bir dedikodu."
            RumorTruthStatus.MISLEADING -> "DİKKAT: Bu söylenti rakipler tarafından sizi yanıltmak ve tuzağa çekmek için uydurulmuş!"
            RumorTruthStatus.UNKNOWN -> "Yeterli delil bulunamadı; şüphe devam ediyor."
        }

        return RumorInvestigationResult(
            success = true,
            message = "Söylenti tahkik edildi.",
            revealedTruth = revealedTruth,
            isConfirmedTrue = isConfirmedTrue
        )
    }

    /**
     * Generates a contextually appropriate procedural rumor based on world state.
     */
    private fun generateProceduralRumor(day: Int, rng: Random, state: LudusUiState): Rumor? {
        val candidates = listOf(
            Rumor(
                id = "rumor_gen_weapon_${day}",
                source = RumorSource.MERCHANT,
                targetId = null,
                subject = "Iberia Çeliği",
                headline = "Liman Muhafızları Bir Sandık Toledo Kılıcına El Koydu",
                fullGossipText = "Gümrük muhafızlarının Neapolis rıhtımında kaçak sokulmak istenen nadir Falcata kılıçlarını zapt ettiği söyleniyor.",
                truthStatus = RumorTruthStatus.TRUE,
                reliability = 0.80f,
                createdDay = day,
                location = "Neapolis Limanı",
                investigationCostDenarii = 120,
                investigationLead = "Silahlar yakında Forum müzayedesinde satışa çıkabilir."
            ),
            Rumor(
                id = "rumor_gen_rival_${day}",
                source = RumorSource.RIVAL_LUDUS,
                targetId = "fighter_cassian",
                subject = "Domus Auctor",
                headline = "Domus Auctor Kışlasında Gladyatör İsyanı",
                fullGossipText = "Yetersiz erzak yüzünden rakip kışladaki acemi dövüşçülerin isyan çıkardığı ve bir eğitmeni yaraladığı dedikodusu dolanıyor.",
                truthStatus = RumorTruthStatus.PARTIALLY_TRUE,
                reliability = 0.50f,
                createdDay = day,
                location = "Capua Banliyösü",
                investigationCostDenarii = 150,
                investigationLead = "Gerginlik var fakat bastırıldı; rakip lanista dövüşçülerini sert cezalandırdı."
            ),
            Rumor(
                id = "rumor_gen_underground_${day}",
                source = RumorSource.UNDERGROUND_ORGANIZER,
                targetId = null,
                subject = "Gizli Bahis",
                headline = "Senatör Vekilleri Gece Taş Ocaklarında Gizli Bahis Oynuyor",
                fullGossipText = "Taş ocaklarındaki gece kan çukurunda patrician soylularının kölelerinin üzerine on binlerce Denarii yatırdığı konuşuluyor.",
                truthStatus = RumorTruthStatus.TRUE,
                reliability = 0.70f,
                createdDay = day,
                location = "Capua Taş Ocakları",
                investigationCostDenarii = 200,
                investigationLead = "Gece devriyesi yüzbaşısı Varo rüşvet alarak alanı koruyor."
            )
        )

        return candidates.shuffled(rng).firstOrNull()
    }
}
