package com.xu.kiko.data.session

import kotlinx.coroutines.flow.Flow

interface SessionStore {
    fun observeCurrentUserId(): Flow<String?>

    suspend fun setCurrentUserId(userId: String)

    suspend fun clearSession()
}
