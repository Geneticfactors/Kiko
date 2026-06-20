package com.xu.kiko.ui.screen.register

sealed interface RegisterUiAction {
    data class NicknameChanged(val value: String) : RegisterUiAction
    data class PhoneChanged(val value: String) : RegisterUiAction
    data class PasswordChanged(val value: String) : RegisterUiAction
    data class ConfirmPasswordChanged(
        val value: String
    ) : RegisterUiAction

    data object TogglePasswordVisibility : RegisterUiAction
    data object ToggleConfirmPasswordVisibility : RegisterUiAction
    data object Submit : RegisterUiAction
    data object NavigateBack : RegisterUiAction
}
