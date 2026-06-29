package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (AppThemeState.themeMode == "white") {
    lightColorScheme(
      primary = Teal400,
      secondary = Indigo400,
      tertiary = Amber400,
      background = Slate950,
      surface = Slate900,
      onPrimary = Color.White,
      onSecondary = Color.White,
      onTertiary = Color.White,
      onBackground = Slate50,
      onSurface = Slate50
    )
  } else {
    darkColorScheme(
      primary = Teal400,
      secondary = Indigo400,
      tertiary = Amber400,
      background = Slate950,
      surface = Slate900,
      onPrimary = Slate950,
      onSecondary = Slate950,
      onTertiary = Slate950,
      onBackground = Slate50,
      onSurface = Slate50
    )
  }

  // White Theme Legibility Fix: Enforce all text levels to be Bold in White theme
  val customTypography = if (AppThemeState.themeMode == "white") {
      androidx.compose.material3.Typography(
          bodyLarge = Typography.bodyLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          bodyMedium = Typography.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          bodySmall = Typography.bodySmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          titleLarge = Typography.titleLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          titleMedium = Typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          titleSmall = Typography.titleSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          labelLarge = Typography.labelLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          labelMedium = Typography.labelMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          labelSmall = Typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          displayLarge = Typography.displayLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          displayMedium = Typography.displayMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          displaySmall = Typography.displaySmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          headlineLarge = Typography.headlineLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          headlineMedium = Typography.headlineMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
          headlineSmall = Typography.headlineSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
      )
  } else {
      Typography
  }

  MaterialTheme(colorScheme = colorScheme, typography = customTypography, content = content)
}
