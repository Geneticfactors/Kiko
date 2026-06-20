package com.xu.kiko.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**应用支持的六种固定主题色，名称与色值用于设置页展示和持久化。*/
enum class KikoThemeColor(
    val displayName: String,
    val color: Color
) {
    BLUE("蔚蓝", ThemeBlue),
    GREEN("翠绿", ThemeGreen),
    AMBER("琥珀", ThemeAmber),
    PINK("樱粉", ThemePink),
    ORANGE("暖橙", ThemeOrange),
    VIOLET("紫罗兰", ThemeViolet)
}

/**根据选中的品牌色生成浅色模式配色。*/
private fun lightKikoColorScheme(themeColor: KikoThemeColor): ColorScheme {
    val primary = themeColor.color
    //琥珀色较浅，按钮前景使用深色以保证对比度
    val onPrimary = if (themeColor == KikoThemeColor.AMBER) OnLightPrimary else OnPrimary

    return lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = lerp(primary, LightSurface, 0.82f),
        onPrimaryContainer = LightTextPrimary,
        secondary = primary,
        onSecondary = onPrimary,
        secondaryContainer = lerp(primary, LightSurface, 0.9f),
        onSecondaryContainer = LightTextPrimary,
        tertiary = Success,
        onTertiary = OnPrimary,
        tertiaryContainer = SuccessContainer,
        onTertiaryContainer = LightTextPrimary,
        error = Danger,
        onError = OnPrimary,
        errorContainer = DangerContainer,
        onErrorContainer = LightTextPrimary,
        background = LightBackground,
        onBackground = LightTextPrimary,
        surface = LightSurface,
        onSurface = LightTextPrimary,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightTextSecondary,
        outline = LightOutline,
        outlineVariant = lerp(LightOutline, LightSurface, 0.45f),
        scrim = Color.Black
    )
}

/**根据选中的品牌色生成深色模式配色。*/
private fun darkKikoColorScheme(themeColor: KikoThemeColor): ColorScheme {
    //深色背景上适当提亮主题色，增强文字、图标和控件的可见性
    val primary = lerp(themeColor.color, Color.White, 0.16f)

    return darkColorScheme(
        primary = primary,
        onPrimary = DarkBackground,
        primaryContainer = lerp(themeColor.color, DarkSurface, 0.65f),
        onPrimaryContainer = DarkTextPrimary,
        secondary = primary,
        onSecondary = DarkBackground,
        secondaryContainer = lerp(themeColor.color, DarkSurface, 0.78f),
        onSecondaryContainer = DarkTextPrimary,
        tertiary = lerp(Success, Color.White, 0.16f),
        onTertiary = DarkBackground,
        tertiaryContainer = lerp(Success, DarkSurface, 0.72f),
        onTertiaryContainer = DarkTextPrimary,
        error = lerp(Danger, Color.White, 0.28f),
        onError = DarkBackground,
        errorContainer = lerp(Danger, DarkSurface, 0.68f),
        onErrorContainer = DarkTextPrimary,
        background = DarkBackground,
        onBackground = DarkTextPrimary,
        surface = DarkSurface,
        onSurface = DarkTextPrimary,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkTextSecondary,
        outline = DarkOutline,
        outlineVariant = lerp(DarkOutline, DarkSurface, 0.35f),
        scrim = Color.Black
    )
}

/**
 * Kiko 全局主题入口。
 *
 * 默认跟随系统深浅模式并使用蔚蓝主题，同时向页面提供颜色、字体、圆角和间距令牌。
 */
@Composable
fun KikoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: KikoThemeColor = KikoThemeColor.BLUE,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkKikoColorScheme(themeColor)
    } else {
        lightKikoColorScheme(themeColor)
    }

    CompositionLocalProvider(LocalKikoSpacing provides KikoSpacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = KikoTypography,
            shapes = KikoShapes,
            content = content
        )
    }
}

/**通过 MaterialTheme.spacing 在任意 Composable 中读取统一间距。*/
val MaterialTheme.spacing: KikoSpacing
    @Composable
    @ReadOnlyComposable
    get() = LocalKikoSpacing.current
