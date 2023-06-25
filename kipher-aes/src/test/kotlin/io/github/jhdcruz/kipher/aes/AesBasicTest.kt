package io.github.jhdcruz.kipher.aes

import io.github.jhdcruz.kipher.aes.AesParams.decodeToString
import io.github.jhdcruz.kipher.aes.AesParams.invalidKey
import io.github.jhdcruz.kipher.aes.AesParams.message
import io.github.jhdcruz.kipher.common.KipherException
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AesBasicTest {

    @Test
    fun `test basic encryption`() {
        val cbcEncryption = CbcEncryption()

        val encrypted = cbcEncryption.encrypt(message)
        val decrypted = cbcEncryption.decrypt(encrypted)

        assertEquals(decodeToString(message), decodeToString(decrypted))
    }

    @Test
    fun `test basic encryption with invalid secret key`() {
        val cbcEncryption = CbcEncryption()

        assertThrows<KipherException> {
            cbcEncryption.encrypt(message, invalidKey)
        }
    }

    @Test
    fun `test basic encryption with custom key size`() {
        val cbcEncryption = CbcEncryption()

        val secretKey = cbcEncryption.generateKey(128)
        val encrypted = cbcEncryption.encrypt(message, secretKey)

        val decrypted = cbcEncryption.decrypt(
            encrypted = encrypted["data"]!!,
            key = encrypted["key"]!!,
        )

        assertEquals(decodeToString(message), decodeToString(decrypted))
    }
}