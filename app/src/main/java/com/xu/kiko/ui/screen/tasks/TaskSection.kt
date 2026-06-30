package com.xu.kiko.ui.screen.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.domain.model.TaskCategory
import com.xu.kiko.ui.component.TaskCard
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

/**
 * 任务分组组件
 * 显示一个日期分组及其下的任务列表
 */
@Composable
fun TaskSection(
    // 任务分组数据
    section: TaskSectionUiModel,

    // 任务完成状态变化回调
    onCompletedChange: (
        taskId: String,
        completed: Boolean
    ) -> Unit,

    // 任务点击回调（打开编辑器）
    onTaskClick: (taskId: String) -> Unit,

    // 删除任务点击回调
    onDeleteClick: (taskId: String) -> Unit,

    modifier: Modifier = Modifier,

    // 是否可用
    enabled: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.medium
        )
    ) {
        // 分组标题（今日/昨日/更早）
        Text(
            text = taskDateSectionText(section.section),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // 任务卡片列表
        section.tasks.forEach { task ->
            TaskCard(
                title = task.title,
                note = task.note,
                categoryText = taskCategoryText(task.category),
                completedPomodoros = task.completedPomodoros,
                estimatedPomodoros = task.estimatedPomodoros,
                completed = task.isCompleted,
                onCompletedChange = { completed ->
                    onCompletedChange(task.id, completed)
                },
                onClick = {
                    onTaskClick(task.id)
                },
                onDeleteClick = {
                    onDeleteClick(task.id)
                },
                enabled = enabled
            )
        }
    }
}

/**
 * 获取日期分组的显示文本
 *
 * @param section 日期分组
 * @return 显示文本
 */
@Composable
private fun taskDateSectionText(section: TaskDateSection): String {
    return when (section) {
        TaskDateSection.TODAY ->
            stringResource(R.string.tasks_section_today)

        TaskDateSection.YESTERDAY ->
            stringResource(R.string.tasks_section_yesterday)

        TaskDateSection.EARLIER ->
            stringResource(R.string.tasks_section_earlier)
    }
}

/**
 * 获取任务分类的显示文本
 *
 * @param category 任务分类
 * @return 显示文本
 */
@Composable
private fun taskCategoryText(category: TaskCategory): String {
    return when (category) {
        TaskCategory.STUDY ->
            stringResource(R.string.task_category_study)

        TaskCategory.WORK ->
            stringResource(R.string.task_category_work)

        TaskCategory.READING ->
            stringResource(R.string.task_category_reading)
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskSectionPreview() {
    KikoTheme {
        TaskSection(
            section = TaskSectionUiModel(
                section = TaskDateSection.TODAY,
                tasks = listOf(
                    TaskUiModel(
                        id = "1",
                        title = "完成任务页面",
                        note = "实现列表、筛选和编辑弹层",
                        category = TaskCategory.STUDY,
                        estimatedPomodoros = 4,
                        completedPomodoros = 1,
                        isCompleted = false,
                        dateSection = TaskDateSection.TODAY
                    )
                )
            ),
            onCompletedChange = { _, _ -> },
            onTaskClick = {},
            onDeleteClick = {}
        )
    }
}
