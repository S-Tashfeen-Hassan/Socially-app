package com.example.socially.models
data class Post(
    val postId: String? = null,
    val userId: String? = null,
    val username: String? = null,
    val caption: String? = null,
    val imageBase64: String? = null,
    val timestamp: Long? = null
)
