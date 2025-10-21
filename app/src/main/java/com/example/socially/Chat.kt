package com.example.socially

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.EditText
import android.widget.LinearLayout
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
        val messageInput = findViewById<EditText>(R.id.message_input)
        val sendButton = findViewById<ImageView>(R.id.send_button)
        val iconsLayout = findViewById<LinearLayout>(R.id.icons_layout)

// Toggle buttons when user types
        messageInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    iconsLayout.visibility = android.view.View.GONE
                    sendButton.visibility = android.view.View.VISIBLE
                } else {
                    iconsLayout.visibility = android.view.View.VISIBLE
                    sendButton.visibility = android.view.View.GONE
                }
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

}