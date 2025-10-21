package com.example.socially

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socially.adapters.UserAdapter
import com.example.socially.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class messages : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private val userList = mutableListOf<User>()
    private var currentUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        recyclerView = findViewById(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UserAdapter(this, userList)
        recyclerView.adapter = adapter

        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            val intent = Intent(this, home_screen::class.java)
            startActivity(intent)
            finish()
        }

        // 🔹 First get username, then load users
        displayCurrentUsername()
    }

    private fun displayCurrentUsername() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val usernameTextView = findViewById<TextView>(R.id.myusername)
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = snapshot.getValue(User::class.java)
                currentUsername = currentUser?.username ?: currentUser?.email ?: "Unknown User"
                usernameTextView.text = currentUsername

                // ✅ Now load users after getting current username
                loadUsers()
            }

            override fun onCancelled(error: DatabaseError) {
                usernameTextView.text = "Error loading"
            }
        })
    }

    private fun loadUsers() {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    // ✅ Skip current user based on username
                    if (user != null && user.username != currentUsername) {
                        userList.add(user)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}




















































































































































































































































































































































