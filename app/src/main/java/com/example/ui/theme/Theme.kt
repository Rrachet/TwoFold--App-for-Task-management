package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkNaturalPrimary,
    onPrimary = DarkNaturalOnPrimary,
    primaryContainer = DarkNaturalContainer,
    background = DarkNaturalBackground,
    surface = DarkNaturalSurface,
    onBackground = DarkNaturalOnSurface,
    onSurface = DarkNaturalOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = NaturalPrimary,
    onPrimary = NaturalOnPrimary,
    primaryContainer = NaturalPrimaryContainer,
    onPrimaryContainer = NaturalOnPrimaryContainer,
    secondary = NaturalSecondary,
    onSecondary = NaturalOnSecondary,
    secondaryContainer = NaturalSecondaryContainer,
    onSecondaryContainer = NaturalOnSecondaryContainer,
    tertiary = NaturalTertiary,
    tertiaryContainer = NaturalTertiaryContainer,
    onTertiaryContainer = NaturalOnTertiaryContainer,
    background = NaturalBackground,
    onBackground = NaturalOnBackground,
    surface = NaturalSurface,
    onSurface = NaturalOnSurface,
    surfaceVariant = NaturalSurfaceVariant,
    onSurfaceVariant = NaturalOnSurfaceVariant
)

@Composable
fun TwofoldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

