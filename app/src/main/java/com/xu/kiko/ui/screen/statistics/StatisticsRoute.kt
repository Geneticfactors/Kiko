package com.xu.kiko.ui.screen.statistics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 统计页面路由组件
 * 负责创建 ViewModel、收集状态并组装统计页面
 */
@Composable
fun StatisticsRoute(
    // 当前登录用户的 ID
    currentUserId: String,

    modifier: Modifier = Modifier,

    // 注入的 ViewModel，默认通过工厂创建
    viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory.fromContext(
            context = LocalContext.current,
            currentUserId = currentUserId
        )
    )
) {
    // 收集统计页面 UI 状态
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // 组装统计页面
    StatisticsScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

