package com.example.socially

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.InputStream

class story_post : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_story_post)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn = findViewById<ImageView>(R.id.back_button)
        val back2Btn = findViewById<ImageView>(R.id.back2)
        val postStoryBtn = findViewById<ImageButton>(R.id.postStory)
        val bgImage = findViewById<ImageView?>(R.id.storyBackground)

        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri = imageUriString?.let { Uri.parse(it) }

        imageUri?.let { uri -> bgImage?.setImageURI(uri) }

        backBtn.setOnClickListener { startActivity(Intent(this, home_screen::class.java)) }
        back2Btn.setOnClickListener { startActivity(Intent(this, home_screen::class.java)) }

        postStoryBtn.setOnClickListener {
            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
                val imageBytes = baos.toByteArray()
                val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

                val currentTime = System.currentTimeMillis()
                val expiryTime = currentTime + 24 * 60 * 60 * 1000 // 24 hours in ms
                val userId: String =
                    FirebaseAuth.getInstance().currentUser?.uid.toString()// Replace this with FirebaseAuth.getInstance().currentUser?.uid if using auth

                val storyData = mapOf(
                    "image" to base64Image,
                    "timestamp" to currentTime,
                    "expiry" to expiryTime
                )

                val databaseRef = FirebaseDatabase.getInstance().getReference("stories").child(userId)
                databaseRef.setValue(storyData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Story posted successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, home_screen::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to post story: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Converts Uri → Bitmap → Base64
    private fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            val bytes = baos.toByteArray()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Uploads Base64 string to Firebase Realtime Database
    private fun uploadStoryToFirebase(base64Image: String) {
        val database = FirebaseDatabase.getInstance()
        val storiesRef = database.getReference("stories")
        val userId = FirebaseAuth.getInstance().currentUser?.uid// Replace this with FirebaseAuth.getInstance().currentUser?.uid if using auth

        val storyId = storiesRef.push().key ?: return
        val storyData = mapOf(
            "storyId" to storyId,
            "userId" to userId,  // replace with real user id if you have auth
            "timestamp" to System.currentTimeMillis(),
            "imageBase64" to base64Image
        )

        storiesRef.child(storyId).setValue(storyData)
            .addOnSuccessListener {
                Toast.makeText(this, "Story uploaded!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, home_screen::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
