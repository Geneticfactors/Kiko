package com.xu.kiko.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**基于 4dp 网格的全局间距规范。*/
@Immutable
data class KikoSpacing(
    //无间距
    val none: Dp = 0.dp,
    //紧凑元素间距
    val extraSmall: Dp = 4.dp,
    //图标与文字、相邻控件间距
    val small: Dp = 8.dp,
    //组件内部常规间距
    val medium: Dp = 12.dp,
    //卡片内部和普通内容间距
    val large: Dp = 16.dp,
    //页面默认水平边距
    val extraLarge: Dp = 20.dp,
    //页面区块之间的间距
    val section: Dp = 24.dp,
    //登录和注册表单水平边距
    val formHorizontal: Dp = 32.dp,
    //交互控件最小触摸尺寸
    val touchTarget: Dp = 48.dp
)

/**KikoTheme 向 Composable 树提供间距令牌的入口。*/
internal val LocalKikoSpacing = staticCompositionLocalOf { KikoSpacing() }
