package com.xu.kiko.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

/**
 * 任务卡片组件
 * 显示任务标题、备注、分类、完成进度和操作菜单
 */
@Composable
fun TaskCard(
    // 任务标题
    title: String,
    // 任务备注（可选）
    note: String?,
    // 分类显示文本
    categoryText: String,
    // 已完成番茄钟数
    completedPomodoros: Int,
    // 预计番茄钟数
    estimatedPomodoros: Int,
    // 是否完成
    completed: Boolean,
    // 完成状态变化回调
    onCompletedChange: (Boolean) -> Unit,
    // 卡片点击回调
    onClick: () -> Unit,
    // 删除按钮点击回调
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    // 是否可用
    enabled: Boolean = true
) {
    // 操作菜单展开状态
    var menuExpanded by remember { mutableStateOf(false) }

    KikoCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // 完成复选框
            Checkbox(
                checked = completed,
                onCheckedChange = if (enabled) {
                    onCompletedChange
                } else {
                    null
                }
            )

            // 任务内容区域
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small
                )
            ) {
                // 任务标题
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    // 完成状态显示删除线
                    textDecoration = if (completed) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    // 完成状态使用灰色
                    color = if (completed) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                // 任务备注
                if (!note.isNullOrBlank()) {
                    Text(
                        text = note,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // 分类标签和进度
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        MaterialTheme.spacing.medium
                    )
                ) {
                    TagChip(text = categoryText)

                    Text(
                        text = stringResource(
                            R.string.tasks_task_progress,
                            completedPomodoros,
                            estimatedPomodoros
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // 更多操作按钮
            IconButton(
                onClick = {
                    menuExpanded = true
                },
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(
                        R.string.tasks_more_actions
                    )
                )
            }

            // 操作菜单
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = {
                    menuExpanded = false
                }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(R.string.tasks_delete))
                    },
                    onClick = {
                        menuExpanded = false
                        onDeleteClick()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCardPreview() {
    KikoTheme {
        TaskCard(
            title = "完成任务页面",
            note = "实现任务卡片、筛选和编辑弹层",
            categoryText = "学习",
            completedPomodoros = 1,
            estimatedPomodoros = 4,
            completed = false,
            onCompletedChange = {},
            onClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCardCompletedPreview() {
    KikoTheme {
        TaskCard(
            title = "复习 Kotlin 协程",
            note = null,
            categoryText = "工作",
            completedPomodoros = 5,
            estimatedPomodoros = 4,
            completed = true,
            onCompletedChange = {},
            onClick = {},
            onDeleteClick = {}
        )
    }
}