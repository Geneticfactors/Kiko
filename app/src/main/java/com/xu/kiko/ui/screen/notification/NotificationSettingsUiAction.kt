package com.xu.kiko.ui.screen.notification

/**
 * 通知设置页面用户操作集合
 * 定义用户在通知设置页面可执行的所有操作
 */
sealed interface NotificationSettingsUiAction {

    /**
     * 设置通知总开关
     *
     * @param enabled 是否开启通知
     */
    data class SetNotificationsEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction

    /**
     * 设置专注计时器通知开关
     *
     * @param enabled 是否开启专注计时器通知
     */
    data class SetFocusTimerEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction

    /**
     * 设置专注完成通知开关
     *
     * @param enabled 是否开启专注完成通知
     */
    data class SetFocusCompletedEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction

    /**
     * 设置休息提醒通知开关
     *
     * @param enabled 是否开启休息提醒通知
     */
    data class SetBreakReminderEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction

    /**
     * 设置每日任务提醒通知开关
     *
     * @param enabled 是否开启每日任务提醒通知
     */
    data class SetDailyTaskReminderEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction

    /**
     * 设置每日任务提醒时间
     *
     * @param hour 小时（0-23）
     * @param minute 分钟（0-59）
     */
    data class SetDailyTaskReminderTime(
        val hour: Int,
        val minute: Int
    ) : NotificationSettingsUiAction

    /**
     * 设置系统通知权限开关
     *
     * @param enabled 是否开启系统通知权限
     */
    data class SetSystemNotificationsEnabled(
        val enabled: Boolean
    ) : NotificationSettingsUiAction
}
