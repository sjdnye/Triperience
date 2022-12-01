package com.example.triperience.features.home.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.features.weatherInfo.presentation.WeatherViewModel
import com.example.triperience.features.weatherInfo.presentation.component.WeatherCard
import com.example.triperience.features.weatherInfo.presentation.component.WeatherForecast
import com.example.triperience.ui.theme.DarkBlue
import com.example.triperience.ui.theme.DeepBlue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostWeatherInfoScreen(
    latitude: Double,
    longitude: Double,
    weatherViewModel: WeatherViewModel = hiltViewModel()
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue)
        ) {
            IconButton(
                onClick = {
                    weatherViewModel.loadWeatherInfo()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
            WeatherCard(state = weatherViewModel.state, backgroundColor = DeepBlue)
            Spacer(modifier = Modifier.height(16.dp))
            WeatherForecast(state = weatherViewModel.state)

        }

        if (weatherViewModel.state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        weatherViewModel.state.error?.let { error ->
            Text(
                text = error, color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

