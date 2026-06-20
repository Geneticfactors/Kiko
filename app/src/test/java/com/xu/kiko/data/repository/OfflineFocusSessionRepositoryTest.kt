package com.xu.kiko.data.repository

import com.xu.kiko.data.local.dao.FocusSessionDao
import com.xu.kiko.data.local.entity.FocusSessionEntity
import com.xu.kiko.domain.model.FocusSessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OfflineFocusSessionRepositoryTest {

    @Test
    fun startSessionInsertsRunningSession() = runBlocking {
        val dao = FakeFocusSessionDao()
        val repository = repository(dao = dao)

        val session = repository.startSession(
            taskId = "task-1",
            durationSeconds = 1_500L
        )

        assertEquals(FocusSessionStatus.RUNNING, session.status)
        assertEquals(
            FocusSessionStatus.RUNNING.name,
            dao.getActiveSession(USER_ID)?.status
        )
    }

    @Test
    fun pauseAndResumeUpdateActiveSession() = runBlocking {
        val dao = FakeFocusSessionDao()
        val clock = FakeClock()
        val repository = repository(
            dao = dao,
            clock = clock
        )
        val session = repository.startSession(
            taskId = "task-1",
            durationSeconds = 1_500L
        )

        repository.pauseSession(
            sessionId = session.id,
            focusedSeconds = 60L
        )

        val paused = dao.getActiveSession(USER_ID)
        assertEquals(FocusSessionStatus.PAUSED.name, paused?.status)
        assertEquals(60L, paused?.focusedDurationSeconds)
        assertNull(paused?.lastStartedAtEpochMillis)

        clock.now = 2_000L
        repository.resumeSession(session.id)

        val resumed = dao.getActiveSession(USER_ID)
        assertEquals(FocusSessionStatus.RUNNING.name, resumed?.status)
        assertEquals(2_000L, resumed?.lastStartedAtEpochMillis)
    }

    @Test
    fun completedAndCancelledSessionsContributeToTodaySummary() =
        runBlocking {
            val dao = FakeFocusSessionDao()
            val repository = repository(dao = dao)
            val completed = repository.startSession(
                taskId = "task-1",
                durationSeconds = 1_500L
            )
            repository.completeSession(
                sessionId = completed.id,
                focusedSeconds = 1_500L
            )
            val cancelled = repository.startSession(
                taskId = "task-2",
                durationSeconds = 1_500L
            )
            repository.cancelSession(
                sessionId = cancelled.id,
                focusedSeconds = 300L
            )

            val summary = repository.observeTodaySummary().first()

            assertEquals(1, summary.completedPomodoros)
            assertEquals(30, summary.focusedMinutes)
        }

    @Test
    fun heatmapCountsCompletedPomodorosByNaturalWeekDays() =
        runBlocking {
            val dao = FakeFocusSessionDao()
            val repository = repository(
                dao = dao,
                clock = FakeClock(epochMillis(2026, 6, 20))
            )
            dao.insert(
                session(
                    id = "completed-1",
                    startedAt = epochMillis(2026, 6, 20),
                    status = FocusSessionStatus.COMPLETED
                )
            )
            dao.insert(
                session(
                    id = "completed-2",
                    startedAt = epochMillis(2026, 6, 20, hour = 12),
                    status = FocusSessionStatus.COMPLETED
                )
            )
            dao.insert(
                session(
                    id = "cancelled",
                    startedAt = epochMillis(2026, 6, 20, hour = 13),
                    status = FocusSessionStatus.CANCELLED
                )
            )
            dao.insert(
                session(
                    id = "out-of-range",
                    startedAt = epochMillis(2026, 5, 24),
                    status = FocusSessionStatus.COMPLETED
                )
            )
            dao.insert(
                session(
                    id = "future",
                    startedAt = epochMillis(2026, 6, 21),
                    status = FocusSessionStatus.COMPLETED
                )
            )

            val days = repository.observeHeatmapDays().first()
            val today = days.first { day ->
                day.dateEpochMillis == epochMillis(2026, 6, 20)
            }
            val future = days.first { day ->
                day.dateEpochMillis == epochMillis(2026, 6, 21)
            }

            assertEquals(28, days.size)
            assertEquals(epochMillis(2026, 5, 25), days.first().dateEpochMillis)
            assertEquals(2, today.completedPomodoros)
            assertEquals(false, today.isFuture)
            assertEquals(0, future.completedPomodoros)
            assertEquals(true, future.isFuture)
        }

    private fun repository(
        dao: FakeFocusSessionDao,
        clock: FakeClock = FakeClock()
    ): OfflineFocusSessionRepository {
        return OfflineFocusSessionRepository(
            focusSessionDao = dao,
            currentUserIdProvider = { USER_ID },
            nowProvider = { clock.now }
        )
    }

    private class FakeClock(
        var now: Long = 1_000L
    )

    private class FakeFocusSessionDao : FocusSessionDao {
        private val sessions = mutableListOf<FocusSessionEntity>()
        private val endedSessions =
            MutableStateFlow<List<FocusSessionEntity>>(emptyList())

        override suspend fun getActiveSession(
            userId: String
        ): FocusSessionEntity? {
            return sessions
                .filter { session ->
                    session.userId == userId &&
                        session.status in setOf(
                            FocusSessionStatus.RUNNING.name,
                            FocusSessionStatus.PAUSED.name
                        )
                }
                .maxByOrNull { session ->
                    session.startedAtEpochMillis
                }
        }

        override fun observeTodayEndedSessions(
            userId: String,
            startOfDayEpochMillis: Long,
            endOfDayEpochMillis: Long
        ): Flow<List<FocusSessionEntity>> {
            return endedSessions
        }

        override fun observeEndedSessionsInRange(
            userId: String,
            startEpochMillis: Long,
            endEpochMillis: Long
        ): Flow<List<FocusSessionEntity>> {
            return endedSessions
        }

        override fun observeCompletedSessionsInRange(
            userId: String,
            startEpochMillis: Long,
            endEpochMillis: Long
        ): Flow<List<FocusSessionEntity>> {
            return endedSessions.map { sessions ->
                sessions.filter { session ->
                    session.userId == userId &&
                        session.startedAtEpochMillis >= startEpochMillis &&
                        session.startedAtEpochMillis < endEpochMillis &&
                        session.status == FocusSessionStatus.COMPLETED.name
                }
            }
        }

        override fun observeCompletedSessionsBefore(
            userId: String,
            endEpochMillis: Long
        ): Flow<List<FocusSessionEntity>> {
            return endedSessions
        }

        override suspend fun insert(entity: FocusSessionEntity) {
            sessions += entity
            publishEndedSessions()
        }

        override suspend fun update(entity: FocusSessionEntity) {
            val index = sessions.indexOfFirst { session ->
                session.id == entity.id
            }
            if (index >= 0) {
                sessions[index] = entity
            }
            publishEndedSessions()
        }

        private fun publishEndedSessions() {
            endedSessions.value = sessions.filter { session ->
                session.status in setOf(
                    FocusSessionStatus.COMPLETED.name,
                    FocusSessionStatus.CANCELLED.name
                )
            }
        }
    }

    private fun session(
        id: String,
        startedAt: Long,
        status: FocusSessionStatus,
        userId: String = USER_ID
    ): FocusSessionEntity {
        return FocusSessionEntity(
            id = id,
            userId = userId,
            taskId = "task",
            plannedDurationSeconds = 1_500L,
            focusedDurationSeconds = 1_500L,
            startedAtEpochMillis = startedAt,
            lastStartedAtEpochMillis = null,
            endedAtEpochMillis = startedAt + 1_500_000L,
            status = status.name
        )
    }

    private fun epochMillis(
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 0
    ): Long {
        return java.util.Calendar.getInstance().apply {
            firstDayOfWeek = java.util.Calendar.MONDAY
            set(java.util.Calendar.YEAR, year)
            set(java.util.Calendar.MONTH, month - 1)
            set(java.util.Calendar.DAY_OF_MONTH, day)
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private companion object {
        const val USER_ID = "user-1"
    }
}
