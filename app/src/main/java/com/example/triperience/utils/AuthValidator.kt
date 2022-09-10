package com.example.triperience.utils

import com.example.triperience.features.authentication.domain.model.User

object AuthValidator {

    fun validateCreateUserRequest(username:String, email: String, password: String): Resource<User?> {

        if (username.isBlank() && password.isBlank() && email.isBlank()) {
            return Resource.Error(message = "All fields are empty")
        }
        if (email.isBlank()) {
            return Resource.Error(message = "Email can not be blank")
        }
        if (email.isNotBlank()) {
            val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
            val matches = EMAIL_REGEX.toRegex().matches(email)
            if (!matches) {
                return Resource.Error("Email is not valid")
            }
        }

        if (username.isBlank()) {
            return Resource.Error("Username can not be blank")
        }
        if (password.isBlank()) {
            return Resource.Error("Password can not be blank")
        }
        if (password.length < 6) {
            return Resource.Error(message = "Password length should be at least 6 characters.")
        }
        return Resource.Success(data = null)
    }

    fun validateSignInRequest(email: String, password: String): Resource<User> {
        if (password.isBlank() && email.isBlank()) {
            return Resource.Error(message = "Email and Password fields are empty")
        }
        if (email.isBlank()) {
            return Resource.Error("Email can not be empty")
        }
        if (email.isNotBlank()) {
            val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
            val matches = EMAIL_REGEX.toRegex().matches(email)
            if (!matches) {
                return Resource.Error(message = "Email is not valid")
            }
        }
        if (password.isBlank()) {
            return Resource.Error("Password can not be empty")
        }
        return Resource.Success(data = null)
    }
}