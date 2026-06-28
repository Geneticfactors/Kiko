package com.xu.kiko.data.theme

import com.xu.kiko.ui.theme.KikoThemeColor

data class ThemePreferences(
    val darkModeEnabled: Boolean = false,
    val themeColor: KikoThemeColor = KikoThemeColor.BLUE
)
