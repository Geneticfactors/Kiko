package com.xu.kiko.ui.screen.tasks

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.ui.component.DangerTextButton
import com.xu.kiko.ui.component.SecondaryButton
import com.xu.kiko.ui.theme.KikoTheme

@Composable
fun DeleteTaskDialog(
    taskTitle: String,
    deleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!deleting) {
                onDismiss()
            }
        },
        title = {
            Text(text = stringResource(R.string.task_delete_title))
        },
        text = {
            Text(
                text = stringResource(
                    R.string.task_delete_message,
                    taskTitle
                )
            )
        },
        confirmButton = {
            DangerTextButton(
                text = stringResource(R.string.task_delete_confirm),
                onClick = onConfirm,
                enabled = !deleting
            )
        },
        dismissButton = {
            SecondaryButton(
                text = stringResource(R.string.task_delete_cancel),
                onClick = onDismiss,
                enabled = !deleting
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DeleteTaskDialogPreview() {
    KikoTheme {
        DeleteTaskDialog(
            taskTitle = "完成任务页面",
            deleting = false,
            onConfirm = {},
            onDismiss = {}
        )
    }
}
