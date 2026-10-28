package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CoachCard
import com.example.data.model.PlayerCard
import com.example.ui.components.CompactFifaCard
import com.example.ui.components.FifaCoachCard
import com.example.ui.components.FormationPresets
import com.example.ui.components.SoccerPitchView
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.AppStrings
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.viewmodel.FifCardsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormationsScreen(
    viewModel: FifCardsViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val allFormations by viewModel.allFormations.collectAsState()
    val currentFormation by viewModel.currentFormation.collectAsState()
    val allPlayers by viewModel.allPlayers.collectAsState()
    val allCoaches by viewModel.allCoaches.collectAsState()
    val selectingSlotIndex by viewModel.selectingSlotIndex.collectAsState()

    var showFormationTypeMenu by remember { mutableStateOf(false) }
    var showNewFormationDialog by remember { mutableStateOf(false) }
    var newFormationName by remember { mutableStateOf("") }
    var showCoachSelectDialog by remember { mutableStateOf(false) }
    var showAddSubDialog by remember { mutableStateOf(false) }

    val formation = currentFormation

    // Parse starting 11 player IDs
    val startingPlayerMap = remember(formation, allPlayers) {
        val map = mutableMapOf<Int, PlayerCard?>()
        val ids = formation?.startingPlayerIds?.split(",") ?: emptyList()
        for (i in 0..10) {
            val id = ids.getOrNull(i)?.toLongOrNull() ?: 0L
            map[i] = if (id > 0) allPlayers.find { it.id == id } else null
        }
        map
    }

    // Parse substitute players
    val substitutePlayers = remember(formation, allPlayers) {
        val subIds = formation?.substitutePlayerIds?.split(",")
            ?.mapNotNull { it.trim().toLongOrNull() }
            ?.filter { it > 0 } ?: emptyList()
        subIds.mapNotNull { id -> allPlayers.find { it.id == id } }
    }

    // Selected coach
    val currentCoach = remember(formation, allCoaches) {
        val coachId = formation?.coachId
        if (coachId != null) allCoaches.find { it.id == coachId } ?: allCoaches.firstOrNull()
        else allCoaches.firstOrNull()
    }

    // Calculate Overall Team Rating:
    // "The team formation has an overall rating calculated by summing all players' ratings and dividing by their number (the 11 starting players only)."
    val teamOverallRating = remember(startingPlayerMap) {
        val starting11 = startingPlayerMap.values.filterNotNull()
        if (starting11.isEmpty()) 0
        else {
            val sum = starting11.sumOf { it.rating }
            // Sum divided by 11 (the 11 starting players)
            (sum.toFloat() / 11f).toInt()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: Formation Switcher & Rating Badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team Rating Display
                Column {
                    Text(
                        text = AppStrings.get("team_rating", language),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (teamOverallRating > 0) teamOverallRating.toString() else "--",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldPrimary,
                            modifier = Modifier.testTag("team_overall_rating")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Shield, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                    }
                }

                // Formation Selector Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        FilledTonalButton(
                            onClick = { showFormationTypeMenu = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(formation?.formationType ?: "4-3-3", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = showFormationTypeMenu,
                            onDismissRequest = { showFormationTypeMenu = false }
                        ) {
                            listOf("4-3-3", "4-4-2", "4-2-3-1", "3-5-2", "5-3-2", "3-4-3").forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        viewModel.updateFormationType(type)
                                        showFormationTypeMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = { showNewFormationDialog = true }) {
                        Icon(Icons.Default.AddCircleOutline, contentDescription = "New Formation", tint = GoldPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tactical Soccer Pitch with 11 Players
        formation?.let { f ->
            SoccerPitchView(
                formationType = f.formationType,
                startingPlayers = startingPlayerMap,
                onSlotClick = { slotIndex ->
                    viewModel.openSlotSelection(slotIndex)
                }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Team Coach Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = AppStrings.get("coach", language),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            TextButton(onClick = { showCoachSelectDialog = true }) {
                Text(AppStrings.get("select_coach", language), color = GoldPrimary, fontWeight = FontWeight.Bold)
            }
        }

        currentCoach?.let { coach ->
            FifaCoachCard(
                coach = coach,
                language = language,
                onClick = { showCoachSelectDialog = true },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Unlimited Substitutes Bench
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${AppStrings.get("substitutes", language)} (${substitutePlayers.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            FilledTonalButton(
                onClick = { showAddSubDialog = true },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(AppStrings.get("add_substitute", language), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (substitutePlayers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No substitute players added yet. Tap '+' to add.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(items = substitutePlayers, key = { it.id }) { sub ->
                    Box(modifier = Modifier.width(70.dp)) {
                        CompactFifaCard(
                            player = sub,
                            positionLabel = sub.position,
                            onClick = {
                                // Tap to remove or view
                                viewModel.removeSubstitute(sub.id)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }

    // Modal Sheet / Dialog to select player for pitch slot
    selectingSlotIndex?.let { slotIndex ->
        val slots = FormationPresets.getSlotsForFormation(formation?.formationType ?: "4-3-3")
        val currentSlot = slots.find { it.index == slotIndex }
        val slotLabel = currentSlot?.label ?: "Slot ${slotIndex + 1}"
        val occupiedPlayer = startingPlayerMap[slotIndex]

        Dialog(onDismissRequest = { viewModel.closeSlotSelection() }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 560.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format(AppStrings.get("select_player_for_slot", language), slotLabel),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { viewModel.closeSlotSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    if (occupiedPlayer != null) {
                        OutlinedButton(
                            onClick = { viewModel.removePlayerFromSlot(slotIndex) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.PersonRemove, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppStrings.get("remove_player", language))
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "Choose player from nominal list:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(items = allPlayers, key = { it.id }) { p ->
                            PlayerNominalRow(
                                player = p,
                                onClick = {
                                    viewModel.assignPlayerToSlot(slotIndex, p)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog for adding substitute from nominal list
    if (showAddSubDialog) {
        Dialog(onDismissRequest = { showAddSubDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 560.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("add_substitute", language),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showAddSubDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(items = allPlayers, key = { it.id }) { p ->
                            PlayerNominalRow(
                                player = p,
                                onClick = {
                                    viewModel.addSubstitute(p)
                                    showAddSubDialog = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog for selecting or adding coach
    if (showCoachSelectDialog) {
        Dialog(onDismissRequest = { showCoachSelectDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("select_coach", language),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showCoachSelectDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = allCoaches, key = { it.id }) { coach ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setFormationCoach(coach.id)
                                        showCoachSelectDialog = false
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(coach.rating.toString(), fontSize = 18.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(coach.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("${coach.nationalityFlag} ${coach.clubName} • ${coach.tacticStyle}", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for creating a new formation
    if (showNewFormationDialog) {
        AlertDialog(
            onDismissRequest = { showNewFormationDialog = false },
            title = { Text(AppStrings.get("new_formation", language)) },
            text = {
                OutlinedTextField(
                    value = newFormationName,
                    onValueChange = { newFormationName = it },
                    label = { Text(AppStrings.get("formation_name", language)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFormationName.isNotBlank()) {
                            viewModel.createNewFormation(newFormationName)
                            newFormationName = ""
                            showNewFormationDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewFormationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
