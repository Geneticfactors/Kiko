package com.xu.kiko.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xu.kiko.domain.repository.AuthError
import com.xu.kiko.domain.repository.AuthRepository
import com.xu.kiko.domain.repository.AuthResult
import com.xu.kiko.ui.screen.auth.normalizePhone
import com.xu.kiko.ui.screen.auth.validatePassword
import com.xu.kiko.ui.screen.auth.validatePhone
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 登录页面 ViewModel
 * 负责处理登录页面的状态管理和业务逻辑
 */
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 登录页面 UI 状态流
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // 一次性副作用通道，用于发送导航等一次性事件
    private val _effects = Channel<LoginUiEffect>(Channel.BUFFERED)
    val effect: Flow<LoginUiEffect> = _effects.receiveAsFlow()

    /**
     * 处理用户操作
     * 根据不同的 [LoginUiAction] 更新状态或触发副作用
     */
    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.PhoneChanged -> {
                _uiState.update { state ->
                    state.copy(
                        phone = action.value,
                        phoneError = null,
                        authError = null
                    )
                }
            }

            is LoginUiAction.PasswordChanged -> {
                _uiState.update { state ->
                    state.copy(
                        password = action.value,
                        passwordError = null,
                        authError = null
                    )
                }
            }

            LoginUiAction.TogglePasswordVisibility -> {
                _uiState.update { state ->
                    state.copy(isPasswordVisible = !state.isPasswordVisible)
                }
            }

            LoginUiAction.Submit -> submit()

            LoginUiAction.OpenForgotPassword -> {
                _uiState.update { state ->
                    state.copy(isForgotPasswordDialogVisible = true)
                }
            }

            LoginUiAction.CloseForgotPassword -> {
                _uiState.update { state ->
                    state.copy(isForgotPasswordDialogVisible = false)
                }
            }

            LoginUiAction.OpenRegister -> {
                _effects.trySend(LoginUiEffect.NavigateToRegister)
            }
        }
    }

    /**
     * 提交登录请求
     * 1. 校验手机号和密码
     * 2. 校验通过后调用 [authRepository.login] 进行登录
     * 3. 根据登录结果更新状态或发送副作用
     */
    private fun submit() {
        val state = _uiState.value
        val phoneError = validatePhone(state.phone)
        val passwordError = validatePassword(state.password)

        _uiState.update {
            it.copy(
                phoneError = phoneError,
                passwordError = passwordError
            )
        }

        if (phoneError != null || passwordError != null) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSubmitting = true,
                    authError = null
                )
            }

            when (
                val result = authRepository.login(
                    phone = normalizePhone(state.phone),
                    password = state.password
                )
            ) {
                AuthResult.Success -> {
                    _effects.trySend(LoginUiEffect.LoggedIn)
                }

                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            authError = result.error.toLoginError(),
                            isSubmitting = false
                        )
                    }
                }
            }
        }
    }

    /**
     * 设置提交状态
     * 供外部调用以控制加载状态
     */
    fun setSubmitting(value: Boolean) {
        _uiState.update { state ->
            state.copy(isSubmitting = value)
        }
    }

    /**
     * 将通用认证错误转换为登录页面专用错误
     * 过滤掉与登录无关的错误类型（如手机号已注册）
     */
    private fun AuthError.toLoginError(): AuthError {
        return when (this) {
            AuthError.PHONE_ALREADY_REGISTERED -> AuthError.UNKNOWN
            AuthError.INVALID_CREDENTIALS -> AuthError.INVALID_CREDENTIALS
            AuthError.UNKNOWN -> AuthError.UNKNOWN
        }
    }
}
