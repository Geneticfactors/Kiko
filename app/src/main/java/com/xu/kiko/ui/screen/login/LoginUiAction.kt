package com.xu.kiko.ui.screen.login

sealed interface LoginUiAction {
    data class PhoneChanged(val value: String) :
            LoginUiAction
    data class PasswordChanged(val value: String) :
            LoginUiAction
    data object  TogglePasswordVisibility : LoginUiAction
    data object  Submit : LoginUiAction
    data object OpenForgotPassword : LoginUiAction
    data object CloseForgotPassword : LoginUiAction
    data object OpenRegister : LoginUiAction
}