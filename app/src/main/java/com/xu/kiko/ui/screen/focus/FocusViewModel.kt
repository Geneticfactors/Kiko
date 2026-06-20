package com.xu.kiko.ui.screen.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xu.kiko.domain.model.FocusSession
import com.xu.kiko.domain.model.FocusSessionStatus
import com.xu.kiko.domain.repository.FocusSessionRepository
import com.xu.kiko.domain.repository.TaskRepository
import com.xu.kiko.domain.usecase.task.ObserveTodayTaskUseCase
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

class FocusViewModel(
    private val observeTodayTask: ObserveTodayTaskUseCase,
    private val taskRepository: TaskRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val nowProvider: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> =
        _uiState.asStateFlow()

    private val _effects =
        Channel<FocusUiEffect>(Channel.BUFFERED)
    val effects: Flow<FocusUiEffect> =
        _effects.receiveAsFlow()

    private var timerJob: Job? = null

    init {
        observeTodayTasks()
        observeTodaySummary()
        restoreActiveSession()
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

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

    private fun setCustomDurationSheetVisible(visible: Boolean) {
        _uiState.update { state ->
            state.copy(
                isCustomDurationSheetVisible =
                    visible && state.timerStatus == FocusTimerStatus.IDLE
            )
        }
    }

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
            }.onFailure {
                _uiState.update {
                    it.copy(focusErrorMessage = START_FAILED_MESSAGE)
                }
            }
        }
    }

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
            }.onFailure {
                _uiState.update {
                    it.copy(focusErrorMessage = OPERATION_FAILED_MESSAGE)
                }
            }
        }
    }

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
            }.onFailure {
                _uiState.update {
                    it.copy(focusErrorMessage = OPERATION_FAILED_MESSAGE)
                }
            }
        }
    }

    private fun requestStopTimer() {
        _uiState.update { state ->
            if (state.timerStatus == FocusTimerStatus.IDLE) {
                state
            } else {
                state.copy(showInterruptConfirmSheet = true)
            }
        }
    }

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

    private fun completeTimer() {
        val sessionId = _uiState.value.activeSessionId ?: return
        val taskId = _uiState.value.selectedTaskId ?: return

        viewModelScope.launch {
            val focusedSeconds = _uiState.value.totalSeconds
            runCatching {
                focusSessionRepository.completeSession(
                    sessionId = sessionId,
                    focusedSeconds = focusedSeconds
                )
                taskRepository.incrementCompletedPomodoros(taskId)
            }.onSuccess {
                timerJob?.cancel()
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

    private suspend fun currentFocusedSeconds(): Long {
        val session = focusSessionRepository.getActiveSession()
            ?: return 0L
        return focusedSecondsFor(session)
    }

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

    private fun remainingSecondsFor(
        focusedSeconds: Long,
        state: FocusUiState
    ): Long {
        return (state.totalSeconds - focusedSeconds)
            .coerceAtLeast(0L)
    }

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
            }
        }
    }

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

    private companion object {
        const val TODAY_TASK_LIMIT = 3
        const val TICK_DELAY_MILLIS = 1_000L
        const val SELECT_TASK_MESSAGE = "请先选择一个今日任务"
        const val START_FAILED_MESSAGE = "专注开始失败，请重试"
        const val OPERATION_FAILED_MESSAGE = "操作失败，请重试"
    }
}
