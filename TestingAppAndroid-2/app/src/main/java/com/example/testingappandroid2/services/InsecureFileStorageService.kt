package com.example.testingappandroid2.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.testingappandroid2.keystore.InsecureFileKeyStorage
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Service 2: Insecure File Storage Service
 * M9: Insecure Data Storage
 * 
 * This service demonstrates storing cryptographic keys in files
 * that are readable by other applications on the device.
 */
class InsecureFileStorageService : Service() {

    companion object {
        private const val TAG = "InsecureFileStorage"
    }

    private lateinit var fileKeyStorage: InsecureFileKeyStorage

    override fun onCreate() {
        super.onCreate()
        fileKeyStorage = InsecureFileKeyStorage(this)
        Log.d(TAG, "Service created with insecure file storage")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started - demonstrating M9 vulnerability")
        
        try {
            // VULNERABILITY: Generate a key
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256)
            val secretKey = keyGenerator.generateKey()
            
            // VULNERABILITY: Save key to world-readable file
            fileKeyStorage.saveKeyToFile(secretKey)
            Log.d(TAG, "Saved key to insecure file location")
            
            // VULNERABILITY: Expose file path
            val filePath = fileKeyStorage.getKeyFilePath()
            Log.d(TAG, "Key file path (world-readable): $filePath")
            
            // VULNERABILITY: Check if file is world-readable
            val isWorldReadable = fileKeyStorage.isKeyFileWorldReadable()
            Log.d(TAG, "Is key file world-readable: $isWorldReadable")
            
            // VULNERABILITY: Store multiple keys in plain text
            val keys = mapOf(
                "key1" to ByteArray(32) { 0x01 },
                "key2" to ByteArray(32) { 0x02 },
                "api_key" to "super_secret_api_key_12345".toByteArray()
            )
            fileKeyStorage.saveMultipleKeys(keys)
            Log.d(TAG, "Stored multiple keys in plain text file")
            
            // VULNERABILITY: Read back all stored keys without authentication
            val allKeys = fileKeyStorage.getAllStoredKeys()
            Log.d(TAG, "Retrieved ${allKeys.size} keys from insecure storage")
            
            // VULNERABILITY: Load key from file
            val loadedKey = fileKeyStorage.loadKeyFromFile()
            Log.d(TAG, "Loaded key from file: ${loadedKey != null}")
            
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
