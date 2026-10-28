package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.model.FormationSlot
import com.example.data.model.PlayerCard
import com.example.data.model.PositionCategory
import com.example.ui.theme.PitchGreenDark
import com.example.ui.theme.PitchGreenLight
import com.example.ui.theme.PitchLineColor

object FormationPresets {
    fun getSlotsForFormation(type: String): List<FormationSlot> {
        return when (type) {
            "4-4-2" -> listOf(
                // GK
                FormationSlot(0, "GK", PositionCategory.GOALKEEPER, 0.5f, 0.88f),
                // 4 DEF
                FormationSlot(1, "LB", PositionCategory.DEFENDER, 0.15f, 0.68f),
                FormationSlot(2, "CB", PositionCategory.DEFENDER, 0.38f, 0.70f),
                FormationSlot(3, "CB", PositionCategory.DEFENDER, 0.62f, 0.70f),
                FormationSlot(4, "RB", PositionCategory.DEFENDER, 0.85f, 0.68f),
                // 4 MID
                FormationSlot(5, "LM", PositionCategory.MIDFIELDER, 0.15f, 0.44f),
                FormationSlot(6, "CM", PositionCategory.MIDFIELDER, 0.38f, 0.46f),
                FormationSlot(7, "CM", PositionCategory.MIDFIELDER, 0.62f, 0.46f),
                FormationSlot(8, "RM", PositionCategory.MIDFIELDER, 0.85f, 0.44f),
                // 2 ST
                FormationSlot(9, "ST", PositionCategory.FORWARD, 0.35f, 0.18f),
                FormationSlot(10, "ST", PositionCategory.FORWARD, 0.65f, 0.18f)
            )

            "4-2-3-1" -> listOf(
                FormationSlot(0, "GK", PositionCategory.GOALKEEPER, 0.5f, 0.88f),
                FormationSlot(1, "LB", PositionCategory.DEFENDER, 0.15f, 0.70f),
                FormationSlot(2, "CB", PositionCategory.DEFENDER, 0.38f, 0.72f),
                FormationSlot(3, "CB", PositionCategory.DEFENDER, 0.62f, 0.72f),
                FormationSlot(4, "RB", PositionCategory.DEFENDER, 0.85f, 0.70f),
                FormationSlot(5, "CDM", PositionCategory.MIDFIELDER, 0.35f, 0.54f),
                FormationSlot(6, "CDM", PositionCategory.MIDFIELDER, 0.65f, 0.54f),
                FormationSlot(7, "LM", PositionCategory.MIDFIELDER, 0.15f, 0.36f),
                FormationSlot(8, "CAM", PositionCategory.MIDFIELDER, 0.50f, 0.34f),
                FormationSlot(9, "RM", PositionCategory.MIDFIELDER, 0.85f, 0.36f),
                FormationSlot(10, "ST", PositionCategory.FORWARD, 0.50f, 0.16f)
            )

            "3-5-2" -> listOf(
                FormationSlot(0, "GK", PositionCategory.GOALKEEPER, 0.5f, 0.88f),
                FormationSlot(1, "CB", PositionCategory.DEFENDER, 0.25f, 0.70f),
                FormationSlot(2, "CB", PositionCategory.DEFENDER, 0.50f, 0.72f),
                FormationSlot(3, "CB", PositionCategory.DEFENDER, 0.75f, 0.70f),
                FormationSlot(4, "LWB", PositionCategory.MIDFIELDER, 0.12f, 0.48f),
                FormationSlot(5, "CDM", PositionCategory.MIDFIELDER, 0.35f, 0.52f),
                FormationSlot(6, "CAM", PositionCategory.MIDFIELDER, 0.50f, 0.38f),
                FormationSlot(7, "CDM", PositionCategory.MIDFIELDER, 0.65f, 0.52f),
                FormationSlot(8, "RWB", PositionCategory.MIDFIELDER, 0.88f, 0.48f),
                FormationSlot(9, "ST", PositionCategory.FORWARD, 0.35f, 0.18f),
                FormationSlot(10, "ST", PositionCategory.FORWARD, 0.65f, 0.18f)
            )

            "5-3-2" -> listOf(
                FormationSlot(0, "GK", PositionCategory.GOALKEEPER, 0.5f, 0.88f),
                FormationSlot(1, "LWB", PositionCategory.DEFENDER, 0.10f, 0.66f),
                FormationSlot(2, "CB", PositionCategory.DEFENDER, 0.30f, 0.70f),
                FormationSlot(3, "CB", PositionCategory.DEFENDER, 0.50f, 0.72f),
                FormationSlot(4, "CB", PositionCategory.DEFENDER, 0.70f, 0.70f),
                FormationSlot(5, "RWB", PositionCategory.DEFENDER, 0.90f, 0.66f),
                FormationSlot(6, "CM", PositionCategory.MIDFIELDER, 0.25f, 0.45f),
                FormationSlot(7, "CDM", PositionCategory.MIDFIELDER, 0.50f, 0.48f),
                FormationSlot(8, "CM", PositionCategory.MIDFIELDER, 0.75f, 0.45f),
                FormationSlot(9, "ST", PositionCategory.FORWARD, 0.35f, 0.20f),
                FormationSlot(10, "ST", PositionCategory.FORWARD, 0.65f, 0.20f)
            )

            "3-4-3" -> listOf(
                FormationSlot(0, "GK", PositionCategory.GOALKEEPER, 0.5f, 0.88f),
                FormationSlot(1, "CB", PositionCategory.DEFENDER, 0.25f, 0.70f),
                FormationSlot(2, "CB", PositionCategory.DEFENDER, 0.50f, 0.72f),
                FormationSlot(3, "CB", PositionCategory.DEFENDER, 0.75f, 0.70f),
                FormationSlot(4, "LM", PositionCategory.MIDFIELDER, 0.15f, 0.46f),
                FormationSlot(5, "CM", PositionCategory.MIDFIELDER, 0.38f, 0.48f),
                FormationSlot(6, "CM", PositionCategory.MIDFIELDER, 0.62f, 0.48f),
                FormationSlot(7, "RM", PositionCategory.MIDFIELDER, 0.85f, 0.46f),
                FormationSlot(8, "LW", PositionCategory.FORWARD, 0.18f, 0.20f),
                FormationSlot(9, "ST", PositionCategory.FORWARD, 0.50f, 0.16f),
                FormationSlot(10, "RW", PositionCategory.FORWARD, 0.82f, 0.20f)
            )

            // Default 4-3-3 Attack
            else -> listOf(
                // 1 GK
                FormationSlot(0, "GK", PositionCategory.GOALKEEPER, 0.5f, 0.88f),
                // 4 DEF
                FormationSlot(1, "LB", PositionCategory.DEFENDER, 0.15f, 0.68f),
                FormationSlot(2, "CB", PositionCategory.DEFENDER, 0.38f, 0.70f),
                FormationSlot(3, "CB", PositionCategory.DEFENDER, 0.62f, 0.70f),
                FormationSlot(4, "RB", PositionCategory.DEFENDER, 0.85f, 0.68f),
                // 3 MID
                FormationSlot(5, "CDM", PositionCategory.MIDFIELDER, 0.50f, 0.54f),
                FormationSlot(6, "CM", PositionCategory.MIDFIELDER, 0.28f, 0.42f),
                FormationSlot(7, "CAM", PositionCategory.MIDFIELDER, 0.72f, 0.42f),
                // 3 ATT
                FormationSlot(8, "LW", PositionCategory.FORWARD, 0.18f, 0.20f),
                FormationSlot(9, "ST", PositionCategory.FORWARD, 0.50f, 0.16f),
                FormationSlot(10, "RW", PositionCategory.FORWARD, 0.82f, 0.20f)
            )
        }
    }
}

@Composable
fun SoccerPitchView(
    formationType: String,
    startingPlayers: Map<Int, PlayerCard?>,
    onSlotClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val slots = FormationPresets.getSlotsForFormation(formationType)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(560.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color(0x66FFFFFF), RoundedCornerShape(16.dp))
            .background(PitchGreenDark)
    ) {
        val pitchWidth = maxWidth
        val pitchHeight = maxHeight

        // Draw pitch markings on Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Grass horizontal stripes
            val stripeCount = 10
            val stripeHeight = h / stripeCount
            for (i in 0 until stripeCount) {
                if (i % 2 == 0) {
                    drawRect(
                        color = PitchGreenLight,
                        topLeft = Offset(0f, i * stripeHeight),
                        size = Size(w, stripeHeight)
                    )
                }
            }

            val strokeWidth = 2.dp.toPx()
            val lineStroke = Stroke(width = strokeWidth)

            // Outer boundary
            drawRect(
                color = PitchLineColor,
                topLeft = Offset(16.dp.toPx(), 16.dp.toPx()),
                size = Size(w - 32.dp.toPx(), h - 32.dp.toPx()),
                style = lineStroke
            )

            // Center line
            drawLine(
                color = PitchLineColor,
                start = Offset(16.dp.toPx(), h / 2),
                end = Offset(w - 16.dp.toPx(), h / 2),
                strokeWidth = strokeWidth
            )

            // Center circle
            val centerRadius = 55.dp.toPx()
            drawCircle(
                color = PitchLineColor,
                radius = centerRadius,
                center = Offset(w / 2, h / 2),
                style = lineStroke
            )
            drawCircle(
                color = PitchLineColor,
                radius = 4.dp.toPx(),
                center = Offset(w / 2, h / 2)
            )

            // Top Penalty box (Opponent)
            val penaltyW = w * 0.5f
            val penaltyH = h * 0.16f
            drawRect(
                color = PitchLineColor,
                topLeft = Offset((w - penaltyW) / 2, 16.dp.toPx()),
                size = Size(penaltyW, penaltyH),
                style = lineStroke
            )

            // Bottom Penalty box (Own goal)
            drawRect(
                color = PitchLineColor,
                topLeft = Offset((w - penaltyW) / 2, h - 16.dp.toPx() - penaltyH),
                size = Size(penaltyW, penaltyH),
                style = lineStroke
            )

            // Bottom Goal Area (6 yard box)
            val goalAreaW = w * 0.28f
            val goalAreaH = h * 0.07f
            drawRect(
                color = PitchLineColor,
                topLeft = Offset((w - goalAreaW) / 2, h - 16.dp.toPx() - goalAreaH),
                size = Size(goalAreaW, goalAreaH),
                style = lineStroke
            )

            // Bottom Penalty spot
            drawCircle(
                color = PitchLineColor,
                radius = 3.dp.toPx(),
                center = Offset(w / 2, h - 16.dp.toPx() - penaltyH * 0.65f)
            )
        }

        // Render the 11 starting player cards on the pitch
        slots.forEach { slot ->
            val player = startingPlayers[slot.index]
            val cardW = 64.dp
            val cardH = 86.dp

            val posX = (pitchWidth - cardW) * slot.xPercent
            val posY = (pitchHeight - cardH) * slot.yPercent

            Box(
                modifier = Modifier
                    .offset(x = posX, y = posY)
            ) {
                CompactFifaCard(
                    player = player,
                    positionLabel = slot.label,
                    onClick = { onSlotClick(slot.index) }
                )
            }
        }
    }
}
