package com.example.socially.models

data class Message(
    val messageId: String? = null,
    val senderId: String? = null,
    val receiverId: String? = null,
    val messageText: String? = null,
    val timestamp: Long? = null
)
