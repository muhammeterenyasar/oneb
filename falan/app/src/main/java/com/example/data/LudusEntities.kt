package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.Gladiator
import com.example.model.GladiatorClass
import com.example.model.GladiatorContractType
import com.example.model.InjurySeverity
import com.example.model.LudusState
import com.example.model.CityTier
import com.example.model.DayPhase
import com.example.model.DietPlan
import com.example.model.ThreatStage
import com.example.model.TrainingType
import com.example.model.Teacher
import com.example.model.TeacherSpecialty

@Entity(tableName = "gladiators")
data class GladiatorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val nickname: String,
    val origin: String,
    val gladiatorClass: String,
    val contractType: String,
    val dailySalary: Int,
    val priceValue: Int,
    
    val str: Int,
    val agi: Int,
    val sta: Int,
    val mor: Int,
    
    val age: Int = 23,
    val fatigue: Int = 0,
    val experience: Int = 0,
    val isRetiredTeacher: Boolean = false,
    
    val currentHp: Int,
    val maxHp: Int,
    val isInjured: Boolean,
    val injurySeverity: String,
    val recoveryDaysLeft: Int,
    val hasDisabledLimb: Boolean,
    val disabledLimbDesc: String?,
    
    val promiseOfFreedom: Boolean,
    val wins: Int,
    val losses: Int,
    val kills: Int,
    val weaponLevel: Int,
    val armorLevel: Int,
    val isTraining: Boolean,
    val trainedStatToday: String?,
    val trainingFocus: String = "STRENGTH",
    val assignedDrill: String = "PALUS_WOODEN_POST",
    val strXpProgress: Int = 20,
    val agiXpProgress: Int = 15,
    val staXpProgress: Int = 25,
    val drillMasteryLevel: Int = 1,
    val drillsCompletedCount: Int = 0,
    val lastDrillSummary: String? = null,
    val hasTrainedToday: Boolean = false,
    val equippedWeaponId: String? = null,
    val equippedArmorId: String? = null,
    val equippedRelicId: String? = null,
    val personalityTrait: String = "IRON_WILLED"
) {
    fun toDomain(): Gladiator = Gladiator(
        id = id,
        name = name,
        nickname = nickname,
        origin = origin,
        gladiatorClass = try { GladiatorClass.valueOf(gladiatorClass) } catch (e: Exception) { GladiatorClass.MURMILLO },
        contractType = try { GladiatorContractType.valueOf(contractType) } catch (e: Exception) { GladiatorContractType.SLAVE },
        dailySalary = dailySalary,
        priceValue = priceValue,
        str = str,
        agi = agi,
        sta = sta,
        mor = mor,
        age = age,
        fatigue = fatigue,
        experience = experience,
        isRetiredTeacher = isRetiredTeacher,
        currentHp = currentHp,
        maxHp = maxHp,
        isInjured = isInjured,
        injurySeverity = try { InjurySeverity.valueOf(injurySeverity) } catch (e: Exception) { InjurySeverity.NONE },
        recoveryDaysLeft = recoveryDaysLeft,
        hasDisabledLimb = hasDisabledLimb,
        disabledLimbDesc = disabledLimbDesc,
        promiseOfFreedom = promiseOfFreedom,
        wins = wins,
        losses = losses,
        kills = kills,
        weaponLevel = weaponLevel,
        armorLevel = armorLevel,
        isTraining = isTraining,
        trainedStatToday = trainedStatToday,
        trainingFocus = try { TrainingType.valueOf(trainingFocus) } catch (e: Exception) { TrainingType.STRENGTH },
        assignedDrill = try { com.example.model.SpecificDrill.valueOf(assignedDrill) } catch (e: Exception) { com.example.model.SpecificDrill.PALUS_WOODEN_POST },
        strXpProgress = strXpProgress,
        agiXpProgress = agiXpProgress,
        staXpProgress = staXpProgress,
        drillMasteryLevel = drillMasteryLevel,
        drillsCompletedCount = drillsCompletedCount,
        lastDrillSummary = lastDrillSummary,
        hasTrainedToday = hasTrainedToday,
        equippedWeaponId = equippedWeaponId,
        equippedArmorId = equippedArmorId,
        equippedRelicId = equippedRelicId,
        personalityTrait = try { com.example.model.GladiatorTrait.valueOf(personalityTrait) } catch (e: Exception) { com.example.model.GladiatorTrait.IRON_WILLED }
    )

    companion object {
        fun fromDomain(g: Gladiator): GladiatorEntity = GladiatorEntity(
            id = g.id,
            name = g.name,
            nickname = g.nickname,
            origin = g.origin,
            gladiatorClass = g.gladiatorClass.name,
            contractType = g.contractType.name,
            dailySalary = g.dailySalary,
            priceValue = g.priceValue,
            str = g.str,
            agi = g.agi,
            sta = g.sta,
            mor = g.mor,
            age = g.age,
            fatigue = g.fatigue,
            experience = g.experience,
            isRetiredTeacher = g.isRetiredTeacher,
            currentHp = g.currentHp,
            maxHp = g.maxHp,
            isInjured = g.isInjured,
            injurySeverity = g.injurySeverity.name,
            recoveryDaysLeft = g.recoveryDaysLeft,
            hasDisabledLimb = g.hasDisabledLimb,
            disabledLimbDesc = g.disabledLimbDesc,
            promiseOfFreedom = g.promiseOfFreedom,
            wins = g.wins,
            losses = g.losses,
            kills = g.kills,
            weaponLevel = g.weaponLevel,
            armorLevel = g.armorLevel,
            isTraining = g.isTraining,
            trainedStatToday = g.trainedStatToday,
            trainingFocus = g.trainingFocus.name,
            assignedDrill = g.assignedDrill.name,
            strXpProgress = g.strXpProgress,
            agiXpProgress = g.agiXpProgress,
            staXpProgress = g.staXpProgress,
            drillMasteryLevel = g.drillMasteryLevel,
            drillsCompletedCount = g.drillsCompletedCount,
            lastDrillSummary = g.lastDrillSummary,
            hasTrainedToday = g.hasTrainedToday,
            equippedWeaponId = g.equippedWeaponId,
            equippedArmorId = g.equippedArmorId,
            equippedRelicId = g.equippedRelicId,
            personalityTrait = g.personalityTrait.name
        )
    }
}


@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val title: String,
    val specialty: String,
    val originGladiatorName: String? = null,
    val level: Int = 1,
    val statBonusMultiplier: Float = 1.35f,
    val dailySalary: Int = 10,
    val hireCost: Int = 100,
    val description: String,
    val isPromotedFromRoster: Boolean = false
) {
    fun toDomain(): Teacher = Teacher(
        id = id,
        name = name,
        title = title,
        specialty = try { TeacherSpecialty.valueOf(specialty) } catch (e: Exception) { TeacherSpecialty.MURMILLO_MASTER },
        originGladiatorName = originGladiatorName,
        level = level,
        statBonusMultiplier = statBonusMultiplier,
        dailySalary = dailySalary,
        hireCost = hireCost,
        description = description,
        isPromotedFromRoster = isPromotedFromRoster
    )

    companion object {
        fun fromDomain(t: Teacher): TeacherEntity = TeacherEntity(
            id = t.id,
            name = t.name,
            title = t.title,
            specialty = t.specialty.name,
            originGladiatorName = t.originGladiatorName,
            level = t.level,
            statBonusMultiplier = t.statBonusMultiplier,
            dailySalary = t.dailySalary,
            hireCost = t.hireCost,
            description = t.description,
            isPromotedFromRoster = t.isPromotedFromRoster
        )
    }
}

@Entity(tableName = "ludus_state")
data class LudusStateEntity(
    @PrimaryKey val id: Int = 1,
    val day: Int,
    val phase: String,
    val gold: Int,
    val prestige: Int,
    val maxGladiatorSlots: Int,
    val physicianLevel: Int,
    val guardsHired: Int,
    val maxGuards: Int,
    val dietPlan: String = "BARLEY_PORRIDGE",
    val nextScheduledMatchDay: Int = 5,
    val activeDebt: Int,
    val debtDueDaysLeft: Int,
    val threatStage: String,
    val lastThreatEventMessage: String?,
    val cityTier: String,
    val totalFights: Int,
    val totalWins: Int,
    val freedGladiatorsCount: Int,
    val senatorSponsorshipDays: Int,
    val scoutedEnemyWeakness: Boolean = false,
    val sharpenedWeapons: Boolean = false,
    val crowdHypeBonus: Boolean = false,
    val rivalWeakenedByPoison: Boolean = false,
    val marsDivineBlessing: Boolean = false,
    val opponentDifficultyModifier: Float = 1.0f,
    val ruthlessnessScore: Int = 0,
    val mercyScore: Int = 0,
    val crowdSentimentScore: Int = 50,
    val lastMatchExcitement: Int = 50,
    val lastDecisionConsequence: String? = null,
    val unlockedPerks: String = "",
    val completedMissions: String = ""
) {
    fun toDomain(teachers: List<Teacher> = emptyList()): LudusState = LudusState(
        day = day,
        phase = try { DayPhase.valueOf(phase) } catch (e: Exception) { DayPhase.MORNING },
        gold = gold,
        prestige = prestige,
        maxGladiatorSlots = maxGladiatorSlots,
        physicianLevel = physicianLevel,
        guardsHired = guardsHired,
        maxGuards = maxGuards,
        dietPlan = try { DietPlan.valueOf(dietPlan) } catch (e: Exception) { DietPlan.BARLEY_PORRIDGE },
        nextScheduledMatchDay = nextScheduledMatchDay,
        activeTeachers = teachers,
        activeDebt = activeDebt,
        debtDueDaysLeft = debtDueDaysLeft,
        threatStage = try { ThreatStage.valueOf(threatStage) } catch (e: Exception) { ThreatStage.NONE },
        lastThreatEventMessage = lastThreatEventMessage,
        cityTier = try { CityTier.valueOf(cityTier) } catch (e: Exception) { CityTier.TOWN_RURAL },
        totalFights = totalFights,
        totalWins = totalWins,
        freedGladiatorsCount = freedGladiatorsCount,
        senatorSponsorshipDays = senatorSponsorshipDays,
        scoutedEnemyWeakness = scoutedEnemyWeakness,
        sharpenedWeapons = sharpenedWeapons,
        crowdHypeBonus = crowdHypeBonus,
        rivalWeakenedByPoison = rivalWeakenedByPoison,
        marsDivineBlessing = marsDivineBlessing,
        opponentDifficultyModifier = opponentDifficultyModifier,
        ruthlessnessScore = ruthlessnessScore,
        mercyScore = mercyScore,
        crowdSentimentScore = crowdSentimentScore,
        lastMatchExcitement = lastMatchExcitement,
        lastDecisionConsequence = lastDecisionConsequence,
        unlockedPerkIds = unlockedPerks.split(",").filter { it.isNotBlank() },
        completedCampaignMissionIds = completedMissions.split(",").filter { it.isNotBlank() }
    )

    companion object {
        fun fromDomain(s: LudusState): LudusStateEntity = LudusStateEntity(
            id = 1,
            day = s.day,
            phase = s.phase.name,
            gold = s.gold,
            prestige = s.prestige,
            maxGladiatorSlots = s.maxGladiatorSlots,
            physicianLevel = s.physicianLevel,
            guardsHired = s.guardsHired,
            maxGuards = s.maxGuards,
            dietPlan = s.dietPlan.name,
            nextScheduledMatchDay = s.nextScheduledMatchDay,
            activeDebt = s.activeDebt,
            debtDueDaysLeft = s.debtDueDaysLeft,
            threatStage = s.threatStage.name,
            lastThreatEventMessage = s.lastThreatEventMessage,
            cityTier = s.cityTier.name,
            totalFights = s.totalFights,
            totalWins = s.totalWins,
            freedGladiatorsCount = s.freedGladiatorsCount,
            senatorSponsorshipDays = s.senatorSponsorshipDays,
            scoutedEnemyWeakness = s.scoutedEnemyWeakness,
            sharpenedWeapons = s.sharpenedWeapons,
            crowdHypeBonus = s.crowdHypeBonus,
            rivalWeakenedByPoison = s.rivalWeakenedByPoison,
            marsDivineBlessing = s.marsDivineBlessing,
            opponentDifficultyModifier = s.opponentDifficultyModifier,
            ruthlessnessScore = s.ruthlessnessScore,
            mercyScore = s.mercyScore,
            crowdSentimentScore = s.crowdSentimentScore,
            lastMatchExcitement = s.lastMatchExcitement,
            lastDecisionConsequence = s.lastDecisionConsequence,
            unlockedPerks = s.unlockedPerkIds.joinToString(","),
            completedMissions = s.completedCampaignMissionIds.joinToString(",")
        )
    }
}


@Entity(tableName = "match_logs")
data class MatchLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val day: Int,
    val gladiatorName: String,
    val opponentName: String,
    val format: String,
    val tactic: String,
    val result: String,
    val goldEarned: Int,
    val prestigeEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)
