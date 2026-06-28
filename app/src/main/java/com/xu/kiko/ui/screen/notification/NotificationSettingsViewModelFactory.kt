package com.xu.kiko.ui.screen.notification

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies

class NotificationSettingsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (
            modelClass.isAssignableFrom(
                NotificationSettingsViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return NotificationSettingsViewModel(
                preferencesStore =
                    AppDependencies.notificationPreferencesStore(context),
                dailyTaskReminderScheduler =
                    AppDependencies.dailyTaskReminderScheduler(context)
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
