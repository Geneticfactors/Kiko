package com.xu.kiko.ui.screen.tasks

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 任务页面路由组件
 * 负责创建 ViewModel、收集状态、处理副作用并组装任务页面
 */
@Composable
fun TasksRoute(
    // 当前登录用户的 ID
    currentUserId: String,

    modifier: Modifier = Modifier,

    // 注入的 ViewModel，默认通过工厂创建
    viewModel: TasksViewModel = viewModel(
        factory = TasksViewModelFactory.fromContext(
            context = LocalContext.current,
            currentUserId = currentUserId
        )
    )
) {
    // 收集任务页面 UI 状态
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Snackbar 宿主状态，用于显示提示消息
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    // 监听一次性副作用并处理消息提示
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TasksUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 组装任务页面
    TasksScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )
}
