package com.xu.kiko.ui.screen.tasks

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TasksRoute(
    currentUserId: String,
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = viewModel(
        factory = TasksViewModelFactory.fromContext(
            context = LocalContext.current,
            currentUserId = currentUserId
        )
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is TasksUiEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    TasksScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
        snackbarHostState = snackbarHostState
    )
}
