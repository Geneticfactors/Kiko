package com.xu.kiko.ui.screen.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.ui.component.KikoCard
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing
import kotlin.math.abs
import kotlin.math.max

@Composable
private fun formatFocusDuration(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return if (hours > 0) {
        stringResource(R.string.statistics_focus_duration_hours_minutes, hours, minutes)
    } else {
        stringResource(R.string.statistics_focus_duration_minutes, minutes)
    }
}

@Composable
fun StatisticsTotalFocusCard(
    totalFocusMinutes: Int,
    comparePercent: Int,
    modifier: Modifier = Modifier
) {
    KikoCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                Text(
                    text = stringResource(R.string.statistics_week_focus),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatFocusDuration(totalFocusMinutes),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = when {
                    comparePercent > 0 ->
                        stringResource(R.string.statistics_compare_up, comparePercent)

                    comparePercent < 0 ->
                        stringResource(R.string.statistics_compare_down, abs(comparePercent))

                    else ->
                        stringResource(R.string.statistics_compare_flat)
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun StatisticsMetricCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    KikoCard(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
            ) {
                Text(
                    text = value,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = label,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun DailyFocusBarChart(
    bars: List<StatisticsBarUiModel>,
    modifier: Modifier = Modifier
) {
    if (bars.isEmpty()) {
        Box(modifier = modifier.height(160.dp))
        return
    }

    val primary = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val maxMinutes = max(1, bars.maxOf { it.valueMinutes })

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp)
        ) {
            val spacing = size.width / (bars.size * 2f + 1f)
            val barWidth = spacing
            val maxBarHeight = size.height

            bars.forEachIndexed { index, bar ->
                val ratio = bar.valueMinutes.toFloat() / maxMinutes.toFloat()
                val barHeight = maxBarHeight * ratio
                val left = spacing + index * spacing * 2f
                val top = size.height - barHeight

                drawRoundRect(
                    color = primary,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            bars.forEach { bar ->
                Text(
                    text = bar.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor
                )
            }
        }
    }
}

@Composable
fun FocusTrendChart(
    points: List<StatisticsTrendPointUiModel>,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val maxMinutes = max(1, points.maxOfOrNull { it.valueMinutes } ?: 1)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        if (points.isEmpty()) return@Canvas

        val horizontalStep = if (points.size > 1) {
            size.width / (points.size - 1)
        } else {
            size.width / 2f
        }

        fun pointOffset(index: Int, valueMinutes: Int): Offset {
            val x = if (points.size > 1) horizontalStep * index else size.width / 2f
            val ratio = valueMinutes.toFloat() / maxMinutes.toFloat()
            val y = size.height - size.height * ratio
            return Offset(x, y)
        }

        if (points.size == 1) {
            drawCircle(
                color = primary,
                radius = 5.dp.toPx(),
                center = pointOffset(0, points.first().valueMinutes)
            )
            return@Canvas
        }

        val linePath = Path()
        val fillPath = Path()

        points.forEachIndexed { index, point ->
            val offset = pointOffset(index, point.valueMinutes)

            if (index == 0) {
                linePath.moveTo(offset.x, offset.y)
                fillPath.moveTo(offset.x, size.height)
                fillPath.lineTo(offset.x, offset.y)
            } else {
                linePath.lineTo(offset.x, offset.y)
                fillPath.lineTo(offset.x, offset.y)
            }
        }

        fillPath.lineTo(size.width, size.height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    primary.copy(alpha = 0.18f),
                    primary.copy(alpha = 0.02f)
                )
            )
        )

        drawPath(
            path = linePath,
            color = primary,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        points.forEachIndexed { index, point ->
            drawCircle(
                color = primary,
                radius = 4.dp.toPx(),
                center = pointOffset(index, point.valueMinutes)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsTotalFocusCardPreview() {
    KikoTheme {
        StatisticsTotalFocusCard(
            totalFocusMinutes = 750,
            comparePercent = 15,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsMetricCardPreview() {
    KikoTheme {
        Row(
            modifier = Modifier
                .width(360.dp)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticsMetricCard(
                value = stringResource(R.string.statistics_streak_days, 23),
                label = stringResource(R.string.statistics_streak_label),
                modifier = Modifier.weight(1f)
            )
            StatisticsMetricCard(
                value = stringResource(R.string.statistics_completion_rate, 78),
                label = stringResource(R.string.statistics_completion_label),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyFocusBarChartPreview() {
    KikoTheme {
        KikoCard(
            modifier = Modifier
                .width(360.dp)
                .padding(20.dp)
        ) {
            DailyFocusBarChart(
                bars = listOf(
                    StatisticsBarUiModel("一", 90),
                    StatisticsBarUiModel("二", 120),
                    StatisticsBarUiModel("三", 60),
                    StatisticsBarUiModel("四", 150),
                    StatisticsBarUiModel("五", 110),
                    StatisticsBarUiModel("六", 180),
                    StatisticsBarUiModel("日", 40)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FocusTrendChartPreview() {
    KikoTheme {
        KikoCard(
            modifier = Modifier
                .width(360.dp)
                .padding(20.dp)
        ) {
            FocusTrendChart(
                points = listOf(
                    StatisticsTrendPointUiModel(60),
                    StatisticsTrendPointUiModel(120),
                    StatisticsTrendPointUiModel(90),
                    StatisticsTrendPointUiModel(160),
                    StatisticsTrendPointUiModel(140),
                    StatisticsTrendPointUiModel(210),
                    StatisticsTrendPointUiModel(180)
                )
            )
        }
    }
}