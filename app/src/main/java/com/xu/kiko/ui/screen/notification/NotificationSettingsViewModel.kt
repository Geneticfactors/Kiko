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

/**
 * 通知设置页面 ViewModel
 * 负责管理通知设置的状态和持久化
 */
class NotificationSettingsViewModel(
    // 通知偏好设置存储
    private val preferencesStore: NotificationPreferencesStore,

    // 每日任务提醒调度器
    private val dailyTaskReminderScheduler: DailyTaskReminderScheduler
) : ViewModel() {

    // 内部 UI 状态流
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())

    // 暴露给 UI 层的只读状态流
    val uiState: StateFlow<NotificationSettingsUiState> =
        _uiState.asStateFlow()

    /**
     * ViewModel 初始化
     * 开始监听通知设置变化
     */
    init {
        observeSettings()
    }

    /**
     * 处理用户操作
     * 根据 [NotificationSettingsUiAction] 分发到对应的处理方法
     *
     * @param action 用户操作意图
     */
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

    /**
     * 观察通知设置变化
     * 监听偏好设置中的通知配置变化并同步到 UI 和调度器
     */
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

    /**
     * 更新通知设置
     * 在协程中执行设置更新操作
     *
     * @param block 设置更新操作
     */
    private fun updateSettings(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    /**
     * 将 Domain [NotificationSettings] 转换为 UI 模型 [NotificationSettingsUiState]
     *
     * @param systemNotificationsEnabled 系统通知权限状态
     * @return UI 状态
     */
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
