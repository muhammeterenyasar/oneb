package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simulation.LudusUiState
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.theme.*

// -------------------------------------------------------------
// 1. STAFF & SERVICES SCREEN
// -------------------------------------------------------------
@Composable
fun StaffScreen(
    state: LudusUiState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "Ludus Personeli & Hizmetkârlar", badge = "${state.staff.size} Uzman") {
                state.staff.forEach { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF241A14), RoundedCornerShape(3.dp))
                            .border(0.6.dp, RomanBronze, RoundedCornerShape(3.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "${member.name} (${member.role})", color = RomanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Uzmanlık: ${member.specialty} | Seviye: ${member.level}", color = RomanParchmentDark, fontSize = 9.5.sp)
                            Text(text = "Aktif Etki: ${member.perkDescription}", color = RomanSuccessGreen, fontSize = 9.5.sp)
                        }
                        Text(text = "${member.monthlyWage} Denarii/ay", color = RomanGoldLight, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "Maaş Bordrosu") {
                val totalWages = state.staff.sumOf { it.monthlyWage }
                Text(text = "Toplam Personel Gideri:", color = RomanTextSecondary, fontSize = 10.sp)
                Text(text = "$totalWages Denarii / ay", color = RomanDangerRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Sadakat Seviyesi: %92", color = RomanSuccessGreen, fontSize = 10.5.sp)
                Text(text = "Personelinize düzenli maaş ödemek firarları ve rüşvet skandallarını önler.", color = RomanTextMuted, fontSize = 9.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// 2. POLITICS SCREEN (Moved to dedicated full simulation in PoliticsScreen.kt)
// -------------------------------------------------------------


// -------------------------------------------------------------
// 3. WORLD MAP & PROGRESSION SCREEN
// -------------------------------------------------------------
@Composable
fun WorldMapScreen(
    state: LudusUiState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "İtalya Yarımadası & Arena İlerlemesi", badge = "4 Bölge") {
                state.cities.forEach { city ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (city.isUnlocked) Color(0xFF2E1C15) else Color(0xFF191412),
                                RoundedCornerShape(3.dp)
                            )
                            .border(0.6.dp, if (city.isUnlocked) RomanGold else RomanBronzeDark, RoundedCornerShape(3.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = city.cityName, color = if (city.isUnlocked) RomanGold else RomanParchmentDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                if (city.tier == 1) {
                                    Text(text = "✓ AKTİF", color = RomanSuccessGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(text = "${city.arenaName} (Çarpan: x${city.purseMultiplier})", color = RomanParchment, fontSize = 10.sp)
                            Text(text = "Gereken Prestij: ${city.prestigeRequired} | Seviye: ${city.tier}", color = RomanTextSecondary, fontSize = 9.sp)
                            Text(text = city.description, color = RomanTextMuted, fontSize = 8.5.sp)
                        }

                        Box(
                            modifier = Modifier
                                .background(if (city.isUnlocked) RomanCrimson else RomanBronzeDark, RoundedCornerShape(2.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = if (city.isUnlocked) "AÇIK" else "KİLİTLİ", color = RomanParchment, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "Nihai Hedef: Roma Colosseum") {
                Text(text = "Amphitheatrum Flavium", color = RomanGold, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                Text(text = "50.000 Romalının önünde, İmparatorun locasının hemen altında ludusunuzu dünyanın en büyüğü yapmak için Capua ve Pompeii arenalarını fethedin.", color = RomanTextSecondary, fontSize = 9.5.sp, lineHeight = 14.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// 4. CHRONICLE (ANNALS) SCREEN
// -------------------------------------------------------------
@Composable
fun ChronicleScreen(
    state: LudusUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp)
    ) {
        RomanCard(
            title = "Ludus Tarihçesi & Kronikleri (The Annals of Blood)",
            badge = "${state.chronicles.size} Kayıt",
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.chronicles) { entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF221814), RoundedCornerShape(3.dp))
                            .border(0.6.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(if (entry.isGlory) RomanSuccessGreen.copy(alpha = 0.2f) else RomanDangerRed.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .border(1.dp, if (entry.isGlory) RomanSuccessGreen else RomanDangerRed, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (entry.isGlory) "VICT" else "FAT", color = if (entry.isGlory) RomanSuccessGreen else RomanDangerRed, fontSize = 7.sp, fontWeight = FontWeight.Black)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = entry.title, color = RomanGold, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${entry.yearAUC}", color = RomanParchmentDark, fontSize = 9.5.sp)
                            }
                            Text(text = entry.description, color = RomanParchment, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. ECONOMY & FINANCIAL LEDGER SCREEN
// -------------------------------------------------------------
@Composable
fun EconomyScreen(
    state: LudusUiState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "Gelir Kalemleri (Revenue)") {
                Text(text = "• Arena Maç Ödülleri: ~2.400 Denarii / ay", color = RomanSuccessGreen, fontSize = 10.sp)
                Text(text = "• Hami & Senatör Destekleri: +750 Denarii / ay", color = RomanSuccessGreen, fontSize = 10.sp)
                Text(text = "• Özel Dövüş Gösterileri: +400 Denarii / ay", color = RomanSuccessGreen, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Toplam Tahmini Gelir: +3.550 Denarii", color = RomanGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "Gider Kalemleri (Expenses)") {
                Text(text = "• Gladyatör & Personel Maaşları: -950 Denarii / ay", color = RomanDangerRed, fontSize = 10.sp)
                Text(text = "• Arpa, Et & Şarap İaşesi: -450 Denarii / ay", color = RomanDangerRed, fontSize = 10.sp)
                Text(text = "• Tıbbi Malzeme & Şifa Otları: -180 Denarii / ay", color = RomanDangerRed, fontSize = 10.sp)
                Text(text = "• Tesis Bakım Masrafı: -200 Denarii / ay", color = RomanDangerRed, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Toplam Tahmini Gider: -1.780 Denarii", color = RomanDangerRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "Net Kasa Durumu") {
                Text(text = "Mevcut Hazine:", color = RomanTextSecondary, fontSize = 10.sp)
                Text(text = "%,d Denarii".format(state.dominus.denarii), color = RomanGoldLight, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Aylık Net Kâr: +1.770 Denarii", color = RomanSuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "Hazine güvende. İflas veya açlık riski bulunmuyor.", color = RomanParchmentDark, fontSize = 9.sp)
            }
        }
    }
}
