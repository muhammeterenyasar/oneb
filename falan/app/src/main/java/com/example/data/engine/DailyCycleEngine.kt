package com.example.data.engine

import com.example.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object DailyCycleEngine {

    data class PhaseAdvanceResult(
        val updatedState: LudusState,
        val updatedGladiators: List<Gladiator>,
        val summaryMessage: String
    )

    fun advancePhase(
        currentState: LudusState,
        allGladiators: List<Gladiator>,
        activeTeachers: List<Teacher>,
        onProcessDrill: (Gladiator, SpecificDrill, DietPlan, List<Teacher>, List<String>) -> Pair<Gladiator, String>
    ): PhaseAdvanceResult {
        when (currentState.phase) {
            DayPhase.MORNING -> {
                val diet = currentState.dietPlan
                val updatedGladiators = mutableListOf<Gladiator>()
                var trainedCount = 0

                for (g in allGladiators) {
                    if (!g.hasTrainedToday && !g.isInjured && g.fatigue < 100) {
                        val drill = g.assignedDrill
                        val (updated, _) = onProcessDrill(g, drill, diet, activeTeachers, currentState.unlockedPerkIds)
                        updatedGladiators.add(updated)
                        trainedCount++
                    } else {
                        updatedGladiators.add(g)
                    }
                }

                val newState = currentState.copy(phase = DayPhase.NOON)
                val msg = if (trainedCount > 0) {
                    "🏛️ Sabah İdmanları Tamamlandı ($trainedCount gladyatör çalıştırıldı). Öğle vaktine geçildi."
                } else {
                    "🏛️ Sabah idman dönemi sona erdi. Öğle vaktine geçildi."
                }
                return PhaseAdvanceResult(newState, updatedGladiators, msg)
            }

            DayPhase.NOON -> {
                val isMatchDay = currentState.isFightDay
                val msg = if (isMatchDay) {
                    "🏛️ BÜYÜK MÜSABAKA GÜNÜ GELDİ! ${currentState.cityTier.cityName} arenası kapılarını açtı! Akşam karşılaşması için gladyatörünüzü arenaya sürün."
                } else {
                    "Öğle taktik brifingi bitti. Bir sonraki resmi müsabakaya ${currentState.daysUntilNextFight} gün kaldı. Hazırlıklar devam ediyor."
                }
                val newState = currentState.copy(phase = DayPhase.EVENING)
                return PhaseAdvanceResult(newState, allGladiators, msg)
            }

            DayPhase.EVENING -> {
                val newState = currentState.copy(phase = DayPhase.NIGHT)
                val msg = "Akşam dövüş ve hazırlık saatleri sona erdi. Revir ve gece hesap kesimi zamanı."
                return PhaseAdvanceResult(newState, allGladiators, msg)
            }

            DayPhase.NIGHT -> {
                val summaryMessages = mutableListOf<String>()
                var newGold = currentState.gold
                var newPrestige = currentState.prestige
                var activeDebt = currentState.activeDebt
                var debtDaysLeft = currentState.debtDueDaysLeft
                var threatStage = currentState.threatStage
                var threatMessage: String? = null

                // 1. Calculate Daily Expenses
                val expenses = EconomyEngine.calculateDailyExpenses(
                    allGladiators,
                    activeTeachers,
                    currentState.guardsHired,
                    currentState.physicianLevel,
                    currentState.dietPlan
                )
                newGold -= expenses.totalExpenses
                summaryMessages.add("Günlük giderler ödendi: -${expenses.totalExpenses} Altın (Diyet: ${expenses.foodCost}, Maaşlar: ${expenses.salaryCost}, Eğitmenler: ${expenses.teacherSalaries}, Muhafız: ${expenses.guardCost}, Hekim: ${expenses.physicianUpkeep})")

                // Senator sponsorship bonus if active
                val sponsorBonus = EconomyEngine.calculateSenatorSponsorshipBonus(currentState.senatorSponsorshipDays)
                if (sponsorBonus > 0) {
                    newGold += sponsorBonus
                    summaryMessages.add("🏛️ Senatör Sponsorluğu: +$sponsorBonus Altın ödendi.")
                }

                // 2. Debt & Usurer check
                if (newGold < 0) {
                    val deficit = -newGold
                    newGold = 0
                    activeDebt += (deficit * 1.25f).toInt() + 100
                    debtDaysLeft = 90
                    threatStage = ThreatStage.WARNING
                    threatMessage = "Kasa eksiye düştü! Tefeci devreye girdi ve faizle zorunlu borç açtı. 90 gün içinde ödenmeli."
                    summaryMessages.add(threatMessage)
                } else if (activeDebt > 0) {
                    debtDaysLeft = max(0, debtDaysLeft - 1)
                    if (debtDaysLeft <= 0) {
                        when (threatStage) {
                            ThreatStage.NONE, ThreatStage.WARNING -> {
                                threatStage = ThreatStage.INFILTRATION_RISK
                                activeDebt = (activeDebt * 1.15f).toInt()
                                debtDaysLeft = 30
                                threatMessage = "⚠️ Tefeci Uyarısı: Borç süresi doldu! %15 faiz bindi ve suikastçı sızma riski başladı."
                                summaryMessages.add(threatMessage)
                            }
                            ThreatStage.INFILTRATION_RISK -> {
                                if (Random.nextFloat() < 0.35f) {
                                    val detectionChance = min(90, currentState.guardsHired * 30 + currentState.physicianLevel * 10)
                                    val roll = Random.nextInt(100)
                                    if (roll < detectionChance) {
                                        threatMessage = "🛡️ GECE BASKINI ÖNLENDİ! Tefecinin gönderdiği suikastçı muhafızlarca yakalandı."
                                        summaryMessages.add(threatMessage)
                                    } else {
                                        threatStage = ThreatStage.ATTACK_ACTIVE
                                        val topGladiator = allGladiators.maxByOrNull { it.totalPowerScore }
                                        if (topGladiator != null) {
                                            threatMessage = "☠️ SUİKAST! Sızan suikastçı şampiyonun ${topGladiator.name}'ı yaraladı!"
                                            summaryMessages.add(threatMessage)
                                        }
                                    }
                                }
                            }
                            ThreatStage.ATTACK_ACTIVE -> {
                                threatMessage = "Tefecinin adamları ludus çevresinde baskı kuruyor."
                                summaryMessages.add(threatMessage)
                            }
                        }
                    }
                }

                // 3. Physician & Natural Recovery & Aging
                val isAgingMonth = (currentState.day + 1) % 30 == 0
                val updatedGladiators = allGladiators.map { g ->
                    var updated = g
                    // Aging check
                    if (isAgingMonth) {
                        updated = updated.copy(age = updated.age + 1)
                    }

                    // Night fatigue recovery (-15 natural recovery)
                    val newFatigue = max(0, updated.fatigue - 15)
                    updated = updated.copy(fatigue = newFatigue)

                    if (updated.isInjured) {
                        val hasAesculapiusPerk = currentState.unlockedPerkIds.contains(LanistaPerk.AESCULAPIUS_TOUCH.id)
                        val perkBonusSpeed = if (hasAesculapiusPerk) 2 else 0
                        val baseSpeed = when (currentState.physicianLevel) {
                            3 -> 2
                            2 -> 1
                            else -> if (Random.nextBoolean()) 1 else 0
                        }
                        val healingSpeed = baseSpeed + perkBonusSpeed
                        val newDaysLeft = max(0, updated.recoveryDaysLeft - healingSpeed)
                        val isHealed = newDaysLeft == 0
                        val recoveredHp = if (isHealed) updated.maxHp else min(updated.maxHp, updated.currentHp + (updated.maxHp * (if (hasAesculapiusPerk) 0.40f else 0.25f)).toInt())

                        updated = updated.copy(
                            recoveryDaysLeft = newDaysLeft,
                            isInjured = !isHealed,
                            injurySeverity = if (isHealed) InjurySeverity.NONE else updated.injurySeverity,
                            currentHp = recoveredHp
                        )
                    } else {
                        val hasAesculapiusPerk = currentState.unlockedPerkIds.contains(LanistaPerk.AESCULAPIUS_TOUCH.id)
                        val hpRegen = if (hasAesculapiusPerk) 35 else 20
                        updated = updated.copy(
                            currentHp = min(updated.maxHp, updated.currentHp + hpRegen)
                        )
                    }

                    val moralDelta = if (currentState.threatStage != ThreatStage.NONE) -2 else 1
                    updated.copy(
                        mor = max(10, min(100, updated.mor + moralDelta)),
                        hasTrainedToday = false
                    )
                }

                // Check city tier upgrade automatically if prestige threshold reached
                var newCityTier = currentState.cityTier
                if (newPrestige >= 750 && newCityTier.tierNumber < 4) {
                    newCityTier = CityTier.ROME_COLOSSEUM
                    summaryMessages.add("🏆 KADEME ATLAMA: Roma Colosseum kapıları açıldı! İmparatorluk dövüşleri sizi bekliyor.")
                } else if (newPrestige >= 300 && newCityTier.tierNumber < 3) {
                    newCityTier = CityTier.VERONA
                    summaryMessages.add("🏆 KADEME ATLAMA: Verona Amfitiyatrosu açıldı! Büyük sponsorlar ludus'u takip ediyor.")
                } else if (newPrestige >= 100 && newCityTier.tierNumber < 2) {
                    newCityTier = CityTier.CAPUA_POMPEII
                    summaryMessages.add("🏆 KADEME ATLAMA: Capua & Pompeii arenalarına terfi ettiniz! 2v2 takım maçları açıldı.")
                }

                val nextDay = currentState.day + 1
                var nextMatchDay = currentState.nextScheduledMatchDay
                if (nextDay > nextMatchDay) {
                    nextMatchDay = nextDay + Random.nextInt(3, 5)
                }

                val nextDayState = currentState.copy(
                    day = nextDay,
                    phase = DayPhase.MORNING,
                    gold = newGold,
                    prestige = newPrestige,
                    activeDebt = activeDebt,
                    debtDueDaysLeft = debtDaysLeft,
                    threatStage = threatStage,
                    lastThreatEventMessage = threatMessage ?: currentState.lastThreatEventMessage,
                    cityTier = newCityTier,
                    nextScheduledMatchDay = nextMatchDay,
                    senatorSponsorshipDays = max(0, currentState.senatorSponsorshipDays - 1)
                )

                val fullSummary = "Gece tamamlandı. ${nextDayState.day}. Gün başladı!\n" + summaryMessages.joinToString("\n")
                return PhaseAdvanceResult(nextDayState, updatedGladiators, fullSummary)
            }
        }
    }
}
