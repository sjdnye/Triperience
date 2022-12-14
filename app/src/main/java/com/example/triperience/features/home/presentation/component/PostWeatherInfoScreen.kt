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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun PostWeatherInfoScreen(
    latitude: Double,
    longitude: Double,
    weatherViewModel: WeatherViewModel = hiltViewModel()
) {

    val swipeRefreshState =
        rememberSwipeRefreshState(isRefreshing = weatherViewModel.state.isLoading)
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SwipeRefresh(
            modifier = Modifier
                .fillMaxSize(),
            state = swipeRefreshState,
            onRefresh = {
                weatherViewModel.loadWeatherInfo()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBlue)
                    .verticalScroll(rememberScrollState())
            ) {
                WeatherCard(state = weatherViewModel.state, backgroundColor = DeepBlue)
                Spacer(modifier = Modifier.height(16.dp))
                WeatherForecast(state = weatherViewModel.state)

            }
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

