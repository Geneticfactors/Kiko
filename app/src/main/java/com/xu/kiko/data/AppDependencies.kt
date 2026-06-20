package com.xu.kiko.data

import android.content.Context
import com.xu.kiko.data.local.database.KikoDatabase
import com.xu.kiko.data.profile.ProfilePreferencesStore
import com.xu.kiko.data.repository.LocalAuthRepository
import com.xu.kiko.data.repository.OfflineFocusSessionRepository
import com.xu.kiko.data.repository.OfflineStatisticsRepository
import com.xu.kiko.data.repository.OfflineTaskRepository
import com.xu.kiko.data.session.SessionDataStore
import com.xu.kiko.domain.repository.AuthRepository
import com.xu.kiko.domain.repository.FocusSessionRepository
import com.xu.kiko.domain.repository.StatisticsRepository
import com.xu.kiko.domain.repository.TaskRepository

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
}
