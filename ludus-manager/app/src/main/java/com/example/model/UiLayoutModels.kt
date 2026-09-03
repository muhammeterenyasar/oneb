package com.example.model

import com.example.simulation.ActiveScreen

/**
 * Priority levels for the unified Attention / Inbox system.
 */
enum class AttentionPriority(val label: String, val colorHex: Long) {
    CRITICAL("Acil & Kritik", 0xFFB3261E), // Severe injury, death risk, license revocation
    IMPORTANT("Önemli", 0xFFD4AF37),       // Bout tomorrow, senator meeting, smith order ready
    RELEVANT("Gözden Geçir", 0xFF3B82F6),   // Recovery complete, low wheat, market opportunity
    BACKGROUND("Havadis", 0xFFB5A490)       // Normal daily background notices
}

/**
 * Actionable notification in the unified Attention / Inbox.
 */
data class AttentionItem(
    val id: String,
    val title: String,
    val message: String,
    val priority: AttentionPriority,
    val actionLabel: String,
    val targetScreen: ActiveScreen,
    val targetGladiatorId: String? = null,
    val actionKey: String? = null
)

/**
 * What Changed summary generated upon day advance.
 */
data class DayAdvanceSummary(
    val fromDay: Int,
    val toDay: Int,
    val recoveries: List<String> = emptyList(),
    val marketChanges: List<String> = emptyList(),
    val boutResults: List<String> = emptyList(),
    val politicalChanges: List<String> = emptyList(),
    val storyAlerts: List<String> = emptyList()
)

/**
 * Smart, contextual equipment / roster recommendations.
 */
data class SmartRecommendation(
    val id: String,
    val gladiatorId: String,
    val gladiatorName: String,
    val title: String,
    val reason: String,
    val actionType: String, // "REPAIR", "REST", "TRAIN", "UPGRADE_WEAPON"
    val costDenarii: Int = 0,
    val actionLabel: String
)

/**
 * Global search result across gladiators, NPCs, items, and arenas.
 */
data class GlobalSearchResult(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String, // "Gladyatör", "Şahsiyet / Senatör", "Teçhizat", "Arena"
    val targetScreen: ActiveScreen,
    val targetEntityId: String? = null
)

/**
 * Contextual tabs inside the unified Gladiator Profile.
 */
enum class GladiatorProfileTab(val title: String, val iconSymbol: String) {
    OVERVIEW("Genel Bakış", "👤"),
    TRAINING("Antrenman", "🏋"),
    EQUIPMENT("Teçhizat", "🛡"),
    MEDICAL("Revir & Sağlık", "🩸"),
    CONTRACT("Sözleşme & Sadakat", "📜"),
    RELATIONSHIPS("İlişkiler & Husumet", "⚔"),
    HISTORY("Kariyer & Tarihçe", "🏆")
}

/**
 * Contextual tabs inside the unified Ludus Management Hub.
 */
enum class LudusHubTab(val title: String, val iconSymbol: String) {
    FACILITIES("Tesisler", "🏛"),
    STAFF("Personel & Ustalar", "👥"),
    ECONOMY("Finans & Ambar", "💰")
}

/**
 * Contextual tabs inside the unified Market Hub.
 */
enum class MarketHubTab(val title: String, val iconSymbol: String) {
    RECRUITS("Köle & Gladyatörler", "⛓"),
    EQUIPMENT("Teçhizat & Tüccarlar", "⚔"),
    AUCTIONS("Müzayedeler", "🏛"),
    COMMISSIONS("Demirci Ocağı", "🔥")
}
