package com.xu.kiko.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xu.kiko.data.AppDependencies
import kotlinx.coroutines.flow.first

class BreakReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val settings = AppDependencies
            .notificationPreferencesStore(applicationContext)
            .observeSettings()
            .first()

        if (
            settings.notificationsEnabled &&
            settings.breakReminderEnabled
        ) {
            AppDependencies.notificationController(applicationContext)
                .showBreakReminderNotification()
        }

        return Result.success()
    }

    companion object {
        const val UNIQUE_WORK_NAME = "break_reminder"
    }
}
