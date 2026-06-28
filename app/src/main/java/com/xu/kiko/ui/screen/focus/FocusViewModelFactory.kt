package com.xu.kiko.ui.screen.focus

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.domain.repository.FocusSessionRepository
import com.xu.kiko.domain.repository.TaskRepository
import com.xu.kiko.notification.FocusNotificationCoordinator
import com.xu.kiko.domain.usecase.task.ObserveTodayTaskUseCase

class FocusViewModelFactory(
    private val taskRepository: TaskRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val focusNotificationCoordinator: FocusNotificationCoordinator
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FocusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FocusViewModel(
                observeTodayTask = ObserveTodayTaskUseCase(taskRepository),
                taskRepository = taskRepository,
                focusSessionRepository = focusSessionRepository,
                focusNotificationCoordinator = focusNotificationCoordinator
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }

    companion object {
        fun fromContext(
            context: Context,
            currentUserId: String
        ): FocusViewModelFactory {
            return FocusViewModelFactory(
                taskRepository = AppDependencies.taskRepository(
                    context = context,
                    currentUserId = currentUserId
                ),
                focusSessionRepository =
                AppDependencies.focusSessionRepository(
                    context = context,
                    currentUserId = currentUserId
                ),
                focusNotificationCoordinator =
                AppDependencies.focusNotificationCoordinator(
                    context = context,
                    currentUserId = currentUserId
                )
            )
        }
    }
}
