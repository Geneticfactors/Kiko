package com.xu.kiko.ui.screen.focus

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.xu.kiko.R

/**
 * 中断确认弹窗组件
 * 用于确认用户是否要停止当前正在进行的专注会话
 */
@Composable
fun InterruptConfirmSheet(
    // 关闭弹窗回调
    onDismiss: () -> Unit,

    // 确认中断回调
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.focus_interrupt_title))
        },
        text = {
            Text(text = stringResource(R.string.focus_interrupt_message))
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.focus_interrupt_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.focus_interrupt_confirm))
            }
        }
    )
}