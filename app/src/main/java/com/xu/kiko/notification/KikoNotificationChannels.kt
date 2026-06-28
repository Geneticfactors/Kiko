package com.xu.kiko.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object KikoNotificationChannels {
    const val FOCUS_TIMER = "focus_timer"
    const val FOCUS_RESULT = "focus_result"
    const val DAILY_TASK = "daily_task"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val manager = context.getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannels(
            listOf(
                NotificationChannel(
                    FOCUS_TIMER,
                    "专注计时",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "显示正在进行的专注计时"
                },
                NotificationChannel(
                    FOCUS_RESULT,
                    "专注结果",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "提醒专注完成和休息结束"
                },
                NotificationChannel(
                    DAILY_TASK,
                    "今日任务",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "提醒查看今天的任务"
                }
            )
        )
    }
}
