package com.xu.kiko.ui.screen.focus

import com.xu.kiko.domain.model.Task

enum class FocusTimerStatus{
    IDLE,
    RUNNING,
    PAUSED
}

sealed interface FocusDurationOption{
    data object TwentyFiveMinutes : FocusDurationOption
    data object FortyFiveMinutes : FocusDurationOption
    data class Custom(val minutes: Int) : FocusDurationOption
}

data class FocusTaskUiModel(
    val id: String,
    val title: String,
    val completedPomodoros: Int,
    val estimatedPomodoros: Int,
    val isCompleted: Boolean
)

data class FocusUiState(
    val activeSessionId: String? = null,
    val selectedTaskId: String? = null,
    val dateText: String = "",
    val selectedDuration: FocusDurationOption =
        FocusDurationOption.TwentyFiveMinutes,
    val totalSeconds: Long = 25 * 60L,
    val remainingSeconds: Long = 25 * 60L,
    val timerStatus: FocusTimerStatus = FocusTimerStatus.IDLE,
    val todayPomodoroCount: Int = 0,
    val todayFocusMinutes: Int = 0,
    val todayTasks: List<FocusTaskUiModel> = emptyList(),
    val isCustomDurationSheetVisible: Boolean = false,
    val showFinishedOverlay: Boolean = false,
    val showInterruptConfirmSheet: Boolean = false,
    val focusErrorMessage: String? = null
)

fun Task.toFocusTaskUiModel(): FocusTaskUiModel {
    return FocusTaskUiModel(
        id = id,
        title = title,
        completedPomodoros = completedPomodoros,
        estimatedPomodoros = estimatedPomodoros,
        isCompleted = isCompleted
    )
}
