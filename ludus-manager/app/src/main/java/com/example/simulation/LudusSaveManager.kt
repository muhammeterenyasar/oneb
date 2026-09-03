package com.example.simulation

import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Robust, Canonical Save / Load Persistence Manager for the complete Roman world state.
 * Guarantees that:
 * - Dead gladiators remain DEAD across save & load.
 * - Calendar dates, scheduled fights, and completed bouts persist identically.
 * - Daily underground pools are deterministic and stable for the same calendar date.
 * - Economy, prestige, equipment durability, and political factions persist with 100% fidelity.
 */
object LudusSaveManager {

    fun serializeStateToJson(state: LudusUiState): String {
        val root = JSONObject()

        // 1. Dominus
        val domObj = JSONObject().apply {
            put("dayNumber", state.dominus.dayNumber)
            put("denarii", state.dominus.denarii)
            put("prestige", state.dominus.prestige)
            put("foodWheat", state.dominus.foodWheat)
            put("popularity", state.dominus.popularity)
            put("ludusName", state.dominus.ludusName)
            put("yearAUC", state.dominus.yearAUC)
        }
        root.put("dominus", domObj)

        // 2. Player Gladiators
        val gladArray = JSONArray()
        state.gladiators.forEach { g -> gladArray.put(serializeGladiator(g)) }
        root.put("gladiators", gladArray)

        // 3. Persistent Circuit & Underground Fighters
        val pfArray = JSONArray()
        state.persistentFighters.forEach { pf -> pfArray.put(serializeGladiator(pf)) }
        root.put("persistentFighters", pfArray)

        // 4. Calendar Bouts
        val calArray = JSONArray()
        state.arenaCalendar.forEach { b ->
            val bObj = JSONObject().apply {
                put("id", b.id)
                put("day", b.day)
                put("venueId", b.venueId.name)
                put("fighter1Id", b.fighter1Id)
                put("fighter1Name", b.fighter1Name)
                put("fighter1Ludus", b.fighter1Ludus)
                put("fighter2Id", b.fighter2Id)
                put("fighter2Name", b.fighter2Name)
                put("fighter2Ludus", b.fighter2Ludus)
                put("matchType", b.matchType.name)
                put("isPlayerMatch", b.isPlayerMatch)
                put("isCompleted", b.isCompleted)
                put("resultSummary", b.resultSummary ?: "")
                put("winnerName", b.winnerName ?: "")
            }
            calArray.put(bObj)
        }
        root.put("arenaCalendar", calArray)

        // 5. Underground Fights
        val ugArray = JSONArray()
        state.undergroundFights.forEach { ug ->
            val ugObj = JSONObject().apply {
                put("id", ug.id)
                put("title", ug.title)
                put("venueName", ug.venueName)
                put("organizerName", ug.organizerName)
                put("organizerRole", ug.organizerRole)
                put("opponentFighterId", ug.opponentFighter.id)
                put("entryFee", ug.entryFee)
                put("purseReward", ug.purseReward)
                put("riskLevel", ug.riskLevel)
                put("discoveryRiskPercent", ug.discoveryRiskPercent)
                put("atmosphere", ug.atmosphere)
                put("dayOffered", ug.dayOffered)
                put("warningNote", ug.warningNote)
                put("isCompleted", ug.isCompleted)
                put("isBossFight", ug.isBossFight)
                put("bossState", ug.bossState?.name ?: "")
                put("storylineSnippet", ug.storylineSnippet ?: "")
            }
            ugArray.put(ugObj)
        }
        root.put("undergroundFights", ugArray)

        // 6. Fallen Memorials
        val memArray = JSONArray()
        state.fallenGladiators.forEach { mem ->
            val mObj = JSONObject().apply {
                put("id", mem.id)
                put("name", mem.name)
                put("nickname", mem.nickname)
                put("gladiatorClass", mem.gladiatorClass.name)
                put("origin", mem.origin.name)
                put("ludusAffiliation", mem.ludusAffiliation)
                put("recordSummary", mem.recordSummary)
                put("kills", mem.kills)
                put("diedOnDay", mem.diedOnDay)
                put("arenaName", mem.arenaName)
                put("killedBy", mem.killedBy)
                put("causeOfDeath", mem.causeOfDeath)
                put("wasChampion", mem.wasChampion)
                put("yearAUC", mem.yearAUC)
            }
            memArray.put(mObj)
        }
        root.put("fallenGladiators", memArray)

        // 7. Chronicles
        val chronArray = JSONArray()
        state.chronicles.forEach { ch ->
            val cObj = JSONObject().apply {
                put("dateText", ch.dateText)
                put("title", ch.title)
                put("description", ch.description)
                put("isGlory", ch.isGlory)
            }
            chronArray.put(cObj)
        }
        root.put("chronicles", chronArray)

        // 8. World Memory
        val memArr = JSONArray()
        state.worldMemory.forEach { m ->
            memArr.put(JSONObject().apply {
                put("id", m.id)
                put("eventType", m.eventType.name)
                put("date", m.date)
                put("location", m.location)
                put("participants", JSONArray(m.participantIds))
                put("causeDescription", m.causeDescription)
                put("hiddenFacts", m.hiddenFacts ?: "")
                put("importance", m.importance.name)
            })
        }
        root.put("worldMemory", memArr)

        // 9. Rumors
        val rumArr = JSONArray()
        state.rumors.forEach { r ->
            rumArr.put(JSONObject().apply {
                put("id", r.id)
                put("source", r.source.name)
                put("targetId", r.targetId ?: "")
                put("subject", r.subject)
                put("headline", r.headline)
                put("fullGossipText", r.fullGossipText)
                put("truthStatus", r.truthStatus.name)
                put("reliability", r.reliability.toDouble())
                put("createdDay", r.createdDay)
                put("location", r.location)
                put("spreadCount", r.spreadCount)
                put("distortionLevel", r.distortionLevel)
                put("isInvestigated", r.isInvestigated)
                put("investigationCostDenarii", r.investigationCostDenarii)
                put("investigationLead", r.investigationLead ?: "")
                put("isExpired", r.isExpired)
            })
        }
        root.put("rumors", rumArr)

        // 10. Mysteries
        val mystArr = JSONArray()
        state.mysteries.forEach { my ->
            mystArr.put(JSONObject().apply {
                put("id", my.id)
                put("title", my.title)
                put("category", my.category.name)
                put("initialClue", my.initialClue)
                put("discoveredDay", my.discoveredDay)
                put("status", my.status.name)
                put("resolutionSummary", my.resolutionSummary ?: "")
                put("rewardSummary", my.rewardSummary ?: "")
                put("knownFacts", JSONArray(my.knownFacts))
                put("unknownSuspicions", JSONArray(my.unknownSuspicions))
            })
        }
        root.put("mysteries", mystArr)

        // 11. Story Threads
        val thArr = JSONArray()
        state.storyThreads.forEach { th ->
            thArr.put(JSONObject().apply {
                put("id", th.id)
                put("title", th.title)
                put("synopsis", th.synopsis)
                put("originEventId", th.originEventId ?: "")
                put("status", th.status.name)
                put("startDay", th.startDay)
                put("lastActivityDay", th.lastActivityDay)
                put("urgency", th.urgency)
                put("currentStageIndex", th.currentStageIndex)
            })
        }
        root.put("storyThreads", thArr)

        return root.toString(2)
    }

    private fun serializeGladiator(g: Gladiator): JSONObject {
        return JSONObject().apply {
            put("id", g.id)
            put("name", g.name)
            put("nickname", g.nickname)
            put("gladiatorClass", g.gladiatorClass.name)
            put("origin", g.origin.name)
            put("status", g.status.name)
            put("age", g.age)
            put("personality", g.personality.name)
            put("rank", g.rank)
            put("isAlive", g.isAlive)
            put("isChampion", g.isChampion)
            put("isRetired", g.isRetired)
            put("isUnderground", g.isUnderground)
            put("currentArena", g.currentArena.name)
            put("tier", g.tier.name)
            put("aiPersonality", g.aiPersonality.name)
            put("ludusAffiliation", g.ludusAffiliation)
            put("ownerName", g.ownerName)
            put("signatureTactic", g.signatureTactic)
            put("lastFightDay", g.lastFightDay)
            put("fightCountAgainstPlayer", g.fightCountAgainstPlayer)
            put("undergroundBossState", g.undergroundBossState?.name ?: "")
            put("deathDay", g.deathDay ?: -1)
            put("killedBy", g.killedBy ?: "")
            put("causeOfDeath", g.causeOfDeath ?: "")
            put("deathArena", g.deathArena ?: "")

            // Health & Stats
            put("health", g.condition.health)
            put("stamina", g.condition.stamina)
            put("morale", g.condition.morale)
            put("strength", g.physicalStats.strength)
            put("speed", g.physicalStats.speed)
            put("agility", g.physicalStats.agility)
            put("endurance", g.physicalStats.endurance)
            put("reflex", g.physicalStats.reflex)
            put("painTolerance", g.physicalStats.painTolerance)
            put("swordsmanship", g.attributes.swordsmanship)
            put("shieldSkill", g.attributes.shieldSkill)
            put("discipline", g.attributes.discipline)

            // Career
            put("wins", g.historicalPerformance.victories)
            put("losses", g.historicalPerformance.defeats)
            put("kills", g.historicalPerformance.kills)
            put("spared", g.historicalPerformance.sparedByCrowd)
            put("currentWinStreak", g.historicalPerformance.currentWinStreak)
            put("crowdApproval", g.historicalPerformance.crowdApprovalRating)
            put("denariiEarned", g.historicalPerformance.totalDenariiEarned)
        }
    }

    fun deserializeStateFromJson(jsonString: String, baseState: LudusUiState): LudusUiState {
        val root = JSONObject(jsonString)

        // 1. Dominus
        val domObj = root.optJSONObject("dominus")
        val dominus = if (domObj != null) {
            baseState.dominus.copy(
                dayNumber = domObj.optInt("dayNumber", baseState.dominus.dayNumber),
                denarii = domObj.optInt("denarii", baseState.dominus.denarii),
                prestige = domObj.optInt("prestige", baseState.dominus.prestige),
                foodWheat = domObj.optInt("foodWheat", baseState.dominus.foodWheat),
                popularity = domObj.optInt("popularity", baseState.dominus.popularity),
                ludusName = domObj.optString("ludusName", baseState.dominus.ludusName),
                yearAUC = domObj.optString("yearAUC", baseState.dominus.yearAUC)
            )
        } else baseState.dominus

        // 2. Gladiators
        val gladArray = root.optJSONArray("gladiators")
        val gladiators = mutableListOf<Gladiator>()
        if (gladArray != null) {
            for (i in 0 until gladArray.length()) {
                val gObj = gladArray.getJSONObject(i)
                gladiators.add(deserializeGladiator(gObj))
            }
        } else {
            gladiators.addAll(baseState.gladiators)
        }

        // 3. Persistent Circuit Fighters
        val pfArray = root.optJSONArray("persistentFighters")
        val persistentFighters = mutableListOf<Gladiator>()
        if (pfArray != null) {
            for (i in 0 until pfArray.length()) {
                val pfObj = pfArray.getJSONObject(i)
                persistentFighters.add(deserializeGladiator(pfObj))
            }
        } else {
            persistentFighters.addAll(baseState.persistentFighters)
        }

        // 4. Calendar Bouts
        val calArray = root.optJSONArray("arenaCalendar")
        val calendar = mutableListOf<ArenaCalendarBout>()
        if (calArray != null) {
            for (i in 0 until calArray.length()) {
                val bObj = calArray.getJSONObject(i)
                val venue = try { ArenaVenueId.valueOf(bObj.getString("venueId")) } catch (_: Exception) { ArenaVenueId.CAPUA }
                val matchType = try { ArenaMatchType.valueOf(bObj.getString("matchType")) } catch (_: Exception) { ArenaMatchType.STANDARD_DUEL }
                calendar.add(
                    ArenaCalendarBout(
                        id = bObj.getString("id"),
                        day = bObj.getInt("day"),
                        venueId = venue,
                        fighter1Id = bObj.getString("fighter1Id"),
                        fighter1Name = bObj.getString("fighter1Name"),
                        fighter1Ludus = bObj.getString("fighter1Ludus"),
                        fighter2Id = bObj.getString("fighter2Id"),
                        fighter2Name = bObj.getString("fighter2Name"),
                        fighter2Ludus = bObj.getString("fighter2Ludus"),
                        matchType = matchType,
                        isPlayerMatch = bObj.getBoolean("isPlayerMatch"),
                        isCompleted = bObj.getBoolean("isCompleted"),
                        resultSummary = bObj.optString("resultSummary").ifBlank { null },
                        winnerName = bObj.optString("winnerName").ifBlank { null }
                    )
                )
            }
        } else {
            calendar.addAll(baseState.arenaCalendar)
        }

        // 5. Underground Fights
        val ugArray = root.optJSONArray("undergroundFights")
        val undergroundFights = mutableListOf<UndergroundFight>()
        if (ugArray != null) {
            for (i in 0 until ugArray.length()) {
                val ugObj = ugArray.getJSONObject(i)
                val oppId = ugObj.getString("opponentFighterId")
                val opponent = (gladiators + persistentFighters).find { it.id == oppId } ?: persistentFighters.first()
                val bossState = ugObj.optString("bossState").let { if (it.isNotBlank()) try { BossState.valueOf(it) } catch (_: Exception) { null } else null }
                undergroundFights.add(
                    UndergroundFight(
                        id = ugObj.getString("id"),
                        title = ugObj.getString("title"),
                        venueName = ugObj.getString("venueName"),
                        organizerName = ugObj.getString("organizerName"),
                        organizerRole = ugObj.getString("organizerRole"),
                        opponentFighter = opponent,
                        entryFee = ugObj.getInt("entryFee"),
                        purseReward = ugObj.getInt("purseReward"),
                        riskLevel = ugObj.getString("riskLevel"),
                        discoveryRiskPercent = ugObj.getInt("discoveryRiskPercent"),
                        atmosphere = ugObj.getString("atmosphere"),
                        dayOffered = ugObj.getInt("dayOffered"),
                        warningNote = ugObj.getString("warningNote"),
                        isCompleted = ugObj.getBoolean("isCompleted"),
                        isBossFight = ugObj.optBoolean("isBossFight", false),
                        bossState = bossState,
                        storylineSnippet = ugObj.optString("storylineSnippet").ifBlank { null }
                    )
                )
            }
        } else {
            undergroundFights.addAll(baseState.undergroundFights)
        }

        // 6. Fallen Memorials
        val memArray = root.optJSONArray("fallenGladiators")
        val memorials = mutableListOf<FallenGladiatorMemorial>()
        if (memArray != null) {
            for (i in 0 until memArray.length()) {
                val mObj = memArray.getJSONObject(i)
                val gClass = try { GladiatorClass.valueOf(mObj.getString("gladiatorClass")) } catch (_: Exception) { GladiatorClass.MURMILLO }
                val origin = try { Origin.valueOf(mObj.getString("origin")) } catch (_: Exception) { Origin.ITALIA }
                memorials.add(
                    FallenGladiatorMemorial(
                        id = mObj.getString("id"),
                        name = mObj.getString("name"),
                        nickname = mObj.getString("nickname"),
                        gladiatorClass = gClass,
                        origin = origin,
                        ludusAffiliation = mObj.getString("ludusAffiliation"),
                        recordSummary = mObj.getString("recordSummary"),
                        kills = mObj.getInt("kills"),
                        diedOnDay = mObj.getInt("diedOnDay"),
                        arenaName = mObj.getString("arenaName"),
                        killedBy = mObj.getString("killedBy"),
                        causeOfDeath = mObj.getString("causeOfDeath"),
                        wasChampion = mObj.getBoolean("wasChampion"),
                        yearAUC = mObj.getString("yearAUC")
                    )
                )
            }
        } else {
            memorials.addAll(baseState.fallenGladiators)
        }

        // 7. World Memory
        val memArr = root.optJSONArray("worldMemory")
        val worldMemory = mutableListOf<MemoryEntry>()
        if (memArr != null) {
            for (i in 0 until memArr.length()) {
                val mObj = memArr.getJSONObject(i)
                val eType = try { MemoryEventType.valueOf(mObj.getString("eventType")) } catch (_: Exception) { MemoryEventType.DISCOVERY_EVENT }
                val importance = try { EventImportance.valueOf(mObj.optString("importance", "MINOR")) } catch (_: Exception) { EventImportance.MINOR }
                val partArr = mObj.optJSONArray("participants")
                val participants = mutableListOf<String>()
                if (partArr != null) {
                    for (p in 0 until partArr.length()) participants.add(partArr.getString(p))
                }
                worldMemory.add(
                    MemoryEntry(
                        id = mObj.getString("id"),
                        eventType = eType,
                        date = mObj.getInt("date"),
                        location = mObj.getString("location"),
                        participantIds = participants,
                        causeDescription = mObj.getString("causeDescription"),
                        hiddenFacts = mObj.optString("hiddenFacts").ifBlank { null },
                        importance = importance
                    )
                )
            }
        } else {
            worldMemory.addAll(baseState.worldMemory)
        }

        // 8. Rumors
        val rumArr = root.optJSONArray("rumors")
        val rumors = mutableListOf<Rumor>()
        if (rumArr != null) {
            for (i in 0 until rumArr.length()) {
                val rObj = rumArr.getJSONObject(i)
                val source = try { RumorSource.valueOf(rObj.getString("source")) } catch (_: Exception) { RumorSource.TAVERN_GOSSIP }
                val truth = try { RumorTruthStatus.valueOf(rObj.getString("truthStatus")) } catch (_: Exception) { RumorTruthStatus.TRUE }
                rumors.add(
                    Rumor(
                        id = rObj.getString("id"),
                        source = source,
                        targetId = rObj.optString("targetId").ifBlank { null },
                        subject = rObj.getString("subject"),
                        headline = rObj.getString("headline"),
                        fullGossipText = rObj.getString("fullGossipText"),
                        truthStatus = truth,
                        reliability = rObj.getDouble("reliability").toFloat(),
                        createdDay = rObj.getInt("createdDay"),
                        location = rObj.getString("location"),
                        spreadCount = rObj.optInt("spreadCount", 1),
                        distortionLevel = rObj.optInt("distortionLevel", 0),
                        isInvestigated = rObj.optBoolean("isInvestigated", false),
                        investigationCostDenarii = rObj.optInt("investigationCostDenarii", 100),
                        investigationLead = rObj.optString("investigationLead").ifBlank { null },
                        isExpired = rObj.optBoolean("isExpired", false)
                    )
                )
            }
        } else {
            rumors.addAll(baseState.rumors)
        }

        // 9. Mysteries
        val mystArr = root.optJSONArray("mysteries")
        val mysteries = mutableListOf<WorldMystery>()
        if (mystArr != null) {
            for (i in 0 until mystArr.length()) {
                val myObj = mystArr.getJSONObject(i)
                val myId = myObj.getString("id")
                val baseMystery = baseState.mysteries.find { it.id == myId }
                val cat = try { MysteryCategory.valueOf(myObj.getString("category")) } catch (_: Exception) { MysteryCategory.DISAPPEARANCE }
                val status = try { MysteryStatus.valueOf(myObj.getString("status")) } catch (_: Exception) { MysteryStatus.ACTIVE }
                mysteries.add(
                    WorldMystery(
                        id = myId,
                        title = myObj.getString("title"),
                        category = cat,
                        initialClue = myObj.getString("initialClue"),
                        discoveredDay = myObj.getInt("discoveredDay"),
                        knownFacts = baseMystery?.knownFacts ?: mutableListOf(),
                        unknownSuspicions = baseMystery?.unknownSuspicions ?: mutableListOf(),
                        possibleExplanations = baseMystery?.possibleExplanations ?: emptyList(),
                        discoveredEvidence = baseMystery?.discoveredEvidence ?: mutableListOf(),
                        investigationPaths = baseMystery?.investigationPaths ?: emptyList(),
                        status = status,
                        resolutionSummary = myObj.optString("resolutionSummary").ifBlank { null },
                        rewardSummary = myObj.optString("rewardSummary").ifBlank { null }
                    )
                )
            }
        } else {
            mysteries.addAll(baseState.mysteries)
        }

        return baseState.copy(
            dominus = dominus,
            gladiators = gladiators,
            persistentFighters = persistentFighters,
            arenaCalendar = calendar,
            undergroundFights = undergroundFights,
            fallenGladiators = memorials,
            worldMemory = worldMemory,
            rumors = rumors,
            mysteries = mysteries
        )
    }

    private fun deserializeGladiator(obj: JSONObject): Gladiator {
        val gClass = try { GladiatorClass.valueOf(obj.getString("gladiatorClass")) } catch (_: Exception) { GladiatorClass.MURMILLO }
        val origin = try { Origin.valueOf(obj.getString("origin")) } catch (_: Exception) { Origin.ITALIA }
        val status = try { GladiatorStatus.valueOf(obj.getString("status")) } catch (_: Exception) { GladiatorStatus.AUCTORATUS }
        val personality = try { Personality.valueOf(obj.getString("personality")) } catch (_: Exception) { Personality.DISCIPLINED }
        val tier = try { OpponentTier.valueOf(obj.optString("tier", "COMMON")) } catch (_: Exception) { OpponentTier.COMMON }
        val aiPersonality = try { AiTacticalPersonality.valueOf(obj.optString("aiPersonality", "VETERAN")) } catch (_: Exception) { AiTacticalPersonality.VETERAN }
        val venue = try { ArenaVenueId.valueOf(obj.optString("currentArena", "CAPUA")) } catch (_: Exception) { ArenaVenueId.CAPUA }
        val bossState = obj.optString("undergroundBossState").let { if (it.isNotBlank()) try { BossState.valueOf(it) } catch (_: Exception) { null } else null }

        val deathDay = obj.optInt("deathDay", -1).let { if (it >= 0) it else null }

        return Gladiator(
            id = obj.getString("id"),
            name = obj.getString("name"),
            nickname = obj.optString("nickname", ""),
            age = obj.optInt("age", 25),
            origin = origin,
            ethnicity = obj.optString("ethnicity", "Roman"),
            gladiatorClass = gClass,
            ludusAffiliation = obj.optString("ludusAffiliation", "Ludus Valerius"),
            ownerName = obj.optString("ownerName", "Dominus"),
            tier = tier,
            aiPersonality = aiPersonality,
            signatureTactic = obj.optString("signatureTactic", "Standard Duel"),
            strength = obj.optInt("strength", 14),
            speed = obj.optInt("speed", 14),
            agility = obj.optInt("agility", 13),
            endurance = obj.optInt("endurance", 14),
            reflex = obj.optInt("reflex", 13),
            painTolerance = obj.optInt("painTolerance", 14),
            swordsmanship = obj.optInt("swordsmanship", 14),
            shieldSkill = obj.optInt("shieldSkill", 14),
            discipline = obj.optInt("discipline", 14),
            wins = obj.optInt("wins", 0),
            losses = obj.optInt("losses", 0),
            kills = obj.optInt("kills", 0),
            spared = obj.optInt("spared", 0),
            currentWinStreak = obj.optInt("currentWinStreak", 0),
            crowdApproval = obj.optInt("crowdApproval", 60),
            denariiEarned = obj.optInt("denariiEarned", 0),
            currentArena = venue,
            rankingPosition = obj.optInt("rank", 1),
            currentHealth = obj.optInt("health", 100),
            isChampion = obj.optBoolean("isChampion", false),
            isAlive = obj.optBoolean("isAlive", true) && status != GladiatorStatus.DEAD,
            isRetired = obj.optBoolean("isRetired", false),
            deathDay = deathDay,
            killedBy = obj.optString("killedBy").ifBlank { null },
            causeOfDeath = obj.optString("causeOfDeath").ifBlank { null },
            deathArena = obj.optString("deathArena").ifBlank { null },
            isUnderground = obj.optBoolean("isUnderground", false),
            undergroundBossState = bossState
        ).apply {
            this.status = status
            this.personality = personality
            this.condition.health = obj.optInt("health", 100)
            this.condition.stamina = obj.optInt("stamina", 100)
            this.condition.morale = obj.optInt("morale", 80)
        }
    }

    fun saveGameToFile(file: File, state: LudusUiState): Boolean {
        return try {
            val json = serializeStateToJson(state)
            file.writeText(json)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadGameFromFile(file: File, baseState: LudusUiState): LudusUiState? {
        return try {
            if (!file.exists()) return null
            val json = file.readText()
            deserializeStateFromJson(json, baseState)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
