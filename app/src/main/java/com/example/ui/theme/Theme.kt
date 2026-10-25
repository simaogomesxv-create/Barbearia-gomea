package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BarberProColorScheme = darkColorScheme(
  primary = AccentGold,
  onPrimary = BackgroundBlack,
  primaryContainer = SurfaceElevated,
  onPrimaryContainer = TextWhite,
  secondary = TextWhite,
  onSecondary = BackgroundBlack,
  background = BackgroundBlack,
  onBackground = TextWhite,
  surface = SurfaceDark,
  onSurface = TextWhite,
  surfaceVariant = SurfaceElevated,
  onSurfaceVariant = TextGray,
  outline = CardBorder,
  outlineVariant = CardBorderLight,
)

@Composable
fun BarberProTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = BarberProColorScheme,
    typography = Typography,
    content = content
  )
}
