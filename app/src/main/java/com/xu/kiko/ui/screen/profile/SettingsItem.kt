package com.xu.kiko.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.ThemeBlue
import com.xu.kiko.ui.theme.spacing

/**
 * 设置项开关组件
 * 用于带开关的设置项（如深色模式）
 */
@Composable
fun SettingsSwitchItem(
    // 设置项标题
    title: String,

    // 开关状态
    checked: Boolean,

    // 开关状态变化回调
    onCheckedChange: (Boolean) -> Unit,

    modifier: Modifier = Modifier
) {
    SettingsItemRow(
        title = title,
        modifier = modifier,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

/**
 * 设置项主题颜色组件
 * 用于显示当前主题颜色的设置项
 */
@Composable
fun SettingsThemeColorItem(
    // 设置项标题
    title: String,

    // 当前选中的颜色
    color: Color,

    // 点击回调
    onClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    SettingsItemRow(
        title = title,
        modifier = modifier.clickable(onClick = onClick),
        trailingContent = {
            // 颜色预览圆点
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    )
}

/**
 * 设置项导航组件
 * 用于可点击跳转到其他页面的设置项
 */
@Composable
fun SettingsNavigationItem(
    // 设置项标题
    title: String,

    // 点击回调
    onClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    SettingsItemRow(
        title = title,
        modifier = modifier.clickable(onClick = onClick),
        trailingContent = {
            // 右侧箭头图标
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

/**
 * 设置项行组件
 * 设置项的基础布局，包含标题和尾部内容
 */
@Composable
private fun SettingsItemRow(
    // 设置项标题
    title: String,

    modifier: Modifier = Modifier,

    // 尾部内容
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp),
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

        trailingContent()
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun SettingsSwitchItemPreview() {
    KikoTheme {
        SettingsSwitchItem(
            title = "深色模式",
            checked = false,
            onCheckedChange = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun SettingsThemeColorItemPreview() {
    KikoTheme {
        SettingsThemeColorItem(
            title = "主题色",
            color = ThemeBlue,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun SettingsNavigationItemPreview() {
    KikoTheme {
        SettingsNavigationItem(
            title = "通知设置",
            onClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun SettingsItemDarkPreview() {
    KikoTheme(darkTheme = true) {
        SettingsNavigationItem(
            title = "通知设置",
            onClick = {}
        )
    }
}

