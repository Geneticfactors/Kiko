package com.xu.kiko.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.ui.component.KikoBottomNavDestination
import com.xu.kiko.ui.component.KikoBottomNavigationBar
import com.xu.kiko.ui.screen.focus.FocusRoute
import com.xu.kiko.ui.screen.notification.NotificationSettingsRoute
import com.xu.kiko.ui.screen.profile.ProfileRoute
import com.xu.kiko.ui.screen.statistics.StatisticsRoute
import com.xu.kiko.ui.screen.tasks.TasksRoute
import com.xu.kiko.ui.theme.KikoTheme

/**
 * 主应用路由组件
 * 作为登录后的主界面容器，管理底部导航和四个主要页面的切换
 */
@Composable
fun MainRoute(
    // 当前登录用户的 ID
    currentUserId: String,

    // 退出登录的回调
    onLoggedOut: () -> Unit,

    modifier: Modifier = Modifier
) {
    // 当前选中的底部导航目标，使用 [rememberSaveable] 保存状态以支持配置变更
    var selectedDestination by rememberSaveable {
        mutableStateOf(KikoBottomNavDestination.HOME)
    }

    // 是否显示通知设置页面
    var showNotificationSettings by rememberSaveable {
        mutableStateOf(false)
    }

    // 如果显示通知设置页面，优先渲染通知设置路由（覆盖主内容区）
    if (showNotificationSettings) {
        NotificationSettingsRoute(
            onNavigateBack = { showNotificationSettings = false },
            modifier = modifier.fillMaxSize()
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // 底部导航栏
            KikoBottomNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = { destination ->
                    selectedDestination = destination
                }
            )
        }
    ) { innerPadding ->
        // 根据选中的导航目标渲染对应页面
        when (selectedDestination) {
            // 首页 - 专注计时页面
            KikoBottomNavDestination.HOME -> {
                FocusRoute(
                    currentUserId = currentUserId,
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    onNavigateToTasks = {
                        selectedDestination = KikoBottomNavDestination.TASKS
                    }
                )
            }

            // 任务页面
            KikoBottomNavDestination.TASKS -> {
                TasksRoute(
                    currentUserId = currentUserId,
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                )
            }

            // 统计页面
            KikoBottomNavDestination.STATISTICS -> {
                StatisticsRoute(
                    currentUserId = currentUserId,
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                )
            }

            // 个人中心页面
            KikoBottomNavDestination.PROFILE -> {
                ProfileRoute(
                    currentUserId = currentUserId,
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    onNavigateToNotificationSettings = {
                        showNotificationSettings = true
                    },
                    onLoggedOut = onLoggedOut
                )
            }
        }
    }
}

/**
 * 页面占位内容组件
 * 用于预览时显示导航目标的标签名称
 */
@Composable
private fun MainPlaceholderContent(
    // 导航目标
    destination: KikoBottomNavDestination,

    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(destination.labelRes),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(
    name = "Main - Light",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun MainRouteLightPreview() {
    KikoTheme {
        MainRoute(
            currentUserId = "preview_user",
            onLoggedOut = {}
        )
    }
}

@Preview(
    name = "Main - Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun MainRouteDarkPreview() {
    KikoTheme(darkTheme = true) {
        MainRoute(
            currentUserId = "preview_user",
            onLoggedOut = {}
        )
    }
}
