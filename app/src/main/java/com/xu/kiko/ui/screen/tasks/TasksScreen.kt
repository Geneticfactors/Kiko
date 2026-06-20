package com.xu.kiko.ui.screen.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.domain.model.TaskCategory
import com.xu.kiko.ui.component.EmptyContent
import com.xu.kiko.ui.component.ErrorContent
import com.xu.kiko.ui.component.LoadingContent
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    state: TasksUiState,
    onAction: (TasksUiAction) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.tasks_title))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(TasksUiAction.OpenCreateTask)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(
                        R.string.tasks_add
                    )
                )
            }
        }
    ) { innerPadding ->
        TasksScreenContent(
            state = state,
            onAction = onAction,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        )

        state.editor?.let { editor ->
            TaskEditorSheet(
                state = editor,
                onAction = { action ->
                    onAction(TasksUiAction.EditorAction(action))
                },
                onDismissRequest = {
                    onAction(TasksUiAction.CloseEditor)
                },
                saving = state.isSaving
            )
        }

        state.pendingDeleteTask?.let { task ->
            DeleteTaskDialog(
                taskTitle = task.title,
                deleting = state.isDeleting,
                onConfirm = {
                    onAction(TasksUiAction.ConfirmDeleteTask)
                },
                onDismiss = {
                    onAction(TasksUiAction.CancelDeleteTask)
                }
            )
        }
    }
}

@Composable
private fun TasksScreenContent(
    state: TasksUiState,
    onAction: (TasksUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        state.isLoading -> {
            LoadingContent(modifier = modifier)
        }

        state.errorMessage != null && state.sections.isEmpty() -> {
            ErrorContent(
                message = state.errorMessage,
                retryText = stringResource(R.string.tasks_retry),
                onRetry = {
                    onAction(TasksUiAction.Retry)
                },
                modifier = modifier.padding(
                    horizontal = MaterialTheme.spacing.extraLarge
                )
            )
        }

        state.sections.isEmpty() -> {
            EmptyContent(
                title = stringResource(R.string.tasks_empty_title),
                message = stringResource(
                    R.string.tasks_empty_message
                ),
                actionText = stringResource(
                    R.string.tasks_empty_action
                ),
                onAction = {
                    onAction(TasksUiAction.OpenCreateTask)
                },
                modifier = modifier.padding(
                    horizontal = MaterialTheme.spacing.extraLarge
                )
            )
        }

        else -> {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(
                    horizontal = MaterialTheme.spacing.extraLarge,
                    vertical = MaterialTheme.spacing.large
                )
            ) {
                item {
                    TaskFilterBar(
                        selectedFilter = state.selectedFilter,
                        onFilterSelected = { filter ->
                            onAction(
                                TasksUiAction.SelectFilter(filter)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = MaterialTheme.spacing.section
                            )
                    )
                }

                items(
                    items = state.sections,
                    key = { section -> section.section.name }
                ) { section ->
                    TaskSection(
                        section = section,
                        onCompletedChange = { taskId, completed ->
                            onAction(
                                TasksUiAction.SetTaskCompleted(
                                    taskId = taskId,
                                    completed = completed
                                )
                            )
                        },
                        onTaskClick = { taskId ->
                            onAction(TasksUiAction.OpenEditTask(taskId))
                        },
                        onDeleteClick = { taskId ->
                            onAction(
                                TasksUiAction.RequestDeleteTask(taskId)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = MaterialTheme.spacing.section
                            )
                    )
                }
            }
        }
    }
}

private fun previewTaskSections(): List<TaskSectionUiModel> {
    return listOf(
        TaskSectionUiModel(
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
                ),
                TaskUiModel(
                    id = "2",
                    title = "复习 Kotlin 协程",
                    note = null,
                    category = TaskCategory.WORK,
                    estimatedPomodoros = 2,
                    completedPomodoros = 2,
                    isCompleted = true,
                    dateSection = TaskDateSection.TODAY
                )
            )
        ),
        TaskSectionUiModel(
            section = TaskDateSection.YESTERDAY,
            tasks = listOf(
                TaskUiModel(
                    id = "3",
                    title = "整理番茄钟开发计划",
                    note = "补充任务闭环",
                    category = TaskCategory.READING,
                    estimatedPomodoros = 3,
                    completedPomodoros = 0,
                    isCompleted = false,
                    dateSection = TaskDateSection.YESTERDAY
                )
            )
        )
    )
}

@Preview(
    name = "Tasks - Default",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun TasksScreenDefaultPreview() {
    KikoTheme {
        TasksScreen(
            state = TasksUiState(
                sections = previewTaskSections()
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Tasks - Empty",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun TasksScreenEmptyPreview() {
    KikoTheme {
        TasksScreen(
            state = TasksUiState(),
            onAction = {}
        )
    }
}

@Preview(
    name = "Tasks - Error",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun TasksScreenErrorPreview() {
    KikoTheme {
        TasksScreen(
            state = TasksUiState(
                errorMessage = "任务加载失败"
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Tasks - Editor",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun TasksScreenEditorPreview() {
    KikoTheme {
        TasksScreen(
            state = TasksUiState(
                sections = previewTaskSections(),
                editor = TaskEditorUiState(
                    title = "完成任务页面",
                    note = "实现列表、筛选和编辑弹层",
                    category = TaskCategory.STUDY
                )
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Tasks - Delete Dialog",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun TasksScreenDeleteDialogPreview() {
    val task = previewTaskSections().first().tasks.first()

    KikoTheme {
        TasksScreen(
            state = TasksUiState(
                sections = previewTaskSections(),
                pendingDeleteTask = task
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Tasks - Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun TasksScreenDarkPreview() {
    KikoTheme(darkTheme = true) {
        TasksScreen(
            state = TasksUiState(
                sections = previewTaskSections()
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Tasks - 320dp",
    showBackground = true,
    widthDp = 320,
    heightDp = 844
)
@Composable
private fun TasksScreenNarrowPreview() {
    KikoTheme {
        TasksScreen(
            state = TasksUiState(
                sections = previewTaskSections()
            ),
            onAction = {}
        )
    }
}
