package com.xu.kiko.ui.screen.tasks

import com.xu.kiko.domain.model.TaskCategory

sealed interface TaskEditorUiAction {
    data class TitleChanged(
        val value: String
    ) : TaskEditorUiAction

    data class NoteChanged(
        val value: String
    ) : TaskEditorUiAction

    data class CategoryChanged(
        val category: TaskCategory
    ) : TaskEditorUiAction

    data object DecreasePomodoros : TaskEditorUiAction
    data object IncreasePomodoros : TaskEditorUiAction
    data object Save : TaskEditorUiAction
}