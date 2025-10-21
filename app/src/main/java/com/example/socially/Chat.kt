package com.example.socially

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socially.adapters.MessageAdapter
import com.example.socially.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Chat : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageView
    private lateinit var adapter: MessageAdapter
    private val messageList = mutableListOf<Message>()

    private lateinit var dbRef: DatabaseReference
    private lateinit var senderId: String
    private lateinit var receiverId: String
    private lateinit var chatRoomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize UI
        recyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.message_input)
        sendButton = findViewById(R.id.send_button)
        val iconsLayout = findViewById<LinearLayout>(R.id.icons_layout)
        val nameTextView = findViewById<TextView>(R.id.chatUsername)

        // Set receiver name
        val receiverName = intent.getStringExtra("receiverName")
        nameTextView.text = receiverName ?: "Chat"

        // Get sender and receiver IDs
        senderId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.e("ChatDebug", "Current user is null")
            finish()
            return
        }

        receiverId = intent.getStringExtra("receiverUid") ?: run {
            Log.e("ChatDebug", "Receiver UID not provided")
            finish()
            return
        }

        Log.d("ChatDebug", "Sender: $senderId, Receiver: $receiverId")

        // Generate unique chat room ID
        chatRoomId = if (senderId < receiverId) "${senderId}_${receiverId}" else "${receiverId}_${senderId}"
        dbRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(messageList)
        recyclerView.adapter = adapter

        // Listen for new messages
        listenForMessages()

        // TextWatcher for showing/hiding send button
        messageInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    iconsLayout.visibility = View.GONE
                    sendButton.visibility = View.VISIBLE
                } else {
                    iconsLayout.visibility = View.VISIBLE
                    sendButton.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // Send button click listener
        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageInput.text.clear()
            } else {
                Log.d("ChatDebug", "Empty message, not sending")
            }
        }
    }

    private fun sendMessage(text: String) {
        val messageId = dbRef.push().key
        if (messageId == null) {
            Log.e("ChatDebug", "Failed to get message ID from push()")
            return
        }

        val message = Message(
            messageId = messageId,
            senderId = senderId,
            receiverId = receiverId,
            messageText = text,
            timestamp = System.currentTimeMillis()
        )

        dbRef.child(messageId).setValue(message)
            .addOnSuccessListener { Log.d("ChatDebug", "Message sent: $text") }
            .addOnFailureListener { e -> Log.e("ChatDebug", "Failed to send message", e) }
    }

    private fun listenForMessages() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (child in snapshot.children) {
                    val msg = child.getValue(Message::class.java)
                    if (msg != null) messageList.add(msg)
                }
                adapter.notifyDataSetChanged()
                if (messageList.isNotEmpty()) {
                    recyclerView.scrollToPosition(messageList.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatDebug", "Failed to read messages: ${error.message}")
            }
        })
    }
}
