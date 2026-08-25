package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppScreen(
    val route: String,
    val title: String,
    val shortTitle: String,
    val icon: ImageVector,
    val subtitle: String,
    val isPrimaryBottomNav: Boolean = true
) {
    LUDUS_OVERVIEW(
        route = "ludus_overview",
        title = "Ludus Overview",
        shortTitle = "Ludus",
        icon = Icons.Default.AccountBalance,
        subtitle = "Karargah & Yönetim"
    ),
    TRAINING_ARENA(
        route = "training_arena",
        title = "Training Arena",
        shortTitle = "Antrenman",
        icon = Icons.Default.FitnessCenter,
        subtitle = "Talim & Kadro"
    ),
    PHYSICIAN_TENT(
        route = "physician_tent",
        title = "Physician Tent",
        shortTitle = "Hekim Çadırı",
        icon = Icons.Default.Healing,
        subtitle = "Şifahane & İyileşme"
    ),
    MATCH_LOBBY(
        route = "match_lobby",
        title = "Match Lobby",
        shortTitle = "Arena Lobisi",
        icon = Icons.Default.SportsMartialArts,
        subtitle = "Müsabaka & Dövüş"
    ),
    SCHEDULED_FIGHTS(
        route = "scheduled_fights",
        title = "Scheduled Fights",
        shortTitle = "Müsabaka Takvimi",
        icon = Icons.Default.CalendarMonth,
        subtitle = "Gelecek Dövüşler & Planlama",
        isPrimaryBottomNav = false
    ),
    MARKET_AND_FACILITIES(
        route = "market_facilities",
        title = "Market & Facilities",
        shortTitle = "Pazar & Tesis",
        icon = Icons.Default.Gavel,
        subtitle = "Köle & Tefeci"
    ),
    IMPERIAL_SHOP(
        route = "imperial_shop",
        title = "Imperial Shop",
        shortTitle = "Mağaza",
        icon = Icons.Default.ShoppingBag,
        subtitle = "Bağış & Şifa",
        isPrimaryBottomNav = false
    ),
    IMPERIAL_CAMPAIGN(
        route = "imperial_campaign",
        title = "Imperial Campaign",
        shortTitle = "Sefer",
        icon = Icons.Default.Flag,
        subtitle = "Roma Seferi & Boss Dövüşleri",
        isPrimaryBottomNav = false
    ),
    ARMORY_EQUIPMENT(
        route = "armory_equipment",
        title = "Armory & Blacksmith",
        shortTitle = "Cephanelik",
        icon = Icons.Default.Shield,
        subtitle = "Silah, Zırh & Tılsım",
        isPrimaryBottomNav = false
    ),
    HALL_OF_FAME(
        route = "hall_of_fame",
        title = "Pantheon & Efsaneler Salonu",
        shortTitle = "Efsaneler",
        icon = Icons.Default.EmojiEvents,
        subtitle = "Emekli Gladyatörler & Onur Anıtı",
        isPrimaryBottomNav = false
    ),
    RIVAL_LEAGUE(
        route = "rival_league",
        title = "İmparatorluk Gladyatör Ligi",
        shortTitle = "Lig Tablosu",
        icon = Icons.Default.Leaderboard,
        subtitle = "Roma Okulları & Sıralama",
        isPrimaryBottomNav = false
    );


    companion object {
        fun fromRoute(route: String?): AppScreen {
            if (route == null) return LUDUS_OVERVIEW
            val clean = route.removePrefix("#").removePrefix("/").trim()
            return entries.find { it.route.equals(clean, ignoreCase = true) } ?: LUDUS_OVERVIEW
        }
    }
}
