package com.xu.kiko.ui.screen.register

/**
 * 注册页面一次性副作用
 * 定义需要由 UI 层处理的一次性事件（如导航等）
 */
sealed interface RegisterUiEffect {
    // 注册成功，通知上层自动登录并导航到主页
    data object Registered : RegisterUiEffect

    // 返回登录页面
    data object NavigateBack : RegisterUiEffect
}
