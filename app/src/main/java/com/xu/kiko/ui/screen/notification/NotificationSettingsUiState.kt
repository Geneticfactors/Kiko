package com.xu.kiko.ui.screen.notification

data class NotificationSettingsUiState(
    val notificationsEnabled: Boolean = true,
    val focusTimerEnabled: Boolean = true,
    val focusCompletedEnabled: Boolean = true,
    val breakReminderEnabled: Boolean = true,
    val dailyTaskReminderEnabled: Boolean = false,
    val dailyTaskReminderHour: Int = 9,
    val dailyTaskReminderMinute: Int = 0,
    val systemNotificationsEnabled: Boolean = true
) {
    val dailyTaskReminderTimeText: String
        get() = "%02d:%02d".format(
            dailyTaskReminderHour,
            dailyTaskReminderMinute
        )
}
