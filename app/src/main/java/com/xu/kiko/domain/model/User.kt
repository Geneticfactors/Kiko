package com.xu.kiko.domain.model

data class User(
    val id: String,
    val nickname: String,
    val phone: String,
    val createdAtEpochMillis: Long
)
