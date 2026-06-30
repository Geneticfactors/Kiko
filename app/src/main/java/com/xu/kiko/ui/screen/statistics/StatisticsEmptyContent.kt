package com.xu.kiko.ui.screen.statistics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.ui.component.EmptyContent
import com.xu.kiko.ui.theme.KikoTheme

/**
 * 统计页面空状态组件
 * 当没有统计数据时显示提示信息
 */
@Composable
fun StatisticsEmptyContent(
    modifier: Modifier = Modifier
) {
    EmptyContent(
        title = stringResource(R.string.statistics_empty_title),
        message = stringResource(R.string.statistics_empty_message),
        actionText = null,
        onAction = null,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun StatisticsEmptyContentPreview() {
    KikoTheme {
        StatisticsEmptyContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticsEmptyContentDarkPreview() {
    KikoTheme(darkTheme = true) {
        StatisticsEmptyContent()
    }
}

