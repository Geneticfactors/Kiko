package com.xu.kiko.ui.screen.focus

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.ui.theme.KikoTheme

/**
 * 今日任务卡片组件
 * 显示单个任务的标题、完成进度和勾选状态
 */
@Composable
fun TodayTaskCard(
    // 任务数据
    task: FocusTaskUiModel,

    // 是否被选中
    selected: Boolean,

    // 是否可用
    enabled: Boolean,

    // 点击任务回调
    onClick: () -> Unit,

    // 完成状态变化回调
    onCompletedChange: (Boolean) -> Unit,

    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled && !task.isCompleted,
                onClick = onClick
            ),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = MaterialTheme.shapes.medium,
        border = if (selected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 勾选框
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCompletedChange,
                enabled = enabled
            )

            // 任务标题
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textDecoration = if (task.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    TextDecoration.None
                },
                color = if (task.isCompleted) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(0.05f))

            // 完成进度（如 "1/3"）
            Text(
                text = stringResource(
                    R.string.focus_task_progress,
                    task.completedPomodoros,
                    task.estimatedPomodoros
                ),
                modifier = Modifier.padding(end = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TodayTaskCardIncompletePreview() {
    KikoTheme {
        TodayTaskCard(
            task = FocusTaskUiModel(
                id = "1",
                title = "完成 Focus 页面开发",
                completedPomodoros = 1,
                estimatedPomodoros = 3,
                isCompleted = false
            ),
            selected = false,
            enabled = true,
            onClick = {},
            onCompletedChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TodayTaskCardCompletedPreview() {
    KikoTheme {
        TodayTaskCard(
            task = FocusTaskUiModel(
                id = "1",
                title = "完成 Focus 页面开发",
                completedPomodoros = 3,
                estimatedPomodoros = 3,
                isCompleted = true
            ),
            selected = false,
            enabled = true,
            onClick = {},
            onCompletedChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TodayTaskCardSelectedPreview() {
    KikoTheme {
        TodayTaskCard(
            task = FocusTaskUiModel(
                id = "1",
                title = "完成 Focus 页面开发",
                completedPomodoros = 1,
                estimatedPomodoros = 3,
                isCompleted = false
            ),
            selected = true,
            enabled = true,
            onClick = {},
            onCompletedChange = {}
        )
    }
}