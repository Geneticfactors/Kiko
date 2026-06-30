package com.xu.kiko.ui.screen.tasks

import com.xu.kiko.domain.model.TaskCategory

/**
 * 任务编辑器用户操作意图
 * 定义任务编辑器内所有可能的用户交互动作
 */
sealed interface TaskEditorUiAction {
    // 标题输入变化
    data class TitleChanged(
        val value: String
    ) : TaskEditorUiAction

    // 备注输入变化
    data class NoteChanged(
        val value: String
    ) : TaskEditorUiAction

    // 分类选择变化
    data class CategoryChanged(
        val category: TaskCategory
    ) : TaskEditorUiAction

    // 减少预计番茄数
    data object DecreasePomodoros : TaskEditorUiAction

    // 增加预计番茄数
    data object IncreasePomodoros : TaskEditorUiAction

    // 保存任务
    data object Save : TaskEditorUiAction
}