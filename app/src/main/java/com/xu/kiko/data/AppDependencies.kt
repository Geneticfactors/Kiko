package com.xu.kiko.data

import android.content.Context
import com.xu.kiko.data.local.database.KikoDatabase
import com.xu.kiko.data.profile.ProfilePreferencesStore
import com.xu.kiko.data.notification.NotificationPreferencesStore
import com.xu.kiko.data.repository.LocalAuthRepository
import com.xu.kiko.data.repository.OfflineFocusSessionRepository
import com.xu.kiko.data.repository.OfflineStatisticsRepository
import com.xu.kiko.data.repository.OfflineTaskRepository
import com.xu.kiko.data.session.SessionDataStore
import com.xu.kiko.data.session.SessionStore
import com.xu.kiko.data.theme.ThemePreferencesStore
import com.xu.kiko.domain.repository.AuthRepository
import com.xu.kiko.domain.repository.FocusSessionRepository
import com.xu.kiko.domain.repository.StatisticsRepository
import com.xu.kiko.domain.repository.TaskRepository
import com.xu.kiko.notification.DailyTaskReminderScheduler
import com.xu.kiko.notification.FocusNotificationCoordinator
import com.xu.kiko.notification.KikoNotificationController

object AppDependencies {
    fun authRepository(context: Context): AuthRepository {
        val appContext = context.applicationContext
        val database = KikoDatabase.getDatabase(appContext)
        return LocalAuthRepository(
            userDao = database.userDao(),
            sessionStore = SessionDataStore(appContext)
        )
    }

    fun taskRepository(
        context: Context,
        currentUserId: String
    ): TaskRepository {
        val database = KikoDatabase.getDatabase(context.applicationContext)
        return OfflineTaskRepository(
            taskDao = database.taskDao(),
            currentUserIdProvider = { currentUserId }
        )
    }

    fun focusSessionRepository(
        context: Context,
        currentUserId: String
    ): FocusSessionRepository {
        val database = KikoDatabase.getDatabase(context.applicationContext)
        return OfflineFocusSessionRepository(
            focusSessionDao = database.focusSessionDao(),
            currentUserIdProvider = { currentUserId }
        )
    }

    fun statisticsRepository(
        context: Context,
        currentUserId: String
    ): StatisticsRepository {
        val database = KikoDatabase.getDatabase(context.applicationContext)
        return OfflineStatisticsRepository(
            focusSessionDao = database.focusSessionDao(),
            taskDao = database.taskDao(),
            currentUserIdProvider = { currentUserId }
        )
    }

    fun profilePreferencesStore(context: Context): ProfilePreferencesStore {
        return ProfilePreferencesStore(context.applicationContext)
    }

    fun themePreferencesStore(context: Context): ThemePreferencesStore {
        return ThemePreferencesStore(context.applicationContext)
    }

    fun sessionStore(context: Context): SessionStore {
        return SessionDataStore(context.applicationContext)
    }

    fun notificationPreferencesStore(
        context: Context
    ): NotificationPreferencesStore {
        return NotificationPreferencesStore(context.applicationContext)
    }

    fun notificationController(context: Context): KikoNotificationController {
        return KikoNotificationController(context.applicationContext)
    }

    fun dailyTaskReminderScheduler(
        context: Context
    ): DailyTaskReminderScheduler {
        return DailyTaskReminderScheduler(context.applicationContext)
    }

    fun focusNotificationCoordinator(
        context: Context,
        currentUserId: String
    ): FocusNotificationCoordinator {
        val appContext = context.applicationContext
        return FocusNotificationCoordinator(
            context = appContext,
            currentUserId = currentUserId,
            preferencesStore = notificationPreferencesStore(appContext),
            notificationController = notificationController(appContext)
        )
    }
}
