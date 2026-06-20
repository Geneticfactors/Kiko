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

@Composable
fun DurationOptionChip(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(text = text)
        },
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled
    )
}

@Composable
fun DurationSelector(
    selectedDuration: FocusDurationOption,
    enabled: Boolean,
    onSelect25Minutes: () -> Unit,
    onSelect45Minutes: () -> Unit,
    onSelectCustom: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.medium
        )
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
            selectedDuration =
                FocusDurationOption.TwentyFiveMinutes,
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
            selectedDuration =
                FocusDurationOption.FortyFiveMinutes,
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
            selectedDuration = FocusDurationOption.Custom(minutes = 30),
            enabled = true,
            onSelect25Minutes = {},
            onSelect45Minutes = {},
            onSelectCustom = {}
        )
    }
}