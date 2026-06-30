package com.xu.kiko.ui.screen.tasks

/**
 * 任务页面一次性副作用
 * 定义需要由 UI 层处理的一次性事件
 */
sealed interface TasksUiEffect {
    // 显示提示消息
    data class ShowMessage(
        val message: String
    ) : TasksUiEffect
}