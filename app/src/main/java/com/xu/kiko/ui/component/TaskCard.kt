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

@Composable
fun TaskCard(
    title: String,
    note: String?,
    categoryText: String,
    completedPomodoros: Int,
    estimatedPomodoros: Int,
    completed: Boolean,
    onCompletedChange: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
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
            Checkbox(
                checked = completed,
                onCheckedChange = if (enabled) {
                    onCompletedChange
                } else {
                    null
                }
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small
                )
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (completed) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    color = if (completed) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                if (!note.isNullOrBlank()) {
                    Text(
                        text = note,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

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