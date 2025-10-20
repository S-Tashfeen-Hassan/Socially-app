package com.example.socially

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class post : AppCompatActivity() {

    private lateinit var backButton: TextView
    private lateinit var postButton: TextView
    private lateinit var captionInput: EditText
    private lateinit var mainImage: LinearLayout

    private var selectedImageUri: Uri? = null
    private val imageViews = mutableListOf<ImageView>()

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        backButton = findViewById(R.id.back_button)
        postButton = findViewById(R.id.post_button)
        captionInput = findViewById(R.id.caption_input)
        mainImage = findViewById(R.id.mainImage)

        imageViews.addAll(
            listOf(
                findViewById(R.id.imageView1),
                findViewById(R.id.imageView2),
                findViewById(R.id.imageView3),
                findViewById(R.id.imageView4),
                findViewById(R.id.imageView5),
                findViewById(R.id.imageView6),
                findViewById(R.id.imageView7),
                findViewById(R.id.imageView8)
            )
        )

        checkPermissionAndLoadImages()

        backButton.setOnClickListener {
            startActivity(Intent(this, home_screen::class.java))
            finish()
        }

        postButton.setOnClickListener {
            val caption = captionInput.text.toString().trim()
            if (selectedImageUri == null) {
                Toast.makeText(this, "Select an image first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            uploadPost(caption)
        }
    }

    private fun checkPermissionAndLoadImages() {
        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_MEDIA_IMAGES
            else
                Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
        } else {
            loadRecentImages()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadRecentImages()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadRecentImages() {
        val uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(uriExternal, projection, null, null, sortOrder)
        if (cursor != null) {
            var count = 0
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext() && count < imageViews.size) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(uriExternal, id)
                val imageView = imageViews[count]
                imageView.setImageURI(contentUri)

                // When user clicks on a thumbnail
                imageView.setOnClickListener {
                    selectedImageUri = contentUri
                    setLayoutBackgroundFromUri(contentUri)
                    Toast.makeText(this, "Selected image #${count + 1}", Toast.LENGTH_SHORT).show()
                }

                // Automatically show the first image as background
                if (count == 0) {
                    selectedImageUri = contentUri
                    setLayoutBackgroundFromUri(contentUri)
                }

                count++
            }
            cursor.close()
        }
    }

    // ✅ Helper function: load URI as layout background
    private fun setLayoutBackgroundFromUri(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    mainImage.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    mainImage.background = placeholder
                }
            })
    }

    private fun uploadPost(caption: String) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Select an image first", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // 1️⃣ Convert selected image to Base64 string
            val inputStream = contentResolver.openInputStream(selectedImageUri!!)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes == null) {
                Toast.makeText(this, "Failed to read image data", Toast.LENGTH_SHORT).show()
                return
            }

            val base64Image = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)

            // 2️⃣ Create post data
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
            val postId = FirebaseDatabase.getInstance().getReference("posts").push().key ?: return

            val postMap = mapOf(
                "userId" to uid,
                "username" to (FirebaseAuth.getInstance().currentUser?.email ?: "Anonymous"),
                "caption" to caption,
                "imageBase64" to base64Image,
                "timestamp" to System.currentTimeMillis()
            )

            // 3️⃣ Save to Realtime Database
            FirebaseDatabase.getInstance().getReference("posts").child(postId)
                .setValue(postMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "✅ Post uploaded successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, home_screen::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "❌ Failed to upload post: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
