package com.xu.kiko.ui.screen.statistics

enum class StatisticsRange{
    WEEK,
    MONTH
}

data class StatisticsBarUiModel(
    val label: String,
    val valueMinutes: Int
)

data class StatisticsTrendPointUiModel(
    val valueMinutes: Int
)

data class StatisticsUiState(
    val selectedRange: StatisticsRange = StatisticsRange.WEEK,
    val totalFocusMinutes: Int = 750,
    val comparePercent: Int = 15,
    val streakDays: Int = 23,
    val taskCompletionRate: Int = 78,
    val dailyFocusBars: List<StatisticsBarUiModel> = emptyList(),
    val trendPoints: List<StatisticsTrendPointUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val hasData: Boolean = true
)