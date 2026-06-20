package com.xu.kiko.data.mapper

import com.xu.kiko.data.local.entity.TaskEntity
import com.xu.kiko.domain.model.Task
import com.xu.kiko.domain.model.TaskCategory

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        note = note,
        category = category.toTaskCategory(),
        estimatedPomodoros = estimatedPomodoros,
        completedPomodoros = completedPomodoros,
        isCompleted = isCompleted,
        createdAtEpochMillis = createdAtEpochMillis,
        completedAtEpochMillis = completedAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis
    )
}

fun Task.toEntity(userId: String): TaskEntity {
    return TaskEntity(
        id = id,
        userId = userId,
        title = title,
        note = note,
        category = category.name,
        estimatedPomodoros = estimatedPomodoros,
        completedPomodoros = completedPomodoros,
        isCompleted = isCompleted,
        createdAtEpochMillis = createdAtEpochMillis,
        completedAtEpochMillis = completedAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis
    )
}

private fun String.toTaskCategory(): TaskCategory {
    return runCatching {
        TaskCategory.valueOf(this)
    }.getOrDefault(TaskCategory.STUDY)
}
