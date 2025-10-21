package com.example.socially

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class story_edit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_story_edit)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Back button
        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            val intent = Intent(this, edit_profile::class.java)
            startActivity(intent)
        }

        // Get the passed image URI (from story_post)
        val imageUriString = intent.getStringExtra("selected_image_uri")
        val imageLayout = findViewById<LinearLayout>(R.id.story_background_layout) // make sure this id exists in XML

        if (!imageUriString.isNullOrEmpty() && imageLayout != null) {
            try {
                val imageUri = Uri.parse(imageUriString)
                val input = contentResolver.openInputStream(imageUri)
                val bitmap = input?.use { BitmapFactory.decodeStream(it) }
                if (bitmap != null) {
                    val drawable = android.graphics.drawable.BitmapDrawable(resources, bitmap)
                    imageLayout.background = drawable
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // optionally show a toast
            }
        }
    }
}
