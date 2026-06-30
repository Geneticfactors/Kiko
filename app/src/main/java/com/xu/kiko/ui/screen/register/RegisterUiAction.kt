package com.xu.kiko.ui.screen.register

/**
 * 注册页面用户操作意图
 * 定义注册页面所有可能的用户交互动作
 */
sealed interface RegisterUiAction {
    // 昵称输入框内容变更
    data class NicknameChanged(val value: String) : RegisterUiAction

    // 手机号输入框内容变更
    data class PhoneChanged(val value: String) : RegisterUiAction

    // 密码输入框内容变更
    data class PasswordChanged(val value: String) : RegisterUiAction

    // 确认密码输入框内容变更
    data class ConfirmPasswordChanged(val value: String) : RegisterUiAction

    // 切换密码可见性
    data object TogglePasswordVisibility : RegisterUiAction

    // 切换确认密码可见性
    data object ToggleConfirmPasswordVisibility : RegisterUiAction

    // 提交注册请求
    data object Submit : RegisterUiAction

    // 返回登录页面
    data object NavigateBack : RegisterUiAction
}
