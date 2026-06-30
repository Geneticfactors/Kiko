package com.xu.kiko.ui.screen.login

/**
 * 登录页面一次性副作用
 * 定义需要由 UI 层处理的一次性事件（如导航、弹窗等）
 */
sealed interface LoginUiEffect {
    // 登录成功，通知上层导航到主页
    data object LoggedIn : LoginUiEffect

    // 跳转到注册页面
    data object NavigateToRegister : LoginUiEffect
}
