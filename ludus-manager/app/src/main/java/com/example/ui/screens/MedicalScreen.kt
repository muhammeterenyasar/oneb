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
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.theme.*

@Composable
fun MedicalScreen(
    state: LudusUiState,
    onSelectGladiator: (Gladiator) -> Unit,
    onTreatInjury: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val injuredGlads = state.gladiators.filter { it.isInjured }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // COLUMN 1: Medicus & Infirmary Status
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(title = "Hekim & Revir Durumu", badge = "Hastane Seviye 1") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .border(1.dp, RomanBronze, RoundedCornerShape(3.dp))
                            .background(Color(0xFF2B201A), RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = "Medicus",
                            tint = RomanGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Column {
                        Text(text = "Hekim Lucius", color = RomanGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Uzmanlık: Cerrahi & Şifa", color = RomanParchmentDark, fontSize = 9.5.sp)
                        Text(text = "Seviye: 2 (Aylık: 250 Denarii)", color = RomanTextSecondary, fontSize = 9.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "HEKİM TALİMATLARI",
                    color = RomanGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• Temiz bez ve zeytinyağı pansumanı enfeksiyonu önler.\n• Kırık kemikler alçı ve ahşap atellerle sabitlenir.\n• Şarap banyosu kan kaybını durdurur.",
                    color = RomanTextSecondary,
                    fontSize = 9.5.sp,
                    lineHeight = 14.sp
                )
            }
        }

        // COLUMN 2: Injured Gladiators List
        Column(
            modifier = Modifier
                .weight(1.4f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(
                title = "Revirdeki Gladyatörler",
                badge = "${injuredGlads.size} Yaralı Mevcut"
            ) {
                if (injuredGlads.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Safe", tint = RomanSuccessGreen, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Şu anda revirde yatan ağır yaralı dövüşçü yok.", color = RomanParchment, fontSize = 11.sp)
                            Text(text = "Bütün gladyatörler kumların üzerine çıkmaya hazır.", color = RomanTextMuted, fontSize = 9.5.sp)
                        }
                    }
                } else {
                    injuredGlads.forEach { glad ->
                        glad.injuries.forEach { inj ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF281915), RoundedCornerShape(3.dp))
                                    .border(0.6.dp, RomanBronze, RoundedCornerShape(3.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    GladiatorAvatarCanvas(glad.gladiatorClass, 36.dp)
                                    Column {
                                        Text(text = glad.name, color = RomanGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "${inj.name} (${inj.severity})", color = RomanDangerRed, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                        Text(text = "Kalan Süre: ${inj.daysRemaining} Gün | Etki: ${inj.statDebuff}", color = RomanTextSecondary, fontSize = 9.sp)
                                    }
                                }

                                Button(
                                    onClick = { onTreatInjury(glad.id, inj.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RomanCrimson,
                                        contentColor = RomanParchment
                                    ),
                                    shape = RoundedCornerShape(3.dp),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text(text = "TEDAVİ ET (80 💰)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        // COLUMN 3: All Roster Health Summary
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RomanCard(title = "Kadro Sıhhat Durumu") {
                state.gladiators.forEach { glad ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = glad.name, color = RomanParchment, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "HP %${glad.condition.health}",
                                color = if (glad.condition.health > 60) RomanSuccessGreen else RomanDangerRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        RomanStatBar("Dayanıklılık", glad.condition.stamina, 100, RomanStaminaCyan)
                    }
                    HorizontalDivider(color = RomanBronzeDark.copy(alpha = 0.3f), thickness = 0.5.dp)
                }
            }
        }
    }
}
