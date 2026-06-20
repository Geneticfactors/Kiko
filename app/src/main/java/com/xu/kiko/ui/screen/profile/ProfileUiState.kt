package com.xu.kiko.ui.screen.profile

import com.xu.kiko.ui.theme.KikoThemeColor

data class ProfileCalendarDayUiModel(
    val id: String,
    val dateEpochMillis: Long,
    val pomodoroCount: Int,
    val intensity: Int,
    val isFuture: Boolean
)

data class ProfileUiState(
    val nickname: String = "用户昵称",
    val avatarText: String = "FF",
    val avatarImagePath: String? = null,
    val joinedDays: Int = 32,
    val focusedDays: List<ProfileCalendarDayUiModel> = emptyList(),
    val darkModeEnabled: Boolean = false,
    val selectedThemeColor: KikoThemeColor = KikoThemeColor.BLUE,
    val showThemeColorDialog: Boolean = false,
    val showLogoutConfirmDialog: Boolean = false,
    val isLoggingOut: Boolean = false
)
