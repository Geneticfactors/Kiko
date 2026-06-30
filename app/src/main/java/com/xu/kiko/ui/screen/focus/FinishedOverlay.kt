package com.xu.kiko.ui.screen.focus

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.xu.kiko.R

/**
 * 专注完成弹窗组件
 * 在专注计时结束后显示，提示用户专注已完成
 */
@Composable
fun FinishedOverlay(
    // 关闭弹窗回调
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.focus_finished_title))
        },
        text = {
            Text(text = stringResource(R.string.focus_finished_message))
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.focus_confirm))
            }
        }
    )
}