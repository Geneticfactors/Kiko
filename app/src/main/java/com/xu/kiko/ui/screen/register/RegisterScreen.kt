package com.xu.kiko.ui.screen.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.domain.repository.AuthError
import com.xu.kiko.ui.component.KikoTextField
import com.xu.kiko.ui.component.PasswordTextField
import com.xu.kiko.ui.component.PrimaryButton
import com.xu.kiko.ui.screen.auth.AuthFieldError
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onAction: (RegisterUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onAction(RegisterUiAction.NavigateBack)
                        },
                        enabled = !state.isSubmitting
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                R.string.register_back
                            )
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = MaterialTheme.spacing.formHorizontal,
                    vertical = MaterialTheme.spacing.large
                )
        ) {
            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Text(
                text = stringResource(R.string.register_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.section))

            KikoTextField(
                value = state.nickname,
                onValueChange = {
                    onAction(RegisterUiAction.NicknameChanged(it))
                },
                label = stringResource(R.string.register_nickname_label),
                placeholder = stringResource(
                    R.string.register_nickname_placeholder
                ),
                errorText = authErrorMessage(state.nicknameError),
                enabled = !state.isSubmitting,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            KikoTextField(
                value = state.phone,
                onValueChange = {
                    onAction(RegisterUiAction.PhoneChanged(it))
                },
                label = stringResource(R.string.register_phone_label),
                placeholder = stringResource(
                    R.string.register_phone_placeholder
                ),
                errorText = authErrorMessage(state.phoneError),
                enabled = !state.isSubmitting,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            PasswordTextField(
                value = state.password,
                onValueChange = {
                    onAction(RegisterUiAction.PasswordChanged(it))
                },
                label = stringResource(R.string.register_password_label),
                passwordVisible = state.isPasswordVisible,
                onPasswordVisibilityChange = {
                    onAction(RegisterUiAction.TogglePasswordVisibility)
                },
                errorText = authErrorMessage(state.passwordError),
                enabled = !state.isSubmitting,
                keyboardOption = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            PasswordTextField(
                value = state.confirmPassword,
                onValueChange = {
                    onAction(RegisterUiAction.ConfirmPasswordChanged(it))
                },
                label = stringResource(
                    R.string.register_confirm_password_label
                ),
                passwordVisible = state.isConfirmPasswordVisible,
                onPasswordVisibilityChange = {
                    onAction(
                        RegisterUiAction.ToggleConfirmPasswordVisibility
                    )
                },
                errorText = authErrorMessage(state.confirmPasswordError),
                enabled = !state.isSubmitting,
                keyboardOption = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        onAction(RegisterUiAction.Submit)
                    }
                )
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

            Text(
                text = stringResource(R.string.register_password_hint),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.section))

            state.authError?.let { error ->
                Text(
                    text = authErrorMessage(error),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(
                    modifier = Modifier.height(
                        MaterialTheme.spacing.small
                    )
                )
            }

            PrimaryButton(
                text = stringResource(R.string.register_submit),
                onClick = { onAction(RegisterUiAction.Submit) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
                loading = state.isSubmitting
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = stringResource(
                    R.string.register_local_account_notice
                ),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun authErrorMessage(error: AuthError): String {
    return when (error) {
        AuthError.PHONE_ALREADY_REGISTERED ->
            stringResource(R.string.auth_error_phone_registered)

        AuthError.INVALID_CREDENTIALS ->
            stringResource(R.string.auth_error_unknown)

        AuthError.UNKNOWN ->
            stringResource(R.string.auth_error_unknown)
    }
}

@Composable
private fun authErrorMessage(error: AuthFieldError?): String? {
    return when (error) {
        AuthFieldError.REQUIRED ->
            stringResource(R.string.auth_error_required)

        AuthFieldError.INVALID_LENGTH ->
            stringResource(R.string.auth_error_nickname_length)

        AuthFieldError.INVALID_PHONE ->
            stringResource(R.string.auth_error_phone)

        AuthFieldError.PASSWORD_TOO_SHORT ->
            stringResource(R.string.auth_error_password_short)

        AuthFieldError.PASSWORD_MISMATCH ->
            stringResource(R.string.auth_error_password_mismatch)

        null -> null
    }
}

@Preview(
    name = "Register - Default",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun RegisterScreenDefaultPreview() {
    KikoTheme {
        RegisterScreen(
            state = RegisterUiState(),
            onAction = {}
        )
    }
}

@Preview(
    name = "Register - Error",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun RegisterScreenErrorPreview() {
    KikoTheme {
        RegisterScreen(
            state = RegisterUiState(
                nickname = "A",
                phone = "123",
                password = "123",
                confirmPassword = "456",
                nicknameError = AuthFieldError.INVALID_LENGTH,
                phoneError = AuthFieldError.INVALID_PHONE,
                passwordError = AuthFieldError.PASSWORD_TOO_SHORT,
                confirmPasswordError =
                    AuthFieldError.PASSWORD_MISMATCH
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Register - Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun RegisterScreenDarkPreview() {
    KikoTheme(darkTheme = true) {
        RegisterScreen(
            state = RegisterUiState(
                nickname = "Kiko",
                phone = "13800138000",
                password = "password",
                confirmPassword = "password"
            ),
            onAction = {}
        )
    }
}
