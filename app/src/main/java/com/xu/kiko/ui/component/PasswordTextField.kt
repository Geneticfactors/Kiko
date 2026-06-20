package com.xu.kiko.ui.component


import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.ui.theme.KikoTheme

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    enabled: Boolean = true,
    keyboardOption: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Password
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    KikoTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        errorText = errorText,
        enabled = enabled,
        keyboardOptions = keyboardOption,
        keyboardActions = keyboardActions,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        }else{
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    onPasswordVisibilityChange(!passwordVisible)
                },
                modifier = Modifier.sizeIn(
                    minWidth = 48.dp,
                    minHeight = 48.dp
                )
            ) {
                Icon(
                    imageVector = if (passwordVisible){
                        Icons.Default.VisibilityOff
                    }else{
                        Icons.Default.Visibility
                    },
                    contentDescription = if (passwordVisible){
                        stringResource(R.string.auth_hide_password)
                    }else{
                        stringResource(R.string.auth_show_password)
                    }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun PasswordTextFieldPreview() {
    KikoTheme {
        PasswordTextField(
            value = "12345678",
            onValueChange = {},
            label = "密码",
            passwordVisible = false,
            onPasswordVisibilityChange = {}
        )
    }
}
