package com.xu.kiko.domain.model

data class FocusSession(
    val id: String,
    val userId: String,
    val taskId: String,
    val plannedDurationSeconds: Long,
    val focusedDurationSeconds: Long,
    val startedAtEpochMillis: Long,
    val lastStartedAtEpochMillis: Long?,
    val endedAtEpochMillis: Long?,
    val status: FocusSessionStatus
)

enum class FocusSessionStatus {
    RUNNING,
    PAUSED,
    COMPLETED,
    CANCELLED
}

data class FocusTodaySummary(
    val completedPomodoros: Int,
    val focusedMinutes: Int
)

data class FocusHeatmapDay(
    val dateEpochMillis: Long,
    val completedPomodoros: Int,
    val isFuture: Boolean
)
