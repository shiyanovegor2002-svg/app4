package com.example.testingappandroid2.keystore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.SecretKeyFactory
import java.security.spec.KeySpec

/**
 * VULNERABLE Private Key Storage - Service 3
 * M1: Improper Credential Usage
 * 
 * This class demonstrates storing private keys protected with a weak password.
 */
class WeakPasswordPrivateKeysManager {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val PRIVATE_KEY_ALIAS = "weak_private_key_alias"
        
        // VULNERABILITY: Extremely weak, hardcoded passwords
        private const val WEAK_PASSWORD_1 = "password"
        private const val WEAK_PASSWORD_2 = "admin123"
        private const val WEAK_PASSWORD_3 = "12345678"
        
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ITERATION_COUNT = 100 // VULNERABILITY: Too low iteration count
        private const val KEY_LENGTH = 128 // VULNERABILITY: Weak key length
    }

    private val keyStore: KeyStore

    init {
        keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
    }

    /**
     * VULNERABILITY: Generates private key with weak protection
     */
    fun generateWeakPrivateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        // VULNERABILITY: Weak key parameters
        val params = KeyGenParameterSpec.Builder(
            PRIVATE_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_LENGTH) // VULNERABILITY: Should be 256
            .build()

        keyGenerator.init(params)
        return keyGenerator.generateKey()
    }

    /**
     * VULNERABILITY: Creates PBE key with weak password and low iteration count
     */
    fun createPBEKeyFromWeakPassword(password: String = WEAK_PASSWORD_1): SecretKey {
        val salt = "static_salt".toByteArray() // VULNERABILITY: Static salt
        
        val keySpec: KeySpec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATION_COUNT, // VULNERABILITY: Too low
            KEY_LENGTH // VULNERABILITY: Too short
        )
        
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return secretKeyFactory.generateSecret(keySpec)
    }

    /**
     * VULNERABILITY: Returns cipher initialized with weak password protection
     */
    fun getCipherWithWeakPrivateProtection(): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val key = getOrCreatePrivateKey()
        
        // VULNERABILITY: Predictable IV based on weak password
        val iv = WEAK_PASSWORD_1.toByteArray().copyOf(12)
        val spec = GCMParameterSpec(128, iv)
        
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        return cipher
    }

    /**
     * VULNERABILITY: Exposes private key material
     */
    fun getEncodedPrivateKey(): ByteArray? {
        val key = getOrCreatePrivateKey()
        return key.encoded // VULNERABILITY: Exposing encoded private key
    }

    /**
     * VULNERABILITY: Lists all weak passwords used
     */
    fun getWeakPasswordsList(): List<String> {
        return listOf(WEAK_PASSWORD_1, WEAK_PASSWORD_2, WEAK_PASSWORD_3)
    }

    /**
     * VULNERABILITY: Checks if password is one of the known weak passwords
     */
    fun isWeakPassword(password: String): Boolean {
        return password in getWeakPasswordsList()
    }

    /**
     * VULNERABILITY: Encrypts data using weak password-derived key
     */
    fun encryptWithWeakPasswordDerivedKey(data: String, password: String = WEAK_PASSWORD_1): ByteArray {
        val pbeKey = createPBEKeyFromWeakPassword(password)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        
        val iv = password.toByteArray().copyOf(12)
        val spec = GCMParameterSpec(128, iv)
        
        cipher.init(Cipher.ENCRYPT_MODE, pbeKey, spec)
        return cipher.doFinal(data.toByteArray())
    }

    /**
     * VULNERABILITY: Decrypts data using weak password-derived key
     */
    fun decryptWithWeakPasswordDerivedKey(encryptedData: ByteArray, password: String = WEAK_PASSWORD_1): String {
        val pbeKey = createPBEKeyFromWeakPassword(password)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        
        val iv = password.toByteArray().copyOf(12)
        val spec = GCMParameterSpec(128, iv)
        
        cipher.init(Cipher.DECRYPT_MODE, pbeKey, spec)
        return String(cipher.doFinal(encryptedData))
    }

    private fun getOrCreatePrivateKey(): SecretKey {
        if (!keyStore.containsAlias(PRIVATE_KEY_ALIAS)) {
            return generateWeakPrivateKey()
        }
        
        val secretKeyEntry = keyStore.getEntry(PRIVATE_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return secretKeyEntry?.secretKey ?: generateWeakPrivateKey()
    }

    /**
     * VULNERABILITY: Returns information about key store entries
     */
    fun getKeyStoreInfo(): String {
        val aliases = keyStore.aliases().toList()
        return "Aliases: ${aliases.joinToString(", ")}"
    }
}
