package com.example.triperience.features.authentication.presentation

sealed class AuthScreenEvents {
    data class OnLogin(val email: String, val password: String) : AuthScreenEvents()
    data class OnRegister(
        val email: String,
        val password: String,
        val username: String,
        val confirmedPassword: String
    ) : AuthScreenEvents()
}