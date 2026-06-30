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

/**
 * 格式化专注时间显示
 * 将秒数转换为 "MM:SS" 格式的字符串
 *
 * @param seconds 剩余秒数
 * @return 格式化后的时间字符串
 */
private fun formatFocusTime(seconds: Long): String {
    val safeSeconds = seconds.coerceAtLeast(0)
    val minutes = safeSeconds / 60
    val remainingSeconds = safeSeconds % 60

    return "%02d:%02d".format(minutes, remainingSeconds)
}

/**
 * 专注计时器组件
 * 使用 Canvas 绘制环形进度条和剩余时间
 */
@Composable
fun FocusTimer(
    // 剩余时长（秒）
    remainingSeconds: Long,

    // 总时长（秒）
    totalSeconds: Long,

    modifier: Modifier = Modifier
) {
    val safeTotalSeconds = totalSeconds.coerceAtLeast(1L)
    val safeRemainingSeconds = remainingSeconds.coerceIn(0L, safeTotalSeconds)
    val progress = 1f - safeRemainingSeconds.toFloat() / safeTotalSeconds.toFloat()

    val backgroundRingColor = MaterialTheme.colorScheme.primaryContainer
    val progressRingColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 16.dp.toPx()
            val strokeOffset = strokeWidth / 2
            val arcSize = Size(
                width = size.width - strokeWidth,
                height = size.height - strokeWidth
            )

            // 绘制背景环
            drawCircle(
                color = backgroundRingColor,
                radius = (size.minDimension - strokeWidth) / 2,
                style = Stroke(width = strokeWidth)
            )

            // 绘制进度弧
            drawArc(
                color = progressRingColor,
                startAngle = -90f,
                sweepAngle = progress * 360f,
                useCenter = false,
                topLeft = Offset(strokeOffset, strokeOffset),
                size = arcSize,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        // 显示剩余时间
        Text(
            text = formatFocusTime(safeRemainingSeconds),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FocusTimerPreview() {
    KikoTheme {
        FocusTimer(
            remainingSeconds = 25 * 60L,
            totalSeconds = 25 * 60L
        )
    }
}