package com.xu.kiko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xu.kiko.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query(
        """
        SELECT * FROM tasks
        WHERE userId = :userId
        ORDER BY isCompleted ASC, createdAtEpochMillis DESC
        """
    )
    fun observeAll(userId: String): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE userId = :userId AND category = :category
        ORDER BY isCompleted ASC, createdAtEpochMillis DESC
        """
    )
    fun observeByCategory(
        userId: String,
        category: String
    ): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE userId = :userId
            AND createdAtEpochMillis >= :startOfDayEpochMillis
            AND createdAtEpochMillis < :endOfDayEpochMillis
        ORDER BY isCompleted ASC, createdAtEpochMillis DESC
        LIMIT :limit
        """
    )
    fun observeToday(
        userId: String,
        startOfDayEpochMillis: Long,
        endOfDayEpochMillis: Long,
        limit: Int
    ): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE userId = :userId
            AND createdAtEpochMillis >= :startEpochMillis
            AND createdAtEpochMillis < :endEpochMillis
        ORDER BY createdAtEpochMillis ASC
        """
    )
    fun observeCreatedInRange(
        userId: String,
        startEpochMillis: Long,
        endEpochMillis: Long
    ): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getById(
        id: String,
        userId: String
    ): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: TaskEntity)

    @Update
    suspend fun update(entity: TaskEntity)

    @Query(
        """
        UPDATE tasks
        SET isCompleted = :completed,
            completedAtEpochMillis = :completedAtEpochMillis,
            updatedAtEpochMillis = :updatedAtEpochMillis
        WHERE id = :id
            AND userId = :userId
        """
    )
    suspend fun setCompleted(
        id: String,
        userId: String,
        completed: Boolean,
        completedAtEpochMillis: Long?,
        updatedAtEpochMillis: Long
    )

    @Query(
        """
        UPDATE tasks
        SET completedPomodoros = completedPomodoros + 1,
            isCompleted = CASE
                WHEN completedPomodoros + 1 >= estimatedPomodoros
                THEN 1
                ELSE isCompleted
            END,
            completedAtEpochMillis = CASE
                WHEN completedPomodoros + 1 >= estimatedPomodoros
                    AND completedAtEpochMillis IS NULL
                THEN :updatedAtEpochMillis
                ELSE completedAtEpochMillis
            END,
            updatedAtEpochMillis = :updatedAtEpochMillis
        WHERE id = :id
            AND userId = :userId
        """
    )
    suspend fun incrementCompletedPomodoros(
        id: String,
        userId: String,
        updatedAtEpochMillis: Long
    )

    @Query("DELETE FROM tasks WHERE id = :id AND userId = :userId")
    suspend fun deleteById(
        id: String,
        userId: String
    )
}
