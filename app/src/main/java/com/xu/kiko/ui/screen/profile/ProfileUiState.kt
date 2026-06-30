package com.xu.kiko.ui.screen.profile

import com.xu.kiko.ui.theme.KikoThemeColor

/**
 * 个人中心日历热力图单日数据模型
 * 用于展示用户每日的专注情况
 */
data class ProfileCalendarDayUiModel(
    // 日期唯一标识
    val id: String,

    // 日期时间戳（毫秒）
    val dateEpochMillis: Long,

    // 该日完成的番茄钟数量
    val pomodoroCount: Int,

    // 热力图强度（0-4），用于决定颜色深浅
    val intensity: Int,

    // 是否为未来日期
    val isFuture: Boolean
)

/**
 * 个人中心页面 UI 状态
 * 管理用户信息、设置选项和对话框状态
 */
data class ProfileUiState(
    // 用户昵称
    val nickname: String = "用户昵称",

    // 头像文本（昵称首字）
    val avatarText: String = "FF",

    // 头像图片路径
    val avatarImagePath: String? = null,

    // 注册天数
    val joinedDays: Int = 32,

    // 专注日历数据（热力图）
    val focusedDays: List<ProfileCalendarDayUiModel> = emptyList(),

    // 是否开启深色模式
    val darkModeEnabled: Boolean = false,

    // 当前选中的主题颜色
    val selectedThemeColor: KikoThemeColor = KikoThemeColor.BLUE,

    // 是否显示主题颜色选择对话框
    val showThemeColorDialog: Boolean = false,

    // 是否显示退出登录确认对话框
    val showLogoutConfirmDialog: Boolean = false,

    // 是否正在退出登录中
    val isLoggingOut: Boolean = false
)
