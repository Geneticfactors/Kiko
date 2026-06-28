package com.xu.kiko.domain.repository

import com.xu.kiko.domain.model.FocusSession
import com.xu.kiko.domain.model.FocusHeatmapDay
import com.xu.kiko.domain.model.FocusTodaySummary
import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    suspend fun startSession(
        taskId: String,
        durationSeconds: Long
    ): FocusSession

    suspend fun pauseSession(
        sessionId: String,
        focusedSeconds: Long
    ): Boolean

    suspend fun resumeSession(sessionId: String): Boolean

    suspend fun completeSession(
        sessionId: String,
        focusedSeconds: Long
    ): Boolean

    suspend fun cancelSession(
        sessionId: String,
        focusedSeconds: Long
    ): Boolean

    suspend fun getActiveSession(): FocusSession?

    fun observeTodaySummary(): Flow<FocusTodaySummary>

    fun observeHeatmapDays(): Flow<List<FocusHeatmapDay>>
}
