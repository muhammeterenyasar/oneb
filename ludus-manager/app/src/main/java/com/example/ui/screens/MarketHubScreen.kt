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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.simulation.ActiveScreen
import com.example.simulation.LudusUiState
import com.example.ui.components.GladiatorMiniSprite
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.theme.*

/**
 * Unified Roman Market Hub (Pazar & Ticaret).
 * Consolidates:
 * 1. Köle & Gladyatör Pazarı (Recruitment)
 * 2. Teçhizat & Tüccarlar (6 Persistent Merchants with Smart Recommendations)
 * 3. Müzayedeler (Live Contested Auctions)
 * 4. Demirci Ocağı (Custom Commissions)
 */
@Composable
fun MarketHubScreen(
    state: LudusUiState,
    onPurchaseGladiator: (String) -> Unit,
    onSelectMerchant: (String) -> Unit,
    onSelectMarketCategory: (EquipmentCategory?) -> Unit,
    onSelectMarketItem: (EquipmentItem) -> Unit,
    onBuyItem: (EquipmentItem) -> Unit,
    onSellItem: (EquipmentItem) -> Unit,
    onRepairItem: (EquipmentItem) -> Unit,
    onPlaceAuctionBid: (EquipmentAuction, Int) -> Unit,
    onSubmitCommission: (CustomCommission) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(MarketHubTab.EQUIPMENT) }

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
            MarketHubTab.values().forEach { tab ->
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

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (activeTab) {
                MarketHubTab.RECRUITS -> RecruitsTabContent(state, onPurchaseGladiator)
                MarketHubTab.EQUIPMENT -> EquipmentTabContent(
                    state = state,
                    onSelectMerchant = onSelectMerchant,
                    onSelectMarketCategory = onSelectMarketCategory,
                    onSelectMarketItem = onSelectMarketItem,
                    onBuyItem = onBuyItem,
                    onSellItem = onSellItem,
                    onRepairItem = onRepairItem
                )
                MarketHubTab.AUCTIONS -> AuctionsTabContent(state, onPlaceAuctionBid)
                MarketHubTab.COMMISSIONS -> CommissionsTabContent(state, onSubmitCommission)
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: RECRUITS (SLAVES & FREEMEN MARKET)
// -------------------------------------------------------------
@Composable
private fun RecruitsTabContent(
    state: LudusUiState,
    onPurchaseGladiator: (String) -> Unit
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
            RomanCard(
                title = "Capua Köle & Gladyatör Pazarı",
                badge = "${state.marketGladiators.size} Dövüşçü Müzayedede"
            ) {
                if (state.marketGladiators.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Bu haftaki pazar kapandı. Yeni kafileler 5 gün sonra foruma ulaşacak.",
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GladiatorMiniSprite(gladiatorClass = glad.gladiatorClass, isAlive = true, size = 36.dp)
                                Column {
                                    Text(text = glad.name, color = RomanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${glad.gladiatorClass.title} • ${glad.origin.displayName}", color = RomanParchmentDark, fontSize = 9.5.sp)
                                    Text(text = "STR: ${glad.physicalStats.strength} | AGI: ${glad.physicalStats.agility} | END: ${glad.physicalStats.endurance}", color = RomanTextSecondary, fontSize = 8.5.sp)
                                }
                            }

                            Button(
                                onClick = { onPurchaseGladiator(glad.id) },
                                enabled = state.dominus.denarii >= glad.contractPrice,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RomanCrimson,
                                    contentColor = RomanParchment
                                ),
                                shape = RoundedCornerShape(2.dp),
                                modifier = Modifier.height(30.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                            ) {
                                Text(text = "Satın Al (${glad.contractPrice} D)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
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
            RomanCard(title = "Köle Ticareti Bilgisi") {
                Text(text = "Satın alınan her gladyatör kışlanıza 9-slotluk temel talim teçhizatı ile katılır.", color = RomanParchment, fontSize = 9.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Mevcut Kasa: ${state.dominus.denarii} Denarii", color = RomanGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "Mevcut Kadro: ${state.gladiators.size} Gladyatör", color = RomanGoldLight, fontSize = 10.sp)
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: EQUIPMENT & MERCHANTS WITH SMART RECOMMENDATIONS
// -------------------------------------------------------------
@Composable
private fun EquipmentTabContent(
    state: LudusUiState,
    onSelectMerchant: (String) -> Unit,
    onSelectMarketCategory: (EquipmentCategory?) -> Unit,
    onSelectMarketItem: (EquipmentItem) -> Unit,
    onBuyItem: (EquipmentItem) -> Unit,
    onSellItem: (EquipmentItem) -> Unit,
    onRepairItem: (EquipmentItem) -> Unit
) {
    EquipmentMarketScreen(
        state = state,
        onSelectMerchant = onSelectMerchant,
        onSelectCategory = onSelectMarketCategory,
        onSelectItem = onSelectMarketItem,
        onBuyItem = onBuyItem,
        onSellItem = onSellItem,
        onRepairItem = onRepairItem,
        onRepairAllGladiator = { /* handled */ },
        onRepairAllArmory = { /* handled */ },
        onEquipItem = { /* handled */ },
        onUnequipSlot = { /* handled */ },
        onPlaceBid = { _, _ -> /* handled */ },
        onSubmitCommission = { /* handled */ }
    )
}

// -------------------------------------------------------------
// TAB 3: AUCTIONS
// -------------------------------------------------------------
@Composable
private fun AuctionsTabContent(
    state: LudusUiState,
    onPlaceAuctionBid: (EquipmentAuction, Int) -> Unit
) {
    RomanCard(
        title = "Canlı Roma Müzayedeleri",
        badge = "${state.auctions.size} Açık Artırma",
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(state.auctions) { auction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF221814), RoundedCornerShape(3.dp))
                        .border(0.8.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = auction.item.name, color = RomanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "En Yüksek Teklif: ${auction.currentBid} Denarii (${auction.highestBidderName})", color = RomanGoldLight, fontSize = 9.5.sp)
                        Text(text = "Kalan Süre: ${auction.daysRemaining} Gün", color = RomanParchmentDark, fontSize = 8.5.sp)
                    }

                    Button(
                        onClick = { onPlaceAuctionBid(auction, auction.currentBid + 100) },
                        enabled = state.dominus.denarii >= auction.currentBid + 100,
                        colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson, contentColor = RomanParchment),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.height(30.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        Text(text = "Pey Sür (+100 D)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: COMMISSIONS (CUSTOM BLACKSMITH FORGE)
// -------------------------------------------------------------
@Composable
private fun CommissionsTabContent(
    state: LudusUiState,
    onSubmitCommission: (CustomCommission) -> Unit
) {
    RomanCard(
        title = "Demirci Ocağı & Özel Siparişler",
        badge = "${state.commissions.size} Aktif Sipariş",
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.commissions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Şu anda devam eden özel demirci siparişiniz bulunmuyor.",
                    color = RomanTextSecondary,
                    fontSize = 11.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.commissions) { comm ->
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
                            Text(text = comm.weaponName, color = RomanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Usta: ${comm.smithName} • Çelik: ${comm.steelGrade}", color = RomanParchmentDark, fontSize = 9.5.sp)
                            Text(text = "Kalan Süre: ${comm.daysRemaining} / ${comm.totalDaysRequired} Gün", color = RomanSuccessGreen, fontSize = 9.sp)
                        }
                        Text(text = "Maliyet: ${comm.totalCost} D", color = RomanGoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
