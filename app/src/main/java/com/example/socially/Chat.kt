package com.example.socially

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Chat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            val intent = Intent(this, messages::class.java)
            startActivity(intent)
        }
        findViewById<ImageView>(R.id.call).setOnClickListener {
            val intent = Intent(this, call::class.java)
            startActivity(intent)
        }
        findViewById<ImageView>(R.id.open_camera).setOnClickListener {
            val intent = Intent(this, camera::class.java)
            startActivity(intent)
        }
    }
}