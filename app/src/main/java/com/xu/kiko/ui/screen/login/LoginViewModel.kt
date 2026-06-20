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

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _effects = Channel<LoginUiEffect>(Channel.BUFFERED)
    val effect: Flow<LoginUiEffect> = _effects.receiveAsFlow()

    fun onAction(action: LoginUiAction) {
        when(action) {
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
                    state.copy(
                        isPasswordVisible = !state.isPasswordVisible
                    )
                }
            }

            LoginUiAction.Submit -> submit()

            LoginUiAction.OpenForgotPassword -> {
                _uiState.update { state ->
                    state.copy(
                        isForgotPasswordDialogVisible = true
                    )
                }
            }

            LoginUiAction.CloseForgotPassword ->{
                _uiState.update { state ->
                    state.copy(
                        isForgotPasswordDialogVisible = false
                    )
                }
            }

            LoginUiAction.OpenRegister -> {
                _effects.trySend(
                    LoginUiEffect.NavigateToRegister
                )
            }
        }
    }
    private fun submit(){
        val state = _uiState.value
        val phoneError = validatePhone(state.phone)
        val passwordError = validatePassword(state.password)

        _uiState.update {
            it.copy(
                phoneError = phoneError,
                passwordError = passwordError
            )
        }
        if (phoneError != null || passwordError != null){
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

    fun setSubmitting(value: Boolean){
        _uiState.update { state ->
            state.copy(isSubmitting = value)
        }
    }

    private fun AuthError.toLoginError(): AuthError {
        return when (this) {
            AuthError.PHONE_ALREADY_REGISTERED ->
                AuthError.UNKNOWN

            AuthError.INVALID_CREDENTIALS ->
                AuthError.INVALID_CREDENTIALS

            AuthError.UNKNOWN ->
                AuthError.UNKNOWN
        }
    }
}
