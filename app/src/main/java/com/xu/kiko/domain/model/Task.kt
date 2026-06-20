package com.xu.kiko.domain.model

data class Task(
    val id: String,
    val title: String,
    val note: String?,
    val category: TaskCategory,
    val estimatedPomodoros: Int,
    val completedPomodoros: Int,
    val isCompleted: Boolean,
    val createdAtEpochMillis: Long,
    val completedAtEpochMillis: Long?,
    val updatedAtEpochMillis: Long
)
