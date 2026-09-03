package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.simulation.ActiveScreen
import com.example.simulation.LudusUiState
import com.example.ui.components.RomanCard
import com.example.ui.theme.*

@Composable
fun PostMatchScreen(
    state: LudusUiState,
    onReturnToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = state.lastMatchResult ?: return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF16120F))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        RomanCard(
            title = "MAÇ SONUCU VE RAPORU (POST MATCH)",
            badge = result.arenaName,
            modifier = Modifier.width(520.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // VICTORY / DEFEAT BANNER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (result.playerWon) RomanSuccessGreen.copy(alpha = 0.25f) else RomanDangerRed.copy(alpha = 0.25f),
                            RoundedCornerShape(4.dp)
                        )
                        .border(
                            1.dp,
                            if (result.playerWon) RomanSuccessGreen else RomanDangerRed,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (result.playerWon) "ŞAN VE ZAFER! (VICTORIA)" else "MAĞLUBİYET",
                            color = if (result.playerWon) RomanGoldLight else RomanDangerRed,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = if (result.playerWon) "Rakip ${result.opponentName} dize getirildi!" else "${result.opponentName} üstün geldi.",
                            color = RomanParchment,
                            fontSize = 11.sp
                        )
                    }
                }

                // MATCH STATS GRID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Süre", color = RomanTextSecondary, fontSize = 10.sp)
                        Text(text = "${result.durationSeconds} sn", color = RomanParchment, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "İsabetli Vuruş", color = RomanTextSecondary, fontSize = 10.sp)
                        Text(text = "${result.playerHits}", color = RomanGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Kalkan Bloğu", color = RomanTextSecondary, fontSize = 10.sp)
                        Text(text = "${result.playerBlocks}", color = Color(0xFF60A5FA), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Kritik Darbe", color = RomanTextSecondary, fontSize = 10.sp)
                        Text(text = "${result.playerCriticals}", color = RomanDangerRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)

                // REWARDS & GAINS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = "Gold", tint = RomanGold, modifier = Modifier.size(16.dp))
                        Text(text = "+${result.goldReward} Denarii", color = RomanGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.MilitaryTech, contentDescription = "Prestige", tint = RomanParchmentDark, modifier = Modifier.size(16.dp))
                        Text(text = "${if (result.prestigeReward > 0) "+" else ""}${result.prestigeReward} Prestij", color = RomanParchment, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ThumbUp, contentDescription = "Favor", tint = Color(0xFFEAB308), modifier = Modifier.size(16.dp))
                        Text(text = "${if (result.crowdFavorDelta > 0) "+" else ""}${result.crowdFavorDelta}% Halk Sevgisi", color = Color(0xFFEAB308), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // INJURY NOTICE (IF ANY)
                if (result.injurySuffered != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(RomanDangerRed.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "⚠ Sakatlık: ${result.injurySuffered.name} (${result.injurySuffered.daysRemaining} gün tedavi gerekiyor)",
                            color = RomanDangerRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onReturnToDashboard,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RomanCrimson,
                        contentColor = RomanParchment
                    ),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .border(1.dp, RomanGold, RoundedCornerShape(3.dp))
                ) {
                    Text(
                        text = "LUDUS'A DÖN (RETURN TO LUDUS)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}
