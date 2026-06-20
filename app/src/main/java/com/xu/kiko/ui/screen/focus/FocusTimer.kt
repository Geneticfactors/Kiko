package com.xu.kiko.ui.screen.focus

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.ui.theme.KikoTheme

private fun formatFocusTime(seconds: Long): String {
    val safeSeconds = seconds.coerceAtLeast(0)
    val minutes = safeSeconds / 60
    val remainingSeconds = safeSeconds % 60

    return "%02d:%02d".format(minutes,remainingSeconds)
}

@Composable
fun FocusTimer(
    remainingSeconds: Long,
    totalSeconds: Long,
    modifier: Modifier = Modifier
) {
    val safeTotalSeconds = totalSeconds.coerceAtLeast(1L)
    val safeRemainingSeconds = remainingSeconds.coerceIn(0L,safeTotalSeconds)
    val progress = 1f - safeRemainingSeconds.toFloat() / safeTotalSeconds.toFloat()

    val backgroundRingColor = MaterialTheme.colorScheme.primaryContainer
    val progressRingColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ){
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 16.dp.toPx()
            val strokeOffset = strokeWidth / 2
            val arcSize = Size(
                width = size.width - strokeWidth,
                height = size.height - strokeWidth
            )

            drawCircle(
                color = backgroundRingColor,
                radius = (size.minDimension - strokeWidth) / 2,
                style = Stroke(width = strokeWidth)
            )

            drawArc(
                color = progressRingColor,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                topLeft = Offset(strokeOffset,strokeOffset),
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        Text(
            text = formatFocusTime(safeRemainingSeconds),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(
    name = "Focus Timer - Start",
    showBackground = true
)
@Composable
private fun FocusTimerStartPreview() {
    KikoTheme {
        FocusTimer(
            remainingSeconds = 25 * 60L,
            totalSeconds = 25 * 60L
        )
    }
}

@Preview(
    name = "Focus Timer - Half",
    showBackground = true
)
@Composable
private fun FocusTimerHalfPreview() {
    KikoTheme {
        FocusTimer(
            remainingSeconds = 12 * 60L + 30L,
            totalSeconds = 25 * 60L
        )
    }
}

@Preview(
    name = "Focus Timer - Finished",
    showBackground = true
)
@Composable
private fun FocusTimerFinishedPreview() {
    KikoTheme {
        FocusTimer(
            remainingSeconds = 0L,
            totalSeconds = 25 * 60L
        )
    }
}

@Preview(
    name = "Focus Timer - Dark",
    showBackground = true
)
@Composable
private fun FocusTimerDarkPreview() {
    KikoTheme(darkTheme = true) {
        FocusTimer(
            remainingSeconds = 25 * 60L,
            totalSeconds = 25 * 60L
        )
    }
}
