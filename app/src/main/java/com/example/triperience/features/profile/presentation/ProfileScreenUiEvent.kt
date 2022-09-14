package com.example.triperience.features.profile.presentation

sealed class ProfileScreenUiEvent {
    data class ShowMessage(val message: String, val showToast: Boolean = false): ProfileScreenUiEvent()
}
