package com.xu.kiko.ui.screen.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.ui.component.KikoCard
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

@Composable
private fun formatFocusMinutes(minutes: Int): String {
    val safeMinutes = minutes.coerceAtLeast(0)
    val hours = safeMinutes / 60
    val remainingMinutes = safeMinutes % 60

    return when {
        hours == 0 -> stringResource(
            R.string.focus_minutes,
            remainingMinutes
        )

        remainingMinutes == 0 -> stringResource(
            R.string.focus_hours,
            hours
        )

        else -> stringResource(
            R.string.focus_hours_minutes,
            hours,
            remainingMinutes
        )
    }
}

@Composable
fun TodaySummaryCard(
    pomodoroCount: Int,
    focusMinutes: Int,
    modifier: Modifier = Modifier
) {
    KikoCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_pomodoro),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )

                Text(
                    text = stringResource(
                        R.string.focus_pomodoro_count,
                        pomodoroCount.coerceAtLeast(0)
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_timer),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = formatFocusMinutes(focusMinutes),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TodaySummaryCardPreview() {
    KikoTheme {
        TodaySummaryCard(
            pomodoroCount = 4,
            focusMinutes = 135
        )
    }
}
