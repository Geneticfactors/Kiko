package com.xu.kiko.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.ui.component.KikoCard
import com.xu.kiko.ui.component.LoadingContent
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

@Composable
fun StatisticsScreen(
    state: StatisticsUiState,
    onAction: (StatisticsUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = MaterialTheme.spacing.extraLarge)
    ) {
        Text(
            text = stringResource(R.string.statistics_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = MaterialTheme.spacing.large,
                    bottom = MaterialTheme.spacing.section
                ),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        when {
            state.isLoading -> {
                LoadingContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            !state.hasData -> {
                StatisticsEmptyContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            else -> {
                StatisticsScreenContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    }
}

@Composable
private fun StatisticsScreenContent(
    state: StatisticsUiState,
    onAction: (StatisticsUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(bottom = MaterialTheme.spacing.section),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
    ) {
        StatisticsTotalFocusCard(
            totalFocusMinutes = state.totalFocusMinutes,
            comparePercent = state.comparePercent,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
        ) {
            StatisticsMetricCard(
                value = stringResource(
                    R.string.statistics_streak_days,
                    state.streakDays
                ),
                label = stringResource(R.string.statistics_streak_label),
                modifier = Modifier.weight(1f)
            )
            StatisticsMetricCard(
                value = stringResource(
                    R.string.statistics_completion_rate,
                    state.taskCompletionRate
                ),
                label = stringResource(R.string.statistics_completion_label),
                modifier = Modifier.weight(1f)
            )
        }

        StatisticsChartCard(
            title = stringResource(R.string.statistics_daily_focus),
            selectedRange = state.selectedRange,
            onRangeSelected = { range ->
                onAction(StatisticsUiAction.SelectRange(range))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            DailyFocusBarChart(
                bars = state.dailyFocusBars,
                modifier = Modifier.fillMaxWidth()
            )
        }

        StatisticsChartCard(
            title = stringResource(R.string.statistics_focus_trend),
            selectedRange = state.selectedRange,
            onRangeSelected = { range ->
                onAction(StatisticsUiAction.SelectRange(range))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            FocusTrendChart(
                points = state.trendPoints,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatisticsChartCard(
    title: String,
    selectedRange: StatisticsRange,
    onRangeSelected: (StatisticsRange) -> Unit,
    modifier: Modifier = Modifier,
    chart: @Composable () -> Unit
) {
    KikoCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatisticsRangeSelector(
                    selectRange = selectedRange,
                    onRangeSelected = onRangeSelected
                )
            }

            chart()
        }
    }
}

private fun previewStatisticsState(
    selectedRange: StatisticsRange = StatisticsRange.WEEK,
    hasData: Boolean = true,
    isLoading: Boolean = false
): StatisticsUiState {
    return StatisticsUiState(
        selectedRange = selectedRange,
        dailyFocusBars = if (selectedRange == StatisticsRange.WEEK) {
            listOf(
                StatisticsBarUiModel("一", 90),
                StatisticsBarUiModel("二", 120),
                StatisticsBarUiModel("三", 60),
                StatisticsBarUiModel("四", 150),
                StatisticsBarUiModel("五", 110),
                StatisticsBarUiModel("六", 180),
                StatisticsBarUiModel("日", 40)
            )
        } else {
            listOf(
                StatisticsBarUiModel("1", 60),
                StatisticsBarUiModel("5", 120),
                StatisticsBarUiModel("10", 90),
                StatisticsBarUiModel("15", 180),
                StatisticsBarUiModel("20", 150),
                StatisticsBarUiModel("25", 210),
                StatisticsBarUiModel("30", 160)
            )
        },
        trendPoints = if (selectedRange == StatisticsRange.WEEK) {
            listOf(
                StatisticsTrendPointUiModel(60),
                StatisticsTrendPointUiModel(120),
                StatisticsTrendPointUiModel(90),
                StatisticsTrendPointUiModel(160),
                StatisticsTrendPointUiModel(140),
                StatisticsTrendPointUiModel(210),
                StatisticsTrendPointUiModel(180)
            )
        } else {
            listOf(
                StatisticsTrendPointUiModel(90),
                StatisticsTrendPointUiModel(140),
                StatisticsTrendPointUiModel(120),
                StatisticsTrendPointUiModel(190),
                StatisticsTrendPointUiModel(170),
                StatisticsTrendPointUiModel(220),
                StatisticsTrendPointUiModel(200)
            )
        },
        hasData = hasData,
        isLoading = isLoading
    )
}

@Preview(
    name = "Statistics - Default",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun StatisticsScreenDefaultPreview() {
    KikoTheme {
        StatisticsScreen(
            state = previewStatisticsState(),
            onAction = {}
        )
    }
}

@Preview(
    name = "Statistics - Empty",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun StatisticsScreenEmptyPreview() {
    KikoTheme {
        StatisticsScreen(
            state = previewStatisticsState(hasData = false),
            onAction = {}
        )
    }
}

@Preview(
    name = "Statistics - Month",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun StatisticsScreenMonthPreview() {
    KikoTheme {
        StatisticsScreen(
            state = previewStatisticsState(
                selectedRange = StatisticsRange.MONTH
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Statistics - Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun StatisticsScreenDarkPreview() {
    KikoTheme(darkTheme = true) {
        StatisticsScreen(
            state = previewStatisticsState(),
            onAction = {}
        )
    }
}

@Preview(
    name = "Statistics - 320dp",
    showBackground = true,
    widthDp = 320,
    heightDp = 844
)
@Composable
private fun StatisticsScreenNarrowPreview() {
    KikoTheme {
        StatisticsScreen(
            state = previewStatisticsState(),
            onAction = {}
        )
    }
}

