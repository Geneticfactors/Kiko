package com.xu.kiko.ui.screen.notification

sealed interface NotificationSettingsUiAction {
    data class SetNotificationsEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction

    data class SetFocusTimerEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction

    data class SetFocusCompletedEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction

    data class SetBreakReminderEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction

    data class SetDailyTaskReminderEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction

    data class SetDailyTaskReminderTime(
        val hour: Int,
        val minute: Int
    ) : NotificationSettingsUiAction

    data class SetSystemNotificationsEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction
}
