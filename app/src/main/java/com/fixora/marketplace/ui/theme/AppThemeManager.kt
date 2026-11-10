package com.fixora.marketplace.ui.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode(val title: String, val subtitle: String) {
    SYSTEM("System Default", "Follows device appearance settings"),
    LIGHT("Light Mode", "Soft lavender surfaces with pink accents"),
    DARK("Neon Dark", "Midnight violet with electric pink accents")
}

object AppThemeManager {
    private val _currentThemeMode = MutableStateFlow(AppThemeMode.DARK)
    val currentThemeMode: StateFlow<AppThemeMode> = _currentThemeMode.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _currentThemeMode.value = mode
    }

    fun setThemeModeByName(name: String) {
        _currentThemeMode.value = try {
            AppThemeMode.valueOf(name.uppercase())
        } catch (_: Exception) {
            when (name.lowercase()) {
                "light", "light mode" -> AppThemeMode.LIGHT
                "dark", "dark mode" -> AppThemeMode.DARK
                else -> AppThemeMode.SYSTEM
            }
        }
    }
}
