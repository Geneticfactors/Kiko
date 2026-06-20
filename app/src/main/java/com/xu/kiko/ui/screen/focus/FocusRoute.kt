package com.xu.kiko.ui.screen.focus

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FocusRoute(
    currentUserId: String,
    modifier: Modifier = Modifier,
    onNavigateToTasks: () -> Unit = {},
    viewModel: FocusViewModel = viewModel(
        factory = FocusViewModelFactory.fromContext(
            context = LocalContext.current,
            currentUserId = currentUserId
        )
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                FocusUiEffect.NavigateToTasks -> onNavigateToTasks()
            }
        }
    }

    FocusScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
