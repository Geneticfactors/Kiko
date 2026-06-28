package com.xu.kiko.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.xu.kiko.MainActivity
import com.xu.kiko.R

class KikoNotificationController(
    private val context: Context
) {
    private val appContext = context.applicationContext
    private val notificationManager =
        NotificationManagerCompat.from(appContext)

    fun showFocusCompletedNotification() {
        if (!NotificationPermissionHelper.areNotificationsEnabled(appContext)) {
            return
        }

        notificationManager.notify(
            FOCUS_COMPLETED_NOTIFICATION_ID,
            baseBuilder(KikoNotificationChannels.FOCUS_RESULT)
                .setContentTitle("专注完成")
                .setContentText("很好，这个番茄已经记录完成。")
                .setAutoCancel(true)
                .build()
        )
    }

    fun showBreakReminderNotification() {
        if (!NotificationPermissionHelper.areNotificationsEnabled(appContext)) {
            return
        }

        notificationManager.notify(
            BREAK_REMINDER_NOTIFICATION_ID,
            baseBuilder(KikoNotificationChannels.FOCUS_RESULT)
                .setContentTitle("休息结束")
                .setContentText("可以回到下一轮专注了。")
                .setAutoCancel(true)
                .build()
        )
    }

    fun showDailyTaskReminderNotification(
        remainingTaskCount: Int
    ) {
        if (!NotificationPermissionHelper.areNotificationsEnabled(appContext)) {
            return
        }

        val content = if (remainingTaskCount > 0) {
            "今天还有 $remainingTaskCount 个任务可以推进。"
        } else {
            "今天还没有待办任务，先规划一个小目标吧。"
        }

        notificationManager.notify(
            DAILY_TASK_NOTIFICATION_ID,
            baseBuilder(KikoNotificationChannels.DAILY_TASK)
                .setContentTitle("今日任务提醒")
                .setContentText(content)
                .setAutoCancel(true)
                .build()
        )
    }

    fun cancelFocusTimerNotification() {
        notificationManager.cancel(FOCUS_TIMER_NOTIFICATION_ID)
    }

    fun cancelAllKikoNotifications() {
        notificationManager.cancel(FOCUS_TIMER_NOTIFICATION_ID)
        notificationManager.cancel(FOCUS_COMPLETED_NOTIFICATION_ID)
        notificationManager.cancel(BREAK_REMINDER_NOTIFICATION_ID)
        notificationManager.cancel(DAILY_TASK_NOTIFICATION_ID)
    }

    fun focusTimerNotification(
        title: String,
        content: String,
        isRunning: Boolean
    ) = baseBuilder(KikoNotificationChannels.FOCUS_TIMER)
        .setContentTitle(title)
        .setContentText(content)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setShowWhen(false)
        .addAction(
            R.drawable.ic_timer,
            if (isRunning) "暂停" else "继续",
            servicePendingIntent(
                if (isRunning) {
                    FocusTimerForegroundService.ACTION_PAUSE
                } else {
                    FocusTimerForegroundService.ACTION_RESUME
                },
                if (isRunning) REQUEST_PAUSE else REQUEST_RESUME
            )
        )
        .addAction(
            R.drawable.ic_timer,
            "结束",
            servicePendingIntent(
                FocusTimerForegroundService.ACTION_STOP,
                REQUEST_STOP
            )
        )
        .build()

    private fun baseBuilder(channelId: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(openAppPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }

    private fun openAppPendingIntent(): PendingIntent {
        val intent = Intent(appContext, MainActivity::class.java)
        return PendingIntent.getActivity(
            appContext,
            REQUEST_OPEN_APP,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun servicePendingIntent(
        action: String,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(
            appContext,
            FocusTimerForegroundService::class.java
        ).setAction(action)
        return PendingIntent.getService(
            appContext,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val FOCUS_TIMER_NOTIFICATION_ID = 1001
        private const val FOCUS_COMPLETED_NOTIFICATION_ID = 1002
        private const val BREAK_REMINDER_NOTIFICATION_ID = 1003
        private const val DAILY_TASK_NOTIFICATION_ID = 1004
        private const val REQUEST_OPEN_APP = 2001
        private const val REQUEST_PAUSE = 2002
        private const val REQUEST_RESUME = 2003
        private const val REQUEST_STOP = 2004
    }
}
