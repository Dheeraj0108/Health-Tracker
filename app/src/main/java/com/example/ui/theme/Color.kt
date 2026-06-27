package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object AppThemeState {
    var themeMode by mutableStateOf("dark_yellow") // "dark_yellow" or "light_nude"
}

// Getters allow all composables referencing these colors to automatically update/recompose on theme switch!
val Slate950: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFF000000) else Color(0xFFFAF8F5)

val Slate900: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFF0B0B0C) else Color(0xFFF3EFE9)

val Slate800: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFF18181B) else Color(0xFFEADCC9)

val Slate700: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFF27272A) else Color(0xFFDFD4C6)

val Slate600: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFF3F3F46) else Color(0xFFD4C5B3)

val Slate500: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFF71717A) else Color(0xFFBCAAA4)

val Slate400: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFA1A1AA) else Color(0xFF8A7A75)

val Slate300: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFD4D4D8) else Color(0xFF6E5E5A)

val Slate200: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFE4E4E7) else Color(0xFF4A3E3D)

val Slate100: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFF4F4F5) else Color(0xFF362E2C)

val Slate50: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFFAFAFA) else Color(0xFF231C1A)

val ThemeWhite: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color.White else Color(0xFF1C1917)

// Dynamic Accents to meet "Dark black with yellow accent" vs "white background with nude accent"
val Teal500: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFEAB308) else Color(0xFFB5835A)

val Teal400: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFFACC15) else Color(0xFFC5A880)

val Indigo500: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFCA8A04) else Color(0xFF9C6644)

val Indigo400: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFFCD34D) else Color(0xFFDDB892)

val Amber500: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFD97706) else Color(0xFF7F5539)

val Amber400: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFF59E0B) else Color(0xFFE6CCB2)

// Other helpers for Crimson (reds) and Emerald (greens) to fit the themes
val Crimson500: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFEF4444) else Color(0xFFBA7A65)

val Crimson400: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFFF87171) else Color(0xFFCD8D7A)

val Emerald500: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFF10B981) else Color(0xFF90A955)

val Emerald400: Color
    get() = if (AppThemeState.themeMode == "dark_yellow") Color(0xFF34D399) else Color(0xFF829377)
