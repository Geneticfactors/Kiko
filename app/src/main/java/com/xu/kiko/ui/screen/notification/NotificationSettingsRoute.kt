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

@Composable
fun NotificationSettingsRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationSettingsViewModel = viewModel(
        factory = NotificationSettingsViewModelFactory(
            LocalContext.current
        )
    )
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onAction(
            NotificationSettingsUiAction.SetSystemNotificationsEnabled(
                granted
            )
        )
    }

    LaunchedEffect(context) {
        viewModel.onAction(
            NotificationSettingsUiAction.SetSystemNotificationsEnabled(
                NotificationPermissionHelper
                    .areNotificationsEnabled(context)
            )
        )
    }

    BackHandler(onBack = onNavigateBack)

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
