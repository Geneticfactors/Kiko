package com.xu.kiko.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["userId", "category"]),
        Index(value = ["userId", "createdAtEpochMillis"])
    ]
)
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val note: String?,
    val category: String,
    val estimatedPomodoros: Int,
    val completedPomodoros: Int,
    val isCompleted: Boolean,
    val createdAtEpochMillis: Long,
    val completedAtEpochMillis: Long?,
    val updatedAtEpochMillis: Long
)
