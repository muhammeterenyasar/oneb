package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.MainUiState
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun LudusShopScreen(
    state: MainUiState,
    onBuyInstantHeal: (Long) -> Unit,
    onBuyExpansion: () -> Unit,
    onClaimRewardedAd: () -> Unit,
    onBuySenatorSponsorship: () -> Unit,
    onRepayDebt: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val injuredGladiator = state.gladiators.firstOrNull { it.isInjured }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBg)
            .padding(horizontal = 16.dp)
            .testTag("ludus_shop_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Header Banner: IAP & Shop Policy Note
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCardBg),
                border = BorderStroke(1.2.dp, ImmersiveGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ImmersiveTerracotta.copy(alpha = 0.25f),
                                    ImmersiveCardBg
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏛️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LUDUS MAĞAZASI & GÜÇLENDİRMELER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = ImmersiveGold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tasarım İlkesi: Ürünler zaman kazandırır, riskleri azaltır veya prestij sağlar; doğrudan dövüşü kazanma gücü satmaz.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ImmersiveTextMuted
                    )
                }
            }
        }

        // 1. Rewarded Ad (İzle - Kazan)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCardBg),
                border = BorderStroke(1.dp, ImmersiveSuccess.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🎬", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "İzle - Kazan (Çift Ödül)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveGold
                            )
                        }
                        Text(
                            text = "+120 Altın ve +15 Prestij kazandırır. Tamamen opsiyoneldir.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ImmersiveTextMuted
                        )
                    }

                    Button(
                        onClick = onClaimRewardedAd,
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveSuccess.copy(alpha = 0.3f), contentColor = ImmersiveSuccess),
                        border = BorderStroke(1.dp, ImmersiveSuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("claim_rewarded_ad_button")
                    ) {
                        Text(text = "İzle (+Ödül)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // 2. Zaman Kısayolu: Anında İyileşme İksiri
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCardBg),
                border = BorderStroke(1.dp, ImmersiveBorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🧪 Anında İyileşme İksiri",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveGold
                        )
                        Text(
                            text = "Devam eden revir tedavisini sıfırlar ve gladyatörü anında tam canla arenaya hazır eder.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ImmersiveTextMuted
                        )
                        if (injuredGladiator != null) {
                            Text(
                                text = "Hedef: ${injuredGladiator.name} (${injuredGladiator.injurySeverity.displayName})",
                                style = MaterialTheme.typography.labelSmall,
                                color = ImmersiveTerracottaLight
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (injuredGladiator != null) {
                                onBuyInstantHeal(injuredGladiator.id)
                            }
                        },
                        enabled = injuredGladiator != null && state.ludusState.gold >= 75,
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("buy_instant_heal_button")
                    ) {
                        Text(
                            text = "75 🪙 Al",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // 3. Ludus Yuva Genişletme
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCardBg),
                border = BorderStroke(1.dp, ImmersiveBorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🏰 Ludus Genişletme (+2 Yuva)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveGold
                        )
                        Text(
                            text = "Aynı anda barındırılabilecek gladyatör üst limitini artırır. Mevcut: ${state.ludusState.maxGladiatorSlots} Yuva.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ImmersiveTextMuted
                        )
                    }

                    Button(
                        onClick = onBuyExpansion,
                        enabled = state.ludusState.gold >= 300,
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("buy_expansion_button")
                    ) {
                        Text(
                            text = "300 🪙 Al",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // 4. Senatör Hamiliği (Sponsorluk)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCardBg),
                border = BorderStroke(1.dp, ImmersiveBorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📜 Senatör Sponsorluğu (14 Gün)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = ImmersiveGold
                        )
                        Text(
                            text = "Her gün kasanıza +60 Altın patron fonu yatar. Gereksinim: 120 Prestij.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ImmersiveTextMuted
                        )
                        if (state.ludusState.senatorSponsorshipDays > 0) {
                            Text(
                                text = "Aktif: ${state.ludusState.senatorSponsorshipDays} Gün kaldı",
                                style = MaterialTheme.typography.labelSmall,
                                color = ImmersiveSuccess
                            )
                        }
                    }

                    Button(
                        onClick = onBuySenatorSponsorship,
                        enabled = state.ludusState.prestige >= 120 && state.ludusState.gold >= 150 && state.ludusState.senatorSponsorshipDays == 0,
                        colors = ButtonDefaults.buttonColors(containerColor = ImmersiveTerracotta, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("buy_senator_sponsorship_button")
                    ) {
                        Text(
                            text = "150 🪙 İmzala",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // 5. Borç Kurtarma (Tefeci Borcunu Kapat)
        if (state.ludusState.activeDebt > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ImmersiveTerracotta.copy(alpha = 0.15f)),
                    border = BorderStroke(1.2.dp, ImmersiveTerracotta)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🛡️ Tefeci Borcunu Acil Sıfırla",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = ImmersiveGold
                            )
                            Text(
                                text = "Aktif borcu (${state.ludusState.activeDebt} 🪙) kapatır ve sızma/suikast tehditlerini anında iptal eder.",
                                style = MaterialTheme.typography.bodySmall,
                                color = ImmersiveTextMuted
                            )
                        }

                        Button(
                            onClick = { onRepayDebt(state.ludusState.activeDebt) },
                            enabled = state.ludusState.gold >= state.ludusState.activeDebt,
                            colors = ButtonDefaults.buttonColors(containerColor = ImmersiveTerracotta, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Borcu Kapat",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}
