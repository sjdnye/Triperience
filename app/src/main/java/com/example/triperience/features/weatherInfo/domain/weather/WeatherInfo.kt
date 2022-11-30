package com.example.triperience.features.weatherInfo.domain.weather

import com.example.triperience.features.weatherInfo.domain.weather.WeatherData

data class WeatherInfo(
    val weatherDataPerDay: Map<Int, List<WeatherData>>,
    val currentWeatherData: WeatherData?
)