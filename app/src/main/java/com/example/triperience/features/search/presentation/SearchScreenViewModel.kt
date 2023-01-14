package com.example.triperience.features.search.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triperience.features.authentication.domain.model.User
import com.example.triperience.features.profile.domain.model.Post
import com.example.triperience.features.search.domain.repository.SearchRepository
import com.example.triperience.utils.Resource
import com.example.triperience.utils.common.screen_ui_event.ScreenUiEvent
import com.example.triperience.utils.core.GetPostsPublisher
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    val getPostsPublisher: GetPostsPublisher,
) : ViewModel() {

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var addressList by mutableStateOf<List<Address>?>(null)
    var currentAddress by mutableStateOf<Address?>(null)
    var latLng by mutableStateOf<LatLng?>(null)
    lateinit var geocoder: Geocoder

    private val _searchEventFlow = MutableSharedFlow<ScreenUiEvent>()
    val searchEventFlow: SharedFlow<ScreenUiEvent> = _searchEventFlow

    var job: Job? = null
    var jobLocation: Job? = null

    fun searchUsers(query: String) {
        if (query == "") {
            isLoading = false
            users = emptyList()
            job?.cancel()
        } else {
            job?.cancel()
            job = viewModelScope.launch {
                delay(500L)
                searchRepository.searchUsers(query = query.trim().lowercase()).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            isLoading = false
                            users = result.data as MutableList<User>
                        }
                        is Resource.Error -> {
                            isLoading = false
                            sendScreenUiEvent(
                                ScreenUiEvent.ShowMessage(
                                    message = result.message.toString(),
                                    isToast = false
                                )
                            )
                        }
                        is Resource.Loading -> {
                            isLoading = true
                        }
                    }
                }
            }
        }
    }

    fun getMapLocation(location: String, context: Context) {
        if (location == "") {
            isLoading = false
            addressList = emptyList()
            latLng = null
            jobLocation?.cancel()
        }else {
            jobLocation?.cancel()
            jobLocation = viewModelScope.launch(Dispatchers.IO) {
                try {
                    // on below line we are getting location from the
                    // location name and adding that location to address list.
                    delay(500L)
                    geocoder = Geocoder(context)
                    isLoading = true
                    addressList = geocoder.getFromLocationName(location, 5)
                    isLoading = false
                } catch (e: IOException) {
                    withContext(Dispatchers.Main){
                        isLoading = false
                        sendScreenUiEvent(
                            ScreenUiEvent.ShowMessage(
                                message = e.message.toString(),
                                isToast = false
                            )
                        )
                    }
                    return@launch
                }
            }
//            viewModelScope.launch {
//                jobLocation!!.join()
//                // on below line we are getting the location
//                // from our list a first position.
//                val address: Address = addressList!![0]
//                // on below line we are creating a variable for our location
//                // where we will add our locations latitude and longitude.
//                latLng = LatLng(address.latitude, address.longitude)
//            }
        }
    }


    private fun sendScreenUiEvent(searchUiEvent: ScreenUiEvent) {
        viewModelScope.launch {
            _searchEventFlow.emit(searchUiEvent)
        }
    }
}

