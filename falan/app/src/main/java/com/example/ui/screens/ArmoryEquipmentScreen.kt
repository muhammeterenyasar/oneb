package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.ArmoryEngine
import com.example.model.*
import com.example.ui.MainUiState
import com.example.ui.theme.*

@Composable
fun ArmoryEquipmentScreen(
    state: MainUiState,
    onSelectGladiator: (Gladiator) -> Unit,
    onBuyAndEquip: (Long, String) -> Unit,
    onUnequip: (Long, EquipmentSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedGladiator = state.selectedGladiator ?: state.gladiators.firstOrNull()
    var selectedSlotFilter by remember { mutableStateOf<EquipmentSlot?>(null) }

    val hasSilverTongue = state.ludusState.unlockedPerkIds.contains(LanistaPerk.SILVER_TONGUE.id)
    val discountPercent = if (hasSilverTongue) LanistaPerk.SILVER_TONGUE.discountPercent else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBg)
            .padding(horizontal = 16.dp)
            .testTag("armory_equipment_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp)
    ) {
        // 1. Blacksmith Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                border = BorderStroke(1.2.dp, ImmersiveGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ImperialRedSurface,
                                    ImmersiveCard
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🛡️", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "LUDUS ARMORY & DEMİRCİ",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.2.sp
                                    ),
                                    color = ImperialRomanRed
                                )
                                Text(
                                    text = "Silah, Zırh & Antik Roma Tılsımları",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ImmersiveTextMuted
                                )
                            }
                        }

                        if (hasSilverTongue) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = RomanGoldSurface,
                                border = BorderStroke(1.dp, ImmersiveBorderGold)
                            ) {
                                Text(
                                    text = "🪙 %20 Gümüş Dil İndirimi",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = RomanImperialGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Gladiator Selector Carousel
        item {
            Column {
                Text(
                    text = "KUŞANACAK GLADYATÖR",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                    color = ImmersiveGold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.gladiators, key = { it.id }) { g ->
                        val isSelected = g.id == selectedGladiator?.id
                        Surface(
                            modifier = Modifier
                                .clickable { onSelectGladiator(g) }
                                .testTag("select_armory_gladiator_${g.id}"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ImperialRedSurface else ImmersiveCard,
                            border = BorderStroke(1.2.dp, if (isSelected) ImperialRomanRed else ImmersiveCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = g.gladiatorClass.icon, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = g.name,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) ImperialRomanRed else ImmersiveTextPrimary
                                    )
                                    Text(
                                        text = "${g.careerRank.shortTitle} • Güç: ${g.totalPowerScore}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp),
                                        color = ImmersiveTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedGladiator != null) {
            // 3. Equipped Gear Summary Card
            item {
                EquippedSlotsCard(
                    gladiator = selectedGladiator,
                    onUnequip = { slot -> onUnequip(selectedGladiator.id, slot) }
                )
            }

            // 4. Equipment Slot Filter Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedSlotFilter == null,
                        onClick = { selectedSlotFilter = null },
                        label = { Text("Tümü (${ArmoryEngine.catalog.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ImperialRomanRed,
                            selectedLabelColor = Color.White
                        )
                    )
                    EquipmentSlot.entries.forEach { slot ->
                        FilterChip(
                            selected = selectedSlotFilter == slot,
                            onClick = { selectedSlotFilter = slot },
                            label = { Text("${slot.icon} ${slot.title}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ImperialRomanRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // 5. Catalog List
            val displayedItems = ArmoryEngine.catalog.filter { selectedSlotFilter == null || it.slot == selectedSlotFilter }
            items(displayedItems, key = { it.id }) { item ->
                val isEquipped = when (item.slot) {
                    EquipmentSlot.WEAPON -> selectedGladiator.equippedWeaponId == item.id
                    EquipmentSlot.ARMOR -> selectedGladiator.equippedArmorId == item.id
                    EquipmentSlot.RELIC -> selectedGladiator.equippedRelicId == item.id
                }
                val effectivePrice = ArmoryEngine.getEffectivePrice(item, discountPercent)
                val canAfford = state.ludusState.gold >= effectivePrice

                ArmoryItemCard(
                    item = item,
                    effectivePrice = effectivePrice,
                    isEquipped = isEquipped,
                    canAfford = canAfford,
                    onBuyAndEquip = { onBuyAndEquip(selectedGladiator.id, item.id) }
                )
            }
        }
    }
}

@Composable
fun EquippedSlotsCard(
    gladiator: Gladiator,
    onUnequip: (EquipmentSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    val weapon = ArmoryEngine.getItemById(gladiator.equippedWeaponId)
    val armor = ArmoryEngine.getItemById(gladiator.equippedArmorId)
    val relic = ArmoryEngine.getItemById(gladiator.equippedRelicId)
    val bonuses = ArmoryEngine.calculateEquipmentBonuses(gladiator)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
        border = BorderStroke(1.dp, ImmersiveCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "${gladiator.name.uppercase()} — MEVCUT EKİPMANLARI",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                color = ImperialRomanRed
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EquippedSlotItem(slot = EquipmentSlot.WEAPON, item = weapon, onUnequip = { onUnequip(EquipmentSlot.WEAPON) }, modifier = Modifier.weight(1f))
                EquippedSlotItem(slot = EquipmentSlot.ARMOR, item = armor, onUnequip = { onUnequip(EquipmentSlot.ARMOR) }, modifier = Modifier.weight(1f))
                EquippedSlotItem(slot = EquipmentSlot.RELIC, item = relic, onUnequip = { onUnequip(EquipmentSlot.RELIC) }, modifier = Modifier.weight(1f))
            }

            if (bonuses.totalDamageBonus > 0 || bonuses.totalArmorReductionPercent > 0 || bonuses.totalCritBonusPercent > 0 || bonuses.totalDodgeBonusPercent > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RomanCardSecondary,
                    border = BorderStroke(0.8.dp, ImmersiveCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (bonuses.totalDamageBonus > 0) Text(text = "⚔️ +${bonuses.totalDamageBonus} Hasar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ImperialRomanRed)
                        if (bonuses.totalArmorReductionPercent > 0) Text(text = "🛡️ -%${bonuses.totalArmorReductionPercent} Zırh", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        if (bonuses.totalCritBonusPercent > 0) Text(text = "⚡ +%${bonuses.totalCritBonusPercent} Kritik", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RomanImperialGold)
                        if (bonuses.totalDodgeBonusPercent > 0) Text(text = "💨 +%${bonuses.totalDodgeBonusPercent} Kaçınma", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                    }
                }
            }
        }
    }
}

@Composable
fun EquippedSlotItem(
    slot: EquipmentSlot,
    item: EquipmentItem?,
    onUnequip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (item != null) RomanGoldSurface else RomanCardSecondary,
        border = BorderStroke(1.dp, if (item != null) ImmersiveBorderGold else ImmersiveCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = slot.icon, fontSize = 18.sp)
            Text(text = slot.title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = ImmersiveTextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            if (item != null) {
                Text(text = item.name, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = RomanInkDark, maxLines = 1)
                IconButton(onClick = onUnequip, modifier = Modifier.size(20.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Çıkar", tint = ImperialRomanRed, modifier = Modifier.size(14.dp))
                }
            } else {
                Text(text = "Boş", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = ImmersiveTextMuted)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ArmoryItemCard(
    item: EquipmentItem,
    effectivePrice: Int,
    isEquipped: Boolean,
    canAfford: Boolean,
    onBuyAndEquip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = ImmersiveCard,
        border = BorderStroke(1.dp, if (isEquipped) ImperialRomanRed else ImmersiveCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = RomanCardSecondary,
                    border = BorderStroke(1.dp, Color(item.rarity.colorHex)),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = item.icon, fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = RomanInkDark
                        )
                    }
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = ImmersiveTextMuted,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (item.damageBonus > 0) Text(text = "+${item.damageBonus} Hasar", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ImperialRomanRed)
                        if (item.armorReductionPercent > 0) Text(text = "+%${item.armorReductionPercent} Zırh", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                        if (item.critBonusPercent > 0) Text(text = "+%${item.critBonusPercent} Kritik", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RomanImperialGold)
                        if (item.dodgeBonusPercent > 0) Text(text = "+%${item.dodgeBonusPercent} Kaçınma", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isEquipped) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ImperialRedSurface,
                    border = BorderStroke(1.dp, ImperialRomanRed)
                ) {
                    Text(
                        text = "✅ KUŞANILDI",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp),
                        color = ImperialRomanRed
                    )
                }
            } else {
                Button(
                    onClick = onBuyAndEquip,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImperialRomanRed,
                        contentColor = Color.White,
                        disabledContainerColor = ImmersiveTrack,
                        disabledContentColor = ImmersiveTextMuted
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$effectivePrice 🪙 Kuşan",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 11.sp)
                    )
                }
            }
        }
    }
}
