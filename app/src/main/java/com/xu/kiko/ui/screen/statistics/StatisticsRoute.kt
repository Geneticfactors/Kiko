package com.xu.kiko.ui.screen.statistics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StatisticsRoute(
    currentUserId: String,
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModelFactory.fromContext(
            context = LocalContext.current,
            currentUserId = currentUserId
        )
    )
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    StatisticsScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

