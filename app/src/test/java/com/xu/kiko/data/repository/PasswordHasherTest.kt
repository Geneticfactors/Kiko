package com.xu.kiko.data.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    private val passwordHasher = PasswordHasher()

    @Test
    fun samePasswordWithSameSaltVerifies() {
        val salt = passwordHasher.createSalt()
        val hash = passwordHasher.hashPassword(
            password = "password123",
            encodedSalt = salt
        )

        assertTrue(
            passwordHasher.verifyPassword(
                password = "password123",
                encodedSalt = salt,
                expectedHash = hash
            )
        )
    }

    @Test
    fun samePasswordWithDifferentSaltsCreatesDifferentHashes() {
        val firstHash = passwordHasher.hashPassword(
            password = "password123",
            encodedSalt = passwordHasher.createSalt()
        )
        val secondHash = passwordHasher.hashPassword(
            password = "password123",
            encodedSalt = passwordHasher.createSalt()
        )

        assertNotEquals(firstHash, secondHash)
    }

    @Test
    fun wrongPasswordDoesNotVerify() {
        val salt = passwordHasher.createSalt()
        val hash = passwordHasher.hashPassword(
            password = "password123",
            encodedSalt = salt
        )

        assertFalse(
            passwordHasher.verifyPassword(
                password = "wrong-password",
                encodedSalt = salt,
                expectedHash = hash
            )
        )
    }
}
