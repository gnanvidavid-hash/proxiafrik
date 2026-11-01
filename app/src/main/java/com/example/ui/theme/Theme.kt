package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF689BD8),
    onPrimary = Color(0xFF041E3A),
    primaryContainer = Color(0xFF133E6E),
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFF4ADE80),
    onSecondary = Color(0xFF00391A),
    secondaryContainer = Color(0xFF00522B),
    onSecondaryContainer = Color(0xFF86F8A8),
    tertiary = ProxiAmber,
    background = Color(0xFF0B1320),
    surface = Color(0xFF111D30),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E2E48),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

private val LightColorScheme = lightColorScheme(
    primary = ProxiDarkBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E6F8),
    onPrimaryContainer = Color(0xFF05172C),
    secondary = ProxiGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1F4DE),
    onSecondaryContainer = Color(0xFF003822),
    tertiary = ProxiAmber,
    background = ProxiBackground,
    surface = ProxiSurface,
    onBackground = ProxiTextPrimary,
    onSurface = ProxiTextPrimary,
    surfaceVariant = ProxiSurfaceVariant,
    onSurfaceVariant = ProxiTextSecondary,
    outline = ProxiBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep ProxiAfrik brand identity prominent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
