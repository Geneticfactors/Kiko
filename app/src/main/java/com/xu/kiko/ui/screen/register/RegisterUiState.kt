package com.xu.kiko.ui.screen.register

import com.xu.kiko.ui.screen.auth.AuthFieldError
import com.xu.kiko.domain.repository.AuthError

/**
 * 注册页面 UI 状态
 * 包含注册表单的所有显示数据和状态标志
 */
data class RegisterUiState(
    // 昵称输入内容
    val nickname: String = "",

    // 手机号输入内容
    val phone: String = "",

    // 密码输入内容
    val password: String = "",

    // 确认密码输入内容
    val confirmPassword: String = "",

    // 昵称校验错误
    val nicknameError: AuthFieldError? = null,

    // 手机号校验错误
    val phoneError: AuthFieldError? = null,

    // 密码校验错误
    val passwordError: AuthFieldError? = null,

    // 确认密码校验错误
    val confirmPasswordError: AuthFieldError? = null,

    // 注册认证错误（如手机号已注册）
    val authError: AuthError? = null,

    // 密码是否可见
    val isPasswordVisible: Boolean = false,

    // 确认密码是否可见
    val isConfirmPasswordVisible: Boolean = false,

    // 是否正在提交注册请求
    val isSubmitting: Boolean = false
)
