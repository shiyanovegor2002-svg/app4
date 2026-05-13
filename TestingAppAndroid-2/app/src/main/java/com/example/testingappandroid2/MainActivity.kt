package com.example.testingappandroid2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.testingappandroid2.services.InsecureFileStorageService
import com.example.testingappandroid2.services.WeakPasswordPrivateKeysService
import com.example.testingappandroid2.services.WeakPasswordPublicKeysService

class MainActivity : AppCompatActivity() {

    private lateinit var textViewStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewStatus = findViewById(R.id.textViewStatus)

        val buttonService1: Button = findViewById(R.id.buttonService1)
        val buttonService2: Button = findViewById(R.id.buttonService2)
        val buttonService3: Button = findViewById(R.id.buttonService3)

        buttonService1.setOnClickListener {
            startService(android.content.Intent(this, WeakPasswordPublicKeysService::class.java))
            textViewStatus.text = "Status: Service 1 started (Weak Password Public Keys)"
        }

        buttonService2.setOnClickListener {
            startService(android.content.Intent(this, InsecureFileStorageService::class.java))
            textViewStatus.text = "Status: Service 2 started (Insecure File Storage)"
        }

        buttonService3.setOnClickListener {
            startService(android.content.Intent(this, WeakPasswordPrivateKeysService::class.java))
            textViewStatus.text = "Status: Service 3 started (Weak Password Private Keys)"
        }
    }
}
