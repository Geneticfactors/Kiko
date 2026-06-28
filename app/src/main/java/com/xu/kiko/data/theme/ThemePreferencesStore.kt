package com.xu.kiko.data.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.xu.kiko.ui.theme.KikoThemeColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore(
    name = "kiko_theme"
)

class ThemePreferencesStore(
    context: Context
) {
    private val dataStore = context.applicationContext.themeDataStore

    fun observeThemePreferences(): Flow<ThemePreferences> {
        return dataStore.data.map { preferences ->
            ThemePreferences(
                darkModeEnabled = preferences[DARK_MODE_ENABLED]
                    ?: false,
                themeColor = preferences[THEME_COLOR]
                    ?.let(::themeColorOrDefault)
                    ?: KikoThemeColor.BLUE
            )
        }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun setThemeColor(themeColor: KikoThemeColor) {
        dataStore.edit { preferences ->
            preferences[THEME_COLOR] = themeColor.name
        }
    }

    private fun themeColorOrDefault(value: String): KikoThemeColor {
        return runCatching {
            KikoThemeColor.valueOf(value)
        }.getOrDefault(KikoThemeColor.BLUE)
    }

    private companion object {
        val DARK_MODE_ENABLED =
            booleanPreferencesKey("dark_mode_enabled")
        val THEME_COLOR =
            stringPreferencesKey("theme_color")
    }
}
