package com.example.model

enum class CombatStance(val label: String, val desc: String) {
    BALANCED("Balanced (Dengeli)", "Equal focus on blade strikes and shield deflection"),
    AGGRESSIVE("Aggressive (Hücum)", "High strike frequency, elevated critical risk & fatigue"),
    DEFENSIVE("Defensive (Savunma)", "Turtle behind shield, minimize incoming damage"),
    DISTANCE("Keep Distance (Mesafe Koru)", "Circle the perimeter, exhaust opponent before striking"),
    COUNTER("Counter Strike (Karşı Saldırı)", "Wait for opponent overextension to punish")
}

enum class CombatTarget(val label: String) {
    BLEED_DRAIN("Bleed & Drain (Kanatma / Yıpratma)"),
    SHIELD_CRUSH("Shield Crush (Kalkana Odaklan)"),
    CRITICAL_FINISH("Vital Strike / Finish (Bitirici Vuruş)"),
    PLAY_CROWD("Play to Crowd (Kalabalığı Kışkırt)")
}

enum class TacticalCommand(val title: String, val shortDesc: String, val cooldownSeconds: Int) {
    RAISE_SHIELD("Kalkanın Arkasına Geç", "Block high incoming blow", 4),
    KEEP_DISTANCE("Mesafe Koru", "Disengage & regain breath", 5),
    RUSH_ATTACK("Hücum Et!", "Charge with full body weight", 6),
    FALL_BACK("Geri Çekil", "Break clinch & step back", 4),
    COUNTER_STRIKE("Karşı Saldırı", "Parry and riposte immediately", 5),
    FINISH_HIM("Kan Gör / Bitir!", "Unleash decisive lethal combo", 8),
    TAUNT_CROWD("Kalabalığı Coştur", "Pump fist; boost crowd favor & morale", 7)
}

data class CombatLogEntry(
    val timeSeconds: Int,
    val text: String,
    val isCritical: Boolean = false,
    val isPlayerAction: Boolean = true,
    val bloodEffect: Boolean = false
)

data class ArenaBloodSplatter(
    val x: Float,
    val y: Float,
    val radius: Float = 14f,
    val alpha: Float = 0.65f
)

data class CombatFighterState(
    val gladiator: Gladiator,
    var currentHealth: Float,
    var currentStamina: Float,
    var bloodLoss: Float = 0f,
    var pain: Float = 0f,
    var stress: Float = 10f,
    var posX: Float = 0.32f,
    var posY: Float = 0.50f,
    var facingAngle: Float = 0f,
    var isAttacking: Boolean = false,
    var isBlocking: Boolean = false,
    var isStaggered: Boolean = false,
    var isDown: Boolean = false,
    var activeCommand: TacticalCommand? = null
)

data class PolliceVersoState(
    val isActive: Boolean = false,
    val fallenFighterName: String = "",
    val isPlayerFallen: Boolean = false,
    val crowdMercyPercent: Int = 50,
    val hasDecided: Boolean = false,
    val spared: Boolean = false
)

data class MatchResult(
    val playerWon: Boolean,
    val opponentName: String,
    val arenaName: String,
    val durationSeconds: Int,
    val playerHits: Int,
    val playerBlocks: Int,
    val playerCriticals: Int,
    val goldReward: Int,
    val prestigeReward: Int,
    val crowdFavorDelta: Int,
    val injurySuffered: Injury?,
    val opponentSpared: Boolean
)
