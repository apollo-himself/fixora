package com.fixora.marketplace.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = FixoraAction,
    onPrimary = LightFixoraSurface,
    primaryContainer = LightFixoraPrimaryLight,
    onPrimaryContainer = FixoraPrimaryDark,
    secondary = LightFixoraDarkNavy,
    onSecondary = LightFixoraSurface,
    background = LightFixoraBackground,
    onBackground = LightFixoraTextPrimary,
    surface = LightFixoraSurface,
    surfaceContainerLowest = LightFixoraSurface,
    surfaceContainerLow = LightFixoraSurfaceElevated,
    surfaceContainer = LightFixoraSurfaceVariant,
    surfaceContainerHigh = LightFixoraSurfaceVariant,
    surfaceContainerHighest = LightFixoraPrimaryLight,
    outlineVariant = LightFixoraBorder,
    onSurface = LightFixoraTextPrimary,
    surfaceVariant = LightFixoraSurfaceVariant,
    onSurfaceVariant = LightFixoraTextSecondary,
    outline = LightFixoraBorder,
    error = FixoraEmergency,
    onError = LightFixoraSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = FixoraAction,
    onPrimary = LightFixoraSurface,
    primaryContainer = DarkFixoraPrimaryLight,
    onPrimaryContainer = FixoraNeonPink,
    secondary = FixoraViolet,
    onSecondaryContainer = DarkFixoraTextPrimary,
    secondaryContainer = DarkFixoraSurfaceVariant,
    tertiary = FixoraBlue,
    onTertiary = DarkFixoraBackground,
    onSecondary = DarkFixoraBackground,
    background = DarkFixoraBackground,
    onBackground = DarkFixoraTextPrimary,
    surface = DarkFixoraSurface,
    surfaceContainerLowest = DarkFixoraBackground,
    surfaceContainerLow = DarkFixoraSurface,
    surfaceContainer = DarkFixoraSurfaceVariant,
    surfaceContainerHigh = DarkFixoraSurfaceElevated,
    surfaceContainerHighest = DarkFixoraSurfaceElevated,
    surfaceTint = FixoraNeonPink,
    outlineVariant = DarkFixoraBorder,
    onSurface = DarkFixoraTextPrimary,
    surfaceVariant = DarkFixoraSurfaceVariant,
    onSurfaceVariant = DarkFixoraTextSecondary,
    outline = DarkFixoraBorder,
    error = FixoraEmergency,
    onError = LightFixoraSurface
)

private val FixoraShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun FixoraTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val fixoraColors = if (darkTheme) DarkFixoraColors else LightFixoraColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = fixoraColors.surface.toArgb()
                window.navigationBarColor = fixoraColors.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalFixoraColors provides fixoraColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
