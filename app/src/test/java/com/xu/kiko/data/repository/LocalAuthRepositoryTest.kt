package com.xu.kiko.data.repository

import com.xu.kiko.data.local.dao.UserDao
import com.xu.kiko.data.local.entity.UserEntity
import com.xu.kiko.data.session.SessionStore
import com.xu.kiko.domain.repository.AuthError
import com.xu.kiko.domain.repository.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class LocalAuthRepositoryTest {

    @Test
    fun registerInsertsUserAndWritesSession() = runBlocking {
        val userDao = FakeUserDao()
        val sessionStore = FakeSessionStore()
        val repository = repository(
            userDao = userDao,
            sessionStore = sessionStore
        )

        val result = repository.register(
            nickname = "Kiko",
            phone = "13800138000",
            password = "password123"
        )

        assertEquals(AuthResult.Success, result)
        val user = userDao.getByPhone("13800138000")
        assertNotNull(user)
        assertEquals(user?.id, sessionStore.observeCurrentUserId().first())
    }

    @Test
    fun duplicatePhoneRegisterFails() = runBlocking {
        val repository = repository()

        repository.register(
            nickname = "Kiko",
            phone = "13800138000",
            password = "password123"
        )
        val result = repository.register(
            nickname = "Other",
            phone = "13800138000",
            password = "password456"
        )

        assertEquals(
            AuthResult.Failure(AuthError.PHONE_ALREADY_REGISTERED),
            result
        )
    }

    @Test
    fun correctPasswordLoginWritesSession() = runBlocking {
        val userDao = FakeUserDao()
        val sessionStore = FakeSessionStore()
        val repository = repository(
            userDao = userDao,
            sessionStore = sessionStore
        )

        repository.register(
            nickname = "Kiko",
            phone = "13800138000",
            password = "password123"
        )
        repository.logout()
        val result = repository.login(
            phone = "13800138000",
            password = "password123"
        )

        assertEquals(AuthResult.Success, result)
        assertNotNull(sessionStore.observeCurrentUserId().first())
    }

    @Test
    fun wrongPasswordAndMissingUserReturnSameError() = runBlocking {
        val repository = repository()
        repository.register(
            nickname = "Kiko",
            phone = "13800138000",
            password = "password123"
        )

        val wrongPassword = repository.login(
            phone = "13800138000",
            password = "wrong-password"
        )
        val missingUser = repository.login(
            phone = "13900139000",
            password = "password123"
        )

        assertEquals(
            AuthResult.Failure(AuthError.INVALID_CREDENTIALS),
            wrongPassword
        )
        assertEquals(wrongPassword, missingUser)
    }

    @Test
    fun logoutClearsSession() = runBlocking {
        val sessionStore = FakeSessionStore()
        val repository = repository(sessionStore = sessionStore)

        repository.register(
            nickname = "Kiko",
            phone = "13800138000",
            password = "password123"
        )
        repository.logout()

        assertNull(sessionStore.observeCurrentUserId().first())
    }

    private fun repository(
        userDao: FakeUserDao = FakeUserDao(),
        sessionStore: FakeSessionStore = FakeSessionStore()
    ): LocalAuthRepository {
        return LocalAuthRepository(
            userDao = userDao,
            sessionStore = sessionStore,
            nowProvider = { 1_000L }
        )
    }

    private class FakeUserDao : UserDao {
        private val users = mutableMapOf<String, UserEntity>()

        override suspend fun getByPhone(phone: String): UserEntity? {
            return users.values.firstOrNull { user -> user.phone == phone }
        }

        override suspend fun getById(id: String): UserEntity? {
            return users[id]
        }

        override suspend fun insert(entity: UserEntity) {
            check(getByPhone(entity.phone) == null)
            users[entity.id] = entity
        }
    }

    private class FakeSessionStore : SessionStore {
        private val currentUserId = MutableStateFlow<String?>(null)

        override fun observeCurrentUserId(): Flow<String?> {
            return currentUserId
        }

        override suspend fun setCurrentUserId(userId: String) {
            currentUserId.value = userId
        }

        override suspend fun clearSession() {
            currentUserId.value = null
        }
    }
}
