package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.*

data class SpriteFrameInfo(
    val frameNumber: Int,
    val title: String,
    val subTitle: String,
    val description: String,
    val combatState: String
)

val GLADIATOR_SPRITE_FRAMES = listOf(
    SpriteFrameInfo(
        frameNumber = 1,
        title = "Duruş (Idle / Stance)",
        subTitle = "Frame 1 • 64x64px",
        description = "Kuşbakışı hazır duruş; bronz miğfer tepeliği, balteus omuz askısı, solda scutum kalkanı ve sağda gladius kılıcı.",
        combatState = "Bekleme & Taktiksel Mesafe Gözetimi"
    ),
    SpriteFrameInfo(
        frameNumber = 2,
        title = "İlerleme (March / Advance)",
        subTitle = "Frame 2 • 64x64px",
        description = "Ayak adımı ve ağırlık aktarımı; scutum kalkanı ileri itilmiş, kılıç arkada yaylanıyor.",
        combatState = "Rakibe Yaklaşma & Çevreleme"
    ),
    SpriteFrameInfo(
        frameNumber = 3,
        title = "Hücum & Hamle (Strike / Slash)",
        subTitle = "Frame 3 • 64x64px",
        description = "Gladius tam menzille öne saplanıyor; arkada sarı-kızıl piksel savurma arkı ve kıvılcım patlaması.",
        combatState = "Hücum Emri & Kritik Darbe"
    ),
    SpriteFrameInfo(
        frameNumber = 4,
        title = "Kalkan Savunması (Scutum Block)",
        subTitle = "Frame 4 • 64x64px",
        description = "Scutum göğüs merkezine kilitlenmiş, miğfer eğilmiş; kalkan yüzeyinde mavi saptırma ışıması.",
        combatState = "Kalkan Kaldır Emri & Darbe Karşılama"
    ),
    SpriteFrameInfo(
        frameNumber = 5,
        title = "Darbe & Sarsılma (Hit / Stagger)",
        subTitle = "Frame 5 • 64x64px",
        description = "Geriye savrulma açısı; kalkan yana kaymış, kumun üzerine sıçrayan kızıl kan damlaları ve sarsılma efekti.",
        combatState = "Darbe Alma & Sersemleme"
    ),
    SpriteFrameInfo(
        frameNumber = 6,
        title = "Yere Düşüş (Fallen / Defeat)",
        subTitle = "Frame 6 • 64x64px",
        description = "Kum zemin üzerinde boylu boyunca yatış; düşmüş miğfer, elden fırlayan kalkan ve gladius, kan birikintisi.",
        combatState = "Yere Düşme & Pollice Verso Oylaması"
    )
)

/**
 * Top-down pixel art sprite sheet showcase card
 */
@Composable
fun GladiatorTopDownSpriteSheetCard(
    onInspectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFrameIndex by remember { mutableStateOf(0) }
    val currentFrame = GLADIATOR_SPRITE_FRAMES[selectedFrameIndex]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, RomanBronze, RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18120C)),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "TOP-DOWN PİKSEL SPRITE SHEET",
                        color = RomanGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    modifier = Modifier.clickable { onInspectClick() },
                    shape = RoundedCornerShape(2.dp),
                    color = RomanGoldDark.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(0.6.dp, RomanGold)
                ) {
                    Text(
                        text = "BÜYÜT & İNCELE",
                        color = RomanGoldLight,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Sprite Sheet Graphic Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF0F0B08))
                    .border(1.dp, Color(0xFF382A1E), RoundedCornerShape(3.dp))
                    .clickable { onInspectClick() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_gladiator_topdown_spritesheet),
                    contentDescription = "Top-Down Gladiator Pixel Art Sprite Sheet",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // Overlay tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(Color(0xFF140F0C).copy(alpha = 0.85f), RoundedCornerShape(2.dp))
                        .border(0.5.dp, RomanBronze, RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "6 KARE (384×256)",
                        color = RomanGoldLight,
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Interactive Frame Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                GLADIATOR_SPRITE_FRAMES.forEachIndexed { index, frame ->
                    val isSelected = index == selectedFrameIndex
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedFrameIndex = index },
                        shape = RoundedCornerShape(2.dp),
                        color = if (isSelected) RomanGold else Color(0xFF231A12),
                        border = androidx.compose.foundation.BorderStroke(
                            0.6.dp,
                            if (isSelected) RomanGoldLight else RomanBronzeDark
                        )
                    ) {
                        Text(
                            text = "F${frame.frameNumber}",
                            color = if (isSelected) Color(0xFF1B120C) else RomanParchmentDark,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(vertical = 3.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            // Selected Frame Detail Strip
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F1710), RoundedCornerShape(2.dp))
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentFrame.title,
                        color = RomanGoldLight,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentFrame.subTitle,
                        color = RomanParchmentDark,
                        fontSize = 8.sp
                    )
                }
                Text(
                    text = currentFrame.description,
                    color = RomanParchment,
                    fontSize = 8.5.sp,
                    lineHeight = 11.sp
                )
                Text(
                    text = "Taktiksel Eşleşme: ${currentFrame.combatState}",
                    color = RomanGold,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * High-definition inspection dialog for the top-down pixel art sprite sheet
 */
@Composable
fun TopDownSpriteSheetDialog(
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .border(2.dp, RomanGold, RoundedCornerShape(6.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF140E0A)),
            shape = RoundedCornerShape(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOP-DOWN GLADYATÖR SPRITE SHEET",
                            color = RomanGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Kuşbakışı Taktiksel Dövüş Animasyon Tablosu",
                            color = RomanParchmentDark,
                            fontSize = 9.sp
                        )
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text(text = "✕", color = RomanGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Full Sprite Sheet Graphic (Scaled up)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0A0705))
                        .border(1.dp, RomanBronze, RoundedCornerShape(4.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_gladiator_topdown_spritesheet),
                        contentDescription = "Full Top-Down Gladiator Sprite Sheet",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Text(
                    text = "ANİMASYON KARELERİ VE MEKANİKLER",
                    color = RomanGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                // Detailed Grid breakdown of all 6 frames
                GLADIATOR_SPRITE_FRAMES.forEach { frame ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1610), RoundedCornerShape(3.dp))
                            .border(0.6.dp, RomanBronzeDark, RoundedCornerShape(3.dp))
                            .padding(6.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Kare ${frame.frameNumber}: ${frame.title}",
                                color = RomanGoldLight,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = frame.combatState,
                                color = RomanGold,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = frame.description,
                            color = RomanParchment,
                            fontSize = 8.5.sp,
                            lineHeight = 11.5.sp
                        )
                    }
                }

                // Close Button
                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RomanCrimson),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text(
                        text = "KAPAT VE DÖVÜŞE DÖN",
                        color = RomanParchment,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
