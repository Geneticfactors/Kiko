package com.xu.kiko.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 个人中心页面路由组件
 * 负责创建 ViewModel、收集状态和副作用，并组装个人中心页面
 */
@Composable
fun ProfileRoute(
    // 当前登录用户的 ID
    currentUserId: String,

    modifier: Modifier = Modifier,

    // 导航到通知设置页面的回调
    onNavigateToNotificationSettings: () -> Unit = {},

    // 用户退出登录后的回调
    onLoggedOut: () -> Unit = {},

    // 注入的 ViewModel，默认通过工厂创建
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory.fromContext(
            context = LocalContext.current,
            currentUserId = currentUserId
        )
    )
) {
    // 收集个人中心页面 UI 状态
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // 监听副作用并执行对应操作
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ProfileUiEffect.NavigateToNotificationSettings ->
                    onNavigateToNotificationSettings()

                ProfileUiEffect.LoggedOut ->
                    onLoggedOut()
            }
        }
    }

    // 组装个人中心页面
    ProfileScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

