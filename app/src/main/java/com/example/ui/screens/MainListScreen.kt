package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.PlayerCard
import com.example.data.model.PlayerSortMethod
import com.example.ui.components.FifaPlayerCard
import com.example.ui.components.getPositionColor
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.AppStrings
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.FifCardsViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainListScreen(
    viewModel: FifCardsViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val startupStage by viewModel.startupStage.collectAsState()
    val players by viewModel.displayedPlayers.collectAsState()
    val allPlayers by viewModel.allPlayers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sortMethod by viewModel.sortMethod.collectAsState()
    val selectedPlayer by viewModel.selectedPlayer.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.startCreateNewCard() },
                containerColor = GoldPrimary,
                contentColor = Color.Black,
                modifier = Modifier.testTag("fab_create_card")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Card")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header stats badge (Gradual fade-in for Snapdragon 662)
            AnimatedVisibility(
                visible = startupStage >= 1,
                enter = fadeIn() + expandVertically()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = AppStrings.get("tab_players", language),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${AppStrings.get("total_players", language)}: ${allPlayers.size}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Sort button with dropdown
                    Box {
                        FilledTonalButton(
                            onClick = { showSortMenu = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("sort_button")
                        ) {
                            Icon(
                                Icons.Default.Sort,
                                contentDescription = "Sort",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (sortMethod) {
                                    PlayerSortMethod.NATIONALITY -> AppStrings.get("sort_nationality", language)
                                    PlayerSortMethod.POSITION -> AppStrings.get("sort_position", language)
                                    PlayerSortMethod.RATING -> AppStrings.get("sort_rating", language)
                                    PlayerSortMethod.NAME_FIRST_LETTER -> AppStrings.get("sort_name", language)
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("sort_rating", language)) },
                                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                                onClick = {
                                    viewModel.setSortMethod(PlayerSortMethod.RATING)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("sort_position", language)) },
                                leadingIcon = { Icon(Icons.Default.SportsSoccer, contentDescription = null) },
                                onClick = {
                                    viewModel.setSortMethod(PlayerSortMethod.POSITION)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("sort_nationality", language)) },
                                leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null) },
                                onClick = {
                                    viewModel.setSortMethod(PlayerSortMethod.NATIONALITY)
                                    showSortMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(AppStrings.get("sort_name", language)) },
                                leadingIcon = { Icon(Icons.Default.Abc, contentDescription = null) },
                                onClick = {
                                    viewModel.setSortMethod(PlayerSortMethod.NAME_FIRST_LETTER)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text(AppStrings.get("search_players", language), fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_players_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nominal List of Players
            if (players.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = AppStrings.get("no_players_found", language),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("players_lazy_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(items = players, key = { it.id }) { player ->
                        PlayerNominalRow(
                            player = player,
                            onClick = { viewModel.selectPlayer(player) }
                        )
                    }
                }
            }
        }
    }

    // Modal Card Preview Dialog when tapping a player's name
    selectedPlayer?.let { player ->
        Dialog(onDismissRequest = { viewModel.selectPlayer(null) }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Full FIFA Card
                    FifaPlayerCard(
                        player = player,
                        language = language,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons: Edit Card, Delete, Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.startEditPlayer(player) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_edit_card"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(AppStrings.get("edit_card", language), fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.deletePlayer(player) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_delete_card")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = { viewModel.selectPlayer(null) }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerNominalRow(
    player: PlayerCard,
    onClick: () -> Unit
) {
    val posColor = getPositionColor(player.position)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("player_row_${player.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position Edge Accent Strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(posColor)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Rating + Position Badge
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(42.dp)
            ) {
                Text(
                    text = player.rating.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = player.position,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = posColor
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Avatar / Photo Thumbnail
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.5.dp, posColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val photoPath = player.photoPath
                if (!photoPath.isNullOrBlank() && File(photoPath).exists()) {
                    AsyncImage(
                        model = File(photoPath),
                        contentDescription = player.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = player.nationalityFlag.ifBlank { "⚽" },
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name + Club + League
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${player.clubBadge} ${player.clubName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text("•", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = player.nationality,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Arrow forward icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
