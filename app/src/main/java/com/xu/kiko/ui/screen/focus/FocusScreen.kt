package com.xu.kiko.ui.screen.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.ui.component.KikoCard
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

@Composable
fun FocusScreen(
    state: FocusUiState,
    onAction: (FocusUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = MaterialTheme.spacing.extraLarge,
                    vertical = MaterialTheme.spacing.section
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.section
            )
        ) {
            Text(
                text = state.dateText,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            TodaySummaryCard(
                pomodoroCount = state.todayPomodoroCount,
                focusMinutes = state.todayFocusMinutes,
                modifier = Modifier.fillMaxWidth()
            )

            FocusTimer(
                remainingSeconds = state.remainingSeconds,
                totalSeconds = state.totalSeconds
            )

            DurationSelector(
                selectedDuration = state.selectedDuration,
                enabled = state.timerStatus == FocusTimerStatus.IDLE,
                onSelect25Minutes = {
                    onAction(FocusUiAction.Select25Minutes)
                },
                onSelect45Minutes = {
                    onAction(FocusUiAction.Select45Minutes)
                },
                onSelectCustom = {
                    onAction(FocusUiAction.OpenCustomDuration)
                },
                modifier = Modifier.fillMaxWidth()
            )

            FocusControlButtons(
                status = state.timerStatus,
                startEnabled =
                    state.selectedTaskId != null &&
                        state.timerStatus == FocusTimerStatus.IDLE,
                onStart = {
                    onAction(FocusUiAction.StartTimer)
                },
                onPause = {
                    onAction(FocusUiAction.PauseTimer)
                },
                onResume = {
                    onAction(FocusUiAction.ResumeTimer)
                },
                onRequestStop = {
                    onAction(FocusUiAction.RequestStopTimer)
                },
                modifier = Modifier.fillMaxWidth()
            )

            state.focusErrorMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            TodayTasksSection(
                state = state,
                onAction = onAction,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (state.isCustomDurationSheetVisible) {
            CustomDurationSheet(
                currentMinutes = (state.totalSeconds / 60L)
                    .toInt()
                    .coerceIn(5, 120),
                onDismiss = {
                    onAction(FocusUiAction.CloseCustomDuration)
                },
                onConfirm = { minutes ->
                    onAction(
                        FocusUiAction.ConfirmCustomDuration(minutes)
                    )
                }
            )
        }

        if (state.showFinishedOverlay) {
            FinishedOverlay(
                onDismiss = {
                    onAction(FocusUiAction.DismissFinishedOverlay)
                }
            )
        }

        if (state.showInterruptConfirmSheet) {
            InterruptConfirmSheet(
                onDismiss = {
                    onAction(FocusUiAction.DismissStopTimer)
                },
                onConfirm = {
                    onAction(FocusUiAction.ConfirmStopTimer)
                }
            )
        }
    }
}

@Composable
private fun TodayTasksSection(
    state: FocusUiState,
    onAction: (FocusUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    KikoCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(
                    R.string.focus_today_tasks,
                    state.todayTasks.size
                ),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(
                onClick = {
                    onAction(FocusUiAction.ViewAllTasks)
                }
            ) {
                Text(
                    text = stringResource(R.string.focus_view_all)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(MaterialTheme.spacing.small)
        )

        if (state.todayTasks.isEmpty()) {
            Text(
                text = stringResource(R.string.focus_no_tasks),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = {
                    onAction(FocusUiAction.ViewAllTasks)
                }
            ) {
                Text(text = stringResource(R.string.focus_create_task_hint))
            }
        } else {
            state.todayTasks.take(3).forEach { task ->
                TodayTaskCard(
                    task = task,
                    selected = task.id == state.selectedTaskId,
                    enabled = state.timerStatus == FocusTimerStatus.IDLE,
                    onClick = {
                        onAction(FocusUiAction.SelectTask(task.id))
                    },
                    onCompletedChange = { completed ->
                        onAction(
                            FocusUiAction.SetTaskCompleted(
                                taskId = task.id,
                                completed = completed
                            )
                        )
                    }
                )
            }
        }
    }
}

private fun previewFocusUiState(): FocusUiState {
    return FocusUiState(
        dateText = "6月13日 星期六",
        selectedDuration =
            FocusDurationOption.TwentyFiveMinutes,
        totalSeconds = 25 * 60L,
        remainingSeconds = 25 * 60L,
        timerStatus = FocusTimerStatus.IDLE,
        todayPomodoroCount = 4,
        todayFocusMinutes = 135,
        selectedTaskId = "1",
        todayTasks = listOf(
            FocusTaskUiModel(
                id = "1",
                title = "完成 Focus 页面",
                completedPomodoros = 1,
                estimatedPomodoros = 3,
                isCompleted = false
            ),
            FocusTaskUiModel(
                id = "2",
                title = "复习 Kotlin 协程",
                completedPomodoros = 2,
                estimatedPomodoros = 2,
                isCompleted = true
            ),
            FocusTaskUiModel(
                id = "3",
                title = "整理项目开发计划",
                completedPomodoros = 0,
                estimatedPomodoros = 1,
                isCompleted = false
            )
        )
    )
}

@Preview(
    name = "Focus - Default",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun FocusScreenPreview() {
    KikoTheme {
        FocusScreen(
            state = previewFocusUiState(),
            onAction = {}
        )
    }
}

@Preview(
    name = "Focus - 45 Minutes",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun FocusScreen45Preview() {
    KikoTheme {
        FocusScreen(
            state = previewFocusUiState().copy(
                selectedDuration =
                    FocusDurationOption.FortyFiveMinutes,
                totalSeconds = 45 * 60L,
                remainingSeconds = 45 * 60L
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Focus - Running",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun FocusScreenRunningPreview() {
    KikoTheme {
        FocusScreen(
            state = previewFocusUiState().copy(
                remainingSeconds = 18 * 60L + 42L,
                timerStatus = FocusTimerStatus.RUNNING
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Focus - Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun FocusScreenDarkPreview() {
    KikoTheme(darkTheme = true) {
        FocusScreen(
            state = previewFocusUiState(),
            onAction = {}
        )
    }
}
