package com.xu.kiko.data.mapper

import com.xu.kiko.data.local.entity.FocusSessionEntity
import com.xu.kiko.domain.model.FocusSession
import com.xu.kiko.domain.model.FocusSessionStatus

fun FocusSessionEntity.toDomain(): FocusSession {
    return FocusSession(
        id = id,
        userId = userId,
        taskId = taskId,
        plannedDurationSeconds = plannedDurationSeconds,
        focusedDurationSeconds = focusedDurationSeconds,
        startedAtEpochMillis = startedAtEpochMillis,
        lastStartedAtEpochMillis = lastStartedAtEpochMillis,
        endedAtEpochMillis = endedAtEpochMillis,
        status = status.toFocusSessionStatus()
    )
}

private fun String.toFocusSessionStatus(): FocusSessionStatus {
    return runCatching {
        FocusSessionStatus.valueOf(this)
    }.getOrDefault(FocusSessionStatus.CANCELLED)
}
