package com.ghostdev.coinbit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryPurpleVariant,
    onPrimaryContainer = TextPrimary,
    
    secondary = PrimaryCyan,
    onSecondary = TextPrimary,
    secondaryContainer = PrimaryCyanVariant,
    onSecondaryContainer = TextPrimary,
    
    tertiary = AccentPink,
    onTertiary = TextPrimary,
    
    background = BackgroundDark,
    onBackground = TextPrimary,
    
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    
    error = RedNegative,
    onError = TextPrimary,
    
    outline = DividerColor,
    outlineVariant = DividerColor
)

@Composable
fun CoinBitTheme(
    content: @Composable () -> Unit
) {
    // Always use dark theme for Web3 aesthetic
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}