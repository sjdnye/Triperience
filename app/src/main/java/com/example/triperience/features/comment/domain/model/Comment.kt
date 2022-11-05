package com.example.triperience.features.comment.domain.model

data class Comment(
    val postId : String = "",
    val publisher: String = "",
    val description: String = "",
    val dateTime : Long? = null
)
