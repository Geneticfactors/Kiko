package com.xu.kiko.ui.screen.profile

import com.xu.kiko.ui.theme.KikoThemeColor

/**
 * 个人中心页面用户操作集合
 * 定义用户在个人中心页面可执行的所有操作
 */
sealed interface ProfileUiAction {

    /**
     * 设置深色模式开关状态
     *
     * @param enabled 是否开启深色模式
     */
    data class SetDarkModeEnabled(
        val enabled: Boolean
    ) : ProfileUiAction

    // 打开主题颜色选择对话框
    data object OpenThemeColorDialog : ProfileUiAction

    // 关闭主题颜色选择对话框
    data object CloseThemeColorDialog : ProfileUiAction

    /**
     * 选择主题颜色
     *
     * @param color 选中的主题颜色
     */
    data class SelectThemeColor(
        val color: KikoThemeColor
    ) : ProfileUiAction

    /**
     * 选择头像图片
     *
     * @param uri 头像图片的 URI
     */
    data class SelectAvatarImage(
        val uri: String
    ) : ProfileUiAction

    // 打开通知设置页面
    data object OpenNotificationSettings : ProfileUiAction

    // 请求退出登录（显示确认对话框）
    data object RequestLogout : ProfileUiAction

    // 取消退出登录
    data object CancelLogout : ProfileUiAction

    // 确认退出登录
    data object ConfirmLogout : ProfileUiAction
}
