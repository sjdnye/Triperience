package com.example.triperience.features.profile.domain.model


data class Post(
    var postId : String = "",
    var postImage: String = "",
    val description: String = "",
    val publisher: String = "",
    val postCategory: String = "Idle",
    val score : String = "",
    val latitude : Double? = null,
    val longitude : Double? = null,
    var dateTime: Long? = null
)
