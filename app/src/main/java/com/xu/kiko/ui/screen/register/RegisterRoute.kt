package com.xu.kiko.ui.screen.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterRoute(
    onNavigateBack: () -> Unit,
    onRegistered: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory.fromContext(
            LocalContext.current
        )
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                RegisterUiEffect.NavigateBack -> {
                    onNavigateBack()
                }
                RegisterUiEffect.Registered -> {
                    onRegistered()
                }
            }
        }
    }
    RegisterScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
