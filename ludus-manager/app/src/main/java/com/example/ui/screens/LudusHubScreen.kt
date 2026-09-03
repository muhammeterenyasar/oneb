package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Facility
import com.example.model.LudusHubTab
import com.example.simulation.LudusUiState
import com.example.ui.components.RomanCard
import com.example.ui.theme.*

/**
 * Unified Ludus Management Hub (Kışla & İdare).
 * Consolidates Facilities, Staff, and Economy into 3 clean, accessible tabs.
 */
@Composable
fun LudusHubScreen(
    state: LudusUiState,
    onUpgradeFacility: (Facility) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(LudusHubTab.FACILITIES) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Sub-navigation bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1713), RoundedCornerShape(4.dp))
                .border(0.8.dp, RomanBronzeDark, RoundedCornerShape(4.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LudusHubTab.values().forEach { tab ->
                val isSelected = tab == activeTab
                Button(
                    onClick = { activeTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) RomanCrimson else Color(0xFF281F19),
                        contentColor = if (isSelected) RomanGold else RomanParchment
                    ),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .border(
                            0.8.dp,
                            if (isSelected) RomanGold else RomanBronzeDark,
                            RoundedCornerShape(3.dp)
                        ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "${tab.iconSymbol} ${tab.title}",
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Tab Body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (activeTab) {
                LudusHubTab.FACILITIES -> FacilitiesTabContent(state, onUpgradeFacility)
                LudusHubTab.STAFF -> StaffTabContent(state)
                LudusHubTab.ECONOMY -> EconomyTabContent(state)
            }
        }
    }
}

@Composable
private fun FacilitiesTabContent(
    state: LudusUiState,
    onUpgradeFacility: (Facility) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "Kışla Binaları & Altyapı", badge = "${state.facilities.size} Tesis") {
                state.facilities.forEach { facility ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF241A14), RoundedCornerShape(3.dp))
                            .border(0.6.dp, RomanBronze, RoundedCornerShape(3.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = facility.name, color = RomanGold, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Seviye ${facility.level} / ${facility.maxLevel}", color = RomanGoldLight, fontSize = 9.5.sp)
                            }
                            Text(text = facility.description, color = RomanParchment, fontSize = 9.sp)
                            Text(text = "Mevcut Etki: ${facility.currentBonus}", color = RomanSuccessGreen, fontSize = 9.sp)
                            if (facility.level < facility.maxLevel) {
                                Text(text = "Sonraki Seviye: ${facility.nextBonus}", color = RomanTextSecondary, fontSize = 8.5.sp)
                            }
                        }

                        if (facility.level < facility.maxLevel) {
                            Button(
                                onClick = { onUpgradeFacility(facility) },
                                enabled = state.dominus.denarii >= facility.upgradeCost,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RomanCrimson,
                                    disabledContainerColor = Color(0xFF281F19),
                                    contentColor = RomanParchment
                                ),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.height(30.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text(text = "Yükselt (${facility.upgradeCost} D)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(text = "AZAMİ SEVİYE", color = RomanGoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
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
            RomanCard(title = "İnşaat ve Genişleme") {
                Text(text = "Tesis yatırımları gladyatörlerinizin sakatlanma oranını düşürür, antrenman verimini katlar ve kışlanın prestijini artırır.", color = RomanParchment, fontSize = 9.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Mevcut Kasa: ${state.dominus.denarii} Denarii", color = RomanGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Text(text = "Tahıl Ambarı: ${state.dominus.foodWheat} Ölçek", color = RomanGoldLight, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun StaffTabContent(state: LudusUiState) {
    Row(
        modifier = Modifier.fillMaxSize(),
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
                            Text(text = "${member.name} (${member.role})", color = RomanGold, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Uzmanlık: ${member.specialty} | Seviye: ${member.level}", color = RomanParchmentDark, fontSize = 9.sp)
                            Text(text = "Aktif Etki: ${member.perkDescription}", color = RomanSuccessGreen, fontSize = 9.sp)
                        }
                        Text(text = "${member.monthlyWage} Denarii/ay", color = RomanGoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
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
                Text(text = "Toplam Personel Gideri:", color = RomanTextSecondary, fontSize = 9.5.sp)
                Text(text = "$totalWages Denarii / ay", color = RomanDangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Personel Sadakati: %94", color = RomanSuccessGreen, fontSize = 10.sp)
                Text(text = "Düzenli maaş ödemek firarları ve rüşvet skandallarını önler.", color = RomanTextMuted, fontSize = 8.5.sp)
            }
        }
    }
}

@Composable
private fun EconomyTabContent(state: LudusUiState) {
    Row(
        modifier = Modifier.fillMaxSize(),
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
                Text(text = "• Arena Maç Ödülleri: ~2.400 Denarii / ay", color = RomanSuccessGreen, fontSize = 9.5.sp)
                Text(text = "• Hami & Senatör Destekleri: +750 Denarii / ay", color = RomanSuccessGreen, fontSize = 9.5.sp)
                Text(text = "• Özel Dövüş Gösterileri: +400 Denarii / ay", color = RomanSuccessGreen, fontSize = 9.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Toplam Tahmini Gelir: +3.550 Denarii", color = RomanGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
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
                Text(text = "• Gladyatör & Personel Maaşları: -950 Denarii / ay", color = RomanDangerRed, fontSize = 9.5.sp)
                Text(text = "• Arpa, Et & Şarap İaşesi: -450 Denarii / ay", color = RomanDangerRed, fontSize = 9.5.sp)
                Text(text = "• Tıbbi Malzeme & Şifa Otları: -180 Denarii / ay", color = RomanDangerRed, fontSize = 9.5.sp)
                Text(text = "• Tesis Bakım Masrafı: -200 Denarii / ay", color = RomanDangerRed, fontSize = 9.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Toplam Tahmini Gider: -1.780 Denarii", color = RomanDangerRed, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "Net Bakiye & Ambar") {
                Text(text = "Aylık Net Kâr: +1.770 Denarii", color = RomanSuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Mevcut Kasa: ${state.dominus.denarii} Denarii", color = RomanGold, fontSize = 10.sp)
                Text(text = "Tahıl Ambarı: ${state.dominus.foodWheat} Ölçek Buğday", color = RomanGoldLight, fontSize = 10.sp)
                Text(text = "Kışla iaşesi 45 gün boyunca güvence altında.", color = RomanParchment, fontSize = 8.5.sp)
            }
        }
    }
}
