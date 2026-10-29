package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.data.model.FormationSlot
import com.example.data.model.PlayerCard
import com.example.data.model.PositionCategory
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PositionDEF
import java.io.File
import kotlin.math.roundToInt

@Composable
fun FormationGridView(
    formationType: String,
    startingPlayers: Map<Int, PlayerCard?>,
    onSlotClick: (Int) -> Unit,
    onSwapSlots: (Int, Int) -> Unit,
    onRemoveFromSlot: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val slots = FormationPresets.getSlotsForFormation(formationType)
    var selectedSwapSlot by remember { mutableStateOf<Int?>(null) }

    // Group the slots into formation lines: Attackers, Midfielders, Defenders, Goalkeeper
    val attackers = remember(slots) { slots.filter { it.category == PositionCategory.FORWARD } }
    val midfielders = remember(slots) { slots.filter { it.category == PositionCategory.MIDFIELDER } }
    val defenders = remember(slots) { slots.filter { it.category == PositionCategory.DEFENDER } }
    val goalkeepers = remember(slots) { slots.filter { it.category == PositionCategory.GOALKEEPER } }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Swap mode info / hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selectedSwapSlot != null) GoldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = if (selectedSwapSlot != null) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedSwapSlot != null) {
                            "Tap any other slot to swap with Slot #${selectedSwapSlot!! + 1}"
                        } else {
                            "Drag or tap a slot to place/swap players"
                        },
                        fontSize = 12.sp,
                        fontWeight = if (selectedSwapSlot != null) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedSwapSlot != null) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (selectedSwapSlot != null) {
                    IconButton(
                        onClick = { selectedSwapSlot = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel swap", modifier = Modifier.size(16.dp))
                    }
                }
            }

            // 1. Attackers Line (Forward row)
            FormationLineRow(
                lineTitle = "ATTACK",
                slots = attackers,
                startingPlayers = startingPlayers,
                selectedSwapSlot = selectedSwapSlot,
                onSlotClick = { slotIdx ->
                    handleSlotInteraction(slotIdx, selectedSwapSlot, onSlotClick, onSwapSlots) { selectedSwapSlot = it }
                },
                onRemove = onRemoveFromSlot
            )

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // 2. Midfielders Line
            FormationLineRow(
                lineTitle = "MIDFIELD",
                slots = midfielders,
                startingPlayers = startingPlayers,
                selectedSwapSlot = selectedSwapSlot,
                onSlotClick = { slotIdx ->
                    handleSlotInteraction(slotIdx, selectedSwapSlot, onSlotClick, onSwapSlots) { selectedSwapSlot = it }
                },
                onRemove = onRemoveFromSlot
            )

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // 3. Defenders Line
            FormationLineRow(
                lineTitle = "DEFENSE",
                slots = defenders,
                startingPlayers = startingPlayers,
                selectedSwapSlot = selectedSwapSlot,
                onSlotClick = { slotIdx ->
                    handleSlotInteraction(slotIdx, selectedSwapSlot, onSlotClick, onSwapSlots) { selectedSwapSlot = it }
                },
                onRemove = onRemoveFromSlot
            )

            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // 4. Goalkeeper Line
            FormationLineRow(
                lineTitle = "GOALKEEPER",
                slots = goalkeepers,
                startingPlayers = startingPlayers,
                selectedSwapSlot = selectedSwapSlot,
                onSlotClick = { slotIdx ->
                    handleSlotInteraction(slotIdx, selectedSwapSlot, onSlotClick, onSwapSlots) { selectedSwapSlot = it }
                },
                onRemove = onRemoveFromSlot
            )
        }
    }
}

private fun handleSlotInteraction(
    slotIdx: Int,
    currentSelectedSwap: Int?,
    onSlotClick: (Int) -> Unit,
    onSwapSlots: (Int, Int) -> Unit,
    setSwapSelected: (Int?) -> Unit
) {
    if (currentSelectedSwap == null) {
        // Open slot selection or mark for swap
        onSlotClick(slotIdx)
    } else {
        if (currentSelectedSwap == slotIdx) {
            setSwapSelected(null)
        } else {
            onSwapSlots(currentSelectedSwap, slotIdx)
            setSwapSelected(null)
        }
    }
}

@Composable
private fun FormationLineRow(
    lineTitle: String,
    slots: List<FormationSlot>,
    startingPlayers: Map<Int, PlayerCard?>,
    selectedSwapSlot: Int?,
    onSlotClick: (Int) -> Unit,
    onRemove: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = lineTitle,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            slots.forEach { slot ->
                val player = startingPlayers[slot.index]
                val isSelectedForSwap = selectedSwapSlot == slot.index

                GridSlotCard(
                    slot = slot,
                    player = player,
                    isSelectedForSwap = isSelectedForSwap,
                    onClick = { onSlotClick(slot.index) },
                    onRemove = { onRemove(slot.index) }
                )
            }
        }
    }
}

@Composable
fun GridSlotCard(
    slot: FormationSlot,
    player: PlayerCard?,
    isSelectedForSwap: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val posColor = if (player != null) getPositionColor(player.position) else getPositionColor(slot.label)
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    val scale by animateFloatAsState(
        targetValue = if (isDragging || isSelectedForSwap) 1.08f else 1.0f,
        label = "scale"
    )

    Box(
        modifier = modifier
            .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
            .zIndex(if (isDragging) 10f else 1f)
            .scale(scale)
            .width(68.dp)
            .height(96.dp)
            .shadow(
                elevation = if (isDragging || isSelectedForSwap) 10.dp else 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = if (isSelectedForSwap) GoldPrimary else posColor
            )
            .border(
                BorderStroke(
                    width = if (isSelectedForSwap) 2.5.dp else 1.5.dp,
                    color = if (isSelectedForSwap) GoldPrimary else if (player != null) posColor else Color(0x55FFFFFF)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F131D))
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        dragOffset = Offset.Zero
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffset = Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                    }
                )
            }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (player != null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top: OVR and Position
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = player.rating.toString(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = player.position,
                        color = posColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // Middle: Photo or nationality flag
                val photo = player.photoPath
                if (!photo.isNullOrBlank() && File(photo).exists()) {
                    AsyncImage(
                        model = File(photo),
                        contentDescription = player.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = player.nationalityFlag.ifBlank { "⚽" },
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Bottom: Player short name
                Text(
                    text = player.name.split(" ").lastOrNull()?.uppercase() ?: player.name.uppercase(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Empty Slot with Label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "+",
                    color = GoldPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = slot.label,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
