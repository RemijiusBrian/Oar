package dev.ridill.oar.core.domain.crypto

import org.mindrot.jbcrypt.BCrypt
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class DefaultCryptoManager : CryptoManager {

    private fun getEncryptCipher(password: String, salt: String): Cipher = Cipher
        .getInstance(CryptoManager.TRANSFORMATION)
        .apply {
            init(Cipher.ENCRYPT_MODE, createKey(password, salt))
        }

    private fun getDecryptCipher(password: String, salt: String, iv: ByteArray): Cipher = Cipher
        .getInstance(CryptoManager.TRANSFORMATION)
        .apply {
            init(Cipher.DECRYPT_MODE, createKey(password, salt), IvParameterSpec(iv))
        }

    private fun createKey(password: String, salt: String): SecretKey {
        val factory = SecretKeyFactory.getInstance(CryptoManager.KEY_ALGORITHM)
        val keySpec = PBEKeySpec(
            password.toCharArray(),
            salt.toByteArray(),
            CryptoManager.ITERATION_COUNT,
            CryptoManager.KEY_LENGTH
        )
        val key = factory.generateSecret(keySpec)
        return SecretKeySpec(key.encoded, CryptoManager.ALGORITHM)
    }

    override fun encrypt(rawData: ByteArray, password: String, salt: String): EncryptionResult {
        val cipher = getEncryptCipher(password = password, salt = salt)
        val encryptedData = cipher.doFinal(rawData)
        return EncryptionResult(
            data = encryptedData,
            iv = cipher.iv
        )
    }

    override fun decrypt(
        encryptedData: ByteArray,
        iv: ByteArray,
        password: String,
        salt: String
    ): ByteArray = getDecryptCipher(
        password = password,
        salt = salt,
        iv = iv
    ).doFinal(encryptedData)

    override fun generateSalt(): HashSaltString =
        BCrypt.gensalt(CryptoManager.HASH_LOG_ROUNDS)

    override fun saltedHash(message: String, salt: String): Pair<HashString, HashSaltString> {
        val hash = BCrypt.hashpw(message, salt)
        return hash to salt
    }

    override fun areHashesMatch(value: String?, hash2: String?): Boolean =
        BCrypt.checkpw(value, hash2)
}