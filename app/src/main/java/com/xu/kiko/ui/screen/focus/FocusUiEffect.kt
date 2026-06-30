package com.xu.kiko.ui.screen.focus

/**
 * 专注页面一次性副作用
 * 定义需要由 UI 层处理的一次性事件（如导航等）
 */
sealed interface FocusUiEffect {
    // 导航到任务列表页面
    data object NavigateToTasks : FocusUiEffect
}