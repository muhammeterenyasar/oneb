package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Gladiator
import com.example.simulation.LudusUiState
import com.example.ui.components.GladiatorAvatarCanvas
import com.example.ui.components.GladiatorMiniSprite
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.theme.*

@Composable
fun RecruitmentScreen(
    state: LudusUiState,
    onPurchaseGladiator: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Market Gladiator Cards
        Column(
            modifier = Modifier
                .weight(2.2f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(
                title = "Capua Köle & Gladyatör Pazarı",
                badge = "${state.marketGladiators.size} Dövüşçü Müzayedede"
            ) {
                if (state.marketGladiators.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Bu haftaki pazar kapandı. Yeni kafileler sonraki günlerde gelecek.",
                            color = RomanTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    state.marketGladiators.forEach { glad ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF241A14), RoundedCornerShape(3.dp))
                                .border(0.6.dp, RomanBronze, RoundedCornerShape(3.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GladiatorMiniSprite(glad.gladiatorClass, 48.dp)

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${glad.name} (Yaş: ${glad.age})",
                                        color = RomanGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "1.500 Denarii",
                                        color = RomanGoldLight,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Sınıf: ${glad.gladiatorClass.title} | Köken: ${glad.origin.region} | Boy: ${glad.heightCm}cm",
                                    color = RomanParchmentDark,
                                    fontSize = 9.5.sp
                                )
                                Text(
                                    text = "Kişilik: ${glad.personality.label} (${glad.personality.effect})",
                                    color = RomanTextSecondary,
                                    fontSize = 9.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(text = "Güç: ${glad.attributes.strength}/20", color = RomanCrimson, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "Hız: ${glad.attributes.speed}/20", color = RomanWarningAmber, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = "Kılıç: ${glad.attributes.swordsmanship}/20", color = RomanGold, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Button(
                                onClick = { onPurchaseGladiator(glad.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RomanCrimson,
                                    contentColor = RomanParchment
                                ),
                                shape = RoundedCornerShape(3.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .border(0.8.dp, RomanGold, RoundedCornerShape(3.dp)),
                                contentPadding = PaddingValues(horizontal = 10.dp)
                            ) {
                                Text(text = "SATIN AL", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        // Market Rules & Budget Panel
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(title = "Müzayede Bütçesi") {
                Text(
                    text = "Mevcut Kasa: %,d Denarii".format(state.dominus.denarii),
                    color = RomanGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Aylık Bakım Maliyeti: ~${state.gladiators.size * 50} Denarii",
                    color = RomanTextSecondary,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "KÖLE TÜCCARI İPUÇLARI",
                    color = RomanGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• Trakya ve Galya köleleri doğuştan savaşçıdır ve yüksek güce sahiptir.\n• Retiarius sınıfı dövüşçüler çeviklik ve refleks ister.\n• Sadakati düşük olanlar gece firar edebilir.",
                    color = RomanTextSecondary,
                    fontSize = 9.5.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
