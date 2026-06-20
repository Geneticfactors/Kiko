package com.xu.kiko.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "focus_sessions",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["userId", "status"]),
        Index(value = ["userId", "startedAtEpochMillis"]),
        Index(value = ["userId", "taskId"])
    ]
)
data class FocusSessionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val taskId: String,
    val plannedDurationSeconds: Long,
    val focusedDurationSeconds: Long,
    val startedAtEpochMillis: Long,
    val lastStartedAtEpochMillis: Long?,
    val endedAtEpochMillis: Long?,
    val status: String
)
