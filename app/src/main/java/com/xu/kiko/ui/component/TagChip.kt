package com.xu.kiko.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

/**
 * 标签组件
 * 用于显示分类、状态等信息的小圆角标签
 */
@Composable
fun TagChip(
    // 标签文本
    text: String,
    modifier: Modifier = Modifier,
    // 容器颜色，默认使用次要容器色
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    // 内容颜色，默认使用次要容器文字色
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.extraSmall
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TagChipPreview() {
    KikoTheme {
        TagChip(text = "学习")
    }
}

@Preview(showBackground = true)
@Composable
private fun TagChipDarkPreview() {
    KikoTheme(darkTheme = true) {
        TagChip(text = "工作")
    }
}