package com.xu.kiko.data.repository

import com.xu.kiko.data.local.dao.TaskDao
import com.xu.kiko.data.local.entity.TaskEntity
import com.xu.kiko.data.mapper.toDomain
import com.xu.kiko.domain.repository.TaskQuery
import com.xu.kiko.domain.model.Task
import com.xu.kiko.domain.repository.TaskRepository
import com.xu.kiko.domain.usecase.task.TaskInput
import java.util.Calendar
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineTaskRepository(
    private val taskDao: TaskDao,
    private val currentUserIdProvider: () -> String = { LOCAL_USER_ID },
    private val nowProvider: () -> Long = { System.currentTimeMillis() }
) : TaskRepository {

    override fun observeTasks(query: TaskQuery): Flow<List<Task>> {
        val userId = currentUserIdProvider()
        val entities = query.category?.let { category ->
            taskDao.observeByCategory(
                userId = userId,
                category = category.name
            )
        } ?: taskDao.observeAll(userId)

        return entities.map { tasks ->
            tasks.map { entity -> entity.toDomain() }
        }
    }

    override fun observeTodayTasks(limit: Int): Flow<List<Task>> {
        val userId = currentUserIdProvider()
        val startOfDay = startOfCurrentDayEpochMillis()
        val endOfDay = startOfDay + MILLIS_PER_DAY

        return taskDao.observeToday(
            userId = userId,
            startOfDayEpochMillis = startOfDay,
            endOfDayEpochMillis = endOfDay,
            limit = limit.coerceAtLeast(1)
        ).map { tasks ->
            tasks.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun getTodayTasks(limit: Int): List<Task> {
        val userId = currentUserIdProvider()
        val startOfDay = startOfCurrentDayEpochMillis()
        val endOfDay = startOfDay + MILLIS_PER_DAY

        return taskDao.getToday(
            userId = userId,
            startOfDayEpochMillis = startOfDay,
            endOfDayEpochMillis = endOfDay,
            limit = limit.coerceAtLeast(1)
        ).map { entity -> entity.toDomain() }
    }

    override suspend fun saveTask(
        taskId: String?,
        input: TaskInput
    ) {
        val now = nowProvider()
        val normalizedTitle = input.title.trim()
        val normalizedNote = input.note.trim().ifEmpty { null }

        if (taskId == null) {
            taskDao.insert(
                TaskEntity(
                    id = UUID.randomUUID().toString(),
                    userId = currentUserIdProvider(),
                    title = normalizedTitle,
                    note = normalizedNote,
                    category = input.category.name,
                    estimatedPomodoros = input.estimatedPomodoros,
                    completedPomodoros = 0,
                    isCompleted = false,
                    createdAtEpochMillis = now,
                    completedAtEpochMillis = null,
                    updatedAtEpochMillis = now
                )
            )
            return
        }

        val current = taskDao.getById(
            id = taskId,
            userId = currentUserIdProvider()
        ) ?: return
        taskDao.update(
            current.copy(
                title = normalizedTitle,
                note = normalizedNote,
                category = input.category.name,
                estimatedPomodoros = input.estimatedPomodoros,
                updatedAtEpochMillis = now
            )
        )
    }

    override suspend fun setTaskCompleted(
        taskId: String,
        completed: Boolean
    ) {
        val now = nowProvider()
        taskDao.setCompleted(
            id = taskId,
            userId = currentUserIdProvider(),
            completed = completed,
            completedAtEpochMillis = if (completed) now else null,
            updatedAtEpochMillis = now
        )
    }

    override suspend fun incrementCompletedPomodoros(taskId: String) {
        taskDao.incrementCompletedPomodoros(
            id = taskId,
            userId = currentUserIdProvider(),
            updatedAtEpochMillis = nowProvider()
        )
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteById(
            id = taskId,
            userId = currentUserIdProvider()
        )
    }

    private fun startOfCurrentDayEpochMillis(): Long {
        return Calendar.getInstance().apply {
            timeInMillis = nowProvider()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private companion object {
        const val LOCAL_USER_ID = "local_user"
        const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    }
}
