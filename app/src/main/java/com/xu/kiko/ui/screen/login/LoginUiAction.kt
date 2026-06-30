package com.xu.kiko.ui.screen.login

/**
 * 登录页面用户操作意图
 * 定义登录页面所有可能的用户交互动作
 */
sealed interface LoginUiAction {
    // 手机号输入框内容变更
    data class PhoneChanged(val value: String) : LoginUiAction

    // 密码输入框内容变更
    data class PasswordChanged(val value: String) : LoginUiAction

    // 切换密码可见性
    data object TogglePasswordVisibility : LoginUiAction

    // 提交登录请求
    data object Submit : LoginUiAction

    // 打开忘记密码对话框
    data object OpenForgotPassword : LoginUiAction

    // 关闭忘记密码对话框
    data object CloseForgotPassword : LoginUiAction

    // 跳转注册页面
    data object OpenRegister : LoginUiAction
}