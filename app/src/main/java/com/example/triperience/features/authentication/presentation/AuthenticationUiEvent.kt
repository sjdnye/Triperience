package com.example.triperience.features.authentication.presentation

sealed class AuthenticationUiEvent {
    object NavigateToMainScreen : AuthenticationUiEvent()
    data class ShowMessage(val message: String) : AuthenticationUiEvent()
}
