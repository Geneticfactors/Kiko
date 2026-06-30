package com.xu.kiko.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xu.kiko.R
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

/**
 * 底部导航目标枚举
 * 定义所有底部导航项的标签和图标
 */
enum class KikoBottomNavDestination(
    // 标签资源 ID
    @StringRes val labelRes: Int,
    // 图标
    val icon: ImageVector
) {
    HOME(
        labelRes = R.string.bottom_nav_home,
        icon = Icons.Outlined.Timer
    ),
    STATISTICS(
        labelRes = R.string.bottom_nav_statistics,
        icon = Icons.Outlined.BarChart
    ),
    TASKS(
        labelRes = R.string.bottom_nav_tasks,
        icon = Icons.Outlined.Article
    ),
    PROFILE(
        labelRes = R.string.bottom_nav_profile,
        icon = Icons.Outlined.PersonOutline
    )
}

/**
 * 底部导航栏组件
 * 显示应用主要页面入口，支持选中状态切换和无障碍语义
 */
@Composable
fun KikoBottomNavigationBar(
    // 当前选中的导航目标
    selectedDestination: KikoBottomNavDestination,
    // 导航目标选中回调
    onDestinationSelected: (KikoBottomNavDestination) -> Unit,
    modifier: Modifier = Modifier,
    // 导航目标列表，默认显示所有目标
    destinations: List<KikoBottomNavDestination> =
        KikoBottomNavDestination.entries
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column {
            // 顶部分割线
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .padding(
                        top = MaterialTheme.spacing.small,
                        bottom = MaterialTheme.spacing.small
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEach { destination ->
                    KikoBottomNavigationItem(
                        destination = destination,
                        selected = destination == selectedDestination,
                        onClick = {
                            onDestinationSelected(destination)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * 底部导航项组件
 * 显示图标和标签，支持选中状态样式变化
 */
@Composable
private fun KikoBottomNavigationItem(
    // 导航目标
    destination: KikoBottomNavDestination,
    // 是否选中
    selected: Boolean,
    // 点击回调
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = stringResource(destination.labelRes)
    val selectedDescription = stringResource(R.string.bottom_nav_selected)
    val unselectedDescription = stringResource(R.string.bottom_nav_unselected)
    // 根据选中状态设置颜色
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
    }

    Column(
        modifier = modifier
            .heightIn(min = MaterialTheme.spacing.touchTarget)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab
            )
            // 无障碍语义配置
            .semantics {
                contentDescription = label
                this.selected = selected
                stateDescription = if (selected) {
                    selectedDescription
                } else {
                    unselectedDescription
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = contentColor
        )
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 0.sp
            ),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Preview(
    name = "Bottom Navigation - Light",
    widthDp = 400,
    heightDp = 80,
    showBackground = true
)
@Composable
private fun KikoBottomNavigationBarLightPreview() {
    KikoTheme(darkTheme = false) {
        KikoBottomNavigationBar(
            selectedDestination = KikoBottomNavDestination.HOME,
            onDestinationSelected = {}
        )
    }
}

@Preview(
    name = "Bottom Navigation - Tasks Selected",
    widthDp = 400,
    heightDp = 80,
    showBackground = true
)
@Composable
private fun KikoBottomNavigationBarTasksPreview() {
    KikoTheme(darkTheme = false) {
        KikoBottomNavigationBar(
            selectedDestination = KikoBottomNavDestination.TASKS,
            onDestinationSelected = {}
        )
    }
}

@Preview(
    name = "Bottom Navigation - Dark",
    widthDp = 400,
    heightDp = 80,
    showBackground = true
)
@Composable
private fun KikoBottomNavigationBarDarkPreview() {
    KikoTheme(darkTheme = true) {
        KikoBottomNavigationBar(
            selectedDestination = KikoBottomNavDestination.HOME,
            onDestinationSelected = {}
        )
    }
}
