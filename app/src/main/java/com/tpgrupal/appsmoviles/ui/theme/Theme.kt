package com.tpgrupal.appsmoviles.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(

    primary = NeonPurple,
    secondary = NeonBlue,
    tertiary = NeonPink,

    background = DarkBackground,
    surface = DarkSurface,

    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,

    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun TorneosTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}