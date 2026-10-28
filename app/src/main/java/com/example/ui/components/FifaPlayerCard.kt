package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.PlayerCard
import com.example.data.model.PositionCategory
import com.example.ui.theme.*
import java.io.File

fun getPositionCategory(pos: String): PositionCategory {
    return when (pos.uppercase().trim()) {
        "GK" -> PositionCategory.GOALKEEPER
        "CB", "LB", "RB", "LWB", "RWB" -> PositionCategory.DEFENDER
        "CDM", "CM", "CAM", "LM", "RM" -> PositionCategory.MIDFIELDER
        "LW", "RW", "ST", "CF" -> PositionCategory.FORWARD
        else -> PositionCategory.MIDFIELDER
    }
}

fun getPositionColor(pos: String): Color {
    return when (getPositionCategory(pos)) {
        PositionCategory.GOALKEEPER -> PositionGK
        PositionCategory.DEFENDER -> PositionDEF
        PositionCategory.MIDFIELDER -> PositionMID
        PositionCategory.FORWARD -> PositionFWD
    }
}

fun getPositionLabel(pos: String, lang: AppLanguage): String {
    val cat = getPositionCategory(pos)
    return when (cat) {
        PositionCategory.GOALKEEPER -> AppStrings.get("pos_gk", lang)
        PositionCategory.DEFENDER -> AppStrings.get("pos_def", lang)
        PositionCategory.MIDFIELDER -> AppStrings.get("pos_mid", lang)
        PositionCategory.FORWARD -> AppStrings.get("pos_fwd", lang)
    }
}

@Composable
fun FifaPlayerCard(
    player: PlayerCard,
    modifier: Modifier = Modifier,
    language: AppLanguage = AppLanguage.ENGLISH,
    onPhotoClick: (() -> Unit)? = null,
    cardWidth: Dp = 270.dp,
    cardHeight: Dp = 410.dp
) {
    val positionColor = getPositionColor(player.position)
    val isGoalkeeper = getPositionCategory(player.position) == PositionCategory.GOALKEEPER

    val statLabels = if (isGoalkeeper) {
        listOf(
            AppStrings.get("stat_div", language),
            AppStrings.get("stat_han", language),
            AppStrings.get("stat_kic", language),
            AppStrings.get("stat_ref", language),
            AppStrings.get("stat_spe", language),
            AppStrings.get("stat_pos", language)
        )
    } else {
        listOf(
            AppStrings.get("stat_pac", language),
            AppStrings.get("stat_sho", language),
            AppStrings.get("stat_pas", language),
            AppStrings.get("stat_dri", language),
            AppStrings.get("stat_def", language),
            AppStrings.get("stat_phy", language)
        )
    }

    val cardShape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .width(cardWidth)
            .height(cardHeight)
            .shadow(elevation = 16.dp, shape = cardShape, spotColor = positionColor.copy(alpha = 0.5f))
            .border(
                BorderStroke(
                    width = 3.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            positionColor,
                            positionColor.copy(alpha = 0.85f),
                            Color(0xFFE5A93C).copy(alpha = 0.7f),
                            positionColor
                        )
                    )
                ),
                shape = cardShape
            )
            .clip(cardShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF131722),
                        Color(0xFF090B10),
                        Color(0xFF06070A)
                    )
                )
            )
            .testTag("fifa_card_${player.id}")
            .padding(12.dp)
    ) {
        // Decorative background FIFA shield geometry
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    BorderStroke(1.dp, Color(0x22FFFFFF)),
                    shape = RoundedCornerShape(12.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Upper Card Section: [Left: OVR, POS, Flag, Club, League] + [Right: Player Photo]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.35f),
                verticalAlignment = Alignment.Top
            ) {
                // Info Column (Left)
                Column(
                    modifier = Modifier.width(64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Overall Rating (OVR)
                    Text(
                        text = player.rating.toString(),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.testTag("card_rating_text")
                    )

                    // Position Code
                    Text(
                        text = player.position.uppercase(),
                        color = positionColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Nationality Flag
                    Text(
                        text = player.nationalityFlag.ifBlank { "🌍" },
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Club Badge
                    Text(
                        text = player.clubBadge.ifBlank { "⚽" },
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // League Badge
                    Text(
                        text = player.leagueBadge.ifBlank { "🏆" },
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Player Photo (Center-Right)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1A1F2C))
                        .clickable(enabled = onPhotoClick != null) {
                            onPhotoClick?.invoke()
                        }
                        .testTag("card_photo_container"),
                    contentAlignment = Alignment.Center
                ) {
                    val photoPath = player.photoPath
                    if (!photoPath.isNullOrBlank() && File(photoPath).exists()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(File(photoPath))
                                .crossfade(true)
                                .build(),
                            contentDescription = player.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_male_placeholder),
                            contentDescription = "Neutral Male Player Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // Camera tap indicator badge
                    if (onPhotoClick != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(positionColor)
                                .padding(5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add Photo",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Player Name Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x33FFFFFF))
                    .padding(vertical = 4.dp, horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.uppercase(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("card_player_name")
                )
            }

            // Gold divider line with position glow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .height(1.5.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                positionColor,
                                Color(0xFFFFD54F),
                                positionColor,
                                Color.Transparent
                            )
                        )
                    )
            )

            // 6 Stats Section (2 Columns x 3 Rows)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.75f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Column (PAC, SHO, PAS or DIV, HAN, KIC)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StatRow(value = player.stat1, label = statLabels[0])
                    StatRow(value = player.stat2, label = statLabels[1])
                    StatRow(value = player.stat3, label = statLabels[2])
                }

                // Vertical divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(55.dp)
                        .background(Color(0x33FFFFFF))
                )

                // Right Column (DRI, DEF, PHY or REF, SPE, POS)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StatRow(value = player.stat4, label = statLabels[3])
                    StatRow(value = player.stat5, label = statLabels[4])
                    StatRow(value = player.stat6, label = statLabels[5])
                }
            }

            // Bottom subtle branding
            Text(
                text = "${player.clubName} • ${player.leagueName}",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun StatRow(value: Int, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value.toString(),
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color(0xFFCBD5E1),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Compact Player Card specifically formatted for Tactical Soccer Pitch slots
 */
@Composable
fun CompactFifaCard(
    player: PlayerCard?,
    positionLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posColor = if (player != null) getPositionColor(player.position) else PositionDEF

    Box(
        modifier = modifier
            .width(64.dp)
            .height(86.dp)
            .shadow(6.dp, shape = RoundedCornerShape(10.dp), spotColor = posColor)
            .border(
                BorderStroke(2.dp, if (player != null) posColor else Color(0x66FFFFFF)),
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F1219))
            .clickable { onClick() }
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        if (player != null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row: OVR and Position
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = player.rating.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = player.position,
                        color = posColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Middle: Photo or avatar
                val photoPath = player.photoPath
                if (!photoPath.isNullOrBlank() && File(photoPath).exists()) {
                    AsyncImage(
                        model = File(photoPath),
                        contentDescription = player.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = player.nationalityFlag.ifBlank { "⚽" },
                        fontSize = 24.sp
                    )
                }

                // Bottom: Name
                Text(
                    text = player.name.split(" ").lastOrNull()?.uppercase() ?: player.name.uppercase(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            // Empty slot indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "+",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = positionLabel,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
