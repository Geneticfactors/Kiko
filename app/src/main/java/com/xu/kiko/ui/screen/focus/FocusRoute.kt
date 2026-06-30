package com.xu.kiko.ui.screen.focus

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 专注页面路由组件
 * 负责创建 ViewModel、收集状态、处理副作用并组装专注页面
 */
@Composable
fun FocusRoute(
    // 当前登录用户的 ID
    currentUserId: String,

    modifier: Modifier = Modifier,

    // 跳转任务列表页面的回调
    onNavigateToTasks: () -> Unit = {},

    // 注入的 ViewModel，默认通过工厂创建
    viewModel: FocusViewModel = viewModel(
        factory = FocusViewModelFactory.fromContext(
            context = LocalContext.current,
            currentUserId = currentUserId
        )
    )
) {
    // 收集专注页面 UI 状态
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // 监听一次性副作用并处理导航事件
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                FocusUiEffect.NavigateToTasks -> onNavigateToTasks()
            }
        }
    }

    // 组装专注页面
    FocusScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}