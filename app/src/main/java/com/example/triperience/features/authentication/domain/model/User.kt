package com.example.triperience.features.authentication.domain.model

data class User(
    val userid: String = "",
    val username: String = "",
    val email: String = "",
    val bio: String = "",
    val profileImage: String = "",
    val following: List<String> = emptyList(),
    val followers: List<String> = emptyList(),
    val posts: List<String> = emptyList()
)
