package com.xu.kiko.ui.screen.login

sealed interface LoginUiEffect {
    data object LoggedIn : LoginUiEffect
    data object NavigateToRegister : LoginUiEffect
}
