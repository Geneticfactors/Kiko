package com.xu.kiko.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.ui.theme.KikoTheme
import androidx.compose.ui.text.input.VisualTransformation

/**
 * 文本输入框组件
 * 基于 OutlinedTextField 封装，提供统一的样式和错误状态支持
 */
@Composable
fun KikoTextField(
    // 当前输入值
    value: String,
    // 值变化回调
    onValueChange: (String) -> Unit,
    // 标签文本
    label: String,
    modifier: Modifier = Modifier,
    // 占位文本（可选）
    placeholder: String? = null,
    // 错误提示文本（可选）
    errorText: String? = null,
    // 是否可用
    enabled: Boolean = true,
    // 是否单行
    singleLine: Boolean = true,
    // 键盘选项
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    // 键盘动作
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    // 视觉转换（如密码隐藏）
    visualTransformation: VisualTransformation = VisualTransformation.None,
    // 前置图标（可选）
    leadingIcon: (@Composable (() -> Unit))? = null,
    // 后置图标（可选）
    trailingIcon: (@Composable (() -> Unit))? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        visualTransformation = visualTransformation,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        label = {
            Text(text = label)
        },
        placeholder = placeholder?.let {
            {
                Text(text = it)
            }
        },
        // 错误提示
        supportingText = errorText?.let {
            {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        isError = errorText != null,
        enabled = enabled,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = MaterialTheme.shapes.small
    )
}

@Preview(showBackground = true)
@Composable
private fun KikoTextFieldPreview() {
    KikoTheme {
        KikoTextField(
            value = "",
            onValueChange = {},
            label = "昵称",
            placeholder = "请输入昵称"
        )
    }
}