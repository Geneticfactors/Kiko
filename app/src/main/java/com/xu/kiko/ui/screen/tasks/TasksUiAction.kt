package com.xu.kiko.ui.screen.tasks

/**
 * 任务页面用户操作意图
 * 定义任务页面所有可能的用户交互动作
 */
sealed interface TasksUiAction {
    // 选择筛选条件
    data class SelectFilter(
        val filter: TaskFilter
    ) : TasksUiAction

    // 打开创建任务编辑器
    data object OpenCreateTask : TasksUiAction

    // 打开编辑任务编辑器
    data class OpenEditTask(
        val taskId: String
    ) : TasksUiAction

    // 关闭任务编辑器
    data object CloseEditor : TasksUiAction

    // 任务编辑器内的操作
    data class EditorAction(
        val action: TaskEditorUiAction
    ) : TasksUiAction

    // 设置任务完成状态
    data class SetTaskCompleted(
        val taskId: String,
        val completed: Boolean
    ) : TasksUiAction

    // 请求删除任务（弹出确认对话框）
    data class RequestDeleteTask(
        val taskId: String
    ) : TasksUiAction

    // 取消删除任务（关闭确认对话框）
    data object CancelDeleteTask : TasksUiAction

    // 确认删除任务
    data object ConfirmDeleteTask : TasksUiAction

    // 重试加载任务
    data object Retry : TasksUiAction

    // 关闭提示消息
    data object DismissMessage : TasksUiAction
}