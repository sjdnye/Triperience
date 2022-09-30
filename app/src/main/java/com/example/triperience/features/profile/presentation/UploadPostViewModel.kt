package com.example.triperience.features.profile.presentation

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triperience.features.profile.domain.location.LocationTracker
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.profile.domain.model.PostCategory
import com.example.triperience.features.profile.domain.repository.UploadPostRepository
import com.example.triperience.utils.Resource
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.example.triperience.utils.shared_preferences.SharedPrefUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadPostViewModel @Inject constructor(
    private val uploadPostRepository: UploadPostRepository,
    private val sharedPrefUtil: SharedPrefUtil,
    private val locationTracker: LocationTracker
) : ViewModel() {

    var latitude by mutableStateOf<Double?>(null)
    var longitude by mutableStateOf<Double?>(null)
    var description by mutableStateOf<String>("")
    var category by mutableStateOf<PostCategory>(PostCategory.Idle)
    var score by mutableStateOf<String>("")
    var imageUri by mutableStateOf<Uri?>(null)

    var isLoading by mutableStateOf(false)

    private val _uploadEventFlow = MutableSharedFlow<ScreenUiEvent>()
    val uploadEventFlow: SharedFlow<ScreenUiEvent> = _uploadEventFlow

    fun uploadPost() {
        viewModelScope.launch {
            if (imageUri != null && latitude != null && longitude != null) {
                uploadPostRepository.uploadPost(
                    uri = imageUri!!,
                    post = Post(
                        latitude = latitude!!,
                        longitude = longitude!!,
                        description = description,
                        postCategory = when (category) {
                            PostCategory.Sea -> "Sea"
                            PostCategory.Jungle -> "Jungle"
                            PostCategory.Mountainous -> "Mountain"
                            PostCategory.Desert -> "Desert"
                            PostCategory.City -> "City"
                            else -> "Idle"
                        },
                        score = score,
                        publisher = sharedPrefUtil.getCurrentUser()?.userid!!
                    )
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            isLoading = false
                            sendEventSharedFlow(
                                ScreenUiEvent.ShowMessage(
                                    message = "Post Uploaded SuccessFully",
                                    isToast = true
                                )
                            )
                        }
                        is Resource.Error -> {
                            isLoading = false
                            sendEventSharedFlow(
                                ScreenUiEvent.ShowMessage(
                                    message = result.message.toString()
                                )
                            )
                        }
                        is Resource.Loading -> {
                            isLoading = true
                        }
                    }
                }
            } else {
                sendEventSharedFlow(
                    ScreenUiEvent.ShowMessage(message = "Image and coordinates can not be empty!!")
                )
            }
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            locationTracker.getCurrentLocation()?.let { location ->
                latitude = location.latitude
                longitude = location.longitude
            } ?: kotlin.run {
                sendEventSharedFlow(
                    ScreenUiEvent.ShowMessage(
                        message = "Couldn't retrieve location. Make sure to grant permission and enable GPS."
                    )
                )
            }
        }
    }


    private fun sendEventSharedFlow(uploadScreenUiEvent: ScreenUiEvent) {
        viewModelScope.launch {
            _uploadEventFlow.emit(uploadScreenUiEvent)
        }
    }
}
