package com.xu.kiko.data.repository

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PasswordHasher(
    private val secureRandom: SecureRandom = SecureRandom()
) {
    fun createSalt(): String {
        val salt = ByteArray(SALT_BYTE_COUNT)
        secureRandom.nextBytes(salt)
        return salt.toBase64()
    }

    fun hashPassword(
        password: String,
        encodedSalt: String
    ): String {
        val salt = Base64.getDecoder().decode(encodedSalt)
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATION_COUNT,
            KEY_LENGTH_BITS
        )
        val hash = SecretKeyFactory
            .getInstance(ALGORITHM)
            .generateSecret(spec)
            .encoded
        return hash.toBase64()
    }

    fun verifyPassword(
        password: String,
        encodedSalt: String,
        expectedHash: String
    ): Boolean {
        val actualHash = hashPassword(password, encodedSalt)
        return MessageDigest.isEqual(
            actualHash.toByteArray(Charsets.UTF_8),
            expectedHash.toByteArray(Charsets.UTF_8)
        )
    }

    private fun ByteArray.toBase64(): String {
        return Base64.getEncoder().encodeToString(this)
    }

    private companion object {
        const val ALGORITHM = "PBKDF2WithHmacSHA256"
        const val SALT_BYTE_COUNT = 16
        const val ITERATION_COUNT = 120_000
        const val KEY_LENGTH_BITS = 256
    }
}
