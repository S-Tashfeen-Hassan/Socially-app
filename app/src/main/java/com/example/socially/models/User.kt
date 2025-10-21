package com.example.socially.models

data class User(
    val uid: String? = null,
    val username: String? = null,
    val email: String? = null,
    val profileImage: String? = null // optional (Base64 or URL)
)
