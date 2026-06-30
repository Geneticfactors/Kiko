package com.xu.kiko.ui.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * 登录页面路由组件
 * 负责创建 ViewModel、收集状态、处理副作用并组装登录页面
 */
@Composable
fun LoginRoute(
    // 跳转注册页面的回调
    onOpenRegister: () -> Unit,

    // 登录成功后的回调
    onLoggedIn: () -> Unit,

    modifier: Modifier = Modifier,

    // 注入的 ViewModel，默认通过工厂创建
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory.fromContext(LocalContext.current)
    )
) {
    // 收集登录页面 UI 状态
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // 监听一次性副作用并处理导航事件
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginUiEffect.NavigateToRegister -> onOpenRegister()
                LoginUiEffect.LoggedIn -> onLoggedIn()
            }
        }
    }

    // 组装登录页面
    LoginScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
