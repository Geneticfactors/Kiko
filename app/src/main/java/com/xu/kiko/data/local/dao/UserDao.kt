package com.xu.kiko.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xu.kiko.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: UserEntity)
}
