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

@Composable
fun KikoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorText: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: (@Composable (() -> Unit))? = null,
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