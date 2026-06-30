package com.xu.kiko.ui.screen.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.ui.component.PrimaryButton
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

/**
 * 专注控制按钮组组件
 * 根据计时器状态显示不同的操作按钮
 */
@Composable
fun FocusControlButtons(
    // 计时器当前状态
    status: FocusTimerStatus,

    // 开始按钮是否可用（需要先选择任务）
    startEnabled: Boolean,

    // 开始计时回调
    onStart: () -> Unit,

    // 暂停计时回调
    onPause: () -> Unit,

    // 恢复计时回调
    onResume: () -> Unit,

    // 请求停止计时回调
    onRequestStop: () -> Unit,

    modifier: Modifier = Modifier
) {
    when (status) {
        // 空闲状态：显示单个开始按钮
        FocusTimerStatus.IDLE -> {
            PrimaryButton(
                text = stringResource(R.string.focus_start),
                onClick = onStart,
                modifier = modifier.fillMaxWidth(),
                enabled = startEnabled
            )
        }

        // 运行/暂停状态：显示暂停/恢复按钮和停止按钮
        FocusTimerStatus.RUNNING,
        FocusTimerStatus.PAUSED -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                PrimaryButton(
                    text = if (status == FocusTimerStatus.RUNNING) {
                        stringResource(R.string.focus_pause)
                    } else {
                        stringResource(R.string.focus_resume)
                    },
                    onClick = if (status == FocusTimerStatus.RUNNING) {
                        onPause
                    } else {
                        onResume
                    },
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = onRequestStop,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.focus_stop),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FocusControlButtonsIdlePreview() {
    KikoTheme {
        FocusControlButtons(
            status = FocusTimerStatus.IDLE,
            startEnabled = true,
            onStart = {},
            onPause = {},
            onResume = {},
            onRequestStop = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FocusControlButtonsRunningPreview() {
    KikoTheme {
        FocusControlButtons(
            status = FocusTimerStatus.RUNNING,
            startEnabled = true,
            onStart = {},
            onPause = {},
            onResume = {},
            onRequestStop = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FocusControlButtonsPausedPreview() {
    KikoTheme {
        FocusControlButtons(
            status = FocusTimerStatus.PAUSED,
            startEnabled = true,
            onStart = {},
            onPause = {},
            onResume = {},
            onRequestStop = {}
        )
    }
}