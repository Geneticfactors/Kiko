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

/**
 * 退出登录确认对话框组件
 * 用于确认用户是否真的要退出登录
 */
@Composable
fun LogoutConfirmDialog(
    // 是否正在退出登录中
    loggingOut: Boolean,

    // 确认退出登录回调
    onConfirm: () -> Unit,

    // 取消退出登录回调
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

