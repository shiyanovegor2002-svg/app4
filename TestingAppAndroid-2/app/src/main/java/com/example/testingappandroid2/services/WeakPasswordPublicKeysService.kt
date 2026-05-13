package com.example.testingappandroid2.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.testingappandroid2.keystore.WeakPasswordPublicKeysManager

/**
 * Service 1: Weak Password Public Keys Service
 * M1: Improper Credential Usage
 * 
 * This service demonstrates using a keystore with weak password protection
 * and storing public keys that can be accessed by other applications.
 */
class WeakPasswordPublicKeysService : Service() {

    companion object {
        private const val TAG = "WeakPasswordPublicKeys"
    }

    private lateinit var keyManager: WeakPasswordPublicKeysManager

    override fun onCreate() {
        super.onCreate()
        keyManager = WeakPasswordPublicKeysManager()
        Log.d(TAG, "Service created with vulnerable key manager")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started - demonstrating M1 vulnerability")
        
        try {
            // VULNERABILITY: Generate weak key
            val key = keyManager.generateWeakKey()
            Log.d(TAG, "Generated weak key: ${key.encoded?.size} bytes")
            
            // VULNERABILITY: Use weak password for cipher
            val cipher = keyManager.getCipherWithWeakPassword()
            
            // VULNERABILITY: Encrypt sensitive data with weak protection
            val sensitiveData = "Sensitive user credentials"
            val encryptedData = keyManager.encryptWithWeakPassword(sensitiveData)
            Log.d(TAG, "Encrypted data with weak password: ${encryptedData.size} bytes")
            
            // VULNERABILITY: Expose public key info
            val publicKeyInfo = keyManager.getPublicKeyInfo()
            Log.d(TAG, "Public key info exposed: $publicKeyInfo")
            
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
