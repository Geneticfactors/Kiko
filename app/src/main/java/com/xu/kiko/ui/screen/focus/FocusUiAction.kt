package com.xu.kiko.ui.screen.focus

/**
 * 专注页面用户操作意图
 * 定义专注页面所有可能的用户交互动作
 */
sealed interface FocusUiAction {
    // 选择 25 分钟专注时长
    data object Select25Minutes : FocusUiAction

    // 选择 45 分钟专注时长
    data object Select45Minutes : FocusUiAction

    // 打开自定义时长弹窗
    data object OpenCustomDuration : FocusUiAction

    // 关闭自定义时长弹窗
    data object CloseCustomDuration : FocusUiAction

    // 确认自定义时长
    data class ConfirmCustomDuration(val minutes: Int) : FocusUiAction

    // 开始专注计时
    data object StartTimer : FocusUiAction

    // 暂停专注计时
    data object PauseTimer : FocusUiAction

    // 恢复专注计时
    data object ResumeTimer : FocusUiAction

    // 请求停止计时（弹出确认对话框）
    data object RequestStopTimer : FocusUiAction

    // 取消停止计时（关闭确认对话框）
    data object DismissStopTimer : FocusUiAction

    // 确认停止计时（取消当前专注会话）
    data object ConfirmStopTimer : FocusUiAction

    // 关闭完成弹窗
    data object DismissFinishedOverlay : FocusUiAction

    // 关闭错误提示
    data object DismissError : FocusUiAction

    // 选择任务
    data class SelectTask(val taskId: String) : FocusUiAction

    // 跳转到任务列表页面
    data object ViewAllTasks : FocusUiAction

    // 标记任务完成状态
    data class SetTaskCompleted(
        val taskId: String,
        val completed: Boolean
    ) : FocusUiAction
}