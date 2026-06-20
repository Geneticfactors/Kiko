package com.xu.kiko.ui.screen.register

import com.xu.kiko.ui.screen.auth.AuthFieldError
import com.xu.kiko.domain.repository.AuthError

data class RegisterUiState(
    val nickname: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nicknameError: AuthFieldError? = null,
    val phoneError: AuthFieldError? = null,
    val passwordError: AuthFieldError? = null,
    val confirmPasswordError: AuthFieldError? = null,
    val authError: AuthError? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isSubmitting: Boolean = false
)
