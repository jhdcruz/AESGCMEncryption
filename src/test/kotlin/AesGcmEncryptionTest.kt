import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

internal class AesGcmEncryptionTest {
    @Test
    fun `test key bytes generation`() {
        val aesgcmEncryption = AesGcmEncryption()
        val keyBytes = aesgcmEncryption.generateSecretKey()

        assertEquals(16, keyBytes.size)
    }

    @Test
    fun `test encryption`() {
        val aesgcmEncryption = AesGcmEncryption()
        val secretKey = aesgcmEncryption.generateSecretKey()

        val message = "admin"
        val cipherText = aesgcmEncryption.encrypt(message, secretKey)

        assertEquals(33, cipherText.size)
    }

    @Test
    fun `test decryption`() {
        val aesgcmEncryption = AesGcmEncryption()
        val secretKey = aesgcmEncryption.generateSecretKey()

        val message = "admin"
        val cipherText = aesgcmEncryption.encrypt(message, secretKey)
        val decrypted = aesgcmEncryption.decrypt(cipherText, secretKey)

        assertEquals(message, decrypted)
    }
}