package com.xu.kiko.notification

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.xu.kiko.data.notification.NotificationPreferencesStore
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

class FocusNotificationCoordinator(
    private val context: Context,
    private val currentUserId: String,
    private val preferencesStore: NotificationPreferencesStore,
    private val notificationController: KikoNotificationController
) {
    private val appContext = context.applicationContext
    private val workManager = WorkManager.getInstance(appContext)

    suspend fun onTimerStarted() {
        val settings = preferencesStore.observeSettings().first()
        if (
            !settings.notificationsEnabled ||
            !settings.focusTimerEnabled ||
            !NotificationPermissionHelper.areNotificationsEnabled(appContext)
        ) {
            return
        }

        val intent = Intent(
            appContext,
            FocusTimerForegroundService::class.java
        ).apply {
            action = FocusTimerForegroundService.ACTION_START
            putExtra(
                FocusTimerForegroundService.EXTRA_USER_ID,
                currentUserId
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent)
        } else {
            appContext.startService(intent)
        }
    }

    suspend fun onTimerPausedOrResumed() {
        onTimerStarted()
    }

    fun onTimerCancelled() {
        cancelBreakReminder()
        stopFocusTimerService()
    }

    suspend fun onTimerCompleted() {
        stopFocusTimerService()
        val settings = preferencesStore.observeSettings().first()
        if (!settings.notificationsEnabled) {
            cancelBreakReminder()
            return
        }

        if (settings.focusCompletedEnabled) {
            notificationController.showFocusCompletedNotification()
        }

        if (settings.breakReminderEnabled) {
            scheduleBreakReminder()
        } else {
            cancelBreakReminder()
        }
    }

    fun onLoggedOut() {
        cancelBreakReminder()
        stopFocusTimerService()
        notificationController.cancelAllKikoNotifications()
    }

    private fun scheduleBreakReminder() {
        val request = OneTimeWorkRequestBuilder<BreakReminderWorker>()
            .setInitialDelay(BREAK_REMINDER_DELAY_MINUTES, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniqueWork(
            BreakReminderWorker.UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    private fun cancelBreakReminder() {
        workManager.cancelUniqueWork(BreakReminderWorker.UNIQUE_WORK_NAME)
    }

    private fun stopFocusTimerService() {
        appContext.startService(
            Intent(
                appContext,
                FocusTimerForegroundService::class.java
            ).setAction(FocusTimerForegroundService.ACTION_DISMISS)
        )
    }

    private companion object {
        const val BREAK_REMINDER_DELAY_MINUTES = 5L
    }
}
