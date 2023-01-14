package com.example.triperience.features.weatherInfo.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.triperience.features.weatherInfo.data.mapper.toWeatherInfo
import com.example.triperience.features.weatherInfo.data.remote.WeatherApi
import com.example.triperience.features.weatherInfo.domain.repository.WeatherRepository
import com.example.triperience.features.weatherInfo.domain.weather.WeatherInfo
import com.example.triperience.utils.Resource
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
): WeatherRepository {
    override suspend fun getWeatherData(lat: Double, long: Double): Resource<WeatherInfo> {
        return try {
            Resource.Success(
                data = api.getWeatherData(
                    lat = lat,
                    long = long
                ).toWeatherInfo()
            )
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred.")
        }
    }
}