package com.xu.kiko.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.ui.theme.KikoTheme

/**
 * 统计时间范围选择器组件
 * 提供周/月两种时间范围切换
 */
@Composable
fun StatisticsRangeSelector(
    // 当前选中的时间范围
    selectRange: StatisticsRange,

    // 时间范围选择回调
    onRangeSelected: (StatisticsRange) -> Unit,

    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(2.dp)
    ) {
        StatisticsRangeOption(
            text = stringResource(R.string.statistics_range_week),
            selected = selectRange == StatisticsRange.WEEK,
            onClick = {onRangeSelected(StatisticsRange.WEEK)}
        )
        StatisticsRangeOption(
            text = stringResource(R.string.statistics_range_month),
            selected = selectRange == StatisticsRange.MONTH,
            onClick = {onRangeSelected(StatisticsRange.MONTH)}
        )
    }
}

/**
 * 时间范围选项组件
 * 根据选中状态显示不同样式
 */
@Composable
fun StatisticsRangeOption(
    // 选项文本
    text: String,

    // 是否选中
    selected: Boolean,

    // 点击回调
    onClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary
    }else{
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Text(
        text = text,
        color = contentColor,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = if(selected) FontWeight.SemiBold else FontWeight.Medium,
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .defaultMinSize(minWidth = 44.dp, minHeight = 32.dp)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun StatisticsRangeSelectorWeekPreview() {
    KikoTheme {
        StatisticsRangeSelector(
            selectRange = StatisticsRange.WEEK,
            onRangeSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsRangeSelectorMonthPreview() {
    KikoTheme {
        StatisticsRangeSelector(
            selectRange = StatisticsRange.MONTH,
            onRangeSelected = {}
        )
    }
}