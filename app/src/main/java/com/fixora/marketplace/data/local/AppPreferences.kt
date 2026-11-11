package com.fixora.marketplace.data.local

import android.content.Context
import android.content.SharedPreferences
import com.fixora.marketplace.data.model.User
import com.fixora.marketplace.data.model.UserRole

object AppPreferences {
    const val DEBUG_GUEST_ID = "FIXORA-DEBUG-GUEST"
    private const val PREFS_NAME = "fixora_app_prefs"
    private const val KEY_ONBOARDING_COMPLETED = "has_completed_onboarding"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_EMAIL_VERIFIED = "is_email_verified"
    private const val KEY_PENDING_VERIFY_EMAIL = "pending_verify_email"
    private const val KEY_SELECTED_LANGUAGE = "selected_language"
    private const val KEY_THEME_MODE = "theme_mode"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getThemeMode(context: Context): String {
        return getPrefs(context).getString(KEY_THEME_MODE, "DARK") ?: "DARK"
    }

    fun setThemeMode(context: Context, mode: String) {
        getPrefs(context).edit().putString(KEY_THEME_MODE, mode).apply()
    }

    fun hasSelectedLanguage(context: Context): Boolean {
        return getPrefs(context).contains(KEY_SELECTED_LANGUAGE)
    }

    fun getSelectedLanguage(context: Context): String {
        return getPrefs(context).getString(KEY_SELECTED_LANGUAGE, "English") ?: "English"
    }

    fun setSelectedLanguage(context: Context, language: String) {
        getPrefs(context).edit().putString(KEY_SELECTED_LANGUAGE, language).apply()
    }

    fun hasCompletedOnboarding(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(context: Context, completed: Boolean = true) {
        getPrefs(context).edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun getOnboardingRole(context: Context): UserRole {
        return if (getPrefs(context).getString("onboarding_role", null) == UserRole.PROVIDER.name) {
            UserRole.PROVIDER
        } else {
            UserRole.CUSTOMER
        }
    }

    fun setOnboardingRole(context: Context, role: UserRole) {
        getPrefs(context).edit()
            .putString("onboarding_role", role.name)
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun isEmailVerified(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_EMAIL_VERIFIED, false)
    }

    fun setEmailVerified(context: Context, verified: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_EMAIL_VERIFIED, verified).apply()
    }

    fun setPendingVerifyEmail(context: Context, email: String?) {
        if (email == null) {
            getPrefs(context).edit().remove(KEY_PENDING_VERIFY_EMAIL).apply()
        } else {
            getPrefs(context).edit().putString(KEY_PENDING_VERIFY_EMAIL, email).apply()
        }
    }

    fun getPendingVerifyEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_PENDING_VERIFY_EMAIL, null)
    }

    fun saveUserSession(context: Context, user: User, isEmailVerified: Boolean = user.isVerified) {
        if (user.id == DEBUG_GUEST_ID) return

        getPrefs(context).edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putBoolean(KEY_EMAIL_VERIFIED, isEmailVerified)
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_USER_EMAIL, user.email)
            .putString(KEY_USER_ROLE, user.role.name)
            .apply()
    }

    fun getSavedUser(context: Context): User? {
        val prefs = getPrefs(context)
        if (!prefs.getBoolean(KEY_IS_LOGGED_IN, false)) return null
        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        val name = prefs.getString(KEY_USER_NAME, "") ?: ""
        val email = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        val roleStr = prefs.getString(KEY_USER_ROLE, UserRole.CUSTOMER.name) ?: UserRole.CUSTOMER.name
        val isVerified = prefs.getBoolean(KEY_EMAIL_VERIFIED, false)
        val role = try {
            UserRole.valueOf(roleStr)
        } catch (_: Exception) {
            UserRole.CUSTOMER
        }

        return User(
            id = id,
            name = name,
            email = email,
            phone = "",
            role = role,
            isVerified = isVerified
        )
    }

    fun clearSession(context: Context) {
        com.fixora.marketplace.data.repository.FixoraRepository.disconnectBackend()
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
        getPrefs(context).edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .putBoolean(KEY_EMAIL_VERIFIED, false)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_NAME)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_ROLE)
            .remove(KEY_PENDING_VERIFY_EMAIL)
            .apply()
    }
}
