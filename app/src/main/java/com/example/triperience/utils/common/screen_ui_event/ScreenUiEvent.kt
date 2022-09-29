package com.example.triperience.utils.common.screen_ui_event

sealed class ScreenUiEvent() {
    data class ShowMessage(val message: String, val isToast: Boolean = false) :
        ScreenUiEvent()
}
