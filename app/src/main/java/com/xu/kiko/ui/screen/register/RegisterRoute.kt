package com.xu.kiko.ui.screen.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 注册页面路由组件
 * 负责创建 ViewModel、收集状态、处理副作用并组装注册页面
 */
@Composable
fun RegisterRoute(
    // 返回登录页面的回调
    onNavigateBack: () -> Unit,

    // 注册成功后的回调
    onRegistered: () -> Unit,

    modifier: Modifier = Modifier,

    // 注入的 ViewModel，默认通过工厂创建
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory.fromContext(LocalContext.current)
    )
) {
    // 收集注册页面 UI 状态
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // 监听一次性副作用并处理导航事件
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                RegisterUiEffect.NavigateBack -> onNavigateBack()
                RegisterUiEffect.Registered -> onRegistered()
            }
        }
    }

    // 组装注册页面
    RegisterScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
