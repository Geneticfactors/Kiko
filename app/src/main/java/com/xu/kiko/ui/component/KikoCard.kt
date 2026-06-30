package com.xu.kiko.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

/**
 * 卡片容器组件
 * 使用 Surface 包装内容，提供统一的卡片样式
 */
@Composable
fun KikoCard(
    modifier: Modifier = Modifier,
    // 内容内边距，默认使用大间距
    contentPadding: PaddingValues =
        PaddingValues(MaterialTheme.spacing.large),
    // 卡片内容
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KikoCardPreview() {
    KikoTheme {
        KikoCard {
            Text(text = "Kiko Card")
        }
    }
}
