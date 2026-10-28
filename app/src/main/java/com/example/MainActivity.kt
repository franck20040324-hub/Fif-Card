package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.CardEditorScreen
import com.example.ui.screens.FormationsScreen
import com.example.ui.screens.MainListScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.AppStrings
import com.example.ui.theme.FifCardsTheme
import com.example.ui.theme.GoldPrimary
import com.example.ui.viewmodel.FifCardsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: FifCardsViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()
            val language by viewModel.language.collectAsState()

            // RTL support for Arabic, LTR for English
            val layoutDirection = if (language == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                FifCardsTheme(themeMode = themeMode) {
                    FifCardsApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FifCardsApp(viewModel: FifCardsViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val language by viewModel.language.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val musicState by viewModel.musicPlayer.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle back button: if on a sub-tab, return to Main Nominal List (Tab 0)
    BackHandler(enabled = activeTab != 0) {
        viewModel.setActiveTab(0)
    }

    // Display snackbar messages
    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // FIFA Shield Icon
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.SportsSoccer,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = AppStrings.get("app_title", language),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    // Mini Music Ticker if playing
                    if (musicState.playlist.isNotEmpty()) {
                        val currentTrack = if (musicState.currentTrackIndex in musicState.playlist.indices) {
                            musicState.playlist[musicState.currentTrackIndex]
                        } else null

                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (musicState.isPlaying) GoldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.musicPlayer.togglePlayPause() }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (musicState.isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = "Play/Pause Music",
                                tint = if (musicState.isPlaying) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${musicState.volumePercent}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (musicState.isPlaying) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // Settings Icon Button
                    IconButton(
                        onClick = { viewModel.setActiveTab(3) },
                        modifier = Modifier.testTag("top_settings_button")
                    ) {
                        Icon(
                            imageVector = if (activeTab == 3) Icons.Default.Settings else Icons.Default.SettingsSuggest,
                            contentDescription = "Settings",
                            tint = if (activeTab == 3) GoldPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Tab 0: Main Nominal List (Players)
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { viewModel.setActiveTab(0) },
                    icon = { Icon(Icons.Default.People, contentDescription = null) },
                    label = { Text(AppStrings.get("tab_players", language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = GoldPrimary
                    ),
                    modifier = Modifier.testTag("nav_tab_players")
                )

                // Tab 1: Create / Edit Card
                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { viewModel.setActiveTab(1) },
                    icon = { Icon(Icons.Default.Style, contentDescription = null) },
                    label = { Text(AppStrings.get("tab_create", language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = GoldPrimary
                    ),
                    modifier = Modifier.testTag("nav_tab_create")
                )

                // Tab 2: Formations
                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { viewModel.setActiveTab(2) },
                    icon = { Icon(Icons.Default.SportsSoccer, contentDescription = null) },
                    label = { Text(AppStrings.get("tab_formations", language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = GoldPrimary
                    ),
                    modifier = Modifier.testTag("nav_tab_formations")
                )

                // Tab 3: Settings
                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { viewModel.setActiveTab(3) },
                    icon = { Icon(Icons.Default.Tune, contentDescription = null) },
                    label = { Text(AppStrings.get("tab_settings", language), fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        indicatorColor = GoldPrimary
                    ),
                    modifier = Modifier.testTag("nav_tab_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (activeTab) {
                0 -> MainListScreen(viewModel = viewModel)
                1 -> CardEditorScreen(viewModel = viewModel)
                2 -> FormationsScreen(viewModel = viewModel)
                3 -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
