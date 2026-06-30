package com.xu.kiko.ui.screen.tasks

import com.xu.kiko.domain.model.TaskCategory
import com.xu.kiko.domain.usecase.task.TaskFieldError
import com.xu.kiko.domain.usecase.task.TaskValidationRules

/**
 * 任务编辑器模式
 * 区分创建任务和编辑任务两种模式
 */
enum class TaskEditorMode {
    // 创建新任务
    CREATE,

    // 编辑已有任务
    EDIT
}

/**
 * 任务编辑器 UI 状态
 * 包含编辑器所有输入数据和验证错误
 */
data class TaskEditorUiState(
    // 编辑器模式
    val mode: TaskEditorMode = TaskEditorMode.CREATE,

    // 正在编辑的任务 ID（编辑模式下有值）
    val editingTaskId: String? = null,

    // 任务标题
    val title: String = "",

    // 任务备注
    val note: String = "",

    // 任务分类
    val category: TaskCategory = TaskCategory.STUDY,

    // 预计需要的番茄数
    val estimatedPomodoros: Int =
        TaskValidationRules.DEFAULT_ESTIMATED_POMODOROS,

    // 标题验证错误
    val titleError: TaskFieldError? = null,

    // 备注验证错误
    val noteError: TaskFieldError? = null,

    // 番茄数验证错误
    val pomodoroError: TaskFieldError? = null
)