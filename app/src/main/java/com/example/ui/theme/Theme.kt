package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Pure Black Dark Mode
private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    secondary = GoldSecondary,
    onSecondary = Color.Black,
    tertiary = PositionMID,
    background = TrueBlackBackground,
    onBackground = TrueBlackOnBackground,
    surface = TrueBlackSurface,
    onSurface = TrueBlackOnBackground,
    surfaceVariant = TrueBlackSurfaceElevated,
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = TrueBlackBorder
)

// Pure White Day Mode
private val LightColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    secondary = GoldSecondary,
    onSecondary = Color.Black,
    tertiary = PositionDEF,
    background = PureWhiteBackground,
    onBackground = PureWhiteOnBackground,
    surface = PureWhiteSurface,
    onSurface = PureWhiteOnBackground,
    surfaceVariant = PureWhiteSurfaceElevated,
    onSurfaceVariant = Color(0xFF374151),
    outline = PureWhiteBorder
)

enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

@Composable
fun FifCardsTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
