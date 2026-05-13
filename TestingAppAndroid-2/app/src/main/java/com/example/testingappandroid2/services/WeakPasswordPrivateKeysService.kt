package com.example.testingappandroid2.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.testingappandroid2.keystore.WeakPasswordPrivateKeysManager

/**
 * Service 3: Weak Password Private Keys Service
 * M1: Improper Credential Usage
 * 
 * This service demonstrates storing private keys protected with weak passwords.
 */
class WeakPasswordPrivateKeysService : Service() {

    companion object {
        private const val TAG = "WeakPasswordPrivateKeys"
    }

    private lateinit var keyManager: WeakPasswordPrivateKeysManager

    override fun onCreate() {
        super.onCreate()
        keyManager = WeakPasswordPrivateKeysManager()
        Log.d(TAG, "Service created with vulnerable private key manager")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started - demonstrating M1 vulnerability (private keys)")
        
        try {
            // VULNERABILITY: Generate weak private key
            val privateKey = keyManager.generateWeakPrivateKey()
            Log.d(TAG, "Generated weak private key: ${privateKey.encoded?.size} bytes")
            
            // VULNERABILITY: Create PBE key from weak password
            val pbeKey = keyManager.createPBEKeyFromWeakPassword()
            Log.d(TAG, "Created PBE key from weak password")
            
            // VULNERABILITY: Use cipher with weak protection
            val cipher = keyManager.getCipherWithWeakPrivateProtection()
            
            // VULNERABILITY: Expose encoded private key
            val encodedKey = keyManager.getEncodedPrivateKey()
            Log.d(TAG, "Exposed encoded private key: ${encodedKey?.size} bytes")
            
            // VULNERABILITY: List all weak passwords
            val weakPasswords = keyManager.getWeakPasswordsList()
            Log.d(TAG, "Weak passwords in use: ${weakPasswords.joinToString(", ")}")
            
            // VULNERABILITY: Encrypt sensitive data with weak password-derived key
            val sensitiveData = "User authentication token: secret_token_12345"
            val encryptedData = keyManager.encryptWithWeakPasswordDerivedKey(sensitiveData)
            Log.d(TAG, "Encrypted sensitive data with weak password: ${encryptedData.size} bytes")
            
            // VULNERABILITY: Decrypt using weak password
            val decryptedData = keyManager.decryptWithWeakPasswordDerivedKey(encryptedData)
            Log.d(TAG, "Decrypted data: $decryptedData")
            
            // VULNERABILITY: Check if a password is weak
            val testPassword = "password"
            val isWeak = keyManager.isWeakPassword(testPassword)
            Log.d(TAG, "Is '$testPassword' a weak password? $isWeak")
            
            // VULNERABILITY: Get keystore info
            val keystoreInfo = keyManager.getKeyStoreInfo()
            Log.d(TAG, "Keystore info: $keystoreInfo")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in vulnerable operation", e)
        }
        
        // Stop service after demonstration
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }
}
