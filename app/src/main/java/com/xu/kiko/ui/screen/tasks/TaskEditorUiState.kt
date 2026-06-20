package com.xu.kiko.ui.screen.tasks

import com.xu.kiko.domain.model.TaskCategory
import com.xu.kiko.domain.usecase.task.TaskFieldError
import com.xu.kiko.domain.usecase.task.TaskValidationRules

enum class TaskEditorMode {
    CREATE,
    EDIT
}

data class TaskEditorUiState(
    val mode: TaskEditorMode = TaskEditorMode.CREATE,
    val editingTaskId: String? = null,
    val title: String = "",
    val note: String = "",
    val category: TaskCategory = TaskCategory.STUDY,
    val estimatedPomodoros: Int =
        TaskValidationRules.DEFAULT_ESTIMATED_POMODOROS,
    val titleError: TaskFieldError? = null,
    val noteError: TaskFieldError? = null,
    val pomodoroError: TaskFieldError? = null
)