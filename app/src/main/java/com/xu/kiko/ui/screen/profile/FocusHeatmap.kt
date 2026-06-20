package com.xu.kiko.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.ui.component.KikoCard
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing
import java.util.Calendar

private const val HeatmapWeekCount = 4
private const val HeatmapDaysPerWeek = 7
private const val HeatmapDayCount = HeatmapWeekCount * HeatmapDaysPerWeek

@Composable
fun FocusHeatmap(
    days: List<ProfileCalendarDayUiModel>,
    modifier: Modifier = Modifier
) {
    val heatmapDays = days
        .take(HeatmapDayCount)
        .let { visibleDays ->
            if (visibleDays.size >= HeatmapDayCount) {
                visibleDays
            } else {
                visibleDays + List(HeatmapDayCount - visibleDays.size) { index ->
                    ProfileCalendarDayUiModel(
                        id = "empty-${visibleDays.size + index}",
                        dateEpochMillis = 0L,
                        pomodoroCount = 0,
                        intensity = 0,
                        isFuture = false
                    )
                }
            }
        }

    KikoCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.medium
            )
        ) {
            Text(
                text = stringResource(R.string.profile_focus_calendar),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            FocusHeatmapWeekdayRow()

            Column(
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.medium
                )
            ) {
                heatmapDays.chunked(HeatmapDaysPerWeek).forEach { week ->
                    FocusHeatmapRow(days = week)
                }
            }
        }
    }
}

@Composable
private fun FocusHeatmapWeekdayRow(
    modifier: Modifier = Modifier
) {
    val weekdays = listOf(
        stringResource(R.string.profile_weekday_monday),
        stringResource(R.string.profile_weekday_tuesday),
        stringResource(R.string.profile_weekday_wednesday),
        stringResource(R.string.profile_weekday_thursday),
        stringResource(R.string.profile_weekday_friday),
        stringResource(R.string.profile_weekday_saturday),
        stringResource(R.string.profile_weekday_sunday)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.medium
        )
    ) {
        weekdays.forEach { weekday ->
            Text(
                text = weekday,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FocusHeatmapRow(
    days: List<ProfileCalendarDayUiModel>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.medium
        )
    ) {
        days.forEach { day ->
            FocusHeatmapCell(
                day = day,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FocusHeatmapCell(
    day: ProfileCalendarDayUiModel,
    modifier: Modifier = Modifier
) {
    val color = when {
        day.isFuture ->
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)

        day.intensity <= 0 ->
            MaterialTheme.colorScheme.surfaceVariant

        else ->
            MaterialTheme.colorScheme.primary.copy(
                alpha = heatmapAlphaFor(day.intensity)
            )
    }
    val contentDescription = heatmapCellDescription(day)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .semantics {
                this.contentDescription = contentDescription
            }
    )
}

@Composable
private fun heatmapCellDescription(
    day: ProfileCalendarDayUiModel
): String {
    if (day.dateEpochMillis <= 0L) {
        return stringResource(R.string.profile_heatmap_unknown_day)
    }

    val dateText = dateTextFor(day.dateEpochMillis)
    return when {
        day.isFuture ->
            stringResource(
                R.string.profile_heatmap_future_day,
                dateText
            )

        day.pomodoroCount <= 0 ->
            stringResource(
                R.string.profile_heatmap_no_pomodoro_day,
                dateText
            )

        else ->
            stringResource(
                R.string.profile_heatmap_completed_day,
                dateText,
                day.pomodoroCount
            )
    }
}

private fun heatmapAlphaFor(intensity: Int): Float {
    return when (intensity.coerceIn(0, 4)) {
        1 -> 0.35f
        2 -> 0.55f
        3 -> 0.75f
        else -> 0.95f
    }
}

private fun dateTextFor(epochMillis: Long): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = epochMillis
    }
    return "${calendar.get(Calendar.MONTH) + 1}月" +
        "${calendar.get(Calendar.DAY_OF_MONTH)}日"
}

private fun previewHeatmapDays(): List<ProfileCalendarDayUiModel> {
    val focusedIndexes = setOf(
        1, 2, 4,
        7, 9, 11, 12,
        15, 16, 17, 20,
        21, 24, 26
    )
    val start = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, -21)
    }.timeInMillis

    return List(HeatmapDayCount) { index ->
        val pomodoroCount = if (index in focusedIndexes) {
            (index % 5).coerceAtLeast(1)
        } else {
            0
        }
        ProfileCalendarDayUiModel(
            id = index.toString(),
            dateEpochMillis = start + index * MILLIS_PER_DAY,
            pomodoroCount = pomodoroCount,
            intensity = pomodoroCount.coerceIn(0, 4),
            isFuture = index > 25
        )
    }
}

private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun FocusHeatmapPreview() {
    KikoTheme {
        FocusHeatmap(days = previewHeatmapDays())
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun FocusHeatmapEmptyPreview() {
    KikoTheme {
        FocusHeatmap(days = emptyList())
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun FocusHeatmapDarkPreview() {
    KikoTheme(darkTheme = true) {
        FocusHeatmap(days = previewHeatmapDays())
    }
}

