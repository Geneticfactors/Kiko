package com.xu.kiko.ui.component

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.ui.theme.KikoTheme

/**
 * 次要按钮组件
 * 使用描边样式，适用于次要操作或取消操作
 */
@Composable
fun SecondaryButton(
    // 按钮文本
    text: String,
    // 点击回调
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // 是否可用
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 56.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
private fun SecondaryButtonPreview() {
    KikoTheme {
        SecondaryButton(
            text = "取消",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SecondaryButtonDarkPreview() {
    KikoTheme(darkTheme = true) {
        SecondaryButton(
            text = "取消",
            onClick = {}
        )
    }
}

