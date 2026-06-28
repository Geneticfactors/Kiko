package com.xu.kiko.domain.repository

import com.xu.kiko.domain.model.Task
import com.xu.kiko.domain.model.TaskCategory
import com.xu.kiko.domain.usecase.task.TaskInput
import kotlinx.coroutines.flow.Flow

data class TaskQuery(
    val category: TaskCategory? = null
)

interface TaskRepository {
    fun observeTasks(
        query: TaskQuery = TaskQuery()
    ): Flow<List<Task>>

    fun observeTodayTasks(
        limit: Int = 3
    ): Flow<List<Task>>

    suspend fun getTodayTasks(
        limit: Int = 3
    ): List<Task>

    suspend fun saveTask(
        taskId: String?,
        input: TaskInput
    )

    suspend fun setTaskCompleted(
        taskId: String,
        completed: Boolean
    )

    suspend fun incrementCompletedPomodoros(taskId: String)

    suspend fun deleteTask(taskId: String)
}
