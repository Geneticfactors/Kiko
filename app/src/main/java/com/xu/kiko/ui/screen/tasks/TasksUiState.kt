package com.xu.kiko.ui.screen.tasks

import com.xu.kiko.domain.model.TaskCategory

enum class TaskFilter {
    ALL,
    STUDY,
    WORK,
    READING
}

enum class TaskDateSection{
    TODAY,
    YESTERDAY,
    EARLIER
}

data class TaskUiModel(
    val id: String,
    val title: String,
    val note: String?,
    val category: TaskCategory,
    val estimatedPomodoros: Int,
    val completedPomodoros: Int,
    val isCompleted: Boolean,
    val dateSection: TaskDateSection
)

data class TaskSectionUiModel(
    val section: TaskDateSection,
    val tasks: List<TaskUiModel>
)

data class TasksUiState(
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val sections: List<TaskSectionUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val editor: TaskEditorUiState? = null,
    val pendingDeleteTask: TaskUiModel? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false
)