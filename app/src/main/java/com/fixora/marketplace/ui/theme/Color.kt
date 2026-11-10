package com.fixora.marketplace.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand accents
val FixoraNeonPink = Color(0xFFFF51A5)
val FixoraViolet = Color(0xFFBD83FF)
val FixoraBlue = Color(0xFF65B6FF)
val FixoraAction = Color(0xFFD60063)
val FixoraPrimaryDark = Color(0xFFA80C56)
val FixoraSuccess = Color(0xFF20C998)
val FixoraWarning = Color(0xFFFFBA55)
val FixoraEmergency = Color(0xFFEE4369)
val FixoraGradientStart = Color(0xFFD719B8)
val FixoraGradientEnd = Color(0xFFA80055)

// Light palette
val LightFixoraBackground = Color(0xFFFBF7FF)
val LightFixoraSurface         = Color(0xFFFFFFFF)  // White card surface
val LightFixoraSurfaceVariant = Color(0xFFF4EAF9)
val LightFixoraSurfaceElevated = Color(0xFFFCF5FF)
val LightFixoraDarkNavy = Color(0xFF100B26)
val LightFixoraNavySurface = Color(0xFF1B1435)
val LightFixoraTextPrimary = Color(0xFF1A112C)
val LightFixoraTextSecondary = Color(0xFF524761)
val LightFixoraTextMuted = Color(0xFF6B5C7B)
val LightFixoraBorder = Color(0xFFE8D8F2)
val LightFixoraDivider = Color(0xFFF0E4F7)
val LightFixoraPrimaryLight = Color(0xFFFCE5F3)
val LightFixoraSuccessLight    = Color(0xFFE6F7F2)
val LightFixoraWarningLight    = Color(0xFFFFFBEB)
val LightFixoraEmergencyLight  = Color(0xFFFEF2F2)

// Neon palette
val DarkFixoraBackground = Color(0xFF080611)
val DarkFixoraSurface = Color(0xFF151126)
val DarkFixoraSurfaceVariant = Color(0xFF211936)
val DarkFixoraSurfaceElevated = Color(0xFF292040)
val DarkFixoraDarkNavy = Color(0xFF0D091D)
val DarkFixoraNavySurface = Color(0xFF191131)
val DarkFixoraTextPrimary = Color(0xFFF9F5FF)
val DarkFixoraTextSecondary = Color(0xFFC9BED9)
val DarkFixoraTextMuted = Color(0xFFA59AB8)
val DarkFixoraBorder = Color(0xFF3C3056)
val DarkFixoraDivider = Color(0xFF2A2140)
val DarkFixoraPrimaryLight = Color(0xFF35152E)
val DarkFixoraSuccessLight     = Color(0xFF0D382A)
val DarkFixoraWarningLight     = Color(0xFF3B2800)
val DarkFixoraEmergencyLight   = Color(0xFF3B1212)

// ─── Fixora Colors State Container ────────────────────────────────────────────
data class FixoraColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceElevated: Color,
    val darkNavy: Color,
    val navySurface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val border: Color,
    val divider: Color,
    val primaryLight: Color,
    val successLight: Color,
    val warningLight: Color,
    val emergencyLight: Color,
    val isDark: Boolean
)

val LightFixoraColors = FixoraColors(
    background = LightFixoraBackground,
    surface = LightFixoraSurface,
    surfaceVariant = LightFixoraSurfaceVariant,
    surfaceElevated = LightFixoraSurfaceElevated,
    darkNavy = LightFixoraDarkNavy,
    navySurface = LightFixoraNavySurface,
    textPrimary = LightFixoraTextPrimary,
    textSecondary = LightFixoraTextSecondary,
    textMuted = LightFixoraTextMuted,
    border = LightFixoraBorder,
    divider = LightFixoraDivider,
    primaryLight = LightFixoraPrimaryLight,
    successLight = LightFixoraSuccessLight,
    warningLight = LightFixoraWarningLight,
    emergencyLight = LightFixoraEmergencyLight,
    isDark = false
)

val DarkFixoraColors = FixoraColors(
    background = DarkFixoraBackground,
    surface = DarkFixoraSurface,
    surfaceVariant = DarkFixoraSurfaceVariant,
    surfaceElevated = DarkFixoraSurfaceElevated,
    darkNavy = DarkFixoraDarkNavy,
    navySurface = DarkFixoraNavySurface,
    textPrimary = DarkFixoraTextPrimary,
    textSecondary = DarkFixoraTextSecondary,
    textMuted = DarkFixoraTextMuted,
    border = DarkFixoraBorder,
    divider = DarkFixoraDivider,
    primaryLight = DarkFixoraPrimaryLight,
    successLight = DarkFixoraSuccessLight,
    warningLight = DarkFixoraWarningLight,
    emergencyLight = DarkFixoraEmergencyLight,
    isDark = true
)

val LocalFixoraColors = staticCompositionLocalOf { LightFixoraColors }

// ─── Dynamic Composable Properties (Auto-adapt to active theme) ───────────────
val FixoraPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalFixoraColors.current.isDark) FixoraNeonPink else FixoraAction

val FixoraBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.background

val FixoraSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.surface

val FixoraSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.surfaceVariant

val FixoraSurfaceElevated: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.surfaceElevated

val FixoraDarkNavy: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.darkNavy

val FixoraNavySurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.navySurface

val FixoraTextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.textPrimary

val FixoraTextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.textSecondary

val FixoraTextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.textMuted

val FixoraBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.border

val FixoraDivider: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.divider

val FixoraPrimaryLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.primaryLight

val FixoraSuccessLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.successLight

val FixoraWarningLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.warningLight

val FixoraEmergencyLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalFixoraColors.current.emergencyLight

val FixoraPageBrush: Brush
    @Composable
    @ReadOnlyComposable
    get() = if (LocalFixoraColors.current.isDark) {
        Brush.verticalGradient(
            0f to Color(0xFF160E2B),
            0.45f to DarkFixoraBackground,
            1f to DarkFixoraBackground
        )
    } else {
        Brush.verticalGradient(0f to LightFixoraSurfaceVariant, 0.55f to LightFixoraBackground)
    }