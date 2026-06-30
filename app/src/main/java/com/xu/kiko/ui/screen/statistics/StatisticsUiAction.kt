package com.xu.kiko.ui.screen.statistics

/**
 * 统计页面用户操作意图
 * 定义统计页面所有可能的用户交互动作
 */
sealed interface StatisticsUiAction {
    // 选择时间范围（周/月）
    data class SelectRange(
        val range: StatisticsRange
    ) : StatisticsUiAction
}