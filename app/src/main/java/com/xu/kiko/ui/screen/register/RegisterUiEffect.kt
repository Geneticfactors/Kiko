package com.xu.kiko.ui.screen.register

sealed interface RegisterUiEffect {
    data object Registered : RegisterUiEffect
    data object NavigateBack : RegisterUiEffect
}
