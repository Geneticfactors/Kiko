package com.xu.kiko.ui.screen.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

/**
 * 时长选项 Chip 组件
 * 单个时长选项的展示控件
 */
@Composable
fun DurationOptionChip(
    // 显示文本
    text: String,

    // 是否被选中
    selected: Boolean,

    // 是否可用
    enabled: Boolean,

    // 点击回调
    onClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = text) },
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled
    )
}

/**
 * 专注时长选择器组件
 * 提供 25 分钟、45 分钟和自定义时长三个选项
 */
@Composable
fun DurationSelector(
    // 当前选中的时长选项
    selectedDuration: FocusDurationOption,

    // 是否可用（计时未开始时可用）
    enabled: Boolean,

    // 选择 25 分钟回调
    onSelect25Minutes: () -> Unit,

    // 选择 45 分钟回调
    onSelect45Minutes: () -> Unit,

    // 选择自定义时长回调
    onSelectCustom: () -> Unit,

    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
    ) {
        DurationOptionChip(
            text = stringResource(R.string.focus_duration_25),
            selected = selectedDuration is FocusDurationOption.TwentyFiveMinutes,
            enabled = enabled,
            onClick = onSelect25Minutes,
            modifier = Modifier.weight(1f)
        )

        DurationOptionChip(
            text = stringResource(R.string.focus_duration_45),
            selected = selectedDuration is FocusDurationOption.FortyFiveMinutes,
            enabled = enabled,
            onClick = onSelect45Minutes,
            modifier = Modifier.weight(1f)
        )

        DurationOptionChip(
            text = stringResource(R.string.focus_duration_custom),
            selected = selectedDuration is FocusDurationOption.Custom,
            enabled = enabled,
            onClick = onSelectCustom,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(
    name = "Duration Selector - 25 Minutes",
    showBackground = true,
    widthDp = 390
)
@Composable
private fun DurationSelector25Preview() {
    KikoTheme {
        DurationSelector(
            selectedDuration = FocusDurationOption.TwentyFiveMinutes,
            enabled = true,
            onSelect25Minutes = {},
            onSelect45Minutes = {},
            onSelectCustom = {}
        )
    }
}

@Preview(
    name = "Duration Selector - 45 Minutes",
    showBackground = true,
    widthDp = 390
)
@Composable
private fun DurationSelector45Preview() {
    KikoTheme {
        DurationSelector(
            selectedDuration = FocusDurationOption.FortyFiveMinutes,
            enabled = true,
            onSelect25Minutes = {},
            onSelect45Minutes = {},
            onSelectCustom = {}
        )
    }
}

@Preview(
    name = "Duration Selector - Custom",
    showBackground = true,
    widthDp = 390
)
@Composable
private fun DurationSelectorCustomPreview() {
    KikoTheme {
        DurationSelector(
            selectedDuration = FocusDurationOption.Custom(30),
            enabled = true,
            onSelect25Minutes = {},
            onSelect45Minutes = {},
            onSelectCustom = {}
        )
    }
}

@Preview(
    name = "Duration Selector - Disabled",
    showBackground = true,
    widthDp = 390
)
@Composable
private fun DurationSelectorDisabledPreview() {
    KikoTheme {
        DurationSelector(
            selectedDuration = FocusDurationOption.TwentyFiveMinutes,
            enabled = false,
            onSelect25Minutes = {},
            onSelect45Minutes = {},
            onSelectCustom = {}
        )
    }
}