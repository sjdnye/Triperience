package com.example.triperience.features.weatherInfo.domain.repository

import com.example.triperience.utils.Resource
import com.example.triperience.features.weatherInfo.domain.weather.WeatherInfo

interface WeatherRepository{

    suspend fun getWeatherData(lat:Double, long:Double): Resource<WeatherInfo>

}