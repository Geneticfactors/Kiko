package com.xu.kiko.ui.screen.focus

sealed interface FocusUiEffect {
    data object NavigateToTasks : FocusUiEffect
}
