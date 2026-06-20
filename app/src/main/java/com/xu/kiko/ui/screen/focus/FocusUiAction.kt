package com.xu.kiko.ui.screen.focus

sealed interface FocusUiAction{
    data object Select25Minutes : FocusUiAction
    data object Select45Minutes : FocusUiAction
    data object OpenCustomDuration : FocusUiAction
    data object CloseCustomDuration : FocusUiAction
    data class ConfirmCustomDuration(val minutes: Int) : FocusUiAction
    data object StartTimer : FocusUiAction
    data object PauseTimer : FocusUiAction
    data object ResumeTimer : FocusUiAction
    data object RequestStopTimer : FocusUiAction
    data object DismissStopTimer : FocusUiAction
    data object ConfirmStopTimer : FocusUiAction
    data object DismissFinishedOverlay : FocusUiAction
    data object DismissError : FocusUiAction
    data class SelectTask(val taskId: String) : FocusUiAction
    data object ViewAllTasks : FocusUiAction
    data class SetTaskCompleted(
        val taskId: String,
        val completed: Boolean
    ) : FocusUiAction
}
