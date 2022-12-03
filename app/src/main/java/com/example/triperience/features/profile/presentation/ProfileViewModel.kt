package com.example.triperience.features.profile.presentation

import android.net.Uri
import android.util.Log
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
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.example.triperience.utils.shared_preferences.SharedPrefUtil
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
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

    var followListUser by mutableStateOf<List<User>?>(emptyList())
        private set
    var followListIsLoading by mutableStateOf(false)

    var meUser by mutableStateOf<User?>(null)
    var showDialog by mutableStateOf(false)

    var showDeletePostDialog by mutableStateOf(false)
    var deletedPost by mutableStateOf<String?>(null)

    var isShowFollowButton by mutableStateOf(false)


    var mainButtonText by mutableStateOf("")
    var mainButtonIsLoading by mutableStateOf(false)

    private val _profileEventFlow = MutableSharedFlow<ScreenUiEvent>()
    val profileEventFlow: SharedFlow<ScreenUiEvent> = _profileEventFlow

    init {
        meUser = sharedPrefUtil.getCurrentUser()
        savedStateHandle.get<String>(Constants.USER_ID)?.let {
            if (meUser?.userid!! != it) {
                isShowFollowButton = true
            }
            getUserInformation(it)
            mainButtonText =
                if (meUser?.following!!.contains(it)) "following" else "follow"
        } ?: meUser?.userid?.let { getUserInformation(it) }
    }


    private fun getUserInformation(userId: String) {
        viewModelScope.launch {
            profileRepository.getUserInformation(userid = userId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        state = state.copy(user = result.data, isLoading = false, error = "")
                        if (userId == meUser?.userid) {
                            sharedPrefUtil.saveCurrentUser(result.data)
                            meUser = result.data
                        }
                        async {
                            getUserPosts(userId = userId)
                        }
                    }
                    is Resource.Error -> {
                        state = state.copy(isLoading = false, error = result.message.toString())
                        sendProfileUiEvent(ScreenUiEvent.ShowMessage(message = result.message.toString()))
                    }
                    is Resource.Loading -> {
                        state = state.copy(isLoading = true)
                    }
                }
            }
        }
    }

    suspend fun getUserPosts(userId: String) {

        profileRepository.getUserPosts(userId = userId).collect { result ->
            when (result) {
                is Resource.Success -> {
                    state = state.copy(posts = result.data, postsIsLoading = false, error = "")
                }
                is Resource.Error -> {
                    state = state.copy(postsIsLoading = false, error = result.message.toString())
                    sendProfileUiEvent(ScreenUiEvent.ShowMessage(message = result.message.toString()))
                }
                is Resource.Loading -> {
                    state = state.copy(postsIsLoading = true)
                }
            }
        }
    }

    fun setUserInformation(username: String, bio: String) {
        if (username.isBlank()) {
            sendProfileUiEvent(ScreenUiEvent.ShowMessage(message = "UserName can not be empty"))
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
                                                ScreenUiEvent.ShowMessage(
                                                    message = "Profile updated successfully!",
                                                    isToast = true
                                                )
                                            )

                                        }
                                        is Resource.Error -> {
                                            state = state.copy(isLoading = false)
                                            sendProfileUiEvent(
                                                ScreenUiEvent.ShowMessage(
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
                                sendProfileUiEvent(ScreenUiEvent.ShowMessage(message = "This username is already exists!"))
                            }

                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            sendProfileUiEvent(ScreenUiEvent.ShowMessage(message = response.message.toString()))
                        }
                        else -> {}
                    }

                }
            }
        }
    }

    fun uploadImageProfile(imageUrl: Uri) {
        viewModelScope.launch {
            profileRepository.uploadProfileImage(imageUri = imageUrl, userid = meUser?.userid!!)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            state = state.copy(isLoading = false)
                            sendProfileUiEvent(
                                ScreenUiEvent.ShowMessage(
                                    message = "Profile image changed successfully",
                                    isToast = true
                                )
                            )
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            sendProfileUiEvent(
                                ScreenUiEvent.ShowMessage(
                                    message = result.message.toString(),
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

    fun deletePost() {
        viewModelScope.launch {
            profileRepository.deletePost(postId = deletedPost!!, meUser!!.userid)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            state = state.copy(isLoading = false)
                            sendProfileUiEvent(
                                ScreenUiEvent.ShowMessage(
                                    message = "Selected post has been deleted successfully!",
                                    isToast = true
                                )
                            )
                        }
                        is Resource.Error -> {
                            state = state.copy(isLoading = false)
                            sendProfileUiEvent(
                                ScreenUiEvent.ShowMessage(
                                    message = result.message.toString(),
                                )
                            )
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = true)
                        }
                    }
                    deletedPost = null
                }
        }
    }

    fun followUser(userid: String) {
        val myFollowing: MutableList<String> = meUser!!.following as MutableList<String>
        viewModelScope.launch {
            profileRepository.followUser(
                myId = meUser!!.userid,
                userid = userid
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        mainButtonIsLoading = false
                        myFollowing.add(userid)
                        meUser = meUser?.copy(following = myFollowing)
                        sharedPrefUtil.saveCurrentUser(meUser)
                        mainButtonText = Constants.FOLLOWING_USER_TEXT
                    }
                    is Resource.Error -> {
                        mainButtonIsLoading = false
                        sendProfileUiEvent(ScreenUiEvent.ShowMessage(message = result.message.toString()))
                    }
                    is Resource.Loading -> {
                        mainButtonIsLoading = true
                    }
                }
            }
        }
    }

    fun unFollowUser(userid: String) {
        val myFollowing: MutableList<String> = meUser?.following as MutableList<String>
        viewModelScope.launch {
            profileRepository.unFollowUser(
                myId = meUser!!.userid,
                userid = userid
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        mainButtonIsLoading = false
                        myFollowing.remove(userid)
                        meUser = meUser!!.copy(following = myFollowing)
                        sharedPrefUtil.saveCurrentUser(meUser)
                        mainButtonText = Constants.FOLLOW_USER_TEXT
                    }
                    is Resource.Error -> {
                        mainButtonIsLoading = false
                        sendProfileUiEvent(ScreenUiEvent.ShowMessage(message = result.message.toString()))
                    }
                    is Resource.Loading -> {
                        mainButtonIsLoading = true
                    }
                }
            }
        }
    }

    suspend fun getFollowListUsers(followList: List<String>){
        viewModelScope.launch {
            profileRepository.getFollowListUsers(followList = followList).collect{result ->
                when(result){
                    is Resource.Success -> {
                        followListIsLoading = false
                        followListUser = result.data
                    }
                    is Resource.Error -> {
                        followListIsLoading = false
                        sendProfileUiEvent(ScreenUiEvent.ShowMessage(message = result.message.toString()))
                    }
                    is Resource.Loading -> {
                        followListIsLoading = true
                    }
                }
            }
        }
    }

    private fun sendProfileUiEvent(profileScreenUiEvent: ScreenUiEvent) {
        viewModelScope.launch {
            _profileEventFlow.emit(profileScreenUiEvent)
        }
    }
}