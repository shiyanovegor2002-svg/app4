package com.example.testingappandroid2.keystore

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * VULNERABLE File-based Key Storage - Service 2
 * M9: Insecure Data Storage
 * 
 * This class demonstrates storing keys in files that are readable
 * by other applications on the device.
 */
class InsecureFileKeyStorage(private val context: Context) {

    companion object {
        // VULNERABILITY: Storing keys in world-readable location
        private const val KEY_FILE_NAME = "secret_keys.dat"
        private const val KEY_ALIAS = "insecure_file_key"
    }

    private val keyFile: File = File(context.filesDir, KEY_FILE_NAME)

    /**
     * VULNERABILITY: Saves key to file with world-readable permissions
     */
    fun saveKeyToFile(key: SecretKey) {
        try {
            val encodedKey = key.encoded
            
            // VULNERABILITY: Writing key to file without proper protection
            FileOutputStream(keyFile).use { fos ->
                fos.write(encodedKey)
            }
            
            // VULNERABILITY: Setting world-readable permissions
            keyFile.setReadable(true, false) // false = not owner-only, so world-readable
            keyFile.setWritable(true, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * VULNERABILITY: Reads key from insecure file storage
     */
    fun loadKeyFromFile(): SecretKey? {
        return try {
            if (!keyFile.exists()) {
                return null
            }

            // VULNERABILITY: Reading key from unprotected file
            FileInputStream(keyFile).use { fis ->
                val keyBytes = ByteArray(32) // AES-256 key size
                fis.read(keyBytes)
                SecretKeySpec(keyBytes, "AES")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * VULNERABILITY: Stores multiple keys in plain text file
     */
    fun saveMultipleKeys(keys: Map<String, ByteArray>) {
        try {
            val stringBuilder = StringBuilder()
            
            // VULNERABILITY: Storing keys as plain text
            keys.forEach { (alias, keyBytes) ->
                stringBuilder.appendLine("$alias:${keyBytes.joinToString(",")}")
            }
            
            FileOutputStream(keyFile).use { fos ->
                fos.write(stringBuilder.toString().toByteArray())
            }
            
            // VULNERABILITY: World-readable file
            keyFile.setReadable(true, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * VULNERABILITY: Returns all stored keys without authentication
     */
    fun getAllStoredKeys(): Map<String, ByteArray> {
        val keys = mutableMapOf<String, ByteArray>()
        
        try {
            if (!keyFile.exists()) return keys

            FileInputStream(keyFile).use { fis ->
                val content = fis.bufferedReader().readText()
                
                // VULNERABILITY: Parsing and exposing all keys
                content.lines().forEach { line ->
                    val parts = line.split(":")
                    if (parts.size == 2) {
                        val alias = parts[0]
                        val keyBytes = parts[1].split(",").map { it.toByte() }.toByteArray()
                        keys[alias] = keyBytes
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return keys
    }

    /**
     * VULNERABILITY: Exposes file path for external access
     */
    fun getKeyFilePath(): String {
        return keyFile.absolutePath
    }

    /**
     * VULNERABILITY: Checks if file is readable by any app
     */
    fun isKeyFileWorldReadable(): Boolean {
        return keyFile.canRead() && keyFile.isReadable
    }
}
