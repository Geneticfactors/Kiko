package com.xu.kiko.data.notification

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.notificationDataStore by preferencesDataStore(
    name = "kiko_notifications"
)

class NotificationPreferencesStore(
    context: Context
) {
    private val dataStore =
        context.applicationContext.notificationDataStore

    fun observeSettings(): Flow<NotificationSettings> {
        return dataStore.data.map { preferences ->
            NotificationSettings(
                notificationsEnabled =
                    preferences[NOTIFICATIONS_ENABLED] ?: true,
                focusTimerEnabled =
                    preferences[FOCUS_TIMER_ENABLED] ?: true,
                focusCompletedEnabled =
                    preferences[FOCUS_COMPLETED_ENABLED] ?: true,
                breakReminderEnabled =
                    preferences[BREAK_REMINDER_ENABLED] ?: true,
                dailyTaskReminderEnabled =
                    preferences[DAILY_TASK_REMINDER_ENABLED] ?: false,
                dailyTaskReminderHour =
                    (preferences[DAILY_TASK_REMINDER_HOUR] ?: 9)
                        .coerceIn(0, 23),
                dailyTaskReminderMinute =
                    (preferences[DAILY_TASK_REMINDER_MINUTE] ?: 0)
                        .coerceIn(0, 59)
            )
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setFocusTimerEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[FOCUS_TIMER_ENABLED] = enabled
        }
    }

    suspend fun setFocusCompletedEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[FOCUS_COMPLETED_ENABLED] = enabled
        }
    }

    suspend fun setBreakReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BREAK_REMINDER_ENABLED] = enabled
        }
    }

    suspend fun setDailyTaskReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DAILY_TASK_REMINDER_ENABLED] = enabled
        }
    }

    suspend fun setDailyTaskReminderTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[DAILY_TASK_REMINDER_HOUR] = hour.coerceIn(0, 23)
            preferences[DAILY_TASK_REMINDER_MINUTE] =
                minute.coerceIn(0, 59)
        }
    }

    private companion object {
        val NOTIFICATIONS_ENABLED =
            booleanPreferencesKey("notifications_enabled")
        val FOCUS_TIMER_ENABLED =
            booleanPreferencesKey("focus_timer_enabled")
        val FOCUS_COMPLETED_ENABLED =
            booleanPreferencesKey("focus_completed_enabled")
        val BREAK_REMINDER_ENABLED =
            booleanPreferencesKey("break_reminder_enabled")
        val DAILY_TASK_REMINDER_ENABLED =
            booleanPreferencesKey("daily_task_reminder_enabled")
        val DAILY_TASK_REMINDER_HOUR =
            intPreferencesKey("daily_task_reminder_hour")
        val DAILY_TASK_REMINDER_MINUTE =
            intPreferencesKey("daily_task_reminder_minute")
    }
}
