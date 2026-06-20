package com.xu.kiko.data.repository

import com.xu.kiko.data.local.dao.FocusSessionDao
import com.xu.kiko.data.local.entity.FocusSessionEntity
import com.xu.kiko.data.mapper.toDomain
import com.xu.kiko.domain.model.FocusHeatmapDay
import com.xu.kiko.domain.model.FocusSession
import com.xu.kiko.domain.model.FocusSessionStatus
import com.xu.kiko.domain.model.FocusTodaySummary
import com.xu.kiko.domain.repository.FocusSessionRepository
import java.util.Calendar
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineFocusSessionRepository(
    private val focusSessionDao: FocusSessionDao,
    private val currentUserIdProvider: () -> String,
    private val nowProvider: () -> Long = { System.currentTimeMillis() }
) : FocusSessionRepository {

    override suspend fun startSession(
        taskId: String,
        durationSeconds: Long
    ): FocusSession {
        val now = nowProvider()
        val entity = FocusSessionEntity(
            id = UUID.randomUUID().toString(),
            userId = currentUserIdProvider(),
            taskId = taskId,
            plannedDurationSeconds = durationSeconds,
            focusedDurationSeconds = 0L,
            startedAtEpochMillis = now,
            lastStartedAtEpochMillis = now,
            endedAtEpochMillis = null,
            status = FocusSessionStatus.RUNNING.name
        )
        focusSessionDao.insert(entity)
        return entity.toDomain()
    }

    override suspend fun pauseSession(
        sessionId: String,
        focusedSeconds: Long
    ) {
        updateActiveSession(sessionId) { session ->
            session.copy(
                focusedDurationSeconds = focusedSeconds,
                lastStartedAtEpochMillis = null,
                status = FocusSessionStatus.PAUSED.name
            )
        }
    }

    override suspend fun resumeSession(sessionId: String) {
        updateActiveSession(sessionId) { session ->
            session.copy(
                lastStartedAtEpochMillis = nowProvider(),
                status = FocusSessionStatus.RUNNING.name
            )
        }
    }

    override suspend fun completeSession(
        sessionId: String,
        focusedSeconds: Long
    ) {
        endSession(
            sessionId = sessionId,
            focusedSeconds = focusedSeconds,
            status = FocusSessionStatus.COMPLETED
        )
    }

    override suspend fun cancelSession(
        sessionId: String,
        focusedSeconds: Long
    ) {
        endSession(
            sessionId = sessionId,
            focusedSeconds = focusedSeconds,
            status = FocusSessionStatus.CANCELLED
        )
    }

    override suspend fun getActiveSession(): FocusSession? {
        return focusSessionDao
            .getActiveSession(currentUserIdProvider())
            ?.toDomain()
    }

    override fun observeTodaySummary(): Flow<FocusTodaySummary> {
        val startOfDay = startOfCurrentDayEpochMillis()
        val endOfDay = startOfDay + MILLIS_PER_DAY
        return focusSessionDao.observeTodayEndedSessions(
            userId = currentUserIdProvider(),
            startOfDayEpochMillis = startOfDay,
            endOfDayEpochMillis = endOfDay
        ).map { sessions ->
            FocusTodaySummary(
                completedPomodoros = sessions.count {
                    it.status == FocusSessionStatus.COMPLETED.name
                },
                focusedMinutes = sessions
                    .sumOf { it.focusedDurationSeconds }
                    .floorDiv(60L)
                    .toInt()
            )
        }
    }

    override fun observeHeatmapDays(): Flow<List<FocusHeatmapDay>> {
        val todayStart = startOfCurrentDayEpochMillis()
        val rangeStart = startOfHeatmapRangeEpochMillis()
        val rangeEnd = rangeStart + HEATMAP_DAY_COUNT * MILLIS_PER_DAY
        val queryEnd = (todayStart + MILLIS_PER_DAY)
            .coerceAtMost(rangeEnd)

        return focusSessionDao.observeCompletedSessionsInRange(
            userId = currentUserIdProvider(),
            startEpochMillis = rangeStart,
            endEpochMillis = queryEnd
        ).map { sessions ->
            val completedByDay = sessions
                .groupingBy { session ->
                    startOfDayEpochMillis(session.startedAtEpochMillis)
                }
                .eachCount()

            List(HEATMAP_DAY_COUNT) { index ->
                val dayStart =
                    rangeStart + index * MILLIS_PER_DAY
                FocusHeatmapDay(
                    dateEpochMillis = dayStart,
                    completedPomodoros = completedByDay[dayStart] ?: 0,
                    isFuture = dayStart > todayStart
                )
            }
        }
    }

    private suspend fun endSession(
        sessionId: String,
        focusedSeconds: Long,
        status: FocusSessionStatus
    ) {
        updateActiveSession(sessionId) { session ->
            session.copy(
                focusedDurationSeconds = focusedSeconds,
                lastStartedAtEpochMillis = null,
                endedAtEpochMillis = nowProvider(),
                status = status.name
            )
        }
    }

    private suspend fun updateActiveSession(
        sessionId: String,
        transform: (FocusSessionEntity) -> FocusSessionEntity
    ) {
        val current = focusSessionDao
            .getActiveSession(currentUserIdProvider())
            ?: return

        if (current.id != sessionId) {
            return
        }

        focusSessionDao.update(transform(current))
    }

    private fun startOfCurrentDayEpochMillis(): Long {
        return startOfDayEpochMillis(nowProvider())
    }

    private fun startOfDayEpochMillis(epochMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = epochMillis
            clearTime()
        }.timeInMillis
    }

    private fun startOfHeatmapRangeEpochMillis(): Long {
        return Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            timeInMillis = nowProvider()
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            clearTime()
            add(Calendar.DAY_OF_YEAR, -DAYS_PER_WEEK * 3)
        }.timeInMillis
    }

    private fun Calendar.clearTime() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private companion object {
        const val DAYS_PER_WEEK = 7
        const val HEATMAP_DAY_COUNT = 28
        const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    }
}
