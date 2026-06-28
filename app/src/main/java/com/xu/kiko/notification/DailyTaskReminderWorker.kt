package com.xu.kiko.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xu.kiko.data.AppDependencies
import kotlinx.coroutines.flow.first

class DailyTaskReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val settings = AppDependencies
            .notificationPreferencesStore(applicationContext)
            .observeSettings()
            .first()

        if (
            !settings.notificationsEnabled ||
            !settings.dailyTaskReminderEnabled ||
            !NotificationPermissionHelper
                .areNotificationsEnabled(applicationContext)
        ) {
            return Result.success()
        }

        val currentUserId = AppDependencies.sessionStore(applicationContext)
            .observeCurrentUserId()
            .first()
            ?: return Result.success()

        val tasks = AppDependencies.taskRepository(
            context = applicationContext,
            currentUserId = currentUserId
        ).getTodayTasks(limit = TODAY_TASK_LIMIT)

        val remainingCount = tasks.count { task -> !task.isCompleted }
        AppDependencies.notificationController(applicationContext)
            .showDailyTaskReminderNotification(remainingCount)

        return Result.success()
    }

    private companion object {
        const val TODAY_TASK_LIMIT = 20
    }
}
