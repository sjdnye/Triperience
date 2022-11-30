package com.example.triperience.features.weatherInfo.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.triperience.features.profile.domain.location.LocationTracker
import com.example.triperience.features.weatherInfo.domain.repository.WeatherRepository
import com.example.triperience.features.weatherInfo.presentation.WeatherState
import com.example.triperience.utils.Resource
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var latitude: Double? = null
    private var longitude: Double? = null


    var state by mutableStateOf(WeatherState())
        private set

    init {
        savedStateHandle.get<Double>("latitude")?.let {
            latitude = it
        }
        savedStateHandle.get<Double>("longitude")?.let {
            longitude = it
        }
        if (latitude != null && longitude != null) {
            loadWeatherInfo()
        } else {
            state = state.copy(
                isLoading = false,
                error = "Couldn't retrieve location. Make sure to grant permission and enable GPS."
            )
        }
    }

    fun loadWeatherInfo() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )
            when (val result = repository.getWeatherData(latitude!!, longitude!!)) {
                is Resource.Success -> {
                    state = state.copy(
                        weatherInfo = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        weatherInfo = null,
                        isLoading = false,
                        error = result.message
                    )
                }
                else -> {

                }
            }
        }
    }
}

