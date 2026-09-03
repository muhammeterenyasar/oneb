package com.example.ui.components

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LudusDominus
import com.example.ui.theme.*

@Composable
fun RomanHeaderBar(
    dominus: LudusDominus,
    onAdvanceDay: () -> Unit,
    onOpenSearch: () -> Unit = {},
    onOpenAttention: () -> Unit = {},
    attentionCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp),
        color = Color(0xFF1B1410),
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF331010),
                            Color(0xFF1E1713),
                            Color(0xFF261D18)
                        )
                    )
                )
                .border(width = 0.8.dp, color = RomanBronzeDark)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // SPQR crest & Game title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(RomanCrimson, RoundedCornerShape(2.dp))
                        .border(1.dp, RomanGold, RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SPQR",
                        color = RomanGold,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column {
                    Text(
                        text = "LUDUS MANAGER",
                        color = RomanGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "BLOOD & GOLD",
                        color = RomanCrimson,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp
                    )
                }
            }

            // Stats row (Denarii, Prestige, Wheat, Date, City)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Denarii
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Denarii",
                        tint = RomanGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "%,d".format(dominus.denarii),
                        color = RomanGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Prestige
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = "Prestige",
                        tint = RomanParchmentDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${dominus.prestige}",
                        color = RomanParchment,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Food / Wheat
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Agriculture,
                        contentDescription = "Wheat",
                        tint = Color(0xFFEAB308),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${dominus.foodWheat}",
                        color = RomanTextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Roman Date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Date",
                        tint = RomanBronze,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${dominus.dayNumber} ${dominus.monthName}, ${dominus.yearAUC}",
                        color = RomanParchmentDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // City
                Box(
                    modifier = Modifier
                        .background(RomanDarkCrimson, RoundedCornerShape(2.dp))
                        .border(0.6.dp, RomanBronze, RoundedCornerShape(2.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = dominus.currentCity.uppercase(),
                        color = RomanGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Actions: Search, Attention Badge, End Day
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Search Button
                IconButton(
                    onClick = onOpenSearch,
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color(0xFF281F19), RoundedCornerShape(3.dp))
                        .border(0.8.dp, RomanBronze, RoundedCornerShape(3.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Ara",
                        tint = RomanGold,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Attention Badge Button
                Button(
                    onClick = onOpenAttention,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (attentionCount > 0) Color(0xFF381F19) else Color(0xFF241A14),
                        contentColor = if (attentionCount > 0) RomanGold else RomanParchmentDark
                    ),
                    shape = RoundedCornerShape(3.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .border(0.8.dp, if (attentionCount > 0) RomanCrimson else RomanBronzeDark, RoundedCornerShape(3.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Dikkat",
                            tint = if (attentionCount > 0) RomanGold else RomanParchmentDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (attentionCount > 0) "$attentionCount DİKKAT" else "BİLDİRİM",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // End Day Button
                Button(
                    onClick = onAdvanceDay,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RomanCrimson,
                        contentColor = RomanParchment
                    ),
                    shape = RoundedCornerShape(3.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .border(1.dp, RomanGold, RoundedCornerShape(3.dp))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Günü Bitir",
                            tint = RomanGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "GÜNÜ BİTİR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }
    }
}
