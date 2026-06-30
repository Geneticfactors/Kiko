package com.xu.kiko.ui.screen.notification

/**
 * 通知设置页面 UI 状态
 * 管理各类通知的开关状态和提醒时间
 */
data class NotificationSettingsUiState(
    // 通知总开关
    val notificationsEnabled: Boolean = true,

    // 专注计时器通知开关
    val focusTimerEnabled: Boolean = true,

    // 专注完成通知开关
    val focusCompletedEnabled: Boolean = true,

    // 休息提醒通知开关
    val breakReminderEnabled: Boolean = true,

    // 每日任务提醒通知开关
    val dailyTaskReminderEnabled: Boolean = false,

    // 每日任务提醒时间 - 小时
    val dailyTaskReminderHour: Int = 9,

    // 每日任务提醒时间 - 分钟
    val dailyTaskReminderMinute: Int = 0,

    // 系统通知权限开关
    val systemNotificationsEnabled: Boolean = true
) {
    // 格式化后的每日任务提醒时间文本（HH:mm）
    val dailyTaskReminderTimeText: String
        get() = "%02d:%02d".format(
            dailyTaskReminderHour,
            dailyTaskReminderMinute
        )
}
