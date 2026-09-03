package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simulation.LudusUiState
import com.example.ui.components.RomanCard
import com.example.ui.theme.*

@Composable
fun FacilitiesScreen(
    state: LudusUiState,
    onUpgradeFacility: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Facilities Grid
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(
                title = "Ludus Yapıları ve Tesisler (Architecture & Upgrades)",
                badge = "${state.facilities.size} Tesis"
            ) {
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = facility.name,
                                    color = RomanGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Seviye ${facility.level} / ${facility.maxLevel}",
                                    color = RomanParchmentDark,
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = facility.description,
                                color = RomanTextSecondary,
                                fontSize = 9.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mevcut Verim: ${facility.currentBonus}",
                                color = RomanSuccessGreen,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Sonraki Seviye: ${facility.nextBonus}",
                                color = RomanGoldLight,
                                fontSize = 9.sp
                            )
                        }

                        Button(
                            onClick = { onUpgradeFacility(facility.id) },
                            enabled = facility.level < facility.maxLevel && state.dominus.denarii >= facility.upgradeCost,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RomanCrimson,
                                contentColor = RomanParchment
                            ),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .border(0.8.dp, RomanGold, RoundedCornerShape(3.dp)),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = if (facility.level < facility.maxLevel) "YÜKSELT (${facility.upgradeCost} 💰)" else "MAKSİMUM",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        // Architectural Lore & Status
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(title = "İnşaat Ustabaşısı") {
                Text(
                    text = "Mühendis Tiberius",
                    color = RomanGold,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tesisleri geliştirmek gladyatörlerin sakatlanma riskini azaltır, antrenman hızını %30'a kadar artırır ve isyan tehlikesini önler.",
                    color = RomanTextSecondary,
                    fontSize = 9.5.sp,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "GEREKSİNİMLER",
                    color = RomanGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• Roma çimentosu ve kireçtaşı temini\n• Ahşap ve demir ocakları desteği\n• Yeterli Denarii sermayesi",
                    color = RomanTextSecondary,
                    fontSize = 9.5.sp
                )
            }
        }
    }
}
