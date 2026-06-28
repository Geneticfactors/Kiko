package com.xu.kiko.ui.screen.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xu.kiko.data.notification.NotificationPreferencesStore
import com.xu.kiko.data.notification.NotificationSettings
import com.xu.kiko.notification.DailyTaskReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationSettingsViewModel(
    private val preferencesStore: NotificationPreferencesStore,
    private val dailyTaskReminderScheduler: DailyTaskReminderScheduler
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> =
        _uiState.asStateFlow()

    init {
        observeSettings()
    }

    fun onAction(action: NotificationSettingsUiAction) {
        when (action) {
            is NotificationSettingsUiAction.SetNotificationsEnabled ->
                updateSettings {
                    preferencesStore.setNotificationsEnabled(action.enabled)
                }

            is NotificationSettingsUiAction.SetFocusTimerEnabled ->
                updateSettings {
                    preferencesStore.setFocusTimerEnabled(action.enabled)
                }

            is NotificationSettingsUiAction.SetFocusCompletedEnabled ->
                updateSettings {
                    preferencesStore.setFocusCompletedEnabled(action.enabled)
                }

            is NotificationSettingsUiAction.SetBreakReminderEnabled ->
                updateSettings {
                    preferencesStore.setBreakReminderEnabled(action.enabled)
                }

            is NotificationSettingsUiAction.SetDailyTaskReminderEnabled ->
                updateSettings {
                    preferencesStore.setDailyTaskReminderEnabled(
                        action.enabled
                    )
                }

            is NotificationSettingsUiAction.SetDailyTaskReminderTime ->
                updateSettings {
                    preferencesStore.setDailyTaskReminderTime(
                        hour = action.hour,
                        minute = action.minute
                    )
                }

            is NotificationSettingsUiAction.SetSystemNotificationsEnabled ->
                _uiState.update { state ->
                    state.copy(
                        systemNotificationsEnabled = action.enabled
                    )
                }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            preferencesStore.observeSettings()
                .catch { emit(NotificationSettings()) }
                .collect { settings ->
                    dailyTaskReminderScheduler.sync(settings)
                    _uiState.update { state ->
                        settings.toUiState(
                            systemNotificationsEnabled =
                                state.systemNotificationsEnabled
                        )
                    }
                }
        }
    }

    private fun updateSettings(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    private fun NotificationSettings.toUiState(
        systemNotificationsEnabled: Boolean
    ): NotificationSettingsUiState {
        return NotificationSettingsUiState(
            notificationsEnabled = notificationsEnabled,
            focusTimerEnabled = focusTimerEnabled,
            focusCompletedEnabled = focusCompletedEnabled,
            breakReminderEnabled = breakReminderEnabled,
            dailyTaskReminderEnabled = dailyTaskReminderEnabled,
            dailyTaskReminderHour = dailyTaskReminderHour,
            dailyTaskReminderMinute = dailyTaskReminderMinute,
            systemNotificationsEnabled = systemNotificationsEnabled
        )
    }
}
