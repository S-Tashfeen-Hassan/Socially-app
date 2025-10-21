package com.example.socially

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

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

        // ✅ LOGOUT BUTTON FUNCTIONALITY
        findViewById<ImageView>(R.id.logout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, pre_login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
