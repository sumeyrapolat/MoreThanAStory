package com.sumeyra.morethanastory.model.entities


data class Story(
    val userPhoto: String, // Firebase Storage URL veya Image URL
    val userName: String,  // firstName + lastName
    val title: String,
    val content: String,
    val usedWords: List<String>,
    val timestamp: Long,
    val userEmail: String,
    val storyId: String
)