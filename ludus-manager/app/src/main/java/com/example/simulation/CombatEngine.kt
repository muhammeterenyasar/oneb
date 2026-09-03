package com.example.simulation

import com.example.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class CombatEngine(
    val playerGladiator: Gladiator,
    val opponentGladiator: Gladiator,
    val stance: CombatStance,
    val target: CombatTarget,
    val arenaName: String,
    val aiPersonality: AiTacticalPersonality = AiTacticalPersonality.VETERAN,
    val matchType: ArenaMatchType = ArenaMatchType.STANDARD_DUEL,
    val playerLoadout: GladiatorLoadout? = null,
    val opponentLoadout: GladiatorLoadout? = null
) {
    var tick: Int = 0
    val maxDurationSeconds: Int = 60
    var isFinished: Boolean = false
    var playerWon: Boolean = false

    val playerState = CombatFighterState(
        gladiator = playerGladiator,
        currentHealth = playerGladiator.condition.health.toFloat(),
        currentStamina = playerGladiator.condition.stamina.toFloat(),
        posX = 0.32f,
        posY = 0.50f,
        facingAngle = 0f
    )

    val opponentState = CombatFighterState(
        gladiator = opponentGladiator,
        currentHealth = opponentGladiator.condition.health.toFloat(),
        currentStamina = opponentGladiator.condition.stamina.toFloat(),
        posX = 0.68f,
        posY = 0.50f,
        facingAngle = 180f
    )

    val logs = mutableListOf<CombatLogEntry>()
    val bloodSplatters = mutableListOf<ArenaBloodSplatter>()

    var crowdExcitement: Int = 50
    var crowdBloodlust: Int = 40
    var crowdMercyBias: Int = 50

    var playerHits = 0
    var playerBlocks = 0
    var playerCriticals = 0

    var polliceVersoState: PolliceVersoState = PolliceVersoState()

    init {
        logs.add(CombatLogEntry(0, "Dövüş Başladı! ${playerGladiator.name} (${playerGladiator.gladiatorClass.title}) vs ${opponentGladiator.name} (${opponentGladiator.gladiatorClass.title}) kumların üzerine çıktı."))
    }

    fun executeTacticalCommand(command: TacticalCommand): String {
        // Check obedience influenced by helmet hearing and stress
        val discipline = playerGladiator.attributes.discipline
        val hearingMod = (playerLoadout?.helmet?.hearingPercent ?: 85) / 100f
        val obedienceChance = ((0.60f + (discipline * 0.02f) - (playerState.stress * 0.002f)) * hearingMod).coerceIn(0.20f, 0.98f)
        val roll = Random.nextFloat()

        val dx = opponentState.posX - playerState.posX
        val dy = opponentState.posY - playerState.posY
        val distance = max(0.01f, kotlin.math.sqrt(dx * dx + dy * dy))
        val normX = dx / distance
        val normY = dy / distance

        return if (roll <= obedienceChance) {
            playerState.activeCommand = command
            when (command) {
                TacticalCommand.RAISE_SHIELD -> {
                    playerState.isBlocking = true
                    playerState.currentStamina = max(0f, playerState.currentStamina - 8f)
                    logs.add(CombatLogEntry(tick, "${playerGladiator.name} kalkanını kaldırdı ve savunma pozisyonuna geçti!", isPlayerAction = true))
                    "Emir uygulandı: Kalkan kaldırıldı!"
                }
                TacticalCommand.KEEP_DISTANCE -> {
                    playerState.posX = (playerState.posX - normX * 0.12f).coerceIn(0.18f, 0.82f)
                    playerState.posY = (playerState.posY - normY * 0.12f).coerceIn(0.22f, 0.78f)
                    playerState.currentStamina = min(100f, playerState.currentStamina + 10f)
                    logs.add(CombatLogEntry(tick, "${playerGladiator.name} mesafeyi açtı, nefesleniyor.", isPlayerAction = true))
                    "Mesafe korundu, nefes tazelendi."
                }
                TacticalCommand.RUSH_ATTACK -> {
                    playerState.posX = (playerState.posX + normX * 0.10f).coerceIn(0.18f, 0.82f)
                    playerState.posY = (playerState.posY + normY * 0.10f).coerceIn(0.22f, 0.78f)
                    playerState.isAttacking = true
                    performStrike(isPlayerAttacker = true, isRush = true)
                    "Hücum emri verildi!"
                }
                TacticalCommand.FALL_BACK -> {
                    playerState.posX = (playerState.posX - normX * 0.14f).coerceIn(0.18f, 0.82f)
                    playerState.posY = (playerState.posY - normY * 0.14f).coerceIn(0.22f, 0.78f)
                    logs.add(CombatLogEntry(tick, "${playerGladiator.name} geriye adım atarak rakibin menzilinden çıktı.", isPlayerAction = true))
                    "Geri çekilindi."
                }
                TacticalCommand.COUNTER_STRIKE -> {
                    playerState.isBlocking = true
                    logs.add(CombatLogEntry(tick, "${playerGladiator.name} karşı vuruş fırsatı kolluyor!", isPlayerAction = true))
                    "Karşı saldırı hazırlandı."
                }
                TacticalCommand.FINISH_HIM -> {
                    logs.add(CombatLogEntry(tick, "Lanista haykırdı: 'KAN GÖR! BİTİR!' Kalabalık kükredi!", isCritical = true))
                    crowdBloodlust = min(100, crowdBloodlust + 20)
                    performStrike(isPlayerAttacker = true, isFinishAttempt = true)
                    "Ölümcül hamle yapıldı!"
                }
                TacticalCommand.TAUNT_CROWD -> {
                    crowdExcitement = min(100, crowdExcitement + 15)
                    playerGladiator.condition.morale = min(100, playerGladiator.condition.morale + 10)
                    logs.add(CombatLogEntry(tick, "${playerGladiator.name} kılıcını kalkanına vurarak Capua halkını selamladı!", isPlayerAction = true))
                    "Kalabalık coşturuldu!"
                }
            }
        } else {
            logs.add(CombatLogEntry(tick, "${playerGladiator.name} panik ve yorgunluk sebebiyle emre uyamadı!", isPlayerAction = true))
            "Gladyatör emri duymadı veya uygulayamadı!"
        }
    }

    fun stepTick() {
        if (isFinished || polliceVersoState.isActive) return

        tick++

        // Reset momentary states
        playerState.isAttacking = false
        opponentState.isAttacking = false
        playerState.isStaggered = false
        playerState.isBlocking = false
        opponentState.isStaggered = false
        opponentState.isBlocking = false

        // 2D Movement & distance calculation in Top-Down Arena
        val dx = opponentState.posX - playerState.posX
        val dy = opponentState.posY - playerState.posY
        val distance = max(0.01f, kotlin.math.sqrt(dx * dx + dy * dy))
        val normX = dx / distance
        val normY = dy / distance

        // Dynamic 360-degree facing angles
        playerState.facingAngle = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
        opponentState.facingAngle = Math.toDegrees(kotlin.math.atan2(-dy.toDouble(), -dx.toDouble())).toFloat()

        // Tactical 2D circling around center of combat
        val perpX = -normY
        val perpY = normX
        val circleDirection = if ((tick / 4) % 2 == 0) 1f else -1f
        val circleStep = if (stance == CombatStance.DISTANCE) 0.012f else 0.007f

        playerState.posX = (playerState.posX + perpX * circleStep * circleDirection).coerceIn(0.20f, 0.80f)
        playerState.posY = (playerState.posY + perpY * circleStep * circleDirection).coerceIn(0.22f, 0.78f)

        // Opponent circles conversely
        opponentState.posX = (opponentState.posX - perpX * circleStep * circleDirection).coerceIn(0.20f, 0.80f)
        opponentState.posY = (opponentState.posY - perpY * circleStep * circleDirection).coerceIn(0.22f, 0.78f)

        // Natural closing in if outside strike range
        if (distance > 0.22f) {
            val advanceSpeed = 0.014f
            playerState.posX = (playerState.posX + normX * advanceSpeed).coerceIn(0.20f, 0.80f)
            playerState.posY = (playerState.posY + normY * advanceSpeed).coerceIn(0.22f, 0.78f)
            opponentState.posX = (opponentState.posX - normX * advanceSpeed).coerceIn(0.20f, 0.80f)
            opponentState.posY = (opponentState.posY - normY * advanceSpeed).coerceIn(0.22f, 0.78f)
        }

        // Action exchange every 2 seconds
        if (tick % 2 == 0) {
            val playerSpeed = playerGladiator.attributes.speed + (if (stance == CombatStance.AGGRESSIVE) 3 else 0)
            val opponentSpeed = opponentGladiator.attributes.speed

            val initiative = playerSpeed + Random.nextInt(1, 10) >= opponentSpeed + Random.nextInt(1, 10)

            if (initiative) {
                performStrike(isPlayerAttacker = true)
            } else {
                performStrike(isPlayerAttacker = false)
            }
        }

        // Natural fatigue & blood loss drain
        if (playerState.bloodLoss > 0) {
            playerState.currentHealth = max(0f, playerState.currentHealth - (playerState.bloodLoss * 0.15f))
            if (Random.nextFloat() < 0.35f) {
                bloodSplatters.add(ArenaBloodSplatter(playerState.posX, playerState.posY, radius = 7f, alpha = 0.6f))
            }
        }
        if (opponentState.bloodLoss > 0) {
            opponentState.currentHealth = max(0f, opponentState.currentHealth - (opponentState.bloodLoss * 0.15f))
            if (Random.nextFloat() < 0.35f) {
                bloodSplatters.add(ArenaBloodSplatter(opponentState.posX, opponentState.posY, radius = 7f, alpha = 0.6f))
            }
        }

        // Check knockdown & Pollice Verso condition
        if (opponentState.currentHealth <= 12f && !isFinished) {
            opponentState.isDown = true
            isFinished = true
            playerWon = true
            logs.add(CombatLogEntry(tick, "${opponentGladiator.name} dizlerinin üzerine çöktü! Hakem dövüşü durdurdu!", isCritical = true, bloodEffect = true))
            polliceVersoState = PolliceVersoState(
                isActive = true,
                fallenFighterName = opponentGladiator.name,
                isPlayerFallen = false,
                crowdMercyPercent = calculateCrowdMercy(opponentGladiator)
            )
        } else if (playerState.currentHealth <= 12f && !isFinished) {
            playerState.isDown = true
            isFinished = true
            playerWon = false
            logs.add(CombatLogEntry(tick, "${playerGladiator.name} kumların üzerine yığıldı! Pollici Verso kararı bekleniyor!", isCritical = true, bloodEffect = true))
            polliceVersoState = PolliceVersoState(
                isActive = true,
                fallenFighterName = playerGladiator.name,
                isPlayerFallen = true,
                crowdMercyPercent = calculateCrowdMercy(playerGladiator)
            )
        } else if (tick >= maxDurationSeconds) {
            isFinished = true
            // Decision by points/health
            playerWon = playerState.currentHealth >= opponentState.currentHealth
            val winnerName = if (playerWon) playerGladiator.name else opponentGladiator.name
            logs.add(CombatLogEntry(tick, "Süre Doldu! Hakemler puan üstünlüğüyle $winnerName galip ilan etti!", isCritical = true))
        }
    }

    private fun performStrike(isPlayerAttacker: Boolean, isRush: Boolean = false, isFinishAttempt: Boolean = false) {
        val attacker = if (isPlayerAttacker) playerGladiator else opponentGladiator
        val defender = if (isPlayerAttacker) opponentGladiator else playerGladiator
        val defenderState = if (isPlayerAttacker) opponentState else playerState
        val attackerState = if (isPlayerAttacker) playerState else opponentState

        val isBerserk = !isPlayerAttacker && aiPersonality == AiTacticalPersonality.BERSERKER && opponentState.currentHealth < 50f
        val isTankTurtle = !isPlayerAttacker && aiPersonality == AiTacticalPersonality.TANK
        val isDuelistCounter = !isPlayerAttacker && aiPersonality == AiTacticalPersonality.DUELIST
        val isTricksterFeint = !isPlayerAttacker && aiPersonality == AiTacticalPersonality.TRICKSTER && Random.nextFloat() < 0.28f

        val aiAttackBonus = when {
            isBerserk -> 7
            !isPlayerAttacker && aiPersonality == AiTacticalPersonality.AGGRESSOR -> 4
            !isPlayerAttacker && aiPersonality == AiTacticalPersonality.CROWD_PLAYER && crowdExcitement > 65 -> 3
            else -> 0
        }
        val aiDefenseBonus = when {
            isTankTurtle -> 5
            isDuelistCounter -> 4
            else -> 0
        }

        val atkLoadout = if (isPlayerAttacker) playerLoadout else opponentLoadout
        val defLoadout = if (isPlayerAttacker) opponentLoadout else playerLoadout

        val weapon = atkLoadout?.mainHand
        val weaponDmgBonus = (weapon?.effectiveDamage ?: 12) / 3
        val weaponPen = (weapon?.armorPenetrationPercent ?: 18) / 100f
        val reachBonus = if ((weapon?.reachCm ?: 65) > 100) 2 else 0

        val defShield = defLoadout?.shield
        val shieldBonus = (defShield?.effectiveBlock ?: 50) / 10
        val defMobilityPenalty = (defLoadout?.totalMobilityPenalty ?: 0) / 2
        val defArmorAbsorption = ((defLoadout?.totalProtection ?: 12) * (1f - weaponPen) * 0.35f).coerceAtLeast(1f)

        val attackPower = attacker.attributes.swordsmanship + (attacker.attributes.strength / 2) + weaponDmgBonus + reachBonus + (if (isRush) 5 else 0) + aiAttackBonus
        val defensePower = defender.attributes.shieldSkill + (defender.attributes.reflex / 2) + shieldBonus - defMobilityPenalty + aiDefenseBonus

        // Stamina drain influenced by weapon stamina cost and armor drain
        val baseStamina = weapon?.staminaCost ?: (if (isRush) 12f else if (isTankTurtle) 4f else 6f)
        val armorDrainFactor = 1f + ((atkLoadout?.totalStaminaDrainPercent ?: 0) / 100f)
        val staminaCost = baseStamina * armorDrainFactor
        attackerState.currentStamina = max(0f, attackerState.currentStamina - staminaCost)
        attackerState.isAttacking = true

        val hitRoll = attackPower + Random.nextInt(1, 20) + (if (isTricksterFeint) 4 else 0)
        val defRoll = defensePower + Random.nextInt(1, 20)

        if (hitRoll > defRoll + 4) {
            // Critical hit
            val rawDamage = (attacker.attributes.strength * 1.5f) + (weapon?.effectiveDamage?.toFloat() ?: 14f) + (if (isFinishAttempt) 15f else 5f)
            val damage = max(8f, (rawDamage - (defender.attributes.painTolerance * 0.3f) - defArmorAbsorption)).roundToInt().toFloat()
            defenderState.currentHealth = max(0f, defenderState.currentHealth - damage)
            defenderState.bloodLoss += 1.5f
            defenderState.isStaggered = true
            crowdExcitement = min(100, crowdExcitement + 10)
            bloodSplatters.add(ArenaBloodSplatter(defenderState.posX, defenderState.posY, radius = 16f, alpha = 0.85f))

            // Durability wear on critical hit
            weapon?.let { w -> w.currentDurability = max(0, w.currentDurability - 1) }
            defLoadout?.bodyArmor?.let { a -> a.currentDurability = max(0, a.currentDurability - 2) }
            defLoadout?.helmet?.let { h -> h.currentDurability = max(0, h.currentDurability - 2) }

            val weaponNote = weapon?.let { " [${it.name}]" } ?: ""
            if (isPlayerAttacker) {
                playerHits++
                playerCriticals++
                logs.add(CombatLogEntry(tick, "${attacker.name}$weaponNote, güçlü bir darbeyle savunmayı yardı! ($damage Hasar)", isCritical = true, isPlayerAction = true, bloodEffect = true))
            } else {
                logs.add(CombatLogEntry(tick, "${attacker.name}$weaponNote, ${defender.name}'nin zırhını deşip omzunu kesti! ($damage Hasar)", isCritical = true, isPlayerAction = false, bloodEffect = true))
            }
        } else if (hitRoll > defRoll) {
            // Standard hit
            val rawDmg = (attacker.attributes.strength * 0.8f) + ((weapon?.effectiveDamage ?: 12) * 0.6f) + Random.nextInt(1, 4)
            val damage = max(4f, (rawDmg - defArmorAbsorption)).roundToInt().toFloat()
            defenderState.currentHealth = max(0f, defenderState.currentHealth - damage)
            bloodSplatters.add(ArenaBloodSplatter(defenderState.posX, defenderState.posY, radius = 10f, alpha = 0.65f))

            weapon?.let { w -> w.currentDurability = max(0, w.currentDurability - 1) }
            defLoadout?.bodyArmor?.let { a -> a.currentDurability = max(0, a.currentDurability - 1) }

            if (isPlayerAttacker) {
                playerHits++
                logs.add(CombatLogEntry(tick, "${attacker.name}, ${defender.name}'ye kılıç darbesi indirdi. ($damage Hasar)", isPlayerAction = true))
            } else {
                logs.add(CombatLogEntry(tick, "${attacker.name} darbesini ulaştırdı. ($damage Hasar)", isPlayerAction = false))
            }
        } else {
            // Blocked / Parried
            defenderState.isBlocking = true
            defShield?.let { s ->
                s.currentDurability = max(0, s.currentDurability - Random.nextInt(1, 3))
                if (s.currentDurability == 0 && s.conditionState == EquipmentConditionState.BROKEN) {
                    logs.add(CombatLogEntry(tick, "KIRILMA! ${defender.name}'nin kalkanı çatlayıp parçalandı!", isCritical = true, bloodEffect = true))
                }
            }
            if (!isPlayerAttacker) {
                playerBlocks++
                logs.add(CombatLogEntry(tick, "${defender.name}, ${attacker.name}'nin atağını kalkanıyla savuşturdu!", isPlayerAction = true))
            } else {
                logs.add(CombatLogEntry(tick, "${defender.name} darbeyi kalkanıyla emdi.", isPlayerAction = false))
            }
        }
    }

    private fun calculateCrowdMercy(fallenFighter: Gladiator): Int {
        // Base mercy influenced by fighter's crowd favor, courage, and fight excitement
        val base = 40 + (fallenFighter.careerStats.crowdFavor / 4) + (crowdExcitement / 5) - (crowdBloodlust / 4)
        return min(95, max(15, base))
    }

    fun decidePolliceVerso(spareFighter: Boolean, usedBribe: Boolean = false) {
        val actualSpared = if (usedBribe) true else spareFighter
        polliceVersoState = polliceVersoState.copy(
            hasDecided = true,
            spared = actualSpared
        )
        if (actualSpared) {
            logs.add(CombatLogEntry(tick, "BAĞIŞLANDI! Kalabalık ve hakem başparmaklarını yukarı kaldırdı: Missio! Canı bağışlandı.", isCritical = true))
        } else {
            logs.add(CombatLogEntry(tick, "İDAMLIK! Başparmak aşağı çevrildi: Pollice Verso! Dövüşçü kumların üzerinde infaz edildi!", isCritical = true, bloodEffect = true))
        }
    }

    fun buildMatchResult(): MatchResult {
        val rawGold = 850 + (playerHits * 40) + (if (playerWon) 1200 else 200)
        val rawPrestige = if (playerWon) 150 else -30
        val baseGold = (rawGold * matchType.goldMultiplier).toInt()
        val basePrestige = (rawPrestige * matchType.prestigeMultiplier).toInt()
        val crowdDelta = if (playerWon) (8 * matchType.prestigeMultiplier).toInt() else -4

        var injury: Injury? = null
        if (playerState.currentHealth < 40f && Random.nextFloat() < 0.45f) {
            injury = Injury("inj_match_${tick}", "Kaburga Çatlağı", "Sert darbe sonucu kaburga hasarı.", 5, "Moderate", "-2 Endurance")
        }

        return MatchResult(
            playerWon = playerWon,
            opponentName = opponentGladiator.name,
            arenaName = arenaName,
            durationSeconds = tick,
            playerHits = playerHits,
            playerBlocks = playerBlocks,
            playerCriticals = playerCriticals,
            goldReward = baseGold,
            prestigeReward = basePrestige,
            crowdFavorDelta = crowdDelta,
            injurySuffered = injury,
            opponentSpared = polliceVersoState.spared
        )
    }
}
