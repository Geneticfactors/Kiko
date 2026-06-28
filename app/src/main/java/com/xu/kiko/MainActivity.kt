package com.xu.kiko

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.data.theme.ThemePreferences
import com.xu.kiko.ui.theme.KikoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val themePreferencesStore = remember(context) {
                AppDependencies.themePreferencesStore(context)
            }
            val themePreferences by themePreferencesStore
                .observeThemePreferences()
                .collectAsStateWithLifecycle(
                    initialValue = ThemePreferences()
                )

            KikoTheme(
                darkTheme = themePreferences.darkModeEnabled,
                themeColor = themePreferences.themeColor
            ) {
                KikoApp()
            }
        }
    }
}
