package com.xu.kiko.ui.screen.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.domain.model.TaskCategory
import com.xu.kiko.domain.usecase.task.TaskFieldError
import com.xu.kiko.ui.component.CategorySelector
import com.xu.kiko.ui.component.KikoTextField
import com.xu.kiko.ui.component.PomodoroStepper
import com.xu.kiko.ui.component.PrimaryButton
import com.xu.kiko.ui.component.SecondaryButton
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorSheet(
    state: TaskEditorUiState,
    onAction: (TaskEditorUiAction) -> Unit,
    onDismissRequest: () -> Unit,
    saving: Boolean,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = MaterialTheme.spacing.formHorizontal,
                    vertical = MaterialTheme.spacing.large
                ),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.large
            )
        ) {
            Text(
                text = stringResource(
                    if (state.mode == TaskEditorMode.CREATE) {
                        R.string.task_editor_create_title
                    } else {
                        R.string.task_editor_edit_title
                    }
                ),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            KikoTextField(
                value = state.title,
                onValueChange = {
                    onAction(TaskEditorUiAction.TitleChanged(it))
                },
                label = stringResource(
                    R.string.task_editor_title_label
                ),
                placeholder = stringResource(
                    R.string.task_editor_title_placeholder
                ),
                errorText = taskFieldErrorText(state.titleError),
                enabled = !saving,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            KikoTextField(
                value = state.note,
                onValueChange = {
                    onAction(TaskEditorUiAction.NoteChanged(it))
                },
                label = stringResource(R.string.task_editor_note_label),
                placeholder = stringResource(
                    R.string.task_editor_note_placeholder
                ),
                errorText = taskFieldErrorText(state.noteError),
                enabled = !saving,
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier.heightIn(max = 160.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small
                )
            ) {
                Text(
                    text = stringResource(
                        R.string.task_editor_pomodoros
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                PomodoroStepper(
                    value = state.estimatedPomodoros,
                    onDecrease = {
                        onAction(TaskEditorUiAction.DecreasePomodoros)
                    },
                    onIncrease = {
                        onAction(TaskEditorUiAction.IncreasePomodoros)
                    },
                    enabled = !saving
                )

                taskFieldErrorText(state.pomodoroError)?.let { error ->
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small
                )
            ) {
                Text(
                    text = stringResource(R.string.task_editor_category),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                CategorySelector(
                    selectedCategory = state.category,
                    onCategorySelected = {
                        onAction(TaskEditorUiAction.CategoryChanged(it))
                    },
                    enabled = !saving
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.medium
                )
            ) {
                SecondaryButton(
                    text = stringResource(R.string.task_editor_cancel),
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f),
                    enabled = !saving
                )

                PrimaryButton(
                    text = stringResource(R.string.task_editor_save),
                    onClick = {
                        onAction(TaskEditorUiAction.Save)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !saving,
                    loading = saving
                )
            }
        }
    }
}

@Composable
private fun taskFieldErrorText(error: TaskFieldError?): String? {
    return when (error) {
        TaskFieldError.REQUIRED ->
            stringResource(R.string.task_error_required)

        TaskFieldError.TITLE_TOO_LONG ->
            stringResource(R.string.task_error_title_too_long)

        TaskFieldError.NOTE_TOO_LONG ->
            stringResource(R.string.task_error_note_too_long)

        TaskFieldError.INVALID_POMODORO_COUNT ->
            stringResource(R.string.task_error_pomodoro_count)

        null -> null
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskEditorSheetPreview() {
    KikoTheme {
        TaskEditorSheet(
            state = TaskEditorUiState(
                title = "完成任务页面",
                note = "实现列表、筛选和编辑弹层",
                category = TaskCategory.STUDY
            ),
            onAction = {},
            onDismissRequest = {},
            saving = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskEditorSheetErrorPreview() {
    KikoTheme {
        TaskEditorSheet(
            state = TaskEditorUiState(
                titleError = TaskFieldError.REQUIRED,
                pomodoroError =
                    TaskFieldError.INVALID_POMODORO_COUNT
            ),
            onAction = {},
            onDismissRequest = {},
            saving = false
        )
    }
}
