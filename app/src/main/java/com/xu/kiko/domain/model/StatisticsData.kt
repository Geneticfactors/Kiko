package com.xu.kiko.domain.model

data class StatisticsData(
    val totalFocusMinutes: Int,
    val comparePercent: Int,
    val streakDays: Int,
    val taskCompletionRate: Int,
    val dailyFocusMinutes: List<StatisticsDayFocus>,
    val hasData: Boolean
)

data class StatisticsDayFocus(
    val label: String,
    val valueMinutes: Int
)

enum class StatisticsPeriod {
    WEEK,
    MONTH
}
