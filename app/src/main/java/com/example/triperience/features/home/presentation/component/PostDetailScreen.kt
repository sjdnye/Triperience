package com.example.triperience.features.home.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.triperience.features.home.presentation.PostDetailViewModel
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun PostDetailScreen(
    latitude: Double,
    longitude: Double,
    postDetailViewModel: PostDetailViewModel = hiltViewModel()
) {

    var selectedTabIndex by remember {
        mutableStateOf(0)
    }
    val tabRowItems = listOf<String>("Location", "Weather Info")


    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color.Transparent,
        ) {
            tabRowItems.forEachIndexed { index, title ->
                Tab(
                    text = {
                        Text(
                            text = title,
                            textAlign = TextAlign.Center
                        )
                    },
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                    },
                    unselectedContentColor = MaterialTheme.colors.onBackground,
                    selectedContentColor = MaterialTheme.colors.primary,

                    )
            }

        }
        Spacer(modifier = Modifier.height(2.dp))
        when (selectedTabIndex) {
            0 -> {
                PostLocationScreen(
                    postDetailViewModel.latLong!!,
                    postDetailViewModel.isMapLoaded,
                    changeMapSituation = {
                        postDetailViewModel.isMapLoaded = !postDetailViewModel.isMapLoaded
                    }
                )
            }
            1 -> {
                PostWeatherInfoScreen(
                    postDetailViewModel.latLong!!.latitude,
                    postDetailViewModel.latLong!!.longitude
                )
            }
        }
    }
}