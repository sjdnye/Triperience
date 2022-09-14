package com.example.triperience.features.profile.presentation

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.authentication.domain.repository.AuthRepository
import com.example.triperience.features.profile.domain.repository.ProfileRepository
import com.example.triperience.utils.Constants
import com.example.triperience.utils.Resource
import com.example.triperience.utils.shared_preferences.SharedPrefUtil
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val sharedPrefUtil: SharedPrefUtil,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf<ProfileState>(ProfileState())
        private set

    private var meUser by mutableStateOf<User?>(null)


    var mainButtonText by mutableStateOf("")
    var mainButtonIsLoading by mutableStateOf(false)

    private val _profileEventFlow = MutableSharedFlow<ProfileScreenUiEvent>()
    val profileEventFlow: SharedFlow<ProfileScreenUiEvent> = _profileEventFlow

    init {
        meUser = sharedPrefUtil.getCurrentUser()
        savedStateHandle.get<String>(Constants.USER_ID)?.let {
            getUserInformation(it)
            mainButtonText = if (meUser?.following?.contains(state.user?.userid.toString()) == true) "following" else "follow"
        } ?: meUser?.userid?.let { getUserInformation(it) }

    }


    fun getUserInformation(userId: String) {
        viewModelScope.launch {
            profileRepository.getUserInformation(userid = userId).collect{ result ->
                when (result) {
                    is Resource.Success -> {
                        state = state.copy(user = result.data, isLoading = false, error = "")
                        if (userId == auth.currentUser?.uid) {
                            sharedPrefUtil.saveCurrentUser(state.user)
                        }
                    }
                    is Resource.Error -> {
                        state = state.copy(isLoading = false, error = result.message.toString())
                        sendProfileUiEvent(ProfileScreenUiEvent.ShowMessage(message = result.message.toString()))
                    }
                    is Resource.Loading -> {
                        state = state.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun setUserInformation(username: String, bio: String) {
        if (username.isBlank()) {
            sendProfileUiEvent(ProfileScreenUiEvent.ShowMessage(message = "UserName can not be empty"))
        } else {
            viewModelScope.launch {
                state = state.copy(isLoading = true)
                authRepository.checkUsernameAvailability(username = username).collect { response ->
                    when (response) {
                        is Resource.Success -> {
                            if (response.data == true) {
                                profileRepository.setUserInformation(
                                    userid = state.user?.userid!!,
                                    userName = username,
                                    bio = bio
                                ).collect { result ->
                                    when (result) {
                                        is Resource.Success -> {
                                            state = state.copy(isLoading = false)
                                            sendProfileUiEvent(
                                                ProfileScreenUiEvent.ShowMessage(
                                                    message = "Profile updated successfully!",
                                                    showToast = true
                                                )
                                            )

                                        }
                                        is Resource.Error -> {
                                            state = state.copy(isLoading = false)
                                            sendProfileUiEvent(
                                                ProfileScreenUiEvent.ShowMessage(
                                                    message = result.message.toString()
                                                )
                                            )
                                        }
                                        is Resource.Loading -> {
                                            state = state.copy(isLoading = true)
                                        }
                                    }
                                }

                            } else {
                                state = state.copy(isLoading = false)
                                sendProfileUiEvent(ProfileScreenUiEvent.ShowMessage(message = "This username is already exists!"))
                            }

                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            sendProfileUiEvent(ProfileScreenUiEvent.ShowMessage(message = response.message.toString()))
                        }
                        else -> {}
                    }

                }
            }
        }
    }

    fun uploadImageProfile(imageUrl: Uri) {
        viewModelScope.launch {
            profileRepository.uploadProfileImage(imageUri = imageUrl).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        state = state.copy(isLoading = false)
                        sendProfileUiEvent(ProfileScreenUiEvent.ShowMessage(message = "Profile image changed successfully"))
                    }
                    is Resource.Error -> {
                        state = state.copy(isLoading = false)
                        sendProfileUiEvent(
                            ProfileScreenUiEvent.ShowMessage(
                                message = result.message.toString(),
                                showToast = true
                            )
                        )
                    }
                    is Resource.Loading -> {
                        state = state.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun followUser(userid: String) {
        val myFollowing : MutableList<String> = meUser?.following as MutableList<String>
        viewModelScope.launch {
            profileRepository.followUser(
                myId = meUser?.userid!!,
                userid = userid
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        mainButtonIsLoading = false
                        myFollowing.add(userid)
                        meUser = meUser!!.copy(following = myFollowing)
                        sharedPrefUtil.saveCurrentUser(meUser?.copy(following = myFollowing))
                        sharedPrefUtil.saveCurrentUser(meUser)
                        mainButtonText = "following"
                    }
                    is Resource.Error -> {
                        mainButtonIsLoading = false
                        sendProfileUiEvent(ProfileScreenUiEvent.ShowMessage(message = result.message.toString()))
                    }
                    is Resource.Loading -> {
                        mainButtonIsLoading = true
                    }
                }
            }
        }
    }

    fun unFollowUser(userid: String) {
        val myFollowing : MutableList<String> = meUser?.following as MutableList<String>
        viewModelScope.launch {
            profileRepository.followUser(
                myId = meUser?.userid!!,
                userid = userid
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        mainButtonIsLoading = false
                        myFollowing.remove(userid)
                        meUser = meUser!!.copy(following = myFollowing)
                        sharedPrefUtil.saveCurrentUser(meUser?.copy(following = myFollowing))
                        sharedPrefUtil.saveCurrentUser(meUser)
                        mainButtonText = "follow"
                    }
                    is Resource.Error -> {
                        mainButtonIsLoading = false
                        sendProfileUiEvent(ProfileScreenUiEvent.ShowMessage(message = result.message.toString()))
                    }
                    is Resource.Loading -> {
                        mainButtonIsLoading = true
                    }
                }
            }
        }
    }

    private fun sendProfileUiEvent(profileScreenUiEvent: ProfileScreenUiEvent) {
        viewModelScope.launch {
            _profileEventFlow.emit(profileScreenUiEvent)
        }
    }
}