package com.moviles.jobmatch.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Primary Colors
val DarkBlue = Color(0xFF2196F3)
val LightBlue = Color(0xFFCBDCFF)

// Background & Surfaces
val Background = Color(0xFFFFFFFF)
val Surface = Color(0xFFFFFFFF)

// Text Colors
val TextPrimary = Color(0xFF2196F3)
val TextSecondary = Color(0xFF5292fb)

// Bottom Navigation Colors
val BottomNavUnselected = Color(0xFF2A2A2A)
val BottomNavSelected = DarkBlue

// Status Colors
val StatusActive = Color(0xFF4CAF50)
val StatusInactive = Color(0xFFF44336)

// Splash Gradient
val SplashGradientTop = Color(0xFF5B7BFF)
val SplashGradientBottom = Color(0xFF3D5AF1)

object JobMatchColors {
    val bottomNavUnselected: Color
        @Composable
        get() = BottomNavUnselected

    val bottomNavSelected: Color
        @Composable
        get() = BottomNavSelected
}