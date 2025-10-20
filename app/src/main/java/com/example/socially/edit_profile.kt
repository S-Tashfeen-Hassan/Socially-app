package com.example.socially

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class edit_profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<ImageView>(R.id.activity1).setOnClickListener {
            val intent = Intent(this, activity::class.java)
            startActivity(intent)
        }
        findViewById<ImageView>(R.id.home_button).setOnClickListener {
            val intent = Intent(this, home_screen::class.java)
            startActivity(intent)
        }
        findViewById<ImageView>(R.id.search_button).setOnClickListener {
            val intent = Intent(this, for_you_page::class.java)
            startActivity(intent)
        }
        findViewById<ImageView>(R.id.create_button).setOnClickListener {
            val intent = Intent(this, post::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.edit_details).setOnClickListener {
            val intent = Intent(this, profile_details::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.add_highlight).setOnClickListener {
            val intent = Intent(this, story_edit::class.java)
            startActivity(intent)
        }
    }
}