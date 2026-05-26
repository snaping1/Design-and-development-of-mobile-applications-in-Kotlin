package com.example.nobellaureatesclient.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = NobelBrown,
    onPrimary = NobelCream,
    primaryContainer = NobelGoldLight,
    onPrimaryContainer = NobelBrown,
    secondary = NobelGold,
    onSecondary = NobelCream,
    secondaryContainer = NobelCream,
    onSecondaryContainer = NobelBrown,
    tertiary = NobelCrimson,
    onTertiary = NobelCream,
    background = NobelCream,
    onBackground = NobelInk,
    surface = NobelSurface,
    onSurface = NobelInk,
    surfaceVariant = NobelCream,
    onSurfaceVariant = NobelMuted,
    outline = NobelOutline,
)

private val DarkColorScheme = darkColorScheme(
    primary = NobelGoldDark,
    onPrimary = NobelBrownDark,
    primaryContainer = NobelBrown,
    onPrimaryContainer = NobelGoldLight,
    secondary = NobelGoldLight,
    onSecondary = NobelBrownDark,
    tertiary = NobelCrimsonDark,
    onTertiary = NobelBrownDark,
    background = NobelBrownDark,
    onBackground = NobelInkDark,
    surface = NobelSurfaceDark,
    onSurface = NobelInkDark,
    surfaceVariant = NobelBrownDark,
    onSurfaceVariant = NobelGoldLight,
    outline = NobelOutlineDark,
)

@Composable
fun NobelLaureatesClientTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
