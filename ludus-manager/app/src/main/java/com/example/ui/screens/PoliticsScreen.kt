package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.simulation.LudusUiState
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

enum class PoliticsSubTab(val title: String) {
    FACTIONS("🏛 SENATO & FRAKSİYONLAR"),
    CHARACTERS("👥 ŞAHSİYETLER & HAMİLER"),
    NETWORK("🕸 GÜÇ AĞI & İTTİFAKLAR"),
    SCANDALS("📜 SKANDALLAR & SIRLAR"),
    CALENDAR("📅 SİYASİ TAKVİM")
}

val PoliticalFactionId.emblemBadge: String
    get() = when (this) {
        PoliticalFactionId.SENATORIAL_ELITE -> "🏛"
        PoliticalFactionId.ARENA_OFFICIALS -> "⚔"
        PoliticalFactionId.MERCHANT_GUILD -> "⚖"
        PoliticalFactionId.MILITARY -> "🛡"
        PoliticalFactionId.RELIGIOUS_AUTHORITIES -> "🏺"
        PoliticalFactionId.IMPERIAL_ADMINISTRATION -> "👑"
        PoliticalFactionId.RIVAL_LUDUSES -> "💀"
    }

@Composable
fun PoliticsScreen(
    state: LudusUiState,
    onResolveChoice: (PoliticalChoice) -> Unit = {},
    onDismissEvent: () -> Unit = {},
    onSelectNpc: (PoliticalCharacter?) -> Unit = {},
    onSelectFaction: (PoliticalFaction?) -> Unit = {},
    onExecuteInteraction: (String, PoliticalInteractionType, String?) -> Unit = { _, _, _ -> },
    onSuppressScandal: (String, Boolean) -> Unit = { _, _ -> },
    onExposeSecret: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(PoliticsSubTab.FACTIONS) }
    var factionFilter by remember { mutableStateOf<PoliticalFactionId?>(null) }
    val activePatron = state.politicalCharacters.find { it.id == state.activePatronId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(RomanBackground)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // -------------------------------------------------------------
        // 1. TOP POLITICAL STATUS BAR
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF2C1E16), Color(0xFF1F1713))),
                    RoundedCornerShape(4.dp)
                )
                .border(1.dp, RomanBronze, RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Auctoritas (Influence)
                Column {
                    Text(text = "AUCTORITAS (NÜFUZ)", color = RomanTextMuted, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    Text(text = "👑 ${state.politicalResources.influence}/100", color = RomanGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                // Honestas (Reputation)
                Column {
                    Text(text = "HONESTAS (SAYGINLIK)", color = RomanTextMuted, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    Text(text = "🏛 ${state.politicalResources.reputation}/100", color = RomanParchment, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                // Gratia (Favors Owed)
                Column {
                    Text(text = "GRATIA (LÜTUF PUANI)", color = RomanTextMuted, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    Text(text = "📜 ${state.politicalResources.politicalFavor} Lütuf", color = RomanGoldLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                // Secrets Dossier
                Column {
                    Text(text = "GİZLİ ARŞİV", color = RomanTextMuted, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    Text(text = "🗝 ${state.playerSecrets.size} Şantaj Dosyası", color = RomanMoralePurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                // Active Patron Status
                Column {
                    Text(text = "RESMİ HAMİLİK (PATRONUS)", color = RomanTextMuted, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    if (activePatron != null) {
                        Text(
                            text = "🛡 ${activePatron.name} (+${activePatron.monthlyStipend} D/ay)",
                            color = RomanSuccessGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(text = "⚖ Bağımsız (Hamisiz)", color = RomanTextSecondary, fontSize = 12.sp)
                    }
                }
            }

            // Quick Senate Banquet Action
            Button(
                onClick = {
                    val senatorial = state.politicalCharacters.firstOrNull { it.factionId == PoliticalFactionId.SENATORIAL_ELITE }
                    if (senatorial != null) {
                        onExecuteInteraction(senatorial.id, PoliticalInteractionType.HOST_SENATE_BANQUET, null)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                shape = RoundedCornerShape(3.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text(text = "🍷 Senato Ziyafeti (3.000 D)", color = RomanGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        // -------------------------------------------------------------
        // 2. ACTIVE POLITICAL CRISIS / DILEMMA BANNER
        // -------------------------------------------------------------
        state.activePoliticalEvent?.let { polEvent ->
            RomanCard(
                title = "⚡ ACİL SİYASİ BUHRAN: ${polEvent.title}",
                badge = if (polEvent.isUrgent) "DERHAL KARAR GEREKLİ" else "SENATO TALEBİ",
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = polEvent.narrative,
                        color = RomanParchment,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (choice in polEvent.choices) {
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, RomanBronze, RoundedCornerShape(4.dp))
                                    .background(Color(0xFF231A15), RoundedCornerShape(4.dp))
                                    .padding(6.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = choice.label,
                                        color = RomanGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = choice.effectDescription,
                                        color = RomanTextSecondary,
                                        fontSize = 9.sp,
                                        lineHeight = 12.sp
                                    )

                                    // Cost & Consequence badges
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (choice.requiredGold > 0) {
                                            Text(
                                                text = "-${choice.requiredGold} D",
                                                color = RomanWarningAmber,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (choice.requiredFavor > 0) {
                                            Text(
                                                text = "-${choice.requiredFavor} Lütuf",
                                                color = RomanGoldLight,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (choice.goldDelta > 0) {
                                            Text(
                                                text = "+${choice.goldDelta} D",
                                                color = RomanSuccessGreen,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (choice.reputationDelta != 0) {
                                            val sign = if (choice.reputationDelta > 0) "+" else ""
                                            Text(
                                                text = "$sign${choice.reputationDelta} Nam",
                                                color = if (choice.reputationDelta > 0) RomanSuccessGreen else RomanDangerRed,
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { onResolveChoice(choice) },
                                        colors = ButtonDefaults.buttonColors(containerColor = RomanBronzeDark),
                                        shape = RoundedCornerShape(2.dp),
                                        contentPadding = PaddingValues(4.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(26.dp)
                                    ) {
                                        Text(text = "Hükmü Uygula", color = RomanParchment, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // -------------------------------------------------------------
        // 3. NAVIGATION SUB-TABS
        // -------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (tab in PoliticsSubTab.values()) {
                val isSelected = selectedTab == tab
                Button(
                    onClick = { selectedTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) RomanCrimson else RomanSurfaceVariant
                    ),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, RomanGold) else null,
                    shape = RoundedCornerShape(3.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = tab.title,
                        color = if (isSelected) RomanGoldLight else RomanTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 4. MAIN CONTENT AREA
        // -------------------------------------------------------------
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                PoliticsSubTab.FACTIONS -> {
                    FactionsTabView(
                        factions = state.politicalFactions,
                        characters = state.politicalCharacters,
                        onFilterByFaction = {
                            factionFilter = it
                            selectedTab = PoliticsSubTab.CHARACTERS
                        }
                    )
                }
                PoliticsSubTab.CHARACTERS -> {
                    CharactersTabView(
                        characters = state.politicalCharacters,
                        factions = state.politicalFactions,
                        activePatronId = state.activePatronId,
                        selectedNpc = state.selectedPoliticalNpc,
                        playerSecrets = state.playerSecrets,
                        currentGold = state.dominus.denarii,
                        currentFavor = state.politicalResources.politicalFavor,
                        factionFilter = factionFilter,
                        onFilterChanged = { factionFilter = it },
                        onSelectNpc = onSelectNpc,
                        onExecuteInteraction = onExecuteInteraction
                    )
                }
                PoliticsSubTab.NETWORK -> {
                    PowerNetworkTabView(
                        characters = state.politicalCharacters,
                        factions = state.politicalFactions,
                        connections = state.politicalNetwork,
                        selectedNpc = state.selectedPoliticalNpc,
                        onSelectNpc = onSelectNpc
                    )
                }
                PoliticsSubTab.SCANDALS -> {
                    ScandalsAndSecretsTabView(
                        scandals = state.activeScandals,
                        secrets = state.playerSecrets,
                        characters = state.politicalCharacters,
                        currentGold = state.dominus.denarii,
                        currentFavor = state.politicalResources.politicalFavor,
                        onSuppressScandal = onSuppressScandal,
                        onExposeSecret = onExposeSecret,
                        onBlackmail = { npcId, secretId ->
                            onExecuteInteraction(npcId, PoliticalInteractionType.BLACKMAIL_WITH_SECRET, secretId)
                        }
                    )
                }
                PoliticsSubTab.CALENDAR -> {
                    PoliticalCalendarTabView(
                        calendar = state.politicalCalendar,
                        chronicles = state.chronicles,
                        currentDay = state.dominus.dayNumber
                    )
                }
            }
        }
    }
}

// =====================================================================
// SUB-VIEW 1: FACTIONS & SENATE BALANCES
// =====================================================================
@Composable
private fun FactionsTabView(
    factions: List<PoliticalFaction>,
    characters: List<PoliticalCharacter>,
    onFilterByFaction: (PoliticalFactionId) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(factions) { faction ->
            val opinionColor = when {
                faction.opinionOfPlayer >= 50 -> RomanSuccessGreen
                faction.opinionOfPlayer >= 15 -> RomanGoldLight
                faction.opinionOfPlayer >= -15 -> RomanBronze
                faction.opinionOfPlayer >= -50 -> RomanWarningAmber
                else -> RomanDangerRed
            }

            val opinionLabel = when {
                faction.opinionOfPlayer >= 60 -> "MÜTTEFİK (Sadık İttifak)"
                faction.opinionOfPlayer >= 20 -> "DOSTANE (İltifat Gösteriyor)"
                faction.opinionOfPlayer >= -15 -> "TARAFSIZ (Gözlemliyor)"
                faction.opinionOfPlayer >= -50 -> "SOĞUK (Şüpheci & Mesafeli)"
                else -> "DÜŞMANCA (Baskı & Tehdit Uyguluyor)"
            }

            val members = characters.filter { it.factionId == faction.id }

            RomanCard(
                title = "${faction.id.emblemBadge} ${faction.name}",
                badge = "Nüfuz: %${faction.influence}"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1.8f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = faction.description,
                            color = RomanParchmentDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Opinion Gauge
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Ludus'a Bakış Açısı:", color = RomanTextSecondary, fontSize = 9.sp)
                            Text(
                                text = "$opinionLabel (${faction.opinionOfPlayer})",
                                color = opinionColor,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Gauge Bar (-100 to 100 mapped to 0f..1f)
                        val progress = ((faction.opinionOfPlayer + 100) / 200f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF161210))
                                .border(0.5.dp, RomanBronzeDark, RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progress)
                                    .background(opinionColor)
                            )
                        }

                        // Current Issue
                        Text(
                            text = "Fraksiyonun Güncel Meselesi: ${faction.currentPoliticalIssue}",
                            color = RomanTextMuted,
                            fontSize = 9.sp
                        )
                    }

                    // Faction Perks & Sanctions
                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .background(Color(0xFF1A1411), RoundedCornerShape(3.dp))
                            .border(0.5.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "GÜNCEL ETKİLER",
                            color = RomanGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (faction.activePerks.isNotEmpty()) {
                            for (perk in faction.activePerks) {
                                Text(text = "✓ $perk", color = RomanSuccessGreen, fontSize = 8.5.sp)
                            }
                        }
                        if (faction.activeSanctions.isNotEmpty()) {
                            for (sanction in faction.activeSanctions) {
                                Text(text = "✗ $sanction", color = RomanDangerRed, fontSize = 8.5.sp)
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = { onFilterByFaction(faction.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = RomanBronzeDark),
                            shape = RoundedCornerShape(2.dp),
                            contentPadding = PaddingValues(2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                        ) {
                            Text(text = "Şahsiyetleri İncele (${members.size})", color = RomanGoldLight, fontSize = 8.5.sp)
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// SUB-VIEW 2: CHARACTERS DOSSIER & INTERACTIONS
// =====================================================================
@Composable
private fun CharactersTabView(
    characters: List<PoliticalCharacter>,
    factions: List<PoliticalFaction>,
    activePatronId: String?,
    selectedNpc: PoliticalCharacter?,
    playerSecrets: List<PoliticalSecret>,
    currentGold: Int,
    currentFavor: Int,
    factionFilter: PoliticalFactionId?,
    onFilterChanged: (PoliticalFactionId?) -> Unit,
    onSelectNpc: (PoliticalCharacter) -> Unit,
    onExecuteInteraction: (String, PoliticalInteractionType, String?) -> Unit
) {
    val activeCharacter = selectedNpc ?: characters.firstOrNull()
    val filteredList = if (factionFilter == null) characters else characters.filter { it.factionId == factionFilter }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Left Column: Character List with Faction Filter
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Faction Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Button(
                    onClick = { onFilterChanged(null) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (factionFilter == null) RomanCrimson else RomanSurfaceVariant
                    ),
                    shape = RoundedCornerShape(2.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text(text = "Tümü (${characters.size})", fontSize = 9.sp, color = RomanParchment)
                }

                for (fac in factions) {
                    Button(
                        onClick = { onFilterChanged(fac.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (factionFilter == fac.id) RomanCrimson else RomanSurfaceVariant
                        ),
                        shape = RoundedCornerShape(2.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text(text = "${fac.id.emblemBadge} ${fac.name.take(12)}", fontSize = 9.sp, color = RomanParchment)
                    }
                }
            }

            // Characters Scrollable List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredList) { char ->
                    val isSelected = activeCharacter?.id == char.id
                    val isCurrentPatron = char.id == activePatronId

                    val relColor = when {
                        char.relationshipWithPlayer >= 40 -> RomanSuccessGreen
                        char.relationshipWithPlayer >= 10 -> RomanGoldLight
                        char.relationshipWithPlayer >= -10 -> RomanTextSecondary
                        char.relationshipWithPlayer >= -40 -> RomanWarningAmber
                        else -> RomanDangerRed
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color(0xFF33231B) else Color(0xFF1E1714),
                                RoundedCornerShape(3.dp)
                            )
                            .border(
                                if (isSelected) 1.2.dp else 0.6.dp,
                                if (isSelected) RomanGold else RomanBronzeDark,
                                RoundedCornerShape(3.dp)
                            )
                            .clickable { onSelectNpc(char) }
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = char.name,
                                    color = if (isSelected) RomanGoldLight else RomanParchment,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isCurrentPatron) {
                                    Text(
                                        text = "★ HAMİ",
                                        color = RomanSuccessGreen,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "${char.title} • ${char.personality}",
                                color = RomanTextSecondary,
                                fontSize = 8.5.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "İlişki: ${char.relationshipWithPlayer}",
                                    color = relColor,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Lütuf: ${char.favorsOwedToPlayer} borçlu",
                                    color = RomanGold,
                                    fontSize = 8.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Right Column: Character Dossier & Interactive Decision Panel
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (activeCharacter != null) {
                val hasSecretOnTarget = playerSecrets.any { it.targetNpcId == activeCharacter.id }
                val targetSecrets = playerSecrets.filter { it.targetNpcId == activeCharacter.id }

                RomanCard(
                    title = "ŞAHSİYET DOSYASI: ${activeCharacter.name.uppercase()}",
                    badge = activeCharacter.title
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Character Portrait Badge
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2E1C14))
                                .border(1.dp, RomanBronze, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = activeCharacter.avatarSymbol,
                                fontSize = 28.sp
                            )
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Kişilik Özelliği: ${activeCharacter.personality}",
                                color = RomanGoldLight,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Siyasi Hedef: ${activeCharacter.personalGoals}",
                                color = RomanParchment,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "Siyasi İdeoloji: ${activeCharacter.ideology}",
                                color = RomanTextSecondary,
                                fontSize = 8.5.sp
                            )
                            Text(
                                text = "Servet: ${activeCharacter.wealth} D | Nüfuz: ${activeCharacter.influence} | Prestij: ${activeCharacter.prestige}",
                                color = RomanTextMuted,
                                fontSize = 8.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Character Psyche & Morals
                    RomanStatBar("Hırs (Ambition)", activeCharacter.ambition, 100, RomanCrimson)
                    RomanStatBar("Açgözlülük (Greed)", activeCharacter.greed, 100, RomanWarningAmber)
                    RomanStatBar("Dürüstlük (Integrity)", activeCharacter.integrity, 100, RomanInfoBlue)
                    RomanStatBar("Ludus ile İtimat", activeCharacter.relationshipWithPlayer + 100, 200, RomanGold)

                    if (targetSecrets.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "⚠ ELİNİZDEKİ ŞANTAJ BELGELERİ (${targetSecrets.size}):",
                            color = RomanDangerRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        targetSecrets.forEach { sec ->
                            Text(
                                text = "• ${sec.title}: ${sec.description}",
                                color = RomanMoralePurple,
                                fontSize = 8.5.sp
                            )
                        }
                    }
                }

                // Interactive Actions Card
                RomanCard(title = "SİYASİ HAMLELER VE MÜZAKERE", badge = "ETKİLEŞİM PANELİ") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        // Row 1: Diplomatic Soft Power
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { onExecuteInteraction(activeCharacter.id, PoliticalInteractionType.TALK_FLATTERY, null) },
                                colors = ButtonDefaults.buttonColors(containerColor = RomanSurfaceVariant),
                                shape = RoundedCornerShape(2.dp),
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.weight(1f).height(28.dp)
                            ) {
                                Text(text = "🗣 İltifat & Sohbet (Ücretsiz)", fontSize = 8.5.sp, color = RomanParchment)
                            }

                            Button(
                                onClick = { onExecuteInteraction(activeCharacter.id, PoliticalInteractionType.GIVE_GOLD_GIFT, null) },
                                enabled = currentGold >= 1000,
                                colors = ButtonDefaults.buttonColors(containerColor = RomanBronzeDark),
                                shape = RoundedCornerShape(2.dp),
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.weight(1f).height(28.dp)
                            ) {
                                Text(text = "🎁 Altın Hediye (1.000 D)", fontSize = 8.5.sp, color = RomanGoldLight)
                            }
                        }

                        // Row 2: Hard Bribery & Escort
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { onExecuteInteraction(activeCharacter.id, PoliticalInteractionType.BRIBE_OFFICIAL, null) },
                                enabled = currentGold >= 2500,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B271A)),
                                shape = RoundedCornerShape(2.dp),
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.weight(1f).height(28.dp)
                            ) {
                                Text(text = "💰 Rüşvet Ver (2.500 D -> +1 Lütuf)", fontSize = 8.5.sp, color = RomanGold)
                            }

                            Button(
                                onClick = { onExecuteInteraction(activeCharacter.id, PoliticalInteractionType.ASSIGN_GLADIATOR_ESCORT, null) },
                                colors = ButtonDefaults.buttonColors(containerColor = RomanSurfaceVariant),
                                shape = RoundedCornerShape(2.dp),
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.weight(1f).height(28.dp)
                            ) {
                                Text(text = "🛡 Gladyatör Muhafız Tahsis Et", fontSize = 8.5.sp, color = RomanParchment)
                            }
                        }

                        // Row 3: Calling in Favors (Cash / License / Sabotage)
                        if (activeCharacter.favorsOwedToPlayer > 0) {
                            Text(
                                text = "LÜTUF BORCUNU KULLAN (${activeCharacter.favorsOwedToPlayer} Lütuf Var):",
                                color = RomanGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Button(
                                    onClick = { onExecuteInteraction(activeCharacter.id, PoliticalInteractionType.REQUEST_FAVOR_CASH, null) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanSuccessGreen.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(2.dp),
                                    contentPadding = PaddingValues(2.dp),
                                    modifier = Modifier.weight(1f).height(26.dp)
                                ) {
                                    Text(text = "💵 1.500 D Nakit Talep Et", fontSize = 8.5.sp, color = RomanParchment)
                                }

                                Button(
                                    onClick = { onExecuteInteraction(activeCharacter.id, PoliticalInteractionType.REQUEST_FAVOR_LICENSE, null) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanInfoBlue.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(2.dp),
                                    contentPadding = PaddingValues(2.dp),
                                    modifier = Modifier.weight(1f).height(26.dp)
                                ) {
                                    Text(text = "🏛 Arena Lisansı Al", fontSize = 8.5.sp, color = RomanParchment)
                                }

                                Button(
                                    onClick = { onExecuteInteraction(activeCharacter.id, PoliticalInteractionType.REQUEST_FAVOR_SABOTAGE, null) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanDangerRed.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(2.dp),
                                    contentPadding = PaddingValues(2.dp),
                                    modifier = Modifier.weight(1f).height(26.dp)
                                ) {
                                    Text(text = "⚡ Rakibi Sabote Et", fontSize = 8.5.sp, color = RomanParchment)
                                }
                            }
                        }

                        // Row 4: Espionage & Blackmail
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Button(
                                onClick = { onExecuteInteraction(activeCharacter.id, PoliticalInteractionType.INVESTIGATE_NPC, null) },
                                enabled = currentGold >= 500,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B1D2A)),
                                shape = RoundedCornerShape(2.dp),
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.weight(1f).height(28.dp)
                            ) {
                                Text(text = "🕵 Casus Gönder (500 D)", fontSize = 8.5.sp, color = RomanMoralePurple)
                            }

                            if (hasSecretOnTarget) {
                                Button(
                                    onClick = {
                                        val sec = targetSecrets.first()
                                        onExecuteInteraction(activeCharacter.id, PoliticalInteractionType.BLACKMAIL_WITH_SECRET, sec.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = RomanDangerRed),
                                    shape = RoundedCornerShape(2.dp),
                                    contentPadding = PaddingValues(4.dp),
                                    modifier = Modifier.weight(1f).height(28.dp)
                                ) {
                                    Text(text = "⛓ Şantaj Yap (+2 Lütuf)", fontSize = 8.5.sp, color = RomanGoldLight, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Row 5: Patronage Toggle
                        if (activeCharacter.id == activePatronId) {
                            Button(
                                onClick = { onExecuteInteraction(activeCharacter.id, PoliticalInteractionType.RENOUNCE_PATRONAGE, null) },
                                colors = ButtonDefaults.buttonColors(containerColor = RomanDangerRed.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(2.dp),
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.fillMaxWidth().height(26.dp)
                            ) {
                                Text(text = "✕ Hamilik Anlaşmasını İptal Et (İlişki Düşer)", fontSize = 9.sp, color = RomanParchment)
                            }
                        } else {
                            Button(
                                onClick = { onExecuteInteraction(activeCharacter.id, PoliticalInteractionType.ASK_PATRONAGE, null) },
                                enabled = activeCharacter.relationshipWithPlayer >= 15,
                                colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                                shape = RoundedCornerShape(2.dp),
                                contentPadding = PaddingValues(4.dp),
                                modifier = Modifier.fillMaxWidth().height(26.dp)
                            ) {
                                Text(text = "🛡 Hami (Patronus) Olarak Seç (+Düzenli Ödenek)", fontSize = 9.sp, color = RomanGoldLight)
                            }
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// SUB-VIEW 3: INTERACTIVE POWER NETWORK GRAPH
// =====================================================================
@Composable
private fun PowerNetworkTabView(
    characters: List<PoliticalCharacter>,
    factions: List<PoliticalFaction>,
    connections: List<NetworkConnection>,
    selectedNpc: PoliticalCharacter?,
    onSelectNpc: (PoliticalCharacter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Visual Canvas of Network
        Column(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(
                title = "ROMA GÜÇ AĞI & KULİS ŞEMASI",
                badge = "${connections.size} İTTİFAK & HUSUMET BAĞI",
                modifier = Modifier.weight(1f)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val centerX = w / 2f
                        val centerY = h / 2f
                        val radius = (minOf(w, h) / 2.3f).coerceAtLeast(60f)

                        val charCount = characters.size
                        val nodePositions = characters.mapIndexed { idx, char ->
                            val angle = (2 * Math.PI * idx / charCount) - (Math.PI / 2)
                            val x = centerX + (radius * cos(angle)).toFloat()
                            val y = centerY + (radius * sin(angle)).toFloat()
                            char.id to Offset(x, y)
                        }.toMap()

                        // Draw Connections
                        connections.forEach { conn ->
                            val posA = nodePositions[conn.fromNpcId]
                            val posB = nodePositions[conn.toNpcId]
                            if (posA != null && posB != null) {
                                val lineColor = when (conn.type) {
                                    RelationshipType.FRIENDSHIP, RelationshipType.POLITICAL_ALLIANCE -> RomanSuccessGreen
                                    RelationshipType.PATRONAGE -> RomanGold
                                    RelationshipType.RIVALRY, RelationshipType.FEUD -> RomanDangerRed
                                    RelationshipType.BLACKMAIL -> RomanMoralePurple
                                    RelationshipType.DEBT, RelationshipType.BUSINESS -> RomanWarningAmber
                                    RelationshipType.FAMILY -> RomanInfoBlue
                                }
                                val strokeWidth = (conn.strength.coerceIn(10, 100) / 25f).coerceIn(1.5f, 4.5f)

                                drawLine(
                                    color = lineColor.copy(alpha = 0.75f),
                                    start = posA,
                                    end = posB,
                                    strokeWidth = strokeWidth
                                )
                            }
                        }

                        // Draw Character Nodes
                        characters.forEach { char ->
                            val pos = nodePositions[char.id] ?: return@forEach
                            val isSelected = selectedNpc?.id == char.id
                            val nodeRadius = if (isSelected) 14f else 10f

                            val nodeColor = when (char.factionId) {
                                PoliticalFactionId.SENATORIAL_ELITE -> RomanGold
                                PoliticalFactionId.ARENA_OFFICIALS -> RomanCrimson
                                PoliticalFactionId.MERCHANT_GUILD -> RomanBronze
                                PoliticalFactionId.MILITARY -> RomanDangerRed
                                PoliticalFactionId.IMPERIAL_ADMINISTRATION -> RomanMoralePurple
                                else -> RomanTextSecondary
                            }

                            drawCircle(
                                color = nodeColor,
                                radius = nodeRadius,
                                center = pos
                            )
                            drawCircle(
                                color = if (isSelected) RomanGoldLight else Color.Black,
                                radius = nodeRadius + 2f,
                                center = pos,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                            )
                        }
                    }

                    // Clickable transparent overlay nodes
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val w = maxWidth.value
                        val h = maxHeight.value
                        val centerX = w / 2f
                        val centerY = h / 2f
                        val radius = (minOf(w, h) / 2.3f).coerceAtLeast(60f)

                        characters.forEachIndexed { idx, char ->
                            val angle = (2 * Math.PI * idx / characters.size) - (Math.PI / 2)
                            val x = centerX + (radius * cos(angle)).toFloat()
                            val y = centerY + (radius * sin(angle)).toFloat()

                            Box(
                                modifier = Modifier
                                    .offset(x = (x - 14).dp, y = (y - 14).dp)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .clickable { onSelectNpc(char) }
                            )
                        }
                    }
                }
            }

            // Legend Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1B1512), RoundedCornerShape(2.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "● İttifak", color = RomanSuccessGreen, fontSize = 8.5.sp)
                Text(text = "● Hamilik", color = RomanGold, fontSize = 8.5.sp)
                Text(text = "● Husumet", color = RomanDangerRed, fontSize = 8.5.sp)
                Text(text = "● Şantaj", color = RomanMoralePurple, fontSize = 8.5.sp)
                Text(text = "● Borç", color = RomanWarningAmber, fontSize = 8.5.sp)
            }
        }

        // Right Detail Column
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "AĞ ÇÖZÜMLEMESİ") {
                if (selectedNpc != null) {
                    Text(
                        text = "${selectedNpc.name} Bağları:",
                        color = RomanGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))

                    val relatedConnections = connections.filter {
                        it.fromNpcId == selectedNpc.id || it.toNpcId == selectedNpc.id
                    }

                    if (relatedConnections.isNotEmpty()) {
                        relatedConnections.forEach { conn ->
                            val otherId = if (conn.fromNpcId == selectedNpc.id) conn.toNpcId else conn.fromNpcId
                            val otherChar = characters.find { it.id == otherId }
                            val label = when (conn.type) {
                                RelationshipType.FRIENDSHIP, RelationshipType.POLITICAL_ALLIANCE -> "Gizli İttifak (+${conn.strength})"
                                RelationshipType.PATRONAGE -> "Hamilik & Koruma"
                                RelationshipType.RIVALRY, RelationshipType.FEUD -> "Kan Davası & Husumet (-${conn.strength})"
                                RelationshipType.BLACKMAIL -> "Şantaj Tehdidi Altında"
                                RelationshipType.DEBT, RelationshipType.BUSINESS -> "Mali Borç / Ortaklık"
                                RelationshipType.FAMILY -> "Ailevi Bağlar"
                            }
                            Text(
                                text = "• ${otherChar?.name ?: otherId}: $label",
                                color = RomanParchment,
                                fontSize = 9.sp
                            )
                        }
                    } else {
                        Text(text = "Bilinen doğrudan ittifak veya husumet kaydı yok.", color = RomanTextMuted, fontSize = 9.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Stratejik Fırsat: Bu figürün husumet duyduğu rakipleriyle anlaşarak Senato'daki gücünü kırabilir veya gladyatörünüzü koruma olarak tahsis edebilirsiniz.",
                        color = RomanTextSecondary,
                        fontSize = 8.5.sp,
                        lineHeight = 11.sp
                    )
                } else {
                    Text(
                        text = "İlişkilerini ve düşmanlarını görmek için haritadan veya listeden bir figüre dokunun.",
                        color = RomanTextSecondary,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

// =====================================================================
// SUB-VIEW 4: SCANDALS & SECRETS DOSSIER
// =====================================================================
@Composable
private fun ScandalsAndSecretsTabView(
    scandals: List<PoliticalScandal>,
    secrets: List<PoliticalSecret>,
    characters: List<PoliticalCharacter>,
    currentGold: Int,
    currentFavor: Int,
    onSuppressScandal: (String, Boolean) -> Unit,
    onExposeSecret: (String) -> Unit,
    onBlackmail: (String, String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Left Column: Active Scandals
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(
                title = "AKTİF SİYASİ SKANDALLAR",
                badge = "${scandals.count { it.status != ScandalStatus.SUPPRESSED }} GÜNDEMDE"
            ) {
                if (scandals.isEmpty()) {
                    Text(text = "Şu anda ludusunuzu tehdit eden açık bir skandal bulunmuyor.", color = RomanTextMuted, fontSize = 9.5.sp)
                } else {
                    scandals.forEach { scandal ->
                        val isSuppressed = scandal.status == ScandalStatus.SUPPRESSED

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSuppressed) Color(0xFF191919) else Color(0xFF261616), RoundedCornerShape(3.dp))
                                .border(0.6.dp, if (isSuppressed) RomanBronzeDark else RomanDangerRed, RoundedCornerShape(3.dp))
                                .padding(6.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = scandal.title,
                                    color = if (isSuppressed) RomanTextMuted else RomanGoldLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isSuppressed) "ÖRTBAS EDİLDİ" else "${scandal.daysRemaining} Gün Kaldı",
                                    color = if (isSuppressed) RomanSuccessGreen else RomanDangerRed,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = scandal.description,
                                color = RomanTextSecondary,
                                fontSize = 8.5.sp
                            )

                            if (!isSuppressed) {
                                RomanStatBar("Halkın Haberdar Olma Oranı", scandal.publicAwareness, 100, RomanDangerRed)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Button(
                                        onClick = { onSuppressScandal(scandal.id, true) },
                                        enabled = currentFavor >= scandal.suppressionCostFavor,
                                        colors = ButtonDefaults.buttonColors(containerColor = RomanBronzeDark),
                                        shape = RoundedCornerShape(2.dp),
                                        contentPadding = PaddingValues(2.dp),
                                        modifier = Modifier.weight(1f).height(24.dp)
                                    ) {
                                        Text(text = "Lütuf ile Örtbas (${scandal.suppressionCostFavor} Lütuf)", fontSize = 8.sp, color = RomanGoldLight)
                                    }

                                    Button(
                                        onClick = { onSuppressScandal(scandal.id, false) },
                                        enabled = currentGold >= scandal.suppressionCostGold,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B1E1E)),
                                        shape = RoundedCornerShape(2.dp),
                                        contentPadding = PaddingValues(2.dp),
                                        modifier = Modifier.weight(1f).height(24.dp)
                                    ) {
                                        Text(text = "Rüşvetle Sustur (${scandal.suppressionCostGold} D)", fontSize = 8.sp, color = RomanWarningAmber)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        // Right Column: Discovered Secrets & Blackmail Leverage
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(
                title = "GİZLİ ŞANTAJ DOSYALARI (LEVERAGE)",
                badge = "${secrets.size} DOSYA"
            ) {
                if (secrets.isEmpty()) {
                    Text(
                        text = "Henüz politikacılar hakkında ele geçirdiğiniz şantaj belgesi yok. Casusluk yapmak için şahsiyetler sekmesinden 'Casus Gönder' seçeneğini kullanın.",
                        color = RomanTextMuted,
                        fontSize = 9.sp
                    )
                } else {
                    secrets.forEach { secret ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF221A26), RoundedCornerShape(3.dp))
                                .border(0.6.dp, RomanMoralePurple, RoundedCornerShape(3.dp))
                                .padding(6.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = secret.title,
                                    color = RomanGoldLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (secret.isExposed) "İFŞA EDİLDİ" else "GİZLİ KOZ",
                                    color = if (secret.isExposed) RomanDangerRed else RomanMoralePurple,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "Hedef: ${secret.targetName} | Kategori: ${secret.category.label} | Şiddet: ${secret.severity.label}",
                                color = RomanTextSecondary,
                                fontSize = 8.5.sp
                            )
                            Text(
                                text = secret.description,
                                color = RomanParchmentDark,
                                fontSize = 8.5.sp
                            )

                            if (!secret.isExposed) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Button(
                                        onClick = { onBlackmail(secret.targetNpcId, secret.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = RomanBronzeDark),
                                        shape = RoundedCornerShape(2.dp),
                                        contentPadding = PaddingValues(2.dp),
                                        modifier = Modifier.weight(1f).height(24.dp)
                                    ) {
                                        Text(text = "Şantajda Kullan (+2 Lütuf)", fontSize = 8.sp, color = RomanGoldLight)
                                    }

                                    Button(
                                        onClick = { onExposeSecret(secret.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = RomanDangerRed),
                                        shape = RoundedCornerShape(2.dp),
                                        contentPadding = PaddingValues(2.dp),
                                        modifier = Modifier.weight(1f).height(24.dp)
                                    ) {
                                        Text(text = "Halka İfşa Et! (+300 Şan)", fontSize = 8.sp, color = RomanParchment)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

// =====================================================================
// SUB-VIEW 5: POLITICAL CALENDAR & RECENT DISPATCHES
// =====================================================================
@Composable
private fun PoliticalCalendarTabView(
    calendar: List<PoliticalCalendarEntry>,
    chronicles: List<ChronicleEntry>,
    currentDay: Int
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Left Column: Upcoming Political Calendar
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(
                title = "YAKLAŞAN SİYASİ GELİŞMELER",
                badge = "${calendar.size} OLAY"
            ) {
                val sortedEntries = calendar.sortedBy { it.day }
                for (entry in sortedEntries) {
                    val daysUntil = entry.day - currentDay
                    val dayBadge = if (daysUntil <= 0) "BUGÜN" else "$daysUntil Gün Sonra"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1714), RoundedCornerShape(3.dp))
                            .border(0.6.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = entry.title,
                                    color = RomanGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "[$dayBadge]",
                                    color = if (daysUntil <= 2) RomanDangerRed else RomanGoldLight,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = entry.impactDescription,
                                color = RomanTextSecondary,
                                fontSize = 8.5.sp
                            )
                            Text(
                                text = "Etki Sahası: ${entry.factionId.title}",
                                color = RomanTextMuted,
                                fontSize = 8.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }

        // Right Column: Political Chronicle & Dispatches
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(
                title = "SİYASİ KULİS BÜLTENİ & GÜNCESİ",
                badge = "ROMA DİPLOMASİSİ"
            ) {
                val politicalChronicles = chronicles.filter {
                    it.title.contains("Siyasi") || it.title.contains("Hami") || it.title.contains("Skandal") || it.title.contains("Senato")
                }

                if (politicalChronicles.isEmpty()) {
                    Text(
                        text = "Henüz kayda geçen önemli bir siyasi karar veya kriz bulunmuyor.",
                        color = RomanTextMuted,
                        fontSize = 9.sp
                    )
                } else {
                    politicalChronicles.take(15).forEach { entry ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1C1613), RoundedCornerShape(2.dp))
                                .border(0.5.dp, RomanBronzeDark, RoundedCornerShape(2.dp))
                            .padding(5.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "${entry.title} (${entry.yearAUC} A.U.C.)",
                                color = if (entry.isGlory) RomanGoldLight else RomanWarningAmber,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = entry.description,
                                color = RomanParchment,
                                fontSize = 8.5.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                }
            }
        }
    }
}
