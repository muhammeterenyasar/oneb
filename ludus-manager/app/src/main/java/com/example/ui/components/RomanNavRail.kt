package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simulation.ActiveScreen
import com.example.ui.theme.*

data class NavItem(
    val screen: ActiveScreen,
    val title: String,
    val icon: ImageVector
)

@Composable
fun RomanNavRail(
    currentScreen: ActiveScreen,
    onNavigate: (ActiveScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(ActiveScreen.DASHBOARD, "Ana Sayfa", Icons.Default.Home),
        NavItem(ActiveScreen.ROSTER, "Gladyatörler", Icons.Default.Groups),
        NavItem(ActiveScreen.FACILITIES, "Kışla (Ludus)", Icons.Default.Castle),
        NavItem(ActiveScreen.ARENA_HUB, "Arena & Lig", Icons.Default.Shield),
        NavItem(ActiveScreen.EQUIPMENT_MARKET, "Roma Pazarı", Icons.Default.ShoppingCart),
        NavItem(ActiveScreen.POLITICS, "Politika", Icons.Default.AccountBalance),
        NavItem(ActiveScreen.CHRONICLE, "Tarihçe", Icons.Default.HistoryEdu)
    )

    Column(
        modifier = modifier
            .width(112.dp)
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1E1713), Color(0xFF140F0D))
                )
            )
            .border(width = 1.dp, color = RomanBronzeDark)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            val isSelected = when (item.screen) {
                ActiveScreen.DASHBOARD -> currentScreen == ActiveScreen.DASHBOARD
                ActiveScreen.ROSTER -> currentScreen in listOf(ActiveScreen.ROSTER, ActiveScreen.GLADIATOR_PROFILE, ActiveScreen.TRAINING, ActiveScreen.MEDICAL)
                ActiveScreen.FACILITIES -> currentScreen in listOf(ActiveScreen.FACILITIES, ActiveScreen.STAFF, ActiveScreen.ECONOMY)
                ActiveScreen.ARENA_HUB -> currentScreen in listOf(ActiveScreen.ARENA_HUB, ActiveScreen.MATCH_PREP, ActiveScreen.WORLD_MAP)
                ActiveScreen.EQUIPMENT_MARKET -> currentScreen in listOf(ActiveScreen.EQUIPMENT_MARKET, ActiveScreen.RECRUITMENT)
                ActiveScreen.POLITICS -> currentScreen == ActiveScreen.POLITICS
                ActiveScreen.CHRONICLE -> currentScreen == ActiveScreen.CHRONICLE
                else -> currentScreen == item.screen
            }
            val bgBrush = if (isSelected) {
                Brush.horizontalGradient(
                    listOf(RomanCrimson, RomanDarkCrimson)
                )
            } else {
                Brush.horizontalGradient(
                    listOf(Color.Transparent, Color.Transparent)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(38.dp)
                    .background(bgBrush, RoundedCornerShape(3.dp))
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) RomanGold else Color.Transparent,
                        shape = RoundedCornerShape(3.dp)
                    )
                    .clickable { onNavigate(item.screen) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) RomanGold else RomanParchmentDark,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = item.title,
                        color = if (isSelected) RomanGoldLight else RomanTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
