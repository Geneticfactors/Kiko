package com.xu.kiko.ui.component

import androidx.compose.material3.Button
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.ui.theme.KikoTheme

/**
 * 主按钮组件
 * 使用主色调，支持加载状态显示进度指示器
 */
@Composable
fun PrimaryButton(
    // 按钮文本
    text: String,
    // 点击回调
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // 是否可用
    enabled: Boolean = true,
    // 是否显示加载状态
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 56.dp),
        // 加载状态下禁用
        enabled = enabled && !loading,
        shape = MaterialTheme.shapes.extraLarge
    ){
        if (loading){
            // 显示加载指示器
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        }else{
            // 显示文本
            Text(text = text)
        }
    }
}

@Preview(
    name = "Primary Button - Light",
    showBackground = true
)
@Composable
private fun PrimaryButtonLightPreview() {
    KikoTheme(darkTheme = false) {
        PrimaryButton(
            text = "开始",
            onClick = {}
        )
    }
}

@Preview(
    name = "Primary Button - Dark",
    showBackground = true
)
@Composable
private fun PrimaryButtonDarkPreview() {
    KikoTheme(darkTheme = true) {
        PrimaryButton(
            text = "开始",
            onClick = {},
            loading = true
        )
    }
}