package com.xu.kiko.domain.usecase.task

import com.xu.kiko.domain.model.Task
import com.xu.kiko.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class ObserveTodayTaskUseCase(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(limit: Int = 3): Flow<List<Task>> {
        return taskRepository.observeTodayTasks(limit)
    }
}
