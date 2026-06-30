package com.xu.kiko.ui.screen.statistics

/**
 * 统计数据时间范围
 * 用于选择查看周统计或月统计
 */
enum class StatisticsRange {
    // 周统计
    WEEK,

    // 月统计
    MONTH
}

/**
 * 柱状图 UI 模型
 * 包含柱状图的标签和数值
 */
data class StatisticsBarUiModel(
    // 柱状图标签（如星期几）
    val label: String,

    // 专注时长（分钟）
    val valueMinutes: Int
)

/**
 * 趋势图数据点 UI 模型
 * 包含单个数据点的专注时长
 */
data class StatisticsTrendPointUiModel(
    // 专注时长（分钟）
    val valueMinutes: Int
)

/**
 * 统计页面完整 UI 状态
 * 包含页面所有显示数据和状态标志
 */
data class StatisticsUiState(
    // 当前选中的时间范围
    val selectedRange: StatisticsRange = StatisticsRange.WEEK,

    // 总专注时长（分钟）
    val totalFocusMinutes: Int = 750,

    // 与上一周期对比的变化百分比
    val comparePercent: Int = 15,

    // 连续专注天数
    val streakDays: Int = 23,

    // 任务完成率（百分比）
    val taskCompletionRate: Int = 78,

    // 每日专注柱状图数据
    val dailyFocusBars: List<StatisticsBarUiModel> = emptyList(),

    // 趋势图数据点
    val trendPoints: List<StatisticsTrendPointUiModel> = emptyList(),

    // 是否正在加载数据
    val isLoading: Boolean = false,

    // 是否有数据
    val hasData: Boolean = true
)