package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.PlayerCard
import com.example.ui.components.FifaPlayerCard
import com.example.ui.components.getPositionCategory
import com.example.ui.components.getPositionColor
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.AppStrings
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.FifCardsViewModel
import kotlinx.coroutines.launch

data class PresetItem(val name: String, val badge: String)

val PRESET_NATIONS = listOf(
    PresetItem("Brazil", "🇧🇷"),
    PresetItem("Argentina", "🇦🇷"),
    PresetItem("France", "🇫🇷"),
    PresetItem("England", "🏴󠁧󠁢󠁥󠁮󠁧󠁿"),
    PresetItem("Spain", "🇪🇸"),
    PresetItem("Germany", "🇩🇪"),
    PresetItem("Portugal", "🇵🇹"),
    PresetItem("Morocco", "🇲🇦"),
    PresetItem("Saudi Arabia", "🇸🇦"),
    PresetItem("Egypt", "🇪🇬"),
    PresetItem("Algeria", "🇩🇿"),
    PresetItem("Netherlands", "🇳🇱"),
    PresetItem("Belgium", "🇧🇪"),
    PresetItem("Italy", "🇮🇹")
)

val PRESET_CLUBS = listOf(
    PresetItem("Real Madrid", "👑"),
    PresetItem("Manchester City", "⛵"),
    PresetItem("FC Barcelona", "🔵🔴"),
    PresetItem("Bayern Munich", "🔴"),
    PresetItem("Arsenal", "🔴⚪"),
    PresetItem("Paris Saint-Germain", "🗼"),
    PresetItem("Liverpool", "🦅"),
    PresetItem("Inter Milan", "⚫🔵"),
    PresetItem("Al Hilal", "🔵"),
    PresetItem("Al Nassr", "🟡"),
    PresetItem("Al Ahly", "🦅")
)

val PRESET_LEAGUES = listOf(
    PresetItem("Premier League", "🏴󠁧󠁢󠁥󠁮󠁧󠁿"),
    PresetItem("La Liga", "🇪🇸"),
    PresetItem("Serie A", "🇮🇹"),
    PresetItem("Bundesliga", "🇩🇪"),
    PresetItem("Ligue 1", "🇫🇷"),
    PresetItem("Saudi Pro League", "🇸🇦"),
    PresetItem("UEFA Champions League", "⭐"),
    PresetItem("MLS", "🇺🇸")
)

val ALL_POSITIONS = listOf("GK", "CB", "LB", "RB", "CDM", "CM", "CAM", "LM", "RM", "LW", "RW", "ST")

@Composable
fun CardEditorScreen(
    viewModel: FifCardsViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.language.collectAsState()
    val editingPlayer by viewModel.editingPlayer.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Form states
    var name by remember(editingPlayer) { mutableStateOf(editingPlayer?.name ?: "") }
    var position by remember(editingPlayer) { mutableStateOf(editingPlayer?.position ?: "ST") }
    var rating by remember(editingPlayer) { mutableStateOf(editingPlayer?.rating ?: 88) }
    var stat1 by remember(editingPlayer) { mutableStateOf(editingPlayer?.stat1 ?: 85) }
    var stat2 by remember(editingPlayer) { mutableStateOf(editingPlayer?.stat2 ?: 86) }
    var stat3 by remember(editingPlayer) { mutableStateOf(editingPlayer?.stat3 ?: 82) }
    var stat4 by remember(editingPlayer) { mutableStateOf(editingPlayer?.stat4 ?: 88) }
    var stat5 by remember(editingPlayer) { mutableStateOf(editingPlayer?.stat5 ?: 45) }
    var stat6 by remember(editingPlayer) { mutableStateOf(editingPlayer?.stat6 ?: 78) }

    var nationality by remember(editingPlayer) { mutableStateOf(editingPlayer?.nationality ?: "Brazil") }
    var nationalityFlag by remember(editingPlayer) { mutableStateOf(editingPlayer?.nationalityFlag ?: "🇧🇷") }
    var clubName by remember(editingPlayer) { mutableStateOf(editingPlayer?.clubName ?: "Real Madrid") }
    var clubBadge by remember(editingPlayer) { mutableStateOf(editingPlayer?.clubBadge ?: "👑") }
    var leagueName by remember(editingPlayer) { mutableStateOf(editingPlayer?.leagueName ?: "La Liga") }
    var leagueBadge by remember(editingPlayer) { mutableStateOf(editingPlayer?.leagueBadge ?: "🇪🇸") }
    var photoPath by remember(editingPlayer) { mutableStateOf(editingPlayer?.photoPath) }

    // System Image picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                val savedPath = viewModel.savePlayerPhotoPrivately(uri)
                if (savedPath != null) {
                    photoPath = savedPath
                }
            }
        }
    }

    // Live preview card instance
    val previewPlayer = remember(
        name, position, rating, stat1, stat2, stat3, stat4, stat5, stat6,
        nationality, nationalityFlag, clubName, clubBadge, leagueName, leagueBadge, photoPath
    ) {
        PlayerCard(
            id = editingPlayer?.id ?: 0,
            name = name.ifBlank { "PLAYER NAME" },
            position = position,
            rating = rating,
            stat1 = stat1,
            stat2 = stat2,
            stat3 = stat3,
            stat4 = stat4,
            stat5 = stat5,
            stat6 = stat6,
            nationality = nationality,
            nationalityFlag = nationalityFlag,
            clubName = clubName,
            clubBadge = clubBadge,
            leagueName = leagueName,
            leagueBadge = leagueBadge,
            photoPath = photoPath
        )
    }

    val isGoalkeeper = getPositionCategory(position).name == "GOALKEEPER"

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = if (editingPlayer != null) AppStrings.get("update_card", language) else AppStrings.get("create_new_card", language),
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Live Interactive Preview Card (Tapping photo triggers picker)
        FifaPlayerCard(
            player = previewPlayer,
            language = language,
            onPhotoClick = { photoPickerLauncher.launch("image/*") },
            cardWidth = 280.dp,
            cardHeight = 425.dp
        )

        // Photo tap hint
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { photoPickerLauncher.launch("image/*") }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = AppStrings.get("tap_to_change_photo", language),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Fields
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Player Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(AppStrings.get("player_name", language)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_player_name")
                )

                // Position Selection Chips
                Column {
                    Text(
                        text = AppStrings.get("position", language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ALL_POSITIONS.take(6).forEach { pos ->
                            PositionChip(
                                pos = pos,
                                selected = position == pos,
                                onClick = { position = pos }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ALL_POSITIONS.drop(6).forEach { pos ->
                            PositionChip(
                                pos = pos,
                                selected = position == pos,
                                onClick = { position = pos }
                            )
                        }
                    }
                }

                // Overall Rating (OVR)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("overall_rating", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = rating.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldPrimary
                        )
                    }
                    Slider(
                        value = rating.toFloat(),
                        onValueChange = { rating = it.toInt() },
                        valueRange = 40f..99f,
                        steps = 58,
                        colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary)
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Editable 6 Stats
                Text(
                    text = "Player Energy & Stats (Editable)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                StatSlider(
                    label = if (isGoalkeeper) AppStrings.get("stat_div", language) else AppStrings.get("stat_pac", language),
                    value = stat1,
                    onValueChange = { stat1 = it }
                )
                StatSlider(
                    label = if (isGoalkeeper) AppStrings.get("stat_han", language) else AppStrings.get("stat_sho", language),
                    value = stat2,
                    onValueChange = { stat2 = it }
                )
                StatSlider(
                    label = if (isGoalkeeper) AppStrings.get("stat_kic", language) else AppStrings.get("stat_pas", language),
                    value = stat3,
                    onValueChange = { stat3 = it }
                )
                StatSlider(
                    label = if (isGoalkeeper) AppStrings.get("stat_ref", language) else AppStrings.get("stat_dri", language),
                    value = stat4,
                    onValueChange = { stat4 = it }
                )
                StatSlider(
                    label = if (isGoalkeeper) AppStrings.get("stat_spe", language) else AppStrings.get("stat_def", language),
                    value = stat5,
                    onValueChange = { stat5 = it }
                )
                StatSlider(
                    label = if (isGoalkeeper) AppStrings.get("stat_pos", language) else AppStrings.get("stat_phy", language),
                    value = stat6,
                    onValueChange = { stat6 = it }
                )

                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Nationality Field + Quick presets
                Column {
                    OutlinedTextField(
                        value = nationality,
                        onValueChange = { nationality = it },
                        label = { Text(AppStrings.get("nationality", language)) },
                        trailingIcon = { Text(nationalityFlag, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Popular Nations:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PRESET_NATIONS.take(6).forEach { nation ->
                            PresetChip(
                                label = "${nation.badge} ${nation.name}",
                                selected = nationality == nation.name,
                                onClick = {
                                    nationality = nation.name
                                    nationalityFlag = nation.badge
                                }
                            )
                        }
                    }
                }

                // Club Field + Quick presets
                Column {
                    OutlinedTextField(
                        value = clubName,
                        onValueChange = { clubName = it },
                        label = { Text(AppStrings.get("club_name", language)) },
                        trailingIcon = { Text(clubBadge, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Popular Clubs:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PRESET_CLUBS.take(5).forEach { club ->
                            PresetChip(
                                label = "${club.badge} ${club.name.split(" ").last()}",
                                selected = clubName == club.name,
                                onClick = {
                                    clubName = club.name
                                    clubBadge = club.badge
                                }
                            )
                        }
                    }
                }

                // League Field + Quick presets
                Column {
                    OutlinedTextField(
                        value = leagueName,
                        onValueChange = { leagueName = it },
                        label = { Text(AppStrings.get("league_name", language)) },
                        trailingIcon = { Text(leagueBadge, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Top Leagues:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PRESET_LEAGUES.take(4).forEach { league ->
                            PresetChip(
                                label = "${league.badge} ${league.name}",
                                selected = leagueName == league.name,
                                onClick = {
                                    leagueName = league.name
                                    leagueBadge = league.badge
                                }
                            )
                        }
                    }
                }

                // Save Card Action Button
                Button(
                    onClick = {
                        val finalPlayer = previewPlayer.copy(
                            name = name.ifBlank { "Unknown Player" },
                            position = position,
                            rating = rating,
                            stat1 = stat1, stat2 = stat2, stat3 = stat3,
                            stat4 = stat4, stat5 = stat5, stat6 = stat6,
                            nationality = nationality.ifBlank { "Global" },
                            nationalityFlag = nationalityFlag.ifBlank { "🌍" },
                            clubName = clubName.ifBlank { "Free Agent" },
                            clubBadge = clubBadge.ifBlank { "⚽" },
                            leagueName = leagueName.ifBlank { "League" },
                            leagueBadge = leagueBadge.ifBlank { "🏆" },
                            photoPath = photoPath
                        )
                        viewModel.savePlayerCard(finalPlayer)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_save_player_card"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get("save_card", language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun PositionChip(
    pos: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val posColor = getPositionColor(pos)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) posColor else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.defaultMinSize(minWidth = 44.dp, minHeight = 36.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(pos, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PresetChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) GoldPrimary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(48.dp)
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..99f,
            steps = 97,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.width(36.dp)
        )
    }
}
