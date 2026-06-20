package com.xu.kiko.ui.screen.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginRoute(
    onOpenRegister: () -> Unit,
    onLoggedIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory.fromContext(LocalContext.current)
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when(effect) {
                LoginUiEffect.NavigateToRegister ->{
                    onOpenRegister()
                }

                LoginUiEffect.LoggedIn -> {
                    onLoggedIn()
                }
            }
        }
    }

    LoginScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
