package com.xu.kiko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xu.kiko.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query(
        """
        SELECT * FROM focus_sessions
        WHERE userId = :userId
            AND status IN ('RUNNING', 'PAUSED')
        ORDER BY startedAtEpochMillis DESC
        LIMIT 1
        """
    )
    suspend fun getActiveSession(userId: String): FocusSessionEntity?

    @Query(
        """
        SELECT * FROM focus_sessions
        WHERE userId = :userId
            AND startedAtEpochMillis >= :startOfDayEpochMillis
            AND startedAtEpochMillis < :endOfDayEpochMillis
            AND status IN ('COMPLETED', 'CANCELLED')
        ORDER BY startedAtEpochMillis DESC
        """
    )
    fun observeTodayEndedSessions(
        userId: String,
        startOfDayEpochMillis: Long,
        endOfDayEpochMillis: Long
    ): Flow<List<FocusSessionEntity>>

    @Query(
        """
        SELECT * FROM focus_sessions
        WHERE userId = :userId
            AND startedAtEpochMillis >= :startEpochMillis
            AND startedAtEpochMillis < :endEpochMillis
            AND status IN ('COMPLETED', 'CANCELLED')
        ORDER BY startedAtEpochMillis ASC
        """
    )
    fun observeEndedSessionsInRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long
    ): Flow<List<FocusSessionEntity>>

    @Query(
        """
        SELECT * FROM focus_sessions
        WHERE userId = :userId
            AND startedAtEpochMillis >= :startEpochMillis
            AND startedAtEpochMillis < :endEpochMillis
            AND status = 'COMPLETED'
        ORDER BY startedAtEpochMillis ASC
        """
    )
    fun observeCompletedSessionsInRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long
    ): Flow<List<FocusSessionEntity>>

    @Query(
        """
        SELECT * FROM focus_sessions
        WHERE userId = :userId
            AND startedAtEpochMillis < :endEpochMillis
            AND status = 'COMPLETED'
        ORDER BY startedAtEpochMillis DESC
        """
    )
    fun observeCompletedSessionsBefore(
        userId: String,
        endEpochMillis: Long
    ): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: FocusSessionEntity)

    @Update
    suspend fun update(entity: FocusSessionEntity)
}
