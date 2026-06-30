package com.xu.kiko.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

/**
 * 番茄钟数量步进器组件
 * 提供增减按钮和当前值显示，支持数值范围限制
 */
@Composable
fun PomodoroStepper(
    // 当前值
    value: Int,
    // 减少回调
    onDecrease: () -> Unit,
    // 增加回调
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    // 是否可用
    enabled: Boolean = true,
    // 数值范围，默认 1-20
    valueRange: IntRange = 1..20
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.medium
        )
    ) {
        // 减少按钮
        IconButton(
            onClick = onDecrease,
            // 到达最小值时禁用
            enabled = enabled && value > valueRange.first,
            modifier = Modifier.sizeIn(
                minWidth = 48.dp,
                minHeight = 48.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(
                    R.string.task_editor_decrease_pomodoros
                )
            )
        }
        // 当前值显示
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        // 增加按钮
        IconButton(
            onClick = onIncrease,
            // 到达最大值时禁用
            enabled = enabled && value < valueRange.last,
            modifier = Modifier.sizeIn(
                minWidth = 48.dp,
                minHeight = 48.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(
                    R.string.task_editor_increase_pomodoros
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PomodoroStepperPreview() {
    KikoTheme {
        PomodoroStepper(
            value = 4,
            onDecrease = {},
            onIncrease = {}
        )
    }
}
