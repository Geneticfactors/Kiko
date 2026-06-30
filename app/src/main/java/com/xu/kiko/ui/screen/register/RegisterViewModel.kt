package com.xu.kiko.ui.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xu.kiko.domain.repository.AuthRepository
import com.xu.kiko.domain.repository.AuthResult
import com.xu.kiko.ui.screen.auth.AuthFieldError
import com.xu.kiko.ui.screen.auth.normalizePhone
import com.xu.kiko.ui.screen.auth.validateNickname
import com.xu.kiko.ui.screen.auth.validatePassword
import com.xu.kiko.ui.screen.auth.validatePasswordConfirmation
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
 * 注册页面 ViewModel
 * 负责处理注册页面的状态管理和业务逻辑
 */
class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // 注册页面 UI 状态流
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // 一次性副作用通道，用于发送导航等一次性事件
    private val _effects = Channel<RegisterUiEffect>(Channel.BUFFERED)
    val effects: Flow<RegisterUiEffect> = _effects.receiveAsFlow()

    /**
     * 处理用户操作
     * 根据不同的 [RegisterUiAction] 更新状态或触发副作用
     */
    fun onAction(action: RegisterUiAction) {
        when (action) {
            is RegisterUiAction.NicknameChanged -> {
                _uiState.update { state ->
                    state.copy(
                        nickname = action.value,
                        nicknameError = null,
                        authError = null
                    )
                }
            }

            is RegisterUiAction.PhoneChanged -> {
                _uiState.update { state ->
                    state.copy(
                        phone = action.value,
                        phoneError = null,
                        authError = null
                    )
                }
            }

            is RegisterUiAction.PasswordChanged -> {
                _uiState.update { state ->
                    state.copy(
                        password = action.value,
                        passwordError = null,
                        authError = null,
                        // 密码变更时，如果之前是密码不匹配错误，则清除该错误
                        confirmPasswordError = if (state.confirmPasswordError == AuthFieldError.PASSWORD_MISMATCH) {
                            null
                        } else {
                            state.confirmPasswordError
                        }
                    )
                }
            }

            is RegisterUiAction.ConfirmPasswordChanged -> {
                _uiState.update { state ->
                    state.copy(
                        confirmPassword = action.value,
                        confirmPasswordError = null,
                        authError = null
                    )
                }
            }

            RegisterUiAction.TogglePasswordVisibility -> {
                _uiState.update { state ->
                    state.copy(isPasswordVisible = !state.isPasswordVisible)
                }
            }

            RegisterUiAction.ToggleConfirmPasswordVisibility -> {
                _uiState.update { state ->
                    state.copy(isConfirmPasswordVisible = !state.isConfirmPasswordVisible)
                }
            }

            RegisterUiAction.Submit -> submit()

            RegisterUiAction.NavigateBack -> {
                _effects.trySend(RegisterUiEffect.NavigateBack)
            }
        }
    }

    /**
     * 提交注册请求
     * 1. 校验昵称、手机号、密码、确认密码
     * 2. 校验通过后调用 [authRepository.register] 进行注册
     * 3. 根据注册结果更新状态或发送副作用
     */
    private fun submit() {
        val state = _uiState.value

        val nicknameError = validateNickname(state.nickname.trim())
        val phoneError = validatePhone(state.phone)
        val passwordError = validatePassword(state.password)
        val confirmPasswordError = validatePasswordConfirmation(
            password = state.password,
            confirmation = state.confirmPassword
        )

        _uiState.update {
            it.copy(
                nicknameError = nicknameError,
                phoneError = phoneError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }

        if (nicknameError != null || phoneError != null || passwordError != null || confirmPasswordError != null) {
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
                val result = authRepository.register(
                    nickname = state.nickname.trim(),
                    phone = normalizePhone(state.phone),
                    password = state.password
                )
            ) {
                AuthResult.Success -> {
                    _effects.trySend(RegisterUiEffect.Registered)
                }

                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            authError = result.error,
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
}
