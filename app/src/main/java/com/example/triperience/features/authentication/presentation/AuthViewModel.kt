package com.example.triperience.features.authentication.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triperience.features.authentication.domain.repository.AuthRepository
import com.example.triperience.utils.AuthValidator
import com.example.triperience.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var isUserAuthenticated by mutableStateOf(false)


    var isLoading by mutableStateOf(false)
        private set

    private val _authEventFlow = MutableSharedFlow<AuthenticationUiEvent>()
    val authEventFlow = _authEventFlow.asSharedFlow()


    init {

    }

    fun onEvent(authScreenEvents: AuthScreenEvents) {
        when (authScreenEvents) {
            is AuthScreenEvents.OnLogin -> {
                val result = AuthValidator.validateSignInRequest(
                    authScreenEvents.email,
                    authScreenEvents.password
                )
                when (result) {
                    is Resource.Success -> {
                        login(authScreenEvents.email, authScreenEvents.password)
                    }
                    is Resource.Error -> {
                        sendAuthUiEvent(
                            AuthenticationUiEvent.ShowMessage(message = result.message.toString())
                        )
                    }
                    else -> {}
                }
            }

            is AuthScreenEvents.OnRegister -> {
                val result = AuthValidator.validateCreateUserRequest(
                    authScreenEvents.username,
                    authScreenEvents.email,
                    authScreenEvents.password
                )
                when (result) {
                    is Resource.Success -> {
                        register(
                            authScreenEvents.username,
                            authScreenEvents.email,
                            authScreenEvents.password
                        )
                    }
                    is Resource.Error -> {
                        sendAuthUiEvent(
                            AuthenticationUiEvent.ShowMessage(message = result.message.toString())
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    private fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            authRepository.checkUsernameAvailability(username = username).collect { response ->
                when (response) {
                    is Resource.Success -> {
                        if (response.data == true) {
                            authRepository.createUserWithEmailAndPassword(
                                email = email,
                                password = password,
                                username = username
                            ).collect { result ->
                                when (result) {
                                    is Resource.Loading -> {
                                        isLoading = true
                                    }
                                    is Resource.Success -> {
                                        isLoading = false
                                        sendAuthUiEvent(AuthenticationUiEvent.NavigateToMainScreen)
                                    }
                                    is Resource.Error -> {
                                        isLoading = false
                                        sendAuthUiEvent(AuthenticationUiEvent.ShowMessage(message = result.message.toString()))
                                    }
                                }
                            }
                        } else {
                            sendAuthUiEvent(AuthenticationUiEvent.ShowMessage(message = "This username is already exist!"))
                        }
                    }
                    is Resource.Error -> {
                        sendAuthUiEvent(AuthenticationUiEvent.ShowMessage(message = response.message.toString()))
                    }
                    else -> {}
                }
            }

        }

    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signInWithEmailAndPassword(email = email, password = password)
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            isLoading = true
                        }
                        is Resource.Success -> {
                            isLoading = false
                            sendAuthUiEvent(AuthenticationUiEvent.NavigateToMainScreen)
                        }
                        is Resource.Error -> {
                            isLoading = false
                            sendAuthUiEvent(AuthenticationUiEvent.ShowMessage(message = result.message.toString()))
                        }
                    }
                }
        }
    }

    fun getUser() {
        isUserAuthenticated = authRepository.isUserAuthenticatedInFirebase()
    }

    private fun sendAuthUiEvent(authenticationUiEvent: AuthenticationUiEvent) {
        viewModelScope.launch {
            _authEventFlow.emit(authenticationUiEvent)
        }
    }
}