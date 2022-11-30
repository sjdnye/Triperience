package com.example.triperience.features.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val  savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var latitude : Double? = null
    private var longitude : Double? = null

    var latLong by mutableStateOf<LatLng?>(null)
    private set

    var isMapLoaded by mutableStateOf(false)

    init {
        savedStateHandle.get<Double>("latitude")?.let {
            latitude = it
        }
        savedStateHandle.get<Double>("longitude")?.let {
            longitude = it
        }
        if (latitude != null && longitude != null){
            latLong = LatLng(latitude!!, longitude!!)
        }
    }
}