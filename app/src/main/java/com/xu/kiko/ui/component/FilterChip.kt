package com.xu.kiko.ui.component

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.ui.theme.KikoTheme

/**
 * 筛选标签组件
 * 选中时显示勾选图标，适用于分类筛选、状态切换等场景
 */
@Composable
fun KikoFilterChip(
    // 标签文本
    text: String,
    // 是否选中
    selected: Boolean,
    // 点击回调
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    // 是否可用
    enabled: Boolean = true
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(text = text)
        },
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        // 选中时显示勾选图标
        leadingIcon = if(selected){
            {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        }else{
            null
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun KikoFilterChipUnselectedPreview() {
    KikoTheme {
        KikoFilterChip(
            text = "全部",
            selected = false,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KikoFilterChipSelectedPreview() {
    KikoTheme {
        KikoFilterChip(
            text = "学习",
            selected = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KikoFilterChipDarkPreview() {
    KikoTheme(darkTheme = true) {
        KikoFilterChip(
            text = "工作",
            selected = true,
            onClick = {}
        )
    }
}