package com.example.triperience.features.weatherInfo.presentation

import com.example.triperience.features.weatherInfo.domain.weather.WeatherInfo


data class WeatherState(
    val weatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
