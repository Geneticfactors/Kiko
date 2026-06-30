package com.xu.kiko.ui.screen.profile

/**
 * 个人中心页面副作用集合
 * 定义 ViewModel 需要通知 UI 层执行的一次性操作
 */
sealed interface ProfileUiEffect {

    // 导航到通知设置页面
    data object NavigateToNotificationSettings : ProfileUiEffect

    // 用户已退出登录
    data object LoggedOut : ProfileUiEffect
}
