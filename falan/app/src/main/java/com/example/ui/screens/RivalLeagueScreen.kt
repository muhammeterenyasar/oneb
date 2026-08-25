package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Shield
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
import com.example.data.engine.RivalLeagueEngine
import com.example.model.RivalLudus
import com.example.ui.MainUiState
import com.example.ui.theme.*

@Composable
fun RivalLeagueScreen(
    state: MainUiState,
    modifier: Modifier = Modifier
) {
    val standings = RivalLeagueEngine.calculateLeagueStandings(state.ludusState, state.gladiators)
    val playerRank = standings.indexOfFirst { it.isPlayerSchool } + 1

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RomanMarbleBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rival_league_header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RomanCardBg),
                border = BorderStroke(2.dp, Brush.horizontalGradient(listOf(ImperialRomanRed, RomanImperialGold)))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(ImperialRomanRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "İMPARATORLUK GLADYATÖR LİGİ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ImperialRomanRed,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Roma Senatosu & Amfitiyatro Okul Sıralaması",
                                fontSize = 12.sp,
                                color = RomanInkMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = ImmersiveCardBorderLight)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "LUDUS DERECENİZ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RomanInkMuted
                            )
                            Text(
                                text = when (playerRank) {
                                    1 -> "🥇 1. SIRA (LİDER)"
                                    2 -> "🥈 2. SIRA (ZİRVE TAKİBİ)"
                                    3 -> "🥉 3. SIRA (PODYUM)"
                                    else -> "🏛️ $playerRank. SIRA"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (playerRank <= 3) ImperialRomanRed else RomanInkDark
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "TOPLAM LİG PUANI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RomanInkMuted
                            )
                            val playerSchool = standings.find { it.isPlayerSchool }
                            Text(
                                text = "${playerSchool?.points ?: 0} PTS (🏆 ${state.ludusState.totalWins}G / ${state.ludusState.totalFights}M)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = RomanImperialGold
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "🏛️ RESMİ OKUL SIRALAMA TABLOSU",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ImperialRomanRed,
                letterSpacing = 0.5.sp
            )
        }

        itemsIndexed(standings) { index, school ->
            RivalLudusStandingCard(
                rank = index + 1,
                school = school
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RivalLudusStandingCard(
    rank: Int,
    school: RivalLudus
) {
    val isLeader = rank == 1
    val isPlayer = school.isPlayerSchool

    val cardBorder = when {
        isPlayer -> BorderStroke(2.dp, ImperialRomanRed)
        isLeader -> BorderStroke(2.dp, RomanImperialGold)
        else -> BorderStroke(1.dp, ImmersiveCardBorder)
    }

    val cardBg = when {
        isPlayer -> ImperialRedSurface
        isLeader -> RomanGoldSurface
        else -> RomanCardBg
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rival_school_card_${school.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = cardBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        when (rank) {
                            1 -> RomanImperialGold
                            2 -> Color(0xFF9E9E9E)
                            3 -> Color(0xFFCD7F32)
                            else -> if (isPlayer) ImperialRomanRed else RomanCardSecondary
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3 || isPlayer) Color.White else RomanInkDark
                )
            }

            // School Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = school.badgeIcon,
                        fontSize = 16.sp
                    )
                    Text(
                        text = school.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPlayer) ImperialRomanRed else RomanInkDark
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Lanista: ${school.lanistaName} • 📍 ${school.city}",
                    fontSize = 11.sp,
                    color = RomanInkMedium
                )

                Text(
                    text = "Baş Dövüşçü: ${school.championName}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ImperialRedDark
                )

                Text(
                    text = school.schoolDoctrine,
                    fontSize = 10.sp,
                    color = RomanInkMuted
                )
            }

            // Points & Record
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${school.points} PTS",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isLeader) RomanImperialGold else ImperialRomanRed
                )
                Text(
                    text = "${school.wins}G - ${school.losses}M",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RomanInkMuted
                )
            }
        }
    }
}
