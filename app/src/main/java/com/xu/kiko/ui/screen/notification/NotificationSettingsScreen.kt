package com.xu.kiko.ui.screen.notification

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.ui.component.KikoCard
import com.xu.kiko.ui.component.PrimaryButton
import com.xu.kiko.ui.component.SecondaryButton
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

/**
 * 通知设置页面主组件
 * 展示各类通知开关、权限状态和时间选择器
 */
@Composable
fun NotificationSettingsScreen(
    // 通知设置页面 UI 状态
    state: NotificationSettingsUiState,

    // 用户操作回调
    onAction: (NotificationSettingsUiAction) -> Unit,

    // 返回导航回调
    onNavigateBack: () -> Unit,

    // 请求通知权限回调
    onRequestPermission: () -> Unit,

    // 打开系统设置回调
    onOpenSystemSettings: () -> Unit,

    modifier: Modifier = Modifier
) {
    // 时间选择对话框显示状态
    var showTimeDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.extraLarge)
            .padding(bottom = MaterialTheme.spacing.section),
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.large
        )
    ) {
        // 顶部导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(
                        R.string.notification_back
                    )
                )
            }
            Text(
                text = stringResource(R.string.notification_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // 通知权限卡片
        PermissionCard(
            systemNotificationsEnabled =
                state.systemNotificationsEnabled,
            onRequestPermission = onRequestPermission,
            onOpenSystemSettings = onOpenSystemSettings
        )

        // 通知开关卡片
        KikoCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = MaterialTheme.spacing.large,
                vertical = MaterialTheme.spacing.small
            )
        ) {
            // 通知总开关
            NotificationSwitchRow(
                title = stringResource(
                    R.string.notification_master_switch
                ),
                checked = state.notificationsEnabled,
                onCheckedChange = { enabled ->
                    onAction(
                        NotificationSettingsUiAction
                            .SetNotificationsEnabled(enabled)
                    )
                }
            )

            // 专注计时器通知
            NotificationSwitchRow(
                title = stringResource(
                    R.string.notification_focus_timer
                ),
                checked = state.focusTimerEnabled,
                enabled = state.notificationsEnabled,
                onCheckedChange = { enabled ->
                    onAction(
                        NotificationSettingsUiAction
                            .SetFocusTimerEnabled(enabled)
                    )
                }
            )

            // 专注完成通知
            NotificationSwitchRow(
                title = stringResource(
                    R.string.notification_focus_completed
                ),
                checked = state.focusCompletedEnabled,
                enabled = state.notificationsEnabled,
                onCheckedChange = { enabled ->
                    onAction(
                        NotificationSettingsUiAction
                            .SetFocusCompletedEnabled(enabled)
                    )
                }
            )

            // 休息提醒通知
            NotificationSwitchRow(
                title = stringResource(
                    R.string.notification_break_reminder
                ),
                checked = state.breakReminderEnabled,
                enabled = state.notificationsEnabled,
                onCheckedChange = { enabled ->
                    onAction(
                        NotificationSettingsUiAction
                            .SetBreakReminderEnabled(enabled)
                    )
                }
            )
        }

        // 每日任务提醒卡片
        KikoCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = MaterialTheme.spacing.large,
                vertical = MaterialTheme.spacing.small
            )
        ) {
            // 每日任务提醒开关
            NotificationSwitchRow(
                title = stringResource(
                    R.string.notification_daily_task
                ),
                checked = state.dailyTaskReminderEnabled,
                enabled = state.notificationsEnabled,
                onCheckedChange = { enabled ->
                    onAction(
                        NotificationSettingsUiAction
                            .SetDailyTaskReminderEnabled(enabled)
                    )
                }
            )

            // 每日任务提醒时间选择
            NotificationNavigationRow(
                title = stringResource(
                    R.string.notification_daily_task_time
                ),
                value = state.dailyTaskReminderTimeText,
                enabled = state.notificationsEnabled &&
                    state.dailyTaskReminderEnabled,
                onClick = { showTimeDialog = true }
            )
        }
    }

    // 时间选择对话框
    if (showTimeDialog) {
        TimePickerDialog(
            hour = state.dailyTaskReminderHour,
            minute = state.dailyTaskReminderMinute,
            onDismissRequest = { showTimeDialog = false },
            onConfirm = { hour, minute ->
                showTimeDialog = false
                onAction(
                    NotificationSettingsUiAction
                        .SetDailyTaskReminderTime(hour, minute)
                )
            }
        )
    }
}

/**
 * 通知权限卡片组件
 * 显示系统通知权限状态和操作按钮
 */
@Composable
private fun PermissionCard(
    // 系统通知权限是否开启
    systemNotificationsEnabled: Boolean,

    // 请求通知权限回调
    onRequestPermission: () -> Unit,

    // 打开系统通知设置回调
    onOpenSystemSettings: () -> Unit
) {
    KikoCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(MaterialTheme.spacing.large)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.medium
            ),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small
                )
            ) {
                Text(
                    text = if (systemNotificationsEnabled) {
                        stringResource(
                            R.string.notification_permission_enabled
                        )
                    } else {
                        stringResource(
                            R.string.notification_permission_disabled
                        )
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(
                        R.string.notification_permission_description
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // 未开启权限时显示操作按钮
                if (!systemNotificationsEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        PrimaryButton(
                            text = stringResource(
                                R.string.notification_permission_request
                            ),
                            onClick = onRequestPermission,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    SecondaryButton(
                        text = stringResource(
                            R.string.notification_permission_system
                        ),
                        onClick = onOpenSystemSettings,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * 通知开关行组件
 * 显示通知选项标题和开关
 */
@Composable
private fun NotificationSwitchRow(
    // 选项标题
    title: String,

    // 开关状态
    checked: Boolean,

    // 开关状态变化回调
    onCheckedChange: (Boolean) -> Unit,

    modifier: Modifier = Modifier,

    // 是否启用
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.medium
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * 通知导航行组件
 * 显示可点击的选项，右侧显示当前值
 */
@Composable
private fun NotificationNavigationRow(
    // 选项标题
    title: String,

    // 当前值
    value: String,

    // 是否启用
    enabled: Boolean,

    // 点击回调
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.medium
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        TextButton(
            onClick = onClick,
            enabled = enabled
        ) {
            Text(text = value)
        }
    }
}

/**
 * 时间选择对话框组件
 * 使用滚轮方式选择小时和分钟
 */
@Composable
private fun TimePickerDialog(
    // 当前小时
    hour: Int,

    // 当前分钟
    minute: Int,

    // 取消回调
    onDismissRequest: () -> Unit,

    // 确认回调，返回选择的小时和分钟
    onConfirm: (Int, Int) -> Unit
) {
    var selectedHour by remember(hour) { mutableIntStateOf(hour) }
    var selectedMinute by remember(minute) { mutableIntStateOf(minute) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(R.string.notification_time_title))
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 小时滚轮
                TimeWheel(
                    values = (0..23).toList(),
                    value = selectedHour,
                    onValueChange = { selectedHour = it }
                )
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineMedium
                )
                // 分钟滚轮
                TimeWheel(
                    values = (0..59).toList(),
                    value = selectedMinute,
                    onValueChange = { selectedMinute = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedHour, selectedMinute)
                }
            ) {
                Text(text = stringResource(R.string.focus_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.task_editor_cancel))
            }
        }
    )
}

/**
 * 时间滚轮组件
 * 使用 LazyColumn 实现可滚动的数字选择器，支持自动对齐到选中项
 */
@Composable
private fun TimeWheel(
    // 可选值列表
    values: List<Int>,

    // 当前选中值
    value: Int,

    // 值变化回调
    onValueChange: (Int) -> Unit
) {
    val selectedIndex = values.indexOf(value).coerceAtLeast(0)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = selectedIndex
    )
    val flingBehavior = rememberSnapFlingBehavior(listState)

    // 监听滚动位置变化，更新选中值
    LaunchedEffect(listState, values) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                values.getOrNull(index)?.let(onValueChange)
            }
    }

    LazyColumn(
        modifier = Modifier
            .width(72.dp)
            .height(132.dp),
        state = listState,
        flingBehavior = flingBehavior,
        contentPadding = PaddingValues(vertical = 44.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(values.size) { index ->
            val itemValue = values[index]
            Text(
                text = "%02d".format(itemValue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                textAlign = TextAlign.Center,
                // 选中项使用更大的字体
                style = if (itemValue == value) {
                    MaterialTheme.typography.headlineSmall
                } else {
                    MaterialTheme.typography.titleMedium
                },
                // 选中项使用更亮的颜色
                color = if (itemValue == value) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

private const val DISABLED_ALPHA = 0.45f

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun NotificationSettingsScreenPreview() {
    KikoTheme {
        NotificationSettingsScreen(
            state = NotificationSettingsUiState(
                systemNotificationsEnabled = false
            ),
            onAction = {},
            onNavigateBack = {},
            onRequestPermission = {},
            onOpenSystemSettings = {}
        )
    }
}
