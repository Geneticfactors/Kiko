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

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> =
        _uiState.asStateFlow()

    private val _effects =
        Channel<RegisterUiEffect>(Channel.BUFFERED)
    val effects: Flow<RegisterUiEffect> =
        _effects.receiveAsFlow()

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
                        confirmPasswordError =
                            if (
                                state.confirmPasswordError ==
                                AuthFieldError.PASSWORD_MISMATCH
                            ) {
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
                    state.copy(
                        isPasswordVisible =
                            !state.isPasswordVisible
                    )
                }
            }

            RegisterUiAction.ToggleConfirmPasswordVisibility -> {
                _uiState.update { state ->
                    state.copy(
                        isConfirmPasswordVisible =
                            !state.isConfirmPasswordVisible
                    )
                }
            }

            RegisterUiAction.Submit -> submit()

            RegisterUiAction.NavigateBack -> {
                _effects.trySend(
                    RegisterUiEffect.NavigateBack
                )
            }
        }
    }

    private fun submit() {
        val state = _uiState.value

        val nicknameError =
            validateNickname(state.nickname.trim())
        val phoneError = validatePhone(state.phone)
        val passwordError = validatePassword(state.password)
        val confirmPasswordError =
            validatePasswordConfirmation(
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

        if (
            nicknameError != null ||
            phoneError != null ||
            passwordError != null ||
            confirmPasswordError != null
        ) {
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

    fun setSubmitting(value: Boolean) {
        _uiState.update { state ->
            state.copy(isSubmitting = value)
        }
    }
}
