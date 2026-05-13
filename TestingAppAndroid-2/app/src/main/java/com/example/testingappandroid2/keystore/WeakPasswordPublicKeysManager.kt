package com.example.testingappandroid2.keystore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * VULNERABLE KeyStore Manager - Service 1
 * M1: Improper Credential Usage
 * 
 * This class demonstrates using a keystore with a weak password
 * and storing public keys that are readable by other applications.
 */
class WeakPasswordPublicKeysManager {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "weak_public_key_alias"
        // VULNERABILITY: Weak, hardcoded password
        private const val WEAK_PASSWORD = "123456"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }

    private val keyStore: KeyStore

    init {
        keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
    }

    /**
     * VULNERABILITY: Creates a key with weak parameters
     */
    fun generateWeakKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        // VULNERABILITY: Using weak key size and parameters
        val params = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(128) // VULNERABILITY: Weak key size (should be 256)
            .build()

        keyGenerator.init(params)
        return keyGenerator.generateKey()
    }

    /**
     * VULNERABILITY: Returns cipher with weak password embedded
     */
    fun getCipherWithWeakPassword(): Cipher {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val key = getOrCreateKey()
        
        // VULNERABILITY: Password is hardcoded and weak
        val iv = WEAK_PASSWORD.toByteArray().copyOf(12) // Weak IV derivation
        val spec = GCMParameterSpec(128, iv)
        
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)
        return cipher
    }

    /**
     * VULNERABILITY: Exposes public key information
     */
    fun getPublicKeyInfo(): String {
        val entry = keyStore.getEntry(KEY_ALIAS, null)
        return entry?.toString() ?: "No key found"
    }

    private fun getOrCreateKey(): SecretKey {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            return generateWeakKey()
        }
        
        val secretKeyEntry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return secretKeyEntry?.secretKey ?: generateWeakKey()
    }

    /**
     * VULNERABILITY: Stores encrypted data with weak password
     */
    fun encryptWithWeakPassword(data: String): ByteArray {
        val cipher = getCipherWithWeakPassword()
        return cipher.doFinal(data.toByteArray())
    }
}
