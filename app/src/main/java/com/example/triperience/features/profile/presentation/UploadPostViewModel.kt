package com.example.triperience.features.profile.presentation

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.triperience.features.profile.domain.location.LocationTracker
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.profile.domain.model.PostCategory
import com.example.triperience.features.profile.domain.repository.UploadPostRepository
import com.example.triperience.utils.Resource
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.example.triperience.utils.shared_preferences.SharedPrefUtil
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.IOException
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
    var city by mutableStateOf<String>("")
    var advantage_1 by mutableStateOf("")
    var advantage_2 by mutableStateOf("")
    var advantage_3 by mutableStateOf("")
    var disAdvantage_1 by mutableStateOf("")
    var disAdvantage_2 by mutableStateOf("")
    var disAdvantage_3 by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)

    var isLoading by mutableStateOf(false)
    var locationIsLoading by mutableStateOf(false)
    var locationSwitchFlag by mutableStateOf(0)
    var addressList by mutableStateOf<List<Address>?>(null)
    var jobLocation: Job? = null
    lateinit var geocoder: Geocoder

    private val _uploadEventFlow = MutableSharedFlow<ScreenUiEvent>()
    val uploadEventFlow: SharedFlow<ScreenUiEvent> = _uploadEventFlow

    fun uploadPost(pickedDate: String, pickedTime: String) {
        viewModelScope.launch {
            if (imageUri != null && latitude != null && longitude != null && city != "") {
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
                        publisher = sharedPrefUtil.getCurrentUser()?.userid!!,
                        city = city,
                        pickedTime = pickedTime,
                        pickedDate = pickedDate,
                        advantages = listOf(advantage_1,advantage_2,advantage_3),
                        disAdvantages = listOf(disAdvantage_1,disAdvantage_2,disAdvantage_3)
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
                    ScreenUiEvent.ShowMessage(message = "Image/city/coordinates can not be empty!!")
                )
            }
        }
    }

    fun getCurrentLocation() {
        locationIsLoading = true
        locationSwitchFlag = 1
        viewModelScope.launch {
            locationTracker.getCurrentLocation()?.let { location ->
                locationIsLoading = false
                latitude = location.latitude
                longitude = location.longitude
            } ?: kotlin.run {
                locationIsLoading = false
                sendEventSharedFlow(
                    ScreenUiEvent.ShowMessage(
                        message = "Couldn't retrieve location. Make sure to grant permission and enable GPS."
                    )
                )
            }
        }
    }

    fun getMapLocationByCity( context: Context) {
        if (city == "") {
            locationIsLoading = false
            addressList = emptyList()
            jobLocation?.cancel()
            sendEventSharedFlow(
                ScreenUiEvent.ShowMessage(
                    message = "Please fill the \"city\" field",
                    isToast = false
                )
            )
        } else {
            jobLocation?.cancel()
            jobLocation = viewModelScope.launch(Dispatchers.IO) {
                try {
                    // on below line we are getting location from the
                    // location name and adding that location to address list.
                    delay(500L)
                    geocoder = Geocoder(context)
                    locationIsLoading = true
                    locationSwitchFlag = 2
                    addressList = geocoder.getFromLocationName(city, 1)
                    locationIsLoading = false
                    withContext(Dispatchers.Main){
                        val address: Address = addressList!![0]
                        latitude = address.latitude
                        longitude = address.longitude
                    }

                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        locationIsLoading = false
                        sendEventSharedFlow(
                            ScreenUiEvent.ShowMessage(
                                message = e.message.toString(),
                                isToast = false
                            )
                        )
                    }
                    return@launch
                }
            }
        }
    }
    
    private fun sendEventSharedFlow(uploadScreenUiEvent: ScreenUiEvent) {
        viewModelScope.launch {
            _uploadEventFlow.emit(uploadScreenUiEvent)
        }
    }
}
