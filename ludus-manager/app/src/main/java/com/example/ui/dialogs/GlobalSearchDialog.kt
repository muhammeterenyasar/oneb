package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GlobalSearchResult
import com.example.simulation.ActiveScreen
import com.example.simulation.LudusUiState
import com.example.ui.theme.*

/**
 * Global Quick Search Dialog allowing instant location of Gladiators, NPCs, Arenas, and Items.
 */
@Composable
fun GlobalSearchDialog(
    state: LudusUiState,
    onNavigateToResult: (ActiveScreen, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val results = remember(searchQuery) {
        if (searchQuery.isBlank() || searchQuery.length < 2) emptyList()
        else {
            val query = searchQuery.trim().lowercase()
            val list = mutableListOf<GlobalSearchResult>()

            // 1. Gladiators
            state.gladiators.filter { it.name.lowercase().contains(query) || it.nickname.lowercase().contains(query) || it.gladiatorClass.title.lowercase().contains(query) }.forEach { glad ->
                list.add(
                    GlobalSearchResult(
                        id = glad.id,
                        title = glad.name,
                        subtitle = "${glad.gladiatorClass.title} • Sağlık: %${glad.condition.health}",
                        category = "Kışla Gladyatörü",
                        targetScreen = ActiveScreen.ROSTER,
                        targetEntityId = glad.id
                    )
                )
            }

            // 2. Persistent Circuit Fighters
            state.persistentFighters.filter { it.name.lowercase().contains(query) || it.nickname.lowercase().contains(query) }.take(5).forEach { pf ->
                list.add(
                    GlobalSearchResult(
                        id = pf.id,
                        title = "${pf.name} '${pf.nickname}'",
                        subtitle = "${pf.ludusAffiliation} • ${pf.currentArena.venueName}",
                        category = "Rakip Gladyatör",
                        targetScreen = ActiveScreen.ARENA_HUB,
                        targetEntityId = pf.id
                    )
                )
            }

            // 3. Political Figures
            state.politicalCharacters.filter { it.name.lowercase().contains(query) || it.title.lowercase().contains(query) }.forEach { pc ->
                list.add(
                    GlobalSearchResult(
                        id = pc.id,
                        title = pc.name,
                        subtitle = pc.title,
                        category = "Senatör / Nüfuzlu Şahsiyet",
                        targetScreen = ActiveScreen.POLITICS,
                        targetEntityId = pc.id
                    )
                )
            }

            // 4. Merchants
            state.merchants.filter { it.name.lowercase().contains(query) || it.shopName.lowercase().contains(query) }.forEach { merch ->
                list.add(
                    GlobalSearchResult(
                        id = merch.id,
                        title = merch.name,
                        subtitle = merch.shopName,
                        category = "Forum Tüccarı",
                        targetScreen = ActiveScreen.EQUIPMENT_MARKET,
                        targetEntityId = merch.id
                    )
                )
            }

            // 5. Arenas
            state.cities.filter { it.cityName.lowercase().contains(query) || it.arenaName.lowercase().contains(query) }.forEach { city ->
                list.add(
                    GlobalSearchResult(
                        id = city.id,
                        title = city.arenaName,
                        subtitle = city.cityName,
                        category = "Roma Arenası",
                        targetScreen = ActiveScreen.ARENA_HUB,
                        targetEntityId = city.id
                    )
                )
            }

            list
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(480.dp)
                .height(380.dp)
                .background(Color(0xFF1E1713), RoundedCornerShape(6.dp))
                .border(1.2.dp, RomanGold, RoundedCornerShape(6.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Header & Search Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "HIZLI ROMA ARAMASI", color = RomanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = RomanGold)
                    }
                }

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Gladyatör, Senatör, Tüccar veya Arena ara...", fontSize = 10.sp, color = RomanParchmentDark) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2B2019),
                        unfocusedContainerColor = Color(0xFF221814),
                        focusedTextColor = RomanGold,
                        unfocusedTextColor = RomanParchment,
                        focusedIndicatorColor = RomanGold,
                        unfocusedIndicatorColor = RomanBronzeDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .border(0.8.dp, RomanBronze, RoundedCornerShape(3.dp))
                )

                // Results
                if (searchQuery.length >= 2 && results.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "'$searchQuery' ile eşleşen kayıt bulunamadı.", color = RomanTextSecondary, fontSize = 10.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(results) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF251C17), RoundedCornerShape(2.dp))
                                    .border(0.6.dp, RomanBronzeDark, RoundedCornerShape(2.dp))
                                    .clickable {
                                        onNavigateToResult(item.targetScreen, item.targetEntityId)
                                        onDismiss()
                                    }
                                    .padding(6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = item.title, color = RomanGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    Text(text = item.subtitle, color = RomanParchment, fontSize = 8.5.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF191310), RoundedCornerShape(2.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = item.category, color = RomanGoldLight, fontSize = 7.5.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
