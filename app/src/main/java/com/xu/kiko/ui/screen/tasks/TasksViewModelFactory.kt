package com.xu.kiko.ui.screen.tasks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.domain.repository.TaskRepository
import com.xu.kiko.domain.usecase.task.ValidateTaskUseCase

class TasksViewModelFactory(
    private val taskRepository: TaskRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(
                taskRepository = taskRepository,
                validateTask = ValidateTaskUseCase()
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
        ): TasksViewModelFactory {
            return TasksViewModelFactory(
                AppDependencies.taskRepository(
                    context = context,
                    currentUserId = currentUserId
                )
            )
        }
    }
}
