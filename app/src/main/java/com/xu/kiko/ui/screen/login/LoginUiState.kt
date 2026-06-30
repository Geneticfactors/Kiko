package com.xu.kiko.ui.screen.login

import com.xu.kiko.ui.screen.auth.AuthFieldError
import com.xu.kiko.domain.repository.AuthError

/**
 * 登录页面 UI 状态
 * 包含登录表单的所有显示数据和状态标志
 */
data class LoginUiState(
    // 手机号输入内容
    val phone: String = "",

    // 密码输入内容
    val password: String = "",

    // 手机号校验错误
    val phoneError: AuthFieldError? = null,

    // 密码校验错误
    val passwordError: AuthFieldError? = null,

    // 登录认证错误（如账号不存在、密码错误）
    val authError: AuthError? = null,

    // 密码是否可见
    val isPasswordVisible: Boolean = false,

    // 是否正在提交登录请求
    val isSubmitting: Boolean = false,

    // 忘记密码对话框是否显示
    val isForgotPasswordDialogVisible: Boolean = false
)
