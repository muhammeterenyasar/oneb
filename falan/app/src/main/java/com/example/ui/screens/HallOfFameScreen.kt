package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
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
import com.example.model.GladiatorClass
import com.example.model.HallOfFameHero
import com.example.ui.MainUiState
import com.example.ui.theme.*

@Composable
fun HallOfFameScreen(
    state: MainUiState,
    modifier: Modifier = Modifier
) {
    // Generate combined hall of fame from promoted teachers + Roman historical pantheon
    val promotedTeachers = state.ludusState.activeTeachers.filter { it.isPromotedFromRoster }

    val playerHeroes = promotedTeachers.map { teacher ->
        HallOfFameHero(
            id = "hero_${teacher.id}",
            gladiatorName = teacher.name,
            nickname = teacher.title,
            origin = "Ludus Magnus",
            gladiatorClass = GladiatorClass.MURMILLO,
            finalWins = teacher.level * 2 + 3,
            finalLosses = 1,
            finalKills = teacher.level,
            rankTitle = "Doctore & Kıdemli Başeğitmen",
            retirementType = "Doctore Olarak Okula Hizmet Veriyor",
            retiredDay = 1,
            honorsDescription = teacher.description,
            icon = "👑"
        )
    }

    val historicalLegends = listOf(
        HallOfFameHero(
            id = "legend_flamma",
            gladiatorName = "Flamma",
            nickname = "Dört Kez Tahta Kılıcı Reddeden",
            origin = "Suriye",
            gladiatorClass = GladiatorClass.SECUTOR,
            finalWins = 25,
            finalLosses = 4,
            finalKills = 21,
            rankTitle = "Primus Palus Efsanesi",
            retirementType = "Arenada Ebediyete Uğurlandı",
            retiredDay = 0,
            honorsDescription = "Roma tarihinin en sadık savaşçısı. Dört kez özgürlük (Rudis) teklif edilmesine rağmen arenayı seçti.",
            icon = "🔥"
        ),
        HallOfFameHero(
            id = "legend_spartacus",
            gladiatorName = "Spartacus",
            nickname = "Capua Aslanı",
            origin = "Trakya",
            gladiatorClass = GladiatorClass.THRAEX,
            finalWins = 18,
            finalLosses = 0,
            finalKills = 15,
            rankTitle = "Özgürlük Savaşçısı & Efsane",
            retirementType = "Tarihin Sayfalarına Kazındı",
            retiredDay = 0,
            honorsDescription = "Capua ludusundan kaçıp Roma lejyonlarına meydan okuyan tüm zamanların en ünlü Trak savaşçısı.",
            icon = "⚡"
        ),
        HallOfFameHero(
            id = "legend_crixus",
            gladiatorName = "Crixus",
            nickname = "Galyalı Fırtına",
            origin = "Galya",
            gladiatorClass = GladiatorClass.MURMILLO,
            finalWins = 14,
            finalLosses = 2,
            finalKills = 11,
            rankTitle = "Capua Şampiyonu",
            retirementType = "Galyalı Kahraman",
            retiredDay = 0,
            honorsDescription = "Murmillo sınıfının gelmiş geçmiş en yıkıcı savunma ve darbe ustası.",
            icon = "🛡️"
        )
    )

    val allHeroes = playerHeroes + historicalLegends

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(RomanMarbleBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Pantheon Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hall_of_fame_header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RomanCardBg),
                border = BorderStroke(2.dp, Brush.horizontalGradient(listOf(RomanImperialGold, ImperialRomanRed)))
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
                                .background(RomanImperialGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "PANTHEON & EFSANELER SALONU",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ImperialRomanRed,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Emekli Şampiyonlar, Azad Edilenler & Roma Anıtı",
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
                                text = "AZAD EDİLENLER (RUDİS)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RomanInkMuted
                            )
                            Text(
                                text = "🕊️ ${state.ludusState.freedGladiatorsCount} Gladyatör",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ImperialRomanRed
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "EMEKLİ DOCTORE'LER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RomanInkMuted
                            )
                            Text(
                                text = "👑 ${promotedTeachers.size} Şampiyon Eğitmen",
                                fontSize = 16.sp,
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
                text = "🏛️ EBEDİ ŞAMPİYONLAR & EFSANELER",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ImperialRomanRed,
                letterSpacing = 0.5.sp
            )
        }

        items(allHeroes) { hero ->
            HallOfFameHeroCard(hero = hero)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HallOfFameHeroCard(hero: HallOfFameHero) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_card_${hero.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = RomanCardBg),
        border = BorderStroke(1.dp, ImmersiveCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(RomanGoldSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = hero.icon,
                            fontSize = 20.sp
                        )
                    }

                    Column {
                        Text(
                            text = hero.gladiatorName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ImperialRomanRed
                        )
                        Text(
                            text = hero.nickname,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RomanImperialGold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ImperialRedSurface,
                    border = BorderStroke(1.dp, ImperialRedLight)
                ) {
                    Text(
                        text = hero.rankTitle,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImperialRomanRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = hero.honorsDescription,
                fontSize = 12.sp,
                color = RomanInkMedium,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = ImmersiveCardBorderLight)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🏆 ${hero.finalWins} Zafer • 💀 ${hero.finalKills} İnfaz",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RomanInkDark
                )
                Text(
                    text = "📜 ${hero.retirementType}",
                    fontSize = 11.sp,
                    color = RomanInkMuted
                )
            }
        }
    }
}
