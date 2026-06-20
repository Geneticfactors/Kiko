package com.xu.kiko.ui.screen.tasks

sealed interface TasksUiEffect {
    data class ShowMessage(
        val message: String
    ) : TasksUiEffect
}