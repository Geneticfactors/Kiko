package com.xu.kiko.ui.screen.statistics

sealed interface StatisticsUiAction {
    data class SelectRange(
        val range: StatisticsRange
    ): StatisticsUiAction
}