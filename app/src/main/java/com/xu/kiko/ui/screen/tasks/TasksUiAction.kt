package com.xu.kiko.ui.screen.tasks

sealed interface TasksUiAction {
    data class SelectFilter(
        val filter: TaskFilter
    ) : TasksUiAction

    data object OpenCreateTask : TasksUiAction

    data class OpenEditTask(
        val taskId: String
    ) : TasksUiAction

    data object CloseEditor : TasksUiAction

    data class EditorAction(
        val action: TaskEditorUiAction
    ) : TasksUiAction

    data class SetTaskCompleted(
        val taskId: String,
        val completed: Boolean
    ) : TasksUiAction

    data class RequestDeleteTask(
        val taskId: String
    ) : TasksUiAction

    data object CancelDeleteTask : TasksUiAction
    data object ConfirmDeleteTask : TasksUiAction
    data object Retry : TasksUiAction
    data object DismissMessage : TasksUiAction
}