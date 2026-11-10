package com.fixora.marketplace.data.i18n

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(
    val code: String,
    val title: String,
    val nativeTitle: String,
    val flag: String,
    val isRtl: Boolean
) {
    ENGLISH("en", "English", "English", "🇬🇧", false),
    URDU("ur", "Urdu", "اردو", "🇵🇰", true),
    ARABIC("ar", "Arabic", "العربية", "🇸🇦", true);

    val layoutDirection: LayoutDirection
        get() = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    companion object {
        fun from(value: String?): AppLanguage {
            val v = value?.trim() ?: return ENGLISH
            return when {
                v.equals("Urdu", true) || v.equals("ur", true) || v.contains("اردو") -> URDU
                v.equals("Arabic", true) || v.equals("ar", true) || v.contains("العربية") -> ARABIC
                else -> ENGLISH
            }
        }
    }
}

object AppLocaleManager {
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun setLanguageByName(name: String) {
        _currentLanguage.value = AppLanguage.from(name)
    }
}

val LocalAppStrings = compositionLocalOf<AppStrings> { EnglishStrings }
val LocalCurrentLanguage = compositionLocalOf<AppLanguage> { AppLanguage.ENGLISH }
