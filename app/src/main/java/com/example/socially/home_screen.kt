package com.example.socially

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socially.adapters.PostAdapter
import com.example.socially.models.Post
import com.google.firebase.database.*

class home_screen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var postsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        // Handle system UI insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ✅ Initialize RecyclerView for posts
        recyclerView = findViewById(R.id.postRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter(emptyList())
        recyclerView.adapter = adapter

        // ✅ Firebase setup
        database = FirebaseDatabase.getInstance()
        postsRef = database.getReference("posts")

        // ✅ Listen for real-time posts
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postList = mutableListOf<Post>()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null) postList.add(post)
                }
                adapter.updatePosts(postList.reversed()) // Show latest first
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // ✅ Navigation Buttons
        findViewById<ImageView>(R.id.open_camera).setOnClickListener {
            startActivity(Intent(this, camera::class.java))
        }

        findViewById<ImageView>(R.id.activity1).setOnClickListener {
            startActivity(Intent(this, activity::class.java))
        }

        findViewById<ImageView>(R.id.direct_message).setOnClickListener {
            startActivity(Intent(this, messages::class.java))
        }

        findViewById<ImageView>(R.id.activity2).setOnClickListener {
            startActivity(Intent(this, activity::class.java))
        }

        findViewById<ImageView>(R.id.search_button).setOnClickListener {
            startActivity(Intent(this, for_you_page::class.java))
        }

        findViewById<ImageView>(R.id.create_button).setOnClickListener {
            startActivity(Intent(this, post::class.java))
        }

        findViewById<ImageView>(R.id.profile).setOnClickListener {
            startActivity(Intent(this, edit_profile::class.java))
        }

        findViewById<ImageView>(R.id.profile_image).setOnClickListener {
            startActivity(Intent(this, your_story::class.java))
        }
    }
}
