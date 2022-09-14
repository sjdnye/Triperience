package com.example.triperience.features.profile.presentation

import com.example.triperience.features.authentication.domain.model.User


data class ProfileState(
    val user : User? = null,
    val isLoading: Boolean = false,
    val error: String = "",
)