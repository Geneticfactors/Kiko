package com.xu.kiko.ui.screen.profile

import com.xu.kiko.ui.theme.KikoThemeColor

sealed interface ProfileUiAction {
    data class SetDarkModeEnabled(
        val enabled: Boolean
    ) : ProfileUiAction

    data object OpenThemeColorDialog : ProfileUiAction

    data object CloseThemeColorDialog : ProfileUiAction

    data class SelectThemeColor(
        val color: KikoThemeColor
    ) : ProfileUiAction

    data class SelectAvatarImage(
        val uri: String
    ) : ProfileUiAction

    data object OpenNotificationSettings : ProfileUiAction

    data object RequestLogout : ProfileUiAction

    data object CancelLogout : ProfileUiAction

    data object ConfirmLogout : ProfileUiAction
}
