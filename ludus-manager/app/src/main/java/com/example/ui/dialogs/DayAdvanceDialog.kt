package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DayAdvanceSummary
import com.example.simulation.LudusUiState
import com.example.ui.components.RomanCard
import com.example.ui.theme.*

/**
 * Pre-flight confirmation dialog shown before ending the day.
 * Alerts the Lanista of any urgent matters requiring attention before time advances.
 */
@Composable
fun EndDayChecklistDialog(
    state: LudusUiState,
    onConfirmAdvance: () -> Unit,
    onDismiss: () -> Unit
) {
    val warnings = mutableListOf<String>()

    // Check if fight tomorrow
    val tomorrowBout = state.arenaCalendar.find { it.day == state.dominus.dayNumber + 1 && it.isPlayerMatch }
    if (tomorrowBout != null) {
        warnings.add("⚔ YARIN RESMİ MÜSABAKA: ${tomorrowBout.fighter1Name} ringe çıkacak!")
    }

    // Check for severe injuries
    val severeInjured = state.gladiators.filter { it.injuries.any { inj -> inj.severity == "Ağır" } }
    if (severeInjured.isNotEmpty()) {
        warnings.add("🩸 AĞIR YARALI: ${severeInjured.joinToString { it.name }} revirde tedavi bekliyor!")
    }

    // Check low wheat
    if (state.dominus.foodWheat < 300) {
        warnings.add("🌾 DÜŞÜK ERZAK: Tahıl ambarı tükenmek üzere (${state.dominus.foodWheat} ölçek)!")
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(440.dp)
                .background(Color(0xFF1E1713), RoundedCornerShape(6.dp))
                .border(1.2.dp, RomanGold, RoundedCornerShape(6.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "GÜNÜ BİTİR: Gün ${state.dominus.dayNumber} → ${state.dominus.dayNumber + 1}",
                    color = RomanGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(color = RomanBronzeDark, thickness = 0.8.dp)

                if (warnings.isEmpty()) {
                    Text(
                        text = "Kışlada her şey yolunda. Dövüşçüler dinleniyor, yarın için kritik bir engel bulunmuyor.",
                        color = RomanParchment,
                        fontSize = 10.sp
                    )
                } else {
                    Text(
                        text = "DİKKAT! Zamanı ilerletmeden önce şu konuları gözden geçirmek isteyebilirsiniz:",
                        color = RomanGoldLight,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    warnings.forEach { warn ->
                        Text(
                            text = warn,
                            color = if (warn.contains("AĞIR") || warn.contains("MÜSABAKA")) RomanDangerRed else RomanGoldLight,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2B2019),
                            contentColor = RomanParchment
                        ),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier.weight(1f).height(34.dp)
                    ) {
                        Text("Vazgeç & İncele", fontSize = 9.5.sp)
                    }

                    Button(
                        onClick = onConfirmAdvance,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RomanCrimson,
                            contentColor = RomanGold
                        ),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier.weight(1f).height(34.dp).border(0.8.dp, RomanGold, RoundedCornerShape(3.dp))
                    ) {
                        Text("Yine de İlerle ⏭", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Concise "What Changed?" summary dialog presented immediately after day advance.
 */
@Composable
fun WhatChangedDialog(
    summary: DayAdvanceSummary,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(460.dp)
                .background(Color(0xFF1E1713), RoundedCornerShape(6.dp))
                .border(1.2.dp, RomanGold, RoundedCornerShape(6.dp))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NE DEĞİŞTİ? (GÜN ${summary.fromDay} → ${summary.toDay})",
                        color = RomanGold,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Günlük Bülten",
                        color = RomanParchmentDark,
                        fontSize = 9.sp
                    )
                }

                HorizontalDivider(color = RomanBronzeDark, thickness = 0.8.dp)

                // Recoveries
                if (summary.recoveries.isNotEmpty()) {
                    Text(text = "SAĞLIK & İYİLEŞME:", color = RomanSuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    summary.recoveries.forEach { Text(text = "• $it", color = RomanParchment, fontSize = 9.sp) }
                }

                // Market & Economy
                if (summary.marketChanges.isNotEmpty()) {
                    Text(text = "PAZAR & TİCARET:", color = RomanGoldLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    summary.marketChanges.forEach { Text(text = "• $it", color = RomanParchment, fontSize = 9.sp) }
                }

                // Arena & Bouts
                if (summary.boutResults.isNotEmpty()) {
                    Text(text = "ARENA GELİŞMELERİ:", color = RomanCrimson, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    summary.boutResults.forEach { Text(text = "• $it", color = RomanParchment, fontSize = 9.sp) }
                }

                // Story & Politics
                if (summary.storyAlerts.isNotEmpty() || summary.politicalChanges.isNotEmpty()) {
                    Text(text = "SİYASET & DÜNYA:", color = RomanGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    summary.politicalChanges.forEach { Text(text = "• $it", color = RomanParchment, fontSize = 9.sp) }
                    summary.storyAlerts.forEach { Text(text = "• $it", color = RomanGoldLight, fontSize = 9.sp) }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RomanCrimson,
                        contentColor = RomanGold
                    ),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier.fillMaxWidth().height(34.dp).border(0.8.dp, RomanGold, RoundedCornerShape(3.dp))
                ) {
                    Text("Anlaşıldı / Kışlaya Dön", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
