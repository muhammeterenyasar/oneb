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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
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
import com.example.data.engine.CampaignEngine
import com.example.model.CampaignMission
import com.example.model.Gladiator
import com.example.ui.MainUiState
import com.example.ui.theme.*

@Composable
fun ImperialCampaignScreen(
    state: MainUiState,
    onStartBossFight: (CampaignMission, Gladiator) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedChapter by remember { mutableStateOf(1) }
    var challengeMission by remember { mutableStateOf<CampaignMission?>(null) }
    val readyGladiator = state.selectedGladiator ?: state.gladiators.firstOrNull { !it.isInjured } ?: state.gladiators.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBg)
            .padding(horizontal = 16.dp)
            .testTag("imperial_campaign_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp)
    ) {
        // 1. Campaign Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
                border = BorderStroke(1.2.dp, ImperialRomanRed)
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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🏛️", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "BÜYÜK ROMA SEFERİ",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.5.sp
                                    ),
                                    color = ImperialRomanRed
                                )
                            }
                            Text(
                                text = "4 Bölge Şampiyonunu Devir & Roma Colosseum'una Hükmet",
                                style = MaterialTheme.typography.bodySmall,
                                color = ImmersiveTextMuted
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RomanGoldSurface,
                            border = BorderStroke(1.dp, ImmersiveBorderGold)
                        ) {
                            Text(
                                text = "🏆 ${state.ludusState.completedCampaignMissionIds.size}/${CampaignEngine.allMissions.size} Zafer",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                ),
                                color = RomanImperialGold
                            )
                        }
                    }
                }
            }
        }

        // 2. Chapter Selector Tabs
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf(1 to "1. Capua Çamuru", 2 to "2. Verona & Venatio", 3 to "3. Senato Komploları", 4 to "4. Roma Colosseum")) { (chapter, title) ->
                    val isSelected = selectedChapter == chapter
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) ImperialRomanRed else ImmersiveCard,
                        border = BorderStroke(1.dp, if (isSelected) ImperialRedDark else ImmersiveCardBorder),
                        modifier = Modifier.clickable { selectedChapter = chapter }
                    ) {
                        Text(
                            text = title,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) Color.White else ImmersiveTextPrimary
                        )
                    }
                }
            }
        }

        // 3. Missions for Selected Chapter
        val missions = CampaignEngine.getMissionsForChapter(selectedChapter)
        items(missions, key = { it.id }) { mission ->
            val isCompleted = state.ludusState.completedCampaignMissionIds.contains(mission.id)
            val isUnlocked = CampaignEngine.isMissionUnlocked(mission, state.ludusState.completedCampaignMissionIds)

            CampaignMissionCard(
                mission = mission,
                isCompleted = isCompleted,
                isUnlocked = isUnlocked,
                onChallenge = { challengeMission = mission }
            )
        }
    }

    // Confirmation / Challenger Select Dialog
    if (challengeMission != null && readyGladiator != null) {
        val mission = challengeMission!!
        AlertDialog(
            onDismissRequest = { challengeMission = null },
            title = {
                Text(
                    text = "⚔️ ${mission.missionTitle}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = ImperialRomanRed
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Rakip Boss: ${mission.bossEnemy.name} (${mission.bossEnemy.title})",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = RomanInkDark
                    )
                    Text(
                        text = "Özellik: ${mission.bossEnemy.traitName} • Can: ${mission.bossEnemy.maxHp} HP",
                        style = MaterialTheme.typography.bodySmall,
                        color = ImmersiveTextMuted
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = "Arenaya Sürülecek Gladyatörünüz:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveGold
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RomanCardSecondary,
                        border = BorderStroke(1.dp, ImmersiveCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = readyGladiator.gladiatorClass.icon, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = readyGladiator.name,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                                    color = ImperialRomanRed
                                )
                                Text(
                                    text = "${readyGladiator.careerRank.title} • Güç: ${readyGladiator.totalPowerScore}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = ImmersiveTextMuted
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onStartBossFight(mission, readyGladiator)
                        challengeMission = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ImperialRomanRed, contentColor = Color.White)
                ) {
                    Text("Arenaya Çık & Dövüş!")
                }
            },
            dismissButton = {
                TextButton(onClick = { challengeMission = null }) {
                    Text("Vazgeç", color = ImmersiveTextMuted)
                }
            }
        )
    }
}

@Composable
fun CampaignMissionCard(
    mission: CampaignMission,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    onChallenge: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = ImmersiveCard,
        border = BorderStroke(
            1.2.dp,
            when {
                isCompleted -> Color(0xFF16A34A)
                isUnlocked -> ImperialRomanRed
                else -> ImmersiveCardBorder
            }
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = if (isCompleted) "🏆" else if (isUnlocked) "⚔️" else "🔒", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = mission.missionTitle,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                            color = if (isUnlocked) RomanInkDark else ImmersiveTextMuted
                        )
                        Text(
                            text = "Rakip: ${mission.bossEnemy.name} (${mission.bossEnemy.title})",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ImperialRomanRed
                        )
                    }
                }

                if (isCompleted) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC))
                    ) {
                        Text(
                            text = "TAMAMLANDI",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                            color = Color(0xFF16A34A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mission.description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ImmersiveTextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Boss Traits & Rewards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(RomanCardSecondary)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "ÖZEL NİŞAN / KUPA", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = ImmersiveTextMuted)
                    Text(text = mission.trophyName, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp), color = RomanImperialGold)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "ÖDÜLLER", style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontWeight = FontWeight.Bold), color = ImmersiveTextMuted)
                    Text(text = "+${mission.rewardGold} 🪙 • +${mission.rewardPrestige} 🌿", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp), color = ImperialRomanRed)
                }
            }

            if (!isCompleted && isUnlocked) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onChallenge,
                    colors = ButtonDefaults.buttonColors(containerColor = ImperialRomanRed, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "⚔️ Şampiyona Meydan Oku (Boss Savaşı)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black))
                }
            }
        }
    }
}
