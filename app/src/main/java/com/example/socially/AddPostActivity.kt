package com.example.socially

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socially.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class AddPostActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var captionInput: EditText
    private lateinit var uploadButton: Button

    private var selectedImageUri: Uri? = null
    private var imageBase64: String = ""
    private val PICK_IMAGE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        imageView = findViewById(R.id.postImagePreview)
        captionInput = findViewById(R.id.captionInput)
        uploadButton = findViewById(R.id.uploadPostButton)

        // Open gallery when image view is clicked
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }

        // Handle post upload
        uploadButton.setOnClickListener {
            val caption = captionInput.text.toString().trim()

            if (imageBase64.isEmpty()) {
                Toast.makeText(this, "Select an image first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val username = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown"

            val postId = System.currentTimeMillis().toString()

            val post = Post(
                postId = postId,
                userId = uid,
                username = username,
                caption = caption,
                imageBase64 = imageBase64,
                timestamp = System.currentTimeMillis()
            )

            FirebaseDatabase.getInstance().getReference("posts")
                .child(postId)
                .setValue(post)
                .addOnSuccessListener {
                    Toast.makeText(this, "Post uploaded successfully!", Toast.LENGTH_SHORT).show()
                    finish() // Return to previous screen
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Handle selected image from gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            imageView.setImageBitmap(bitmap)

            // Convert to Base64
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            val imageBytes = baos.toByteArray()
            imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        }
    }
}
