package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun RomanHeaderBanner(
    ludusState: LudusState,
    onAdvancePhaseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("roman_header_banner"),
        color = ImmersiveCard,
        border = BorderStroke(1.dp, ImmersiveCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top Row: Ludus Identity & Resources
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Title and City / Day Header
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ImperialRomanRed,
                            border = BorderStroke(1.dp, ImperialRedDark)
                        ) {
                            Text(
                                text = "🏛️ ${ludusState.day}. GÜN • ${ludusState.phase.title.uppercase()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "📍 ${ludusState.cityTier.cityName}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                            color = ImmersiveTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "LUDUS MAGNUS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Serif,
                            letterSpacing = 1.sp
                        ),
                        color = ImperialRomanRed
                    )
                }

                // Currency & Prestige Chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Gold Display Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = RomanGoldSurface,
                        border = BorderStroke(1.2.dp, ImmersiveBorderGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🪙", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${ludusState.gold}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = RomanImperialGold
                            )
                        }
                    }

                    // Prestige Display Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0FDF4),
                        border = BorderStroke(1.2.dp, Color(0xFF86EFAC))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🌿", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${ludusState.prestige}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = Color(0xFF16A34A)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-row: Phase Status & Advance Phase CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reputation & Threat Tag
                val diffPercent = (ludusState.opponentDifficultyModifier * 100).toInt()
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RomanCardSecondary,
                    border = BorderStroke(0.8.dp, ImmersiveCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ludusState.reputationTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = if (ludusState.ruthlessnessScore > ludusState.mercyScore) ImperialRomanRed else Color(0xFF16A34A)
                        )
                        Text(
                            text = " • Tehdit: %$diffPercent",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = if (diffPercent > 105) ImperialRomanRed else ImmersiveTextMuted
                        )
                    }
                }

                Button(
                    onClick = onAdvancePhaseClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImperialRomanRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("advance_day_phase_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Vakti İlerlet",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun ThreatWarningBanner(
    ludusState: LudusState,
    onManageDebtClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (ludusState.activeDebt > 0 || ludusState.threatStage != ThreatStage.NONE) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onManageDebtClick() }
                .testTag("threat_warning_banner"),
            color = ImmersiveTerracottaDeep.copy(alpha = 0.85f),
            border = BorderStroke(1.dp, ImmersiveWarningBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚠️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "TEFECİ TEHDİDİ: ${ludusState.threatStage.title.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = ImmersiveWarningText
                        )
                        Text(
                            text = "Borç: ${ludusState.activeDebt} 🪙 • Ödeme Yap",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = ImmersiveTextPrimary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ImmersiveTerracotta
                ) {
                    Text(
                        text = "BORCU ÖDE",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun GladiatorStatRow(
    label: String,
    value: Int,
    color: Color,
    icon: ImageVector,
    maxValue: Int = 30,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            color = ImmersiveTextPrimary,
            modifier = Modifier.width(90.dp)
        )
        LinearProgressIndicator(
            progress = { (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = ImmersiveTrack
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$value",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            ),
            color = color,
            modifier = Modifier.width(24.dp)
        )
    }
}

@Composable
fun GladiatorClassBadge(
    gladiatorClass: GladiatorClass,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = ImmersiveTrack,
        border = BorderStroke(1.dp, ImmersiveCardBorder)
    ) {
        Text(
            text = gladiatorClass.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            ),
            color = ImmersiveTextSecondary
        )
    }
}

@Composable
fun ContractTypeBadge(
    contractType: GladiatorContractType,
    modifier: Modifier = Modifier
) {
    if (contractType == GladiatorContractType.SLAVE) {
        Surface(
            modifier = modifier,
            shape = CircleShape,
            color = ImmersiveGold.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, ImmersiveGold.copy(alpha = 0.35f))
        ) {
            Text(
                text = "SLAVE",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                ),
                color = ImmersiveGold
            )
        }
    } else {
        Surface(
            modifier = modifier,
            shape = CircleShape,
            color = ImmersiveSlate800,
            border = BorderStroke(1.dp, ImmersiveCardBorder)
        ) {
            Text(
                text = "AUCTORATUS",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                ),
                color = ImmersiveSlate400
            )
        }
    }
}

@Composable
fun InjuryChip(
    gladiator: Gladiator,
    modifier: Modifier = Modifier
) {
    if (!gladiator.isInjured) {
        Text(
            text = "HEALTHY",
            modifier = modifier,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            ),
            color = ImmersiveEmerald
        )
    } else {
        Surface(
            modifier = modifier,
            shape = CircleShape,
            color = ImmersiveCard,
            border = BorderStroke(1.dp, ImmersiveWarningBorder)
        ) {
            Text(
                text = "RECOVERING (${gladiator.recoveryDaysLeft}d)",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = ImmersiveWarningText
            )
        }
    }
}

@Composable
fun CrowdHypeMeter(
    hypeLevel: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🏛️ SEYİRCİ COŞKUSU (CROWD HYPE)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = ImmersiveGold
            )
            Text(
                text = "%$hypeLevel",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                ),
                color = ImmersiveGold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (hypeLevel / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = if (hypeLevel > 70) ImmersiveGoldLight else ImmersiveGold,
            trackColor = ImmersiveTrack
        )
    }
}

@Composable
fun ScheduledTournamentCountdownCard(
    ludusState: LudusState,
    onNavigateToArena: () -> Unit,
    onNavigateToCalendar: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val event = ludusState.currentScheduledEvent
    val daysLeft = ludusState.daysUntilNextFight
    val isMatchDay = ludusState.isFightDay

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scheduled_tournament_countdown_card"),
        shape = RoundedCornerShape(16.dp),
        color = ImmersiveCard,
        border = BorderStroke(1.2.dp, if (isMatchDay) ImmersiveGold else ImmersiveCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Arena Level & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isMatchDay) ImmersiveEmerald else ImmersiveGold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ludusState.cityTier.arenaLevelName.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = ImmersiveGold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isMatchDay) ImmersiveEmerald.copy(alpha = 0.2f) else ImmersiveTrack,
                    border = BorderStroke(1.dp, if (isMatchDay) ImmersiveEmerald else ImmersiveCardBorder)
                ) {
                    Text(
                        text = if (isMatchDay) "⚔️ BUGÜN MÜSABAKA GÜNÜ!" else "🗓️ ${daysLeft} GÜN KALDI",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = if (isMatchDay) ImmersiveEmerald else ImmersiveTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tournament Title & Host
            Text(
                text = event?.title ?: "Planlanan Arena Munerası",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                ),
                color = ImmersiveTextPrimary
            )
            Text(
                text = "Düzenleyen: ${event?.hostPatron ?: "Roma Senatosu"} • Format: ${event?.matchFormat?.title ?: "Gösteri"}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ImmersiveTextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Featured Opponent Boss & Rewards Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(ImmersiveTrack)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "RÖVANŞ / RAKİP ŞAMPİYON",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = ImmersiveTextMuted
                    )
                    Text(
                        text = event?.featuredOpponent?.title ?: "Bölgesel Şampiyon",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = ImmersiveTerracottaLight
                    )
                    Text(
                        text = "Özellik: ${event?.featuredOpponent?.traitName ?: "Dengeli"}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = ImmersiveTextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ÖDÜL HAVUZU",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = ImmersiveTextMuted
                    )
                    Text(
                        text = "+${event?.rewardGold ?: 180} 🟡 / +${event?.rewardPrestige ?: 40} 🏛️",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = ImmersiveGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action button
            Button(
                onClick = {
                    if (isMatchDay) {
                        onNavigateToArena()
                    } else {
                        onNavigateToCalendar?.invoke() ?: onNavigateToArena()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("goto_arena_schedule_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMatchDay) ImmersiveGold else ImmersiveTerracotta,
                    contentColor = if (isMatchDay) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = if (isMatchDay) Icons.Default.SportsMartialArts else Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMatchDay) "ARENAYA GİR VE DÖVÜŞ" else "MÜSABAKA DETAYLARI & TAKVİM",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
fun TeacherCard(
    teacher: Teacher,
    canAfford: Boolean = true,
    onHire: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("teacher_card_${teacher.id}"),
        shape = RoundedCornerShape(12.dp),
        color = ImmersiveCard,
        border = BorderStroke(1.dp, if (teacher.isPromotedFromRoster) ImmersiveGold else ImmersiveCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (teacher.isPromotedFromRoster) "👑" else "🎓",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = teacher.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (teacher.isPromotedFromRoster) ImmersiveGold else ImmersiveTextPrimary
                        )
                        Text(
                            text = teacher.title,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = ImmersiveTextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ImmersiveTrack
                ) {
                    Text(
                        text = if (onHire != null) "Bedel: ${teacher.hireCost} 🪙" else "Maaş: ${teacher.dailySalary} 🪙/g",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = ImmersiveGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = teacher.description,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ImmersiveTextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = ImmersiveCardBgSecondary
            ) {
                Text(
                    text = "Bonus: ${teacher.specialty.statBonusDesc} (x${teacher.statBonusMultiplier})",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = ImmersiveEmerald
                )
            }

            if (onHire != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onHire,
                    enabled = canAfford,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hire_teacher_button_${teacher.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImmersiveGold,
                        contentColor = Color.Black,
                        disabledContainerColor = ImmersiveCardBorder
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Eğitmeni Kirala (${teacher.hireCost} 🪙)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            if (onDismiss != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ImmersiveTerracottaLight),
                    border = BorderStroke(1.dp, ImmersiveTerracotta.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Yolları Ayır (Görevi Sonlandır)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

@Composable
fun GladiatorMarketCard(
    candidate: Gladiator,
    canAfford: Boolean,
    hasSlot: Boolean,
    onBuy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("market_gladiator_${candidate.name.replace(" ", "_")}"),
        shape = RoundedCornerShape(14.dp),
        color = ImmersiveCard,
        border = BorderStroke(1.dp, ImmersiveCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = candidate.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            ),
                            color = ImmersiveTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${candidate.age} Yaş)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ImmersiveTextMuted
                        )
                    }
                    Text(
                        text = "\"${candidate.nickname}\" • ${candidate.origin}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ImmersiveGold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ImmersiveGold
                ) {
                    Text(
                        text = "${candidate.priceValue} 🪙",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                GladiatorClassBadge(gladiatorClass = candidate.gladiatorClass)
                ContractTypeBadge(contractType = candidate.contractType)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "STR: ${candidate.str}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = ImmersiveStr)
                    LinearProgressIndicator(
                        progress = { (candidate.str / 30f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                        color = ImmersiveStr,
                        trackColor = ImmersiveTrack
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "AGI: ${candidate.agi}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = ImmersiveAgi)
                    LinearProgressIndicator(
                        progress = { (candidate.agi / 30f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                        color = ImmersiveAgi,
                        trackColor = ImmersiveTrack
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "STA: ${candidate.sta}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = ImmersiveSta)
                    LinearProgressIndicator(
                        progress = { (candidate.sta / 30f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                        color = ImmersiveSta,
                        trackColor = ImmersiveTrack
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onBuy,
                enabled = canAfford && hasSlot,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("buy_candidate_${candidate.name.replace(" ", "_")}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImmersiveGold,
                    contentColor = Color.Black,
                    disabledContainerColor = ImmersiveCardBorder
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = when {
                        !hasSlot -> "Kadro Dolu (Maksimum Kapasite)"
                        !canAfford -> "Yetersiz Altın (${candidate.priceValue} 🪙)"
                        else -> "Satın Al / Sözleşme İmzala (${candidate.priceValue} 🪙)"
                    },
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun DietPlanSelector(
    currentDiet: DietPlan,
    onSelectDiet: (DietPlan) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "🥣 LUDUS BESLENME & DİYET PLANI (SAGINA)",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = ImmersiveGold
        )
        Spacer(modifier = Modifier.height(8.dp))

        DietPlan.entries.forEach { diet ->
            val isSelected = currentDiet == diet
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable { onSelectDiet(diet) }
                    .testTag("diet_option_${diet.name.lowercase()}"),
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) ImmersiveCardBgSecondary else ImmersiveCard,
                border = BorderStroke(1.2.dp, if (isSelected) ImmersiveGold else ImmersiveCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectDiet(diet) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = ImmersiveGold,
                                    unselectedColor = ImmersiveTextMuted
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = diet.title,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) ImmersiveGold else ImmersiveTextPrimary
                            )
                        }
                        Text(
                            text = diet.description,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = ImmersiveTextMuted,
                            modifier = Modifier.padding(start = 36.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ImmersiveTrack
                    ) {
                        Text(
                            text = "${diet.dailyCostPerGladiator} 🟡 / kişi",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = ImmersiveGold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FatigueIndicator(
    fatigue: Int,
    modifier: Modifier = Modifier
) {
    val color = when {
        fatigue >= 75 -> ImmersiveWarningText
        fatigue >= 40 -> ImmersiveGold
        else -> ImmersiveEmerald
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Yorgunluk: %$fatigue",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            ),
            color = color
        )
    }
}

@Composable
fun CrowdSentimentAtmosphereCard(
    ludusState: LudusState,
    modifier: Modifier = Modifier
) {
    val sentiment = ludusState.crowdSentimentLevel
    val sentimentScore = ludusState.crowdSentimentScore.coerceIn(0, 100)
    val diffPercent = ludusState.dynamicOpponentDifficultyPercent

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("crowd_sentiment_atmosphere_card"),
        shape = RoundedCornerShape(12.dp),
        color = ImmersiveCard,
        border = BorderStroke(1.2.dp, Color(sentiment.badgeColorHex).copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Mood Icon & Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = sentiment.icon, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "AMFİTİYATRO HALKI & SEYİRCİ HAVASI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = ImmersiveTextMuted
                        )
                        Text(
                            text = sentiment.displayName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Serif
                            ),
                            color = Color(sentiment.badgeColorHex)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(sentiment.badgeColorHex).copy(alpha = 0.18f),
                    border = BorderStroke(0.8.dp, Color(sentiment.badgeColorHex))
                ) {
                    Text(
                        text = "Zorluk: %$diffPercent",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        color = Color(sentiment.badgeColorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Description
            Text(
                text = sentiment.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = ImmersiveTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Sentiment Progress Bar (Bloodlust 0% <---> 100% Clementia)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "💀 Kan & İnfaz (${ludusState.ruthlessnessScore})",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = Color(0xFFFF8A80)
                    )
                    Text(
                        text = "🕊️ Clementia & Erdem (${ludusState.mercyScore})",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = Color(0xFF81C784)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFE53935), // Red
                                    Color(0xFFFFB300), // Gold
                                    Color(0xFF42A5F5)  // Imperial Blue
                                )
                            )
                        )
                ) {
                    val indicatorFraction = (sentimentScore / 100f).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(indicatorFraction)
                            .background(Color.White.copy(alpha = 0.35f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Difficulty Impact Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ImmersiveCardBgSecondary,
                border = BorderStroke(0.6.dp, ImmersiveCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sentiment.difficultyImpactDesc,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = ImmersiveTextPrimary
                    )
                }
            }

            // Consequence Note from previous match if available
            ludusState.lastDecisionConsequence?.let { note ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    color = ImmersiveGold
                )
            }
        }
    }
}

/**
 * Detailed Hero Profile Card displaying gladiator name, class, origin, weapons, and fight record.
 */
@Composable
fun GladiatorHeroCard(
    gladiator: Gladiator,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveCard),
        border = BorderStroke(1.5.dp, ImmersiveGold)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            ImmersiveTerracotta.copy(alpha = 0.25f),
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
                    Text(
                        text = gladiator.name.uppercase(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = ImmersiveGold
                    )
                    Text(
                        text = "\"${gladiator.nickname}\" • ${gladiator.origin} • ${gladiator.age} Yaş",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ImmersiveTextMuted
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = ImmersiveCardBgSecondary,
                    border = BorderStroke(1.5.dp, ImmersiveGold),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = when (gladiator.gladiatorClass) {
                                GladiatorClass.MURMILLO -> "🛡️"
                                GladiatorClass.RETIARIUS -> "🔱"
                                GladiatorClass.THRAEX -> "⚔️"
                                GladiatorClass.SECUTOR -> "🤺"
                                GladiatorClass.DIMACHAERUS -> "🗡️"
                            },
                            fontSize = 24.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Career Rank Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RomanGoldSurface,
                    border = BorderStroke(1.dp, ImmersiveBorderGold)
                ) {
                    Text(
                        text = "${gladiator.careerRank.icon} ${gladiator.careerRank.title}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.5.sp),
                        color = RomanImperialGold
                    )
                }

                GladiatorClassBadge(gladiatorClass = gladiator.gladiatorClass)
                ContractTypeBadge(contractType = gladiator.contractType)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Personality Trait Badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = ImperialRedSurface,
                border = BorderStroke(1.dp, ImperialRedLight)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = gladiator.personalityTrait.icon, fontSize = 12.sp)
                    Text(
                        text = "${gladiator.personalityTrait.title}: ${gladiator.personalityTrait.combatBonusDesc}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                        color = ImperialRomanRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${gladiator.gladiatorClass.weaponDesc} — ${gladiator.gladiatorClass.statBonusDesc}",
                style = MaterialTheme.typography.bodySmall,
                color = ImmersiveTextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = ImmersiveCardBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Fight Records
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Galibiyet", style = MaterialTheme.typography.labelSmall, color = ImmersiveTextMuted)
                    Text(text = "${gladiator.wins}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = ImmersiveEmerald)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Mağlubiyet", style = MaterialTheme.typography.labelSmall, color = ImmersiveTextMuted)
                    Text(text = "${gladiator.losses}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = ImmersiveTerracottaLight)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Ölümcül Zafer", style = MaterialTheme.typography.labelSmall, color = ImmersiveTextMuted)
                    Text(text = "${gladiator.kills}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = ImmersiveGold)
                }
            }
        }
    }
}


