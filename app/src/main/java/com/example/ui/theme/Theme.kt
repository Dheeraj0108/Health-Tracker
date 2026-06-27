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

private val DarkYellowColorScheme =
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

private val LightNudeColorScheme =
  lightColorScheme(
    primary = Teal400,
    secondary = Indigo400,
    tertiary = Amber400,
    background = Slate950,
    surface = Slate900,
    onPrimary = Slate50,
    onSecondary = Slate50,
    onTertiary = Slate50,
    onBackground = Slate50,
    onSurface = Slate50
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (AppThemeState.themeMode == "light_nude") LightNudeColorScheme else DarkYellowColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
