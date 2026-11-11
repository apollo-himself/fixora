package com.fixora.marketplace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.fixora.marketplace.data.repository.CloudStore
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.fixora.marketplace.data.i18n.*
import com.fixora.marketplace.data.local.AppPreferences
import com.fixora.marketplace.ui.navigation.FixoraNavGraph
import com.fixora.marketplace.ui.theme.AppThemeManager
import com.fixora.marketplace.ui.theme.AppThemeMode
import com.fixora.marketplace.ui.theme.FixoraBackground
import com.fixora.marketplace.ui.theme.FixoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Load saved language & theme preferences
        AppLocaleManager.setLanguage(AppLanguage.from(AppPreferences.getSelectedLanguage(this)))
        AppThemeManager.setThemeModeByName(AppPreferences.getThemeMode(this))

        setContent {
            val currentLanguage by AppLocaleManager.currentLanguage.collectAsState()
            val strings = getStrings(currentLanguage)

            val currentThemeMode by AppThemeManager.currentThemeMode.collectAsState()
            val isSystemDark = isSystemInDarkTheme()
            val darkTheme = when (currentThemeMode) {
                AppThemeMode.SYSTEM -> isSystemDark
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            CompositionLocalProvider(
                LocalAppStrings provides strings,
                LocalCurrentLanguage provides currentLanguage,
                LocalLayoutDirection provides currentLanguage.layoutDirection
            ) {
                FixoraTheme(darkTheme = darkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = FixoraBackground
                    ) {
                        val pending by CloudStore.pendingWrites.collectAsState()
                        val error by CloudStore.error.collectAsState()
                        val snackbar = remember { SnackbarHostState() }
                        LaunchedEffect(error) {
                            error?.let {
                                snackbar.showSnackbar(it, duration = SnackbarDuration.Long)
                                if (CloudStore.error.value == it) CloudStore.error.value = null
                            }
                        }
                        Scaffold(
                            snackbarHost = { SnackbarHost(snackbar) },
                            contentWindowInsets = WindowInsets(0, 0, 0, 0)
                        ) { padding ->
                            Column(Modifier.fillMaxSize().padding(padding)) {
                                if (pending > 0) {
                                    LinearProgressIndicator(Modifier.fillMaxWidth())
                                    Text("Saving changes…", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.labelSmall)
                                }
                                Box(Modifier.weight(1f)) { FixoraNavGraph() }
                            }
                        }
                    }
                }
            }
        }
    }
}
