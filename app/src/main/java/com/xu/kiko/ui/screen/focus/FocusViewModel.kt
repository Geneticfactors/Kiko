package com.xu.kiko.ui.screen.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xu.kiko.domain.model.FocusSession
import com.xu.kiko.domain.model.FocusSessionStatus
import com.xu.kiko.domain.repository.FocusSessionRepository
import com.xu.kiko.domain.repository.TaskRepository
import com.xu.kiko.domain.usecase.task.ObserveTodayTaskUseCase
import com.xu.kiko.notification.FocusNotificationCoordinator
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 专注页面 ViewModel
 * 负责管理专注计时逻辑、任务选择、会话状态和 UI 状态
 */
class FocusViewModel(
    // 观察今日任务用例
    private val observeTodayTask: ObserveTodayTaskUseCase,
    // 任务仓库，用于任务操作
    private val taskRepository: TaskRepository,
    // 专注会话仓库，用于会话管理
    private val focusSessionRepository: FocusSessionRepository,
    // 通知协调器，用于计时期间的通知管理
    private val focusNotificationCoordinator: FocusNotificationCoordinator,
    // 当前时间提供器，默认使用系统时间（便于测试）
    private val nowProvider: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {

    // 内部 UI 状态流
    private val _uiState = MutableStateFlow(FocusUiState())

    // 暴露给 UI 层的只读状态流
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    // 内部副作用通道
    private val _effects = Channel<FocusUiEffect>(Channel.BUFFERED)

    // 暴露给 UI 层的副作用流
    val effects: Flow<FocusUiEffect> = _effects.receiveAsFlow()

    // 计时协程任务
    private var timerJob: Job? = null

    /**
     * ViewModel 初始化
     * 启动今日任务观察、今日统计观察和活动会话恢复
     */
    init {
        observeTodayTasks()
        observeTodaySummary()
        restoreActiveSession()
    }

    /**
     * ViewModel 销毁时取消计时任务
     */
    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    /**
     * 处理用户操作
     * 根据 [FocusUiAction] 分发到对应的处理方法
     *
     * @param action 用户操作意图
     */
    fun onAction(action: FocusUiAction) {
        when (action) {
            FocusUiAction.Select25Minutes ->
                selectDuration(
                    duration = FocusDurationOption.TwentyFiveMinutes,
                    minutes = 25
                )

            FocusUiAction.Select45Minutes ->
                selectDuration(
                    duration = FocusDurationOption.FortyFiveMinutes,
                    minutes = 45
                )

            FocusUiAction.OpenCustomDuration ->
                setCustomDurationSheetVisible(visible = true)

            FocusUiAction.CloseCustomDuration ->
                setCustomDurationSheetVisible(visible = false)

            is FocusUiAction.ConfirmCustomDuration ->
                confirmCustomDuration(action.minutes)

            is FocusUiAction.SelectTask ->
                selectTask(action.taskId)

            FocusUiAction.StartTimer ->
                startTimer()

            FocusUiAction.PauseTimer ->
                pauseTimer()

            FocusUiAction.ResumeTimer ->
                resumeTimer()

            FocusUiAction.RequestStopTimer ->
                requestStopTimer()

            FocusUiAction.DismissStopTimer ->
                _uiState.update { state ->
                    state.copy(showInterruptConfirmSheet = false)
                }

            FocusUiAction.ConfirmStopTimer ->
                cancelTimer()

            FocusUiAction.DismissFinishedOverlay ->
                _uiState.update { state ->
                    state.copy(showFinishedOverlay = false)
                }

            FocusUiAction.DismissError ->
                _uiState.update { state ->
                    state.copy(focusErrorMessage = null)
                }

            FocusUiAction.ViewAllTasks ->
                _effects.trySend(FocusUiEffect.NavigateToTasks)

            is FocusUiAction.SetTaskCompleted ->
                setTaskCompleted(
                    taskId = action.taskId,
                    completed = action.completed
                )
        }
    }

    /**
     * 选择专注时长
     * 仅在计时器处于空闲状态时生效
     *
     * @param duration 时长选项
     * @param minutes 时长（分钟）
     */
    private fun selectDuration(
        duration: FocusDurationOption,
        minutes: Int
    ) {
        _uiState.update { state ->
            if (state.timerStatus != FocusTimerStatus.IDLE) {
                return@update state
            }

            val totalSeconds = minutes * 60L

            state.copy(
                selectedDuration = duration,
                totalSeconds = totalSeconds,
                remainingSeconds = totalSeconds
            )
        }
    }

    /**
     * 设置自定义时长弹窗可见性
     * 仅在计时器空闲时允许显示
     *
     * @param visible 是否显示弹窗
     */
    private fun setCustomDurationSheetVisible(visible: Boolean) {
        _uiState.update { state ->
            state.copy(
                isCustomDurationSheetVisible =
                    visible && state.timerStatus == FocusTimerStatus.IDLE
            )
        }
    }

    /**
     * 确认自定义时长
     * 时长范围限制在 5~120 分钟
     *
     * @param minutes 用户选择的时长（分钟）
     */
    private fun confirmCustomDuration(minutes: Int) {
        val safeMinutes = minutes.coerceIn(5, 120)
        val totalSeconds = safeMinutes * 60L

        _uiState.update { state ->
            if (state.timerStatus != FocusTimerStatus.IDLE) {
                state.copy(isCustomDurationSheetVisible = false)
            } else {
                state.copy(
                    selectedDuration =
                        FocusDurationOption.Custom(safeMinutes),
                    totalSeconds = totalSeconds,
                    remainingSeconds = totalSeconds,
                    isCustomDurationSheetVisible = false
                )
            }
        }
    }

    /**
     * 选择任务
     * 仅在计时器空闲且任务未完成时可选择
     * 再次点击已选中的任务会取消选择
     *
     * @param taskId 任务 ID
     */
    private fun selectTask(taskId: String) {
        _uiState.update { state ->
            if (
                state.timerStatus != FocusTimerStatus.IDLE ||
                state.todayTasks.none { task ->
                    task.id == taskId && !task.isCompleted
                }
            ) {
                state
            } else {
                state.copy(
                    selectedTaskId =
                        if (state.selectedTaskId == taskId) null else taskId,
                    focusErrorMessage = null
                )
            }
        }
    }

    /**
     * 开始专注计时
     * 要求先选择任务，否则显示错误提示
     * 创建专注会话并启动计时 ticker
     */
    private fun startTimer() {
        val state = _uiState.value
        val taskId = state.selectedTaskId

        if (state.timerStatus != FocusTimerStatus.IDLE) {
            return
        }

        if (taskId == null) {
            _uiState.update {
                it.copy(focusErrorMessage = SELECT_TASK_MESSAGE)
            }
            return
        }

        viewModelScope.launch {
            runCatching {
                focusSessionRepository.startSession(
                    taskId = taskId,
                    durationSeconds = state.totalSeconds
                )
            }.onSuccess { session ->
                _uiState.update {
                    it.copy(
                        activeSessionId = session.id,
                        timerStatus = FocusTimerStatus.RUNNING,
                        remainingSeconds = session.plannedDurationSeconds,
                        totalSeconds = session.plannedDurationSeconds,
                        focusErrorMessage = null,
                        showFinishedOverlay = false,
                        showInterruptConfirmSheet = false
                    )
                }
                startTicker()
                focusNotificationCoordinator.onTimerStarted()
            }.onFailure {
                _uiState.update {
                    it.copy(focusErrorMessage = START_FAILED_MESSAGE)
                }
            }
        }
    }

    /**
     * 暂停专注计时
     * 记录当前已专注时长并更新会话状态
     */
    private fun pauseTimer() {
        val sessionId = _uiState.value.activeSessionId ?: return
        if (_uiState.value.timerStatus != FocusTimerStatus.RUNNING) {
            return
        }

        viewModelScope.launch {
            val focusedSeconds = currentFocusedSeconds()
            runCatching {
                focusSessionRepository.pauseSession(
                    sessionId = sessionId,
                    focusedSeconds = focusedSeconds
                )
            }.onSuccess {
                timerJob?.cancel()
                _uiState.update { state ->
                    state.copy(
                        timerStatus = FocusTimerStatus.PAUSED,
                        remainingSeconds =
                            remainingSecondsFor(focusedSeconds, state)
                    )
                }
                focusNotificationCoordinator.onTimerPausedOrResumed()
            }.onFailure {
                _uiState.update {
                    it.copy(focusErrorMessage = OPERATION_FAILED_MESSAGE)
                }
            }
        }
    }

    /**
     * 恢复专注计时
     * 重新启动计时 ticker
     */
    private fun resumeTimer() {
        val sessionId = _uiState.value.activeSessionId ?: return
        if (_uiState.value.timerStatus != FocusTimerStatus.PAUSED) {
            return
        }

        viewModelScope.launch {
            runCatching {
                focusSessionRepository.resumeSession(sessionId)
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(timerStatus = FocusTimerStatus.RUNNING)
                }
                startTicker()
                focusNotificationCoordinator.onTimerPausedOrResumed()
            }.onFailure {
                _uiState.update {
                    it.copy(focusErrorMessage = OPERATION_FAILED_MESSAGE)
                }
            }
        }
    }

    /**
     * 请求停止计时
     * 显示中断确认弹窗
     */
    private fun requestStopTimer() {
        _uiState.update { state ->
            if (state.timerStatus == FocusTimerStatus.IDLE) {
                state
            } else {
                state.copy(showInterruptConfirmSheet = true)
            }
        }
    }

    /**
     * 取消专注计时
     * 终止当前会话并重置计时器状态
     */
    private fun cancelTimer() {
        val sessionId = _uiState.value.activeSessionId ?: return
        viewModelScope.launch {
            val focusedSeconds = currentFocusedSeconds()
            runCatching {
                focusSessionRepository.cancelSession(
                    sessionId = sessionId,
                    focusedSeconds = focusedSeconds
                )
            }.onSuccess {
                timerJob?.cancel()
                focusNotificationCoordinator.onTimerCancelled()
                resetTimer(
                    showFinishedOverlay = false,
                    selectedTaskId = _uiState.value.selectedTaskId
                )
            }.onFailure {
                _uiState.update {
                    it.copy(focusErrorMessage = OPERATION_FAILED_MESSAGE)
                }
            }
        }
    }

    /**
     * 完成专注计时
     * 标记会话完成并增加任务的完成番茄数
     */
    private fun completeTimer() {
        val sessionId = _uiState.value.activeSessionId ?: return
        val taskId = _uiState.value.selectedTaskId ?: return

        viewModelScope.launch {
            val focusedSeconds = _uiState.value.totalSeconds
            runCatching {
                val completed = focusSessionRepository.completeSession(
                    sessionId = sessionId,
                    focusedSeconds = focusedSeconds
                )
                if (completed) {
                    taskRepository.incrementCompletedPomodoros(taskId)
                }
            }.onSuccess {
                timerJob?.cancel()
                focusNotificationCoordinator.onTimerCompleted()
                resetTimer(
                    showFinishedOverlay = true,
                    selectedTaskId = null
                )
            }.onFailure {
                _uiState.update {
                    it.copy(focusErrorMessage = OPERATION_FAILED_MESSAGE)
                }
            }
        }
    }

    /**
     * 启动计时 ticker
     * 每秒更新剩余时间，当剩余时间为 0 时完成计时
     */
    private fun startTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val focusedSeconds = currentFocusedSeconds()
                val remainingSeconds =
                    remainingSecondsFor(focusedSeconds, _uiState.value)

                _uiState.update { state ->
                    state.copy(remainingSeconds = remainingSeconds)
                }

                if (remainingSeconds <= 0L) {
                    completeTimer()
                    return@launch
                }

                delay(TICK_DELAY_MILLIS)
            }
        }
    }

    /**
     * 获取当前已专注时长
     * 从活动会话中计算已专注的秒数
     *
     * @return 已专注时长（秒）
     */
    private suspend fun currentFocusedSeconds(): Long {
        val session = focusSessionRepository.getActiveSession()
            ?: return 0L
        return focusedSecondsFor(session)
    }

    /**
     * 计算会话已专注时长
     * 包含历史专注时长和当前运行期间的时长
     *
     * @param session 专注会话
     * @return 已专注时长（秒）
     */
    private fun focusedSecondsFor(session: FocusSession): Long {
        val runningSeconds =
            if (
                session.status == FocusSessionStatus.RUNNING &&
                session.lastStartedAtEpochMillis != null
            ) {
                ((nowProvider() - session.lastStartedAtEpochMillis) / 1000L)
                    .coerceAtLeast(0L)
            } else {
                0L
            }

        return (session.focusedDurationSeconds + runningSeconds)
            .coerceAtMost(session.plannedDurationSeconds)
    }

    /**
     * 计算剩余时长
     *
     * @param focusedSeconds 已专注时长（秒）
     * @param state 当前 UI 状态
     * @return 剩余时长（秒）
     */
    private fun remainingSecondsFor(
        focusedSeconds: Long,
        state: FocusUiState
    ): Long {
        return (state.totalSeconds - focusedSeconds)
            .coerceAtLeast(0L)
    }

    /**
     * 恢复活动会话
     * 在 ViewModel 初始化时调用，恢复上次未完成的专注会话
     */
    private fun restoreActiveSession() {
        viewModelScope.launch {
            val session = focusSessionRepository.getActiveSession()
                ?: return@launch
            val focusedSeconds = focusedSecondsFor(session)
            val remainingSeconds =
                (session.plannedDurationSeconds - focusedSeconds)
                    .coerceAtLeast(0L)

            _uiState.update { state ->
                state.copy(
                    activeSessionId = session.id,
                    selectedTaskId = session.taskId,
                    totalSeconds = session.plannedDurationSeconds,
                    remainingSeconds = remainingSeconds,
                    timerStatus =
                        if (session.status == FocusSessionStatus.RUNNING) {
                            FocusTimerStatus.RUNNING
                        } else {
                            FocusTimerStatus.PAUSED
                        }
                )
            }

            if (remainingSeconds <= 0L) {
                completeTimer()
            } else if (session.status == FocusSessionStatus.RUNNING) {
                startTicker()
                focusNotificationCoordinator.onTimerStarted()
            }
        }
    }

    /**
     * 重置计时器状态
     * 清除活动会话，重置状态为空闲
     *
     * @param showFinishedOverlay 是否显示完成弹窗
     * @param selectedTaskId 保留的选中任务 ID
     */
    private fun resetTimer(
        showFinishedOverlay: Boolean,
        selectedTaskId: String?
    ) {
        _uiState.update { state ->
            state.copy(
                activeSessionId = null,
                selectedTaskId = selectedTaskId,
                timerStatus = FocusTimerStatus.IDLE,
                remainingSeconds = state.totalSeconds,
                showFinishedOverlay = showFinishedOverlay,
                showInterruptConfirmSheet = false
            )
        }
    }

    /**
     * 设置任务完成状态
     *
     * @param taskId 任务 ID
     * @param completed 是否完成
     */
    private fun setTaskCompleted(
        taskId: String,
        completed: Boolean
    ) {
        viewModelScope.launch {
            runCatching {
                taskRepository.setTaskCompleted(
                    taskId = taskId,
                    completed = completed
                )
            }
        }
    }

    /**
     * 观察今日任务列表
     * 监听任务变化并更新 UI 状态
     */
    private fun observeTodayTasks() {
        viewModelScope.launch {
            observeTodayTask(TODAY_TASK_LIMIT)
                .catch {
                    _uiState.update { state ->
                        state.copy(todayTasks = emptyList())
                    }
                }
                .collect { tasks ->
                    _uiState.update { state ->
                        val todayTasks = tasks.map { task ->
                            task.toFocusTaskUiModel()
                        }
                        state.copy(
                            todayTasks = todayTasks,
                            selectedTaskId =
                                state.selectedTaskId
                                    ?.takeIf { selectedTaskId ->
                                        todayTasks.any { task ->
                                            task.id == selectedTaskId &&
                                                !task.isCompleted
                                        }
                                    }
                        )
                    }
                }
        }
    }

    /**
     * 观察今日专注摘要
     * 监听专注统计数据变化并更新 UI 状态
     */
    private fun observeTodaySummary() {
        viewModelScope.launch {
            focusSessionRepository.observeTodaySummary()
                .catch {
                    _uiState.update { state ->
                        state.copy(
                            todayPomodoroCount = 0,
                            todayFocusMinutes = 0
                        )
                    }
                }
                .collect { summary ->
                    _uiState.update { state ->
                        state.copy(
                            todayPomodoroCount =
                                summary.completedPomodoros,
                            todayFocusMinutes =
                                summary.focusedMinutes
                        )
                    }
                }
        }
    }

    /**
     * 常量定义
     */
    private companion object {
        // 今日任务显示数量限制
        const val TODAY_TASK_LIMIT = 3

        // 计时更新间隔（毫秒）
        const val TICK_DELAY_MILLIS = 1_000L

        // 未选择任务时的提示消息
        const val SELECT_TASK_MESSAGE = "请先选择一个今日任务"

        // 开始专注失败提示消息
        const val START_FAILED_MESSAGE = "专注开始失败，请重试"

        // 通用操作失败提示消息
        const val OPERATION_FAILED_MESSAGE = "操作失败，请重试"
    }
}
