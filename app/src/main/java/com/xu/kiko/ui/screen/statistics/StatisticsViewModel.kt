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

/**
 * 统计页面 ViewModel
 * 负责管理统计数据的加载和展示逻辑
 */
class StatisticsViewModel(
    // 统计数据仓库
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    // 内部 UI 状态流
    private val _uiState =
        MutableStateFlow(StatisticsUiState(isLoading = true))

    // 暴露给 UI 层的只读状态流
    val uiState: StateFlow<StatisticsUiState> =
        _uiState.asStateFlow()

    // 观察统计数据的协程任务
    private var observeStatisticsJob: Job? = null

    /**
     * ViewModel 初始化
     * 默认加载周统计数据
     */
    init {
        observeStatistics(StatisticsRange.WEEK)
    }

    /**
     * 处理用户操作
     * 根据 [StatisticsUiAction] 分发到对应的处理方法
     *
     * @param action 用户操作意图
     */
    fun onAction(action: StatisticsUiAction) {
        when (action) {
            is StatisticsUiAction.SelectRange ->
                observeStatistics(action.range)
        }
    }

    /**
     * 观察统计数据
     * 根据时间范围加载并监听统计数据变化
     *
     * @param range 时间范围（周/月）
     */
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

    /**
     * 将 Domain [StatisticsData] 转换为 UI 模型 [StatisticsUiState]
     *
     * @param selectedRange 当前选中的时间范围
     * @param isLoading 是否正在加载
     * @return UI 状态
     */
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

    /**
     * 将 UI 层的 [StatisticsRange] 转换为 Domain 层的 [StatisticsPeriod]
     *
     * @return 统计周期
     */
    private fun StatisticsRange.toPeriod(): StatisticsPeriod {
        return when (this) {
            StatisticsRange.WEEK -> StatisticsPeriod.WEEK
            StatisticsRange.MONTH -> StatisticsPeriod.MONTH
        }
    }
}
