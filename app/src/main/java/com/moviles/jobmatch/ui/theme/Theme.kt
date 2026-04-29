package com.moviles.jobmatch.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = DarkBlue,
    secondary = LightBlue,

    background = Background,
    surface = Surface,

    onPrimary = Surface,
    onSecondary = Surface,
    onTertiary = Surface,

    onBackground = TextPrimary,
    onSurface = TextPrimary,

)

@Composable
fun JobMatchTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}