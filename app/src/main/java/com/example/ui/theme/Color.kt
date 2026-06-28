package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object AppThemeState {
    var themeMode by mutableStateOf("dark_gold") // "dark_gold" or "white"
}

// Background and Surface Layers
val Slate950: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFF050505) else Color(0xFFF8FAFC) // Deep dark vs crisp light background

val Slate900: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFF0F0F11) else Color(0xFFFFFFFF) // Dark surface vs pure white card

val Slate800: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFF1A1A1E) else Color(0xFFF1F5F9)

val Slate700: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFF26262B) else Color(0xFFE2E8F0)

val Slate600: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFF3E3E47) else Color(0xFFCBD5E1)

val Slate500: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFF6F6F78) else Color(0xFF94A3B8)

val Slate400: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFF9F9FA6) else Color(0xFF64748B)

val Slate300: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFD3D3D8) else Color(0xFF475569)

val Slate200: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFE3E3E6) else Color(0xFF334155)

val Slate100: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFF3F3F5) else Color(0xFF1E293B)

val Slate50: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFFAFAFA) else Color(0xFF0F172A) // White text on dark vs Dark slate text on light

val ThemeWhite: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color.White else Color(0xFF020617)

// Dynamic Premium Accents (Gold for Dark Theme, High-Contrast Accessible Blue/Teal for White Theme)
val Teal500: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFD4AF37) else Color(0xFF1E40AF) // Champagne Gold vs Accessible Blue

val Teal400: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFFFD700) else Color(0xFF0284C7) // Pure Gold `#FFD700` vs Visually Accessible Accent Blue

val Indigo500: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFC5A059) else Color(0xFF0F766E)

val Indigo400: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFE5C17B) else Color(0xFF0D9488)

val Amber500: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFB58D3D) else Color(0xFFB45309)

val Amber400: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFF2D18F) else Color(0xFFD97706)

// Functional helpers adjusted to keep optimal contrast
val Crimson500: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFEF4444) else Color(0xFFDC2626)

val Crimson400: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFFF87171) else Color(0xFFEF4444)

val Emerald500: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFF10B981) else Color(0xFF16A34A)

val Emerald400: Color
    get() = if (AppThemeState.themeMode == "dark_gold") Color(0xFF34D399) else Color(0xFF22C55E)
