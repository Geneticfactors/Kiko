package com.xu.kiko.domain.repository

import com.xu.kiko.domain.model.User
import kotlinx.coroutines.flow.Flow

sealed interface AuthResult {
    data object Success : AuthResult
    data class Failure(val error: AuthError) : AuthResult
}

enum class AuthError {
    PHONE_ALREADY_REGISTERED,
    INVALID_CREDENTIALS,
    UNKNOWN
}

interface AuthRepository {
    fun observeSession(): Flow<String?>

    suspend fun getCurrentUser(): User?

    suspend fun register(
        nickname: String,
        phone: String,
        password: String
    ): AuthResult

    suspend fun login(
        phone: String,
        password: String
    ): AuthResult

    suspend fun logout()
}
