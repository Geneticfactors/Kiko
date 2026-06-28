package com.xu.kiko.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.xu.kiko.data.notification.NotificationSettings
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DailyTaskReminderScheduler(
    context: Context
) {
    private val appContext = context.applicationContext
    private val workManager = WorkManager.getInstance(appContext)

    fun sync(settings: NotificationSettings) {
        if (
            !settings.notificationsEnabled ||
            !settings.dailyTaskReminderEnabled
        ) {
            cancel()
            return
        }

        val request =
            PeriodicWorkRequestBuilder<DailyTaskReminderWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            )
                .setInitialDelay(
                    nextDelayMillis(
                        hour = settings.dailyTaskReminderHour,
                        minute = settings.dailyTaskReminderMinute
                    ),
                    TimeUnit.MILLISECONDS
                )
                .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancel() {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    private fun nextDelayMillis(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (!after(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return (target.timeInMillis - now.timeInMillis).coerceAtLeast(0L)
    }

    companion object {
        const val UNIQUE_WORK_NAME = "daily_task_reminder"
    }
}
