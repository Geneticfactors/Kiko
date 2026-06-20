package com.xu.kiko.domain.usecase.task

import com.xu.kiko.domain.model.Task
import com.xu.kiko.domain.repository.TaskQuery
import com.xu.kiko.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class ObserveTasksUseCase(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(query: TaskQuery = TaskQuery()): Flow<List<Task>> {
        return taskRepository.observeTasks(query)
    }
}
