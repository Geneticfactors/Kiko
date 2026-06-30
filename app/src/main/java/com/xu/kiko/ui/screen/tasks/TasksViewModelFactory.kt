package com.xu.kiko.ui.screen.tasks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.domain.repository.TaskRepository
import com.xu.kiko.domain.usecase.task.ValidateTaskUseCase

/**
 * 任务页面 ViewModel 工厂类
 * 负责创建 [TasksViewModel] 并注入所需依赖
 */
class TasksViewModelFactory(
    // 任务仓库
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
        /**
         * 从 [Context] 创建 [TasksViewModelFactory]
         * 通过 [AppDependencies] 获取所需依赖
         */
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
