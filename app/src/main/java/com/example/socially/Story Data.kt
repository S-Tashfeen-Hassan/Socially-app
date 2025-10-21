package com.example.socially

data class Story(
    val storyId: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0
) {
    // Add an empty constructor for Firebase deserialization
    constructor() : this("", "", "", 0)
}

