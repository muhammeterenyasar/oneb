package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GladiatorClass
import com.example.ui.theme.*

@Composable
fun RomanCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    badge: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .border(1.dp, RomanCardBorder, RoundedCornerShape(4.dp))
            .background(RomanCardBg, RoundedCornerShape(4.dp))
            .padding(1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (title != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(RomanDarkCrimson, RomanSurfaceVariant)
                            )
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title.uppercase(),
                        color = RomanGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    if (badge != null) {
                        Text(
                            text = badge,
                            color = RomanParchmentDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                HorizontalDivider(color = RomanBronzeDark, thickness = 1.dp)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun RomanStatBar(
    label: String,
    value: Int,
    maxValue: Int = 20,
    color: Color = RomanGold,
    modifier: Modifier = Modifier
) {
    val progress = (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 1.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = RomanTextSecondary,
            fontSize = 11.sp,
            modifier = Modifier.weight(1.2f)
        )
        Box(
            modifier = Modifier
                .weight(2f)
                .height(7.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF14110F))
                .border(0.5.dp, RomanBronzeDark, RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(
                        Brush.horizontalGradient(
                            listOf(color.copy(alpha = 0.7f), color)
                        )
                    )
            )
        }
        Text(
            text = "$value",
            color = RomanParchment,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .width(28.dp)
                .padding(start = 6.dp)
        )
    }
}

@Composable
fun GladiatorAvatarCanvas(
    gladiatorClass: GladiatorClass,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .border(1.dp, RomanBronze, RoundedCornerShape(4.dp))
            .background(Color(0xFF1A1512), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.8f)) {
            val w = this.size.width
            val h = this.size.height

            // Background glow
            drawCircle(
                color = Color(0xFF33261C),
                radius = w * 0.45f,
                center = Offset(w / 2f, h / 2f)
            )

            // Shoulders & armor
            drawRect(
                color = when (gladiatorClass) {
                    GladiatorClass.MURMILLO, GladiatorClass.SECUTOR -> Color(0xFF5A4D41)
                    GladiatorClass.THRAEX -> Color(0xFF784E3A)
                    GladiatorClass.RETIARIUS -> Color(0xFF8A6D3B)
                    else -> Color(0xFF6B5843)
                },
                topLeft = Offset(w * 0.2f, h * 0.55f),
                size = Size(w * 0.6f, h * 0.4f)
            )

            // Head / Helmet
            val helmetColor = when (gladiatorClass) {
                GladiatorClass.MURMILLO -> Color(0xFFB5935A)
                GladiatorClass.THRAEX -> Color(0xFFD4AF37)
                GladiatorClass.RETIARIUS -> Color(0xFF8D7359)
                GladiatorClass.HOPLOMACHUS -> Color(0xFFC5A059)
                else -> Color(0xFFA6804A)
            }

            drawCircle(
                color = helmetColor,
                radius = w * 0.22f,
                center = Offset(w / 2f, h * 0.38f)
            )

            // Helmet Visor / Grille
            if (gladiatorClass != GladiatorClass.RETIARIUS) {
                drawRect(
                    color = Color(0xFF1F1813),
                    topLeft = Offset(w * 0.42f, h * 0.34f),
                    size = Size(w * 0.16f, h * 0.14f)
                )
                // Grille lines
                drawLine(
                    color = helmetColor,
                    start = Offset(w * 0.5f, h * 0.34f),
                    end = Offset(w * 0.5f, h * 0.48f),
                    strokeWidth = 1.5f
                )
            } else {
                // Retiarius bare face with bandana/leather headband
                drawRect(
                    color = Color(0xFF7A1818),
                    topLeft = Offset(w * 0.35f, h * 0.3f),
                    size = Size(w * 0.3f, h * 0.08f)
                )
            }

            // Crest for Murmillo & Thraex
            if (gladiatorClass == GladiatorClass.MURMILLO || gladiatorClass == GladiatorClass.THRAEX) {
                drawLine(
                    color = Color(0xFF9E2A2B),
                    start = Offset(w / 2f, h * 0.14f),
                    end = Offset(w / 2f, h * 0.26f),
                    strokeWidth = 4f
                )
            }
        }
    }
}
