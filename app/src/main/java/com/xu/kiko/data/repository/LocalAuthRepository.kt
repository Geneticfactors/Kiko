package com.xu.kiko.data.repository

import com.xu.kiko.data.local.dao.UserDao
import com.xu.kiko.data.local.entity.UserEntity
import com.xu.kiko.data.mapper.toDomain
import com.xu.kiko.data.session.SessionStore
import com.xu.kiko.domain.model.User
import com.xu.kiko.domain.repository.AuthError
import com.xu.kiko.domain.repository.AuthRepository
import com.xu.kiko.domain.repository.AuthResult
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class LocalAuthRepository(
    private val userDao: UserDao,
    private val sessionStore: SessionStore,
    private val passwordHasher: PasswordHasher = PasswordHasher(),
    private val nowProvider: () -> Long = { System.currentTimeMillis() }
) : AuthRepository {

    override fun observeSession(): Flow<String?> {
        return sessionStore.observeCurrentUserId()
    }

    override suspend fun getCurrentUser(): User? {
        val userId = sessionStore.observeCurrentUserId()
            .firstOrNull()
            ?: return null
        return userDao.getById(userId)?.toDomain()
    }

    override suspend fun register(
        nickname: String,
        phone: String,
        password: String
    ): AuthResult {
        return runCatching {
            if (userDao.getByPhone(phone) != null) {
                return AuthResult.Failure(
                    AuthError.PHONE_ALREADY_REGISTERED
                )
            }

            val salt = passwordHasher.createSalt()
            val user = UserEntity(
                id = UUID.randomUUID().toString(),
                nickname = nickname.trim(),
                phone = phone,
                passwordHash = passwordHasher.hashPassword(
                    password = password,
                    encodedSalt = salt
                ),
                passwordSalt = salt,
                createdAtEpochMillis = nowProvider()
            )

            userDao.insert(user)
            sessionStore.setCurrentUserId(user.id)
            AuthResult.Success
        }.getOrElse {
            AuthResult.Failure(AuthError.UNKNOWN)
        }
    }

    override suspend fun login(
        phone: String,
        password: String
    ): AuthResult {
        return runCatching {
            val user = userDao.getByPhone(phone)
                ?: return AuthResult.Failure(
                    AuthError.INVALID_CREDENTIALS
                )

            val verified = passwordHasher.verifyPassword(
                password = password,
                encodedSalt = user.passwordSalt,
                expectedHash = user.passwordHash
            )

            if (!verified) {
                return AuthResult.Failure(
                    AuthError.INVALID_CREDENTIALS
                )
            }

            sessionStore.setCurrentUserId(user.id)
            AuthResult.Success
        }.getOrElse {
            AuthResult.Failure(AuthError.UNKNOWN)
        }
    }

    override suspend fun logout() {
        sessionStore.clearSession()
    }
}
