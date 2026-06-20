package com.xu.kiko.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["phone"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val nickname: String,
    val phone: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAtEpochMillis: Long
)
