package com.xu.kiko.data.notification

data class NotificationSettings(
    val notificationsEnabled: Boolean = true,
    val focusTimerEnabled: Boolean = true,
    val focusCompletedEnabled: Boolean = true,
    val breakReminderEnabled: Boolean = true,
    val dailyTaskReminderEnabled: Boolean = false,
    val dailyTaskReminderHour: Int = DEFAULT_DAILY_TASK_REMINDER_HOUR,
    val dailyTaskReminderMinute: Int = DEFAULT_DAILY_TASK_REMINDER_MINUTE
) {
    val dailyTaskReminderMinutesOfDay: Int
        get() = dailyTaskReminderHour * MINUTES_PER_HOUR +
            dailyTaskReminderMinute

    companion object {
        const val DEFAULT_DAILY_TASK_REMINDER_HOUR = 9
        const val DEFAULT_DAILY_TASK_REMINDER_MINUTE = 0
        private const val MINUTES_PER_HOUR = 60
    }
}
