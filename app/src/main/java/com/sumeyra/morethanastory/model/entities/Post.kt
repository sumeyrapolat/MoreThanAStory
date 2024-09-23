package com.sumeyra.morethanastory.model.entities

data class Post(
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val usedWords: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val postId: String = ""
)