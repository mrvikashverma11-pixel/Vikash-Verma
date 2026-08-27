package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CinematicDarkColorScheme =
  darkColorScheme(
    primary = CyanPrimary,
    onPrimary = ObsidianBg,
    primaryContainer = CyanDark,
    onPrimaryContainer = ElectricCyan,
    secondary = ElectricCyan,
    onSecondary = ObsidianBg,
    secondaryContainer = ObsidianSurfaceVariant,
    onSecondaryContainer = ElectricCyan,
    tertiary = MotivationGold,
    onTertiary = ObsidianBg,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianBorder,
    surfaceTint = CyanPrimary
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = CinematicDarkColorScheme,
    typography = Typography,
    content = content
  )
}
