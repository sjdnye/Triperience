package com.example.triperience.features.profile.presentation

import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.profile.domain.model.Post


data class ProfileState(
    val user : User? = null,
    val posts : List<Post>? = null,
    val isLoading: Boolean = false,
    val postsIsLoading: Boolean = false,
    val error: String = "",
)