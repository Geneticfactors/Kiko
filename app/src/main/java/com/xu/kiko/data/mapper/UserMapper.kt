package com.xu.kiko.data.mapper

import com.xu.kiko.data.local.entity.UserEntity
import com.xu.kiko.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        nickname = nickname,
        phone = phone,
        createdAtEpochMillis = createdAtEpochMillis
    )
}
