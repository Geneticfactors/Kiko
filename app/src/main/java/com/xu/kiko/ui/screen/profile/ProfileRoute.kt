package com.xu.kiko.ui.screen.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileRoute(
    currentUserId: String,
    modifier: Modifier = Modifier,
    onNavigateToNotificationSettings: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory.fromContext(
            context = LocalContext.current,
            currentUserId = currentUserId
        )
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ProfileUiEffect.NavigateToNotificationSettings ->
                    onNavigateToNotificationSettings()

                ProfileUiEffect.LoggedOut ->
                    onLoggedOut()
            }
        }
    }

    ProfileScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

