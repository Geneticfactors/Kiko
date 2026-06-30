package com.xu.kiko.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.domain.repository.AuthError
import com.xu.kiko.ui.component.KikoTextField
import com.xu.kiko.ui.component.PasswordTextField
import com.xu.kiko.ui.component.PrimaryButton
import com.xu.kiko.ui.screen.auth.AuthFieldError
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

/**
 * 登录页面主组件
 * 包含品牌头部、手机号输入框、密码输入框、登录按钮、注册入口等
 */
@Composable
fun LoginScreen(
    // 登录页面 UI 状态
    state: LoginUiState,

    // 用户操作回调
    onAction: (LoginUiAction) -> Unit,

    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = MaterialTheme.spacing.formHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 品牌头部（Logo + 应用名 + 标语）
            LoginBrandHeader()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.section))

            // 手机号输入框
            KikoTextField(
                value = state.phone,
                onValueChange = { onAction(LoginUiAction.PhoneChanged(it)) },
                label = stringResource(R.string.login_phone_label),
                placeholder = stringResource(R.string.login_phone_placeholder),
                errorText = authErrorMessage(state.phoneError),
                enabled = !state.isSubmitting,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            // 密码输入框
            PasswordTextField(
                value = state.password,
                onValueChange = { onAction(LoginUiAction.PasswordChanged(it)) },
                label = stringResource(R.string.login_password_label),
                passwordVisible = state.isPasswordVisible,
                onPasswordVisibilityChange = { onAction(LoginUiAction.TogglePasswordVisibility) },
                errorText = authErrorMessage(state.passwordError),
                enabled = !state.isSubmitting,
                keyboardOption = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onAction(LoginUiAction.Submit)
                        keyboardController?.hide()
                    }
                )
            )

            // 忘记密码链接
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    onClick = { onAction(LoginUiAction.OpenForgotPassword) },
                    enabled = !state.isSubmitting
                ) {
                    Text(text = stringResource(R.string.login_forgot_password))
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            // 认证错误提示
            state.authError?.let { error ->
                Text(
                    text = authErrorMessage(error),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
            }

            // 登录按钮
            PrimaryButton(
                text = stringResource(R.string.login_submit),
                onClick = { onAction(LoginUiAction.Submit) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
                loading = state.isSubmitting
            )

            // 注册入口
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.login_no_account),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = { onAction(LoginUiAction.OpenRegister) },
                    enabled = !state.isSubmitting
                ) {
                    Text(text = stringResource(R.string.login_register_now))
                }
            }

            // 本地账号提示
            Text(
                text = stringResource(R.string.login_local_account_notice),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        // 忘记密码对话框
        if (state.isForgotPasswordDialogVisible) {
            AlertDialog(
                onDismissRequest = { onAction(LoginUiAction.CloseForgotPassword) },
                title = { Text(text = stringResource(R.string.login_forgot_dialog_title)) },
                text = { Text(text = stringResource(R.string.login_forgot_dialog_message)) },
                confirmButton = {
                    TextButton(onClick = { onAction(LoginUiAction.CloseForgotPassword) }) {
                        Text(text = stringResource(R.string.login_forgot_dialog_confirm))
                    }
                }
            )
        }
    }
}

/**
 * 登录页面品牌头部组件
 * 显示应用 Logo、名称和标语
 */
@Composable
private fun LoginBrandHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(R.drawable.ic_kiko_brand),
            contentDescription = null,
            modifier = Modifier.size(96.dp)
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        Text(
            text = stringResource(R.string.login_brand_name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
        Text(
            text = stringResource(R.string.login_slogan),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 将 [AuthError] 转换为用户友好的错误提示文本
 */
@Composable
private fun authErrorMessage(error: AuthError): String {
    return when (error) {
        AuthError.INVALID_CREDENTIALS -> stringResource(R.string.auth_error_invalid_credentials)
        AuthError.PHONE_ALREADY_REGISTERED -> stringResource(R.string.auth_error_unknown)
        AuthError.UNKNOWN -> stringResource(R.string.auth_error_unknown)
    }
}

/**
 * 将 [AuthFieldError] 转换为用户友好的错误提示文本
 */
@Composable
private fun authErrorMessage(error: AuthFieldError?): String? {
    return when (error) {
        AuthFieldError.REQUIRED -> stringResource(R.string.auth_error_required)
        AuthFieldError.INVALID_LENGTH -> stringResource(R.string.auth_error_nickname_length)
        AuthFieldError.INVALID_PHONE -> stringResource(R.string.auth_error_phone)
        AuthFieldError.PASSWORD_TOO_SHORT -> stringResource(R.string.auth_error_password_short)
        AuthFieldError.PASSWORD_MISMATCH -> stringResource(R.string.auth_error_password_mismatch)
        null -> null
    }
}

@Preview(
    name = "Login - Default",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun LoginScreenDefaultPreview() {
    KikoTheme {
        LoginScreen(
            state = LoginUiState(),
            onAction = {}
        )
    }
}

@Preview(
    name = "Login - Error",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun LoginScreenErrorPreview() {
    KikoTheme {
        LoginScreen(
            state = LoginUiState(
                phone = "123",
                password = "123",
                phoneError = AuthFieldError.INVALID_PHONE,
                passwordError = AuthFieldError.PASSWORD_TOO_SHORT
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Login - Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun LoginScreenDarkPreview() {
    KikoTheme(darkTheme = true) {
        LoginScreen(
            state = LoginUiState(
                phone = "13800138000",
                password = "password"
            ),
            onAction = {}
        )
    }
}
