package com.xu.kiko.ui.screen.profile

sealed interface ProfileUiEffect {
    data object NavigateToNotificationSettings : ProfileUiEffect
    data object LoggedOut : ProfileUiEffect
}
