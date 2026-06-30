package com.xu.kiko.ui.screen.notification

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xu.kiko.notification.NotificationPermissionHelper

/**
 * 通知设置页面路由组件
 * 负责创建 ViewModel、收集状态、处理权限请求，并组装通知设置页面
 */
@Composable
fun NotificationSettingsRoute(
    // 返回导航回调
    onNavigateBack: () -> Unit,

    modifier: Modifier = Modifier,

    // 注入的 ViewModel，默认通过工厂创建
    viewModel: NotificationSettingsViewModel = viewModel(
        factory = NotificationSettingsViewModelFactory(
            LocalContext.current
        )
    )
) {
    val context = LocalContext.current

    // 收集通知设置页面 UI 状态
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // 通知权限请求启动器
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onAction(
            NotificationSettingsUiAction.SetSystemNotificationsEnabled(
                granted
            )
        )
    }

    // 初始化时检查系统通知权限状态
    LaunchedEffect(context) {
        viewModel.onAction(
            NotificationSettingsUiAction.SetSystemNotificationsEnabled(
                NotificationPermissionHelper
                    .areNotificationsEnabled(context)
            )
        )
    }

    // 返回键处理
    BackHandler(onBack = onNavigateBack)

    // 组装通知设置页面
    NotificationSettingsScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        onRequestPermission = {
            val permission = NotificationPermissionHelper.runtimePermission
            if (permission != null) {
                permissionLauncher.launch(permission)
            }
        },
        onOpenSystemSettings = {
            context.startActivity(
                NotificationPermissionHelper
                    .appNotificationSettingsIntent(context)
            )
        },
        modifier = modifier
    )
}
