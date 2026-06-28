package com.xu.kiko.data.repository

import com.xu.kiko.data.local.dao.FocusSessionDao
import com.xu.kiko.data.local.dao.TaskDao
import com.xu.kiko.data.local.entity.FocusSessionEntity
import com.xu.kiko.data.local.entity.TaskEntity
import com.xu.kiko.domain.model.FocusSessionStatus
import com.xu.kiko.domain.model.StatisticsPeriod
import com.xu.kiko.domain.model.TaskCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class OfflineStatisticsRepositoryTest {

    @Test
    fun completedAndCancelledSessionsCountTowardFocusMinutes() =
        runBlocking {
            val monday = epochMillis(2026, 6, 15)
            val repository = repository(
                now = epochMillis(2026, 6, 17),
                sessions = listOf(
                    session(
                        startedAt = monday,
                        focusedSeconds = 1_500L,
                        status = FocusSessionStatus.COMPLETED
                    ),
                    session(
                        startedAt = monday,
                        focusedSeconds = 300L,
                        status = FocusSessionStatus.CANCELLED
                    )
                )
            )

            val data = repository
                .observeStatistics(StatisticsPeriod.WEEK)
                .first()

            assertEquals(30, data.totalFocusMinutes)
            assertEquals(30, data.dailyFocusMinutes.first().valueMinutes)
            assertEquals(0, data.streakDays)
        }

    @Test
    fun completedSessionsFromTodayBackwardCountTowardStreak() =
        runBlocking {
            val today = epochMillis(2026, 6, 17)
            val yesterday = epochMillis(2026, 6, 16)
            val repository = repository(
                now = today,
                sessions = listOf(
                    session(
                        startedAt = today,
                        focusedSeconds = 1_500L,
                        status = FocusSessionStatus.COMPLETED
                    ),
                    session(
                        startedAt = yesterday,
                        focusedSeconds = 1_500L,
                        status = FocusSessionStatus.COMPLETED
                    )
                )
            )

            val data = repository
                .observeStatistics(StatisticsPeriod.WEEK)
                .first()

            assertEquals(2, data.streakDays)
        }

    @Test
    fun cancelledSessionsDoNotCountTowardStreak() = runBlocking {
        val today = epochMillis(2026, 6, 17)
        val repository = repository(
            now = today,
            sessions = listOf(
                session(
                    startedAt = today,
                    focusedSeconds = 600L,
                    status = FocusSessionStatus.CANCELLED
                )
            )
        )

        val data = repository
            .observeStatistics(StatisticsPeriod.WEEK)
            .first()

        assertEquals(0, data.streakDays)
    }

    @Test
    fun taskCompletionRateUsesCurrentRangeTasks() = runBlocking {
        val now = epochMillis(2026, 6, 17)
        val repository = repository(
            now = now,
            tasks = listOf(
                task(
                    id = "done",
                    createdAt = epochMillis(2026, 6, 16),
                    isCompleted = true
                ),
                task(
                    id = "open",
                    createdAt = epochMillis(2026, 6, 16),
                    isCompleted = false
                ),
                task(
                    id = "old",
                    createdAt = epochMillis(2026, 6, 8),
                    isCompleted = true
                )
            )
        )

        val data = repository
            .observeStatistics(StatisticsPeriod.WEEK)
            .first()

        assertEquals(50, data.taskCompletionRate)
    }

    @Test
    fun comparePercentIsOneHundredWhenPreviousIsZero() = runBlocking {
        val now = epochMillis(2026, 6, 17)
        val repository = repository(
            now = now,
            sessions = listOf(
                session(
                    startedAt = epochMillis(2026, 6, 16),
                    focusedSeconds = 600L,
                    status = FocusSessionStatus.COMPLETED
                )
            )
        )

        val data = repository
            .observeStatistics(StatisticsPeriod.WEEK)
            .first()

        assertEquals(100, data.comparePercent)
        assertFalse(data.hasData.not())
    }

    private fun repository(
        now: Long,
        sessions: List<FocusSessionEntity> = emptyList(),
        tasks: List<TaskEntity> = emptyList()
    ): OfflineStatisticsRepository {
        return OfflineStatisticsRepository(
            focusSessionDao = FakeFocusSessionDao(sessions),
            taskDao = FakeTaskDao(tasks),
            currentUserIdProvider = { USER_ID },
            nowProvider = { now }
        )
    }

    private class FakeFocusSessionDao(
        sessions: List<FocusSessionEntity>
    ) : FocusSessionDao {
        private val sessions = MutableStateFlow(sessions)

        override suspend fun getActiveSession(
            userId: String
        ): FocusSessionEntity? = null

        override fun observeTodayEndedSessions(
            userId: String,
            startOfDayEpochMillis: Long,
            endOfDayEpochMillis: Long
        ): Flow<List<FocusSessionEntity>> {
            return observeEndedSessionsInRange(
                userId = userId,
                startEpochMillis = startOfDayEpochMillis,
                endEpochMillis = endOfDayEpochMillis
            )
        }

        override fun observeEndedSessionsInRange(
            userId: String,
            startEpochMillis: Long,
            endEpochMillis: Long
        ): Flow<List<FocusSessionEntity>> {
            return sessions.map { values ->
                values.filter { session ->
                    session.userId == userId &&
                        session.startedAtEpochMillis >= startEpochMillis &&
                        session.startedAtEpochMillis < endEpochMillis &&
                        session.status in setOf(
                            FocusSessionStatus.COMPLETED.name,
                            FocusSessionStatus.CANCELLED.name
                        )
                }
            }
        }

        override fun observeCompletedSessionsInRange(
            userId: String,
            startEpochMillis: Long,
            endEpochMillis: Long
        ): Flow<List<FocusSessionEntity>> {
            return sessions.map { values ->
                values.filter { session ->
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
            return sessions.map { values ->
                values.filter { session ->
                    session.userId == userId &&
                        session.startedAtEpochMillis < endEpochMillis &&
                        session.status == FocusSessionStatus.COMPLETED.name
                }
            }
        }

        override suspend fun insert(entity: FocusSessionEntity) = Unit

        override suspend fun update(entity: FocusSessionEntity) = Unit
    }

    private class FakeTaskDao(
        tasks: List<TaskEntity>
    ) : TaskDao {
        private val tasks = MutableStateFlow(tasks)

        override fun observeAll(userId: String): Flow<List<TaskEntity>> {
            return tasks.map { values ->
                values.filter { task -> task.userId == userId }
            }
        }

        override fun observeByCategory(
            userId: String,
            category: String
        ): Flow<List<TaskEntity>> = observeAll(userId)

        override fun observeToday(
            userId: String,
            startOfDayEpochMillis: Long,
            endOfDayEpochMillis: Long,
            limit: Int
        ): Flow<List<TaskEntity>> = observeCreatedInRange(
            userId = userId,
            startEpochMillis = startOfDayEpochMillis,
            endEpochMillis = endOfDayEpochMillis
        )

        override suspend fun getToday(
            userId: String,
            startOfDayEpochMillis: Long,
            endOfDayEpochMillis: Long,
            limit: Int
        ): List<TaskEntity> {
            return tasks.value
                .filter { task ->
                    task.userId == userId &&
                        task.createdAtEpochMillis >=
                        startOfDayEpochMillis &&
                        task.createdAtEpochMillis < endOfDayEpochMillis
                }
                .sortedWith(
                    compareBy<TaskEntity> { it.isCompleted }
                        .thenByDescending { it.createdAtEpochMillis }
                )
                .take(limit)
        }

        override fun observeCreatedInRange(
            userId: String,
            startEpochMillis: Long,
            endEpochMillis: Long
        ): Flow<List<TaskEntity>> {
            return tasks.map { values ->
                values.filter { task ->
                    task.userId == userId &&
                        task.createdAtEpochMillis >= startEpochMillis &&
                        task.createdAtEpochMillis < endEpochMillis
                }
            }
        }

        override suspend fun getById(
            id: String,
            userId: String
        ): TaskEntity? = tasks.value.firstOrNull { task ->
            task.id == id && task.userId == userId
        }

        override suspend fun insert(entity: TaskEntity) = Unit

        override suspend fun update(entity: TaskEntity) = Unit

        override suspend fun setCompleted(
            id: String,
            userId: String,
            completed: Boolean,
            completedAtEpochMillis: Long?,
            updatedAtEpochMillis: Long
        ) = Unit

        override suspend fun incrementCompletedPomodoros(
            id: String,
            userId: String,
            updatedAtEpochMillis: Long
        ) = Unit

        override suspend fun deleteById(
            id: String,
            userId: String
        ) = Unit
    }

    private fun session(
        startedAt: Long,
        focusedSeconds: Long,
        status: FocusSessionStatus,
        userId: String = USER_ID
    ): FocusSessionEntity {
        return FocusSessionEntity(
            id = "${status.name}-$startedAt",
            userId = userId,
            taskId = "task",
            plannedDurationSeconds = 1_500L,
            focusedDurationSeconds = focusedSeconds,
            startedAtEpochMillis = startedAt,
            lastStartedAtEpochMillis = null,
            endedAtEpochMillis = startedAt + focusedSeconds * 1_000L,
            status = status.name
        )
    }

    private fun task(
        id: String,
        createdAt: Long,
        isCompleted: Boolean,
        userId: String = USER_ID
    ): TaskEntity {
        return TaskEntity(
            id = id,
            userId = userId,
            title = id,
            note = null,
            category = TaskCategory.STUDY.name,
            estimatedPomodoros = 1,
            completedPomodoros = if (isCompleted) 1 else 0,
            isCompleted = isCompleted,
            createdAtEpochMillis = createdAt,
            completedAtEpochMillis = if (isCompleted) createdAt else null,
            updatedAtEpochMillis = createdAt
        )
    }

    private fun epochMillis(
        year: Int,
        month: Int,
        day: Int
    ): Long {
        return java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, year)
            set(java.util.Calendar.MONTH, month - 1)
            set(java.util.Calendar.DAY_OF_MONTH, day)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private companion object {
        const val USER_ID = "user-1"
    }
}
