package com.example.triperience.features.home.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.triperience.utils.MapScreen
import com.google.android.gms.maps.model.LatLng

@Composable
fun PostLocationScreen(
        latLng: LatLng,
        isMapLoaded: Boolean,
        changeMapSituation: () -> Unit
) {
        MapScreen(
                latLng = latLng,
                isMapLoaded = isMapLoaded,
                changeMapSituation = changeMapSituation
        )
}