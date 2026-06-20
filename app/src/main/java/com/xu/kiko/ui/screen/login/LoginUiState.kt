package com.xu.kiko.ui.screen.login

import com.xu.kiko.ui.screen.auth.AuthFieldError
import com.xu.kiko.domain.repository.AuthError

data class LoginUiState(
    val phone: String = "",
    val password: String = "",
    val phoneError: AuthFieldError? = null,
    val passwordError: AuthFieldError? = null,
    val authError: AuthError? = null,
    val isPasswordVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val isForgotPasswordDialogVisible: Boolean = false
)
