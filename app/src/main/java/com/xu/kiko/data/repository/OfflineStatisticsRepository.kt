package com.xu.kiko.data.repository

import com.xu.kiko.data.local.dao.FocusSessionDao
import com.xu.kiko.data.local.dao.TaskDao
import com.xu.kiko.data.local.entity.FocusSessionEntity
import com.xu.kiko.data.local.entity.TaskEntity
import com.xu.kiko.domain.model.FocusSessionStatus
import com.xu.kiko.domain.model.StatisticsData
import com.xu.kiko.domain.model.StatisticsDayFocus
import com.xu.kiko.domain.model.StatisticsPeriod
import com.xu.kiko.domain.repository.StatisticsRepository
import java.util.Calendar
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class OfflineStatisticsRepository(
    private val focusSessionDao: FocusSessionDao,
    private val taskDao: TaskDao,
    private val currentUserIdProvider: () -> String,
    private val nowProvider: () -> Long = { System.currentTimeMillis() }
) : StatisticsRepository {

    override fun observeStatistics(
        period: StatisticsPeriod
    ): Flow<StatisticsData> {
        val userId = currentUserIdProvider()
        val currentRange = rangeFor(period)
        val previousRange = previousRangeFor(period, currentRange)
        val streakEnd = startOfNextDay(nowProvider())

        return combine(
            focusSessionDao.observeEndedSessionsInRange(
                userId = userId,
                startEpochMillis = currentRange.startEpochMillis,
                endEpochMillis = currentRange.endEpochMillis
            ),
            focusSessionDao.observeEndedSessionsInRange(
                userId = userId,
                startEpochMillis = previousRange.startEpochMillis,
                endEpochMillis = previousRange.endEpochMillis
            ),
            focusSessionDao.observeCompletedSessionsBefore(
                userId = userId,
                endEpochMillis = streakEnd
            ),
            taskDao.observeCreatedInRange(
                userId = userId,
                startEpochMillis = currentRange.startEpochMillis,
                endEpochMillis = currentRange.endEpochMillis
            )
        ) { currentSessions, previousSessions, completedSessions, tasks ->
            buildStatisticsData(
                period = period,
                currentRange = currentRange,
                currentSessions = currentSessions,
                previousSessions = previousSessions,
                completedSessions = completedSessions,
                tasks = tasks
            )
        }
    }

    private fun buildStatisticsData(
        period: StatisticsPeriod,
        currentRange: TimeRange,
        currentSessions: List<FocusSessionEntity>,
        previousSessions: List<FocusSessionEntity>,
        completedSessions: List<FocusSessionEntity>,
        tasks: List<TaskEntity>
    ): StatisticsData {
        val currentMinutes = currentSessions.focusMinutes()
        val previousMinutes = previousSessions.focusMinutes()

        return StatisticsData(
            totalFocusMinutes = currentMinutes,
            comparePercent = comparePercent(
                current = currentMinutes,
                previous = previousMinutes
            ),
            streakDays = streakDays(completedSessions),
            taskCompletionRate = completionRate(tasks),
            dailyFocusMinutes = dayBuckets(
                period = period,
                range = currentRange,
                sessions = currentSessions
            ),
            hasData = currentSessions.isNotEmpty()
        )
    }

    private fun dayBuckets(
        period: StatisticsPeriod,
        range: TimeRange,
        sessions: List<FocusSessionEntity>
    ): List<StatisticsDayFocus> {
        val calendar = calendarAt(range.startEpochMillis)
        val count = when (period) {
            StatisticsPeriod.WEEK -> DAYS_PER_WEEK
            StatisticsPeriod.MONTH ->
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        val labels = when (period) {
            StatisticsPeriod.WEEK -> WEEK_LABELS
            StatisticsPeriod.MONTH -> List(count) { index ->
                (index + 1).toString()
            }
        }
        val minutesByDay = sessions.groupBy { session ->
            startOfDay(session.startedAtEpochMillis)
        }.mapValues { (_, daySessions) ->
            daySessions.focusMinutes()
        }

        return List(count) { index ->
            val dayStart = calendar.apply {
                timeInMillis = range.startEpochMillis
                add(Calendar.DAY_OF_YEAR, index)
            }.timeInMillis
            StatisticsDayFocus(
                label = labels[index],
                valueMinutes = minutesByDay[dayStart].orZero()
            )
        }
    }

    private fun streakDays(
        completedSessions: List<FocusSessionEntity>
    ): Int {
        val completedDays = completedSessions
            .filter { session ->
                session.status == FocusSessionStatus.COMPLETED.name
            }
            .map { session -> startOfDay(session.startedAtEpochMillis) }
            .toSet()

        var streak = 0
        var day = startOfDay(nowProvider())
        while (completedDays.contains(day)) {
            streak += 1
            day = calendarAt(day).apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }.timeInMillis
        }
        return streak
    }

    private fun completionRate(tasks: List<TaskEntity>): Int {
        if (tasks.isEmpty()) {
            return 0
        }

        val completedCount = tasks.count { task -> task.isCompleted }
        return ((completedCount.toDouble() / tasks.size.toDouble()) * 100)
            .roundToInt()
    }

    private fun comparePercent(
        current: Int,
        previous: Int
    ): Int {
        return when {
            previous == 0 && current == 0 -> 0
            previous == 0 -> 100
            else -> (((current - previous).toDouble() /
                previous.toDouble()) * 100).roundToInt()
        }
    }

    private fun List<FocusSessionEntity>.focusMinutes(): Int {
        return sumOf { session -> session.focusedDurationSeconds }
            .floorDiv(SECONDS_PER_MINUTE)
            .toInt()
    }

    private fun rangeFor(period: StatisticsPeriod): TimeRange {
        return when (period) {
            StatisticsPeriod.WEEK -> currentWeekRange()
            StatisticsPeriod.MONTH -> currentMonthRange()
        }
    }

    private fun previousRangeFor(
        period: StatisticsPeriod,
        currentRange: TimeRange
    ): TimeRange {
        val calendar = calendarAt(currentRange.startEpochMillis)
        when (period) {
            StatisticsPeriod.WEEK ->
                calendar.add(Calendar.WEEK_OF_YEAR, -1)

            StatisticsPeriod.MONTH ->
                calendar.add(Calendar.MONTH, -1)
        }
        val start = calendar.timeInMillis
        val end = when (period) {
            StatisticsPeriod.WEEK ->
                calendar.apply {
                    add(Calendar.DAY_OF_YEAR, DAYS_PER_WEEK)
                }.timeInMillis

            StatisticsPeriod.MONTH ->
                calendar.apply {
                    add(Calendar.MONTH, 1)
                }.timeInMillis
        }
        return TimeRange(start, end)
    }

    private fun currentWeekRange(): TimeRange {
        val start = calendarAt(nowProvider()).apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            clearTime()
        }.timeInMillis
        val end = calendarAt(start).apply {
            add(Calendar.DAY_OF_YEAR, DAYS_PER_WEEK)
        }.timeInMillis
        return TimeRange(start, end)
    }

    private fun currentMonthRange(): TimeRange {
        val start = calendarAt(nowProvider()).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            clearTime()
        }.timeInMillis
        val end = calendarAt(start).apply {
            add(Calendar.MONTH, 1)
        }.timeInMillis
        return TimeRange(start, end)
    }

    private fun startOfNextDay(epochMillis: Long): Long {
        return calendarAt(startOfDay(epochMillis)).apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis
    }

    private fun startOfDay(epochMillis: Long): Long {
        return calendarAt(epochMillis).apply {
            clearTime()
        }.timeInMillis
    }

    private fun calendarAt(epochMillis: Long): Calendar {
        return Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            timeInMillis = epochMillis
        }
    }

    private fun Calendar.clearTime() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun Int?.orZero(): Int = this ?: 0

    private data class TimeRange(
        val startEpochMillis: Long,
        val endEpochMillis: Long
    )

    private companion object {
        const val DAYS_PER_WEEK = 7
        const val SECONDS_PER_MINUTE = 60L
        val WEEK_LABELS = listOf("一", "二", "三", "四", "五", "六", "日")
    }
}
