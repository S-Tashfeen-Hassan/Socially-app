package com.example.socially

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class story : AppCompatActivity() {

    private lateinit var storyImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_story)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        storyImage = findViewById(R.id.storyBackground) // Make sure you have an ImageView with this id
        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            startActivity(Intent(this, home_screen::class.java))
            finish()
        }

        loadActiveStories()
    }

    private fun loadActiveStories() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("stories")

        databaseRef.get().addOnSuccessListener { snapshot ->
            val now = System.currentTimeMillis()
            var storyShown = false

            for (storySnap in snapshot.children) {
                val expiry = storySnap.child("expiry").getValue(Long::class.java) ?: 0L
                if (now > expiry) {
                    // Story expired — delete it
                    storySnap.ref.removeValue()
                } else {
                    // Active story — display it
                    val base64Image = storySnap.child("image").getValue(String::class.java)
                    if (base64Image != null) {
                        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        storyImage.setImageBitmap(bitmap)
                        storyShown = true
                        break // show only the first active one for now
                    }
                }
            }

            if (!storyShown) {
                Toast.makeText(this, "No active stories available.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, home_screen::class.java))
                finish()
            }

        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to load stories: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
