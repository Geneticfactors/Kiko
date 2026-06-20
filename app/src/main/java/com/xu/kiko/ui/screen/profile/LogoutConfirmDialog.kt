package com.xu.kiko.ui.screen.profile

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.ui.component.DangerTextButton
import com.xu.kiko.ui.theme.KikoTheme

@Composable
fun LogoutConfirmDialog(
    loggingOut: Boolean,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!loggingOut) {
                onDismissRequest()
            }
        },
        title = {
            Text(text = stringResource(R.string.profile_logout_title))
        },
        text = {
            Text(text = stringResource(R.string.profile_logout_message))
        },
        confirmButton = {
            DangerTextButton(
                text = stringResource(R.string.profile_logout_confirm),
                onClick = onConfirm,
                enabled = !loggingOut
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                enabled = !loggingOut
            ) {
                Text(text = stringResource(R.string.profile_logout_cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun LogoutConfirmDialogPreview() {
    KikoTheme {
        LogoutConfirmDialog(
            loggingOut = false,
            onConfirm = {},
            onDismissRequest = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LogoutConfirmDialogLoadingPreview() {
    KikoTheme {
        LogoutConfirmDialog(
            loggingOut = true,
            onConfirm = {},
            onDismissRequest = {}
        )
    }
}

