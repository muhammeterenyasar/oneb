package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Gladiator
import com.example.simulation.LudusUiState
import com.example.ui.components.GladiatorAvatarCanvas
import com.example.ui.components.GladiatorMiniSprite
import com.example.ui.components.GladiatorSpriteShowcase
import com.example.ui.components.RomanCard
import com.example.ui.components.RomanStatBar
import com.example.ui.theme.*

@Composable
fun TrainingScreen(
    state: LudusUiState,
    onSelectGladiator: (Gladiator) -> Unit,
    onUpdatePlan: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedGlad = state.selectedGladiator ?: state.gladiators.first()

    var currentFocus by remember(selectedGlad.id) { mutableStateOf(selectedGlad.trainingFocus) }
    var currentDiet by remember(selectedGlad.id) { mutableStateOf(selectedGlad.diet) }

    val focuses = listOf(
        "Güç Antrenmanı" to "Ağır taş kaldırma ve kütük çalışmaları (+Güç, +Hasar)",
        "Kılıç & Kalkan Çalışması" to "Ahşap palis üzerinde teknik vuruşlar (+Kılıç, +Savunma)",
        "Çeviklik & Ayak Oyunları" to "Kum engelleri ve hızlı adım talimleri (+Hız, +Refleks)",
        "Dayanıklılık" to "Güneş altında koşu ve nefes disiplini (+Stamina, +Acı Eşiği)",
        "Dengeli Rejim" to "Tüm temel niteliklerde dengeli gelişim"
    )

    val diets = listOf(
        "High Protein (Gladiator Barley & Meat)" to "Yüksek protein & arpa lapası (+Kas büyümesi, +%10 Dayanıklılık)",
        "Standard Legionary Ration" to "Geleneksel lejyoner tahıl ve zeytin diyeti (Ekonomik, standart kondisyon)",
        "Luxury Feast (Şarap & Et Ziyafeti)" to "Zengin et ve meyve ziyafeti (+Moral, yüksek maliyet)"
    )

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // COLUMN 1: Gladiator Selection
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "Gladyatör Seç", badge = "${state.gladiators.size} Dövüşçü") {
                state.gladiators.forEach { glad ->
                    val isSelected = glad.id == selectedGlad.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isSelected) RomanDarkCrimson else Color.Transparent,
                                RoundedCornerShape(2.dp)
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) RomanGold else Color.Transparent,
                                shape = RoundedCornerShape(2.dp)
                            )
                            .clickable { onSelectGladiator(glad) }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GladiatorMiniSprite(glad.gladiatorClass, 28.dp)
                        Column {
                            Text(
                                text = glad.name,
                                color = if (isSelected) RomanGoldLight else RomanParchment,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = glad.trainingFocus,
                                color = RomanTextSecondary,
                                fontSize = 8.5.sp
                            )
                        }
                    }
                    HorizontalDivider(color = RomanBronzeDark.copy(alpha = 0.3f), thickness = 0.5.dp)
                }
            }
        }

        // COLUMN 2: Regimen Focus Selection
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "Antrenman Odak Alanı", badge = selectedGlad.name) {
                Text(text = "Hangi alanda uzmanlaşacağını belirleyin:", color = RomanTextSecondary, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(4.dp))

                focuses.forEach { (title, desc) ->
                    val isChosen = currentFocus == title
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isChosen) Color(0xFF381515) else Color(0xFF1E1714),
                                RoundedCornerShape(2.dp)
                            )
                            .border(
                                width = if (isChosen) 1.dp else 0.5.dp,
                                color = if (isChosen) RomanGold else RomanBronzeDark,
                                shape = RoundedCornerShape(2.dp)
                            )
                            .clickable {
                                currentFocus = title
                                onUpdatePlan(selectedGlad.id, currentFocus, currentDiet)
                            }
                            .padding(horizontal = 6.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isChosen,
                            onClick = {
                                currentFocus = title
                                onUpdatePlan(selectedGlad.id, currentFocus, currentDiet)
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = RomanGold,
                                unselectedColor = RomanBronzeDark
                            ),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = title, color = if (isChosen) RomanGoldLight else RomanParchment, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            Text(text = desc, color = RomanTextSecondary, fontSize = 9.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }

        // COLUMN 3: Nutrition Diet & Injury Risk
        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RomanCard(title = "Dövüşçü: ${selectedGlad.name}", badge = "Seviye ${selectedGlad.trainingProgress.level}") {
                // Hero Trainee Sprite
                GladiatorSpriteShowcase(
                    gladiator = selectedGlad,
                    height = 110.dp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // XP progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "İlerleme", color = RomanGoldLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${selectedGlad.trainingProgress.experiencePoints} / ${selectedGlad.trainingProgress.nextLevelThreshold} XP", color = RomanParchmentDark, fontSize = 9.sp)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .background(Color(0xFF2B2018), RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(selectedGlad.trainingProgress.progressToNextLevel)
                            .fillMaxHeight()
                            .background(RomanGold, RoundedCornerShape(2.dp))
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "BESLENME & DİYET", color = RomanGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                diets.forEach { (dietTitle, dietDesc) ->
                    val isDietChosen = currentDiet == dietTitle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isDietChosen) Color(0xFF381515) else Color(0xFF1E1714),
                                RoundedCornerShape(2.dp)
                            )
                            .border(
                                width = if (isDietChosen) 1.dp else 0.5.dp,
                                color = if (isDietChosen) RomanGold else RomanBronzeDark,
                                shape = RoundedCornerShape(2.dp)
                            )
                            .clickable {
                                currentDiet = dietTitle
                                onUpdatePlan(selectedGlad.id, currentFocus, currentDiet)
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isDietChosen,
                            onClick = {
                                currentDiet = dietTitle
                                onUpdatePlan(selectedGlad.id, currentFocus, currentDiet)
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = RomanGold,
                                unselectedColor = RomanBronzeDark
                            ),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = dietTitle.split(" (")[0], color = if (isDietChosen) RomanGoldLight else RomanParchment, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = dietDesc, color = RomanTextSecondary, fontSize = 8.5.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = RomanBronzeDark, thickness = 0.6.dp)
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "SAKATLIK RİSKİ: DÜŞÜK", color = RomanSuccessGreen, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Text(text = "Antrenör Marcus gözetiminde çalışılıyor. Tesis verimi Seviye 2.", color = RomanTextMuted, fontSize = 9.sp)

                Spacer(modifier = Modifier.height(6.dp))
                RomanStatBar("Güç", selectedGlad.attributes.strength, 20, RomanGold)
                RomanStatBar("Kılıç", selectedGlad.attributes.swordsmanship, 20, RomanCrimson)
                RomanStatBar("Dayanıklılık", selectedGlad.attributes.endurance, 20, RomanStaminaCyan)
            }
        }
    }
}
