package com.xu.kiko.domain.usecase.task

import com.xu.kiko.domain.repository.TaskRepository

class SaveTaskUseCase(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(
        taskId: String?,
        input: TaskInput
    ) {
        taskRepository.saveTask(
            taskId = taskId,
            input = input
        )
    }
}
