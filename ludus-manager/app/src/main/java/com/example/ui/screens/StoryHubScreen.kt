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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.simulation.ActiveScreen
import com.example.simulation.LudusUiState
import com.example.ui.components.RomanCard
import com.example.ui.theme.*

/**
 * The Story Discovery & World Memory Hub (Dünya Hafızası & Kronikler).
 * Rich, 3-pane landscape interface giving the player access to:
 * 1. Kronikler (Annals of Blood & Glory)
 * 2. Söylenti Ağı (Rumor Network & Gossip Distortion)
 * 3. Gizemler & Soruşturmalar (World Mysteries & Evidence Board)
 * 4. Hikâye Kolları (Persistent Story Threads & Narrative Arcs)
 * 5. Karakter Dosyaları (NPC Memory & Agendas)
 * 6. Teşhis Paneli (Developer Diagnostics & World Memory)
 */
@Composable
fun StoryHubScreen(
    state: LudusUiState,
    onSelectTab: (StoryHubTab) -> Unit,
    onSelectMystery: (String?) -> Unit,
    onSelectRumor: (String?) -> Unit,
    onInvestigateMystery: (String, String) -> Unit,
    onInvestigateRumor: (String) -> Unit,
    onNavigate: (ActiveScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTab = state.selectedStoryHubTab

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // =========================================================
        // TOP NAVIGATION BAR: 6 CATEGORY TABS
        // =========================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1713), RoundedCornerShape(4.dp))
                .border(0.8.dp, RomanBronzeDark, RoundedCornerShape(4.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StoryHubTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                Button(
                    onClick = { onSelectTab(tab) },
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
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "${tab.iconSymbol} ${tab.title}",
                        fontSize = 9.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // =========================================================
        // MAIN 3-PANE LANDSCAPE WORKBENCH
        // =========================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            when (currentTab) {
                StoryHubTab.CHRONICLE -> {
                    ChronicleHubPane(state = state, modifier = Modifier.fillMaxSize())
                }
                StoryHubTab.RUMORS -> {
                    RumorsHubPane(
                        state = state,
                        onSelectRumor = onSelectRumor,
                        onInvestigateRumor = onInvestigateRumor,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                StoryHubTab.MYSTERIES -> {
                    MysteriesHubPane(
                        state = state,
                        onSelectMystery = onSelectMystery,
                        onInvestigateMystery = onInvestigateMystery,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                StoryHubTab.STORY_THREADS -> {
                    StoryThreadsHubPane(state = state, modifier = Modifier.fillMaxSize())
                }
                StoryHubTab.CHARACTER_DOSSIERS -> {
                    CharacterDossiersHubPane(state = state, modifier = Modifier.fillMaxSize())
                }
                StoryHubTab.DIAGNOSTICS -> {
                    StoryDiagnosticsHubPane(state = state, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. CHRONICLE PANE (ANNALS OF BLOOD & GLORY)
// -------------------------------------------------------------
@Composable
private fun ChronicleHubPane(
    state: LudusUiState,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        // Left & Center Column: Chronicle Timeline
        RomanCard(
            title = "Ludus Tarihçesi (The Annals of Blood & Gold)",
            badge = "${state.chronicles.size} Kayıt",
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
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
                                .background(
                                    if (entry.isGlory) RomanSuccessGreen.copy(alpha = 0.2f) else RomanDangerRed.copy(alpha = 0.2f),
                                    RoundedCornerShape(16.dp)
                                )
                                .border(
                                    1.dp,
                                    if (entry.isGlory) RomanSuccessGreen else RomanDangerRed,
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (entry.isGlory) "VICT" else "FAT",
                                color = if (entry.isGlory) RomanSuccessGreen else RomanDangerRed,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = entry.title,
                                    color = RomanGold,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = entry.dateText,
                                    color = RomanParchmentDark,
                                    fontSize = 9.5.sp
                                )
                            }
                            Text(
                                text = entry.description,
                                color = RomanParchment,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Right Column: Historical Overview Dossier
        RomanCard(
            title = "Tarihî Özet & Miras",
            badge = state.dominus.yearAUC,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "LUDUS VALERIUS ARŞİVİ",
                    color = RomanGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Bu kronikler, Capua kumlarında dökülen kanların, kazanılan şereflerin ve Roma meclislerindeki zaferlerin kalıcı kaydıdır. Hiçbir kahraman unutulmaz, hiçbir düşman affedilmez.",
                    color = RomanTextSecondary,
                    fontSize = 9.5.sp
                )

                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)

                Text(
                    text = "KÜMÜLATİF ŞAN: ${state.dominus.prestige}",
                    color = RomanGoldLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "DÜŞEN KAHRAMANLAR: ${state.fallenGladiators.size}",
                    color = RomanDangerRed,
                    fontSize = 10.sp
                )
                Text(
                    text = "DEVAM EDEN HUSUMETLER: ${state.activeRivalries.size}",
                    color = RomanCrimson,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. RUMORS PANE (RUMOR NETWORK & DISTORTION)
// -------------------------------------------------------------
@Composable
private fun RumorsHubPane(
    state: LudusUiState,
    onSelectRumor: (String?) -> Unit,
    onInvestigateRumor: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedRumor = state.rumors.find { it.id == state.selectedRumorId } ?: state.rumors.firstOrNull()

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        // Pane 1: Rumor List
        RomanCard(
            title = "Şehir Fısıltıları & Dedikodular",
            badge = "${state.rumors.count { !it.isExpired }} Aktif Söylenti",
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.rumors.filter { !it.isExpired }) { rumor ->
                    val isSelected = rumor.id == selectedRumor?.id
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color(0xFF382319) else Color(0xFF221814),
                                RoundedCornerShape(3.dp)
                            )
                            .border(
                                0.8.dp,
                                if (isSelected) RomanGold else RomanBronzeDark,
                                RoundedCornerShape(3.dp)
                            )
                            .clickable { onSelectRumor(rumor.id) }
                            .padding(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rumor.source.label,
                                color = RomanGoldLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Yayılma: ${rumor.spreadCount} gün",
                                color = RomanParchmentDark,
                                fontSize = 8.5.sp
                            )
                        }
                        Text(
                            text = rumor.headline,
                            color = if (isSelected) RomanGold else RomanParchment,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = rumor.location,
                                color = RomanTextSecondary,
                                fontSize = 8.5.sp
                            )
                            if (rumor.isInvestigated) {
                                Text(
                                    text = "✓ TAHKİK EDİLDİ",
                                    color = RomanSuccessGreen,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pane 2: Deep Rumor Inspector
        RomanCard(
            title = "Dedikodu Detayı & İstihbarat Analizi",
            badge = selectedRumor?.location ?: "Forum",
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
        ) {
            if (selectedRumor != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = selectedRumor.headline,
                        color = RomanGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kaynak: ${selectedRumor.source.label} | Konum: ${selectedRumor.location}",
                        color = RomanParchmentDark,
                        fontSize = 9.sp
                    )

                    HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)

                    Text(
                        text = "KULAKTAN KULAĞA YAYILAN METİN:",
                        color = RomanGoldLight,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedRumor.fullGossipText,
                        color = RomanParchment,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "KAYNAK GÜVENİLİRLİĞİ: ${selectedRumor.displayReliability}",
                        color = if (selectedRumor.reliability >= 0.7f) RomanSuccessGreen else RomanDangerRed,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (selectedRumor.distortionLevel > 0) {
                        Text(
                            text = "⚠ Söylenti sokaklarda dolaşırken ${selectedRumor.distortionLevel}. derece çarpıtıldı!",
                            color = RomanGoldLight,
                            fontSize = 9.sp
                        )
                    }

                    if (selectedRumor.isInvestigated) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E2818), RoundedCornerShape(3.dp))
                                .border(0.8.dp, RomanSuccessGreen, RoundedCornerShape(3.dp))
                                .padding(6.dp)
                        ) {
                            Column {
                                Text(
                                    text = "KESİNLEŞEN GERÇEK:",
                                    color = RomanSuccessGreen,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = selectedRumor.investigationLead ?: "Söylenti tahkik edildi.",
                                    color = RomanParchment,
                                    fontSize = 9.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Pane 3: Rumor Actions
        RomanCard(
            title = "İstihbarat Eylemi",
            badge = "Tahkikat",
            modifier = Modifier
                .weight(0.9f)
                .fillMaxHeight()
        ) {
            if (selectedRumor != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "MUHBİR GÖREVLENDİR",
                            color = RomanGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Subura meyhanelerine veya Senato kâtiplerine altın uzatarak dedikodunun aslını öğrenin.",
                            color = RomanTextSecondary,
                            fontSize = 9.sp
                        )

                        Text(
                            text = "Tahkikat Masrafı: ${selectedRumor.investigationCostDenarii} Denarii",
                            color = RomanGoldLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { onInvestigateRumor(selectedRumor.id) },
                        enabled = !selectedRumor.isInvestigated && state.dominus.denarii >= selectedRumor.investigationCostDenarii,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RomanCrimson,
                            disabledContainerColor = Color(0xFF281F19),
                            contentColor = RomanParchment,
                            disabledContentColor = RomanParchmentDark
                        ),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .border(1.dp, RomanGold, RoundedCornerShape(3.dp))
                    ) {
                        Text(
                            text = if (selectedRumor.isInvestigated) "TAHKİK EDİLDİ" else "GERÇEĞİ ARAŞTIR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. MYSTERIES PANE (WORLD MYSTERIES & EVIDENCE BOARD)
// -------------------------------------------------------------
@Composable
private fun MysteriesHubPane(
    state: LudusUiState,
    onSelectMystery: (String?) -> Unit,
    onInvestigateMystery: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedMystery = state.mysteries.find { it.id == state.selectedMysteryId } ?: state.mysteries.firstOrNull()

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        // Pane 1: Mysteries List
        RomanCard(
            title = "Çözülememiş Gizemler",
            badge = "${state.mysteries.count { it.status != MysteryStatus.RESOLVED }} Açık Dosya",
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.mysteries) { mystery ->
                    val isSelected = mystery.id == selectedMystery?.id
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color(0xFF382319) else Color(0xFF221814),
                                RoundedCornerShape(3.dp)
                            )
                            .border(
                                0.8.dp,
                                if (isSelected) RomanGold else RomanBronzeDark,
                                RoundedCornerShape(3.dp)
                            )
                            .clickable { onSelectMystery(mystery.id) }
                            .padding(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${mystery.category.iconSymbol} ${mystery.category.label}",
                                color = RomanGoldLight,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = mystery.status.label,
                                color = if (mystery.status == MysteryStatus.RESOLVED) RomanSuccessGreen else RomanGold,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = mystery.title,
                            color = if (isSelected) RomanGold else RomanParchment,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Deliller: ${mystery.discoveredEvidence.size} Keşif",
                            color = RomanTextSecondary,
                            fontSize = 8.5.sp
                        )
                    }
                }
            }
        }

        // Pane 2: Evidence Board & Dossier
        RomanCard(
            title = "Soruşturma Dosyası & Delil Panosu",
            badge = selectedMystery?.status?.label ?: "Dosya",
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        ) {
            if (selectedMystery != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = selectedMystery.title,
                        color = RomanGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedMystery.initialClue,
                        color = RomanParchment,
                        fontSize = 9.5.sp,
                        lineHeight = 13.sp
                    )

                    HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)

                    // Known Facts
                    Text(
                        text = "BİLİNEN GERÇEKLER:",
                        color = RomanGoldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    selectedMystery.knownFacts.forEach { fact ->
                        Text(text = "• $fact", color = RomanParchment, fontSize = 9.sp)
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Discovered Evidence
                    Text(
                        text = "ELE GEÇİRİLEN DELİLLER (${selectedMystery.discoveredEvidence.size}):",
                        color = RomanGoldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (selectedMystery.discoveredEvidence.isEmpty()) {
                        Text(text = "Henüz somut delil bulunamadı.", color = RomanTextSecondary, fontSize = 8.5.sp)
                    } else {
                        selectedMystery.discoveredEvidence.forEach { ev ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF2B1F19), RoundedCornerShape(3.dp))
                                    .border(0.6.dp, RomanBronze, RoundedCornerShape(3.dp))
                                    .padding(4.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "🔍 ${ev.title}", color = RomanGold, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                        Text(text = ev.epistemicStatus.title, color = RomanSuccessGreen, fontSize = 8.sp)
                                    }
                                    Text(text = ev.description, color = RomanParchment, fontSize = 8.5.sp)
                                }
                            }
                        }
                    }

                    if (selectedMystery.status == MysteryStatus.RESOLVED) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1E2818), RoundedCornerShape(3.dp))
                                .border(0.8.dp, RomanSuccessGreen, RoundedCornerShape(3.dp))
                                .padding(6.dp)
                        ) {
                            Column {
                                Text(text = "🏆 GİZEM ÇÖZÜLDÜ!", color = RomanSuccessGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                Text(text = selectedMystery.resolutionSummary ?: "", color = RomanParchment, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }

        // Pane 3: Investigation Paths (Action)
        RomanCard(
            title = "Soruşturma Hamleleri",
            badge = "Eylemler",
            modifier = Modifier
                .weight(0.9f)
                .fillMaxHeight()
        ) {
            if (selectedMystery != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "TAKİP EDİLEBİLİR İZLER:",
                        color = RomanGold,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    selectedMystery.investigationPaths.forEach { path ->
                        Button(
                            onClick = { onInvestigateMystery(selectedMystery.id, path.id) },
                            enabled = !path.isExecuted && selectedMystery.status != MysteryStatus.RESOLVED && state.dominus.denarii >= path.costDenarii,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (path.isExecuted) Color(0xFF281F19) else RomanCrimson,
                                contentColor = RomanParchment
                            ),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.8.dp, RomanBronze, RoundedCornerShape(3.dp)),
                            contentPadding = PaddingValues(6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = path.label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (path.isExecuted) "✓ Yürütüldü" else "Maliyet: ${path.costDenarii} Denarii",
                                    fontSize = 8.sp,
                                    color = RomanGoldLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. STORY THREADS PANE (PERSISTENT NARRATIVE ARCS)
// -------------------------------------------------------------
@Composable
private fun StoryThreadsHubPane(
    state: LudusUiState,
    modifier: Modifier = Modifier
) {
    RomanCard(
        title = "Devam Eden Hikâye Kolları & İttifak Dengeleri",
        badge = "${state.storyThreads.size} Hikâye Kolu",
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(state.storyThreads) { thread ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF221814), RoundedCornerShape(3.dp))
                        .border(0.8.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = thread.title,
                            color = RomanGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tehlike: %${thread.urgency * 10}",
                            color = if (thread.urgency >= 7) RomanDangerRed else RomanGoldLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = thread.synopsis,
                        color = RomanParchment,
                        fontSize = 9.5.sp
                    )

                    HorizontalDivider(color = RomanBronzeDark, thickness = 0.5.dp)

                    Text(
                        text = "AŞAMA ${thread.currentStageIndex + 1} / ${thread.stages.size}: ${thread.stages.getOrNull(thread.currentStageIndex)?.title ?: ""}",
                        color = RomanGoldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = thread.stages.getOrNull(thread.currentStageIndex)?.narrativeDescription ?: "",
                        color = RomanTextSecondary,
                        fontSize = 8.5.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. CHARACTER DOSSIERS PANE (NPC MEMORY & AGENDAS)
// -------------------------------------------------------------
@Composable
private fun CharacterDossiersHubPane(
    state: LudusUiState,
    modifier: Modifier = Modifier
) {
    RomanCard(
        title = "Karakter Hafızası & Roma Şahsiyetleri Dosyaları",
        badge = "${state.characterMemories.size} Kayıtlı Şahsiyet",
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(state.characterMemories.values.toList()) { charMem ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF221814), RoundedCornerShape(3.dp))
                        .border(0.8.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = charMem.npcName,
                            color = RomanGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hedef: ${charMem.currentAgenda.label}",
                            color = RomanGoldLight,
                            fontSize = 9.sp
                        )
                    }

                    Text(
                        text = "Gizli Niyet: ${charMem.currentGoal}",
                        color = RomanTextSecondary,
                        fontSize = 9.sp
                    )

                    // Attitude metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Güven: %${charMem.trust}", color = RomanSuccessGreen, fontSize = 8.5.sp)
                        Text(text = "Saygı: %${charMem.respect}", color = RomanGold, fontSize = 8.5.sp)
                        Text(text = "Korku: %${charMem.fear}", color = RomanTextSecondary, fontSize = 8.5.sp)
                        Text(text = "Nefret: %${charMem.hatred}", color = RomanDangerRed, fontSize = 8.5.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. STORY DIAGNOSTICS PANE (DEVELOPER WORLD MEMORY INSPECTION)
// -------------------------------------------------------------
@Composable
private fun StoryDiagnosticsHubPane(
    state: LudusUiState,
    modifier: Modifier = Modifier
) {
    RomanCard(
        title = "Geliştirici Teşhis Paneli: Dünya Hafızası (World Memory)",
        badge = "${state.worldMemory.size} Olay Kaydı",
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(state.worldMemory) { mem ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1713), RoundedCornerShape(3.dp))
                        .border(0.6.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                        .padding(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = mem.eventType.label, color = RomanGold, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Gün ${mem.date} • ${mem.location}", color = RomanParchmentDark, fontSize = 8.5.sp)
                    }
                    Text(text = mem.causeDescription, color = RomanParchment, fontSize = 9.sp)
                    if (mem.hiddenFacts != null) {
                        Text(text = "Gizli Gerçek: ${mem.hiddenFacts}", color = RomanDangerRed, fontSize = 8.5.sp)
                    }
                }
            }
        }
    }
}
