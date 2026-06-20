package com.xu.kiko.ui.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xu.kiko.domain.model.StatisticsData
import com.xu.kiko.domain.model.StatisticsPeriod
import com.xu.kiko.domain.repository.StatisticsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(StatisticsUiState(isLoading = true))
    val uiState: StateFlow<StatisticsUiState> =
        _uiState.asStateFlow()

    private var observeStatisticsJob: Job? = null

    init {
        observeStatistics(StatisticsRange.WEEK)
    }

    fun onAction(action: StatisticsUiAction) {
        when (action) {
            is StatisticsUiAction.SelectRange ->
                observeStatistics(action.range)
        }
    }

    private fun observeStatistics(range: StatisticsRange) {
        observeStatisticsJob?.cancel()
        observeStatisticsJob = viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    selectedRange = range,
                    isLoading = true
                )
            }

            statisticsRepository.observeStatistics(range.toPeriod())
                .catch {
                    _uiState.update { state ->
                        state.copy(
                            totalFocusMinutes = 0,
                            comparePercent = 0,
                            streakDays = 0,
                            taskCompletionRate = 0,
                            dailyFocusBars = emptyList(),
                            trendPoints = emptyList(),
                            hasData = false,
                            isLoading = false
                        )
                    }
                }
                .collect { data ->
                    _uiState.update {
                        data.toUiState(
                            selectedRange = range,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun StatisticsData.toUiState(
        selectedRange: StatisticsRange,
        isLoading: Boolean
    ): StatisticsUiState {
        return StatisticsUiState(
            selectedRange = selectedRange,
            totalFocusMinutes = totalFocusMinutes,
            comparePercent = comparePercent,
            streakDays = streakDays,
            taskCompletionRate = taskCompletionRate,
            dailyFocusBars = dailyFocusMinutes.map { day ->
                StatisticsBarUiModel(
                    label = day.label,
                    valueMinutes = day.valueMinutes
                )
            },
            trendPoints = dailyFocusMinutes.map { day ->
                StatisticsTrendPointUiModel(
                    valueMinutes = day.valueMinutes
                )
            },
            isLoading = isLoading,
            hasData = hasData
        )
    }

    private fun StatisticsRange.toPeriod(): StatisticsPeriod {
        return when (this) {
            StatisticsRange.WEEK -> StatisticsPeriod.WEEK
            StatisticsRange.MONTH -> StatisticsPeriod.MONTH
        }
    }
}
