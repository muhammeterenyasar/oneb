package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.simulation.EquipmentEngine
import com.example.simulation.LudusUiState
import com.example.ui.components.GladiatorMiniSprite
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun EquipmentMarketScreen(
    state: LudusUiState,
    onSelectMerchant: (String) -> Unit,
    onSetTab: (MarketTab) -> Unit,
    onSetCategory: (EquipmentCategory?) -> Unit,
    onSelectItem: (EquipmentItem?) -> Unit,
    onSetComparisonGladiator: (String) -> Unit,
    onBuyItem: (String, EquipmentItem, String?) -> Unit,
    onSellItem: (EquipmentItem, String?) -> Unit,
    onRepairItem: (EquipmentItem, String?) -> Unit,
    onRepairAllGladiator: (String) -> Unit,
    onRepairAllArmory: () -> Unit,
    onEquipArmoryItem: (String, EquipmentItem) -> Unit,
    onUnequipItem: (String, EquipmentSlot) -> Unit,
    onPlaceBid: (String, Int) -> Unit,
    onSubmitCommission: (CustomCommission) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCommissionDialog by remember { mutableStateOf(false) }
    var selectedAuctionForBid by remember { mutableStateOf<EquipmentAuction?>(null) }
    val comparisonGlad = state.gladiators.find { it.id == state.comparisonGladiatorId } ?: state.gladiators.first()
    val activeMerchant = state.merchants.find { it.id == state.selectedMerchantId } ?: state.merchants.first()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // -------------------------------------------------------------
        // TOP BANNER: Market Ticker, Price Trends & Treasury
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1714), RoundedCornerShape(4.dp))
                .border(1.dp, RomanBronzeDark, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = "Market",
                    tint = RomanGold,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "CAPUA FORUMU & ARMAMENTARIUM",
                    color = RomanGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                // Active Market Events Badges
                state.equipmentMarketEvents.forEach { ev ->
                    Box(
                        modifier = Modifier
                            .background(
                                if (ev.isPositive) RomanSuccessGreen.copy(alpha = 0.2f) else RomanDangerRed.copy(alpha = 0.2f),
                                RoundedCornerShape(2.dp)
                            )
                            .border(0.6.dp, if (ev.isPositive) RomanSuccessGreen else RomanDangerRed, RoundedCornerShape(2.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${ev.scarcityNote} (${ev.daysRemaining}g)",
                            color = if (ev.isPositive) RomanSuccessGreen else RomanDangerRed,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Dominus Treasury & City
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Konum: ${state.currentVenue.city}",
                    color = RomanParchmentDark,
                    fontSize = 10.sp
                )
                Text(
                    text = "Hazine: %,d Denarii".format(state.dominus.denarii),
                    color = RomanGoldLight,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // -------------------------------------------------------------
        // SUB-NAVIGATION TABS (Merchants, Used, Auctions, Commissions, Armory)
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF191310), RoundedCornerShape(3.dp))
                .border(0.6.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                .padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            MarketTab.values().forEach { tab ->
                val isSelected = state.activeMarketTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isSelected) Brush.horizontalGradient(listOf(RomanCrimson, RomanDarkCrimson))
                            else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) RomanGold else Color.Transparent,
                            shape = RoundedCornerShape(2.dp)
                        )
                        .clickable { onSetTab(tab) }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title.uppercase(),
                        color = if (isSelected) RomanGoldLight else RomanTextSecondary,
                        fontSize = 9.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // MAIN BODY: 3-PANE LANDSCAPE WORKSPACE
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // =========================================================
            // PANE 1 (LEFT): Merchant / Tab Source Selector
            // =========================================================
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                when (state.activeMarketTab) {
                    MarketTab.MERCHANTS -> {
                        RomanCard(
                            title = "Kayıtlı Roma Tüccarları",
                            badge = "${state.merchants.size} Dükkan",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                items(state.merchants) { merchant ->
                                    val isSelected = merchant.id == state.selectedMerchantId
                                    MerchantRowCard(
                                        merchant = merchant,
                                        isSelected = isSelected,
                                        onClick = { onSelectMerchant(merchant.id) }
                                    )
                                }
                            }
                        }
                    }

                    MarketTab.USED_MARKET -> {
                        val hanno = state.merchants.find { it.id == "merch_hanno" } ?: state.merchants.first()
                        RomanCard(
                            title = "Subura İkinci El Pazarı",
                            badge = "Hanno Punicus",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = hanno.title,
                                    color = RomanGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = hanno.backstory,
                                    color = RomanParchmentDark,
                                    fontSize = 9.5.sp,
                                    lineHeight = 13.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "İkinci El Kuralları (Caveat Emptor):",
                                    color = RomanWarningAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "• Kullanılmış eşyalar %30-%40 daha ucuzdur.\n• Arenada ölen gladyatörlerin şeceresi ve zafer kaydı teçhizatın prestijini artırır.\n• Dayanıklılıkları düşüktür, almadan önce tamir gerekebilir.",
                                    color = RomanTextSecondary,
                                    fontSize = 9.sp,
                                    lineHeight = 13.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                RomanStatBar(
                                    label = "Tüccar İtimadı",
                                    value = hanno.playerRelationship.coerceIn(0, 100),
                                    maxValue = 100,
                                    color = RomanGoldLight
                                )
                            }
                        }
                    }

                    MarketTab.AUCTIONS -> {
                        RomanCard(
                            title = "Canlı Müzayedeler",
                            badge = "${state.auctions.size} İhale",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(state.auctions) { auc ->
                                    val isPlayerWinning = auc.isPlayerHighBidder
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (isPlayerWinning) RomanDarkCrimson.copy(alpha = 0.5f) else Color(0xFF221A15),
                                                RoundedCornerShape(3.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isPlayerWinning) RomanGold else RomanBronzeDark,
                                                RoundedCornerShape(3.dp)
                                            )
                                            .clickable { onSelectItem(auc.item) }
                                            .padding(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = auc.item.name, color = RomanGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "${auc.daysRemaining} Gün Kaldı", color = RomanWarningAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(text = "Mevcut Pey: ${auc.currentBid} Denarii", color = RomanGoldLight, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            text = if (isPlayerWinning) "✓ En Yüksek Teklif Sizin" else "En Yüksek: ${auc.highBidder}",
                                            color = if (isPlayerWinning) RomanSuccessGreen else RomanParchmentDark,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Button(
                                            onClick = { selectedAuctionForBid = auc },
                                            colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                                            shape = RoundedCornerShape(2.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(26.dp),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(text = "PEY SÜR (BID)", fontSize = 9.5.sp, color = RomanParchment, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    MarketTab.COMMISSIONS -> {
                        RomanCard(
                            title = "Demirci Ocağı & Siparişler",
                            badge = "${state.commissions.size} Ocakta",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { showCommissionDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanGoldDark),
                                    shape = RoundedCornerShape(3.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(32.dp)
                                        .border(1.dp, RomanGoldLight, RoundedCornerShape(3.dp))
                                ) {
                                    Icon(imageVector = Icons.Default.Build, contentDescription = null, tint = RomanParchment, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "+ YENİ SİPARİŞ VER", fontSize = 10.sp, color = RomanParchment, fontWeight = FontWeight.Bold)
                                }

                                if (state.commissions.isEmpty()) {
                                    Text(
                                        text = "Şu anda dövülen özel bir teçhizat bulunmuyor. Usta demircilere gladyatörünüze özel gladius, zırh veya mızrak siparişi verebilirsiniz.",
                                        color = RomanTextSecondary,
                                        fontSize = 9.sp,
                                        lineHeight = 13.sp
                                    )
                                } else {
                                    state.commissions.forEach { comm ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF241A14), RoundedCornerShape(3.dp))
                                                .border(0.6.dp, RomanBronze, RoundedCornerShape(3.dp))
                                                .padding(6.dp)
                                        ) {
                                            Text(text = comm.weaponName, color = RomanGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "Usta: ${comm.smithName} | ${comm.material.displayName}", color = RomanParchmentDark, fontSize = 8.5.sp)
                                            Text(text = "Profil: ${comm.bladeProfile} (${comm.weightProfile})", color = RomanTextSecondary, fontSize = 8.5.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            LinearProgressIndicator(
                                                progress = { comm.progressPercent },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(4.dp)
                                                    .clip(RoundedCornerShape(2.dp)),
                                                color = RomanGold,
                                                trackColor = Color(0xFF140F0D)
                                            )
                                            Text(
                                                text = "${comm.daysRemaining} gün kaldı (%${(comm.progressPercent * 100).toInt()})",
                                                color = RomanGoldLight,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    MarketTab.ARMORY_REPAIRS -> {
                        RomanCard(
                            title = "Cephanelik Bakım Tezgahı",
                            badge = "${state.ludusArmory.size} Eşya Depoda",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = onRepairAllArmory,
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanBronzeDark),
                                    shape = RoundedCornerShape(3.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(28.dp)
                                        .border(0.6.dp, RomanGold, RoundedCornerShape(3.dp))
                                ) {
                                    Icon(imageVector = Icons.Default.Handyman, contentDescription = null, tint = RomanGold, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "TÜM CEPHANELİĞİ ONAR", fontSize = 9.sp, color = RomanParchment, fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    text = "Gladyatör Seti Onarımı:",
                                    color = RomanGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                state.gladiators.forEach { glad ->
                                    val loadout = state.gladiatorLoadouts[glad.id]
                                    val missingDur = listOfNotNull(
                                        loadout?.helmet, loadout?.bodyArmor, loadout?.leftArm, loadout?.rightArm,
                                        loadout?.mainHand, loadout?.offHand, loadout?.shield, loadout?.legs, loadout?.accessory
                                    ).sumOf { it.maxDurability - it.currentDurability }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFF221714), RoundedCornerShape(2.dp))
                                            .border(0.5.dp, RomanBronzeDark, RoundedCornerShape(2.dp))
                                            .padding(4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = glad.name, color = RomanParchment, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = if (missingDur > 0) "Hasar: $missingDur puan aşınma" else "Set Kusursuz",
                                                color = if (missingDur > 0) RomanWarningAmber else RomanSuccessGreen,
                                                fontSize = 8.5.sp
                                            )
                                        }

                                        if (missingDur > 0) {
                                            Button(
                                                onClick = { onRepairAllGladiator(glad.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                                                shape = RoundedCornerShape(2.dp),
                                                modifier = Modifier.height(22.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                            ) {
                                                Text(text = "Onar", fontSize = 8.5.sp, color = RomanParchment, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================
            // PANE 2 (CENTER): Equipment Catalog / Grid / List
            // =========================================================
            Column(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RomanCard(
                    title = when (state.activeMarketTab) {
                        MarketTab.MERCHANTS -> "${activeMerchant.name} Envanteri"
                        MarketTab.USED_MARKET -> "Kullanılmış & Şecereli Eşyalar"
                        MarketTab.AUCTIONS -> "Müzayede Kataloğu"
                        MarketTab.COMMISSIONS -> "Özel Yapım Numuneler"
                        MarketTab.ARMORY_REPAIRS -> "Ludus Deposu (Armory)"
                    },
                    badge = when (state.activeMarketTab) {
                        MarketTab.ARMORY_REPAIRS -> "${state.ludusArmory.size} Eşya"
                        MarketTab.USED_MARKET -> "İkinci El"
                        else -> "${activeMerchant.inventory.size} Ürün"
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Category Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val categories = listOf(
                            null to "Hepsi",
                            EquipmentCategory.WEAPONS to "Silah",
                            EquipmentCategory.SHIELDS to "Kalkan",
                            EquipmentCategory.ARMOR to "Zırh",
                            EquipmentCategory.HELMETS to "Miğfer",
                            EquipmentCategory.ACCESSORIES to "Aksesuar"
                        )
                        categories.forEach { (cat, label) ->
                            val isChosen = state.selectedMarketCategory == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (isChosen) RomanDarkCrimson else Color(0xFF1E1714))
                                    .border(
                                        width = if (isChosen) 1.dp else 0.5.dp,
                                        color = if (isChosen) RomanGold else RomanBronzeDark,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                                    .clickable { onSetCategory(cat) }
                                    .padding(vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isChosen) RomanGoldLight else RomanTextSecondary,
                                    fontSize = 8.5.sp,
                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Items List
                    val rawItems = when (state.activeMarketTab) {
                        MarketTab.ARMORY_REPAIRS -> state.ludusArmory
                        MarketTab.USED_MARKET -> {
                            val hanno = state.merchants.find { it.id == "merch_hanno" }
                            hanno?.inventory ?: emptyList()
                        }
                        MarketTab.AUCTIONS -> state.auctions.map { it.item }
                        else -> activeMerchant.inventory
                    }

                    val filteredItems = rawItems.filter { item ->
                        state.selectedMarketCategory == null || item.category == state.selectedMarketCategory
                    }

                    if (filteredItems.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Bu kategoride şu an satılık teçhizat bulunmuyor.",
                                color = RomanTextMuted,
                                fontSize = 10.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            items(filteredItems) { item ->
                                val isSelected = state.selectedMarketItem?.id == item.id
                                val dynamicPrice = EquipmentEngine.calculateDynamicPrice(
                                    item = item,
                                    merchant = activeMerchant,
                                    activeEvents = state.equipmentMarketEvents,
                                    politicalFactions = state.politicalFactions
                                )

                                ItemCatalogCard(
                                    item = item,
                                    dynamicPrice = dynamicPrice,
                                    isSelected = isSelected,
                                    onClick = { onSelectItem(item) }
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================
            // PANE 3 (RIGHT): Item Inspector, Tradeoffs & Loadout Comparison
            // =========================================================
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val selectedItem = state.selectedMarketItem
                if (selectedItem != null) {
                    val currentLoadout = state.gladiatorLoadouts[comparisonGlad.id]
                    val currentEquipped = currentLoadout?.getItemInSlot(selectedItem.slot)
                    val diff = compareEquipment(selectedItem, currentEquipped)
                    val (isCompatible, incompReason) = checkClassCompatibility(comparisonGlad.gladiatorClass, selectedItem)

                    val dynamicPrice = EquipmentEngine.calculateDynamicPrice(
                        item = selectedItem,
                        merchant = activeMerchant,
                        activeEvents = state.equipmentMarketEvents,
                        politicalFactions = state.politicalFactions
                    )

                    RomanCard(
                        title = selectedItem.name,
                        badge = selectedItem.quality.title
                    ) {
                        // Header tags
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            BadgeChip(text = selectedItem.slot.displayName, color = RomanGoldDark)
                            BadgeChip(text = selectedItem.material.displayName, color = RomanBronze)
                            if (selectedItem.isUsed) BadgeChip(text = "Kullanılmış", color = RomanWarningAmber)
                            if (selectedItem.isNamedArtifact) BadgeChip(text = "Efsanevi Yadigâr", color = RomanDangerRed)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Durability & Condition State Bar
                        val durRatio = if (selectedItem.maxDurability > 0) selectedItem.currentDurability.toFloat() / selectedItem.maxDurability else 0f
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Durum: ${selectedItem.conditionState.displayName}", color = RomanParchment, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${selectedItem.currentDurability}/${selectedItem.maxDurability}", color = RomanGoldLight, fontSize = 9.sp)
                        }
                        LinearProgressIndicator(
                            progress = { durRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (durRatio > 0.6f) RomanSuccessGreen else if (durRatio > 0.3f) RomanWarningAmber else RomanDangerRed,
                            trackColor = Color(0xFF140F0D)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Core Stats Grid
                        when (selectedItem.category) {
                            EquipmentCategory.WEAPONS -> {
                                StatRow("Hasar (Effective Damage)", "${selectedItem.effectiveDamage} Pts", RomanCrimson)
                                StatRow("Menzil / Boyut", "${selectedItem.reachCm} cm", RomanGold)
                                StatRow("Vuruş Hızı (Attack Speed)", "${selectedItem.attackSpeed}/100", RomanWarningAmber)
                                StatRow("Zırh Delme (Armor Pen.)", "%${selectedItem.armorPenetrationPercent}", RomanInfoBlue)
                                StatRow("Ağırlık & Denge", "${selectedItem.weightKg} kg (Denge: %${selectedItem.balance})", RomanParchmentDark)
                                StatRow("Nefes Maliyeti (Stamina)", "${selectedItem.staminaCost} pts/vuruş", RomanStaminaCyan)
                            }
                            EquipmentCategory.ARMOR -> {
                                StatRow("Koruma Gücü (Effective)", "${selectedItem.effectiveProtection} Pts", RomanGold)
                                StatRow("Kapsama Alanı", "%${selectedItem.coveragePercent}", RomanParchmentDark)
                                StatRow("Hareketlilik Cezası", "-${selectedItem.mobilityPenalty} Çeviklik", RomanDangerRed)
                                StatRow("Yorulma Artışı", "+%${selectedItem.staminaDrainPercent}", RomanStaminaCyan)
                                StatRow("Isı & Terleme Cezası", "+${selectedItem.heatResistancePenalty} Hararet", RomanWarningAmber)
                                StatRow("Acı Azaltma Bonusu", "+${selectedItem.painResistanceBonus} Acı Eşiği", RomanSuccessGreen)
                            }
                            EquipmentCategory.SHIELDS -> {
                                StatRow("Koruma & Kapsama", "${selectedItem.effectiveProtection} Pts (%${selectedItem.coveragePercent})", RomanGold)
                                StatRow("Blok Başarısı", "%${selectedItem.effectiveBlock}", RomanSuccessGreen)
                                StatRow("Kalkan Darbesi (Bash)", "%${selectedItem.bashEffectiveness} Sendeletme", RomanCrimson)
                                StatRow("Ağırlık & Hantallık", "${selectedItem.weightKg} kg (-${selectedItem.mobilityPenalty} Hız)", RomanDangerRed)
                            }
                            EquipmentCategory.HELMETS -> {
                                StatRow("Kafa Koruması", "${selectedItem.effectiveProtection} Pts", RomanGold)
                                StatRow("Görüş Alanı", "%${selectedItem.visibilityPercent}", if (selectedItem.visibilityPercent < 75) RomanWarningAmber else RomanSuccessGreen)
                                StatRow("İşitme (Emre İtaat)", "%${selectedItem.hearingPercent}", if (selectedItem.hearingPercent < 75) RomanDangerRed else RomanSuccessGreen)
                                StatRow("Ağırlık & Hararet", "${selectedItem.weightKg} kg (+${selectedItem.heatResistancePenalty} Isı)", RomanParchmentDark)
                            }
                            EquipmentCategory.ACCESSORIES -> {
                                StatRow("Koruma / Destek", "${selectedItem.effectiveProtection} Pts", RomanGold)
                                StatRow("Ağırlık", "${selectedItem.weightKg} kg", RomanParchmentDark)
                                StatRow("Özel Destek", selectedItem.specialProperty, RomanSuccessGreen)
                            }
                        }

                        // Special Properties & Lore
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF19120F), RoundedCornerShape(2.dp))
                                .border(0.5.dp, RomanBronzeDark, RoundedCornerShape(2.dp))
                                .padding(4.dp)
                        ) {
                            Text(
                                text = "Özellik: ${selectedItem.specialProperty}",
                                color = RomanGoldLight,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Provenance (If used or named relic)
                        selectedItem.provenance?.let { prov ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF2A1B14), RoundedCornerShape(2.dp))
                                    .border(0.5.dp, RomanGoldDark, RoundedCornerShape(2.dp))
                                    .padding(4.dp)
                            ) {
                                Column {
                                    Text(text = "📜 ŞECERE & SAVAŞ GEÇMİŞİ", color = RomanGold, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Önceki Sahip: ${prov.previousOwner ?: "Bilinmiyor"} (${prov.previousLudus ?: "Roma"})", color = RomanParchment, fontSize = 8.sp)
                                    Text(text = "Kayıtlı Zafer: ${prov.victoriesRecorded} | İnfaz: ${prov.fatalKills} (+${prov.bonusPrestige} Şan)", color = RomanGoldLight, fontSize = 8.sp)
                                    Text(text = prov.battleLore, color = RomanParchmentDark, fontSize = 7.5.sp, lineHeight = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                        Spacer(modifier = Modifier.height(4.dp))

                        // Gladiator Selector for Comparison
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Karşılaştırma Dövüşçüsü:", color = RomanTextSecondary, fontSize = 9.sp)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                items(state.gladiators) { glad ->
                                    val isPicked = glad.id == comparisonGlad.id
                                    Box(
                                        modifier = Modifier
                                            .background(if (isPicked) RomanCrimson else Color(0xFF1E1714), RoundedCornerShape(2.dp))
                                            .border(0.5.dp, if (isPicked) RomanGold else RomanBronzeDark, RoundedCornerShape(2.dp))
                                            .clickable { onSetComparisonGladiator(glad.id) }
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = glad.name, color = if (isPicked) RomanGoldLight else RomanParchmentDark, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Side-by-Side Comparison Diff
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1B1411), RoundedCornerShape(2.dp))
                                .border(0.5.dp, RomanBronzeDark, RoundedCornerShape(2.dp))
                                .padding(4.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Mevcut: ${currentEquipped?.name ?: "Boş Slot"}",
                                    color = RomanParchmentDark,
                                    fontSize = 8.5.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    DiffText(label = "Hasar", delta = diff.damageDelta, higherIsBetter = true)
                                    DiffText(label = "Zırh", delta = diff.protectionDelta, higherIsBetter = true)
                                    DiffText(label = "Ağırlık", delta = diff.weightDelta.toInt(), higherIsBetter = false, suffix = "kg")
                                    DiffText(label = "Hantallık", delta = diff.mobilityDelta, higherIsBetter = false)
                                }
                            }
                        }

                        // Incompatibility Alert
                        if (!isCompatible) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(RomanDangerRed.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                                    .border(0.6.dp, RomanDangerRed, RoundedCornerShape(2.dp))
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = "⚠ UYARI: $incompReason",
                                    color = RomanDangerRed,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Buttons
                        if (state.activeMarketTab == MarketTab.ARMORY_REPAIRS) {
                            // Armory actions: Equip, Repair, Sell
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = { onEquipArmoryItem(comparisonGlad.id, selectedItem) },
                                    enabled = isCompatible,
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanSuccessGreen),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.weight(1f).height(28.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(text = "KUŞAN", fontSize = 9.5.sp, color = RomanParchment, fontWeight = FontWeight.Bold)
                                }

                                if (selectedItem.repairCost > 0) {
                                    Button(
                                        onClick = { onRepairItem(selectedItem, null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = RomanBronzeDark),
                                        shape = RoundedCornerShape(2.dp),
                                        modifier = Modifier.weight(1f).height(28.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(text = "ONAR (${selectedItem.repairCost}D)", fontSize = 9.sp, color = RomanGoldLight, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Button(
                                    onClick = { onSellItem(selectedItem, activeMerchant.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanDarkCrimson),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.weight(1f).height(28.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(text = "SAT", fontSize = 9.5.sp, color = RomanParchment, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else if (state.activeMarketTab != MarketTab.AUCTIONS) {
                            // Market purchase buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = { onBuyItem(activeMerchant.id, selectedItem, comparisonGlad.id) },
                                    enabled = state.dominus.denarii >= dynamicPrice && isCompatible,
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.weight(1.2f).height(30.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "AL VE KUŞAN ($dynamicPrice D)",
                                        fontSize = 9.sp,
                                        color = RomanParchment,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Button(
                                    onClick = { onBuyItem(activeMerchant.id, selectedItem, null) },
                                    enabled = state.dominus.denarii >= dynamicPrice,
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanSurfaceVariant),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.weight(1f).height(30.dp).border(0.6.dp, RomanGoldDark, RoundedCornerShape(2.dp)),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "DEPOYA AL",
                                        fontSize = 9.sp,
                                        color = RomanGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                } else {
                    RomanCard(title = "Teçhizat İnceleme", badge = "Seçim Yapın") {
                        Text(
                            text = "Ayrıntılı teknik özellikleri, ağırlık dengesini, koruma değerlerini ve gladyatörünüzün mevcut teçhizatı ile karşılaştırmasını görmek için listeden bir eşya seçin.",
                            color = RomanTextMuted,
                            fontSize = 9.5.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // DIALOG: Custom Blacksmith Commission
    // -------------------------------------------------------------
    if (showCommissionDialog) {
        CustomCommissionDialog(
            onDismiss = { showCommissionDialog = false },
            onSubmit = { comm ->
                onSubmitCommission(comm)
                showCommissionDialog = false
            }
        )
    }

    // -------------------------------------------------------------
    // DIALOG: Auction Bidding Modal
    // -------------------------------------------------------------
    selectedAuctionForBid?.let { auc ->
        AuctionBidDialog(
            auction = auc,
            currentDenarii = state.dominus.denarii,
            onDismiss = { selectedAuctionForBid = null },
            onBid = { amount ->
                onPlaceBid(auc.id, amount)
                selectedAuctionForBid = null
            }
        )
    }
}

// -------------------------------------------------------------
// HELPER COMPONENTS
// -------------------------------------------------------------

@Composable
private fun MerchantRowCard(
    merchant: RomanMerchant,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) RomanDarkCrimson.copy(alpha = 0.5f) else Color(0xFF221A15),
                RoundedCornerShape(3.dp)
            )
            .border(
                1.dp,
                if (isSelected) RomanGold else RomanBronzeDark,
                RoundedCornerShape(3.dp)
            )
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = merchant.name, color = RomanGold, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            Text(text = merchant.skillLevel, color = RomanParchmentDark, fontSize = 8.sp)
        }
        Text(text = merchant.specialty, color = RomanTextSecondary, fontSize = 8.5.sp)
        Text(text = "Konum: ${merchant.location}", color = RomanTextMuted, fontSize = 8.sp)

        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "İtibar: ${merchant.reputation}/100", color = RomanGoldLight, fontSize = 8.sp)
            val relColor = if (merchant.playerRelationship >= 15) RomanSuccessGreen else if (merchant.playerRelationship < 0) RomanDangerRed else RomanTextSecondary
            Text(text = "İlişki: ${merchant.playerRelationship}", color = relColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ItemCatalogCard(
    item: EquipmentItem,
    dynamicPrice: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = Color(item.quality.colorHex)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color(0xFF381515) else Color(0xFF1E1714),
                RoundedCornerShape(3.dp)
            )
            .border(
                width = if (isSelected) 1.2.dp else 0.6.dp,
                color = if (isSelected) RomanGold else borderColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(3.dp)
            )
            .clickable { onClick() }
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.name,
                    color = if (isSelected) RomanGoldLight else RomanParchment,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold
                )
                if (item.isUsed) {
                    Text(text = "[2.EL]", color = RomanWarningAmber, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
                if (item.isNamedArtifact) {
                    Text(text = "★", color = RomanGold, fontSize = 9.sp)
                }
            }

            Text(
                text = "${item.quality.title} • ${item.material.displayName}",
                color = borderColor,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Primary stat preview
            val mainStat = when (item.category) {
                EquipmentCategory.WEAPONS -> "Hasar: ${item.effectiveDamage} | Hız: ${item.attackSpeed}"
                EquipmentCategory.ARMOR -> "Koruma: ${item.effectiveProtection} | Ağırlık: ${item.weightKg}kg"
                EquipmentCategory.SHIELDS -> "Blok: %${item.effectiveBlock} | Koruma: ${item.effectiveProtection}"
                EquipmentCategory.HELMETS -> "Baş Koruması: ${item.effectiveProtection} | Görüş: %${item.visibilityPercent}"
                EquipmentCategory.ACCESSORIES -> "Ağırlık: ${item.weightKg}kg | ${item.slot.displayName}"
            }
            Text(text = mainStat, color = RomanTextSecondary, fontSize = 8.sp)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$dynamicPrice D",
                color = RomanGoldLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            val durPercent = if (item.maxDurability > 0) ((item.currentDurability.toFloat() / item.maxDurability) * 100).toInt() else 0
            Text(
                text = "%$durPercent Sağlam",
                color = if (durPercent > 70) RomanSuccessGreen else if (durPercent > 35) RomanWarningAmber else RomanDangerRed,
                fontSize = 8.sp
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = RomanTextSecondary, fontSize = 9.sp)
        Text(text = value, color = color, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun BadgeChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.25f), RoundedCornerShape(2.dp))
            .border(0.5.dp, color, RoundedCornerShape(2.dp))
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Text(text = text, color = color, fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DiffText(label: String, delta: Int, higherIsBetter: Boolean, suffix: String = "") {
    val isZero = delta == 0
    val isPositive = if (higherIsBetter) delta > 0 else delta < 0
    val color = if (isZero) RomanTextMuted else if (isPositive) RomanSuccessGreen else RomanDangerRed
    val sign = if (delta > 0) "+" else ""
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = RomanTextMuted, fontSize = 7.5.sp)
        Text(text = if (isZero) "0" else "$sign$delta$suffix", color = color, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
    }
}

// -------------------------------------------------------------
// DIALOG: CUSTOM COMMISSION FORGE
// -------------------------------------------------------------
@Composable
private fun CustomCommissionDialog(
    onDismiss: () -> Unit,
    onSubmit: (CustomCommission) -> Unit
) {
    var weaponName by remember { mutableStateOf("Özel Dövüm Gladius") }
    var selectedType by remember { mutableStateOf(EquipmentType.GLADIUS) }
    var selectedMaterial by remember { mutableStateOf(EquipmentMaterial.IBERIAN_STEEL) }
    var selectedWeightProfile by remember { mutableStateOf("Balanced") }
    var selectedBladeProfile by remember { mutableStateOf("Pointed Thrusting") }
    var selectedDecoration by remember { mutableStateOf("Roman Eagle Engraving") }
    var targetQuality by remember { mutableStateOf(EquipmentQuality.FINE) }

    val baseCost = 400
    val totalCost = (baseCost * selectedMaterial.priceMod * targetQuality.priceMultiplier).roundToInt()
    val daysRequired = (3 + (targetQuality.ordinal * 2)).coerceAtLeast(3)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = RomanCardBg,
            modifier = Modifier
                .width(460.dp)
                .border(1.dp, RomanGold, RoundedCornerShape(4.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "VULKAN OCAĞI: ÖZEL SİPARİŞ İMALATI",
                    color = RomanGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Usta demircilere gladyatörünüzün fiziksel yapısına ve dövüş tekniğine kusursuz uyum sağlayacak özel bir silah dövdürün.",
                    color = RomanTextSecondary,
                    fontSize = 9.sp
                )

                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)

                // Weapon Type Selector
                Text(text = "Silah Türü:", color = RomanGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    listOf(EquipmentType.GLADIUS, EquipmentType.SPATHA, EquipmentType.SICA, EquipmentType.FALCATA, EquipmentType.SPEAR, EquipmentType.TRIDENT).forEach { t ->
                        val isPicked = selectedType == t
                        Box(
                            modifier = Modifier
                                .background(if (isPicked) RomanCrimson else Color(0xFF1E1714), RoundedCornerShape(2.dp))
                                .border(0.5.dp, if (isPicked) RomanGold else RomanBronzeDark, RoundedCornerShape(2.dp))
                                .clickable { selectedType = t }
                                .padding(horizontal = 4.dp, vertical = 3.dp)
                        ) {
                            Text(text = t.name, color = if (isPicked) RomanGoldLight else RomanParchmentDark, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Material Selector
                Text(text = "Metal / Alaşım:", color = RomanGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    listOf(EquipmentMaterial.STANDARD_IRON, EquipmentMaterial.HIGH_QUALITY_IRON, EquipmentMaterial.IBERIAN_STEEL, EquipmentMaterial.RARE_DAMASCUS_STEEL).forEach { m ->
                        val isPicked = selectedMaterial == m
                        Box(
                            modifier = Modifier
                                .background(if (isPicked) RomanCrimson else Color(0xFF1E1714), RoundedCornerShape(2.dp))
                                .border(0.5.dp, if (isPicked) RomanGold else RomanBronzeDark, RoundedCornerShape(2.dp))
                                .clickable { selectedMaterial = m }
                                .padding(horizontal = 4.dp, vertical = 3.dp)
                        ) {
                            Text(text = m.displayName.take(16), color = if (isPicked) RomanGoldLight else RomanParchmentDark, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Weight Profile
                Text(text = "Ağırlık Dengesi:", color = RomanGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Light & Fast", "Balanced", "Heavy & Devastating").forEach { wp ->
                        val isPicked = selectedWeightProfile == wp
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isPicked) RomanCrimson else Color(0xFF1E1714), RoundedCornerShape(2.dp))
                                .border(0.5.dp, if (isPicked) RomanGold else RomanBronzeDark, RoundedCornerShape(2.dp))
                                .clickable { selectedWeightProfile = wp }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = wp, color = if (isPicked) RomanGoldLight else RomanParchmentDark, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Blade Profile
                Text(text = "Namlu Profili:", color = RomanGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Curved Slashing", "Pointed Thrusting", "Serrated Cleaver").forEach { bp ->
                        val isPicked = selectedBladeProfile == bp
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (isPicked) RomanCrimson else Color(0xFF1E1714), RoundedCornerShape(2.dp))
                                .border(0.5.dp, if (isPicked) RomanGold else RomanBronzeDark, RoundedCornerShape(2.dp))
                                .clickable { selectedBladeProfile = bp }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = bp, color = if (isPicked) RomanGoldLight else RomanParchmentDark, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Cost & Delivery Summary
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF140F0D), RoundedCornerShape(2.dp))
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Toplam Maliyet: $totalCost Denarii", color = RomanGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = "İmalat Süresi: $daysRequired Gün", color = RomanParchmentDark, fontSize = 9.sp)
                    }
                    Button(
                        onClick = {
                            val commission = CustomCommission(
                                id = "comm_${System.currentTimeMillis()}",
                                gladiatorId = null,
                                gladiatorName = null,
                                weaponName = "$selectedBladeProfile ${selectedType.name}",
                                category = selectedType.category,
                                type = selectedType,
                                material = selectedMaterial,
                                weightProfile = selectedWeightProfile,
                                bladeProfile = selectedBladeProfile,
                                decoration = selectedDecoration,
                                smithName = "Servius Ferrarius",
                                totalCost = totalCost,
                                daysRemaining = daysRequired,
                                totalDaysRequired = daysRequired,
                                targetQuality = targetQuality
                            )
                            onSubmit(commission)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(text = "OCAĞA VER (SİPARİŞ ET)", fontSize = 9.5.sp, color = RomanParchment, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOG: AUCTION BID MODAL
// -------------------------------------------------------------
@Composable
private fun AuctionBidDialog(
    auction: EquipmentAuction,
    currentDenarii: Int,
    onDismiss: () -> Unit,
    onBid: (Int) -> Unit
) {
    val minBid = auction.currentBid + auction.minBidIncrement
    var proposedBid by remember { mutableStateOf(minBid) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = RomanCardBg,
            modifier = Modifier
                .width(420.dp)
                .border(1.dp, RomanGold, RoundedCornerShape(4.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "MÜZAYEDE PEY TEKLİFİ",
                    color = RomanGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = auction.item.name,
                    color = RomanParchment,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = auction.historicalProvenance,
                    color = RomanParchmentDark,
                    fontSize = 8.5.sp,
                    lineHeight = 12.sp
                )

                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Mevcut En Yüksek Pey:", color = RomanTextSecondary, fontSize = 9.5.sp)
                    Text(text = "${auction.currentBid} Denarii (${auction.highBidder})", color = RomanGoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Asgari Pey Artışı:", color = RomanTextSecondary, fontSize = 9.5.sp)
                    Text(text = "+${auction.minBidIncrement} Denarii", color = RomanParchment, fontSize = 9.5.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Teklifiniz:", color = RomanGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = "$proposedBid Denarii", color = RomanGoldLight, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }

                // Quick Increment Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(auction.minBidIncrement, 250, 500, 1000).forEach { inc ->
                        Button(
                            onClick = { proposedBid = minBid + inc },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2019)),
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.weight(1f).height(24.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(text = "+$inc", fontSize = 8.5.sp, color = RomanGold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1714)),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.weight(1f).height(30.dp)
                    ) {
                        Text(text = "VAZGEÇ", fontSize = 9.sp, color = RomanTextSecondary)
                    }

                    Button(
                        onClick = { onBid(proposedBid) },
                        enabled = currentDenarii >= proposedBid,
                        colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.weight(1f).height(30.dp)
                    ) {
                        Text(text = "PEY SÜR", fontSize = 10.sp, color = RomanParchment, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
